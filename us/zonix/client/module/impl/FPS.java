package us.zonix.client.module.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.util.RenderUtil;

public final class FPS extends AbstractModule {
   private static final BooleanSetting DRAW_BACKGROUND = new BooleanSetting("Draw background", true);

   public FPS() {
      super("FPS");
      this.x = 4.0F;
      this.y = 91.0F;
      this.addSetting(new LabelSetting("General Settings"));
      this.addSetting(DRAW_BACKGROUND);
      this.addSetting(new LabelSetting("Color Settings"));
      this.addSetting(new ColorSetting("Foreground", -65536));
      this.addSetting(new ColorSetting("Background", 1862270976));
   }

   public void renderReal() {
      int height = 13;
      int width = 70;
      this.setHeight(height);
      this.setWidth(width);
      FontRenderer fontRenderer = this.mc.fontRenderer;
      String fpsString = Minecraft.debugFPS + " FPS";
      if (DRAW_BACKGROUND.getValue().booleanValue()) {
         RenderUtil.drawRect(this.x, this.y, this.x + (float)width, this.y + (float)height, this.getColorSetting("Background").getValue().intValue());
      }

      fontRenderer.drawString(fpsString, this.x + (float)(width / 2 - fontRenderer.getStringWidth(fpsString) / 2), this.y + 3.0F, this.getColorSetting("Foreground").getValue().intValue());
   }
}
