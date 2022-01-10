package us.zonix.client.gui.component.impl.menu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.gui.component.IComponent;
import us.zonix.client.module.IModule;
import us.zonix.client.setting.ISetting;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.FloatSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.setting.impl.StringSetting;
import us.zonix.client.setting.impl.TextSetting;
import us.zonix.client.util.RenderUtil;

public final class MenuComponent implements IComponent {
   private static final ResourceLocation ARROW_RIGHT = new ResourceLocation("icon/caret-right.png");
   private static final ResourceLocation ARROW_LEFT = new ResourceLocation("icon/caret-left.png");
   private static final ResourceLocation TOGGLE_OFF = new ResourceLocation("icon/toggle-off.png");
   private static final ResourceLocation TOGGLE_ON = new ResourceLocation("icon/toggle-on.png");
   private static final ResourceLocation SETTINGS = new ResourceLocation("icon/settings.png");
   private MenuComponent.EnumMenuType menuType;
   private FloatSetting draggingSetting;
   private IModule editing;
   private int switchTime;
   private int scrollAmount;
   private boolean hiding;
   private int hidingTicks;
   private int width;
   private int height;
   private int x;
   private int y;

   public MenuComponent() {
      this.menuType = MenuComponent.EnumMenuType.MODS;
      this.switchTime = 450;
   }

   public void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void onOpen() {
   }

   public void tick() {
      ++this.hidingTicks;
      if ((float)this.switchTime <= 450.0F && this.switchTime >= 0) {
         if (this.menuType == MenuComponent.EnumMenuType.MODS) {
            this.switchTime -= 25;
         } else {
            this.switchTime += 25;
         }
      }

   }

   public void onMouseRelease() {
      this.draggingSetting = null;
      Client.getInstance().getModuleManager().getModules().forEach((module) -> {
         module.getSettingMap().values().forEach((setting) -> {
            if (setting instanceof ColorSetting) {
               ColorSetting colorSetting = (ColorSetting)setting;
               colorSetting.setDraggingAlpha(false);
               colorSetting.setDraggingHue(false);
               colorSetting.setDraggingAll(false);
            }

         });
      });
   }

   public void onKeyPress(int code, char c) {
      if (this.menuType == MenuComponent.EnumMenuType.MOD) {
         List sortedSettings = this.editing.getSortedSettings();

         for(int i = 0; i < sortedSettings.size(); ++i) {
            ISetting setting = (ISetting)sortedSettings.get(i);
            if (setting instanceof TextSetting && ((TextSetting)setting).isEditing()) {
               String value = setting.getValue().toString();
               if (code == 14) {
                  if (!value.isEmpty()) {
                     ((TextSetting)setting).setValue(value.substring(0, value.length() - 1));
                  }
               } else if (code == 28) {
                  ((TextSetting)setting).setEditing(false);
               } else {
                  if (code == 15) {
                     ((TextSetting)setting).setEditing(false);
                     if (i < sortedSettings.size() - 1) {
                        ISetting next = (ISetting)sortedSettings.get(i + 1);
                        if (next instanceof TextSetting) {
                           ((TextSetting)next).setEditing(true);
                        }
                     }
                     break;
                  }

                  if (Character.isLetterOrDigit(c) || " []()<>.&%+-_,'".contains(String.valueOf(c))) {
                     ((TextSetting)setting).setValue(value + c);
                  }
               }
            }
         }
      }

   }

