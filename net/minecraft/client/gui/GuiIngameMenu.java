package net.minecraft.client.gui;

import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.gui.MainMenuScreen;
import us.zonix.client.util.RenderUtil;

public class GuiIngameMenu extends GuiScreen {
   private final ResourceLocation usersIcon = new ResourceLocation("icon/users.png");
   private int field_146445_a;
   private int field_146444_f;
   private static final String __OBFID = "CL_00000703";
   private final GuiIngameMenu.EscapeButton[] escapeButtons = new GuiIngameMenu.EscapeButton[5];

   public void initGui() {
      this.mc.entityRenderer.setBlur(true);
      this.escapeButtons[0] = new GuiIngameMenu.EscapeButton("BACK TO GAME") {
         protected void onClick(int mouseX, int mouseY) {
            GuiIngameMenu.this.mc.displayGuiScreen((GuiScreen)null);
            GuiIngameMenu.this.mc.setIngameFocus();
         }
      };
      this.escapeButtons[1] = new GuiIngameMenu.EscapeButton("OPTIONS") {
         protected void onClick(int mouseX, int mouseY) {
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiOptions(GuiIngameMenu.this, GuiIngameMenu.this.mc.gameSettings));
         }
      };
      this.escapeButtons[2] = new GuiIngameMenu.EscapeButton("SERVER SELECTOR") {
         protected void onClick(int mouseX, int mouseY) {
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiMultiplayer(GuiIngameMenu.this));
         }
      };
      this.escapeButtons[3] = new GuiIngameMenu.EscapeButton("OPEN TO LAN") {
         protected void onClick(int mouseX, int mouseY) {
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiShareToLan(GuiIngameMenu.this));
         }
      };
      this.escapeButtons[4] = new GuiIngameMenu.EscapeButton("DISCONNECT") {
         protected void onClick(int mouseX, int mouseY) {
            GuiIngameMenu.this.mc.theWorld.sendQuittingDisconnectingPacket();
            GuiIngameMenu.this.mc.loadWorld((WorldClient)null);
            GuiIngameMenu.this.mc.displayGuiScreen(new GuiMainMenu());
         }
      };
      this.field_146445_a = 0;
      this.buttonList.clear();
   }

   protected void actionPerformed(GuiButton p_146284_1_) {
      switch(p_146284_1_.id) {
      case 0:
         this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
         break;
      case 1:
         p_146284_1_.enabled = false;
         this.mc.theWorld.sendQuittingDisconnectingPacket();
         this.mc.loadWorld((WorldClient)null);
         this.mc.displayGuiScreen(new GuiMainMenu());
      case 2:
      case 3:
      default:
         break;
      case 4:
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
         break;
      case 5:
         this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.func_146107_m()));
         break;
      case 6:
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.func_146107_m()));
         break;
      case 7:
         this.mc.displayGuiScreen(new GuiShareToLan(this));
      }

   }

   public void updateScreen() {
      super.updateScreen();
      ++this.field_146444_f;
   }

   protected void mouseClicked(int mouseX, int mouseY, int button) {
      ScaledResolution resolution = new ScaledResolution(this.mc);
      float buttonWidth = 90.0F;
      float startX = (float)resolution.getScaledWidth() - 10.0F;
      if ((float)mouseX >= startX - buttonWidth && (float)mouseX <= startX && (float)mouseY >= 11.5F && (float)mouseY <= 31.0F) {
         this.mc.shutdown();
      } else {
         startX -= buttonWidth + 10.0F;
         buttonWidth = 70.0F;
         if ((float)mouseX >= startX - buttonWidth && (float)mouseX <= startX && (float)mouseY >= 11.5F && (float)mouseY <= 31.0F) {
            System.out.println("Open friend menu");
         } else {
            float boxWidth = 225.0F;
            float boxHeight = 150.0F;
            float minX = (float)(resolution.getScaledWidth() / 2) - boxWidth / 2.0F + 10.0F;
            float minY = (float)(resolution.getScaledHeight() / 2) - boxHeight / 2.0F + 10.0F;
            float buttonHeight = 25.0F;
            GuiIngameMenu.EscapeButton[] var12 = this.escapeButtons;
            int var13 = var12.length;

            for(int var14 = 0; var14 < var13; ++var14) {
               GuiIngameMenu.EscapeButton escapeButton = var12[var14];
               if ((!escapeButton.text.equals("OPEN TO LAN") || this.mc.isSingleplayer()) && (!escapeButton.text.equals("SERVER SELECTOR") || !this.mc.isSingleplayer())) {
                  if ((float)mouseX >= minX && (float)mouseX <= minX + boxWidth - 20.0F && (float)mouseY >= minY && (float)mouseY <= minY + buttonHeight) {
                     escapeButton.onClick(mouseX, mouseY);
                     return;
                  }

                  minY += buttonHeight + 10.0F;
               }
            }

         }
      }
   }

   public void onGuiClosed() {
      this.mc.entityRenderer.setBlur(false);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution resolution = new ScaledResolution(this.mc);
      RenderUtil.drawRect(0.0F, 0.0F, (float)resolution.getScaledWidth(), 40.0F, -291356865);
      RenderUtil.drawString(Client.getInstance().getLargeBoldFontRenderer(), "ZONIX CLIENT", 10.0F, 12.5F, -1, true);
      float buttonWidth = 90.0F;
      float startX = (float)resolution.getScaledWidth() - 10.0F;
      RenderUtil.drawBorderedRoundedRect(startX - buttonWidth, 11.5F, startX, 31.0F, 5.0F, -11000539, -7848387);
      RenderUtil.drawCenteredString(Client.getInstance().getSmallBoldFontRenderer(), "EXIT TO DESKTOP", (float)((int)(startX - buttonWidth / 2.0F)) + 6.0F, 21.0F, -1);
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderUtil.drawSquareTexture(MainMenuScreen.closeIcon, 7.0F, startX - buttonWidth + 2.0F, 14.5F);
      GL11.glPopMatrix();
      startX -= buttonWidth + 10.0F;
      buttonWidth = 70.0F;
      RenderUtil.drawBorderedRoundedRect(startX - buttonWidth, 11.5F, startX, 31.0F, 5.0F, -11000539, -7848387);
      int onlineFriends = Client.getInstance().getFriendManager().getOnlineFriends().size();
      RenderUtil.drawCenteredString(Client.getInstance().getSmallBoldFontRenderer(), onlineFriends + " ONLINE", (float)((int)(startX - buttonWidth / 2.0F)) + 7.5F, 21.0F, -1);
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderUtil.drawSquareTexture(this.usersIcon, 7.0F, startX - buttonWidth + 7.5F, 14.5F);
      GL11.glPopMatrix();
      float boxWidth = 225.0F;
      float boxHeight = 150.0F;
      float minX = (float)(resolution.getScaledWidth() / 2) - boxWidth / 2.0F;
      float minY = (float)(resolution.getScaledHeight() / 2) - boxHeight / 2.0F;
      RenderUtil.drawRect(minX, minY, minX + boxWidth, minY + boxHeight, 1997935379);
      minX += 10.0F;
      minY += 10.0F;
      float buttonHeight = 25.0F;
      GuiIngameMenu.EscapeButton[] var13 = this.escapeButtons;
      int var14 = var13.length;

      for(int var15 = 0; var15 < var14; ++var15) {
         GuiIngameMenu.EscapeButton button = var13[var15];
         if ((!button.text.equals("OPEN TO LAN") || this.mc.isSingleplayer()) && (!button.text.equals("SERVER SELECTOR") || !this.mc.isSingleplayer())) {
            RenderUtil.drawBorderedRect(minX, minY, minX + boxWidth - 20.0F, minY + buttonHeight, 1.0F, 1727987712, 1711276032);
            RenderUtil.drawCenteredString(Client.getInstance().getRegularMediumBoldFontRenderer(), button.text, (float)((int)(minX + (boxWidth - 20.0F) / 2.0F)), (float)((int)(minY + buttonHeight / 2.0F) + 1), -1);
            minY += buttonHeight + 10.0F;
         }
      }

   }

   private abstract class EscapeButton {
      private final String text;

      EscapeButton(String text) {
         this.text = text;
      }

      protected abstract void onClick(int var1, int var2);
   }
}
