package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {
   private byte[] byteArray;
   private static final String __OBFID = "CL_00001213";

   NBTTagByteArray() {
   }

   public NBTTagByteArray(byte[] p_i45128_1_) {
      this.byteArray = p_i45128_1_;
   }

   void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.byteArray.length);
      p_74734_1_.write(this.byteArray);
   }

   void func_152446_a(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      int var4 = p_152446_1_.readInt();
      p_152446_3_.func_152450_a((long)(8 * var4));
      this.byteArray = new byte[var4];
      p_152446_1_.readFully(this.byteArray);
   }

   public byte getId() {
      return 7;
   }

   public String toString() {
      return "[" + this.byteArray.length + " bytes]";
   }

   public NBTBase copy() {
      byte[] var1 = new byte[this.byteArray.length];
      System.arraycopy(this.byteArray, 0, var1, 0, this.byteArray.length);
      return new NBTTagByteArray(var1);
   }

   public boolean equals(Object p_equals_1_) {
      return super.equals(p_equals_1_) ? Arrays.equals(this.byteArray, ((NBTTagByteArray)p_equals_1_).byteArray) : false;
   }

   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.byteArray);
   }

   public byte[] func_150292_c() {
      return this.byteArray;
   }
}
