package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase {
   private static final String __OBFID = "CL_00000475";

   public String getCommandName() {
      return "gamerule";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender p_71518_1_) {
      return "commands.gamerule.usage";
   }

   public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
      String var6;
      if (p_71515_2_.length == 2) {
         var6 = p_71515_2_[0];
         String var7 = p_71515_2_[1];
         GameRules var8 = this.getGameRules();
         if (var8.hasRule(var6)) {
            var8.setOrCreateGameRule(var6, var7);
            func_152373_a(p_71515_1_, this, "commands.gamerule.success", new Object[0]);
         } else {
            func_152373_a(p_71515_1_, this, "commands.gamerule.norule", new Object[]{var6});
         }
      } else {
         GameRules var4;
         if (p_71515_2_.length == 1) {
            var6 = p_71515_2_[0];
            var4 = this.getGameRules();
            if (var4.hasRule(var6)) {
               String var5 = var4.getGameRuleStringValue(var6);
               p_71515_1_.addChatMessage((new ChatComponentText(var6)).appendText(" = ").appendText(var5));
            } else {
               func_152373_a(p_71515_1_, this, "commands.gamerule.norule", new Object[]{var6});
            }
         } else {
            if (p_71515_2_.length != 0) {
               throw new WrongUsageException("commands.gamerule.usage", new Object[0]);
            }

            var4 = this.getGameRules();
            p_71515_1_.addChatMessage(new ChatComponentText(joinNiceString(var4.getRules())));
         }
      }

   }

   public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
      return p_71516_2_.length == 1 ? getListOfStringsMatchingLastWord(p_71516_2_, this.getGameRules().getRules()) : (p_71516_2_.length == 2 ? getListOfStringsMatchingLastWord(p_71516_2_, new String[]{"true", "false"}) : null);
   }

   private GameRules getGameRules() {
      return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
   }
}