   public void onClick(int mouseX, int mouseY, int button) {
      ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
      float boxWidth;
      float boxHeight;
      float minX;
      float minY;
      float x;
      switch(this.menuType) {
      case MOD:
         boxWidth = 450.0F;
         boxHeight = 200.0F;
         minX = (float)(resolution.getScaledWidth() / 2) - boxWidth / 2.0F;
         minY = (float)(resolution.getScaledHeight() / 2) - boxHeight / 2.0F;
         float nameWidth = (float)Client.getInstance().getLargeBoldFontRenderer().getStringWidth(this.editing.getName().toUpperCase());
         float x = minX + boxWidth / 2.0F - nameWidth / 2.0F - 25.0F;
         float y = minY + 12.0F;
         if ((float)mouseX >= x && (float)mouseX <= x + 16.0F && (float)mouseY >= y && (float)mouseY <= y + 16.0F) {
            this.menuType = MenuComponent.EnumMenuType.MODS;
            this.scrollAmount = 0;
            this.switchTime = 450;
            this.editing.getSettingMap().values().forEach((settingx) -> {
               if (settingx instanceof ColorSetting) {
                  ColorSetting colorSetting = (ColorSetting)settingx;
                  colorSetting.setPicking(false);
               }

            });
         }

         List settings = this.editing.getSortedSettings();
         float currentMaxY = minY + (float)settings.size() * 50.0F + (float)(settings.size() - 1) * 10.0F;
         if (currentMaxY > minY + boxHeight) {
            x = (float)this.scrollAmount / 10.0F;
            minY += x;
         } else {
            this.scrollAmount = 0;
         }

         minX += 25.0F;
         boolean switchedSelector = false;
         boolean switchedBoolean = false;
         float startY = minY + 40.0F;

         for(int i = 0; i < settings.size(); ++i) {
            ISetting setting = (ISetting)settings.get(i);
            if (!setting.getName().equals("Enabled")) {
               boolean down = true;
               float itemStart = 120.0F;
               float spectrumX;
               float spectrumXEnd;
               if (setting instanceof FloatSetting) {
                  switchedSelector = false;
                  switchedBoolean = false;
                  if ((float)mouseX >= minX + itemStart && (float)mouseX <= minX + boxWidth - 50.0F && (float)mouseY >= startY + 4.0F && (float)mouseY <= startY + 12.5F) {
                     this.draggingSetting = (FloatSetting)setting;
                     spectrumX = minX + boxWidth - 50.0F - (minX + itemStart);
                     spectrumXEnd = ((float)mouseX - (minX + itemStart)) / spectrumX * this.draggingSetting.getMax().floatValue();
                     this.draggingSetting.setValue(spectrumXEnd);
                  }
               } else if (setting instanceof LabelSetting) {
                  switchedSelector = false;
                  switchedBoolean = false;
               } else if (setting instanceof StringSetting) {
                  switchedBoolean = false;
                  x = minX;
                  if (!(switchedSelector = !switchedSelector)) {
                     x = minX + boxWidth - 100.0F - itemStart;
                  } else if (i < settings.size() - 1 && ((ISetting)settings.get(i + 1)).getClass() == setting.getClass()) {
                     down = false;
                  }

                  int index = ((StringSetting)setting).getIndex();
                  if ((float)mouseX >= x + itemStart - 25.0F && (float)mouseX <= x + itemStart - 15.0F && (float)mouseY >= startY + 3.0F && (float)mouseY <= startY + 13.0F) {
                     --index;
                  }

                  if ((float)mouseX >= x + itemStart + 45.0F && (float)mouseX <= x + itemStart + 55.0F && (float)mouseY >= startY + 3.0F && (float)mouseY <= startY + 13.0F) {
                     ++index;
                  }

                  int max = ((StringSetting)setting).getOptions().length;
                  if (index < 0) {
                     index = max - 1;
                  } else if (index >= max) {
                     index = 0;
                  }

                  ((StringSetting)setting).setValue(index);
               } else if (setting instanceof BooleanSetting) {
                  switchedSelector = false;
                  x = minX;
                  if (!(switchedBoolean = !switchedBoolean)) {
                     x = minX + boxWidth - 100.0F - itemStart;
                  } else if (i < settings.size() - 1 && ((ISetting)settings.get(i + 1)).getClass() == setting.getClass()) {
                     down = false;
                  }

                  if ((float)mouseX >= x + itemStart + 8.0F && (float)mouseX <= x + itemStart + 23.0F && (float)mouseY >= startY + 1.0F && (float)mouseY <= startY + 16.0F) {
                     ((BooleanSetting)setting).setValue(!((Boolean)setting.getValue()).booleanValue());
                  }
               } else if (setting instanceof ColorSetting) {
                  switchedSelector = false;
                  switchedBoolean = false;
                  if ((float)mouseX >= minX + itemStart + 10.0F && (float)mouseX <= minX + itemStart + 20.0F && (float)mouseY >= startY + 4.0F && (float)mouseY <= startY + 14.0F) {
                     ((ColorSetting)setting).setPicking(!((ColorSetting)setting).isPicking());
                  }

                  if (!setting.getName().equals("Background")) {
                     spectrumX = minX + boxWidth - 100.0F - itemStart;
                     if ((float)mouseX >= spectrumX + itemStart + 10.0F && (float)mouseX <= spectrumX + itemStart + 20.0F && (float)mouseY >= startY + 4.0F && (float)mouseY <= startY + 14.0F) {
                        ((ColorSetting)setting).setChroma(!((ColorSetting)setting).isChroma());
                     }
                  }

                  if (((ColorSetting)setting).isPicking()) {
                     itemStart -= 45.0F;
                     startY += 15.0F;
                     spectrumX = minX + itemStart + 56.0F;
                     spectrumXEnd = minX + itemStart + 176.0F;
                     float spectrumY = startY + 5.0F;
                     float spectrumYEnd = startY + 119.0F;
                     float spectrumWidth = spectrumXEnd - spectrumX;
                     float spectrumHeight = spectrumYEnd - spectrumY;
                     if ((float)mouseX >= spectrumX && (float)mouseX <= spectrumXEnd && (float)mouseY >= spectrumY && (float)mouseY <= spectrumYEnd) {
                        ((ColorSetting)setting).setDraggingAll(true);
                     }

                     if ((float)mouseX >= spectrumX + spectrumWidth + 4.0F && (float)mouseX <= spectrumX + spectrumWidth + 14.0F && (float)mouseY >= spectrumY - 1.0F && (float)mouseY <= spectrumY + spectrumHeight + 1.0F) {
                        ((ColorSetting)setting).setDraggingHue(true);
                     }

                     if ((float)mouseX >= spectrumX + spectrumWidth + 18.0F && (float)mouseX <= spectrumX + spectrumWidth + 28.0F && (float)mouseY >= spectrumY - 1.0F && (float)mouseY <= spectrumY + spectrumHeight + 1.0F) {
                        ((ColorSetting)setting).setDraggingAlpha(true);
                     }

                     startY += 105.0F;
                  }
               } else if (setting instanceof TextSetting) {
                  if ((float)mouseX >= minX + itemStart && (float)mouseX <= minX + boxWidth - 50.0F && (float)mouseY >= startY && (float)mouseY <= startY + 15.0F) {
                     ((TextSetting)setting).setEditing(true);
                  } else {
                     ((TextSetting)setting).setEditing(false);
                  }
               }

               if (down) {
                  startY += 25.0F;
               }
            }
         }

         return;
      case MODS:
         boxWidth = 450.0F;
         boxHeight = 210.0F;
         minX = (float)(resolution.getScaledWidth() / 2) - boxWidth / 2.0F;
         minY = (float)(resolution.getScaledHeight() / 2) - boxHeight / 2.0F;
         List modules = new ArrayList(Client.getInstance().getModuleManager().getModules());
         modules.sort((m1, m2) -> {
            return m1.getName().compareToIgnoreCase(m2.getName());
         });
         int v = 0;
         int h = 0;
         Iterator var12 = modules.iterator();

         while(var12.hasNext()) {
            IModule module = (IModule)var12.next();
            x = minX + 110.0F * (float)h + 10.0F;
            float y = minY + 50.0F * (float)v + 10.0F;
            if ((float)mouseX >= x + 32.5F && (float)mouseX <= x + 46.5F && (float)mouseY >= y + 20.0F && (float)mouseY <= y + 34.0F) {
               module.setEnabled(!module.isEnabled());
               return;
            }

            if ((float)mouseX >= x + 57.5F && (float)mouseX <= x + 71.5F && (float)mouseY >= y + 20.0F && (float)mouseY <= y + 34.0F) {
               if (module.getSettingMap().size() > 1) {
                  this.menuType = MenuComponent.EnumMenuType.MOD;
                  this.scrollAmount = 0;
                  this.editing = module;
                  this.switchTime = 0;
               }

               return;
            }

            ++h;
            if (h == 4) {
               h = 0;
               ++v;
            }
         }
      }

   }

