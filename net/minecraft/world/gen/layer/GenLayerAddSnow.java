package net.minecraft.world.gen.layer;

public class GenLayerAddSnow extends GenLayer {
   private static final String __OBFID = "CL_00000553";

   public GenLayerAddSnow(long p_i2121_1_, GenLayer p_i2121_3_) {
      super(p_i2121_1_);
      this.parent = p_i2121_3_;
   }

   public int[] getInts(int p_75904_1_, int p_75904_2_, int p_75904_3_, int p_75904_4_) {
      int var5 = p_75904_1_ - 1;
      int var6 = p_75904_2_ - 1;
      int var7 = p_75904_3_ + 2;
      int var8 = p_75904_4_ + 2;
      int[] var9 = this.parent.getInts(var5, var6, var7, var8);
      int[] var10 = IntCache.getIntCache(p_75904_3_ * p_75904_4_);

      for(int var11 = 0; var11 < p_75904_4_; ++var11) {
         for(int var12 = 0; var12 < p_75904_3_; ++var12) {
            int var13 = var9[var12 + 1 + (var11 + 1) * var7];
            this.initChunkSeed((long)(var12 + p_75904_1_), (long)(var11 + p_75904_2_));
            if (var13 == 0) {
               var10[var12 + var11 * p_75904_3_] = 0;
            } else {
               int var14 = this.nextInt(6);
               byte var15;
               if (var14 == 0) {
                  var15 = 4;
               } else if (var14 <= 1) {
                  var15 = 3;
               } else {
                  var15 = 1;
               }

               var10[var12 + var11 * p_75904_3_] = var15;
            }
         }
      }

      return var10;
   }
}