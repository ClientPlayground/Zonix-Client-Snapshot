package net.minecraft.client.multiplayer;

import java.awt.Color;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class ServerData {
   public Color tintColor;
   public String serverName;
   public String serverIP;
   public String populationInfo;
   public String serverMOTD;
   public long pingToServer;
   public int field_82821_f;
   public String gameVersion;
   public boolean field_78841_f;
   public String field_147412_i;
   private ServerData.ServerResourceMode field_152587_j;
   private String field_147411_m;
   private boolean field_152588_l;
   private static final String __OBFID = "CL_00000890";

   public ServerData(String p_i1193_1_, String p_i1193_2_) {
      this.pingToServer = -1L;
      this.field_82821_f = 5;
      this.gameVersion = "1.7.10";
      this.field_152587_j = ServerData.ServerResourceMode.PROMPT;
      this.serverName = p_i1193_1_;
      this.serverIP = p_i1193_2_;
   }

   public ServerData(String p_i46395_1_, String p_i46395_2_, boolean p_i46395_3_) {
      this(p_i46395_1_, p_i46395_2_);
      this.field_152588_l = p_i46395_3_;
   }

   public ServerData(String p_i46395_1_, String p_i46395_2_, int p_i46395_3_) {
      this(p_i46395_1_, p_i46395_2_);
      this.tintColor = new Color(p_i46395_3_);
   }

   public NBTTagCompound getNBTCompound() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.setString("name", this.serverName);
      var1.setString("ip", this.serverIP);
      if (this.field_147411_m != null) {
         var1.setString("icon", this.field_147411_m);
      }

      if (this.field_152587_j == ServerData.ServerResourceMode.ENABLED) {
         var1.setBoolean("acceptTextures", true);
      } else if (this.field_152587_j == ServerData.ServerResourceMode.DISABLED) {
         var1.setBoolean("acceptTextures", false);
      }

      return var1;
   }

   public ServerData.ServerResourceMode func_152586_b() {
      return this.field_152587_j;
   }

   public void func_152584_a(ServerData.ServerResourceMode p_152584_1_) {
      this.field_152587_j = p_152584_1_;
   }

   public static ServerData getServerDataFromNBTCompound(NBTTagCompound p_78837_0_) {
      ServerData var1 = new ServerData(p_78837_0_.getString("name"), p_78837_0_.getString("ip"));
      if (p_78837_0_.func_150297_b("icon", 8)) {
         var1.func_147407_a(p_78837_0_.getString("icon"));
      }

      if (p_78837_0_.func_150297_b("acceptTextures", 1)) {
         if (p_78837_0_.getBoolean("acceptTextures")) {
            var1.func_152584_a(ServerData.ServerResourceMode.ENABLED);
         } else {
            var1.func_152584_a(ServerData.ServerResourceMode.DISABLED);
         }
      } else {
         var1.func_152584_a(ServerData.ServerResourceMode.PROMPT);
      }

      return var1;
   }

   public String func_147409_e() {
      return this.field_147411_m;
   }

   public void func_147407_a(String p_147407_1_) {
      this.field_147411_m = p_147407_1_;
   }

   public void func_152583_a(ServerData p_152583_1_) {
      this.serverIP = p_152583_1_.serverIP;
      this.serverName = p_152583_1_.serverName;
      this.func_152584_a(p_152583_1_.func_152586_b());
      this.field_147411_m = p_152583_1_.field_147411_m;
   }

   public boolean func_152585_d() {
      return this.field_152588_l;
   }

   public void ping() {
   }

   public static enum ServerResourceMode {
      ENABLED("ENABLED", 0, "enabled"),
      DISABLED("DISABLED", 1, "disabled"),
      PROMPT("PROMPT", 2, "prompt");

      private final IChatComponent field_152594_d;
      private static final ServerData.ServerResourceMode[] $VALUES = new ServerData.ServerResourceMode[]{ENABLED, DISABLED, PROMPT};
      private static final String __OBFID = "CL_00001833";

      private ServerResourceMode(String p_i1053_1_, int p_i1053_2_, String p_i1053_3_) {
         this.field_152594_d = new ChatComponentTranslation("addServer.resourcePack." + p_i1053_3_, new Object[0]);
      }

      public IChatComponent func_152589_a() {
         return this.field_152594_d;
      }
   }
}
