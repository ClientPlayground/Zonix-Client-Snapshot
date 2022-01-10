package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavedData {
   public final String mapName;
   private boolean dirty;
   private static final String __OBFID = "CL_00000580";

   public WorldSavedData(String p_i2141_1_) {
      this.mapName = p_i2141_1_;
   }

   public abstract void readFromNBT(NBTTagCompound var1);

   public abstract void writeToNBT(NBTTagCompound var1);

   public void markDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean p_76186_1_) {
      this.dirty = p_76186_1_;
   }

   public boolean isDirty() {
      return this.dirty;
   }
}
