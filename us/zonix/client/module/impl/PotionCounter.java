package us.zonix.client.module.impl;

import net.minecraft.item.ItemStack;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.setting.impl.StringSetting;
import us.zonix.client.util.RenderUtil;

public final class PotionCounter extends AbstractModule {
   private static final StringSetting DISPLAY_TYPE = new StringSetting("Display Type", new String[]{"0 Potions", "Potion: 0"});
   private static final BooleanSetting DRAW_BACKGROUND = new BooleanSetting("Draw background", true);
   private static final ColorSetting FOREGROUND = new ColorSetting("Foreground", -65536);
   private static final ColorSetting BACKGROUND = new ColorSetting("Background", 1862270976);
   private int pots;

   public PotionCounter() {
      super("Pot Counter");
      this.setEnabled(false);
      this.addSetting(new LabelSetting("General Settings"));
      this.addSetting(DRAW_BACKGROUND);
      this.addSetting(DISPLAY_TYPE);
      this.addSetting(new LabelSetting("Color Settings"));
      this.addSetting(FOREGROUND);
      this.addSetting(BACKGROUND);
   }

   public void onPostPlayerUpdate() {
      this.pots = 0;

      for(int i = 0; i < this.mc.thePlayer.inventory.mainInventory.length; ++i) {
         ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory[i];
         if (itemStack != null && itemStack.getUnlocalizedName().equals("item.potion") && (itemStack.getItemDamage() == 16421 || itemStack.getItemDamage() == 16453)) {
            this.pots += itemStack.stackSize;
         }
      }

   }

   public void renderReal() {
      String text = this.pots + " Potions";
      if (DISPLAY_TYPE.getIndex() == 1) {
         text = "Potions: " + this.pots;
      } else if (this.pots == 1) {
         text = "1 Potion";
      }

      float width = (float)(this.mc.fontRenderer.getStringWidth(text) + 4);
      if (width < 70.0F) {
         width = 70.0F;
      }

      this.setWidth((int)width);
      this.setHeight(13);
      if (DRAW_BACKGROUND.getValue().booleanValue()) {
         RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + (float)this.getWidth(), this.getY() + (float)this.getHeight(), BACKGROUND.getValue().intValue());
      }

      this.mc.fontRenderer.drawString(text, this.getX() + (float)(this.getWidth() / 2) - (float)(this.mc.fontRenderer.getStringWidth(text) / 2), this.y + 3.0F, FOREGROUND.getValue().intValue());
   }
}
