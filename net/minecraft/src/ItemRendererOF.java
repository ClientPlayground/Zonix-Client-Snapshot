package net.minecraft.src;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import us.zonix.client.module.impl.FPSBoost;

public class ItemRendererOF extends ItemRenderer {
   private Minecraft mc = null;
   private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

   public ItemRendererOF(Minecraft par1Minecraft) {
      super(par1Minecraft);
      this.mc = par1Minecraft;
   }

   public void renderItem(EntityLivingBase par1EntityLivingBase, ItemStack par2ItemStack, int par3) {
      GL11.glPushMatrix();
      TextureManager var4 = this.mc.getTextureManager();
      Item var5 = par2ItemStack.getItem();
      Block var6 = Block.getBlockFromItem(var5);
      if (par2ItemStack != null && var6 != null && var6.getRenderBlockPass() != 0) {
         GL11.glEnable(3042);
         GL11.glEnable(2884);
         OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      }

      if (par2ItemStack.getItemSpriteNumber() == 0 && var5 instanceof ItemBlock && RenderBlocks.renderItemIn3d(var6.getRenderType())) {
         var4.bindTexture(var4.getResourceLocation(0));
         if (par2ItemStack != null && var6 != null && var6.getRenderBlockPass() != 0) {
            GL11.glDepthMask(false);
            this.mc.renderGlobal.renderBlocksRg.renderBlockAsItem(var6, par2ItemStack.getItemDamage(), 1.0F);
            GL11.glDepthMask(true);
         } else {
            this.mc.renderGlobal.renderBlocksRg.renderBlockAsItem(var6, par2ItemStack.getItemDamage(), 1.0F);
         }
      } else {
         IIcon var7 = par1EntityLivingBase.getItemIcon(par2ItemStack, par3);
         if (var7 == null) {
            GL11.glPopMatrix();
            return;
         }

         var4.bindTexture(var4.getResourceLocation(par2ItemStack.getItemSpriteNumber()));
         TextureUtil.func_152777_a(false, false, 1.0F);
         Tessellator var8 = Tessellator.instance;
         float var9 = var7.getMinU();
         float var10 = var7.getMaxU();
         float var11 = var7.getMinV();
         float var12 = var7.getMaxV();
         float var13 = 0.0F;
         float var14 = 0.3F;
         GL11.glEnable(32826);
         GL11.glTranslatef(-var13, -var14, 0.0F);
         float var15 = 1.5F;
         GL11.glScalef(var15, var15, var15);
         GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
         renderItemIn2D(var8, var10, var11, var9, var12, var7.getIconWidth(), var7.getIconHeight(), 0.0625F);
         boolean renderEffect = par2ItemStack.hasEffect() && par3 == 0;
         if (renderEffect && FPSBoost.ITEM_GLINT.getValue().booleanValue()) {
            GL11.glDepthFunc(514);
            GL11.glDisable(2896);
            var4.bindTexture(RES_ITEM_GLINT);
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(768, 1, 1, 0);
            float var16 = 0.76F;
            GL11.glColor4f(0.5F * var16, 0.25F * var16, 0.8F * var16, 1.0F);
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            float var17 = 0.125F;
            GL11.glScalef(var17, var17, var17);
            float var18 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
            GL11.glTranslatef(var18, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
            renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, 16, 16, 0.0625F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(var17, var17, var17);
            var18 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
            GL11.glTranslatef(-var18, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
            renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, 16, 16, 0.0625F);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glDisable(3042);
            GL11.glEnable(2896);
            GL11.glDepthFunc(515);
         }

         GL11.glDisable(32826);
         var4.bindTexture(var4.getResourceLocation(par2ItemStack.getItemSpriteNumber()));
         TextureUtil.func_147945_b();
      }

      if (par2ItemStack != null && var6 != null && var6.getRenderBlockPass() != 0) {
         GL11.glDisable(3042);
      }

      GL11.glPopMatrix();
   }

   public void renderItemInFirstPerson(float par1) {
      this.mc.theWorld.renderItemInFirstPerson = true;
      super.renderItemInFirstPerson(par1);
      this.mc.theWorld.renderItemInFirstPerson = false;
   }
}
