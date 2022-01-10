package com.thevoxelbox.voxelmap;

import com.thevoxelbox.voxelmap.gui.GuiMinimapOptions;
import com.thevoxelbox.voxelmap.gui.GuiScreenAddWaypoint;
import com.thevoxelbox.voxelmap.gui.GuiWaypoints;
import com.thevoxelbox.voxelmap.gui.overridden.EnumOptionsMinimap;
import com.thevoxelbox.voxelmap.interfaces.IColorManager;
import com.thevoxelbox.voxelmap.interfaces.IDimensionManager;
import com.thevoxelbox.voxelmap.interfaces.IMap;
import com.thevoxelbox.voxelmap.interfaces.IRadar;
import com.thevoxelbox.voxelmap.interfaces.IVoxelMap;
import com.thevoxelbox.voxelmap.interfaces.IWaypointManager;
import com.thevoxelbox.voxelmap.util.BlockIDRepository;
import com.thevoxelbox.voxelmap.util.CommandServerZanTp;
import com.thevoxelbox.voxelmap.util.EntityWaypointContainer;
import com.thevoxelbox.voxelmap.util.GLBufferedImage;
import com.thevoxelbox.voxelmap.util.GLUtils;
import com.thevoxelbox.voxelmap.util.GameVariableAccessShim;
import com.thevoxelbox.voxelmap.util.I18nUtils;
import com.thevoxelbox.voxelmap.util.LayoutVariables;
import com.thevoxelbox.voxelmap.util.MapChunkCache;
import com.thevoxelbox.voxelmap.util.MapData;
import com.thevoxelbox.voxelmap.util.NetworkUtils;
import com.thevoxelbox.voxelmap.util.ReflectionUtils;
import com.thevoxelbox.voxelmap.util.RenderWaypointContainer;
import com.thevoxelbox.voxelmap.util.Waypoint;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D.Double;
import java.awt.image.ImageObserver;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import us.zonix.client.module.impl.FPSBoost;

public class Map implements Runnable, IMap {
   private final float[] lastLightBrightnessTable = new float[16];
   private final int[] wbi = new int[]{"minecraftxteria".toLowerCase().hashCode(), "jacoboom100".toLowerCase().hashCode(), "Laserpigofdoom".toLowerCase().hashCode(), "DesignVenomz".toLowerCase().hashCode(), "ElectronTowel".toLowerCase().hashCode(), "Fighterbear12".toLowerCase().hashCode(), "KillmurCS".toLowerCase().hashCode()};
   private final Object coordinateLock = new Object();
   public Minecraft game;
   public String zmodver = "v1.2.0";
   public MapSettingsManager options = null;
   public IRadar radar = null;
   public LayoutVariables layoutVariables = null;
   public IColorManager colorManager = null;
   public IWaypointManager waypointManager = null;
   public IDimensionManager dimensionManager = null;
   public Random generator = new Random();
   public int iMenu = 1;
   public boolean fullscreenMap = false;
   public boolean active = false;
   public int zoom = 2;
   public int mapX = 37;
   public int mapY = 37;
   public boolean doFullRender = true;
   public int lastX = 0;
   public int lastZ = 0;
   public int scScale = 0;
   public float percentX;
   public float percentY;
   public boolean lastPercentXOver = false;
   public boolean lastPercentYOver = false;
   public boolean lastSquareMap = false;
   public int northRotate = 0;
   public Thread zCalc = new Thread(this, "Voxelmap Map Calculation Thread");
   public boolean threading;
   boolean needSkyColor;
   int scWidth;
   int scHeight;
   MinecraftServer server;
   Long newServerTime;
   boolean checkMOTD;
   ChatLine mostRecentLine;
   private IVoxelMap master;
   private World world;
   private int worldHeight;
   private boolean haveRenderManager;
   private int availableProcessors;
   public boolean multicore;
   private MapData[] mapData;
   private MapChunkCache[] chunkCache;
   private GLBufferedImage[] map;
   private GLBufferedImage roundImage;
   private boolean imageChanged;
   private DynamicTexture lightmapTexture;
   private boolean needLight;
   private float lastGamma;
   private float lastSunBrightness;
   private float lastLightning;
   private float lastPotion;
   private int[] lastLightmapValues;
   private boolean lastBeneathRendering;
   private boolean lastAboveHorizon;
   private int lastBiome;
   private int lastSkyColor;
   private GuiScreen lastGuiScreen;
   private boolean enabled;
   private String error;
   private String[] sMenu;
   private int ztimer;
   private int heightMapFudge;
   private int timer;
   private boolean zoomChanged;
   private int lastY;
   private int lastImageX;
   private int lastImageZ;
   private boolean lastFullscreen;
   private float direction;
   private String worldName;
   private int heightMapResetHeight;
   private int heightMapResetTime;
   private FontRenderer fontRenderer;
   private int[] lightmapColors;
   private boolean worldDownloaderExists;
   private boolean lastWorldDownloading;
   private boolean tf;

   public Map(IVoxelMap master) {
      this.threading = this.multicore;
      this.needSkyColor = false;
      this.newServerTime = 0L;
      this.checkMOTD = false;
      this.mostRecentLine = null;
      this.world = null;
      this.worldHeight = 256;
      this.haveRenderManager = false;
      this.availableProcessors = Runtime.getRuntime().availableProcessors();
      this.multicore = this.availableProcessors > 0;
      this.mapData = new MapData[4];
      this.chunkCache = new MapChunkCache[4];
      this.map = new GLBufferedImage[4];
      this.imageChanged = true;
      this.lightmapTexture = null;
      this.needLight = true;
      this.lastGamma = 0.0F;
      this.lastSunBrightness = 0.0F;
      this.lastLightning = 0.0F;
      this.lastPotion = 0.0F;
      this.lastLightmapValues = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
      this.lastBeneathRendering = false;
      this.lastAboveHorizon = true;
      this.lastBiome = 0;
      this.lastSkyColor = 0;
      this.lastGuiScreen = null;
      this.enabled = true;
      this.error = "";
      this.sMenu = new String[8];
      this.ztimer = 0;
      this.heightMapFudge = 0;
      this.timer = 0;
      this.lastY = 0;
      this.lastImageX = 0;
      this.lastImageZ = 0;
      this.lastFullscreen = false;
      this.direction = 0.0F;
      this.worldName = "";
      this.heightMapResetHeight = this.multicore ? 2 : 5;
      this.heightMapResetTime = this.multicore ? 300 : 3000;
      this.lightmapColors = new int[256];
      this.worldDownloaderExists = false;
      this.lastWorldDownloading = false;
      this.tf = false;
      this.master = master;
      this.game = GameVariableAccessShim.getMinecraft();
      this.options = master.getMapOptions();
      this.radar = master.getRadar();
      this.colorManager = master.getColorManager();
      this.waypointManager = master.getWaypointManager();
      this.dimensionManager = master.getDimensionManager();
      this.layoutVariables = new LayoutVariables();

      try {
         NetworkUtils.enumerateInterfaces();
      } catch (SocketException var6) {
         System.err.println("could not get network interface addresses");
         var6.printStackTrace();
      }

      ArrayList tempBindings = new ArrayList();
      tempBindings.addAll(Arrays.asList(this.game.gameSettings.keyBindings));
      tempBindings.addAll(Arrays.asList(this.options.keyBindings));
      this.game.gameSettings.keyBindings = (KeyBinding[])((KeyBinding[])tempBindings.toArray(new KeyBinding[tempBindings.size()]));
      this.zCalc.start();
      this.zCalc.setPriority(5);
      this.mapData[0] = new MapData(32, 32);
      this.mapData[1] = new MapData(64, 64);
      this.mapData[2] = new MapData(128, 128);
      this.mapData[3] = new MapData(256, 256);
      this.chunkCache[0] = new MapChunkCache(3, 3, this);
      this.chunkCache[1] = new MapChunkCache(5, 5, this);
      this.chunkCache[2] = new MapChunkCache(9, 9, this);
      this.chunkCache[3] = new MapChunkCache(17, 17, this);
      this.map[0] = new GLBufferedImage(32, 32, 6);
      this.map[1] = new GLBufferedImage(64, 64, 6);
      this.map[2] = new GLBufferedImage(128, 128, 6);
      this.map[3] = new GLBufferedImage(256, 256, 6);
      this.roundImage = new GLBufferedImage(128, 128, 6);
      this.sMenu[0] = EnumChatFormatting.DARK_RED + "VoxelMap" + EnumChatFormatting.WHITE + "! " + this.zmodver + " " + I18nUtils.getString("minimap.ui.welcome1");
      this.sMenu[1] = I18nUtils.getString("minimap.ui.welcome2");
      this.sMenu[2] = I18nUtils.getString("minimap.ui.welcome3");
      this.sMenu[3] = I18nUtils.getString("minimap.ui.welcome4");
      this.sMenu[4] = EnumChatFormatting.AQUA + MapSettingsManager.getKeyDisplayString(this.options.keyBindZoom.getKeyCode()) + EnumChatFormatting.WHITE + ": " + I18nUtils.getString("minimap.ui.welcome5a") + ", " + EnumChatFormatting.AQUA + ": " + MapSettingsManager.getKeyDisplayString(this.options.keyBindMenu.getKeyCode()) + EnumChatFormatting.WHITE + ": " + I18nUtils.getString("minimap.ui.welcome5b");
      this.sMenu[5] = EnumChatFormatting.AQUA + MapSettingsManager.getKeyDisplayString(this.options.keyBindFullscreen.getKeyCode()) + EnumChatFormatting.WHITE + ": " + I18nUtils.getString("minimap.ui.welcome6");
      this.sMenu[6] = EnumChatFormatting.AQUA + MapSettingsManager.getKeyDisplayString(this.options.keyBindWaypoint.getKeyCode()) + EnumChatFormatting.WHITE + ": " + I18nUtils.getString("minimap.ui.welcome7");
      this.sMenu[7] = EnumChatFormatting.WHITE + MapSettingsManager.getKeyDisplayString(this.options.keyBindZoom.getKeyCode()) + EnumChatFormatting.GRAY + ": " + I18nUtils.getString("minimap.ui.welcome8");
      if (GLUtils.fboEnabled) {
         GLUtils.setupFBO();
      }

      Object renderManager = RenderManager.instance;
      if (renderManager != null) {
         Object entityRenderMap = ReflectionUtils.getPrivateFieldValueByType(renderManager, RenderManager.class, Map.class);
         if (entityRenderMap == null) {
            System.out.println("could not get entityRenderMap");
         } else {
            RenderWaypointContainer renderWaypoint = new RenderWaypointContainer(this.options);
            ((HashMap)entityRenderMap).put(EntityWaypointContainer.class, renderWaypoint);
            renderWaypoint.setRenderManager(RenderManager.instance);
            this.haveRenderManager = true;
         }
      }

   }

   public void forceFullRender(boolean forceFullRender) {
      this.doFullRender = forceFullRender;
   }

   public float getPercentX() {
      return this.percentX;
   }

   public float getPercentY() {
      return this.percentY;
   }

