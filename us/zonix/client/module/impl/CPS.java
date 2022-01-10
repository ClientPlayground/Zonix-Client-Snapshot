package us.zonix.client.module.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.util.RenderUtil;

public final class CPS extends AbstractModule {
   private static final BooleanSetting DRAW_BACKGROUND = new BooleanSetting("Draw background", true);
   private static final ColorSetting FOREGROUND = new ColorSetting("Foreground", -65536);
   private static final ColorSetting BACKGROUND = new ColorSetting("Background", 1862270976);
   private final Set clicks = new HashSet();
   private boolean buttonDown;

   public CPS() {
      super("CPS");
      this.x = 4.0F;
      this.y = 76.0F;
      this.addSetting(new LabelSetting("General Settings"));
      this.addSetting(DRAW_BACKGROUND);
      this.addSetting(new LabelSetting("Color Settings"));
      this.addSetting(FOREGROUND);
      this.addSetting(BACKGROUND);
   }

   public void renderReal() {
      boolean buttonDown = Mouse.isButtonDown(0);
      Mouse.poll();
      boolean polledButtonDown = Mouse.isButtonDown(0);
      if (polledButtonDown && buttonDown && !this.buttonDown) {
         this.clicks.add(System.currentTimeMillis());
      }

      this.buttonDown = polledButtonDown;
      this.clicks.removeIf((l) -> {
         return l.longValue() + 1000L < System.currentTimeMillis();
      });
      FontRenderer fontRenderer = this.mc.fontRenderer;
      String fps = this.clicks.size() + " CPS";
      int width = 70;
      this.setWidth(width);
      int height = 13;
      this.setHeight(height);
      if (DRAW_BACKGROUND.getValue().booleanValue()) {
         RenderUtil.drawRect(this.x, this.y, this.x + (float)width, this.y + (float)height, this.getColorSetting("Background").getValue().intValue());
      }

      fontRenderer.drawString(fps, this.x + (float)(width / 2 - fontRenderer.getStringWidth(fps) / 2), this.y + 3.0F, this.getColorSetting("Foreground").getValue().intValue());
   }
}
