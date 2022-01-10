package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderIronGolem extends RenderLiving {
   private static final ResourceLocation ironGolemTextures = new ResourceLocation("textures/entity/iron_golem.png");
   private final ModelIronGolem ironGolemModel;
   private static final String __OBFID = "CL_00001031";

   public RenderIronGolem() {
      super(new ModelIronGolem(), 0.5F);
      this.ironGolemModel = (ModelIronGolem)this.mainModel;
   }

   public void doRender(EntityIronGolem p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      super.doRender((EntityLiving)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   public ResourceLocation getEntityTexture(EntityIronGolem p_110775_1_) {
      return ironGolemTextures;
   }

   protected void rotateCorpse(EntityIronGolem p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      super.rotateCorpse(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
      if ((double)p_77043_1_.limbSwingAmount >= 0.01D) {
         float var5 = 13.0F;
         float var6 = p_77043_1_.limbSwing - p_77043_1_.limbSwingAmount * (1.0F - p_77043_4_) + 6.0F;
         float var7 = (Math.abs(var6 % var5 - var5 * 0.5F) - var5 * 0.25F) / (var5 * 0.25F);
         GL11.glRotatef(6.5F * var7, 0.0F, 0.0F, 1.0F);
      }

   }

   protected void renderEquippedItems(EntityIronGolem p_77029_1_, float p_77029_2_) {
      super.renderEquippedItems(p_77029_1_, p_77029_2_);
      if (p_77029_1_.getHoldRoseTick() != 0) {
         GL11.glEnable(32826);
         GL11.glPushMatrix();
         GL11.glRotatef(5.0F + 180.0F * this.ironGolemModel.ironGolemRightArm.rotateAngleX / 3.1415927F, 1.0F, 0.0F, 0.0F);
         GL11.glTranslatef(-0.6875F, 1.25F, -0.9375F);
         GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
         float var3 = 0.8F;
         GL11.glScalef(var3, -var3, var3);
         int var4 = p_77029_1_.getBrightnessForRender(p_77029_2_);
         int var5 = var4 % 65536;
         int var6 = var4 / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var5 / 1.0F, (float)var6 / 1.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindTexture(TextureMap.locationBlocksTexture);
         this.field_147909_c.renderBlockAsItem(Blocks.red_flower, 0, 1.0F);
         GL11.glPopMatrix();
         GL11.glDisable(32826);
      }

   }

   public void doRender(EntityLiving p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      this.doRender((EntityIronGolem)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected void renderEquippedItems(EntityLivingBase p_77029_1_, float p_77029_2_) {
      this.renderEquippedItems((EntityIronGolem)p_77029_1_, p_77029_2_);
   }

   protected void rotateCorpse(EntityLivingBase p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      this.rotateCorpse((EntityIronGolem)p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
   }

   public void doRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      this.doRender((EntityIronGolem)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   public ResourceLocation getEntityTexture(Entity p_110775_1_) {
      return this.getEntityTexture((EntityIronGolem)p_110775_1_);
   }

   public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      this.doRender((EntityIronGolem)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }
}
