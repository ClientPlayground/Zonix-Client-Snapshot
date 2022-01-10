package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSpider extends RenderLiving {
   private static final ResourceLocation spiderEyesTextures = new ResourceLocation("textures/entity/spider_eyes.png");
   private static final ResourceLocation spiderTextures = new ResourceLocation("textures/entity/spider/spider.png");
   private static final String __OBFID = "CL_00001027";

   public RenderSpider() {
      super(new ModelSpider(), 1.0F);
      this.setRenderPassModel(new ModelSpider());
   }

   protected float getDeathMaxRotation(EntitySpider p_77037_1_) {
      return 180.0F;
   }

   public int shouldRenderPass(EntitySpider p_77032_1_, int p_77032_2_, float p_77032_3_) {
      if (p_77032_2_ != 0) {
         return -1;
      } else {
         this.bindTexture(spiderEyesTextures);
         GL11.glEnable(3042);
         GL11.glDisable(3008);
         GL11.glBlendFunc(1, 1);
         if (p_77032_1_.isInvisible()) {
            GL11.glDepthMask(false);
         } else {
            GL11.glDepthMask(true);
         }

         char var4 = '\uf0f0';
         int var5 = var4 % 65536;
         int var6 = var4 / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var5 / 1.0F, (float)var6 / 1.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         return 1;
      }
   }

   public ResourceLocation getEntityTexture(EntitySpider p_110775_1_) {
      return spiderTextures;
   }

   protected float getDeathMaxRotation(EntityLivingBase p_77037_1_) {
      return this.getDeathMaxRotation((EntitySpider)p_77037_1_);
   }

   public int shouldRenderPass(EntityLivingBase p_77032_1_, int p_77032_2_, float p_77032_3_) {
      return this.shouldRenderPass((EntitySpider)p_77032_1_, p_77032_2_, p_77032_3_);
   }

   public ResourceLocation getEntityTexture(Entity p_110775_1_) {
      return this.getEntityTexture((EntitySpider)p_110775_1_);
   }
}
