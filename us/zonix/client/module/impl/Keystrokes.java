package us.zonix.client.module.impl;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.util.RenderUtil;

public final class Keystrokes extends AbstractModule {
   private static final BooleanSetting DRAW_BACKGROUND = new BooleanSetting("Draw background", true);
   private static final ColorSetting FOREGROUND = new ColorSetting("Foreground", -65536);
   private static final ColorSetting BACKGROUND = new ColorSetting("Background", 1862270976);
   private final boolean[] buttonStates;
   private final double[] brightness;
   private final long[] buttonTimes;
   private final int[] buttonColors;

   public Keystrokes() {
      super("Keystrokes");
      this.x = this.y = 4.0F;
      this.buttonStates = new boolean[6];
      this.brightness = new double[6];
      this.buttonTimes = new long[6];
      this.buttonColors = new int[6];
      Arrays.fill(this.buttonTimes, System.currentTimeMillis());
      Arrays.fill(this.buttonStates, true);
      Arrays.fill(this.buttonColors, 255);
      Arrays.fill(this.brightness, 1.0D);
      this.setHeight(70);
      this.setWidth(70);
      this.addSetting(new LabelSetting("General Settings"));
      this.addSetting(new BooleanSetting("Show mouse buttons", true));
      this.addSetting(new BooleanSetting("Show key buttons", true));
      this.addSetting(DRAW_BACKGROUND);
      this.addSetting(new LabelSetting("Color Settings"));
      this.addSetting(FOREGROUND);
      this.addSetting(BACKGROUND);
   }

   public void renderReal() {
      int height = 0;
      if (this.getBooleanSetting("Show key buttons").getValue().booleanValue()) {
         Keystrokes.RenderKey forward = new Keystrokes.RenderKey(this.mc.gameSettings.keyBindForward, 26, 2);
         Keystrokes.RenderKey right = new Keystrokes.RenderKey(this.mc.gameSettings.keyBindRight, 50, 26);
         Keystrokes.RenderKey back = new Keystrokes.RenderKey(this.mc.gameSettings.keyBindBack, 26, 26);
         Keystrokes.RenderKey left = new Keystrokes.RenderKey(this.mc.gameSettings.keyBindLeft, 2, 26);
         Keystrokes.RenderKey[] keys = new Keystrokes.RenderKey[]{forward, back, left, right};
         this.renderKeys(keys);
         height += 46;
      }

      if (this.getBooleanSetting("Show mouse buttons").getValue().booleanValue()) {
         Keystrokes.RenderButton lmb = new Keystrokes.RenderButton("LMB", 0, 2, 50);
         Keystrokes.RenderButton rmb = new Keystrokes.RenderButton("RMB", 1, 38, 50);
         Keystrokes.RenderButton[] buttons = new Keystrokes.RenderButton[]{lmb, rmb};
         this.renderButtons(buttons);
         height += 24;
      }

      this.setHeight(height);
      this.setWidth(70);
   }

   private void renderButtons(Keystrokes.RenderButton[] buttons) {
      for(int i = 0; i < buttons.length; ++i) {
         Keystrokes.RenderButton button = buttons[i];
         boolean wasButtonDown = this.buttonStates[i + 4];
         boolean buttonDown = Mouse.isButtonDown(button.button);
         Mouse.poll();
         boolean polledButtonDown = Mouse.isButtonDown(button.button);
         if (polledButtonDown != wasButtonDown && buttonDown == polledButtonDown) {
            this.buttonTimes[i + 4] = System.currentTimeMillis();
            this.buttonStates[i + 4] = polledButtonDown;
         }

         if (polledButtonDown) {
            this.buttonColors[i + 4] = Math.min(255, (int)((System.currentTimeMillis() - this.buttonTimes[i + 4]) * 2L));
            this.brightness[i + 4] = Math.max(0.0D, 1.0D - (double)(System.currentTimeMillis() - this.buttonTimes[i + 4]) / 20.0D);
         } else {
            this.buttonColors[i + 4] = Math.max(0, (int)(255L - (System.currentTimeMillis() - this.buttonTimes[i + 4]) * 2L));
            this.brightness[i + 4] = Math.min(1.0D, (double)(System.currentTimeMillis() - this.buttonTimes[i + 4]) / 20.0D);
         }

         int color = this.buttonColors[i + 4];
         int y = button.y;
         if (!this.getBooleanSetting("Show key buttons").getValue().booleanValue()) {
            y -= 47;
         }

         if (DRAW_BACKGROUND.getValue().booleanValue()) {
            RenderUtil.drawRect(this.x + (float)button.x - 2.0F, this.y + (float)y - 2.0F, this.x + (float)button.x + 32.0F, this.y + (float)y + 20.0F, 2013265920 + (color << 16) + (color << 8) + color);
         }

         int currentColor = this.getColorSetting("Foreground").getValue().intValue();
         int green = currentColor >> 8 & 255;
         int red = currentColor >> 16 & 255;
         int blue = currentColor & 255;
         int renderColor = -16777216 + ((int)((double)red * this.brightness[i + 4]) << 16) + ((int)((double)green * this.brightness[i + 4]) << 8) + (int)((double)blue * this.brightness[i + 4]);
         FontRenderer fontRenderer = this.mc.fontRenderer;
         fontRenderer.drawString(button.name, this.x + (float)button.x + 6.0F, this.y + (float)y + 6.0F, renderColor);
      }

   }

