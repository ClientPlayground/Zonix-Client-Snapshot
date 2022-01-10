package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.awt.Color;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.util.RenderUtil;

public class GuiMultiplayer extends GuiScreen implements GuiYesNoCallback {
   private static final ResourceLocation[] BARS = new ResourceLocation[]{new ResourceLocation("icon/bars/bars_1.png"), new ResourceLocation("icon/bars/bars_2.png"), new ResourceLocation("icon/bars/bars_3.png"), new ResourceLocation("icon/bars/bars_4.png"), new ResourceLocation("icon/bars/bars_5.png")};
   private static final ResourceLocation[] PINNED = new ResourceLocation[]{new ResourceLocation("pinned/red.png"), new ResourceLocation("pinned/green.png"), new ResourceLocation("pinned/blue.jpg"), new ResourceLocation("pinned/pink.png")};
   private static final ResourceLocation PLAY_BUTTON = new ResourceLocation("icon/play.png");
   private static final ThreadPoolExecutor serverPingExecutor = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
   private static final Logger logger = LogManager.getLogger();
   private final OldServerPinger field_146797_f = new OldServerPinger();
   private GuiScreen field_146798_g;
   private ServerSelectionList field_146803_h;
   private ServerList field_146804_i;
   private GuiButton field_146810_r;
   private GuiButton field_146809_s;
   private GuiButton field_146808_t;
   private boolean field_146807_u;
   private boolean field_146806_v;
   private boolean field_146805_w;
   private boolean field_146813_x;
   private String field_146812_y;
   private ServerData field_146811_z;
   private LanServerDetector.LanServerList field_146799_A;
   private LanServerDetector.ThreadLanServerFind field_146800_B;
   private boolean field_146801_C;
   private static final String __OBFID = "CL_00000814";
   public static boolean multiPlayerOpen;
   private boolean initialised;
   private boolean updating;
   private long lastUpdateTime;
   private int scrollAmount;
   private ServerData hoveringData;
   private ServerData selectedData;
   private ServerData deleting;
   private long lastSelectTime;
   private int mousePressY;

   public GuiMultiplayer(GuiScreen p_i1040_1_) {
      this.field_146798_g = p_i1040_1_;
   }

   public void initGui() {
      multiPlayerOpen = true;
      this.scrollAmount = 0;
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      if (!this.field_146801_C) {
         this.field_146801_C = true;
         this.field_146804_i = new ServerList(this.mc);
         this.field_146804_i.loadServerList();
         this.field_146799_A = new LanServerDetector.LanServerList();

         try {
            this.field_146800_B = new LanServerDetector.ThreadLanServerFind(this.field_146799_A);
            this.field_146800_B.start();
         } catch (Exception var2) {
            logger.warn("Unable to start LAN server detection: " + var2.getMessage());
         }

         this.field_146803_h = new ServerSelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
         this.field_146803_h.func_148195_a(this.field_146804_i);
      } else {
         this.field_146803_h.func_148122_a(this.width, this.height, 32, this.height - 64);
      }

   }