   public void onMouseEvent() {
      int scroll = Mouse.getEventDWheel();
      if (scroll != 0) {
         LinkedList sizes;
         sizes = new LinkedList();
         label68:
         switch(this.menuType) {
         case MOD:
            boolean switchBoolean = false;
            boolean switchString = false;
            List settings = new LinkedList();
            List sortedSettings = this.editing.getSortedSettings();
            int i = 0;

            while(true) {
               if (i >= sortedSettings.size()) {
                  break label68;
               }

               label75: {
                  ISetting setting = (ISetting)sortedSettings.get(i);
                  if (setting instanceof BooleanSetting) {
                     switchString = false;
                     if ((switchBoolean = !switchBoolean) && i < settings.size() - 1 && ((ISetting)settings.get(i + 1)).getClass() == setting.getClass()) {
                        break label75;
                     }
                  } else if (setting instanceof StringSetting) {
                     switchBoolean = false;
                     if ((switchString = !switchString) && i < settings.size() - 1 && ((ISetting)settings.get(i + 1)).getClass() == setting.getClass()) {
                        break label75;
                     }
                  }

                  settings.add(setting);
                  if (setting instanceof ColorSetting && ((ColorSetting)setting).isPicking()) {
                     sizes.add(145.0F);
                  } else {
                     sizes.add(25.0F);
                  }
               }

               ++i;
            }
         case MODS:
            int mods = Client.getInstance().getModuleManager().getModules().size() % 4;

            for(int i = 0; i < mods; ++i) {
               sizes.add(50.0F * (float)i + 10.0F);
            }
         }

         if (sizes.size() > 0) {
            this.scroll(sizes, scroll);
         }

      }
   }

   private void scroll(List sizes, int scroll) {
      int before = this.scrollAmount;
      this.scrollAmount += scroll;
      if (this.scrollAmount > 0) {
         this.scrollAmount = 0;
      }

      ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
      float boxHeight = 50.0F;
      float minY = (float)(resolution.getScaledHeight() / 2) - boxHeight / 2.0F + 40.0F;
      float maxY = minY + boxHeight - 20.0F;
      float translate = (float)this.scrollAmount / 10.0F;
      float startY = 60.0F + translate;
      boolean move = false;

      Float size;
      for(Iterator var11 = sizes.iterator(); var11.hasNext(); startY += size.floatValue()) {
         size = (Float)var11.next();
         if (startY + size.floatValue() >= maxY) {
            move = true;
            break;
         }
      }

      if (!move) {
         this.scrollAmount = before;
      }

   }

