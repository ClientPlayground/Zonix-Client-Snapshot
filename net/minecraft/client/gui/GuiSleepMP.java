package net.minecraft.client.gui;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class GuiSleepMP extends GuiChat {
   private static final String __OBFID = "CL_00000697";

   public void initGui() {
      super.initGui();
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 40, I18n.format("multiplayer.stopSleeping")));
   }

   protected void keyTyped(char p_73869_1_, int p_73869_2_) {
      if (p_73869_2_ == 1) {
         this.func_146418_g();
      } else if (p_73869_2_ != 28 && p_73869_2_ != 156) {
         super.keyTyped(p_73869_1_, p_73869_2_);
      } else {
         String var3 = this.field_146415_a.getText().trim();
         if (!var3.isEmpty()) {
            this.mc.thePlayer.sendChatMessage(var3);
         }

         this.field_146415_a.setText("");
         this.mc.ingameGUI.getChatGUI().resetScroll();
      }

   }

   protected void actionPerformed(GuiButton p_146284_1_) {
      if (p_146284_1_.id == 1) {
         this.func_146418_g();
      } else {
         super.actionPerformed(p_146284_1_);
      }

   }

   private void func_146418_g() {
      NetHandlerPlayClient var1 = this.mc.thePlayer.sendQueue;
      var1.addToSendQueue(new C0BPacketEntityAction(this.mc.thePlayer, 3));
   }
}
