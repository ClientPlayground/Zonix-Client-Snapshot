package us.zonix.client.module.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.setting.impl.StringSetting;
import us.zonix.client.util.HUDElement;

public final class ArmorStatus extends AbstractModule {
   private static final LabelSetting GENERAL_LABEL = new LabelSetting("General Settings");
   public static final StringSetting DAMAGE_THRESHOLD_TYPE = new StringSetting("Damage Type", new String[]{"value", "percent"});
   public static final StringSetting DAMAGE_DISPLAY_TYPE = new StringSetting("Damage Display Type", new String[]{"value", "percent", "none"});
   private static final StringSetting LIST_MODE = new StringSetting("List Mode", new String[]{"vertical", "horizontal"});
   public static final BooleanSetting DAMAGE_OVERLAY = new BooleanSetting("Damage Overlay", true);
   public static final BooleanSetting ARMOR_DAMAGE = new BooleanSetting("Armor Damage", true);
   public static final BooleanSetting ITEM_DAMAGE = new BooleanSetting("Item Damage", true);
   public static final BooleanSetting MAX_DAMAGE = new BooleanSetting("Max Damage", false);
   public static final BooleanSetting ITEM_COUNT = new BooleanSetting("Item Count", true);
   public static final BooleanSetting ITEM_NAME = new BooleanSetting("Item Name", false);
   public static final BooleanSetting HELD_ITEM = new BooleanSetting("Held item", true);
   private static final LabelSetting COLOR_LABEL = new LabelSetting("Color Settings");
   private static final ColorSetting FULL_COLOR = new ColorSetting("100% Color", -1);
   private static final ColorSetting EIGHTY_COLOR = new ColorSetting("80% Color", -5592406);
   private static final ColorSetting SIXTY_COLOR = new ColorSetting("60% Color", -171);
   private static final ColorSetting FORTY_COLOR = new ColorSetting("40% Color", -22016);
   private static final ColorSetting QUARTER_COLOR = new ColorSetting("25% Color", -43691);
   private static final ColorSetting TEN_COLOR = new ColorSetting("10% Color", 11141120);
   public static final RenderItem ITEM_RENDERER = new RenderItem();
   private final List elements = new ArrayList();
   private final List settings = new LinkedList();

   public ArmorStatus() {
      super("Armor Status");
      this.settings.add(GENERAL_LABEL);
      this.settings.add(DAMAGE_OVERLAY);
      this.settings.add(ARMOR_DAMAGE);
      this.settings.add(ITEM_DAMAGE);
      this.settings.add(MAX_DAMAGE);
      this.settings.add(HELD_ITEM);
      this.settings.add(ITEM_COUNT);
      this.settings.add(ITEM_NAME);
      this.settings.add(DAMAGE_THRESHOLD_TYPE);
      this.settings.add(DAMAGE_DISPLAY_TYPE);
      this.settings.add(LIST_MODE);
      this.settings.add(COLOR_LABEL);
      this.settings.add(FULL_COLOR);
      this.settings.add(EIGHTY_COLOR);
      this.settings.add(SIXTY_COLOR);
      this.settings.add(FORTY_COLOR);
      this.settings.add(QUARTER_COLOR);
      this.settings.add(TEN_COLOR);
      this.addSetting(DAMAGE_THRESHOLD_TYPE);
      this.addSetting(DAMAGE_DISPLAY_TYPE);
      this.addSetting(LIST_MODE);
      this.addSetting(DAMAGE_OVERLAY);
      this.addSetting(ARMOR_DAMAGE);
      this.addSetting(ITEM_DAMAGE);
      this.addSetting(MAX_DAMAGE);
      this.addSetting(ITEM_COUNT);
      this.addSetting(ITEM_NAME);
      this.addSetting(FULL_COLOR);
      this.addSetting(EIGHTY_COLOR);
      this.addSetting(SIXTY_COLOR);
      this.addSetting(FORTY_COLOR);
      this.addSetting(QUARTER_COLOR);
      this.addSetting(TEN_COLOR);
   }

