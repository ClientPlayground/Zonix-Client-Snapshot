package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSlime extends RenderLiving {
   private static final ResourceLocation slimeTextures = new ResourceLocation("textures/entity/slime/slime.png");
   private ModelBase scaleAmount;
   private static final String __OBFID = "CL_00001024";

   public RenderSlime(ModelBase p_i1267_1_, ModelBase p_i1267_2_, float p_i1267_3_) {
      super(p_i1267_1_, p_i1267_3_);
      this.scaleAmount = p_i1267_2_;
   }

   public int shouldRenderPass(EntitySlime p_77032_1_, int p_77032_2_, float p_77032_3_) {
      if (p_77032_1_.isInvisible()) {
         return 0;
      } else if (p_77032_2_ == 0) {
         this.setRenderPassModel(this.scaleAmount);
         GL11.glEnable(2977);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         return 1;
      } else {
         if (p_77032_2_ == 1) {
            GL11.glDisable(3042);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         }

         return -1;
      }
   }

   public void preRenderCallback(EntitySlime p_77041_1_, float p_77041_2_) {
      float var3 = (float)p_77041_1_.getSlimeSize();
      float var4 = (p_77041_1_.prevSquishFactor + (p_77041_1_.squishFactor - p_77041_1_.prevSquishFactor) * p_77041_2_) / (var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      GL11.glScalef(var5 * var3, 1.0F / var5 * var3, var5 * var3);
   }

   public ResourceLocation getEntityTexture(EntitySlime p_110775_1_) {
      return slimeTextures;
   }

   public void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_) {
      this.preRenderCallback((EntitySlime)p_77041_1_, p_77041_2_);
   }

   public int shouldRenderPass(EntityLivingBase p_77032_1_, int p_77032_2_, float p_77032_3_) {
      return this.shouldRenderPass((EntitySlime)p_77032_1_, p_77032_2_, p_77032_3_);
   }

   public ResourceLocation getEntityTexture(Entity p_110775_1_) {
      return this.getEntityTexture((EntitySlime)p_110775_1_);
   }
}
