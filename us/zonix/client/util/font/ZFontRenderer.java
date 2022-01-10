package us.zonix.client.util.font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class ZFontRenderer extends ZFont {
   protected final ZFont.CharData[] boldItalicChars = new ZFont.CharData[256];
   protected final ZFont.CharData[] italicChars = new ZFont.CharData[256];
   protected final ZFont.CharData[] boldChars = new ZFont.CharData[256];
   private final int[] colorCode = new int[32];
   private char COLOR_CODE_START = 167;
   protected DynamicTexture texBold;
   protected DynamicTexture texItalic;
   protected DynamicTexture texItalicBold;

   public ZFontRenderer(ResourceLocation resourceLocation, float size) {
      super(resourceLocation, size);
      this.setupMinecraftColorCodes();
      this.setupBoldItalicIDs();
   }

   public float drawStringWithShadow(String text, double x, double y, int color, int shadowColor) {
      float shadowWidth = this.drawString(text, x + 1.0D, y + 1.0D, shadowColor, false);
      return Math.max(shadowWidth, this.drawString(text, x, y, color, false));
   }

   public float drawStringWithShadow(String text, double x, double y, int color) {
      float shadowWidth = this.drawString(text, x + 1.0D, y + 1.0D, color, true);
      return Math.max(shadowWidth, this.drawString(text, x, y, color, false));
   }

   public float drawString(String text, float x, float y, int color) {
      return this.drawString(text, (double)x, (double)y, color, false);
   }

   public float drawCenteredString(String text, float x, float y, int color) {
      return this.drawString(text, x - (float)(this.getStringWidth(text) / 2), y, color);
   }

   public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
      this.drawString(text, (double)(x - (float)(this.getStringWidth(text) / 2)) + 1.0D, (double)y + 1.0D, color, true);
      return this.drawString(text, x - (float)(this.getStringWidth(text) / 2), y, color);
   }

   public float drawString(String text, double x, double y, int color, boolean shadow) {
      --x;
      y -= 0.5D;
      if (text == null) {
         return 0.0F;
      } else {
         if (color == 553648127) {
            color = 16777215;
         }

         if ((color & -67108864) == 0) {
            color |= -16777216;
         }

         if (shadow) {
            color = (color & 16579836) >> 2 | color & -16777216;
         }

         ZFont.CharData[] currentData = this.charData;
         float alpha = (float)(color >> 24 & 255) / 255.0F;
         boolean bold = false;
         boolean italic = false;
         boolean strike = false;
         boolean underline = false;
         boolean render = true;
         x *= 2.0D;
         y = (y - 5.0D) * 2.0D;
         if (render) {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glScaled(0.5D, 0.5D, 0.5D);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, alpha);
            int size = text.length();
            GL11.glEnable(3553);
            GL11.glBindTexture(3553, this.tex.getGlTextureId());

            for(int i = 0; i < size; ++i) {
               char character = text.charAt(i);
               if (character == this.COLOR_CODE_START && i < size) {
                  int colorIndex = 21;

                  try {
                     colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                  } catch (Exception var20) {
                     var20.printStackTrace();
                  }

                  if (colorIndex < 16) {
                     bold = false;
                     italic = false;
                     underline = false;
                     strike = false;
                     GL11.glBindTexture(3553, this.tex.getGlTextureId());
                     currentData = this.charData;
                     if (colorIndex < 0 || colorIndex > 15) {
                        colorIndex = 15;
                     }

                     if (shadow) {
                        colorIndex += 16;
                     }

                     int cc = this.colorCode[colorIndex];
                     GL11.glColor4f((float)(cc >> 16 & 255) / 255.0F, (float)(cc >> 8 & 255) / 255.0F, (float)(cc & 255) / 255.0F, alpha);
                  } else if (colorIndex != 16) {
                     if (colorIndex == 17) {
                        bold = true;
                        if (italic) {
                           GL11.glBindTexture(3553, this.texItalicBold.getGlTextureId());
                           currentData = this.boldItalicChars;
                        } else {
                           GL11.glBindTexture(3553, this.texBold.getGlTextureId());
                           currentData = this.boldChars;
                        }
                     } else if (colorIndex == 18) {
                        strike = true;
                     } else if (colorIndex == 19) {
                        underline = true;
                     } else if (colorIndex == 20) {
                        italic = true;
                        if (bold) {
                           GL11.glBindTexture(3553, this.texItalicBold.getGlTextureId());
                           currentData = this.boldItalicChars;
                        } else {
                           GL11.glBindTexture(3553, this.texItalic.getGlTextureId());
                           currentData = this.italicChars;
                        }
                     } else if (colorIndex == 21) {
                        bold = false;
                        italic = false;
                        underline = false;
                        strike = false;
                        GL11.glColor4f((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, alpha);
                        GL11.glBindTexture(3553, this.tex.getGlTextureId());
                        currentData = this.charData;
                     }
                  }

                  ++i;
               } else if (character < currentData.length && character >= 0) {
                  GL11.glBegin(4);
                  this.drawChar(currentData, character, (float)x, (float)y + 6.0F);
                  GL11.glEnd();
                  if (strike) {
                     this.drawLine(x, y + (double)(currentData[character].height / 2), x + (double)currentData[character].width - 8.0D, y + (double)(currentData[character].height / 2), 1.0F);
                  }

                  if (underline) {
                     this.drawLine(x, y + (double)currentData[character].height - 2.0D, x + (double)currentData[character].width - 8.0D, y + (double)currentData[character].height - 2.0D, 1.0F);
                  }

                  x += (double)(currentData[character].width - 8 + this.charOffset);
               }
            }

            GL11.glHint(3155, 4352);
            GL11.glPopMatrix();
         }

         return (float)x / 2.0F;
      }
   }

   public int getStringWidth(String text) {
      if (text == null) {
         return 0;
      } else {
         int width = 0;
         ZFont.CharData[] currentData = this.charData;
         boolean bold = false;
         boolean italic = false;
         int size = text.length();

         for(int i = 0; i < size; ++i) {
            char character = text.charAt(i);
            if (character == this.COLOR_CODE_START && i < size) {
               int colorIndex = "0123456789abcdefklmnor".indexOf(character);
               if (colorIndex < 16) {
                  bold = false;
                  italic = false;
               } else if (colorIndex == 17) {
                  bold = true;
                  if (italic) {
                     currentData = this.boldItalicChars;
                  } else {
                     currentData = this.boldChars;
                  }
               } else if (colorIndex == 20) {
                  italic = true;
                  if (bold) {
                     currentData = this.boldItalicChars;
                  } else {
                     currentData = this.italicChars;
                  }
               } else if (colorIndex == 21) {
                  bold = false;
                  italic = false;
                  currentData = this.charData;
               }

               ++i;
            } else if (character < currentData.length && character >= 0) {
               width += currentData[character].width - 8 + this.charOffset;
            }
         }

         return width / 2;
      }
   }

   public void setAntiAlias(boolean antiAlias) {
      super.setAntiAlias(antiAlias);
      this.setupBoldItalicIDs();
   }

   public void setFractionalMetrics(boolean fractionalMetrics) {
      super.setFractionalMetrics(fractionalMetrics);
      this.setupBoldItalicIDs();
   }

   private void setupBoldItalicIDs() {
      this.texBold = this.setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
      this.texItalic = this.setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
      this.texItalicBold = this.setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
   }

   private void drawLine(double x, double y, double x1, double y1, float width) {
      GL11.glDisable(3553);
      GL11.glLineWidth(width);
      GL11.glBegin(1);
      GL11.glVertex2d(x, y);
      GL11.glVertex2d(x1, y1);
      GL11.glEnd();
      GL11.glEnable(3553);
   }

   public List wrapWords(String text, double width) {
      List finalWords = new ArrayList();
      if ((double)this.getStringWidth(text) > width) {
         String[] words = text.split(" ");
         String currentWord = "";
         char lastColorCode = '\uffff';
         String[] var8 = words;
         int var9 = words.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            String word = var8[var10];

            for(int i = 0; i < word.toCharArray().length; ++i) {
               char c = word.toCharArray()[i];
               if (c == this.COLOR_CODE_START && i < word.toCharArray().length - 1) {
                  lastColorCode = word.toCharArray()[i + 1];
               }
            }

            if ((double)this.getStringWidth(currentWord + word + " ") < width) {
               currentWord = currentWord + word + " ";
            } else {
               finalWords.add(currentWord);
               currentWord = this.COLOR_CODE_START + lastColorCode + word + " ";
            }
         }

         if (currentWord.length() > 0) {
            if ((double)this.getStringWidth(currentWord) < width) {
               finalWords.add(this.COLOR_CODE_START + lastColorCode + currentWord + " ");
               currentWord = "";
            } else {
               Iterator var14 = this.formatString(currentWord, width).iterator();

               while(var14.hasNext()) {
                  String s = (String)var14.next();
                  finalWords.add(s);
               }
            }
         }
      } else {
         finalWords.add(text);
      }

      return finalWords;
   }

   public List formatString(String string, double width) {
      List finalWords = new ArrayList();
      String currentWord = "";
      char lastColorCode = '\uffff';
      char[] chars = string.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         char c = chars[i];
         if (c == this.COLOR_CODE_START && i < chars.length - 1) {
            lastColorCode = chars[i + 1];
         }

         if ((double)this.getStringWidth(currentWord + c) < width) {
            currentWord = currentWord + c;
         } else {
            finalWords.add(currentWord);
            currentWord = this.COLOR_CODE_START + lastColorCode + String.valueOf(c);
         }
      }

      if (currentWord.length() > 0) {
         finalWords.add(currentWord);
      }

      return finalWords;
   }

   private void setupMinecraftColorCodes() {
      for(int index = 0; index < 32; ++index) {
         int alpha = (index >> 3 & 1) * 85;
         int red = (index >> 2 & 1) * 170 + alpha;
         int green = (index >> 1 & 1) * 170 + alpha;
         int blue = (index & 1) * 170 + alpha;
         if (index == 6) {
            red += 85;
         }

         if (index >= 16) {
            red /= 4;
            green /= 4;
            blue /= 4;
         }

         this.colorCode[index] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
      }

   }
}