   public List getSortedSettings() {
      return this.settings;
   }

   public void renderPreview() {
      this.elements.clear();
      ItemStack[] armor = new ItemStack[]{new ItemStack(Item.getItemById(310)), new ItemStack(Item.getItemById(311)), new ItemStack(Item.getItemById(312)), new ItemStack(Item.getItemById(313))};

      for(int i = 0; i < 4; ++i) {
         ItemStack itemStack = this.mc.thePlayer.inventory.armorInventory[i];
         if (itemStack == null) {
            itemStack = armor[i];
         }

         this.elements.add(new HUDElement(itemStack, 16, 16, 2, true));
      }

      if (HELD_ITEM.getValue().booleanValue()) {
         if (this.mc.thePlayer.getCurrentEquippedItem() == null) {
            this.elements.add(new HUDElement(new ItemStack(Item.getItemById(276)), 16, 16, 2, false));
         } else {
            this.elements.add(new HUDElement(this.mc.thePlayer.getCurrentEquippedItem(), 16, 16, 2, false));
         }
      }

      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.displayArmorStatus();
      GL11.glPopMatrix();
   }

   public void renderReal() {
      this.getHUDElements();
      if (!this.elements.isEmpty()) {
         GL11.glPushMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.displayArmorStatus();
         GL11.glPopMatrix();
      }

   }

   private void getHUDElements() {
      this.elements.clear();

      for(int i = 3; i >= -1; --i) {
         ItemStack itemStack = null;
         if (i == -1 && HELD_ITEM.getValue().booleanValue()) {
            itemStack = this.mc.thePlayer.getCurrentEquippedItem();
         } else if (i != -1) {
            itemStack = this.mc.thePlayer.inventory.armorInventory[i];
         }

         if (itemStack != null) {
            this.elements.add(new HUDElement(itemStack, 16, 16, 2, i > -1));
         }
      }

   }

   public static int getColorCode(int percentage) {
      if (percentage <= 10) {
         return TEN_COLOR.getValue().intValue();
      } else if (percentage <= 25) {
         return QUARTER_COLOR.getValue().intValue();
      } else if (percentage <= 40) {
         return FORTY_COLOR.getValue().intValue();
      } else if (percentage <= 60) {
         return SIXTY_COLOR.getValue().intValue();
      } else {
         return percentage <= 80 ? EIGHTY_COLOR.getValue().intValue() : FULL_COLOR.getValue().intValue();
      }
   }

   private void displayArmorStatus() {
      if (this.elements.size() > 0) {
         int yOffset = ITEM_NAME.getValue().booleanValue() ? 18 : 16;
         int heWidth;
         if (LIST_MODE.getValue().equalsIgnoreCase("vertical")) {
            int yBase = 0;
            heWidth = 0;
            Iterator var4 = this.elements.iterator();

            HUDElement e;
            while(var4.hasNext()) {
               e = (HUDElement)var4.next();
               yBase += yOffset;
               if (e.width() > heWidth) {
                  heWidth = e.width();
               }
            }

            this.setHeight(yBase);
            this.setWidth(heWidth);
            var4 = this.elements.iterator();

            while(var4.hasNext()) {
               e = (HUDElement)var4.next();
               e.renderToHud(this.getX(), this.getY() - (float)yBase + (float)this.getHeight());
               yBase -= yOffset;
               if (e.width() > heWidth) {
                  heWidth = e.width();
               }
            }
         } else if (LIST_MODE.getValue().equalsIgnoreCase("horizontal")) {
            int yBase = 0;
            heWidth = 0;
            int heHeight = 0;
            Iterator var9 = this.elements.iterator();

            while(var9.hasNext()) {
               HUDElement e = (HUDElement)var9.next();
               e.renderToHud(this.getX() + (float)heWidth, this.getY() + (float)yBase);
               heWidth += e.width();
               if (e.height() > heHeight) {
                  heHeight += e.height();
               }
            }

            this.setHeight(heHeight);
            this.setWidth(heWidth);
         }
      }

   }
}
