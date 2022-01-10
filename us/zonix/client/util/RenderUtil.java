package us.zonix.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.util.font.ZFontRenderer;

public final class RenderUtil {
   public static void drawCircle(double x, double y, double r) {
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      Tessellator tes = Tessellator.instance;
      tes.startDrawing(6);
      tes.addVertex(x, y, 0.0D);
      double end = 6.283185307179586D;
      double increment = end / 30.0D;

      for(double theta = -increment; theta < end; theta += increment) {
         tes.addVertex(x + r * Math.cos(-theta), y + r * Math.sin(-theta), 0.0D);
      }

      tes.draw();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
   }

   public static void drawSquareTexture(ResourceLocation resourceLocation, float size, float x, float y) {
      float height = size * 2.0F;
      float width = size * 2.0F;
      float u = 0.0F;
      float v = 0.0F;
      GL11.glEnable(3042);
      Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);
      GL11.glBegin(7);
      GL11.glTexCoord2d((double)(u / size), (double)(v / size));
      GL11.glVertex2d((double)x, (double)y);
      GL11.glTexCoord2d((double)(u / size), (double)((v + size) / size));
      GL11.glVertex2d((double)x, (double)(y + height));
      GL11.glTexCoord2d((double)((u + size) / size), (double)((v + size) / size));
      GL11.glVertex2d((double)(x + width), (double)(y + height));
      GL11.glTexCoord2d((double)((u + size) / size), (double)(v / size));
      GL11.glVertex2d((double)(x + width), (double)y);
      GL11.glEnd();
      GL11.glDisable(3042);
   }

   public static void drawTexturedModalRect(float x, float y, int width, int height, int u, int v) {
      Tessellator tessellator = Tessellator.instance;
      tessellator.startDrawingQuads();
      float scaleX = 0.00390625F;
      float scaleY = 0.00390625F;
      tessellator.addVertexWithUV((double)(x + 0.0F), (double)(y + (float)height), 0.0D, (double)((float)u * scaleX), (double)((float)(v + height) * scaleY));
      tessellator.addVertexWithUV((double)(x + (float)width), (double)(y + (float)height), 0.0D, (double)((float)(u + width) * scaleX), (double)((float)(v + height) * scaleY));
      tessellator.addVertexWithUV((double)(x + (float)width), (double)(y + 0.0F), 0.0D, (double)((float)(u + width) * scaleX), (double)((float)v * scaleY));
      tessellator.addVertexWithUV((double)(x + 0.0F), (double)(y + 0.0F), 0.0D, (double)((float)u * scaleX), (double)((float)v * scaleY));
      tessellator.draw();
   }

   public static void startScissorBox(float minY, float maxY, float minX, float maxX) {
      GL11.glPushMatrix();
      GL11.glEnable(3089);
      float width = maxX - minX;
      float height = maxY - minY;
      Minecraft mc = Minecraft.getMinecraft();
      float scale = (float)(new ScaledResolution(mc)).getScaleFactor();
      GL11.glScissor((int)(minX * scale), (int)((float)mc.displayHeight - (minY + height) * scale), (int)(width * scale), (int)(height * scale));
   }

   public static void endScissorBox() {
      GL11.glDisable(3089);
      GL11.glPopMatrix();
   }

   public static void drawTexture(ResourceLocation resourceLocation, float size, float x, float y) {
      GL11.glPushMatrix();
      float squareSize = size * 2.0F;
      GL11.glEnable(3042);
      GL11.glEnable(3553);
      bindTexture(resourceLocation);
      GL11.glBegin(7);
      GL11.glTexCoord2d(0.0D, 0.0D);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glTexCoord2d(0.0D, 1.0D);
      GL11.glVertex2d((double)x, (double)(y + squareSize));
      GL11.glTexCoord2d(1.0D, 1.0D);
      GL11.glVertex2d((double)(x + squareSize), (double)(y + squareSize));
      GL11.glTexCoord2d(1.0D, 0.0D);
      GL11.glVertex2d((double)(x + squareSize), (double)y);
      GL11.glEnd();
      GL11.glDisable(3553);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }

   public static void drawSquareTexture(ResourceLocation resourceLocation, float width, float height, float x, float y) {
      float size = width / 2.0F;
      float u = 0.0F;
      float v = 0.0F;
      GL11.glEnable(3042);
      Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);
      GL11.glBegin(7);
      GL11.glTexCoord2d((double)(u / size), (double)(v / size));
      GL11.glVertex2d((double)x, (double)y);
      GL11.glTexCoord2d((double)(u / size), (double)((v + size) / size));
      GL11.glVertex2d((double)x, (double)(y + height));
      GL11.glTexCoord2d((double)((u + size) / size), (double)((v + size) / size));
      GL11.glVertex2d((double)(x + width), (double)(y + height));
      GL11.glTexCoord2d((double)((u + size) / size), (double)(v / size));
      GL11.glVertex2d((double)(x + width), (double)y);
      GL11.glEnd();
      GL11.glDisable(3042);
   }

   public static void scaleAtPoint(float centerX, float centerY, float scale) {
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTranslatef(centerX, centerY, 0.0F);
      GL11.glScalef(scale, scale, 0.0F);
      GL11.glTranslatef(centerX * -1.0F, centerY * -1.0F, 0.0F);
   }

   public static void drawCenteredString(String text, int x, int y, int color) {
      ZFontRenderer fontRenderer = Client.getInstance().getRegularFontRenderer();
      drawCenteredString(fontRenderer, text, (float)x, (float)y, color);
   }

   public static void drawCenteredStringWithIcon(ResourceLocation resourceLocation, float width, ZFontRenderer fontRenderer, String text, int x, int y, int color) {
      int textX = (int)((float)x + width * 1.5F + 1.5F);
      drawCenteredString(fontRenderer, text, (float)textX, (float)(y + fontRenderer.getHeight()), color);
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
      float iconX = (float)x - width * 3.0F - 4.5F;
      drawSquareTexture(resourceLocation, width, iconX, (float)y + width / 4.0F);
      GL11.glPopMatrix();
   }

   public static void drawCenteredString(ZFontRenderer fontRenderer, String text, float x, float y, int color) {
      drawCenteredString(fontRenderer, text, x, y, color, true);
   }

   public static void drawCenteredString(ZFontRenderer fontRenderer, String text, float x, float y, int color, boolean shadow) {
      int width = fontRenderer.getStringWidth(text);
      int height = fontRenderer.getHeight();
      float dX = x - (float)(width / 2);
      float dY = y - (float)(height / 2);
      if (shadow) {
         fontRenderer.drawStringWithShadow(text, (double)dX, (double)dY, color);
      } else {
         fontRenderer.drawString(text, dX, dY, color);
      }

   }

   public static void drawSmallString(String text, float x, float y, int color) {
      ZFontRenderer fontRenderer = Client.getInstance().getSmallFontRenderer();
      fontRenderer.drawStringWithShadow(text, (double)((int)x), (double)((int)y), color);
   }

   public static void drawString(String text, float x, float y, int color) {
      ZFontRenderer fontRenderer = Client.getInstance().getRegularFontRenderer();
      fontRenderer.drawStringWithShadow(text, (double)((int)x), (double)((int)y), color);
   }

   public static void drawString(ZFontRenderer fontRenderer, String text, float x, float y, int color, boolean shadow) {
      if (shadow) {
         fontRenderer.drawStringWithShadow(text, (double)((int)x), (double)((int)y), color);
      } else {
         fontRenderer.drawString(text, (float)((int)x), (float)((int)y), color);
      }

   }

   public static void drawString(ZFontRenderer fontRenderer, String text, float x, float y, int color) {
      fontRenderer.drawStringWithShadow(text, (double)((int)x), (double)((int)y), color);
   }

   public static void drawString(String text, int x, int y, int color) {
      Client.getInstance().getRegularFontRenderer().drawStringWithShadow(text, (double)x, (double)y, color);
   }

   public static void bindTexture(ResourceLocation resourceLocation) {
      ITextureObject texture = Minecraft.getMinecraft().renderEngine.getTexture(resourceLocation);
      if (texture == null) {
         texture = new SimpleTexture(resourceLocation);
         Minecraft.getMinecraft().renderEngine.loadTexture(resourceLocation, (ITextureObject)texture);
      }

      GL11.glBindTexture(3553, ((ITextureObject)texture).getGlTextureId());
   }

   public static void drawTexture(ResourceLocation resourceLocation, float x, float y, float width, float height) {
      float size = width / 2.0F;
      float u = 0.0F;
      float v = 0.0F;
      GL11.glEnable(3042);
      bindTexture(resourceLocation);
      GL11.glBegin(7);
      GL11.glTexCoord2d((double)(u / size), (double)(v / size));
      GL11.glVertex2d((double)x, (double)y);
      GL11.glTexCoord2d((double)(u / size), (double)((v + size) / size));
      GL11.glVertex2d((double)x, (double)(y + height));
      GL11.glTexCoord2d((double)((u + size) / size), (double)((v + size) / size));
      GL11.glVertex2d((double)(x + width), (double)(y + height));
      GL11.glTexCoord2d((double)((u + size) / size), (double)(v / size));
      GL11.glVertex2d((double)(x + width), (double)y);
      GL11.glEnd();
      GL11.glDisable(3042);
   }

   public static void drawBorderedRoundedRect(float x, float y, float x1, float y1, float borderSize, int borderC, int insideC) {
      drawRoundedRect((double)x, (double)y, (double)x1, (double)y1, (double)borderSize, borderC);
      drawRoundedRect((double)(x + 0.5F), (double)(y + 0.5F), (double)(x1 - 0.5F), (double)(y1 - 0.5F), (double)borderSize, insideC);
   }

   public static void drawBorderedRoundedRect(float x, float y, float x1, float y1, float radius, float borderSize, int borderC, int insideC) {
      drawRoundedRect((double)x, (double)y, (double)x1, (double)y1, (double)radius, borderC);
      drawRoundedRect((double)(x + borderSize), (double)(y + borderSize), (double)(x1 - borderSize), (double)(y1 - borderSize), (double)radius, insideC);
   }

   public static void drawTexturedRect(float x, float y, int width, int height, int u, int v) {
      float scale = 0.00390625F;
      Tessellator tessellator = Tessellator.instance;
      tessellator.startDrawingQuads();
      tessellator.addVertexWithUV((double)x, (double)(y + (float)v), 0.0D, (double)((float)width * scale), (double)(((float)height + (float)v) * scale));
      tessellator.addVertexWithUV((double)(x + (float)u), (double)(y + (float)v), 0.0D, (double)(((float)width + (float)u) * scale), (double)(((float)height + (float)v) * scale));
      tessellator.addVertexWithUV((double)(x + (float)u), (double)y, 0.0D, (double)(((float)width + (float)u) * scale), (double)((float)height * scale));
      tessellator.addVertexWithUV((double)x, (double)y, 0.0D, (double)((float)width * scale), (double)((float)height * scale));
      tessellator.draw();
   }

   public static void setColor(int color) {
      float r = (float)(color >> 24 & 255) / 255.0F;
      float g = (float)(color >> 16 & 255) / 255.0F;
      float b = (float)(color >> 8 & 255) / 255.0F;
      float a = (float)(color & 255) / 255.0F;
      GL11.glColor4f(r, g, b, a);
   }

   public static void drawRect(float minX, float minY, float maxX, float maxY, int color) {
      float bounds;
      if (minX < maxX) {
         bounds = minX;
         minX = maxX;
         maxX = bounds;
      }

      if (minY < maxY) {
         bounds = minY;
         minY = maxY;
         maxY = bounds;
      }

      float r = (float)(color >> 24 & 255) / 255.0F;
      float g = (float)(color >> 16 & 255) / 255.0F;
      float b = (float)(color >> 8 & 255) / 255.0F;
      float a = (float)(color & 255) / 255.0F;
      Tessellator tessellator = Tessellator.instance;
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(g, b, a, r);
      tessellator.startDrawingQuads();
      tessellator.addVertex((double)minX, (double)maxY, 0.0D);
      tessellator.addVertex((double)maxX, (double)maxY, 0.0D);
      tessellator.addVertex((double)maxX, (double)minY, 0.0D);
      tessellator.addVertex((double)minX, (double)minY, 0.0D);
      tessellator.draw();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
   }

   public static void drawBorderedRect(float x, float y, float x2, float y2, float border, int bColor, int color) {
      drawRect(x + border, y + border, x2 - border, y2 - border, color);
      drawRect(x, y + border, x + border, y2 - border, bColor);
      drawRect(x2 - border, y + border, x2, y2 - border, bColor);
      drawRect(x, y, x2, y + border, bColor);
      drawRect(x, y2 - border, x2, y2, bColor);
   }

   public static void drawHollowRect(float x, float y, float x2, float y2, float border, int bColor) {
      drawRect(x, y + border, x + border, y2 - border, bColor);
      drawRect(x2 - border, y + border, x2, y2 - border, bColor);
      drawRect(x, y, x2, y + border, bColor);
      drawRect(x, y2 - border, x2, y2, bColor);
   }

   public static void drawBorderedRect(int x, int y, int x2, int y2, int border, int bColor, int color) {
      Gui.drawRect(x + border, y + border, x2 - border, y2 - border, color);
      Gui.drawRect(x, y + border, x + border, y2 - border, bColor);
      Gui.drawRect(x2 - border, y + border, x2, y2 - border, bColor);
      Gui.drawRect(x, y, x2, y + border, bColor);
      Gui.drawRect(x, y2 - border, x2, y2, bColor);
   }

   public static void drawRoundedRect(double x, double y, double x1, double y1, double radius, int color) {
      float f = (float)(color >> 24 & 255) / 255.0F;
      float f1 = (float)(color >> 16 & 255) / 255.0F;
      float f2 = (float)(color >> 8 & 255) / 255.0F;
      float f3 = (float)(color & 255) / 255.0F;
      GL11.glPushAttrib(0);
      GL11.glScaled(0.5D, 0.5D, 0.5D);
      x *= 2.0D;
      y *= 2.0D;
      x1 *= 2.0D;
      y1 *= 2.0D;
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glColor4f(f1, f2, f3, f);
      GL11.glEnable(2848);
      GL11.glBegin(9);

      int i;
      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y1 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
      }

      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x1 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y1 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x1 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      GL11.glScaled(2.0D, 2.0D, 2.0D);
      GL11.glPopAttrib();
   }

   public static void drawRoundedTexturedRect(ResourceLocation resourceLocation, double x, double y, double x1, double y1, double radius, int color) {
      float f = (float)(color >> 24 & 255) / 255.0F;
      float f1 = (float)(color >> 16 & 255) / 255.0F;
      float f2 = (float)(color >> 8 & 255) / 255.0F;
      float f3 = (float)(color & 255) / 255.0F;
      GL11.glPushAttrib(0);
      GL11.glScaled(0.5D, 0.5D, 0.5D);
      x *= 2.0D;
      y *= 2.0D;
      x1 *= 2.0D;
      y1 *= 2.0D;
      GL11.glEnable(3042);
      GL11.glEnable(3553);
      GL11.glColor4f(f1, f2, f3, f);
      bindTexture(resourceLocation);
      GL11.glEnable(2848);
      GL11.glBegin(9);

      int i;
      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x + radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D, y1 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius * -1.0D);
      }

      for(i = 0; i <= 90; i += 3) {
         GL11.glVertex2d(x1 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y1 - radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
      }

      for(i = 90; i <= 180; i += 3) {
         GL11.glVertex2d(x1 - radius + Math.sin((double)i * 3.141592653589793D / 180.0D) * radius, y + radius + Math.cos((double)i * 3.141592653589793D / 180.0D) * radius);
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      GL11.glScaled(2.0D, 2.0D, 2.0D);
      GL11.glPopAttrib();
   }
}
