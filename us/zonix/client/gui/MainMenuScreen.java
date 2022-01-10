package us.zonix.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import us.zonix.client.Client;
import us.zonix.client.util.RenderUtil;

public final class MainMenuScreen extends GuiScreen {
   public static final ResourceLocation closeIcon = new ResourceLocation("icon/close.png");
   private static final ResourceLocation panoramaBackground = new ResourceLocation("background.png");
   private static final ResourceLocation panoramaBlur;
   private static final ResourceLocation languageIcon = new ResourceLocation("icon/language.png");
   private static final ResourceLocation settingsIcon = new ResourceLocation("icon/settings.png");
   private static final ResourceLocation zonixLogo = new ResourceLocation("zonix.png");
   private int panoramaTimer;

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
   }

   protected void mouseClicked(int mouseX, int mouseY, int button) {
      if (button == 0) {
         ScaledResolution resolution = new ScaledResolution(this.mc);
         float buttonWidth = 90.0F;
         float startX = (float)resolution.getScaledWidth() - 10.0F;

         for(int i = 0; i < 3; ++i) {
            boolean hovering = (float)mouseX > startX - buttonWidth && (float)mouseX < startX && (float)mouseY > 7.5F && (float)mouseY < 35.0F;
            if (hovering) {
               switch(i) {
               case 0:
                  this.mc.shutdown();
                  break;
               case 1:
                  this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                  break;
               case 2:
                  this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
               }

               return;
            }

            startX -= buttonWidth + 10.0F;
         }

         float height = 200.0F;
         float minX = (float)(resolution.getScaledWidth() / 2) - 165.0F;
         float minY = (float)(resolution.getScaledHeight() / 2) - height / 2.0F + height - 60.0F;

         for(int i = 0; i < 2; ++i) {
            if ((float)mouseX > minX && (float)mouseX < minX + 150.0F && (float)mouseY > minY && (float)mouseY < minY + 35.0F) {
               switch(i) {
               case 0:
                  this.mc.displayGuiScreen(new GuiSelectWorld(this));
                  break;
               case 1:
                  this.mc.displayGuiScreen(new GuiMultiplayer(this));
               }

               return;
            }

            minX += 180.0F;
         }

      }
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution resolution = new ScaledResolution(this.mc);
      RenderUtil.drawTexture(panoramaBackground, 0.0F, 0.0F, (float)resolution.getScaledWidth(), (float)resolution.getScaledHeight());
      RenderUtil.drawRect(0.0F, 0.0F, (float)resolution.getScaledWidth(), (float)resolution.getScaledHeight(), -1358954496);
      RenderUtil.drawRect(0.0F, 0.0F, (float)resolution.getScaledWidth(), 40.0F, -1450294203);
      RenderUtil.drawString(Client.getInstance().getLargeBoldFontRenderer(), "ZONIX CLIENT", 50.0F, 12.0F, -1);
      float buttonWidth = 90.0F;
      float startX = (float)resolution.getScaledWidth() - 10.0F;

      for(int i = 0; i < 3; ++i) {
         boolean hovering = (float)mouseX > startX - buttonWidth && (float)mouseX < startX && (float)mouseY > 7.5F && (float)mouseY < 35.0F;
         RenderUtil.drawBorderedRoundedRect(startX - buttonWidth, 9.5F, startX, 33.0F, 5.0F, -11000539, -7848387);
         switch(i) {
         case 0:
            RenderUtil.drawCenteredString(Client.getInstance().getMediumBoldFontRenderer(), "Close", (float)((int)(startX - buttonWidth / 2.0F + 10.0F + 0.5F)), 21.0F, hovering ? -1 : -1442840577);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, hovering ? 1.0F : 0.6F);
            RenderUtil.drawSquareTexture(closeIcon, 9.0F, startX - buttonWidth / 2.0F - 28.0F, 12.0F);
            break;
         case 1:
            RenderUtil.drawCenteredString(Client.getInstance().getMediumBoldFontRenderer(), "Settings", (float)((int)(startX - buttonWidth / 2.0F + 10.0F + 0.5F)), 21.0F, hovering ? -1 : -1442840577);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, hovering ? 1.0F : 0.6F);
            RenderUtil.drawSquareTexture(settingsIcon, 9.0F, startX - buttonWidth / 2.0F - 37.0F, 12.0F);
            break;
         case 2:
            RenderUtil.drawCenteredString(Client.getInstance().getMediumBoldFontRenderer(), "Language", (float)((int)(startX - buttonWidth / 2.0F + 9.5F)), 21.0F, hovering ? -1 : -1442840577);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, hovering ? 1.0F : 0.6F);
            RenderUtil.drawSquareTexture(languageIcon, 9.0F, startX - buttonWidth / 2.0F - 42.0F, 12.0F);
         }

         startX -= buttonWidth + 10.0F;
      }

      float width = 400.0F;
      float height = 200.0F;
      float minX = (float)(resolution.getScaledWidth() / 2) - width / 2.0F;
      float minY = (float)(resolution.getScaledHeight() / 2) - height / 2.0F;
      RenderUtil.drawBorderedRect(minX, minY, minX + width, minY + height, 5.0F, -1904659663, 1935618353);
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderUtil.drawSquareTexture(zonixLogo, 130.0F, 120.0F, (float)(resolution.getScaledWidth() / 2) - 65.0F, minY + 12.0F);
      GL11.glPopMatrix();
      String[] strings = new String[]{"SINGLEPLAYER", "MULTIPLAYER"};
      minX = (float)(resolution.getScaledWidth() / 2) - 165.0F;
      minY += height - 60.0F;

      for(int i = 0; i < 2; ++i) {
         RenderUtil.drawBorderedRoundedRect(minX, minY, minX + 150.0F, minY + 35.0F, 5.0F, 2.0F, -7593185, -14413294);
         boolean hovering = (float)mouseX >= minX && (float)mouseX <= minX + 150.0F && (float)mouseY >= minY && (float)mouseY <= minY + 35.0F;
         RenderUtil.drawCenteredString(Client.getInstance().getMediumBoldFontRenderer(), strings[i], (float)((int)(minX + 73.0F)), (float)((int)(minY + 17.5F)), hovering ? -1 : -1442840577);
         minX += 180.0F;
      }

      RenderUtil.drawRect(0.0F, (float)resolution.getScaledHeight() - 20.0F, (float)resolution.getScaledWidth(), (float)resolution.getScaledHeight(), -1455867591);
      RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), "Property of Zonix, LLC", 10.0F, (float)resolution.getScaledHeight() - 12.0F, -1);
      RenderUtil.drawCenteredString(Client.getInstance().getSmallFontRenderer(), "Zonix is in no way affiliated with Mojang, AB.", (float)(resolution.getScaledWidth() / 2), (float)(resolution.getScaledHeight() - 10), -1);
      String version = "1.0.0-Dev-Snapshot";
      RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), version, (float)resolution.getScaledWidth() - 10.0F - (float)Client.getInstance().getSmallFontRenderer().getStringWidth(version), (float)resolution.getScaledHeight() - 12.0F, -1);
   }

   public void updateScreen() {
      ++this.panoramaTimer;
   }

   private void renderSkybox(float partialTicks) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GL11.glViewport(0, 0, 256, 256);
      this.drawPanorama(partialTicks);
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.rotateAndBlurSkybox();
      this.mc.getFramebuffer().bindFramebuffer(true);
      GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      Tessellator var4 = Tessellator.instance;
      var4.startDrawingQuads();
      float var5 = this.width > this.height ? 120.0F / (float)this.width : 120.0F / (float)this.height;
      float var6 = (float)this.height * var5 / 256.0F;
      float var7 = (float)this.width * var5 / 256.0F;
      var4.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
      int var8 = this.width;
      int var9 = this.height;
      var4.addVertexWithUV(0.0D, (double)var9, (double)this.zLevel, (double)(0.5F - var6), (double)(0.5F + var7));
      var4.addVertexWithUV((double)var8, (double)var9, (double)this.zLevel, (double)(0.5F - var6), (double)(0.5F - var7));
      var4.addVertexWithUV((double)var8, 0.0D, (double)this.zLevel, (double)(0.5F + var6), (double)(0.5F - var7));
      var4.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(0.5F + var6), (double)(0.5F + var7));
      var4.draw();
   }

   private void drawPanorama(float partialTicks) {
      Tessellator var4 = Tessellator.instance;
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
      GL11.glEnable(3042);
      GL11.glDisable(3008);
      GL11.glDisable(2884);
      GL11.glDepthMask(false);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      byte var5 = 8;

      for(int var6 = 0; var6 < var5 * var5; ++var6) {
         GL11.glPushMatrix();
         float var7 = ((float)(var6 % var5) / (float)var5 - 0.5F) / 64.0F;
         float var8 = ((float)(var6 / var5) / (float)var5 - 0.5F) / 64.0F;
         float var9 = 0.0F;
         GL11.glTranslatef(var7, var8, var9);
         GL11.glRotatef(MathHelper.sin(((float)this.panoramaTimer + partialTicks) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(-((float)this.panoramaTimer + partialTicks) * 0.1F, 0.0F, 1.0F, 0.0F);

         for(int var10 = 0; var10 < 6; ++var10) {
            GL11.glPushMatrix();
            if (var10 == 1) {
               GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var10 == 2) {
               GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var10 == 3) {
               GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var10 == 4) {
               GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var10 == 5) {
               GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            this.mc.getTextureManager().bindTexture(panoramaBackground);
            var4.startDrawingQuads();
            var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
            float var11 = 0.0F;
            var4.addVertexWithUV(-1.0D, -1.0D, 1.0D, (double)(0.0F + var11), (double)(0.0F + var11));
            var4.addVertexWithUV(1.0D, -1.0D, 1.0D, (double)(1.0F - var11), (double)(0.0F + var11));
            var4.addVertexWithUV(1.0D, 1.0D, 1.0D, (double)(1.0F - var11), (double)(1.0F - var11));
            var4.addVertexWithUV(-1.0D, 1.0D, 1.0D, (double)(0.0F + var11), (double)(1.0F - var11));
            var4.draw();
            GL11.glPopMatrix();
         }

         GL11.glPopMatrix();
         GL11.glColorMask(true, true, true, false);
      }

      var4.setTranslation(0.0D, 0.0D, 0.0D);
      GL11.glColorMask(true, true, true, true);
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GL11.glDepthMask(true);
      GL11.glEnable(2884);
      GL11.glEnable(2929);
   }

   private void rotateAndBlurSkybox() {
      this.mc.getTextureManager().bindTexture(panoramaBlur);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
      GL11.glEnable(3042);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      GL11.glColorMask(true, true, true, false);
      Tessellator var2 = Tessellator.instance;
      var2.startDrawingQuads();
      GL11.glDisable(3008);
      byte var3 = 3;

      for(int var4 = 0; var4 < var3; ++var4) {
         var2.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float)(var4 + 1));
         int var5 = this.width;
         int var6 = this.height;
         float var7 = (float)(var4 - var3 / 2) / 256.0F;
         var2.addVertexWithUV((double)var5, (double)var6, (double)this.zLevel, (double)(0.0F + var7), 1.0D);
         var2.addVertexWithUV((double)var5, 0.0D, (double)this.zLevel, (double)(1.0F + var7), 1.0D);
         var2.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(1.0F + var7), 0.0D);
         var2.addVertexWithUV(0.0D, (double)var6, (double)this.zLevel, (double)(0.0F + var7), 0.0D);
      }

      var2.draw();
      GL11.glEnable(3008);
      GL11.glColorMask(true, true, true, true);
   }

   static {
      DynamicTexture viewportTexture = new DynamicTexture(256, 256);
      panoramaBlur = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("background", viewportTexture);
   }
}
