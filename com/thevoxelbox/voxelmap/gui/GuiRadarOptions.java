package com.thevoxelbox.voxelmap.gui;

import com.thevoxelbox.voxelmap.RadarSettingsManager;
import com.thevoxelbox.voxelmap.gui.overridden.EnumOptionsMinimap;
import com.thevoxelbox.voxelmap.gui.overridden.GuiOptionButtonMinimap;
import com.thevoxelbox.voxelmap.gui.overridden.GuiScreenMinimap;
import com.thevoxelbox.voxelmap.interfaces.IVoxelMap;
import com.thevoxelbox.voxelmap.util.I18nUtils;
import java.util.Iterator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiRadarOptions extends GuiScreenMinimap {
   private static final EnumOptionsMinimap[] relevantOptions;
   private final GuiScreen parent;
   private final RadarSettingsManager options;
   protected String screenTitle = "Radar Options";
   private IVoxelMap master;

   public GuiRadarOptions(GuiScreen parent, IVoxelMap master) {
      this.parent = parent;
      this.options = master.getRadarOptions();
   }

   public void initGui() {
      int var2 = 0;
      this.screenTitle = I18nUtils.getString("options.minimap.radar.title");

      GuiOptionButtonMinimap button;
      for(int t = 0; t < relevantOptions.length; ++t) {
         EnumOptionsMinimap option = relevantOptions[t];
         button = new GuiOptionButtonMinimap(option.returnEnumOrdinal(), this.getWidth() / 2 - 155 + var2 % 2 * 160, this.getHeight() / 6 + 24 * (var2 >> 1), option, this.options.getKeyText(option));
         this.getButtonList().add(button);
         ++var2;
      }

      Iterator var5 = this.getButtonList().iterator();

      while(true) {
         do {
            while(true) {
               Object buttonObj;
               do {
                  if (!var5.hasNext()) {
                     this.getButtonList().add(new GuiButton(101, this.getWidth() / 2 - 155, this.getHeight() / 6 + 144 - 6, 150, 20, I18nUtils.getString("options.minimap.radar.selectmobs")));
                     this.getButtonList().add(new GuiButton(200, this.getWidth() / 2 - 100, this.getHeight() / 6 + 168, I18nUtils.getString("gui.done")));
                     return;
                  }

                  buttonObj = var5.next();
               } while(!(buttonObj instanceof GuiOptionButtonMinimap));

               button = (GuiOptionButtonMinimap)buttonObj;
               if (!button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWRADAR)) {
                  button.enabled = this.options.show;
               }

               if (!button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWPLAYERHELMETS) && !button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWPLAYERNAMES)) {
                  break;
               }

               button.enabled = button.enabled && this.options.showPlayers;
            }
         } while(!button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWMOBHELMETS));

         button.enabled = button.enabled && (this.options.showNeutrals || this.options.showHostiles);
      }
   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.enabled) {
         if (par1GuiButton.id < 100 && par1GuiButton instanceof GuiOptionButtonMinimap) {
            this.options.setOptionValue(((GuiOptionButtonMinimap)par1GuiButton).returnEnumOptions(), 1);
            par1GuiButton.displayString = this.options.getKeyText(EnumOptionsMinimap.getEnumOptions(par1GuiButton.id));
            Iterator var2 = this.getButtonList().iterator();

            label80:
            while(true) {
               GuiOptionButtonMinimap button;
               label68:
               do {
                  while(true) {
                     while(true) {
                        if (!var2.hasNext()) {
                           break label80;
                        }

                        Object buttonObj = var2.next();
                        if (buttonObj instanceof GuiOptionButtonMinimap) {
                           button = (GuiOptionButtonMinimap)buttonObj;
                           if (!button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWRADAR)) {
                              button.enabled = this.options.show;
                           }

                           if (button.returnEnumOptions() != EnumOptionsMinimap.SHOWPLAYERHELMETS && button.returnEnumOptions() != EnumOptionsMinimap.SHOWPLAYERNAMES) {
                              continue label68;
                           }

                           button.enabled = button.enabled && this.options.showPlayers;
                        } else if (buttonObj instanceof GuiButton) {
                           GuiButton button = (GuiButton)buttonObj;
                           if (button.id == 101) {
                              button.enabled = this.options.show;
                           }
                        }
                     }
                  }
               } while(!button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWMOBHELMETS));

               button.enabled = button.enabled && (this.options.showNeutrals || this.options.showHostiles);
            }
         }

         if (par1GuiButton.id == 101) {
            this.getMinecraft().displayGuiScreen(new GuiMobs(this, this.options));
         }

         if (par1GuiButton.id == 200) {
            this.getMinecraft().displayGuiScreen(this.parent);
         }
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      super.drawMap();
      this.drawDefaultBackground();
      this.drawCenteredString(this.getFontRenderer(), this.screenTitle, this.getWidth() / 2, 20, 16777215);
      super.drawScreen(par1, par2, par3);
   }

   static {
      relevantOptions = new EnumOptionsMinimap[]{EnumOptionsMinimap.SHOWRADAR, EnumOptionsMinimap.RANDOMOBS, EnumOptionsMinimap.SHOWHOSTILES, EnumOptionsMinimap.SHOWNEUTRALS, EnumOptionsMinimap.SHOWPLAYERS, EnumOptionsMinimap.SHOWPLAYERNAMES, EnumOptionsMinimap.SHOWPLAYERHELMETS, EnumOptionsMinimap.SHOWMOBHELMETS, EnumOptionsMinimap.RADARFILTERING, EnumOptionsMinimap.RADAROUTLINES};
   }
}
