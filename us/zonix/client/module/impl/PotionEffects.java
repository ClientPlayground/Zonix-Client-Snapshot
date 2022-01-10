package us.zonix.client.module.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.util.RenderUtil;

public final class PotionEffects extends AbstractModule {
   private static final ResourceLocation INVENTORY_RESOURCE = new ResourceLocation("textures/gui/container/inventory.png");
   public static final BooleanSetting SHOW_IN_INVENTORY = new BooleanSetting("Show in inventory", true);
   private List potionEffects = new ArrayList();

   public PotionEffects() {
      super("Potion Status");
      this.x = 4.0F;
      this.y = (float)((new ScaledResolution(this.mc)).getScaledHeight() / 2 - this.getHeight());
      this.addSetting(new LabelSetting("General Settings"));
      this.addSetting(new BooleanSetting("Show effect icon", true));
      this.addSetting(new BooleanSetting("Show effect name", true));
      this.addSetting(new BooleanSetting("Show duration", true));
      this.addSetting(SHOW_IN_INVENTORY);
      this.addSetting(new LabelSetting("Color Settings"));
      this.addSetting(new ColorSetting("Effect Color", -65536));
      this.addSetting(new ColorSetting("Duration Color", -65536));
   }

   public void renderReal() {
      if (this.mc.thePlayer != null) {
         this.potionEffects.clear();
         Collection potionEffects = this.mc.thePlayer.getActivePotionEffects();
         this.render(potionEffects);
      }
   }

   public void renderPreview() {
      if (this.mc.thePlayer != null) {
         if (!this.mc.thePlayer.getActivePotionEffects().isEmpty()) {
            this.renderReal();
         } else {
            if (this.potionEffects.isEmpty()) {
               this.potionEffects.add(new PotionEffect(Potion.damageBoost.getId(), 600, 1));
               this.potionEffects.add(new PotionEffect(Potion.moveSpeed.getId(), 1800, 1));
               this.potionEffects.add(new PotionEffect(Potion.fireResistance.getId(), 8400));
            }

            this.potionEffects.removeIf((potionEffect) -> {
               return potionEffect.getDuration() <= 0;
            });
            this.render(this.potionEffects);
         }
      }
   }

   private void render(Collection potionEffects) {
      int height = 0;
      int maxWidth = 0;

      for(Iterator var4 = potionEffects.iterator(); var4.hasNext(); height += 24) {
         PotionEffect effect = (PotionEffect)var4.next();
         String duration;
         int index;
         if (this.getBooleanSetting("Show effect name").getValue().booleanValue()) {
            duration = StatCollector.translateToLocal(effect.getEffectName()) + this.getAmplifierNumerals(effect.getAmplifier());
            index = this.mc.fontRenderer.getStringWidth(duration) + 24;
            this.mc.fontRenderer.drawStringWithShadow(duration, this.getX() + 22.0F, this.getY() + (float)height + 2.0F, this.getColorSetting("Effect Color").getValue().intValue());
            if (index > maxWidth) {
               maxWidth = index;
            }
         }

         if (this.getBooleanSetting("Show duration").getValue().booleanValue()) {
            duration = Potion.getDurationString(effect);
            index = this.mc.fontRenderer.getStringWidth(duration) + 24;
            this.mc.fontRenderer.drawStringWithShadow(duration, this.getX() + 22.0F, this.getY() + (float)height + 12.0F, this.getColorSetting("Duration Color").getValue().intValue());
            if (index > maxWidth) {
               maxWidth = index;
            }
         }

         if (this.getBooleanSetting("Show effect icon").getValue().booleanValue()) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (potion.hasStatusIcon()) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               this.mc.getTextureManager().bindTexture(INVENTORY_RESOURCE);
               index = potion.getStatusIconIndex();
               RenderUtil.drawTexturedRect(this.getX(), this.getY() + (float)height + 2.0F, index % 8 * 18, 198 + index / 8 * 18, 18, 18);
            }
         }
      }

      this.setWidth(maxWidth);
      this.setHeight(height);
   }

   private String getAmplifierNumerals(int amplifier) {
      switch(amplifier) {
      case 0:
         return " I";
      case 1:
         return " II";
      case 2:
         return " III";
      case 3:
         return " IV";
      case 4:
         return " V";
      case 5:
         return " VI";
      case 6:
         return " VII";
      case 7:
         return " VIII";
      case 8:
         return " IX";
      case 9:
         return " X";
      default:
         return amplifier < 1 ? "" : " " + amplifier + 1;
      }
   }
}