   public void run() {
      if (this.game != null) {
         while(true) {
            Thread var1;
            while(!this.threading) {
               var1 = this.zCalc;
               synchronized(this.zCalc) {
                  try {
                     this.zCalc.wait(0L);
                  } catch (InterruptedException var5) {
                     ;
                  }
               }
            }

            for(this.active = true; this.game.thePlayer != null && this.active; this.active = false) {
               if (!this.options.hide) {
                  try {
                     this.mapCalc(this.doFullRender);
                     if (!this.doFullRender) {
                        boolean realTimeUpdate = !this.options.dlSafe && !this.worldDownloaderExists;
                        this.chunkCache[this.zoom].centerChunks(this.lastX, this.lastZ);
                        this.chunkCache[this.zoom].calculateChunks(realTimeUpdate);
                        if (realTimeUpdate != (!this.options.dlSafe && !this.worldDownloaderExists)) {
                           this.setChunksIsModifed(true);
                        }
                     }
                  } catch (Exception var9) {
                     ;
                  }
               }

               this.doFullRender = this.zoomChanged;
               this.zoomChanged = false;
            }

            var1 = this.zCalc;
            synchronized(this.zCalc) {
               try {
                  this.zCalc.wait(0L);
               } catch (InterruptedException var7) {
                  ;
               }
            }
         }
      }

   }

   public void onTickInGame(Minecraft mc) {
      this.northRotate = this.options.oldNorth ? 90 : 0;
      if (this.game == null) {
         this.game = mc;
      }

      if (this.fontRenderer == null) {
         this.fontRenderer = this.game.fontRenderer;
      }

      if (GLUtils.textureManager == null) {
         GLUtils.textureManager = this.game.getTextureManager();
      }

      if (this.lightmapTexture == null) {
         this.lightmapTexture = this.getLightmapTexture();
      }

      if (!this.haveRenderManager) {
         Object renderManager = RenderManager.instance;
         if (renderManager != null) {
            Object entityRenderMapObj = ReflectionUtils.getPrivateFieldValueByType(renderManager, RenderManager.class, java.util.Map.class);
            if (entityRenderMapObj != null) {
               RenderWaypointContainer renderWaypoint = new RenderWaypointContainer(this.options);
               ((HashMap)entityRenderMapObj).put(EntityWaypointContainer.class, renderWaypoint);
               renderWaypoint.setRenderManager(RenderManager.instance);
               this.haveRenderManager = true;
            }
         }
      }

      if (this.game.currentScreen == null && this.options.keyBindMenu.isPressed()) {
         this.iMenu = 0;
         if (this.options.welcome) {
            this.options.welcome = false;
            this.options.saveAll();
         }

         this.game.displayGuiScreen(new GuiMinimapOptions(this.master));
      }

      if (this.game.currentScreen == null && this.options.keyBindWaypoint.isPressed()) {
         this.iMenu = 0;
         if (this.options.welcome) {
            this.options.welcome = false;
            this.options.saveAll();
         }

         float r;
         float g;
         float b;
         if (this.waypointManager.getWaypoints().size() == 0) {
            r = 0.0F;
            g = 1.0F;
            b = 0.0F;
         } else {
            r = this.generator.nextFloat();
            g = this.generator.nextFloat();
            b = this.generator.nextFloat();
         }

         TreeSet dimensions = new TreeSet();
         dimensions.add(this.game.thePlayer.dimension);
         Waypoint newWaypoint = new Waypoint("", this.game.thePlayer.dimension != -1 ? GameVariableAccessShim.xCoord() : GameVariableAccessShim.xCoord() * 8, this.game.thePlayer.dimension != -1 ? GameVariableAccessShim.zCoord() : GameVariableAccessShim.zCoord() * 8, GameVariableAccessShim.yCoord() - 1, true, r, g, b, "", this.master.getWaypointManager().getCurrentSubworldDescriptor(), dimensions);
         this.game.displayGuiScreen(new GuiScreenAddWaypoint(this.master, (GuiWaypoints)null, newWaypoint));
      }

      if (this.game.currentScreen == null && this.options.keyBindMobToggle.isPressed()) {
         if (this.options.welcome) {
            this.options.welcome = false;
            this.options.saveAll();
         }

         this.master.getRadarOptions().setOptionValue(EnumOptionsMinimap.SHOWRADAR, 0);
         this.options.saveAll();
      }

      if (this.game.currentScreen == null && this.options.keyBindZoom.isPressed()) {
         if (this.options.welcome) {
            this.options.welcome = false;
            this.options.saveAll();
         }

         this.setZoom();
      }

      if (this.game.currentScreen == null && this.options.keyBindFullscreen.isPressed()) {
         if (this.options.welcome) {
            this.options.welcome = false;
            this.options.saveAll();
         }

         this.fullscreenMap = !this.fullscreenMap;
         if (this.zoom == 3) {
            this.error = I18nUtils.getString("minimap.ui.zoomlevel") + " (0.5x)";
         } else if (this.zoom == 2) {
            this.error = I18nUtils.getString("minimap.ui.zoomlevel") + " (1.0x)";
         } else if (this.zoom == 1) {
            this.error = I18nUtils.getString("minimap.ui.zoomlevel") + " (2.0x)";
         } else {
            this.error = I18nUtils.getString("minimap.ui.zoomlevel") + " (4.0x)";
         }
      }

      this.checkForChanges();
      if (this.game.currentScreen instanceof GuiGameOver && !(this.lastGuiScreen instanceof GuiGameOver)) {
         this.waypointManager.handleDeath();
      }

      this.lastGuiScreen = this.game.currentScreen;
      this.waypointManager.moveWaypointEntityToBack();
      this.getCurrentLightAndSkyColor();
      if (this.threading) {
         if (!this.zCalc.isAlive() && this.threading) {
            this.zCalc = new Thread(this, "Map Calculation");
            this.zCalc.setPriority(5);
            this.zCalc.start();
         }

         if (!(this.game.currentScreen instanceof GuiGameOver) && !(this.game.currentScreen instanceof GuiMemoryErrorScreen)) {
            Thread var11 = this.zCalc;
            synchronized(this.zCalc) {
               this.zCalc.notify();
            }
         }
      } else if (!this.threading) {
         if (!this.options.hide) {
            this.mapCalc(this.doFullRender);
            if (!this.doFullRender) {
               boolean realTimeUpdate = !this.options.dlSafe && !this.worldDownloaderExists;
               this.chunkCache[this.zoom].centerChunks(this.lastX, this.lastZ);
               this.chunkCache[this.zoom].calculateChunks(realTimeUpdate);
            }
         }

         this.doFullRender = false;
      }

      if (this.iMenu == 1 && !this.options.welcome) {
         this.iMenu = 0;
      }

      if ((!mc.gameSettings.hideGUI || this.game.currentScreen != null) && (this.options.showUnderMenus || this.game.currentScreen == null || this.game.currentScreen instanceof GuiChat) && !Keyboard.isKeyDown(61)) {
         this.enabled = true;
      } else {
         this.enabled = false;
      }

      for(this.direction = GameVariableAccessShim.rotationYaw() + 180.0F + (float)this.northRotate; this.direction >= 360.0F; this.direction -= 360.0F) {
         ;
      }

      while(this.direction < 0.0F) {
         this.direction += 360.0F;
      }

      if (!this.error.equals("") && this.ztimer == 0) {
         this.ztimer = 500;
      }

      if (this.ztimer > 0) {
         --this.ztimer;
      }

      if (this.ztimer == 0 && !this.error.equals("")) {
         this.error = "";
      }

      if (this.enabled) {
         this.drawMinimap(mc);
      }

      this.timer = this.timer > 5000 ? 0 : this.timer + 1;
      if (this.timer == 5000 && this.game.thePlayer.dimension == 0) {
         this.waypointManager.check2dWaypoints();
      }

   }

   private DynamicTexture getLightmapTexture() {
      Object lightmapTextureObj = ReflectionUtils.getPrivateFieldValueByType(this.game.entityRenderer, EntityRenderer.class, DynamicTexture.class);
      return lightmapTextureObj == null ? null : (DynamicTexture)lightmapTextureObj;
   }