   private void renderModMenu(ScaledResolution resolution, int mouseX, int mouseY) {
      if (this.editing == null) {
         this.menuType = MenuComponent.EnumMenuType.MODS;
         this.scrollAmount = 0;
      } else {
         float boxWidth = 450.0F;
         float boxHeight = 200.0F;
         float minX = (float)(resolution.getScaledWidth() / 2) - boxWidth / 2.0F;
         float minY = (float)(resolution.getScaledHeight() / 2) - boxHeight / 2.0F;
         RenderUtil.drawRoundedRect((double)minX, (double)minY, (double)(minX + boxWidth), (double)(minY + boxHeight), 5.0D, 1997935379);
         RenderUtil.drawCenteredString(Client.getInstance().getLargeBoldFontRenderer(), this.editing.getName().toUpperCase(), minX + boxWidth / 2.0F, minY + 20.0F, -1, false);
         float nameWidth = (float)Client.getInstance().getLargeBoldFontRenderer().getStringWidth(this.editing.getName().toUpperCase());
         GL11.glPushMatrix();
         float alpha = 0.7F;
         float currentMaxY = minX + boxWidth / 2.0F - nameWidth / 2.0F - 25.0F;
         float translate = minY + 12.0F;
         if ((float)mouseX >= currentMaxY && (float)mouseX <= currentMaxY + 16.0F && (float)mouseY >= translate && (float)mouseY <= translate + 16.0F) {
            alpha = 1.0F;
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
         RenderUtil.drawSquareTexture(ARROW_LEFT, 8.0F, minX + boxWidth / 2.0F - nameWidth / 2.0F - 25.0F, minY + 12.0F);
         GL11.glPopMatrix();
         RenderUtil.startScissorBox(minY + 40.0F, minY + boxHeight - 20.0F, minX, minX + boxWidth);
         List settings = this.editing.getSortedSettings();
         GL11.glPushMatrix();
         currentMaxY = minY + (float)settings.size() * 50.0F + (float)(settings.size() - 1) * 10.0F;
         if (currentMaxY > minY + boxHeight) {
            translate = (float)this.scrollAmount / 10.0F;
            minY += translate;
         } else {
            this.scrollAmount = 0;
         }

         minX += 25.0F;
         boolean switchedSelector = false;
         boolean switchedBoolean = false;
         float startY = minY + 40.0F;

         for(int i = 0; i < settings.size(); ++i) {
            ISetting setting = (ISetting)settings.get(i);
            if (!setting.getName().equals("Enabled")) {
               boolean down = true;
               GL11.glPushMatrix();
               float itemStart = 120.0F;
               float x;
               float spectrumXEnd;
               float spectrumY;
               if (setting instanceof FloatSetting) {
                  switchedSelector = false;
                  switchedBoolean = false;
                  RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(), minX, startY + 6.0F, -855638017, false);
                  x = minX + boxWidth - 50.0F - (minX + itemStart);
                  FloatSetting floatSetting = (FloatSetting)setting;
                  if (setting == this.draggingSetting) {
                     spectrumXEnd = ((float)mouseX - (minX + itemStart)) / x * floatSetting.getMax().floatValue();
                     floatSetting.setValue(spectrumXEnd);
                  }

                  String value = String.format("%.1f", floatSetting.getValue());
                  RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), value, minX + itemStart - 5.0F - (float)Client.getInstance().getSmallFontRenderer().getStringWidth(value), startY + 6.0F, -1, false);
                  spectrumY = x / 100.0F * floatSetting.getValue().floatValue() / floatSetting.getMax().floatValue() * 100.0F;
                  RenderUtil.drawRect(minX + itemStart, startY + 4.0F, minX + boxWidth - 50.0F, startY + 12.5F, -1456275456);
                  RenderUtil.drawRect(minX + itemStart, startY + 4.0F, minX + itemStart + spectrumY, startY + 12.5F, -1444007117);
               } else if (setting instanceof LabelSetting) {
                  switchedSelector = false;
                  switchedBoolean = false;
                  RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(), minX - 5.0F, startY + 6.0F, -855638017, false);
                  RenderUtil.drawRect(minX - 5.0F, startY + 14.5F, minX + boxWidth - 45.0F, startY + 15.5F, -1999909941);
               } else if (setting instanceof StringSetting) {
                  switchedBoolean = false;
                  x = minX;
                  if (!(switchedSelector = !switchedSelector)) {
                     x = minX + boxWidth - 100.0F - itemStart;
                  } else if (i < settings.size() - 1 && ((ISetting)settings.get(i + 1)).getClass() == setting.getClass()) {
                     down = false;
                  }

                  RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(), x, startY + 6.0F, -855638017, false);
                  RenderUtil.drawCenteredString(Client.getInstance().getSmallFontRenderer(), (String)setting.getValue(), x + itemStart + 15.0F, startY + 6.0F + (float)(Client.getInstance().getSmallFontRenderer().getHeight() / 2), -855638017, false);
                  GL11.glPushMatrix();
                  if ((float)mouseX >= x + itemStart + 45.0F && (float)mouseX <= x + itemStart + 55.0F && (float)mouseY >= startY + 3.0F && (float)mouseY <= startY + 13.0F) {
                     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  } else {
                     GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
                  }

                  RenderUtil.drawSquareTexture(ARROW_RIGHT, 5.0F, x + itemStart + 45.0F, startY + 3.5F);
                  if ((float)mouseX >= x + itemStart - 25.0F && (float)mouseX <= x + itemStart - 15.0F && (float)mouseY >= startY + 3.0F && (float)mouseY <= startY + 13.0F) {
                     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  } else {
                     GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
                  }

                  RenderUtil.drawSquareTexture(ARROW_LEFT, 5.0F, x + itemStart - 25.0F, startY + 3.5F);
                  GL11.glPopMatrix();
               } else {
                  float spectrumX;
                  if (setting instanceof BooleanSetting) {
                     switchedSelector = false;
                     x = minX;
                     if (!(switchedBoolean = !switchedBoolean)) {
                        x = minX + boxWidth - 100.0F - itemStart;
                     } else if (i < settings.size() - 1 && ((ISetting)settings.get(i + 1)).getClass() == setting.getClass()) {
                        down = false;
                     }

                     RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(), x, startY + 6.0F, -855638017, false);
                     spectrumX = 0.6F;
                     if ((float)mouseX >= x + itemStart + 8.0F && (float)mouseX <= x + itemStart + 23.0F && (float)mouseY >= startY + 1.0F && (float)mouseY <= startY + 16.0F) {
                        spectrumX = 0.8F;
                     }

                     GL11.glEnable(3042);
                     if (((Boolean)setting.getValue()).booleanValue()) {
                        GL11.glColor4f(0.0F, 1.0F, 0.0F, spectrumX);
                        RenderUtil.drawSquareTexture(TOGGLE_ON, 7.5F, x + itemStart + 8.0F, startY + 1.0F);
                     } else {
                        GL11.glColor4f(1.0F, 0.0F, 0.0F, spectrumX);
                        RenderUtil.drawSquareTexture(TOGGLE_OFF, 7.5F, x + itemStart + 8.0F, startY + 1.0F);
                     }

                     GL11.glDisable(3042);
                  } else if (setting instanceof ColorSetting) {
                     switchedSelector = false;
                     switchedBoolean = false;
                     RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(), minX, startY + 7.0F, -855638017, false);
                     RenderUtil.drawBorderedRect(minX + itemStart + 10.0F, startY + 4.0F, minX + itemStart + 20.0F, startY + 14.0F, 1.0F, -1, ((Integer)setting.getValue()).intValue());
                     if (!setting.getName().equals("Background")) {
                        x = minX + boxWidth - 100.0F - itemStart;
                        RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), "Chroma", x, startY + 7.0F, -855638017, false);
                        RenderUtil.drawBorderedRect(x + itemStart + 10.0F, startY + 4.0F, x + itemStart + 20.0F, startY + 14.0F, 1.0F, -1, ((ColorSetting)setting).isChroma() ? -16777216 : -1);
                     }

                     RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), "#" + Integer.toHexString(((Integer)setting.getValue()).intValue()).toUpperCase(), minX + itemStart + 25.0F, startY + 7.0F, -855638017, false);
                     if (((ColorSetting)setting).isPicking()) {
                        itemStart -= 45.0F;
                        startY += 15.0F;
                        ColorSetting colorSetting = (ColorSetting)setting;
                        spectrumX = minX + itemStart + 56.0F;
                        spectrumXEnd = minX + itemStart + 176.0F;
                        spectrumY = startY + 5.0F;
                        float spectrumYEnd = startY + 119.0F;
                        float spectrumWidth = spectrumXEnd - spectrumX;
                        float spectrumHeight = spectrumYEnd - spectrumY;
                        RenderUtil.drawRect(minX + itemStart + 55.0F, startY + 4.0F, minX + itemStart + 177.0F, startY + 120.0F, -822083584);
                        Tessellator tess = Tessellator.instance;
                        GL11.glDisable(3553);
                        tess.startDrawingQuads();
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        tess.addVertex((double)spectrumX, (double)spectrumYEnd, 0.0D);
                        tess.addVertex((double)spectrumXEnd, (double)spectrumYEnd, 0.0D);
                        tess.addVertex((double)spectrumXEnd, (double)spectrumY, 0.0D);
                        tess.addVertex((double)spectrumX, (double)spectrumY, 0.0D);
                        tess.draw();
                        int[] colorPos = null;

                        int x;
                        int y;
                        float startAlphaY;
                        int rgb;
                        for(x = 0; (float)x < spectrumWidth; ++x) {
                           for(y = 0; (float)y < spectrumHeight; ++y) {
                              startAlphaY = (float)x / spectrumWidth;
                              float brightness = 1.0F - (float)y / spectrumHeight;
                              rgb = (int)colorSetting.getAlpha() << 24 | Color.HSBtoRGB(colorSetting.getHue(), startAlphaY, brightness);
                              boolean mouseXOver = (float)mouseX >= spectrumX + (float)x && (float)mouseX <= spectrumX + (float)x + 1.0F;
                              boolean mouseYOver = (float)mouseY <= spectrumY + (float)y + 1.0F && (float)mouseY > spectrumY + (float)y;
                              boolean overPosition = mouseXOver && mouseYOver;
                              boolean xTooSmall = x == 0 && (float)mouseX < spectrumX && mouseYOver;
                              boolean yTooSmall = y == 0 && (float)mouseY < spectrumY && mouseXOver;
                              boolean xTooBig = (float)x == spectrumWidth - 1.0F && (float)mouseX > spectrumX + spectrumWidth && mouseYOver;
                              boolean yTooBig = (float)y == spectrumHeight - 1.0F && (float)mouseY > spectrumY + spectrumHeight && mouseXOver;
                              if (colorSetting.isDraggingAll() && (overPosition || xTooSmall || yTooSmall || xTooBig || yTooBig)) {
                                 colorSetting.setValue(rgb);
                                 colorSetting.setPickerLocation(new int[]{x, y});
                              }

                              if (colorSetting.getPickerLocation() != null) {
                                 colorPos = colorSetting.getPickerLocation();
                              } else if (rgb == ((Integer)setting.getValue()).intValue()) {
                                 colorPos = new int[]{x, y};
                              }

                              tess.startDrawingQuads();
                              GL11.glColor4f((float)(rgb >> 16 & 255) / 255.0F, (float)(rgb >> 8 & 255) / 255.0F, (float)(rgb & 255) / 255.0F, 1.0F);
                              tess.addVertex((double)(spectrumX + (float)x), (double)(spectrumY + (float)y + 1.0F), 0.0D);
                              tess.addVertex((double)(spectrumX + (float)x + 1.0F), (double)(spectrumY + (float)y + 1.0F), 0.0D);
                              tess.addVertex((double)(spectrumX + (float)x + 1.0F), (double)(spectrumY + (float)y), 0.0D);
                              tess.addVertex((double)(spectrumX + (float)x), (double)(spectrumY + (float)y), 0.0D);
                              tess.draw();
                           }
                        }

                        if (colorPos != null) {
                           GL11.glPushMatrix();
                           GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.75F);
                           RenderUtil.drawCircle((double)(spectrumX + (float)colorPos[0] + 1.115F), (double)(spectrumY + (float)colorPos[1] + 1.115F), 4.0D);
                           GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                           RenderUtil.drawCircle((double)(spectrumX + (float)colorPos[0] + 1.115F), (double)(spectrumY + (float)colorPos[1] + 1.115F), 2.700000047683716D);
                           GL11.glPopMatrix();
                        }

                        RenderUtil.drawRect(spectrumX + spectrumWidth + 4.0F, spectrumY - 1.0F, spectrumX + spectrumWidth + 14.0F, spectrumY + 1.0F + spectrumHeight, -822083584);

                        int j;
                        for(x = 0; (float)x < spectrumHeight; ++x) {
                           y = Color.HSBtoRGB((float)x / spectrumHeight, 1.0F, 1.0F);
                           RenderUtil.drawRect(spectrumX + spectrumWidth + 5.0F, spectrumY + (float)x, spectrumX + spectrumWidth + 13.0F, spectrumY + (float)x + 1.0F, y);
                           if (colorSetting.isDraggingHue() && (float)mouseY >= spectrumY + (float)x && (float)mouseY <= spectrumY + (float)x + 1.0F) {
                              j = colorSetting.getValue().intValue();
                              float[] hsb = Color.RGBtoHSB(j >> 16 & 255, j >> 8 & 255, j & 255, (float[])null);
                              colorSetting.setValue(Color.HSBtoRGB(colorSetting.getHue(), hsb[1], hsb[2]));
                              colorSetting.setHue((float)x / spectrumHeight);
                           }
                        }

                        float startHueY = -1.0F + spectrumHeight * ((ColorSetting)setting).getHue();
                        RenderUtil.drawRect(spectrumX + spectrumWidth + 4.0F, spectrumY + startHueY, spectrumX + spectrumWidth + 14.0F, spectrumY + startHueY + 3.0F, -822083584);
                        RenderUtil.drawRect(spectrumX + spectrumWidth + 4.0F, spectrumY + startHueY + 1.0F, spectrumX + spectrumWidth + 14.0F, spectrumY + startHueY + 2.0F, -805306369);
                        RenderUtil.drawRect(spectrumX + spectrumWidth + 18.0F, spectrumY - 1.0F, spectrumX + spectrumWidth + 28.0F, spectrumY + 1.0F + spectrumHeight, -822083584);
                        boolean left = true;

                        for(j = 2; (float)j < spectrumHeight; j += 4) {
                           if (!left) {
                              RenderUtil.drawRect(spectrumX + spectrumWidth + 19.0F, spectrumY + (float)j, spectrumX + spectrumWidth + 23.0F, spectrumY + (float)j + 4.0F, -1);
                              RenderUtil.drawRect(spectrumX + spectrumWidth + 23.0F, spectrumY + (float)j, spectrumX + spectrumWidth + 27.0F, spectrumY + (float)j + 4.0F, -7303024);
                              if ((float)j < spectrumHeight - 4.0F) {
                                 RenderUtil.drawRect(spectrumX + spectrumWidth + 19.0F, spectrumY + (float)j + 4.0F, spectrumX + spectrumWidth + 23.0F, spectrumY + (float)j + 8.0F, -7303024);
                                 RenderUtil.drawRect(spectrumX + spectrumWidth + 23.0F, spectrumY + (float)j + 4.0F, spectrumX + spectrumWidth + 27.0F, spectrumY + (float)j + 8.0F, -1);
                              }
                           }

                           left = !left;
                        }

                        for(j = 0; (float)j < spectrumHeight; ++j) {
                           int c = ((Integer)setting.getValue()).intValue();
                           rgb = (new Color(c >> 16 & 255, c >> 8 & 255, c & 255, Math.round(255.0F - (float)j / spectrumHeight * 255.0F))).getRGB();
                           if (colorSetting.isDraggingAlpha() && (float)mouseY >= spectrumY + (float)j && (float)mouseY <= spectrumY + (float)j + 1.0F) {
                              colorSetting.setAlpha((float)j / spectrumHeight);
                              colorSetting.setValue(rgb);
                           }

                           RenderUtil.drawRect(spectrumX + spectrumWidth + 19.0F, spectrumY + (float)j, spectrumX + spectrumWidth + 27.0F, spectrumY + (float)j + 1.0F, rgb);
                        }

                        startAlphaY = -1.0F + spectrumHeight * ((ColorSetting)setting).getAlpha();
                        RenderUtil.drawRect(spectrumX + spectrumWidth + 18.0F, spectrumY + startAlphaY, spectrumX + spectrumWidth + 28.0F, spectrumY + startAlphaY + 3.0F, -822083584);
                        RenderUtil.drawRect(spectrumX + spectrumWidth + 18.0F, spectrumY + startAlphaY + 1.0F, spectrumX + spectrumWidth + 28.0F, spectrumY + startAlphaY + 2.0F, -805306369);
                        startY += 105.0F;
                     }
                  } else if (setting instanceof TextSetting) {
                     switchedSelector = false;
                     switchedBoolean = false;
                     RenderUtil.drawString(Client.getInstance().getSmallFontRenderer(), setting.getName(), minX, startY + 6.0F, -855638017, false);
                     RenderUtil.drawRect(minX + itemStart, startY, minX + boxWidth - 50.0F, startY + 15.0F, -1724697805);
                     String value = (String)setting.getValue();
                     if (((TextSetting)setting).isEditing()) {
                        if (((TextSetting)setting).getValueFlipTime() + 250L < System.currentTimeMillis()) {
                           ((TextSetting)setting).setValued(!((TextSetting)setting).isValued());
                           ((TextSetting)setting).setValueFlipTime(System.currentTimeMillis());
                        }

                        if (((TextSetting)setting).isValued()) {
                           value = value + "_";
                        }
                     }

                     RenderUtil.drawString(Client.getInstance().getRegularFontRenderer(), value, minX + itemStart + 2.5F, startY + 4.0F, -905969665, false);
                  }
               }

               GL11.glPopMatrix();
               if (down) {
                  startY += 25.0F;
               }
            }
         }

         GL11.glPopMatrix();
         RenderUtil.endScissorBox();
      }
   }

   private void renderMainMenu(ScaledResolution resolution, int mouseX, int mouseY) {
      float boxWidth = 450.0F;
      float boxHeight = 210.0F;
      float minX = (float)(resolution.getScaledWidth() / 2) - boxWidth / 2.0F;
      float minY = (float)(resolution.getScaledHeight() / 2) - boxHeight / 2.0F;
      RenderUtil.drawRoundedRect((double)minX, (double)minY, (double)(minX + boxWidth), (double)(minY + boxHeight), 5.0D, 1997935379);
      RenderUtil.startScissorBox(minY, minY + boxHeight, minX, minX + boxWidth);
      GL11.glPushMatrix();
      List modules = new ArrayList(Client.getInstance().getModuleManager().getModules());
      modules.sort((m1, m2) -> {
         return m1.getName().compareToIgnoreCase(m2.getName());
      });
      int v = 0;
      int h = 0;
      Iterator var11 = modules.iterator();

      while(var11.hasNext()) {
         IModule module = (IModule)var11.next();
         float x = minX + 110.0F * (float)h + 10.0F;
         float y = minY + 50.0F * (float)v + 10.0F;
         RenderUtil.drawBorderedRoundedRect(x, y, x + 100.0F, y + 40.0F, 2.5F, 1.0F, -7788247, -13882324);
         RenderUtil.drawCenteredString(Client.getInstance().getRegularMediumBoldFontRenderer(), module.getName().toUpperCase(), x + 50.0F, y + 10.0F, -1);
         float alpha = 0.6F;
         if ((float)mouseX >= x + 32.5F && (float)mouseX <= x + 32.5F + 15.0F && (float)mouseY >= y + 20.0F && (float)mouseY <= y + 20.0F + 15.0F) {
            alpha = 0.8F;
         }

         GL11.glPushMatrix();
         GL11.glEnable(3042);
         if (module.isEnabled()) {
            GL11.glColor4f(0.0F, 1.0F, 0.0F, alpha);
            RenderUtil.drawSquareTexture(TOGGLE_ON, 7.5F, x + 32.5F, y + 20.0F);
         } else {
            GL11.glColor4f(1.0F, 0.0F, 0.0F, alpha);
            RenderUtil.drawSquareTexture(TOGGLE_OFF, 7.5F, x + 32.5F, y + 20.0F);
         }

         if ((float)mouseX >= x + 57.5F && (float)mouseX <= x + 71.5F && (float)mouseY >= y + 20.0F && (float)mouseY <= y + 34.0F) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
         } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
         }

         RenderUtil.drawSquareTexture(SETTINGS, 7.5F, x + 57.5F, y + 20.0F);
         GL11.glDisable(3042);
         GL11.glPopMatrix();
         ++h;
         if (h == 4) {
            h = 0;
            ++v;
         }
      }

      GL11.glPopMatrix();
      RenderUtil.endScissorBox();
   }

   public void draw(int mouseX, int mouseY) {
      Minecraft mc = Minecraft.getMinecraft();
      ScaledResolution resolution = new ScaledResolution(mc);
      GL11.glPushMatrix();
      boolean wasHiding = this.hiding;
      this.hiding = (float)mouseY <= 20.0F;
      if (this.hiding) {
         if (!wasHiding) {
            this.hidingTicks = 0;
         }

         GL11.glTranslatef(0.0F, (float)(-this.hidingTicks) * 2.0F, 0.0F);
      }

      RenderUtil.drawRect(0.0F, 0.0F, (float)resolution.getScaledWidth(), 23.333334F, -291356865);
      RenderUtil.drawString(Client.getInstance().getMediumBoldFontRenderer(), "ZONIX CLIENT", 10.0F, 6.6666665F, -1, true);
      RenderUtil.drawCenteredString(Client.getInstance().getMediumBoldFontRenderer(), "MOD SETTINGS", (float)(resolution.getScaledWidth() / 2), 11.666667F, -1, true);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      float boxWidth = 450.0F;
      float boxHeight = 220.0F;
      float minX = (float)(resolution.getScaledWidth() / 2) - boxWidth / 2.0F;
      float minY = (float)(resolution.getScaledHeight() / 2) - boxHeight / 2.0F;
      GL11.glPushMatrix();
      RenderUtil.startScissorBox(minY, minY + boxHeight, minX, minX + boxWidth);
      GL11.glTranslatef((float)(-this.switchTime) - 25.0F, 0.0F, 0.0F);
      this.renderMainMenu(resolution, mouseX, mouseY);
      RenderUtil.endScissorBox();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      RenderUtil.startScissorBox(minY, minY + boxHeight, minX, minX + boxWidth);
      GL11.glTranslatef((float)(450 - this.switchTime) + 25.0F, 0.0F, 0.0F);
      this.renderModMenu(resolution, mouseX, mouseY);
      RenderUtil.endScissorBox();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }

   private static String toHex(Color color) {
      return String.format("#%02x%02x%02x%02x", color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
   }

   public MenuComponent.EnumMenuType getMenuType() {
      return this.menuType;
   }

   public void setMenuType(MenuComponent.EnumMenuType menuType) {
      this.menuType = menuType;
   }

   public void setDraggingSetting(FloatSetting draggingSetting) {
      this.draggingSetting = draggingSetting;
   }

   public void setEditing(IModule editing) {
      this.editing = editing;
   }

   public void setSwitchTime(int switchTime) {
      this.switchTime = switchTime;
   }

   public void setScrollAmount(int scrollAmount) {
      this.scrollAmount = scrollAmount;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int y) {
      this.y = y;
   }

   public static enum EnumMenuType {
      MOD,
      MODS;
   }
}
