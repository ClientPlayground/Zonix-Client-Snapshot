package us.zonix.client.module.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.setting.impl.StringSetting;
import us.zonix.client.util.RenderUtil;

public final class Coordinates extends AbstractModule {
   private static final LabelSetting GENERAL_LABEL = new LabelSetting("General Settings");
   private static final BooleanSetting DRAW_BACKGROUND = new BooleanSetting("Draw background", true);
   private static final BooleanSetting SHOW_DECIMAL = new BooleanSetting("Show decimal", true);
   private static final StringSetting DISPLAY_TYPE = new StringSetting("Display Type", new String[]{"Zonix", "Compact"});
   private static final BooleanSetting SHOW_Y_COORD = new BooleanSetting("Show Y coordinate", true);
   private static final BooleanSetting SHOW_DIRECTION = new BooleanSetting("Show direction", true);
   private static final ColorSetting FOREGROUND = new ColorSetting("Foreground", -65536);
   private static final ColorSetting BACKGROUND = new ColorSetting("Background", 1862270976);

   public Coordinates() {
      super("Coordinates");
      this.x = 4.0F;
      this.y = 106.0F;
      this.addSetting(GENERAL_LABEL);
      this.addSetting(SHOW_Y_COORD);
      this.addSetting(SHOW_DIRECTION);
      this.addSetting(SHOW_DECIMAL);
      this.addSetting(DISPLAY_TYPE);
      this.addSetting(new LabelSetting("Color Settings"));
      this.addSetting(FOREGROUND);
      this.addSetting(BACKGROUND);
   }

   public void renderReal() {
      String[] rotations = new String[]{"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
      double angle = (double)MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYaw);
      angle = (angle + 202.5D) % 360.0D / 45.0D;
      String direction = rotations[MathHelper.floor_double(angle)];
      double x = this.mc.thePlayer.posX;
      double y = this.mc.thePlayer.posY;
      double z = this.mc.thePlayer.posZ;
      this.render(direction, x, y, z);
   }

   private void render(String direction, double x, double y, double z) {
      switch(DISPLAY_TYPE.getIndex()) {
      case 0:
         this.renderCompact(direction, x, y, z, DRAW_BACKGROUND.getValue().booleanValue());
         break;
      case 1:
         this.renderHorizontal(direction, x, y, z);
      }

   }

   private void renderHorizontal(String direction, double x, double y, double z) {
      String text = String.format("%.2f, %.2f, %.2f", x, y, z);
      if (!SHOW_DECIMAL.getValue().booleanValue()) {
         text = String.format("%d, %d, %d", (int)x, (int)y, (int)z);
         if (!SHOW_Y_COORD.getValue().booleanValue()) {
            text = String.format("%d, %d", (int)x, (int)z);
         }
      } else if (!SHOW_Y_COORD.getValue().booleanValue()) {
         text = String.format("%.2f, %.2f", x, z);
      }

      if (SHOW_DIRECTION.getValue().booleanValue()) {
         text = text + " - " + direction;
      }

      text = "[" + text + "]";
      this.setWidth(this.mc.fontRenderer.getStringWidth(text) + 4);
      this.setHeight(this.mc.fontRenderer.FONT_HEIGHT + 4);
      this.mc.fontRenderer.drawString(text, this.x + 3.0F, this.y + 3.0F, this.getColorSetting("Foreground").getValue().intValue());
   }

   private void renderCompact(String direction, double x, double y, double z, boolean background) {
      FontRenderer fontRenderer = this.mc.fontRenderer;
      String sX = String.format("X %.2f", x);
      String sY = String.format("Y %.2f", y);
      String sZ = String.format("Z %.2f", z);
      if (!SHOW_DECIMAL.getValue().booleanValue()) {
         sX = "X " + (int)x;
         sY = "Y " + (int)y;
         sZ = "Z " + (int)z;
      }

      int height = 12 + fontRenderer.FONT_HEIGHT * 2;
      if (this.getBooleanSetting("Show Y coordinate").getValue().booleanValue()) {
         height += fontRenderer.FONT_HEIGHT;
      }

      int width = 40;
      if (this.getBooleanSetting("Show direction").getValue().booleanValue()) {
         width = 70;
      }

      this.setHeight(height);
      this.setWidth(width);
      if (background) {
         RenderUtil.drawRect(this.x, this.y, this.x + (float)width, this.y + (float)height, this.getColorSetting("Background").getValue().intValue());
      }

      float yPos = this.y + 4.0F;
      fontRenderer.drawString(sX, this.x + 3.0F, yPos, this.getColorSetting("Foreground").getValue().intValue());
      yPos += (float)fontRenderer.FONT_HEIGHT;
      if (this.getBooleanSetting("Show Y coordinate").getValue().booleanValue()) {
         fontRenderer.drawString(sY, this.x + 3.0F, yPos + 3.0F, this.getColorSetting("Foreground").getValue().intValue());
         yPos += (float)fontRenderer.FONT_HEIGHT;
      }

      if (this.getBooleanSetting("Show direction").getValue().booleanValue()) {
         fontRenderer.drawString(direction, this.x + (float)width - 3.0F - (float)fontRenderer.getStringWidth(direction), yPos + 3.0F - (float)fontRenderer.FONT_HEIGHT, this.getColorSetting("Foreground").getValue().intValue());
      }

      fontRenderer.drawString(sZ, this.x + 3.0F, yPos + (float)(this.getBooleanSetting("Show Y coordinate").getValue().booleanValue() ? 6 : 3), this.getColorSetting("Foreground").getValue().intValue());
   }
}