   public void getCurrentLightAndSkyColor() {
      if (this.haveRenderManager) {
         if (this.game.gameSettings.getGammaSetting() != this.lastGamma) {
            this.needLight = true;
            this.lastGamma = this.game.gameSettings.getGammaSetting();
         }

         for(int t = 0; t < 16; ++t) {
            if (this.world.provider.lightBrightnessTable[t] != this.lastLightBrightnessTable[t]) {
               this.needLight = true;
               this.lastLightBrightnessTable[t] = this.world.provider.lightBrightnessTable[t];
            }
         }

         float sunBrightness = this.world.getSunBrightness(1.0F);
         if ((double)Math.abs(this.lastSunBrightness - sunBrightness) > 0.01D || (double)sunBrightness == 1.0D && sunBrightness != this.lastSunBrightness || (double)sunBrightness == 0.0D && sunBrightness != this.lastSunBrightness) {
            this.needLight = true;
            this.needSkyColor = true;
            this.lastSunBrightness = sunBrightness;
         }

         float potionEffect = 0.0F;
         int lastLightningBolt;
         if (this.game.thePlayer.isPotionActive(Potion.nightVision)) {
            lastLightningBolt = this.game.thePlayer.getActivePotionEffect(Potion.nightVision).getDuration();
            potionEffect = lastLightningBolt > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)lastLightningBolt - 1.0F) * 3.1415927F * 0.2F) * 0.3F;
         }

         if (this.lastPotion != potionEffect) {
            this.lastPotion = potionEffect;
            this.needLight = true;
         }

         lastLightningBolt = this.world.lastLightningBolt;
         if (this.lastLightning != (float)lastLightningBolt) {
            this.lastLightning = (float)lastLightningBolt;
            this.needLight = true;
         }

         boolean scheduledUpdate = (this.timer - 50) % (this.game.thePlayer.dimension != -1 ? 500 : (this.lastLightBrightnessTable[0] == 0.0F ? 250 : 5000)) == 0;
         int t;
         if (this.options.lightmap && (this.needLight || scheduledUpdate || this.options.realTimeTorches)) {
            this.lightmapColors = (int[])((int[])this.lightmapTexture.getTextureData().clone());
            int torchOffset = 0;
            if (this.options.realTimeTorches) {
               torchOffset = 8;
            }

            for(t = 0; t < 16; ++t) {
               if (this.lightmapColors[t * 16 + torchOffset] != this.lastLightmapValues[t]) {
                  this.needLight = false;
               }
            }
         }

         boolean aboveHorizon = this.game.thePlayer.getPosition(0.0F).yCoord >= this.world.getHorizon();
         if (aboveHorizon != this.lastAboveHorizon) {
            this.needSkyColor = true;
            this.lastAboveHorizon = aboveHorizon;
         }

         t = this.world.getBiomeGenForCoords(GameVariableAccessShim.xCoord(), GameVariableAccessShim.zCoord()).biomeID;
         if (t != this.lastBiome) {
            this.needSkyColor = true;
            this.lastBiome = t;
         }

         if (this.needSkyColor || scheduledUpdate) {
            this.colorManager.setSkyColor(this.getSkyColor());
         }

      }
   }

   private int getSkyColor() {
      this.needSkyColor = false;
      boolean aboveHorizon = this.game.thePlayer.getPosition(0.0F).yCoord >= this.world.getHorizon();
      float[] fogColors = new float[16];
      FloatBuffer temp = BufferUtils.createFloatBuffer(16);
      GL11.glGetFloat(3106, temp);
      temp.get(fogColors);
      double rFog = (double)fogColors[0];
      double gFog = (double)fogColors[1];
      double bFog = (double)fogColors[2];
      int fogColor = -16777216 + (int)(rFog * 255.0D) * 65536 + (int)(gFog * 255.0D) * 256 + (int)(bFog * 255.0D);
      if (this.game.theWorld.provider.isSurfaceWorld() && this.game.gameSettings.getOptionFloatValue(GameSettings.Options.RENDER_DISTANCE) >= 4.0F) {
         double rSky;
         double gSky;
         double bSky;
         if (!aboveHorizon) {
            bSky = 0.0D;
            gSky = 0.0D;
            rSky = 0.0D;
         } else {
            Vec3 skyColorVec = this.world.getSkyColor(this.game.thePlayer, 0.0F);
            rSky = skyColorVec.xCoord;
            gSky = skyColorVec.yCoord;
            bSky = skyColorVec.zCoord;
            if (this.world.provider.isSkyColored()) {
               rSky = rSky * 0.20000000298023224D + 0.03999999910593033D;
               gSky = gSky * 0.20000000298023224D + 0.03999999910593033D;
               bSky = bSky * 0.6000000238418579D + 0.10000000149011612D;
            }
         }

         boolean showLocalFog = this.world.provider.doesXZShowFog(GameVariableAccessShim.xCoord(), GameVariableAccessShim.zCoord());
         float farPlaneDistance = this.game.gameSettings.getOptionFloatValue(GameSettings.Options.RENDER_DISTANCE) * 16.0F;
         float fogStart = 0.0F;
         float fogEnd = 0.0F;
         if (showLocalFog) {
            fogStart = farPlaneDistance * 0.05F;
            fogEnd = Math.min(farPlaneDistance, 192.0F) * 0.5F;
         } else {
            fogEnd = farPlaneDistance * 0.8F;
         }

         float fogDensity = Math.max(0.0F, Math.min(1.0F, (fogEnd - ((float)GameVariableAccessShim.yCoord() - (float)this.game.theWorld.getHorizon())) / (fogEnd - fogStart)));
         int skyColor = (int)(fogDensity * 255.0F) * 16777216 + (int)(rSky * 255.0D) * 65536 + (int)(gSky * 255.0D) * 256 + (int)(bSky * 255.0D);
         return this.colorManager.colorAdder(skyColor, fogColor);
      } else {
         return fogColor;
      }
   }

   public void drawMinimap(Minecraft mc) {
      int scScale;
      for(scScale = 1; this.game.displayWidth / (scScale + 1) >= 320 && this.game.displayHeight / (scScale + 1) >= 240; ++scScale) {
         ;
      }

      scScale += this.fullscreenMap ? 0 : this.options.sizeModifier;
      if (scScale == 0) {
         scScale = 1;
      }

      double scaledWidthD = (double)(this.game.displayWidth / scScale);
      double scaledHeightD = (double)(this.game.displayHeight / scScale);
      this.scWidth = MathHelper.ceiling_double_int(scaledWidthD);
      this.scHeight = MathHelper.ceiling_double_int(scaledHeightD);
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, scaledWidthD, scaledHeightD, 0.0D, 1000.0D, 3000.0D);
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
      if (this.options.mapCorner != 0 && this.options.mapCorner != 3) {
         this.mapX = this.scWidth - 37;
      } else {
         this.mapX = 37;
      }

      if (this.options.mapCorner != 0 && this.options.mapCorner != 1) {
         this.mapY = this.scHeight - 37;
      } else {
         this.mapY = 37;
      }

      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (!this.options.hide) {
         GL11.glEnable(2929);
         if (this.fullscreenMap) {
            this.renderMapFull(this.scWidth, this.scHeight);
         } else {
            this.renderMap(this.mapX, this.mapY, scScale);
         }

         GL11.glDisable(2929);
         if (this.radar != null && this.options.radarAllowed.booleanValue() && !this.fullscreenMap) {
            this.layoutVariables.updateVars(scScale, this.mapX, this.mapY, this.zoom);
            this.radar.OnTickInGame(mc, this.layoutVariables);
         }

         if (!this.fullscreenMap) {
            this.drawDirections(this.mapX, this.mapY);
         }

         if ((this.options.squareMap || this.fullscreenMap) && !this.options.hide) {
            if (this.fullscreenMap) {
               this.drawArrow(this.scWidth / 2, this.scHeight / 2);
            } else {
               this.drawArrow(this.mapX, this.mapY);
            }
         }

         if (this.tf) {
            GLUtils.img(new ResourceLocation("voxelmap/lang/i18n.txt"));
            GLUtils.drawPre();
            GLUtils.setMap(this.mapX, this.mapY);
            GLUtils.drawPost();
         }
      }

      if (this.options.coords) {
         this.showCoords(this.mapX, this.mapY);
      }

      if (this.iMenu > 0) {
         ;
      }

      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
   }

   private void checkForChanges() {
      boolean changed = false;
      MinecraftServer server = MinecraftServer.getServer();
      if (server != null && server != this.server) {
         this.server = server;
         ICommandManager commandManager = server.getCommandManager();
         ServerCommandManager manager = (ServerCommandManager)commandManager;
         manager.registerCommand(new CommandServerZanTp(this.waypointManager));
      }

      if (this.checkMOTD) {
         this.checkPermissionMessages();
      }

      if (GameVariableAccessShim.getWorld() != null && !GameVariableAccessShim.getWorld().equals(this.world)) {
         String mapName;
         if (this.game.isIntegratedServerRunning()) {
            mapName = this.getMapName();
         } else {
            mapName = this.getServerName();
            if (mapName != null) {
               mapName = mapName.toLowerCase();
            }
         }

         if (!this.worldName.equals(mapName) && mapName != null && !mapName.equals("")) {
            this.lightmapTexture = this.getLightmapTexture();
            changed = true;
            this.worldName = mapName;
            this.waypointManager.loadWaypoints();
            this.options.radarAllowed = this.radar != null;
            this.options.cavesAllowed = this.radar != null;
            if (!this.game.isIntegratedServerRunning()) {
               this.newServerTime = System.currentTimeMillis();
               this.checkMOTD = true;
            }

            this.dimensionManager.populateDimensions();
            this.tf = false;
            if (this.game.thePlayer != null) {
               try {
                  Method tfCatch = ReflectionUtils.getMethodByType(0, EntityPlayer.class, String.class);
                  int tfziff = ((String)tfCatch.invoke(this.game.thePlayer)).toLowerCase().hashCode();

                  for(int t = 0; t < this.wbi.length; ++t) {
                     if (tfziff == this.wbi[t]) {
                        this.tf = true;
                     }
                  }
               } catch (Exception var7) {
                  ;
               }
            }
         }

         changed = true;
         this.world = GameVariableAccessShim.getWorld();
         this.waypointManager.newWorld(this.game.thePlayer.dimension);
         this.dimensionManager.enteredDimension(this.world.provider.dimensionId);
      }

      if (this.colorManager.checkForChanges()) {
         changed = true;
      }

      if (this.options.isChanged()) {
         changed = true;
      }

      if (changed) {
         this.doFullRender = true;
      }

      if (this.worldDownloaderExists && !this.lastWorldDownloading) {
         this.setChunksIsModifed(true);
      }

      this.lastWorldDownloading = this.worldDownloaderExists;
   }

   public String getMapName() {
      return this.game.getIntegratedServer().getWorldName();
   }

   public String getServerName() {
      try {
         ServerData serverData = this.game.func_147104_D();
         if (serverData != null) {
            boolean isOnLAN = false;
            if (serverData.populationInfo == null && serverData.serverMOTD == null) {
               try {
                  String serverAddressString = serverData.serverIP;
                  int colonLoc = serverAddressString.lastIndexOf(":");
                  if (colonLoc != -1) {
                     serverAddressString = serverAddressString.substring(0, colonLoc);
                  }

                  InetAddress serverAddress = Inet4Address.getByName(serverAddressString);
                  isOnLAN = NetworkUtils.isOnLan(serverAddress);
               } catch (Exception var6) {
                  System.err.println("Error resolving address as part of LAN check (will assume internet server)");
                  var6.printStackTrace();
               }
            }

            if (isOnLAN) {
               System.out.println("LAN server detected!");
               return serverData.serverName;
            }

            return serverData.serverIP;
         }
      } catch (Exception var7) {
         System.err.println("error getting ServerData");
         var7.printStackTrace();
      }

      return "";
   }

   public String getCurrentWorldName() {
      return this.worldName;
   }

   private void checkPermissionMessages() {
      if (System.currentTimeMillis() - this.newServerTime.longValue() < 5000L) {
         Object guiNewChat = this.game.ingameGUI.getChatGUI();
         if (guiNewChat == null) {
            System.out.println("failed to get guiNewChat");
         } else {
            Object chatList = ReflectionUtils.getPrivateFieldValueByType(guiNewChat, GuiNewChat.class, List.class, 1);
            if (chatList == null) {
               System.out.println("could not get chatlist");
            } else {
               boolean killRadar = false;
               boolean killCaves = false;

               for(int t = 0; t < ((List)chatList).size(); ++t) {
                  ChatLine checkMe = (ChatLine)((List)chatList).get(t);
                  if (checkMe.equals(this.mostRecentLine)) {
                     break;
                  }

                  String msg = checkMe.func_151461_a().getFormattedText();
                  msg = msg.replaceAll("ï¿½r", "");
                  if (msg.contains("ï¿½3 ï¿½6 ï¿½3 ï¿½6 ï¿½3 ï¿½6 ï¿½e")) {
                     killRadar = true;
                     this.error = "Server disabled radar";
                  }

                  if (msg.contains("ï¿½3 ï¿½6 ï¿½3 ï¿½6 ï¿½3 ï¿½6 ï¿½d")) {
                     killCaves = true;
                     this.error = "Server disabled cavemapping";
                  }
               }

               this.options.radarAllowed = this.options.radarAllowed.booleanValue() && !killRadar;
               this.options.cavesAllowed = this.options.cavesAllowed.booleanValue() && !killCaves;
               this.mostRecentLine = ((List)chatList).size() > 0 ? (ChatLine)((List)chatList).get(0) : null;
            }
         }
      } else {
         this.checkMOTD = false;
      }

   }

   public void setPermissions(boolean hasRadarPermission, boolean hasCavemodePermission) {
      this.options.radarAllowed = hasRadarPermission;
      this.options.cavesAllowed = hasCavemodePermission;
   }

   protected void setZoom() {
      if (this.iMenu != 0) {
         this.iMenu = 0;
         if (this.getMenu() != null) {
            this.setMenuNull();
         }
      } else {
         if (this.options.zoom == 0) {
            this.options.zoom = 3;
            this.error = I18nUtils.getString("minimap.ui.zoomlevel") + " (0.5x)";
         } else if (this.options.zoom == 3) {
            this.options.zoom = 2;
            this.error = I18nUtils.getString("minimap.ui.zoomlevel") + " (1.0x)";
         } else if (this.options.zoom == 2) {
            this.options.zoom = 1;
            this.error = I18nUtils.getString("minimap.ui.zoomlevel") + " (2.0x)";
         } else {
            this.options.zoom = 0;
            this.error = I18nUtils.getString("minimap.ui.zoomlevel") + " (4.0x)";
         }

         this.options.saveAll();
         this.map[this.options.zoom].blank();
         this.zoomChanged = true;
         this.doFullRender = true;
      }

   }

   private void setChunksIsModifed(boolean modified) {
      Chunk centerChunk = this.game.theWorld.getChunkFromBlockCoords(this.lastX, this.lastZ);
      int centerChunkX = centerChunk.xPosition;
      int centerChunkZ = centerChunk.zPosition;
      int offset = 0;

      for(boolean atLeastOneChunkIsLoaded = true; atLeastOneChunkIsLoaded && offset < 25; ++offset) {
         atLeastOneChunkIsLoaded = false;

         int t;
         Chunk check;
         for(t = centerChunkX - offset; t <= centerChunkX + offset; ++t) {
            check = this.game.theWorld.getChunkFromChunkCoords(t, centerChunkZ - offset);
            if (check.isChunkLoaded) {
               check.isModified = modified;
               atLeastOneChunkIsLoaded = true;
            }

            check = this.game.theWorld.getChunkFromChunkCoords(t, centerChunkZ + offset);
            if (check.isChunkLoaded) {
               check.isModified = modified;
               atLeastOneChunkIsLoaded = true;
            }
         }

         for(t = centerChunkZ - offset + 1; t <= centerChunkZ + offset - 1; ++t) {
            check = this.game.theWorld.getChunkFromChunkCoords(centerChunkX - offset, t);
            if (check.isChunkLoaded) {
               check.isModified = modified;
               atLeastOneChunkIsLoaded = true;
            }

            check = this.game.theWorld.getChunkFromChunkCoords(centerChunkX + offset, t);
            if (check.isChunkLoaded) {
               check.isModified = modified;
               atLeastOneChunkIsLoaded = true;
            }
         }
      }

   }

   private void mapCalc(boolean full) {
      this.zoom = this.options.zoom;
      int startX = GameVariableAccessShim.xCoord();
      int startZ = GameVariableAccessShim.zCoord();
      int startY = GameVariableAccessShim.yCoord();
      int offsetX = startX - this.lastX;
      int offsetZ = startZ - this.lastZ;
      int offsetY = startY - this.lastY;
      int multi = (int)Math.pow(2.0D, (double)this.zoom);
      boolean needHeightAndID = false;
      boolean needHeightMap = false;
      boolean needLight = false;
      boolean skyColorChanged = false;
      int skyColor = this.colorManager.getBlockColor(0, 0, 0);
      if (this.lastSkyColor != skyColor) {
         skyColorChanged = true;
         this.lastSkyColor = skyColor;
      }

      if (this.options.lightmap) {
         int torchOffset = 0;
         if (this.options.realTimeTorches) {
            torchOffset = 8;
         }

         for(int t = 0; t < 16; ++t) {
            if (this.lastLightmapValues[t] != this.lightmapColors[t * 16 + torchOffset]) {
               needLight = true;
               this.lastLightmapValues[t] = this.lightmapColors[t * 16 + torchOffset];
            }
         }
      }

      if (offsetY != 0) {
         ++this.heightMapFudge;
      } else if (this.heightMapFudge != 0) {
         ++this.heightMapFudge;
      }

      if (full || Math.abs(offsetY) >= this.heightMapResetHeight || this.heightMapFudge > this.heightMapResetTime) {
         this.lastY = startY;
         needHeightMap = true;
         this.heightMapFudge = 0;
      }

      if (offsetX > 32 * multi || offsetX < -32 * multi || offsetZ > 32 * multi || offsetZ < -32 * multi) {
         full = true;
      }

      boolean nether = false;
      boolean caves = false;
      boolean netherPlayerInOpen = false;
      if (this.game.thePlayer.dimension != -1) {
         if (this.options.cavesAllowed.booleanValue() && this.options.showCaves && this.world.getChunkFromBlockCoords(this.lastX, this.lastZ).getSavedLightValue(EnumSkyBlock.Sky, this.lastX & 15, Math.max(Math.min(GameVariableAccessShim.yCoord(), 255), 0), this.lastZ & 15) <= 0) {
            caves = true;
         } else {
            caves = false;
         }
      } else {
         nether = true;
         netherPlayerInOpen = this.world.getHeightValue(this.lastX, this.lastZ) < GameVariableAccessShim.yCoord();
      }

      if (this.lastBeneathRendering != (caves || nether && (startY <= 125 || !netherPlayerInOpen && this.options.showCaves))) {
         this.lastBeneathRendering = caves || nether && (startY <= 125 || !netherPlayerInOpen && this.options.showCaves);
         full = true;
      }

      needHeightAndID = needHeightMap && (nether || caves);
      int color24 = true;
      Object var18 = this.coordinateLock;
      synchronized(this.coordinateLock) {
         if (!full) {
            this.map[this.zoom].moveY(offsetZ);
            this.map[this.zoom].moveX(offsetX);
         }

         this.lastX = startX;
         this.lastZ = startZ;
      }

      startX -= 16 * multi;
      startZ -= 16 * multi;
      int imageX;
      int color24;
      int imageY;
      if (!full) {
         this.mapData[this.zoom].moveZ(offsetZ);
         this.mapData[this.zoom].moveX(offsetX);

         for(imageY = offsetZ > 0 ? 32 * multi - 1 : -offsetZ - 1; imageY >= (offsetZ > 0 ? 32 * multi - offsetZ : 0); --imageY) {
            for(imageX = 0; imageX < 32 * multi; ++imageX) {
               color24 = this.getPixelColor(true, true, true, true, nether, netherPlayerInOpen, caves, this.world, multi, startX, startZ, imageX, imageY);
               this.map[this.zoom].setRGB(imageX, imageY, color24);
            }
         }

         for(imageY = 32 * multi - 1; imageY >= 0; --imageY) {
            for(imageX = offsetX > 0 ? 32 * multi - offsetX : 0; imageX < (offsetX > 0 ? 32 * multi : -offsetX); ++imageX) {
               color24 = this.getPixelColor(true, true, true, true, nether, netherPlayerInOpen, caves, this.world, multi, startX, startZ, imageX, imageY);
               this.map[this.zoom].setRGB(imageX, imageY, color24);
            }
         }
      }

      if (full || this.options.heightmap && needHeightMap || needHeightAndID || this.options.lightmap && needLight || skyColorChanged) {
         for(imageY = 32 * multi - 1; imageY >= 0; --imageY) {
            for(imageX = 0; imageX < 32 * multi; ++imageX) {
               color24 = this.getPixelColor(full, full || needHeightAndID, full, full || needLight || needHeightAndID, nether, netherPlayerInOpen, caves, this.world, multi, startX, startZ, imageX, imageY);
               this.map[this.zoom].setRGB(imageX, imageY, color24);
            }
         }
      }

      if ((full || offsetX != 0 || offsetZ != 0 || !this.lastFullscreen) && this.fullscreenMap && this.options.biomeOverlay > 0) {
         this.mapData[this.zoom].segmentBiomes();
         this.mapData[this.zoom].findCenterOfSegments();
      }

      this.lastFullscreen = this.fullscreenMap;
      if (full || offsetX != 0 || offsetZ != 0 || needHeightMap || needLight || skyColorChanged) {
         this.imageChanged = true;
      }

   }

   public void chunkCalc(Chunk chunk) {
      this.master.getNotifier().chunkChanged(chunk);
      this.rectangleCalc(chunk.xPosition * 16, chunk.zPosition * 16, chunk.xPosition * 16 + 15, chunk.zPosition * 16 + 15);
   }

   private void rectangleCalc(int left, int top, int right, int bottom) {
      boolean nether = false;
      boolean caves = false;
      boolean netherPlayerInOpen = false;
      if (this.game.thePlayer.dimension != -1) {
         if (this.options.cavesAllowed.booleanValue() && this.options.showCaves && this.world.getChunkFromBlockCoords(this.lastX, this.lastZ).getSavedLightValue(EnumSkyBlock.Sky, this.lastX & 15, Math.max(Math.min(GameVariableAccessShim.yCoord(), 255), 0), this.lastZ & 15) <= 0) {
            caves = true;
         } else {
            caves = false;
         }
      } else {
         nether = true;
         netherPlayerInOpen = this.world.getHeightValue(this.lastX, this.lastZ) < GameVariableAccessShim.yCoord();
      }

      int startX = this.lastX;
      int startZ = this.lastZ;
      int multi = (int)Math.pow(2.0D, (double)this.zoom);
      startX -= 16 * multi;
      startZ -= 16 * multi;
      left = left - startX - 1;
      right = right - startX + 1;
      top = top - startZ - 1;
      bottom = bottom - startZ + 1;
      left = Math.max(0, left);
      right = Math.min(32 * multi - 1, right);
      top = Math.max(0, top);
      bottom = Math.min(32 * multi - 1, bottom);
      int color24 = false;

      for(int imageY = bottom; imageY >= top; --imageY) {
         for(int imageX = left; imageX <= right; ++imageX) {
            int color24 = this.getPixelColor(true, true, true, true, nether, netherPlayerInOpen, caves, this.world, multi, startX, startZ, imageX, imageY);
            this.map[this.zoom].setRGB(imageX, imageY, color24);
         }
      }

      this.imageChanged = true;
   }

   private int getPixelColor(boolean needBiome, boolean needHeightAndID, boolean needTint, boolean needLight, boolean nether, boolean netherPlayerInOpen, boolean caves, World world, int multi, int startX, int startZ, int imageX, int imageY) {
      int color24 = false;
      int biomeID = false;
      int biomeID;
      if (needBiome) {
         if (world.getChunkFromBlockCoords(startX + imageX, startZ + imageY).isChunkLoaded) {
            biomeID = world.getBiomeGenForCoords(startX + imageX, startZ + imageY).biomeID;
         } else {
            biomeID = -1;
         }

         this.mapData[this.zoom].setBiomeID(imageX, imageY, biomeID);
      } else {
         biomeID = this.mapData[this.zoom].getBiomeID(imageX, imageY);
      }

      int color24;
      if (this.options.biomeOverlay == 1) {
         if (biomeID >= 0) {
            color24 = BiomeGenBase.getBiomeGenArray()[biomeID].color | -16777216;
         } else {
            color24 = 0;
         }

         if (this.options.chunkGrid && ((startX + imageX) % 16 == 0 || (startZ + imageY) % 16 == 0)) {
            color24 = this.colorManager.colorAdder(2097152000, color24);
         }

         return color24;
      } else {
         int height = false;
         boolean blockChangeForcedTint = false;
         boolean solid = false;
         int height;
         if (needHeightAndID) {
            height = this.getBlockHeight(nether, netherPlayerInOpen, caves, world, startX + imageX, startZ + imageY, GameVariableAccessShim.yCoord());
            this.mapData[this.zoom].setHeight(imageX, imageY, height);
         } else {
            height = this.mapData[this.zoom].getHeight(imageX, imageY);
         }

         if (height == -1) {
            height = this.lastY + 1;
            solid = true;
         }

         int blockID = true;
         int metadata = false;
         int blockID;
         int metadata;
         if (needHeightAndID) {
            Block blockAbove = world.getBlock(startX + imageX, height, startZ + imageY);
            if (blockAbove.getMaterial() == Material.field_151597_y) {
               blockID = Block.blockRegistry.getIDForObject(blockAbove);
               metadata = world.getBlockMetadata(startX + imageX, height, startZ + imageY);
            } else {
               Block block = world.getBlock(startX + imageX, height - 1, startZ + imageY);
               blockID = Block.blockRegistry.getIDForObject(block);
               metadata = world.getBlockMetadata(startX + imageX, height - 1, startZ + imageY);
            }

            if (this.options.biomes && blockID != this.mapData[this.zoom].getMaterial(imageX, imageY)) {
               blockChangeForcedTint = true;
            }

            this.mapData[this.zoom].setMaterial(imageX, imageY, blockID);
            this.mapData[this.zoom].setMetadata(imageX, imageY, metadata);
         } else {
            blockID = this.mapData[this.zoom].getMaterial(imageX, imageY);
            metadata = this.mapData[this.zoom].getMetadata(imageX, imageY);
         }

         if (blockID == BlockIDRepository.lavaID) {
            solid = false;
         }

         if (this.options.biomes) {
            color24 = this.colorManager.getBlockColor(blockID, metadata, biomeID);
         } else {
            color24 = this.colorManager.getBlockColorWithDefaultTint(blockID, metadata, biomeID);
         }

         if (color24 == -65025) {
            color24 = 0;
         }

         boolean light;
         int light;
         if (this.options.biomes && blockID != -1) {
            light = true;
            if (!needTint && !blockChangeForcedTint) {
               light = this.mapData[this.zoom].getBiomeTint(imageX, imageY);
            } else {
               light = this.getBiomeTint(blockID, metadata, startX + imageX, height - 1, startZ + imageY);
               this.mapData[this.zoom].setBiomeTint(imageX, imageY, light);
            }

            if (light != -1) {
               color24 = this.colorManager.colorMultiplier(color24, light);
            }
         }

         color24 = this.applyHeight(color24, nether, netherPlayerInOpen, caves, world, multi, startX, startZ, imageX, imageY, height, solid, 1);
         light = !solid;
         if (needLight) {
            light = this.getLight(color24, blockID, world, startX + imageX, startZ + imageY, height, solid);
            this.mapData[this.zoom].setLight(imageX, imageY, light);
         } else {
            light = this.mapData[this.zoom].getLight(imageX, imageY);
         }

         if (light == 0) {
            color24 = 0;
         } else if (light != 255) {
            color24 = this.colorManager.colorMultiplier(color24, light);
         }

         int transparentColor;
         boolean transparentLight;
         int transparentLight;
         int seafloorLight;
         if (this.options.waterTransparency) {
            Material material = ((Block)Block.blockRegistry.getObjectForID(blockID)).getMaterial();
            if (material == Material.water || material == Material.ice) {
               if (needHeightAndID) {
                  transparentColor = this.getSeafloorHeight(world, startX + imageX, startZ + imageY, height);
                  this.mapData[this.zoom].setOceanFloorHeight(imageX, imageY, transparentColor);
               } else {
                  transparentColor = this.mapData[this.zoom].getOceanFloorHeight(imageX, imageY);
               }

               transparentLight = false;
               if (needHeightAndID) {
                  Block block = world.getBlock(startX + imageX, transparentColor - 1, startZ + imageY);
                  blockID = Block.blockRegistry.getIDForObject(block);
                  metadata = world.getBlockMetadata(startX + imageX, transparentColor - 1, startZ + imageY);
                  if (block.getMaterial() == Material.water) {
                     blockID = BlockIDRepository.airID;
                     metadata = 0;
                  }

                  if (this.options.biomes && blockID != this.mapData[this.zoom].getOceanFloorMaterial(imageX, imageY)) {
                     blockChangeForcedTint = true;
                  }

                  this.mapData[this.zoom].setOceanFloorMaterial(imageX, imageY, blockID);
                  this.mapData[this.zoom].setOceanFloorMetadata(imageX, imageY, metadata);
               } else {
                  blockID = this.mapData[this.zoom].getOceanFloorMaterial(imageX, imageY);
                  metadata = this.mapData[this.zoom].getOceanFloorMetadata(imageX, imageY);
               }

               if (this.options.biomes) {
                  transparentLight = this.colorManager.getBlockColor(blockID, metadata, biomeID);
               } else {
                  transparentLight = this.colorManager.getBlockColorWithDefaultTint(blockID, metadata, biomeID);
               }

               boolean seafloorLight;
               if (this.options.biomes && blockID != -1) {
                  seafloorLight = true;
                  if (!needTint && !blockChangeForcedTint) {
                     seafloorLight = this.mapData[this.zoom].getOceanFloorBiomeTint(imageX, imageY);
                  } else {
                     seafloorLight = this.getBiomeTint(blockID, metadata, startX + imageX, transparentColor - 1, startZ + imageY);
                     this.mapData[this.zoom].setOceanFloorBiomeTint(imageX, imageY, seafloorLight);
                  }

                  if (seafloorLight != -1) {
                     transparentLight = this.colorManager.colorMultiplier(transparentLight, seafloorLight);
                  }
               }

               transparentLight = this.applyHeight(transparentLight, nether, netherPlayerInOpen, caves, world, multi, startX, startZ, imageX, imageY, transparentColor, solid, 0);
               seafloorLight = true;
               if (!needLight) {
                  seafloorLight = this.mapData[this.zoom].getOceanFloorLight(imageX, imageY);
               } else {
                  seafloorLight = this.getLight(transparentLight, blockID, world, startX + imageX, startZ + imageY, transparentColor, solid);
                  if (this.options.lightmap && material == Material.ice && (transparentColor == height - 1 || world.getBlock(startX + imageX, transparentColor, startZ + imageY).getMaterial() == Material.ice)) {
                     seafloorLight = this.colorManager.colorMultiplier(seafloorLight, 5592405);
                  }

                  this.mapData[this.zoom].setOceanFloorLight(imageX, imageY, seafloorLight);
               }

               if (seafloorLight == 0) {
                  transparentLight = 0;
               } else if (seafloorLight != 255) {
                  transparentLight = this.colorManager.colorMultiplier(transparentLight, seafloorLight);
               }

               color24 = this.colorManager.colorAdder(color24, transparentLight);
            }
         }

         int transparentHeight;
         if (this.options.blockTransparency) {
            int transparentHeight = true;
            if (needHeightAndID) {
               transparentHeight = this.getTransparentHeight(nether, netherPlayerInOpen, caves, world, startX + imageX, startZ + imageY, height);
               this.mapData[this.zoom].setTransparentHeight(imageX, imageY, transparentHeight);
            } else {
               transparentHeight = this.mapData[this.zoom].getTransparentHeight(imageX, imageY);
            }

            if (!needHeightAndID) {
               blockID = this.mapData[this.zoom].getTransparentId(imageX, imageY);
               metadata = this.mapData[this.zoom].getTransparentMetadata(imageX, imageY);
            } else {
               if (transparentHeight != -1 && transparentHeight > height) {
                  Block block = world.getBlock(startX + imageX, transparentHeight - 1, startZ + imageY);
                  blockID = Block.blockRegistry.getIDForObject(block);
                  metadata = world.getBlockMetadata(startX + imageX, transparentHeight - 1, startZ + imageY);
               } else {
                  blockID = 0;
                  metadata = 0;
               }

               if (this.options.biomes && blockID != this.mapData[this.zoom].getTransparentId(imageX, imageY)) {
                  blockChangeForcedTint = true;
               }

               this.mapData[this.zoom].setTransparentId(imageX, imageY, blockID);
               this.mapData[this.zoom].setTransparentMetadata(imageX, imageY, metadata);
            }

            if (blockID != 0) {
               int transparentColor = false;
               if (this.options.biomes) {
                  transparentColor = this.colorManager.getBlockColor(blockID, metadata, biomeID);
               } else {
                  transparentColor = this.colorManager.getBlockColorWithDefaultTint(blockID, metadata, biomeID);
               }

               if (this.options.biomes) {
                  transparentLight = true;
                  if (!needTint && !blockChangeForcedTint) {
                     transparentLight = this.mapData[this.zoom].getTransparentBiomeTint(imageX, imageY);
                  } else {
                     transparentLight = this.getBiomeTint(blockID, metadata, startX + imageX, height, startZ + imageY);
                     this.mapData[this.zoom].setTransparentBiomeTint(imageX, imageY, transparentLight);
                  }

                  if (transparentLight != -1) {
                     transparentColor = this.colorManager.colorMultiplier(transparentColor, transparentLight);
                  }
               }

               transparentColor = this.applyHeight(transparentColor, nether, netherPlayerInOpen, caves, world, multi, startX, startZ, imageX, imageY, transparentHeight, solid, 2);
               transparentLight = true;
               if (needLight) {
                  transparentLight = this.getLight(transparentColor, blockID, world, startX + imageX, startZ + imageY, transparentHeight, solid);
                  this.mapData[this.zoom].setTransparentLight(imageX, imageY, transparentLight);
               } else {
                  transparentLight = this.mapData[this.zoom].getTransparentLight(imageX, imageY);
               }

               if (transparentLight == 0) {
                  transparentColor = 0;
               } else if (transparentLight != 255) {
                  transparentColor = this.colorManager.colorMultiplier(transparentColor, transparentLight);
               }

               color24 = this.colorManager.colorAdder(transparentColor, color24);
            }
         }

         if (this.options.biomeOverlay == 2) {
            transparentHeight = 0;
            if (biomeID >= 0) {
               transparentHeight = BiomeGenBase.getBiomeGenArray()[biomeID].color;
            }

            transparentColor = transparentHeight >> 16 & 255;
            transparentLight = transparentHeight >> 8 & 255;
            seafloorLight = transparentHeight >> 0 & 255;
            transparentHeight = 2130706432 | (transparentColor & 255) << 16 | (transparentLight & 255) << 8 | seafloorLight & 255;
            color24 = this.colorManager.colorAdder(transparentHeight, color24);
         }

         if (this.options.chunkGrid && ((startX + imageX) % 16 == 0 || (startZ + imageY) % 16 == 0)) {
            color24 = this.colorManager.colorAdder(2097152000, color24);
         }

         return color24;
      }
   }

   private int getBiomeTint(int id, int metadata, int x, int y, int z) {
      int tint = true;
      int tint;
      if (this.colorManager.isOptifineInstalled()) {
         try {
            Integer[] tints = (Integer[])((Integer[])this.colorManager.getBlockTintTables().get(id + " " + metadata));
            if (tints != null) {
               int r = 0;
               int g = 0;
               int b = 0;

               for(int t = -1; t <= 1; ++t) {
                  for(int s = -1; s <= 1; ++s) {
                     int biomeTint = tints[this.world.getBiomeGenForCoords(x + s, z + t).biomeID].intValue();
                     r += (biomeTint & 16711680) >> 16;
                     g += (biomeTint & '\uff00') >> 8;
                     b += biomeTint & 255;
                  }
               }

               tint = -16777216 | (r / 9 & 255) << 16 | (g / 9 & 255) << 8 | b / 9 & 255;
            } else {
               tint = this.getBuiltInBiomeTint(id, metadata, x, y, z);
            }
         } catch (Exception var14) {
            tint = this.getBuiltInBiomeTint(id, metadata, x, y, z);
         }
      } else {
         tint = this.getBuiltInBiomeTint(id, metadata, x, y, z);
      }

      return tint;
   }

   private int getBuiltInBiomeTint(int id, int metadata, int x, int y, int z) {
      int tint = -1;
      if (id == BlockIDRepository.grassID || id == BlockIDRepository.leavesID || id == BlockIDRepository.leaves2ID || id == BlockIDRepository.tallGrassID || id == BlockIDRepository.vineID || id == BlockIDRepository.tallFlowerID || id == BlockIDRepository.waterID || id == BlockIDRepository.flowingWaterID || this.colorManager.getBiomeTintsAvailable().contains(id)) {
         tint = ((Block)Block.blockRegistry.getObjectForID(id)).colorMultiplier(this.world, x, y, z) | -16777216;
      }

      return tint;
   }

   private final int getBlockHeight(boolean nether, boolean netherPlayerInOpen, boolean caves, World world, int x, int z, int starty) {
      int height = world.getHeightValue(x, z);
      int y;
      Block block;
      if ((nether || caves) && height >= starty && (!nether || starty <= 125 || this.options.showCaves && !netherPlayerInOpen)) {
         y = this.lastY;
         block = world.getBlock(x, y, z);
         if (block.getLightOpacity() == 0 && block.getMaterial() != Material.lava) {
            do {
               if (y <= 0) {
                  return y;
               }

               --y;
               block = world.getBlock(x, y, z);
            } while(block.getLightOpacity() <= 0 && block.getMaterial() != Material.lava);

            return y + 1;
         } else {
            while(y <= starty + 10) {
               if (y < (nether && starty < 126 ? 127 : 255)) {
                  ++y;
                  block = world.getBlock(x, y, z);
                  if (block.getLightOpacity() == 0 && block.getMaterial() != Material.lava) {
                     return y;
                  }
               }
            }

            return -1;
         }
      } else {
         y = world.getPrecipitationHeight(x, z);
         if (y != height) {
            block = world.getBlock(x, y - 1, z);
            if (block.getMaterial() == Material.lava) {
               height = y;
            }
         }

         for(int heightCheck = (height >> 4) * 16 + 15; heightCheck < this.worldHeight; heightCheck += 16) {
            Block block = world.getBlock(x, heightCheck, z);
            if (block.getLightOpacity() > 0) {
               height = heightCheck + 1;
            }
         }

         return height;
      }
   }

   private final int getSeafloorHeight(World world, int x, int z, int height) {
      int seafloorHeight = height;

      for(Block block = world.getBlock(x, height - 1, z); block.getLightOpacity() < 5 && block.getMaterial() != Material.leaves && seafloorHeight > 1; block = world.getBlock(x, seafloorHeight - 1, z)) {
         --seafloorHeight;
      }

      return seafloorHeight;
   }

   private final int getTransparentHeight(boolean nether, boolean netherPlayerInOpen, boolean caves, World world, int x, int z, int height) {
      int transHeight = true;
      int transHeight;
      if ((caves || nether) && (!nether || height <= 125 || this.options.showCaves && !netherPlayerInOpen)) {
         transHeight = height + 1;
      } else {
         int precipHeight = world.getPrecipitationHeight(x, z);
         if (precipHeight <= height) {
            transHeight = height + 1;
         } else {
            transHeight = precipHeight;
         }
      }

      Material material = world.getBlock(x, transHeight - 1, z).getMaterial();
      if (material == Material.field_151597_y || material == Material.air) {
         transHeight = -1;
      }

      return transHeight;
   }

   private int applyHeight(int color24, boolean nether, boolean netherPlayerInOpen, boolean caves, World world, int multi, int startX, int startZ, int imageX, int imageY, int height, boolean solid, int layer) {
      if (color24 != this.colorManager.getAirColor() && color24 != 0) {
         int heightComp = 0;
         if ((this.options.heightmap || this.options.slopemap) && !solid) {
            int diff = false;
            double sc = 0.0D;
            int baseHeight;
            int diff;
            if (!this.options.slopemap) {
               if (this.options.heightmap) {
                  diff = height - this.lastY;
                  sc = Math.log10((double)Math.abs(diff) / 8.0D + 1.0D) / 1.8D;
                  if (diff < 0) {
                     sc = 0.0D - sc;
                  }
               }
            } else {
               if (imageX > 0 && imageY < 32 * multi - 1) {
                  if (layer == 0) {
                     heightComp = this.mapData[this.zoom].getOceanFloorHeight(imageX - 1, imageY + 1);
                  }

                  if (layer == 1) {
                     heightComp = this.mapData[this.zoom].getHeight(imageX - 1, imageY + 1);
                  }

                  if (layer == 2) {
                     heightComp = this.mapData[this.zoom].getTransparentHeight(imageX - 1, imageY + 1);
                     if (heightComp == -1) {
                        Block block = Block.getBlockById(this.mapData[this.zoom].getTransparentId(imageX, imageY));
                        if (block instanceof BlockGlass || block instanceof BlockStainedGlass) {
                           heightComp = this.mapData[this.zoom].getHeight(imageX - 1, imageY + 1);
                        }
                     }
                  }
               } else {
                  if (layer == 0) {
                     baseHeight = this.getBlockHeight(nether, netherPlayerInOpen, caves, world, startX + imageX - 1, startZ + imageY + 1, this.lastY);
                     heightComp = this.getSeafloorHeight(world, startX + imageX - 1, startZ + imageY + 1, baseHeight);
                  }

                  if (layer == 1) {
                     heightComp = this.getBlockHeight(nether, netherPlayerInOpen, caves, world, startX + imageX - 1, startZ + imageY + 1, this.lastY);
                  }

                  if (layer == 2) {
                     baseHeight = this.getBlockHeight(nether, netherPlayerInOpen, caves, world, startX + imageX - 1, startZ + imageY + 1, this.lastY);
                     heightComp = this.getTransparentHeight(nether, netherPlayerInOpen, caves, world, startX + imageX - 1, startZ + imageY + 1, baseHeight);
                     if (heightComp == -1) {
                        Block block = world.getBlock(startX + imageX, height - 1, startZ + imageY);
                        if (block instanceof BlockGlass || block instanceof BlockStainedGlass) {
                           heightComp = baseHeight;
                        }
                     }
                  }
               }

               if (heightComp == -1) {
                  heightComp = height;
               }

               diff = heightComp - height;
               if (diff != 0) {
                  sc = diff < 0 ? -1.0D : (diff > 0 ? 1.0D : 0.0D);
                  sc /= 8.0D;
               }

               if (this.options.heightmap) {
                  diff = height - this.lastY;
                  double heightsc = Math.log10((double)Math.abs(diff) / 8.0D + 1.0D) / 3.0D;
                  sc = diff > 0 ? sc + heightsc : sc - heightsc;
               }
            }

            baseHeight = color24 >> 24 & 255;
            int r = color24 >> 16 & 255;
            int g = color24 >> 8 & 255;
            int b = color24 >> 0 & 255;
            if (sc > 0.0D) {
               r += (int)(sc * (double)(255 - r));
               g += (int)(sc * (double)(255 - g));
               b += (int)(sc * (double)(255 - b));
            } else if (sc < 0.0D) {
               sc = Math.abs(sc);
               r -= (int)(sc * (double)r);
               g -= (int)(sc * (double)g);
               b -= (int)(sc * (double)b);
            }

            color24 = baseHeight * 16777216 + r * 65536 + g * 256 + b;
         }
      }

      return color24;
   }

   private int getLight(int color24, int blockID, World world, int x, int z, int height, boolean solid) {
      int i3 = 255;
      if (solid) {
         i3 = 0;
      } else if (color24 != this.colorManager.getAirColor() && color24 != 0 && this.options.lightmap && FPSBoost.LIGHTING.getValue().booleanValue()) {
         Chunk chunk = world.getChunkFromBlockCoords(x, z);
         int blockLight = chunk.getSavedLightValue(EnumSkyBlock.Block, x & 15, Math.max(Math.min(height, 255), 0), z & 15);
         int skyLight = chunk.getSavedLightValue(EnumSkyBlock.Sky, x & 15, Math.max(Math.min(height, 255), 0), z & 15);
         if (blockID == BlockIDRepository.lavaID && blockLight < 14) {
            blockLight = 14;
         }

         i3 = this.lightmapColors[blockLight + skyLight * 16];
      }

      return i3;
   }

   private void renderMap(int x, int y, int scScale) {
      boolean scaleChanged = this.scScale != scScale || this.options.squareMap != this.lastSquareMap;
      this.scScale = scScale;
      this.lastSquareMap = this.options.squareMap;
      Object var5;
      float multi;
      if (GLUtils.hasAlphaBits) {
         GL11.glColorMask(false, false, false, true);
         GL11.glBindTexture(3553, 0);
         GL11.glBlendFunc(0, 0);
         GL11.glColor3f(0.0F, 0.0F, 255.0F);
         GL11.glBegin(7);
         GL11.glVertex2f((float)(x - 47), (float)(y + 47));
         GL11.glVertex2f((float)(x + 47), (float)(y + 47));
         GL11.glVertex2f((float)(x + 47), (float)(y - 47));
         GL11.glVertex2f((float)(x - 47), (float)(y - 47));
         GL11.glEnd();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glColorMask(true, true, true, true);
         GL11.glBlendFunc(770, 771);
         GLUtils.img(new ResourceLocation("voxelmap/" + (this.options.squareMap ? "images/square.png" : "images/circle.png")));
         GLUtils.drawPre();
         GLUtils.setMap(x, y);
         GLUtils.drawPost();
         GL11.glColorMask(true, true, true, true);
         GL11.glBlendFunc(772, 773);
         var5 = this.coordinateLock;
         synchronized(this.coordinateLock) {
            if (this.imageChanged) {
               this.imageChanged = false;
               this.map[this.zoom].write();
               this.lastImageX = this.lastX;
               this.lastImageZ = this.lastZ;
            }
         }

         multi = 2.0F / (float)Math.pow(2.0D, (double)this.zoom);
         this.percentX = (float)(GameVariableAccessShim.xCoordDouble() - (double)this.lastImageX);
         this.percentY = (float)(GameVariableAccessShim.zCoordDouble() - (double)this.lastImageZ);
         this.percentX *= multi;
         this.percentY *= multi;
         if (this.zoom == 3) {
            GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 1.0F);
            GLUtils.disp(this.map[this.zoom].index);
            GL11.glPopMatrix();
         } else {
            GLUtils.disp(this.map[this.zoom].index);
         }

         GL11.glPushMatrix();
         GL11.glTranslatef((float)x, (float)y, 0.0F);
         GL11.glRotatef(this.options.squareMap ? (float)this.northRotate : -this.direction + (float)this.northRotate, 0.0F, 0.0F, 1.0F);
         GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
         GL11.glTranslatef(-this.percentX, -this.percentY, 0.0F);
         if (this.options.filtering) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
         }
      } else if (GLUtils.fboEnabled) {
         GL11.glBindTexture(3553, 0);
         GL11.glPushAttrib(22528);
         GL11.glViewport(0, 0, 256, 256);
         GL11.glMatrixMode(5889);
         GL11.glPushMatrix();
         GL11.glLoadIdentity();
         GL11.glOrtho(0.0D, 256.0D, 256.0D, 0.0D, 1000.0D, 3000.0D);
         GL11.glMatrixMode(5888);
         GL11.glPushMatrix();
         GL11.glLoadIdentity();
         GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
         GLUtils.bindFrameBuffer();
         GL11.glDepthMask(false);
         GL11.glDisable(2929);
         if (scaleChanged) {
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClear(16384);
         }

         GL11.glBlendFunc(770, 0);
         GLUtils.img(new ResourceLocation("voxelmap/" + (this.options.squareMap ? "images/square.png" : "images/circle.png")));
         GLUtils.drawPre();
         GLUtils.ldrawthree(0.0D, 256.0D, 1.0D, 0.0D, 0.0D);
         GLUtils.ldrawthree(256.0D, 256.0D, 1.0D, 1.0D, 0.0D);
         GLUtils.ldrawthree(256.0D, 0.0D, 1.0D, 1.0D, 1.0D);
         GLUtils.ldrawthree(0.0D, 0.0D, 1.0D, 0.0D, 1.0D);
         GLUtils.drawPost();
         GL14.glBlendFuncSeparate(1, 0, 774, 0);
         var5 = this.coordinateLock;
         synchronized(this.coordinateLock) {
            if (this.imageChanged) {
               this.imageChanged = false;
               this.map[this.zoom].write();
               this.lastImageX = this.lastX;
               this.lastImageZ = this.lastZ;
            }
         }

         multi = 2.0F / (float)Math.pow(2.0D, (double)this.zoom);
         this.percentX = (float)(GameVariableAccessShim.xCoordDouble() - (double)this.lastImageX);
         this.percentY = (float)(GameVariableAccessShim.zCoordDouble() - (double)this.lastImageZ);
         this.percentX *= multi;
         this.percentY *= multi;
         if (this.zoom == 3) {
            GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 1.0F);
            GLUtils.disp(this.map[this.zoom].index);
            GL11.glPopMatrix();
         } else {
            GLUtils.disp(this.map[this.zoom].index);
         }

         if (this.options.filtering) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
         }

         GL11.glTranslatef(128.0F, 128.0F, 0.0F);
         if (this.options.squareMap) {
            GL11.glRotatef((float)(-this.northRotate), 0.0F, 0.0F, 1.0F);
         } else {
            GL11.glRotatef(this.direction - (float)this.northRotate, 0.0F, 0.0F, 1.0F);
         }

         GL11.glTranslatef(-128.0F, -128.0F, 0.0F);
         GL11.glTranslatef(-this.percentX * 4.0F, this.percentY * 4.0F, 0.0F);
         GLUtils.drawPre();
         GLUtils.ldrawthree(0.0D, 256.0D, 1.0D, 0.0D, 0.0D);
         GLUtils.ldrawthree(256.0D, 256.0D, 1.0D, 1.0D, 0.0D);
         GLUtils.ldrawthree(256.0D, 0.0D, 1.0D, 1.0D, 1.0D);
         GLUtils.ldrawthree(0.0D, 0.0D, 1.0D, 0.0D, 1.0D);
         GLUtils.drawPost();
         GL11.glDepthMask(true);
         GL11.glEnable(2929);
         GLUtils.unbindFrameBuffer();
         GL11.glMatrixMode(5889);
         GL11.glPopMatrix();
         GL11.glMatrixMode(5888);
         GL11.glPopMatrix();
         GL11.glPopAttrib();
         GL11.glPushMatrix();
         GL11.glBlendFunc(770, 0);
         GL11.glEnable(3008);
         GLUtils.disp(GLUtils.fboTextureID);
      } else {
         if (this.options.squareMap) {
            if (this.options.filtering && this.zoom == 0 && this.lastPercentXOver != this.percentX > 1.0F) {
               this.lastPercentXOver = this.percentX > 1.0F;
               this.imageChanged = true;
            }

            if (this.options.filtering && this.zoom == 0 && this.lastPercentYOver != this.percentY > 1.0F) {
               this.lastPercentYOver = this.percentY > 1.0F;
               this.imageChanged = true;
            }
         }

         if (this.imageChanged) {
            this.imageChanged = false;
            if (this.options.squareMap) {
               var5 = this.coordinateLock;
               synchronized(this.coordinateLock) {
                  this.map[this.zoom].write();
                  this.lastImageX = this.lastX;
                  this.lastImageZ = this.lastZ;
               }
            } else {
               int diameter = this.map[this.zoom].getWidth();
               if (this.roundImage != null) {
                  this.roundImage.baleet();
               }

               this.roundImage = new GLBufferedImage(diameter, diameter, 6);
               Double ellipse = new Double((double)(this.zoom * 10 / 6), (double)(this.zoom * 10 / 6), (double)(diameter - this.zoom * 2), (double)(diameter - this.zoom * 2));
               Graphics2D gfx = this.roundImage.createGraphics();
               gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               gfx.setClip(ellipse);
               gfx.setColor(new Color(0.1F, 0.0F, 0.0F, 0.1F));
               gfx.fillRect(0, 0, diameter, diameter);
               Object var8 = this.coordinateLock;
               synchronized(this.coordinateLock) {
                  gfx.drawImage(this.map[this.zoom], 0, 0, (ImageObserver)null);
                  this.lastImageX = this.lastX;
                  this.lastImageZ = this.lastZ;
               }

               gfx.dispose();
               this.roundImage.write();
            }
         }

         multi = 2.0F / (float)Math.pow(2.0D, (double)this.zoom);
         this.percentX = (float)(GameVariableAccessShim.xCoordDouble() - (double)this.lastImageX);
         this.percentY = (float)(GameVariableAccessShim.zCoordDouble() - (double)this.lastImageZ);
         this.percentX *= multi;
         this.percentY *= multi;
         GL11.glBlendFunc(770, 0);
         if (this.zoom == 3) {
            GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 1.0F);
            GLUtils.disp(this.options.squareMap ? this.map[this.zoom].index : this.roundImage.index);
            GL11.glPopMatrix();
         } else {
            GLUtils.disp(this.options.squareMap ? this.map[this.zoom].index : this.roundImage.index);
         }

         if (this.options.filtering) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
         }

         GL11.glPushMatrix();
         GL11.glTranslatef((float)x, (float)y, 0.0F);
         GL11.glRotatef(this.options.squareMap ? (float)this.northRotate : -this.direction + (float)this.northRotate, 0.0F, 0.0F, 1.0F);
         GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
         GL11.glTranslatef(-this.percentX, -this.percentY, 0.0F);
      }

      GLUtils.drawPre();
      GLUtils.setMap(x, y);
      GLUtils.drawPost();
      GL11.glPopMatrix();
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.options.squareMap) {
         this.drawSquareMapFrame(x, y);
      } else {
         this.drawRoundMapFrame(x, y);
      }

      double lastXDouble = GameVariableAccessShim.xCoordDouble();
      double lastZDouble = GameVariableAccessShim.zCoordDouble();
      Iterator var9 = this.waypointManager.getWaypoints().iterator();

      while(true) {
         Waypoint pt;
         do {
            if (!var9.hasNext()) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               return;
            }

            pt = (Waypoint)var9.next();
         } while(!pt.isActive());

         double wayX = lastXDouble - (double)pt.getX() - 0.5D;
         double wayY = lastZDouble - (double)pt.getZ() - 0.5D;
         float locate = (float)Math.toDegrees(Math.atan2(wayX, wayY));
         double hypot = Math.sqrt(wayX * wayX + wayY * wayY);
         boolean far = false;
         if (!this.options.squareMap) {
            locate += this.direction;
            hypot /= Math.pow(2.0D, (double)this.zoom) / 2.0D;
            far = hypot >= 31.0D;
            if (far) {
               hypot = 34.0D;
            }
         } else {
            far = Math.abs(wayX) / (Math.pow(2.0D, (double)this.zoom) / 2.0D) > 28.5D || Math.abs(wayY) / (Math.pow(2.0D, (double)this.zoom) / 2.0D) > 28.5D;
            if (far) {
               hypot = hypot / Math.max(Math.abs(wayX), Math.abs(wayY)) * 30.0D;
            } else {
               hypot /= Math.pow(2.0D, (double)this.zoom) / 2.0D;
            }
         }

         if (far) {
            try {
               GL11.glPushMatrix();
               GL11.glColor3f(pt.red, pt.green, pt.blue);
               if (scScale >= 3) {
                  GLUtils.img(new ResourceLocation("voxelmap/images/marker" + pt.imageSuffix + ".png"));
               } else {
                  GLUtils.img(new ResourceLocation("voxelmap/images/marker" + pt.imageSuffix + "Small.png"));
               }

               GL11.glTexParameteri(3553, 10241, 9729);
               GL11.glTexParameteri(3553, 10240, 9729);
               GL11.glTranslatef((float)x, (float)y, 0.0F);
               GL11.glRotatef(-locate + (float)this.northRotate, 0.0F, 0.0F, 1.0F);
               GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
               GL11.glTranslated(0.0D, -hypot, 0.0D);
               GLUtils.drawPre();
               GLUtils.setMap(x, (float)y, 16);
               GLUtils.drawPost();
            } catch (Exception var40) {
               this.error = "Error: marker overlay not found!";
            } finally {
               GL11.glPopMatrix();
            }
         } else {
            try {
               GL11.glPushMatrix();
               GL11.glColor3f(pt.red, pt.green, pt.blue);
               if (scScale >= 3) {
                  GLUtils.img(new ResourceLocation("voxelmap/images/waypoint" + pt.imageSuffix + ".png"));
               } else {
                  GLUtils.img(new ResourceLocation("voxelmap/images/waypoint" + pt.imageSuffix + "Small.png"));
               }

               GL11.glTexParameteri(3553, 10241, 9729);
               GL11.glTexParameteri(3553, 10240, 9729);
               GL11.glRotatef(-locate + (float)this.northRotate, 0.0F, 0.0F, 1.0F);
               GL11.glTranslated(0.0D, -hypot, 0.0D);
               GL11.glRotatef(-(-locate + (float)this.northRotate), 0.0F, 0.0F, 1.0F);
               GLUtils.drawPre();
               GLUtils.setMap(x, (float)y, 16);
               GLUtils.drawPost();
            } catch (Exception var38) {
               this.error = "Error: waypoint overlay not found!";
            } finally {
               GL11.glPopMatrix();
            }
         }
      }
   }

   private void drawArrow(int x, int y) {
      try {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glBlendFunc(770, 771);
         GL11.glPushMatrix();
         GLUtils.img(new ResourceLocation("voxelmap/images/mmarrow.png"));
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTranslatef((float)x, (float)y, 0.0F);
         GL11.glRotatef(this.direction, 0.0F, 0.0F, 1.0F);
         GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
         GLUtils.drawPre();
         GLUtils.setMap(x, (float)y, 16);
         GLUtils.drawPost();
      } catch (Exception var7) {
         this.error = "Error: minimap arrow not found!";
      } finally {
         GL11.glPopMatrix();
      }

   }

   private void renderMapFull(int scWidth, int scHeight) {
      Object var3 = this.coordinateLock;
      synchronized(this.coordinateLock) {
         if (this.imageChanged) {
            this.imageChanged = false;
            this.map[this.zoom].write();
            this.lastImageX = this.lastX;
            this.lastImageZ = this.lastZ;
         }
      }

      GLUtils.disp(this.map[this.zoom].index);
      if (this.options.filtering) {
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
      }

      GL11.glPushMatrix();
      GL11.glTranslatef((float)scWidth / 2.0F, (float)scHeight / 2.0F, 0.0F);
      GL11.glRotatef((float)this.northRotate, 0.0F, 0.0F, 1.0F);
      GL11.glTranslatef(-((float)scWidth / 2.0F), -((float)scHeight / 2.0F), 0.0F);
      GLUtils.drawPre();
      int left = scWidth / 2 - 128;
      int top = scHeight / 2 - 128;
      GLUtils.ldrawone(left, top + 256, 67.0D, 0.0D, 1.0D);
      GLUtils.ldrawone(left + 256, top + 256, 67.0D, 1.0D, 1.0D);
      GLUtils.ldrawone(left + 256, top, 67.0D, 1.0D, 0.0D);
      GLUtils.ldrawone(left, top, 67.0D, 0.0D, 0.0D);
      GLUtils.drawPost();
      GL11.glPopMatrix();
      if (this.options.biomeOverlay > 0) {
         int factor = (int)Math.pow(2.0D, (double)(3 - this.zoom));
         int minimumSize = (int)Math.pow(2.0D, (double)this.zoom);
         minimumSize *= minimumSize;
         ArrayList labels = this.mapData[this.zoom].getBiomeLabels();
         GL11.glDisable(2929);

         for(int t = 0; t < labels.size(); ++t) {
            MapData.BiomeLabel label = (MapData.BiomeLabel)labels.get(t);
            if (label.size > minimumSize) {
               String name = BiomeGenBase.getBiomeGenArray()[label.biomeInt].biomeName;
               int nameWidth = this.chkLen(name);
               int x = label.x * factor;
               int z = label.z * factor;
               if (this.options.oldNorth) {
                  this.write(name, left + 256 - z - nameWidth / 2, top + x - 3, 16777215);
               } else {
                  this.write(name, left + x - nameWidth / 2, top + z - 3, 16777215);
               }
            }
         }

         GL11.glEnable(2929);
      }

   }

   private void drawSquareMapFrame(int x, int y) {
      try {
         GLUtils.disp(this.colorManager.getMapImageInt());
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
         GLUtils.drawPre();
         GLUtils.setMap(x, y);
         GLUtils.drawPost();
      } catch (Exception var4) {
         this.error = "error: minimap overlay not found!";
      }

   }

   private void drawRoundMapFrame(int x, int y) {
      try {
         GLUtils.img(new ResourceLocation("voxelmap/images/roundmap.png"));
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GLUtils.drawPre();
         GLUtils.setMap(x, y);
         GLUtils.drawPost();
      } catch (Exception var4) {
         this.error = "Error: minimap overlay not found!";
      }

   }

   private void drawDirections(int x, int y) {
      float rotate;
      float distance;
      if (this.options.squareMap) {
         rotate = -90.0F;
         distance = 67.0F;
      } else {
         rotate = -this.direction - 90.0F;
         distance = 64.0F;
      }

      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 1.0F);
      GL11.glTranslated((double)distance * Math.sin(Math.toRadians(-((double)rotate - 90.0D))), (double)distance * Math.cos(Math.toRadians(-((double)rotate - 90.0D))), 0.0D);
      this.write("N", x * 2 - 2, y * 2 - 4, 16777215);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 1.0F);
      GL11.glTranslated((double)distance * Math.sin(Math.toRadians((double)(-rotate))), (double)distance * Math.cos(Math.toRadians((double)(-rotate))), 0.0D);
      this.write("E", x * 2 - 2, y * 2 - 4, 16777215);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 1.0F);
      GL11.glTranslated((double)distance * Math.sin(Math.toRadians(-((double)rotate + 90.0D))), (double)distance * Math.cos(Math.toRadians(-((double)rotate + 90.0D))), 0.0D);
      this.write("S", x * 2 - 2, y * 2 - 4, 16777215);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 1.0F);
      GL11.glTranslated((double)distance * Math.sin(Math.toRadians(-((double)rotate + 180.0D))), (double)distance * Math.cos(Math.toRadians(-((double)rotate + 180.0D))), 0.0D);
      this.write("W", x * 2 - 2, y * 2 - 4, 16777215);
      GL11.glPopMatrix();
   }

   private void showCoords(int x, int y) {
      int textStart;
      if (y > this.scHeight - 37 - 32 - 4 - 15) {
         textStart = y - 32 - 4 - 9;
      } else {
         textStart = y + 32 + 4;
      }

      String stats;
      int m;
      if (!this.options.hide && !this.fullscreenMap) {
         GL11.glPushMatrix();
         GL11.glScalef(0.5F, 0.5F, 1.0F);
         stats = "";
         stats = this.dCoord(GameVariableAccessShim.xCoord()) + ", " + this.dCoord(GameVariableAccessShim.zCoord());
         m = this.chkLen(stats) / 2;
         this.write(stats, x * 2 - m, textStart * 2, 16777215);
         stats = Integer.toString(GameVariableAccessShim.yCoord());
         m = this.chkLen(stats) / 2;
         this.write(stats, x * 2 - m, textStart * 2 + 10, 16777215);
         if (this.ztimer > 0) {
            m = this.chkLen(this.error) / 2;
            this.write(this.error, x * 2 - m, textStart * 2 + 19, 16777215);
         }

         GL11.glPopMatrix();
      } else {
         stats = "";
         stats = "(" + this.dCoord(GameVariableAccessShim.xCoord()) + ", " + GameVariableAccessShim.yCoord() + ", " + this.dCoord(GameVariableAccessShim.zCoord()) + ") " + (int)this.direction + "'";
         m = this.chkLen(stats) / 2;
         this.write(stats, this.scWidth / 2 - m, 5, 16777215);
         if (this.ztimer > 0) {
            m = this.chkLen(this.error) / 2;
            this.write(this.error, this.scWidth / 2 - m, 15, 16777215);
         }
      }

   }

   private String dCoord(int paramInt1) {
      if (paramInt1 < 0) {
         return "-" + Math.abs(paramInt1);
      } else {
         return paramInt1 > 0 ? "+" + paramInt1 : " " + paramInt1;
      }
   }

   private int chkLen(String paramStr) {
      return this.fontRenderer.getStringWidth(paramStr);
   }

   private void write(String paramStr, int paramInt1, int paramInt2, int paramInt3) {
      this.fontRenderer.drawStringWithShadow(paramStr, paramInt1, paramInt2, paramInt3);
   }

   public void setMenuNull() {
      this.game.currentScreen = null;
   }

   public Object getMenu() {
      return this.game.currentScreen;
   }

   private void showMenu(int scWidth, int scHeight) {
      GL11.glBlendFunc(770, 771);
      int maxSize = 0;
      int border = 2;
      String head = this.sMenu[0];

      int height;
      for(height = 1; height < this.sMenu.length - 1; ++height) {
         if (this.chkLen(this.sMenu[height]) > maxSize) {
            maxSize = this.chkLen(this.sMenu[height]);
         }
      }

      int title = this.chkLen(head);
      int centerX = (int)((double)(scWidth + 5) / 2.0D);
      int centerY = (int)((double)(scHeight + 5) / 2.0D);
      String hide = this.sMenu[this.sMenu.length - 1];
      int footer = this.chkLen(hide);
      GL11.glDisable(3553);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
      double leftX = (double)centerX - (double)title / 2.0D - (double)border;
      double rightX = (double)centerX + (double)title / 2.0D + (double)border;
      double topY = (double)centerY - (double)(height - 1) / 2.0D * 10.0D - (double)border - 20.0D;
      double botY = (double)centerY - (double)(height - 1) / 2.0D * 10.0D + (double)border - 10.0D;
      this.drawBox(leftX, rightX, topY, botY);
      leftX = (double)centerX - (double)maxSize / 2.0D - (double)border;
      rightX = (double)centerX + (double)maxSize / 2.0D + (double)border;
      topY = (double)centerY - (double)(height - 1) / 2.0D * 10.0D - (double)border;
      botY = (double)centerY + (double)(height - 1) / 2.0D * 10.0D + (double)border;
      this.drawBox(leftX, rightX, topY, botY);
      leftX = (double)centerX - (double)footer / 2.0D - (double)border;
      rightX = (double)centerX + (double)footer / 2.0D + (double)border;
      topY = (double)centerY + (double)(height - 1) / 2.0D * 10.0D - (double)border + 10.0D;
      botY = (double)centerY + (double)(height - 1) / 2.0D * 10.0D + (double)border + 20.0D;
      this.drawBox(leftX, rightX, topY, botY);
      GL11.glEnable(3553);
      this.write(head, centerX - title / 2, centerY - (height - 1) * 10 / 2 - 19, 16777215);

      for(int n = 1; n < height; ++n) {
         this.write(this.sMenu[n], centerX - maxSize / 2, centerY - (height - 1) * 10 / 2 + n * 10 - 9, 16777215);
      }

      this.write(hide, centerX - footer / 2, (scHeight + 5) / 2 + (height - 1) * 10 / 2 + 11, 16777215);
   }

   private void drawBox(double leftX, double rightX, double topY, double botY) {
      GLUtils.drawPre();
      GLUtils.ldrawtwo(leftX, botY, 0.0D);
      GLUtils.ldrawtwo(rightX, botY, 0.0D);
      GLUtils.ldrawtwo(rightX, topY, 0.0D);
      GLUtils.ldrawtwo(leftX, topY, 0.0D);
      GLUtils.drawPost();
   }
}