   public void func_146794_g() {
      this.buttonList.add(this.field_146810_r = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit")));
      this.buttonList.add(this.field_146808_t = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete")));
      this.buttonList.add(this.field_146809_s = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select")));
      this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct")));
      this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add")));
      this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh")));
      this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel")));
      this.func_146790_a(this.field_146803_h.func_148193_k());
   }

   public void updateScreen() {
      super.updateScreen();
      if (this.field_146799_A.getWasUpdated()) {
         List var1 = this.field_146799_A.getLanServers();
         this.field_146799_A.setWasNotUpdated();
         this.field_146803_h.func_148194_a(var1);
      }

      this.field_146797_f.func_147223_a();
      if (!this.updating && this.lastUpdateTime + 5000L < System.currentTimeMillis()) {
         this.lastUpdateTime = System.currentTimeMillis();
         this.updating = true;
         (new Thread(new Runnable() {
            public void run() {
               List allServers = new LinkedList();
               allServers.addAll(GuiMultiplayer.this.field_146804_i.pinnedServers);
               allServers.addAll(GuiMultiplayer.this.field_146804_i.servers);
               final List pinged = new ArrayList();
               Iterator var3 = allServers.iterator();

               while(var3.hasNext()) {
                  final ServerData serverData = (ServerData)var3.next();
                  GuiMultiplayer.serverPingExecutor.execute(new Runnable() {
                     public void run() {
                        try {
                           if (!GuiMultiplayer.this.initialised) {
                              GuiMultiplayer.this.func_146789_i().init(serverData);
                           } else {
                              GuiMultiplayer.this.func_146789_i().func_147224_a(serverData, (NetworkManager)null, (ServerAddress)null);
                           }
                        } catch (UnknownHostException var2) {
                           serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can't resolve hostname";
                           serverData.pingToServer = -1L;
                        } catch (Exception var3) {
                           serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                           serverData.pingToServer = -1L;
                        }

                        pinged.add(serverData);
                     }
                  });
               }

               while(pinged.size() < allServers.size()) {
                  try {
                     Thread.sleep(100L);
                  } catch (InterruptedException var5) {
                     var5.printStackTrace();
                  }
               }

               GuiMultiplayer.this.initialised = true;
               GuiMultiplayer.this.updating = false;
            }
         })).start();
      }

   }

   public void onGuiClosed() {
      multiPlayerOpen = false;
      this.field_146803_h.close();
      Keyboard.enableRepeatEvents(false);
      if (this.field_146800_B != null) {
         this.field_146800_B.interrupt();
         this.field_146800_B = null;
      }

      this.field_146797_f.func_147226_b();
   }

   protected void actionPerformed(GuiButton p_146284_1_) {
   }

   private void func_146792_q() {
      this.mc.displayGuiScreen(new GuiMultiplayer(this.field_146798_g));
   }

   public void confirmClicked(boolean p_73878_1_, int p_73878_2_) {
      if (this.field_146803_h.func_148193_k() < 0) {
         Object var10000 = null;
      } else {
         this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k());
      }

      if (this.field_146807_u) {
         this.field_146807_u = false;
         if (this.deleting != null) {
            this.field_146804_i.servers.remove(this.deleting);
            this.field_146804_i.saveServerList();
         }

         this.mc.displayGuiScreen(this);
      } else if (this.field_146813_x) {
         this.field_146813_x = false;
         if (p_73878_1_) {
            this.func_146791_a(this.field_146811_z);
         } else {
            this.mc.displayGuiScreen(this);
         }
      } else if (this.field_146806_v) {
         this.field_146806_v = false;
         if (p_73878_1_) {
            this.field_146804_i.addServerData(this.field_146811_z);
            this.field_146804_i.saveServerList();
            this.field_146803_h.func_148192_c(-1);
            this.field_146803_h.func_148195_a(this.field_146804_i);
         }

         this.mc.displayGuiScreen(this);
      } else if (this.field_146805_w) {
         this.field_146805_w = false;
         this.mc.displayGuiScreen(this);
      }

   }

   protected void keyTyped(char p_73869_1_, int p_73869_2_) {
      int var3 = this.field_146803_h.func_148193_k();
      if (var3 < 0) {
         Object var10000 = null;
      } else {
         this.field_146803_h.func_148180_b(var3);
      }

      if (p_73869_2_ == 63) {
         this.func_146792_q();
      } else if (var3 >= 0) {
         if (p_73869_2_ == 200) {
            if (!isShiftKeyDown()) {
               if (var3 > 0) {
                  this.func_146790_a(this.field_146803_h.func_148193_k() - 1);
                  this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());
                  if (this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k()) instanceof ServerListEntryLanScan) {
                     if (this.field_146803_h.func_148193_k() > 0) {
                        this.func_146790_a(this.field_146803_h.getSize() - 1);
                        this.field_146803_h.func_148145_f(-this.field_146803_h.func_148146_j());
                     } else {
                        this.func_146790_a(-1);
                     }
                  }
               } else {
                  this.func_146790_a(-1);
               }
            }
         } else if (p_73869_2_ == 208) {
            if (isShiftKeyDown()) {
               if (var3 < this.field_146804_i.countServers() - 1) {
                  this.field_146804_i.swapServers(var3, var3 + 1);
                  this.func_146790_a(var3 + 1);
                  this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
                  this.field_146803_h.func_148195_a(this.field_146804_i);
               }
            } else if (var3 < this.field_146803_h.getSize()) {
               this.func_146790_a(this.field_146803_h.func_148193_k() + 1);
               this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
               if (this.field_146803_h.func_148180_b(this.field_146803_h.func_148193_k()) instanceof ServerListEntryLanScan) {
                  if (this.field_146803_h.func_148193_k() < this.field_146803_h.getSize() - 1) {
                     this.func_146790_a(this.field_146803_h.getSize() + 1);
                     this.field_146803_h.func_148145_f(this.field_146803_h.func_148146_j());
                  } else {
                     this.func_146790_a(-1);
                  }
               }
            } else {
               this.func_146790_a(-1);
            }
         } else if (p_73869_2_ != 28 && p_73869_2_ != 156) {
            super.keyTyped(p_73869_1_, p_73869_2_);
         } else {
            this.actionPerformed((GuiButton)this.buttonList.get(2));
         }
      } else {
         super.keyTyped(p_73869_1_, p_73869_2_);
      }

   }

