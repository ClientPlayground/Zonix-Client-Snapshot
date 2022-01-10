package com.thevoxelbox.voxelmap.util;

import com.thevoxelbox.voxelmap.interfaces.IDimensionManager;
import com.thevoxelbox.voxelmap.interfaces.IVoxelMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldProvider;

public class DimensionManager implements IDimensionManager {
   public ArrayList dimensions;
   IVoxelMap master;

   public DimensionManager(IVoxelMap master) {
      this.master = master;
      this.dimensions = new ArrayList();
   }

   public ArrayList getDimensions() {
      return this.dimensions;
   }

   public void populateDimensions() {
      this.dimensions.clear();

      for(int t = -1; t <= 1; ++t) {
         String name = "notLoaded";
         WorldProvider provider = null;

         try {
            provider = WorldProvider.getProviderForDimension(t);
         } catch (Exception var11) {
            provider = null;
         }

         if (provider != null) {
            try {
               name = provider.getDimensionName();
            } catch (Exception var10) {
               name = "failedToLoad";
            }

            Dimension dim = new Dimension(name, t);
            this.dimensions.add(dim);
         }
      }

      Iterator var12 = this.master.getWaypointManager().getWaypoints().iterator();

      while(var12.hasNext()) {
         Waypoint pt = (Waypoint)var12.next();
         Iterator var14 = pt.dimensions.iterator();

         while(var14.hasNext()) {
            Integer t = (Integer)var14.next();
            if (this.getDimensionByID(t.intValue()) == null) {
               String name = "notLoaded";
               WorldProvider provider = null;

               try {
                  provider = WorldProvider.getProviderForDimension(t.intValue());
               } catch (Exception var9) {
                  provider = null;
               }

               if (provider != null) {
                  try {
                     name = provider.getDimensionName();
                  } catch (Exception var8) {
                     name = "failedToLoad";
                  }

                  Dimension dim = new Dimension(name, t.intValue());
                  this.dimensions.add(dim);
               }
            }
         }
      }

      Collections.sort(this.dimensions, new Comparator() {
         public int compare(Dimension dim1, Dimension dim2) {
            return dim1.ID - dim2.ID;
         }
      });
   }

   public void enteredDimension(int ID) {
      Dimension dim = this.getDimensionByID(ID);
      if (dim == null) {
         dim = new Dimension("notLoaded", ID);
         this.dimensions.add(dim);
         Collections.sort(this.dimensions, new Comparator() {
            public int compare(Dimension dim1, Dimension dim2) {
               return dim1.ID - dim2.ID;
            }
         });
      }

      if (dim.name.equals("notLoaded") || dim.name.equals("failedToLoad")) {
         try {
            dim.name = Minecraft.getMinecraft().theWorld.provider.getDimensionName();
         } catch (Exception var4) {
            dim.name = "dimension " + ID + "(" + Minecraft.getMinecraft().theWorld.provider.getClass().getSimpleName() + ")";
         }
      }

   }

   public Dimension getDimensionByID(int ID) {
      Iterator var2 = this.dimensions.iterator();

      Dimension dim;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         dim = (Dimension)var2.next();
      } while(dim.ID != ID);

      return dim;
   }

   public Dimension getDimensionByName(String name) {
      Iterator var2 = this.dimensions.iterator();

      Dimension dim;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         dim = (Dimension)var2.next();
      } while(!dim.name.equals(name));

      return dim;
   }
}
