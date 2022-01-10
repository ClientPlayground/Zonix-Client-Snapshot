package com.thevoxelbox.voxelmap.gui;

import com.thevoxelbox.voxelmap.MapSettingsManager;
import com.thevoxelbox.voxelmap.gui.overridden.EnumOptionsMinimap;
import com.thevoxelbox.voxelmap.gui.overridden.GuiOptionButtonMinimap;
import com.thevoxelbox.voxelmap.gui.overridden.GuiScreenMinimap;
import com.thevoxelbox.voxelmap.interfaces.IVoxelMap;
import com.thevoxelbox.voxelmap.util.I18nUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiMinimapOptions extends GuiScreenMinimap {
   private static EnumOptionsMinimap[] relevantOptions;
   private final MapSettingsManager options;
   protected String screenTitle = "Minimap Options";
   private IVoxelMap master;

   public GuiMinimapOptions(IVoxelMap master) {
      this.master = master;
      this.options = master.getMapOptions();
   }

   public void initGui() {
      relevantOptions = new EnumOptionsMinimap[]{EnumOptionsMinimap.COORDS, EnumOptionsMinimap.HIDE, EnumOptionsMinimap.LOCATION, EnumOptionsMinimap.SIZE, EnumOptionsMinimap.SQUARE, EnumOptionsMinimap.OLDNORTH, EnumOptionsMinimap.BEACONS, EnumOptionsMinimap.CAVEMODE};
      int var2 = 0;
      this.screenTitle = I18nUtils.getString("options.minimap.title");

      for(int t = 0; t < relevantOptions.length; ++t) {
         EnumOptionsMinimap option = relevantOptions[t];
         GuiOptionButtonMinimap var7 = new GuiOptionButtonMinimap(option.returnEnumOrdinal(), this.getWidth() / 2 - 155 + var2 % 2 * 160, this.getHeight() / 6 + 24 * (var2 >> 1), option, this.options.getKeyText(option));
         this.getButtonList().add(var7);
         if (option.equals(EnumOptionsMinimap.CAVEMODE)) {
            var7.enabled = this.options.cavesAllowed.booleanValue() && this.master.getRadar() != null;
         }

         ++var2;
      }

      GuiOptionButtonMinimap radarOptionsButton = new GuiOptionButtonMinimap(101, this.getWidth() / 2 - 155, this.getHeight() / 6 + 120 - 6, 150, 20, I18nUtils.getString("options.minimap.radar"));
      radarOptionsButton.enabled = this.options.radarAllowed.booleanValue() && this.master.getRadar() != null;
      this.getButtonList().add(radarOptionsButton);
      this.getButtonList().add(new GuiButton(103, this.getWidth() / 2 + 5, this.getHeight() / 6 + 120 - 6, 150, 20, I18nUtils.getString("options.minimap.detailsperformance")));
      this.getButtonList().add(new GuiButton(102, this.getWidth() / 2 - 155, this.getHeight() / 6 + 144 - 6, 150, 20, I18nUtils.getString("options.controls")));
      this.getButtonList().add(new GuiButton(100, this.getWidth() / 2 + 5, this.getHeight() / 6 + 144 - 6, 150, 20, I18nUtils.getString("options.minimap.waypoints")));
      this.getButtonList().add(new GuiButton(200, this.getWidth() / 2 - 100, this.getHeight() / 6 + 168, I18nUtils.getString("menu.returnToGame")));
   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.enabled) {
         if (par1GuiButton.id < 100 && par1GuiButton instanceof GuiOptionButtonMinimap) {
            this.options.setOptionValue(((GuiOptionButtonMinimap)par1GuiButton).returnEnumOptions(), 1);
            par1GuiButton.displayString = this.options.getKeyText(EnumOptionsMinimap.getEnumOptions(par1GuiButton.id));
         }

         if (par1GuiButton.id == 103) {
            this.getMinecraft().displayGuiScreen(new GuiMinimapPerformance(this, this.master));
         }

         if (par1GuiButton.id == 102) {
            this.getMinecraft().displayGuiScreen(new GuiMinimapControls(this, this.master));
         }

         if (par1GuiButton.id == 101) {
            this.getMinecraft().displayGuiScreen(new GuiRadarOptions(this, this.master));
         }

         if (par1GuiButton.id == 100) {
            this.getMinecraft().displayGuiScreen(new GuiWaypoints(this, this.master));
         }

         if (par1GuiButton.id == 200) {
            this.getMinecraft().displayGuiScreen((GuiScreen)null);
         }
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      super.drawMap();
      this.drawDefaultBackground();
      this.drawCenteredString(this.getFontRenderer(), this.screenTitle, this.getWidth() / 2, 20, 16777215);
      super.drawScreen(par1, par2, par3);
   }
}