   private String[] split(String[] lines, float width) {
      List list = new ArrayList();
      String[] var4 = lines;
      int var5 = lines.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String line = var4[var6];
         StringBuilder builder = new StringBuilder();
         String[] var9 = line.split(" ");
         int var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            String s = var9[var11];
            String temp = builder.toString() + " " + s;
            if ((float)Client.getInstance().getRegularFontRenderer().getStringWidth(temp) >= width) {
               list.add(builder.toString());
               builder = new StringBuilder();
            }

            if (builder.length() > 0) {
               builder.append(" ");
            }

            builder.append(s);
         }

         if (builder.length() > 0) {
            list.add(builder.toString());
         }
      }

      return (String[])list.toArray(new String[0]);
   }

   public void handleMouseInput() {
      super.handleMouseInput();
      int scroll = Mouse.getEventDWheel();
      if (scroll != 0) {
         this.scroll(scroll);
      }
   }

   private void scroll(int scroll) {
      int before = this.scrollAmount;
      this.scrollAmount += scroll;
      if (this.scrollAmount > 0) {
         this.scrollAmount = 0;
      }

      ScaledResolution resolution = new ScaledResolution(this.mc);
      List serverDataList = new LinkedList();
      serverDataList.addAll(this.field_146804_i.servers);
      float maxY = (float)resolution.getScaledHeight() - 59.0F;
      float translate = (float)this.scrollAmount / 10.0F;
      float startY = 130.0F + translate;
      boolean move = false;

      for(int i = 0; i < serverDataList.size(); ++i) {
         if (startY + 60.0F > maxY) {
            move = true;
            break;
         }

         startY += 60.0F;
      }

      if (!move) {
         this.scrollAmount = before;
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution resolution = new ScaledResolution(this.mc);
      this.field_146812_y = null;
      RenderUtil.drawRect(0.0F, 0.0F, (float)resolution.getScaledWidth(), (float)resolution.getScaledHeight(), -16250872);
      RenderUtil.drawRect(0.0F, 0.0F, (float)resolution.getScaledWidth(), 30.0F, -14541283);
      RenderUtil.drawRect(0.0F, (float)resolution.getScaledHeight(), (float)resolution.getScaledWidth(), (float)resolution.getScaledHeight() - 50.0F, -14541283);
      float pinnedWidth = 70.0F;
      float startX = (float)(resolution.getScaledWidth() / 2) - 155.0F;
      int i = 0;

      for(Iterator var8 = this.field_146804_i.pinnedServers.iterator(); var8.hasNext(); startX += pinnedWidth + 10.0F) {
         ServerData serverData = (ServerData)var8.next();
         GL11.glPushMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderUtil.drawSquareTexture(PINNED[i++], pinnedWidth, 75.0F, startX, 45.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
         RenderUtil.drawSquareTexture(PLAY_BUTTON, 10.0F, startX + 24.5F, 93.75F);
         Color color = serverData.tintColor;
         GL11.glColor4f((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), 0.4F);
         RenderUtil.drawSquareTexture(PLAY_BUTTON, 10.0F, startX + 25.0F, 93.0F);
         GL11.glPopMatrix();
         int boxMiddle = (int)(startX + pinnedWidth / 2.0F);
         RenderUtil.drawCenteredString(Client.getInstance().getHugeBoldFontRenderer(), serverData.serverName.replace("Zonix ", ""), (float)boxMiddle, 60.0F, -1);
         String ping = serverData.pingToServer < 0L ? "?" : String.valueOf(serverData.pingToServer);
         RenderUtil.drawCenteredString(Client.getInstance().getTinyFontRenderer(), "Ping: " + ping + "MS", (float)boxMiddle, 73.0F, -1);
         String population;
         if (serverData.populationInfo == null) {
            population = "?";
         } else {
            population = EnumChatFormatting.getTextWithoutFormattingCodes(serverData.populationInfo.split("/")[0]);
         }

         RenderUtil.drawCenteredString(Client.getInstance().getTinyFontRenderer(), "Players: " + population, (float)boxMiddle, 82.0F, -1);
      }

      List serverDataList = new LinkedList();
      serverDataList.addAll(this.field_146804_i.servers);
      float boxWidth = 300.0F;
      float startY = 130.0F;
      float minX = (float)(resolution.getScaledWidth() / 2) - boxWidth / 2.0F;
      float maxY = (float)resolution.getScaledHeight() - 59.0F;
      RenderUtil.startScissorBox(startY, maxY, 0.0F, (float)resolution.getScaledWidth());
      GL11.glPushMatrix();
      float currentMaxY = startY + (float)serverDataList.size() * 50.0F + (float)(serverDataList.size() - 1) * 10.0F;
      float buttonHeight;
      if (currentMaxY > maxY) {
         buttonHeight = (float)this.scrollAmount / 10.0F;
         startY += buttonHeight;
      } else {
         this.scrollAmount = 0;
      }

      this.hoveringData = null;

      for(Iterator var28 = serverDataList.iterator(); var28.hasNext(); startY += 50.0F) {
         ServerData pinnedServer = (ServerData)var28.next();
         FontRenderer fontRenderer = this.mc.fontRenderer;
         if (this.selectedData == pinnedServer) {
            RenderUtil.drawRoundedRect((double)minX, (double)startY, (double)(minX + boxWidth), (double)(startY + 48.0F), 10.0D, -1457249244);
         }

         fontRenderer.drawString(pinnedServer.serverName, (int)minX + 10, (int)startY + 7, -1);
         if ((float)mouseX >= minX && (float)mouseX <= minX + boxWidth && (float)mouseY >= startY && (float)mouseY <= startY + 50.0F) {
            this.hoveringData = pinnedServer;
         }

         byte bars;
         if (pinnedServer.pingToServer < 0L) {
            bars = -1;
         } else if (pinnedServer.pingToServer < 100L) {
            bars = 4;
         } else if (pinnedServer.pingToServer < 200L) {
            bars = 3;
         } else if (pinnedServer.pingToServer < 350L) {
            bars = 2;
         } else if (pinnedServer.pingToServer < 500L) {
            bars = 1;
         } else {
            bars = 0;
         }

         if (bars != -1) {
            RenderUtil.drawTexture(BARS[bars], minX + boxWidth - 20.0F, startY + 7.0F, 10.5F, 7.5F);
         }

         String players = EnumChatFormatting.getTextWithoutFormattingCodes(pinnedServer.populationInfo);
         fontRenderer.drawString(players, (int)(minX + boxWidth - 23.0F - (float)fontRenderer.getStringWidth(players)), (int)(startY + 7.0F), -3421237);
         if (pinnedServer.serverMOTD != null) {
            String[] lines = pinnedServer.serverMOTD.split("\n");
            float stringWidth = (float)fontRenderer.getStringWidth(pinnedServer.serverMOTD);
            if (stringWidth + 10.0F > boxWidth - 15.0F) {
               lines = this.split(lines, boxWidth - 15.0F);
            }

            for(int j = 0; j < lines.length && j <= 1; ++j) {
               fontRenderer.drawString(lines[j], (int)(minX + 10.0F), (int)(startY + 20.0F + (float)(12 * j)), -3421237);
            }
         } else {
            fontRenderer.drawString("Pinging...", (int)(minX + 10.0F), (int)(startY + 20.0F), -3421237);
         }
      }

      GL11.glPopMatrix();
      RenderUtil.endScissorBox();
      if (this.mousePressY != -1 && this.mousePressY != mouseY) {
         this.scroll((this.mousePressY - mouseY) * -10);
         this.mousePressY = mouseY;
      }

      RenderUtil.drawRoundedRect((double)((float)(resolution.getScaledWidth() / 2) - 55.0F), 5.0D, (double)((float)(resolution.getScaledWidth() / 2) + 55.0F), 25.0D, 5.0D, -6538948);
      RenderUtil.drawCenteredString(Client.getInstance().getMediumBoldFontRenderer(), "MULTIPLAYER", (float)(resolution.getScaledWidth() / 2), 15.0F, -524289);
      buttonHeight = 35.0F;
      float buttonWidth = 100.0F;
      float startMinX = (float)(resolution.getScaledWidth() / 2) - (buttonWidth + 12.5F) / 2.0F * 3.0F;
      minX = startMinX;
      startY = (float)resolution.getScaledHeight() - buttonHeight - 10.0F;

      for(int j = 0; j < 6; ++j) {
         int color = -12109771;
         int bColor = -7782075;
         if ((j == 0 || j == 3 || j == 4) && this.selectedData == null) {
            color = -12500928;
            bColor = -11382190;
         }

         RenderUtil.drawBorderedRoundedRect(minX, startY, minX + buttonWidth, startY + buttonHeight / 2.0F, 10.0F, 2.0F, bColor, color);
         String text;
         switch(j) {
         case 0:
            text = "JOIN SERVER";
            break;
         case 1:
            text = "DIRECT CONNECT";
            break;
         case 2:
            text = "ADD SERVER";
            break;
         case 3:
            text = "EDIT";
            break;
         case 4:
            text = "DELETE";
            break;
         default:
            text = "CANCEL";
         }

         RenderUtil.drawCenteredString(Client.getInstance().getRegularBoldFontRenderer(), text, (float)((int)(minX + buttonWidth / 2.0F)), (float)((int)(startY + buttonHeight / 4.0F) + 1), -1);
         minX += buttonWidth + 20.0F;
         if (j == 2) {
            startY += buttonHeight / 2.0F + 4.0F;
            minX = startMinX;
         }
      }

      if (this.field_146812_y != null) {
         this.func_146283_a(Lists.newArrayList(Splitter.on("\n").split(this.field_146812_y)), mouseX, mouseY);
      }

   }

   public void func_146796_h() {
   }

   private void func_146791_a(ServerData p_146791_1_) {
      if (this.field_146798_g instanceof GuiIngameMenu) {
         if (this.mc.theWorld != null) {
            this.mc.theWorld.sendQuittingDisconnectingPacket();
         }

         this.mc.loadWorld((WorldClient)null);
      }

      this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, p_146791_1_));
   }

   public void func_146790_a(int p_146790_1_) {
      this.field_146803_h.func_148192_c(p_146790_1_);
      GuiListExtended.IGuiListEntry var2 = p_146790_1_ < 0 ? null : this.field_146803_h.func_148180_b(p_146790_1_);
      this.field_146809_s.enabled = false;
      this.field_146810_r.enabled = false;
      this.field_146808_t.enabled = false;
      if (var2 != null && !(var2 instanceof ServerListEntryLanScan)) {
         this.field_146809_s.enabled = true;
      }

   }

   public OldServerPinger func_146789_i() {
      return this.field_146797_f;
   }

   public void func_146793_a(String p_146793_1_) {
      this.field_146812_y = p_146793_1_;
   }

   protected void mouseClicked(int mouseX, int mouseY, int button) {
      if (button <= 1) {
         ServerData selectedData = this.selectedData;
         this.selectedData = null;
         ScaledResolution resolution = new ScaledResolution(this.mc);
         float buttonHeight = 35.0F;
         float buttonWidth = 100.0F;
         float startMinX = (float)(resolution.getScaledWidth() / 2) - (buttonWidth + 12.5F) / 2.0F * 3.0F;
         float minX = startMinX;
         float startY = (float)resolution.getScaledHeight() - buttonHeight - 10.0F;

         for(int j = 0; j < 6; ++j) {
            if ((float)mouseX >= minX && (float)mouseX <= minX + buttonWidth && (float)mouseY >= startY && (float)mouseY <= startY + buttonHeight / 2.0F) {
               switch(j) {
               case 0:
                  if (selectedData != null) {
                     this.func_146791_a(selectedData);
                  }
                  break;
               case 1:
                  this.field_146813_x = true;
                  this.field_146811_z = new ServerData("Minecraft Server", "");
                  this.mc.displayGuiScreen(new GuiScreenServerList(this, this.field_146811_z));
                  break;
               case 2:
                  this.field_146806_v = true;
                  this.field_146811_z = new ServerData("Minecraft Server", "");
                  this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.field_146811_z));
                  break;
               case 3:
                  if (selectedData != null) {
                     this.field_146805_w = true;
                     this.field_146811_z = new ServerData(selectedData.serverName, selectedData.serverIP);
                     this.field_146811_z.func_152583_a(selectedData);
                     this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.field_146811_z));
                  }
                  break;
               case 4:
                  if (selectedData != null) {
                     String var9 = selectedData.serverName;
                     if (var9 != null) {
                        this.field_146807_u = true;
                        String var4 = "Are you sure you want to remove this server?";
                        String var5 = "'" + var9 + "' will be lost forever! (A long time!)";
                        String var6 = "Delete";
                        String var7 = "Cancel";
                        int id = this.field_146803_h.func_148193_k();
                        GuiYesNo var8 = new GuiYesNo(this, var4, var5, var6, var7, id);
                        this.mc.displayGuiScreen(var8);
                        this.deleting = selectedData;
                     }
                  }
                  break;
               case 5:
                  this.mc.displayGuiScreen(this.field_146798_g);
               }

               return;
            }

            minX += buttonWidth + 20.0F;
            if (j == 2) {
               startY += buttonHeight / 2.0F + 4.0F;
               minX = startMinX;
            }
         }

         float pinnedWidth = 70.0F;
         float startX = (float)(resolution.getScaledWidth() / 2) - 155.0F;

         for(Iterator var21 = this.field_146804_i.pinnedServers.iterator(); var21.hasNext(); startX += pinnedWidth + 10.0F) {
            ServerData serverData = (ServerData)var21.next();
            if ((float)mouseX >= startX + 25.0F && (float)mouseX <= startX + 45.0F && (float)mouseY >= 93.0F && (float)mouseY <= 113.0F) {
               this.func_146791_a(serverData);
               return;
            }
         }

         if (this.hoveringData != null) {
            if (selectedData == this.hoveringData && this.lastSelectTime + 500L > System.currentTimeMillis()) {
               this.func_146791_a(selectedData);
               return;
            }

            this.lastSelectTime = System.currentTimeMillis();
            this.selectedData = this.hoveringData;
         }

         this.mousePressY = mouseY;
      }
   }

   protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
      this.mousePressY = -1;
   }

   public ServerList func_146795_p() {
      return this.field_146804_i;
   }
}
