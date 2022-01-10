package us.zonix.client.module.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.setting.impl.TextSetting;
import us.zonix.client.util.RenderUtil;

public final class ToggleSneak extends AbstractModule {
   private final TextSetting vanillaSprintText = new TextSetting("Vanilla Sprint Text", "[Sprinting (Vanilla)]");
   private final TextSetting toggleSprintText = new TextSetting("Toggle Sprint Text", "[Sprinting (Toggled)]");
   private final TextSetting toggleSneakText = new TextSetting("Toggle Sneak Text", "[Sneaking (Toggled)]");
   private final TextSetting heldSprintText = new TextSetting("Held Sprint Text", "[Sprinting (Key Held)]");
   private final TextSetting heldSneakText = new TextSetting("Held Sneak Text", "[Sneaking (Key Held)]");
   private final TextSetting flyBoostText = new TextSetting("Fly Boost Text", "[Flying (10x boost)]");
   private final TextSetting dismountingText = new TextSetting("Dismounting Text", "[Dismounting]");
   private final TextSetting descendingText = new TextSetting("Descending Text", "[Descending]");
   private final TextSetting ridingText = new TextSetting("Riding Text", "[Riding]");
   private final TextSetting flyText = new TextSetting("Fly Text", "[Flying]");
   private static final ColorSetting FOREGROUND = new ColorSetting("Foreground", -65536);
   private static final ColorSetting BACKGROUND = new ColorSetting("Background", 1862270976);
   private String hudText;
   private boolean sprintHeldAndReleased;
   private boolean sprintPressTicked;
   private boolean sprinting;
   private boolean inventoryPress;
   private boolean sneakPressTicked;
   private boolean wasRiding;
   private boolean sneaking;
   private long lastSprintPressed;
   private long lastSneakPressed;

   public ToggleSneak() {
      super("ToggleSneak");
      this.y = 4.0F;
      this.addSetting(new LabelSetting("General Settings"));
      this.addSetting(new BooleanSetting("Hide from HUD", false));
      this.addSetting(new BooleanSetting("Draw Background", false));
      this.addSetting(new BooleanSetting("Toggle Sprint", true));
      this.addSetting(new BooleanSetting("Toggle Sneak", true));
      this.addSetting(new LabelSetting("Color Settings"));
      this.addSetting(FOREGROUND);
      this.addSetting(BACKGROUND);
      this.addSetting(new LabelSetting("Text Settings"));
      this.addSetting(this.toggleSprintText);
      this.addSetting(this.toggleSneakText);
      this.addSetting(this.heldSprintText);
      this.addSetting(this.heldSneakText);
      this.addSetting(this.vanillaSprintText);
      this.addSetting(this.flyBoostText);
      this.addSetting(this.flyText);
      this.addSetting(this.ridingText);
      this.addSetting(this.descendingText);
      this.addSetting(this.dismountingText);
   }

   public void renderPreview() {
      if (this.hudText != null) {
         this.renderReal();
      } else {
         this.hudText = this.toggleSprintText.getValue().replace("&", "§");
         if (this.getBooleanSetting("Draw Background").getValue().booleanValue()) {
            this.hudText = this.hudText.replace("[", "").replace("]", "");
         }

         this.setWidth(this.mc.fontRenderer.getStringWidth(this.hudText) + 3);
         this.setHeight(this.mc.fontRenderer.FONT_HEIGHT + 3);
         if (this.getBooleanSetting("Draw Background").getValue().booleanValue()) {
            this.setHeight(this.getHeight() + 2);
            this.setWidth(this.getWidth() + 2);
         }

         this.renderReal();
         this.hudText = null;
      }
   }

   public void renderReal() {
      if (!this.getBooleanSetting("Hide from HUD").getValue().booleanValue()) {
         if (this.hudText != null) {
            FontRenderer fontRenderer = this.mc.fontRenderer;
            if (this.getBooleanSetting("Draw Background").getValue().booleanValue()) {
               RenderUtil.drawRect(this.x, this.y, this.x + (float)this.getWidth(), this.y + (float)this.getHeight(), BACKGROUND.getValue().intValue());
               fontRenderer.drawString(this.hudText, this.x + 2.0F, this.y + 2.0F, this.getColorSetting("Foreground").getValue().intValue());
            } else {
               fontRenderer.drawStringWithShadow(this.hudText, this.getX() + 2.0F, this.getY() + 2.0F, this.getColorSetting("Foreground").getValue().intValue());
            }
         }
      }
   }

   private void setSneaking(boolean sneaking) {
      KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), sneaking);
      this.sneaking = sneaking;
   }

   public void onPrePlayerUpdate() {
      KeyBinding keySprint = this.mc.gameSettings.keyBindSprint;
      KeyBinding keySneak = this.mc.gameSettings.keyBindSneak;
      if (this.getBooleanSetting("Toggle Sneak").getValue().booleanValue() && !this.mc.thePlayer.capabilities.isFlying && this.mc.inGameHasFocus) {
         if (Keyboard.isKeyDown(keySneak.getKeyCode()) && !this.sneakPressTicked) {
            this.lastSneakPressed = System.currentTimeMillis();
            this.sneakPressTicked = true;
         } else if (!Keyboard.isKeyDown(keySneak.getKeyCode()) && this.sneakPressTicked) {
            if (System.currentTimeMillis() - this.lastSneakPressed < 300L) {
               this.setSneaking(this.sneaking = !this.sneaking);
            }

            this.sneakPressTicked = false;
         }
      }

      if (this.getBooleanSetting("Toggle Sprint").getValue().booleanValue() && !this.mc.thePlayer.capabilities.isFlying) {
         if (keySprint.getIsKeyPressed() && !this.sprintPressTicked) {
            this.lastSprintPressed = System.currentTimeMillis();
            this.sprintPressTicked = true;
         } else if (!keySprint.getIsKeyPressed() && this.sprintPressTicked) {
            if (System.currentTimeMillis() - this.lastSprintPressed < 300L) {
               this.sprinting = !this.sprinting;
            }

            this.sprintPressTicked = false;
         }
      }

      if (this.sneaking) {
         this.setSneaking(true);
      }

      if (this.sprinting) {
         this.mc.thePlayer.setSprinting(true);
      }

      this.hudText = null;
      if (this.mc.thePlayer.capabilities.isFlying) {
         if (keySprint.getIsKeyPressed()) {
            this.hudText = this.flyBoostText.getValue();
         } else if (keySneak.getIsKeyPressed()) {
            this.hudText = this.flyText.getValue() + " " + this.descendingText.getValue();
         } else {
            this.hudText = this.flyText.getValue();
         }
      } else if (this.mc.thePlayer.isRiding()) {
         this.hudText = this.ridingText.getValue();
      } else if (this.mc.thePlayer.isSneaking()) {
         if (this.sneaking) {
            this.hudText = this.toggleSneakText.getValue();
         } else {
            this.hudText = this.heldSneakText.getValue();
         }
      } else if (this.mc.thePlayer.isSprinting()) {
         if (this.sprinting) {
            this.hudText = this.toggleSprintText.getValue();
         } else if (keySprint.getIsKeyPressed()) {
            this.hudText = this.heldSprintText.getValue();
         } else {
            this.hudText = this.vanillaSprintText.getValue();
         }
      }

      if (this.hudText == null) {
         this.setHeight(0);
         this.setWidth(0);
      } else {
         this.setWidth(this.mc.fontRenderer.getStringWidth(this.hudText) + 3);
         this.setHeight(this.mc.fontRenderer.FONT_HEIGHT + 3);
      }

   }
}