   private void renderKeys(Keystrokes.RenderKey[] keys) {
      for(int i = 0; i < keys.length; ++i) {
         Keystrokes.RenderKey key = keys[i];
         KeyBinding keyBinding = key.key;
         String name = Keyboard.getKeyName(keyBinding.getKeyCode());
         boolean buttonDown = Keyboard.isKeyDown(keyBinding.getKeyCode());
         boolean wasButtonDown = this.buttonStates[i];
         Keyboard.poll();
         boolean polledButtonDown = Keyboard.isKeyDown(keyBinding.getKeyCode());
         if (polledButtonDown != wasButtonDown && buttonDown == polledButtonDown) {
            this.buttonTimes[i] = System.currentTimeMillis();
            this.buttonStates[i] = buttonDown;
         }

         if (buttonDown) {
            this.buttonColors[i] = Math.min(255, (int)((System.currentTimeMillis() - this.buttonTimes[i]) * 2L));
            this.brightness[i] = Math.max(0.0D, 1.0D - (double)(System.currentTimeMillis() - this.buttonTimes[i]) / 20.0D);
         } else {
            this.buttonColors[i] = Math.max(0, (int)(255L - (System.currentTimeMillis() - this.buttonTimes[i]) * 2L));
            this.brightness[i] = Math.min(1.0D, (double)(System.currentTimeMillis() - this.buttonTimes[i]) / 20.0D);
         }

         int color = this.buttonColors[i];
         if (DRAW_BACKGROUND.getValue().booleanValue()) {
            RenderUtil.drawRect(this.x + (float)key.x - 2.0F, this.y + (float)key.y - 2.0F, this.x + (float)key.x + 20.0F, this.y + (float)key.y + 20.0F, 2013265920 + (color << 16) + (color << 8) + color);
         }

         int currentColor = this.getColorSetting("Foreground").getValue().intValue();
         int green = currentColor >> 8 & 255;
         int red = currentColor >> 16 & 255;
         int blue = currentColor & 255;
         int renderColor = -16777216 + ((int)((double)red * this.brightness[i]) << 16) + ((int)((double)green * this.brightness[i]) << 8) + (int)((double)blue * this.brightness[i]);
         this.mc.fontRenderer.drawString(name, this.x + (float)key.x + 6.0F, this.y + (float)key.y + 6.0F, renderColor);
      }

   }

   private class RenderKey {
      private final KeyBinding key;
      private final int x;
      private final int y;

      @ConstructorProperties({"key", "x", "y"})
      public RenderKey(KeyBinding key, int x, int y) {
         this.key = key;
         this.x = x;
         this.y = y;
      }
   }

   private class RenderButton {
      private final String name;
      private final int button;
      private final int x;
      private final int y;

      @ConstructorProperties({"name", "button", "x", "y"})
      public RenderButton(String name, int button, int x, int y) {
         this.name = name;
         this.button = button;
         this.x = x;
         this.y = y;
      }
   }
}
