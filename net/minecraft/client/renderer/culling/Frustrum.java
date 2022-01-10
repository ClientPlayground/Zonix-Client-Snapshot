package net.minecraft.client.renderer.culling;

import net.minecraft.util.AxisAlignedBB;

public class Frustrum implements ICamera {
   private ClippingHelper clippingHelper = ClippingHelperImpl.getInstance();
   private double xPosition;
   private double yPosition;
   private double zPosition;
   private static final String __OBFID = "CL_00000976";

   public void setPosition(double p_78547_1_, double p_78547_3_, double p_78547_5_) {
      this.xPosition = p_78547_1_;
      this.yPosition = p_78547_3_;
      this.zPosition = p_78547_5_;
   }

   public boolean isBoxInFrustum(double p_78548_1_, double p_78548_3_, double p_78548_5_, double p_78548_7_, double p_78548_9_, double p_78548_11_) {
      return this.clippingHelper.isBoxInFrustum(p_78548_1_ - this.xPosition, p_78548_3_ - this.yPosition, p_78548_5_ - this.zPosition, p_78548_7_ - this.xPosition, p_78548_9_ - this.yPosition, p_78548_11_ - this.zPosition);
   }

   public boolean isBoxInFrustumFully(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return this.clippingHelper.isBoxInFrustumFully(minX - this.xPosition, minY - this.yPosition, minZ - this.zPosition, maxX - this.xPosition, maxY - this.yPosition, maxZ - this.zPosition);
   }

   public boolean isBoundingBoxInFrustum(AxisAlignedBB p_78546_1_) {
      return this.isBoxInFrustum(p_78546_1_.minX, p_78546_1_.minY, p_78546_1_.minZ, p_78546_1_.maxX, p_78546_1_.maxY, p_78546_1_.maxZ);
   }

   public boolean isBoundingBoxInFrustumFully(AxisAlignedBB p_78546_1_) {
      return this.isBoxInFrustumFully(p_78546_1_.minX, p_78546_1_.minY, p_78546_1_.minZ, p_78546_1_.maxX, p_78546_1_.maxY, p_78546_1_.maxZ);
   }
}
