package us.zonix.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.module.impl.ArmorStatus;
import us.zonix.client.util.font.ZFontRenderer;

public class HUDElement {
   public final ItemStack itemStack;
   public final int iconW;
   public final int iconH;
   public final int padW;
   private int elementW;
   private int elementH;
   private String itemName = "";
   private int itemNameW;
   private String itemDamage = "";
   private int itemDamageW;
   private final boolean isArmor;
   private Minecraft mc = Minecraft.getMinecraft();
   private int color;

   public HUDElement(ItemStack itemStack, int iconW, int iconH, int padW, boolean isArmor) {
      this.itemStack = itemStack;
      this.iconW = iconW;
      this.iconH = iconH;
      this.padW = padW;
      this.isArmor = isArmor;
      this.initSize();
   }

   public int width() {
      return this.elementW;
   }

   public int height() {
      return this.elementH;
   }

   private void initSize() {
      this.elementH = ArmorStatus.ITEM_NAME.getValue().booleanValue() ? Math.max(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * 2, this.iconH) : Math.max(this.mc.fontRenderer.FONT_HEIGHT, this.iconH);
      if (this.itemStack != null) {
         if ((this.isArmor && ArmorStatus.ARMOR_DAMAGE.getValue().booleanValue() || !this.isArmor && ArmorStatus.ITEM_DAMAGE.getValue().booleanValue()) && this.itemStack.isItemStackDamageable()) {
            int maxDamage = this.itemStack.getMaxDamage() + 1;
            int damage = maxDamage - this.itemStack.getItemDamageForDisplay();
            this.color = ArmorStatus.getColorCode(ArmorStatus.DAMAGE_THRESHOLD_TYPE.getValue().equalsIgnoreCase("percent") ? damage * 100 / maxDamage : damage);
            if (ArmorStatus.DAMAGE_DISPLAY_TYPE.getValue().equalsIgnoreCase("value")) {
               this.itemDamage = damage + (ArmorStatus.MAX_DAMAGE.getValue().booleanValue() ? "/" + maxDamage : "");
            } else if (ArmorStatus.DAMAGE_DISPLAY_TYPE.getValue().equalsIgnoreCase("percent")) {
               this.itemDamage = damage * 100 / maxDamage + "%";
            }
         }

         this.itemDamageW = this.mc.fontRenderer.getStringWidth(StringUtils.stripCtrl(this.itemDamage));
         this.elementW = this.padW + this.iconW + this.padW + this.itemDamageW;
         if (ArmorStatus.ITEM_NAME.getValue().booleanValue()) {
            this.itemName = this.itemStack.getDisplayName();
            this.elementW = this.padW + this.iconW + this.padW + Math.max(this.mc.fontRenderer.getStringWidth(StringUtils.stripCtrl(this.itemName)), this.itemDamageW);
         }

         this.itemNameW = this.mc.fontRenderer.getStringWidth(StringUtils.stripCtrl(this.itemName));
      }

   }

   public void renderToHud(float x, float y) {
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(32826);
      RenderHelper.enableStandardItemLighting();
      RenderHelper.enableGUIStandardItemLighting();
      ArmorStatus.ITEM_RENDERER.zLevel = -10.0F;
      ArmorStatus.ITEM_RENDERER.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), this.itemStack, (int)x, (int)y);
      HUDUtils.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.itemStack, (int)x, (int)y, ArmorStatus.DAMAGE_OVERLAY.getValue().booleanValue(), ArmorStatus.ITEM_COUNT.getValue().booleanValue());
      RenderHelper.disableStandardItemLighting();
      GL11.glDisable(32826);
      GL11.glDisable(3042);
      ZFontRenderer fontRenderer = Client.getInstance().getRegularFontRenderer();
      fontRenderer.drawString(this.itemName + "§r", x + (float)this.iconW + (float)this.padW, y, this.color);
      fontRenderer.drawString(this.itemDamage + "§r", x + (float)this.iconW + (float)this.padW, y + (float)(ArmorStatus.ITEM_NAME.getValue().booleanValue() ? this.elementH / 2 : this.elementH / 4), this.color);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }
}
