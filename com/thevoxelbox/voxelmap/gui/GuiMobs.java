package com.thevoxelbox.voxelmap.gui;

import com.thevoxelbox.voxelmap.RadarSettingsManager;
import com.thevoxelbox.voxelmap.gui.overridden.GuiScreenMinimap;
import com.thevoxelbox.voxelmap.util.CustomMob;
import com.thevoxelbox.voxelmap.util.CustomMobsManager;
import com.thevoxelbox.voxelmap.util.EnumMobs;
import com.thevoxelbox.voxelmap.util.I18nUtils;
import java.util.Iterator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiMobs extends GuiScreenMinimap {
   protected final RadarSettingsManager options;
   private final GuiScreen parentScreen;
   protected String screenTitle = "Select Mobs";
   protected String selectedMobName = null;
   private GuiSlotMobs mobsList;
   private GuiButton buttonEnable;
   private GuiButton buttonDisable;
   private String tooltip = null;

   public GuiMobs(GuiScreen parentScreen, RadarSettingsManager options) {
      this.parentScreen = parentScreen;
      this.options = options;
   }

   static String setTooltip(GuiMobs par0GuiWaypoints, String par1Str) {
      return par0GuiWaypoints.tooltip = par1Str;
   }

   public void initGui() {
      int var2 = false;
      this.screenTitle = I18nUtils.getString("options.minimap.mobs.title");
      this.mobsList = new GuiSlotMobs(this);
      this.mobsList.func_148134_d(7, 8);
      this.getButtonList().add(this.buttonEnable = new GuiButton(-1, this.getWidth() / 2 - 154, this.getHeight() - 28, 100, 20, I18nUtils.getString("options.minimap.mobs.enable")));
      this.getButtonList().add(this.buttonDisable = new GuiButton(-2, this.getWidth() / 2 - 50, this.getHeight() - 28, 100, 20, I18nUtils.getString("options.minimap.mobs.disable")));
      this.getButtonList().add(new GuiButton(65336, this.getWidth() / 2 + 4 + 50, this.getHeight() - 28, 100, 20, I18nUtils.getString("gui.done")));
      boolean isSomethingSelected = this.selectedMobName != null;
      this.buttonEnable.enabled = isSomethingSelected;
      this.buttonDisable.enabled = isSomethingSelected;
   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.enabled) {
         if (par1GuiButton.id == -1) {
            this.setMobEnabled(this.selectedMobName, true);
         }

         if (par1GuiButton.id == -2) {
            this.setMobEnabled(this.selectedMobName, false);
         }

         if (par1GuiButton.id == 65336) {
            this.getMinecraft().displayGuiScreen(this.parentScreen);
         }
      }

   }

   protected void setSelectedMob(String mob) {
      this.selectedMobName = mob;
   }

   private boolean isMobEnabled(String selectedMobName2) {
      EnumMobs mob = EnumMobs.getMobByName(this.selectedMobName);
      if (mob != null) {
         return mob.enabled;
      } else {
         CustomMob customMob = CustomMobsManager.getCustomMobByName(this.selectedMobName);
         return customMob != null ? customMob.enabled : false;
      }
   }

   private void setMobEnabled(String selectedMobName, boolean enabled) {
      EnumMobs[] var3 = EnumMobs.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumMobs mob = var3[var5];
         if (mob.name.equals(selectedMobName)) {
            mob.enabled = enabled;
         }
      }

      Iterator var7 = CustomMobsManager.mobs.iterator();

      while(var7.hasNext()) {
         CustomMob mob = (CustomMob)var7.next();
         if (mob.name.equals(selectedMobName)) {
            mob.enabled = enabled;
         }
      }

   }

   protected void toggleMobVisibility() {
      EnumMobs mob = EnumMobs.getMobByName(this.selectedMobName);
      if (mob != null) {
         this.setMobEnabled(this.selectedMobName, !mob.enabled);
      } else {
         CustomMob customMob = CustomMobsManager.getCustomMobByName(this.selectedMobName);
         if (customMob != null) {
            this.setMobEnabled(this.selectedMobName, !customMob.enabled);
         }
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      super.drawMap();
      this.tooltip = null;
      this.mobsList.func_148128_a(par1, par2, par3);
      this.drawCenteredString(this.getFontRenderer(), this.screenTitle, this.getWidth() / 2, 20, 16777215);
      boolean isSomethingSelected = this.selectedMobName != null;
      this.buttonEnable.enabled = isSomethingSelected && !this.isMobEnabled(this.selectedMobName);
      this.buttonDisable.enabled = isSomethingSelected && this.isMobEnabled(this.selectedMobName);
      super.drawScreen(par1, par2, par3);
      if (this.tooltip != null) {
         this.drawTooltip(this.tooltip, par1, par2);
      }

   }

   protected void drawTooltip(String par1Str, int par2, int par3) {
      if (par1Str != null) {
         int var4 = par2 + 12;
         int var5 = par3 - 12;
         int var6 = this.getFontRenderer().getStringWidth(par1Str);
         this.drawGradientRect(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
         this.getFontRenderer().drawStringWithShadow(par1Str, var4, var5, -1);
      }

   }
}
