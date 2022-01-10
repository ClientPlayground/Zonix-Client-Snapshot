package us.zonix.client.module.impl;

import com.thevoxelbox.voxelmap.VoxelMap;
import us.zonix.client.module.modules.AbstractModule;

public final class ZansMinimap extends AbstractModule {
   private final VoxelMap voxelMap = new VoxelMap(true, true);

   public ZansMinimap() {
      super("Minimap");
   }

   public void renderReal() {
      if (this.mc.currentScreen == null) {
         this.voxelMap.onTickInGame(this.mc);
      }

   }
}
