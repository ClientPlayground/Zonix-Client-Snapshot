package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener {
   private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
   private int[] charWidth = new int[256];
   public int FONT_HEIGHT = 9;
   public Random fontRandom = new Random();
   private byte[] glyphWidth = new byte[65536];
   private int[] colorCode = new int[32];
   private final ResourceLocation locationFontTexture;
   private final TextureManager renderEngine;
   private float posX;
   private float posY;
   private boolean unicodeFlag;
   private boolean bidiFlag;
   private float red;
   private float blue;
   private float green;
   private float alpha;
   private int textColor;
   private boolean randomStyle;
   private boolean boldStyle;
   private boolean italicStyle;
   private boolean underlineStyle;
   private boolean strikethroughStyle;
   public boolean enabled = true;
   private static final String __OBFID = "CL_00000660";

   public FontRenderer(GameSettings p_i1035_1_, ResourceLocation p_i1035_2_, TextureManager p_i1035_3_, boolean p_i1035_4_) {
      this.locationFontTexture = p_i1035_2_;
      this.renderEngine = p_i1035_3_;
      this.unicodeFlag = p_i1035_4_;
      p_i1035_3_.bindTexture(this.locationFontTexture);

      for(int var5 = 0; var5 < 32; ++var5) {
         int var6 = (var5 >> 3 & 1) * 85;
         int var7 = (var5 >> 2 & 1) * 170 + var6;
         int var8 = (var5 >> 1 & 1) * 170 + var6;
         int var9 = (var5 >> 0 & 1) * 170 + var6;
         if (var5 == 6) {
            var7 += 85;
         }

         if (p_i1035_1_.anaglyph) {
            int var10 = (var7 * 30 + var8 * 59 + var9 * 11) / 100;
            int var11 = (var7 * 30 + var8 * 70) / 100;
            int var12 = (var7 * 30 + var9 * 70) / 100;
            var7 = var10;
            var8 = var11;
            var9 = var12;
         }

         if (var5 >= 16) {
            var7 /= 4;
            var8 /= 4;
            var9 /= 4;
         }

         this.colorCode[var5] = (var7 & 255) << 16 | (var8 & 255) << 8 | var9 & 255;
      }

      this.readGlyphSizes();
   }

   public void onResourceManagerReload(IResourceManager p_110549_1_) {
      this.readFontTexture();
   }

   private void readFontTexture() {
      BufferedImage var1;
      try {
         var1 = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(this.locationFontTexture).getInputStream());
      } catch (IOException var17) {
         throw new RuntimeException(var17);
      }

      int var2 = var1.getWidth();
      int var3 = var1.getHeight();
      int[] var4 = new int[var2 * var3];
      var1.getRGB(0, 0, var2, var3, var4, 0, var2);
      int var5 = var3 / 16;
      int var6 = var2 / 16;
      byte var7 = 1;
      float var8 = 8.0F / (float)var6;

      for(int var9 = 0; var9 < 256; ++var9) {
         int var10 = var9 % 16;
         int var11 = var9 / 16;
         if (var9 == 32) {
            this.charWidth[var9] = 3 + var7;
         }

         int var12;
         for(var12 = var6 - 1; var12 >= 0; --var12) {
            int var13 = var10 * var6 + var12;
            boolean var14 = true;

            for(int var15 = 0; var15 < var5 && var14; ++var15) {
               int var16 = (var11 * var6 + var15) * var2;
               if ((var4[var13 + var16] >> 24 & 255) != 0) {
                  var14 = false;
               }
            }

            if (!var14) {
               break;
            }
         }

         ++var12;
         this.charWidth[var9] = (int)(0.5D + (double)((float)var12 * var8)) + var7;
      }

   }

   private void readGlyphSizes() {
      try {
         InputStream var1 = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/glyph_sizes.bin")).getInputStream();
         var1.read(this.glyphWidth);
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   private float renderCharAtPos(int p_78278_1_, char p_78278_2_, boolean p_78278_3_) {
      return p_78278_2_ == ' ' ? 4.0F : ("ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".indexOf(p_78278_2_) != -1 && !this.unicodeFlag ? this.renderDefaultChar(p_78278_1_, p_78278_3_) : this.renderUnicodeChar(p_78278_2_, p_78278_3_));
   }

   private float renderDefaultChar(int p_78266_1_, boolean p_78266_2_) {
      float var3 = (float)(p_78266_1_ % 16 * 8);
      float var4 = (float)(p_78266_1_ / 16 * 8);
      float var5 = p_78266_2_ ? 1.0F : 0.0F;
      this.renderEngine.bindTexture(this.locationFontTexture);
      float var6 = (float)this.charWidth[p_78266_1_] - 0.01F;
      GL11.glBegin(5);
      GL11.glTexCoord2f(var3 / 128.0F, var4 / 128.0F);
      GL11.glVertex3f(this.posX + var5, this.posY, 0.0F);
      GL11.glTexCoord2f(var3 / 128.0F, (var4 + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX - var5, this.posY + 7.99F, 0.0F);
      GL11.glTexCoord2f((var3 + var6 - 1.0F) / 128.0F, var4 / 128.0F);
      GL11.glVertex3f(this.posX + var6 - 1.0F + var5, this.posY, 0.0F);
      GL11.glTexCoord2f((var3 + var6 - 1.0F) / 128.0F, (var4 + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX + var6 - 1.0F - var5, this.posY + 7.99F, 0.0F);
      GL11.glEnd();
      return (float)this.charWidth[p_78266_1_];
   }

   private ResourceLocation getUnicodePageLocation(int p_111271_1_) {
      if (unicodePageLocations[p_111271_1_] == null) {
         unicodePageLocations[p_111271_1_] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", p_111271_1_));
      }

      return unicodePageLocations[p_111271_1_];
   }

   private void loadGlyphTexture(int p_78257_1_) {
      this.renderEngine.bindTexture(this.getUnicodePageLocation(p_78257_1_));
   }

   private float renderUnicodeChar(char p_78277_1_, boolean p_78277_2_) {
      if (this.glyphWidth[p_78277_1_] == 0) {
         return 0.0F;
      } else {
         int var3 = p_78277_1_ / 256;
         this.loadGlyphTexture(var3);
         int var4 = this.glyphWidth[p_78277_1_] >>> 4;
         int var5 = this.glyphWidth[p_78277_1_] & 15;
         float var6 = (float)var4;
         float var7 = (float)(var5 + 1);
         float var8 = (float)(p_78277_1_ % 16 * 16) + var6;
         float var9 = (float)((p_78277_1_ & 255) / 16 * 16);
         float var10 = var7 - var6 - 0.02F;
         float var11 = p_78277_2_ ? 1.0F : 0.0F;
         GL11.glBegin(5);
         GL11.glTexCoord2f(var8 / 256.0F, var9 / 256.0F);
         GL11.glVertex3f(this.posX + var11, this.posY, 0.0F);
         GL11.glTexCoord2f(var8 / 256.0F, (var9 + 15.98F) / 256.0F);
         GL11.glVertex3f(this.posX - var11, this.posY + 7.99F, 0.0F);
         GL11.glTexCoord2f((var8 + var10) / 256.0F, var9 / 256.0F);
         GL11.glVertex3f(this.posX + var10 / 2.0F + var11, this.posY, 0.0F);
         GL11.glTexCoord2f((var8 + var10) / 256.0F, (var9 + 15.98F) / 256.0F);
         GL11.glVertex3f(this.posX + var10 / 2.0F - var11, this.posY + 7.99F, 0.0F);
         GL11.glEnd();
         return (var7 - var6) / 2.0F + 1.0F;
      }
   }

   public int drawStringWithShadow(String p_78261_1_, float p_78261_2_, float p_78261_3_, int p_78261_4_) {
      return this.drawString(p_78261_1_, (int)p_78261_2_, (int)p_78261_3_, p_78261_4_, true);
   }

   public int drawStringWithShadow(String p_78261_1_, int p_78261_2_, int p_78261_3_, int p_78261_4_) {
      return this.drawString(p_78261_1_, p_78261_2_, p_78261_3_, p_78261_4_, true);
   }

   public int drawString(String p_78276_1_, float p_78276_2_, float p_78276_3_, int p_78276_4_) {
      return this.drawString(p_78276_1_, (int)p_78276_2_, (int)p_78276_3_, p_78276_4_, false);
   }

   public int drawString(String p_78276_1_, int p_78276_2_, int p_78276_3_, int p_78276_4_) {
      return this.drawString(p_78276_1_, p_78276_2_, p_78276_3_, p_78276_4_, false);
   }

   public int drawString(String p_85187_1_, int p_85187_2_, int p_85187_3_, int p_85187_4_, boolean p_85187_5_) {
      GL11.glEnable(3008);
      this.resetStyles();
      int var6;
      if (p_85187_5_) {
         var6 = this.renderString(p_85187_1_, p_85187_2_ + 1, p_85187_3_ + 1, p_85187_4_, true);
         var6 = Math.max(var6, this.renderString(p_85187_1_, p_85187_2_, p_85187_3_, p_85187_4_, false));
      } else {
         var6 = this.renderString(p_85187_1_, p_85187_2_, p_85187_3_, p_85187_4_, false);
      }

      return var6;
   }

   private String func_147647_b(String p_147647_1_) {
      try {
         Bidi var2 = new Bidi((new ArabicShaping(8)).shape(p_147647_1_), 127);
         var2.setReorderingMode(0);
         return var2.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return p_147647_1_;
      }
   }

   private void resetStyles() {
      this.randomStyle = false;
      this.boldStyle = false;
      this.italicStyle = false;
      this.underlineStyle = false;
      this.strikethroughStyle = false;
   }

   private void renderStringAtPos(String p_78255_1_, boolean p_78255_2_) {
      for(int var3 = 0; var3 < p_78255_1_.length(); ++var3) {
         char var4 = p_78255_1_.charAt(var3);
         int var5;
         int var6;
         if (var4 == 167 && var3 + 1 < p_78255_1_.length()) {
            var5 = "0123456789abcdefklmnor".indexOf(p_78255_1_.toLowerCase().charAt(var3 + 1));
            if (var5 < 16) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               if (var5 < 0 || var5 > 15) {
                  var5 = 15;
               }

               if (p_78255_2_) {
                  var5 += 16;
               }

               var6 = this.colorCode[var5];
               this.textColor = var6;
               GL11.glColor4f((float)(var6 >> 16) / 255.0F, (float)(var6 >> 8 & 255) / 255.0F, (float)(var6 & 255) / 255.0F, this.alpha);
            } else if (var5 == 16) {
               this.randomStyle = true;
            } else if (var5 == 17) {
               this.boldStyle = true;
            } else if (var5 == 18) {
               this.strikethroughStyle = true;
            } else if (var5 == 19) {
               this.underlineStyle = true;
            } else if (var5 == 20) {
               this.italicStyle = true;
            } else if (var5 == 21) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               GL11.glColor4f(this.red, this.blue, this.green, this.alpha);
            }

            ++var3;
         } else {
            var5 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".indexOf(var4);
            if (this.randomStyle && var5 != -1) {
               do {
                  var6 = this.fontRandom.nextInt(this.charWidth.length);
               } while(this.charWidth[var5] != this.charWidth[var6]);

               var5 = var6;
            }

            float var11 = this.unicodeFlag ? 0.5F : 1.0F;
            boolean var7 = (var4 == 0 || var5 == -1 || this.unicodeFlag) && p_78255_2_;
            if (var7) {
               this.posX -= var11;
               this.posY -= var11;
            }

            float var8 = this.renderCharAtPos(var5, var4, this.italicStyle);
            if (var7) {
               this.posX += var11;
               this.posY += var11;
            }

            if (this.boldStyle) {
               this.posX += var11;
               if (var7) {
                  this.posX -= var11;
                  this.posY -= var11;
               }

               this.renderCharAtPos(var5, var4, this.italicStyle);
               this.posX -= var11;
               if (var7) {
                  this.posX += var11;
                  this.posY += var11;
               }

               ++var8;
            }

            Tessellator var9;
            if (this.strikethroughStyle) {
               var9 = Tessellator.instance;
               GL11.glDisable(3553);
               var9.startDrawingQuads();
               var9.addVertex((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D);
               var9.addVertex((double)(this.posX + var8), (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D);
               var9.addVertex((double)(this.posX + var8), (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
               var9.addVertex((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
               var9.draw();
               GL11.glEnable(3553);
            }

            if (this.underlineStyle) {
               var9 = Tessellator.instance;
               GL11.glDisable(3553);
               var9.startDrawingQuads();
               int var10 = this.underlineStyle ? -1 : 0;
               var9.addVertex((double)(this.posX + (float)var10), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D);
               var9.addVertex((double)(this.posX + var8), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D);
               var9.addVertex((double)(this.posX + var8), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D);
               var9.addVertex((double)(this.posX + (float)var10), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D);
               var9.draw();
               GL11.glEnable(3553);
            }

            this.posX += (float)((int)var8);
         }
      }

   }

   private int renderStringAligned(String p_78274_1_, int p_78274_2_, int p_78274_3_, int p_78274_4_, int p_78274_5_, boolean p_78274_6_) {
      if (this.bidiFlag) {
         int var7 = this.getStringWidth(this.func_147647_b(p_78274_1_));
         p_78274_2_ = p_78274_2_ + p_78274_4_ - var7;
      }

      return this.renderString(p_78274_1_, p_78274_2_, p_78274_3_, p_78274_5_, p_78274_6_);
   }

   private int renderString(String p_78258_1_, int p_78258_2_, int p_78258_3_, int p_78258_4_, boolean p_78258_5_) {
      if (p_78258_1_ == null) {
         return 0;
      } else {
         if (this.bidiFlag) {
            p_78258_1_ = this.func_147647_b(p_78258_1_);
         }

         if ((p_78258_4_ & -67108864) == 0) {
            p_78258_4_ |= -16777216;
         }

         if (p_78258_5_) {
            p_78258_4_ = (p_78258_4_ & 16579836) >> 2 | p_78258_4_ & -16777216;
         }

         this.red = (float)(p_78258_4_ >> 16 & 255) / 255.0F;
         this.blue = (float)(p_78258_4_ >> 8 & 255) / 255.0F;
         this.green = (float)(p_78258_4_ & 255) / 255.0F;
         this.alpha = (float)(p_78258_4_ >> 24 & 255) / 255.0F;
         GL11.glColor4f(this.red, this.blue, this.green, this.alpha);
         this.posX = (float)p_78258_2_;
         this.posY = (float)p_78258_3_;
         this.renderStringAtPos(p_78258_1_, p_78258_5_);
         return (int)this.posX;
      }
   }

   public int getStringWidth(String p_78256_1_) {
      if (p_78256_1_ == null) {
         return 0;
      } else {
         int var2 = 0;
         boolean var3 = false;

         for(int var4 = 0; var4 < p_78256_1_.length(); ++var4) {
            char var5 = p_78256_1_.charAt(var4);
            int var6 = this.getCharWidth(var5);
            if (var6 < 0 && var4 < p_78256_1_.length() - 1) {
               ++var4;
               var5 = p_78256_1_.charAt(var4);
               if (var5 != 'l' && var5 != 'L') {
                  if (var5 == 'r' || var5 == 'R') {
                     var3 = false;
                  }
               } else {
                  var3 = true;
               }

               var6 = 0;
            }

            var2 += var6;
            if (var3 && var6 > 0) {
               ++var2;
            }
         }

         return var2;
      }
   }

   public int getCharWidth(char p_78263_1_) {
      if (p_78263_1_ == 167) {
         return -1;
      } else if (p_78263_1_ == ' ') {
         return 4;
      } else {
         int var2 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000".indexOf(p_78263_1_);
         if (p_78263_1_ > 0 && var2 != -1 && !this.unicodeFlag) {
            return this.charWidth[var2];
         } else if (this.glyphWidth[p_78263_1_] != 0) {
            int var3 = this.glyphWidth[p_78263_1_] >>> 4;
            int var4 = this.glyphWidth[p_78263_1_] & 15;
            if (var4 > 7) {
               var4 = 15;
               var3 = 0;
            }

            ++var4;
            return (var4 - var3) / 2 + 1;
         } else {
            return 0;
         }
      }
   }

   public String trimStringToWidth(String p_78269_1_, int p_78269_2_) {
      return this.trimStringToWidth(p_78269_1_, p_78269_2_, false);
   }

   public String trimStringToWidth(String p_78262_1_, int p_78262_2_, boolean p_78262_3_) {
      StringBuilder var4 = new StringBuilder();
      int var5 = 0;
      int var6 = p_78262_3_ ? p_78262_1_.length() - 1 : 0;
      int var7 = p_78262_3_ ? -1 : 1;
      boolean var8 = false;
      boolean var9 = false;

      for(int var10 = var6; var10 >= 0 && var10 < p_78262_1_.length() && var5 < p_78262_2_; var10 += var7) {
         char var11 = p_78262_1_.charAt(var10);
         int var12 = this.getCharWidth(var11);
         if (var8) {
            var8 = false;
            if (var11 != 'l' && var11 != 'L') {
               if (var11 == 'r' || var11 == 'R') {
                  var9 = false;
               }
            } else {
               var9 = true;
            }
         } else if (var12 < 0) {
            var8 = true;
         } else {
            var5 += var12;
            if (var9) {
               ++var5;
            }
         }

         if (var5 > p_78262_2_) {
            break;
         }

         if (p_78262_3_) {
            var4.insert(0, var11);
         } else {
            var4.append(var11);
         }
      }

      return var4.toString();
   }

   private String trimStringNewline(String p_78273_1_) {
      while(p_78273_1_ != null && p_78273_1_.endsWith("\n")) {
         p_78273_1_ = p_78273_1_.substring(0, p_78273_1_.length() - 1);
      }

      return p_78273_1_;
   }

   public void drawSplitString(String p_78279_1_, int p_78279_2_, int p_78279_3_, int p_78279_4_, int p_78279_5_) {
      this.resetStyles();
      this.textColor = p_78279_5_;
      p_78279_1_ = this.trimStringNewline(p_78279_1_);
      this.renderSplitString(p_78279_1_, p_78279_2_, p_78279_3_, p_78279_4_, false);
   }

   private void renderSplitString(String p_78268_1_, int p_78268_2_, int p_78268_3_, int p_78268_4_, boolean p_78268_5_) {
      List var6 = this.listFormattedStringToWidth(p_78268_1_, p_78268_4_);

      for(Iterator var7 = var6.iterator(); var7.hasNext(); p_78268_3_ += this.FONT_HEIGHT) {
         String var8 = (String)var7.next();
         this.renderStringAligned(var8, p_78268_2_, p_78268_3_, p_78268_4_, this.textColor, p_78268_5_);
      }

   }

   public int splitStringWidth(String p_78267_1_, int p_78267_2_) {
      return this.FONT_HEIGHT * this.listFormattedStringToWidth(p_78267_1_, p_78267_2_).size();
   }

   public void setUnicodeFlag(boolean p_78264_1_) {
      this.unicodeFlag = p_78264_1_;
   }

   public boolean getUnicodeFlag() {
      return this.unicodeFlag;
   }

   public void setBidiFlag(boolean p_78275_1_) {
      this.bidiFlag = p_78275_1_;
   }

   public List listFormattedStringToWidth(String p_78271_1_, int p_78271_2_) {
      return Arrays.asList(this.wrapFormattedStringToWidth(p_78271_1_, p_78271_2_).split("\n"));
   }

   String wrapFormattedStringToWidth(String p_78280_1_, int p_78280_2_) {
      int var3 = this.sizeStringToWidth(p_78280_1_, p_78280_2_);
      if (p_78280_1_.length() <= var3) {
         return p_78280_1_;
      } else {
         String var4 = p_78280_1_.substring(0, var3);
         char var5 = p_78280_1_.charAt(var3);
         boolean var6 = var5 == ' ' || var5 == '\n';
         String var7 = getFormatFromString(var4) + p_78280_1_.substring(var3 + (var6 ? 1 : 0));
         return var4 + "\n" + this.wrapFormattedStringToWidth(var7, p_78280_2_);
      }
   }

   private int sizeStringToWidth(String p_78259_1_, int p_78259_2_) {
      int var3 = p_78259_1_.length();
      int var4 = 0;
      int var5 = 0;
      int var6 = -1;

      for(boolean var7 = false; var5 < var3; ++var5) {
         char var8 = p_78259_1_.charAt(var5);
         switch(var8) {
         case '\n':
            --var5;
            break;
         case ' ':
            var6 = var5;
         default:
            var4 += this.getCharWidth(var8);
            if (var7) {
               ++var4;
            }
            break;
         case '§':
            if (var5 < var3 - 1) {
               ++var5;
               char var9 = p_78259_1_.charAt(var5);
               if (var9 != 'l' && var9 != 'L') {
                  if (var9 == 'r' || var9 == 'R' || isFormatColor(var9)) {
                     var7 = false;
                  }
               } else {
                  var7 = true;
               }
            }
         }

         if (var8 == '\n') {
            ++var5;
            var6 = var5;
            break;
         }

         if (var4 > p_78259_2_) {
            break;
         }
      }

      return var5 != var3 && var6 != -1 && var6 < var5 ? var6 : var5;
   }

   private static boolean isFormatColor(char p_78272_0_) {
      return p_78272_0_ >= '0' && p_78272_0_ <= '9' || p_78272_0_ >= 'a' && p_78272_0_ <= 'f' || p_78272_0_ >= 'A' && p_78272_0_ <= 'F';
   }

   private static boolean isFormatSpecial(char p_78270_0_) {
      return p_78270_0_ >= 'k' && p_78270_0_ <= 'o' || p_78270_0_ >= 'K' && p_78270_0_ <= 'O' || p_78270_0_ == 'r' || p_78270_0_ == 'R';
   }

   private static String getFormatFromString(String p_78282_0_) {
      String var1 = "";
      int var2 = -1;
      int var3 = p_78282_0_.length();

      while((var2 = p_78282_0_.indexOf(167, var2 + 1)) != -1) {
         if (var2 < var3 - 1) {
            char var4 = p_78282_0_.charAt(var2 + 1);
            if (isFormatColor(var4)) {
               var1 = "§" + var4;
            } else if (isFormatSpecial(var4)) {
               var1 = var1 + "§" + var4;
            }
         }
      }

      return var1;
   }

   public boolean getBidiFlag() {
      return this.bidiFlag;
   }
}
