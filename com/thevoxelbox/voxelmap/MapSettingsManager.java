package com.thevoxelbox.voxelmap;

import com.thevoxelbox.voxelmap.gui.overridden.EnumOptionsMinimap;
import com.thevoxelbox.voxelmap.util.ChatUtils;
import com.thevoxelbox.voxelmap.util.I18nUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class MapSettingsManager {
   public static MapSettingsManager instance;
   public final int SORT_DATE = 1;
   public final int SORT_NAME = 2;
   public final int SORT_DISTANCE = 3;
   public final int SORT_COLOR = 4;
   public RadarSettingsManager radarOptions = null;
   public boolean showUnderMenus;
   public boolean hide = false;
   public boolean waterTransparency;
   public boolean blockTransparency;
   public boolean squareMap;
   public boolean showBeacons;
   public boolean showWaypoints;
   public int deathpoints;
   public int maxWaypointDisplayDistance;
   public int zoom;
   public int sizeModifier;
   public int mapCorner;
   public Boolean radarAllowed;
   public Boolean cavesAllowed;
   public int sort;
   public boolean dlSafe;
   public KeyBinding keyBindZoom;
   public KeyBinding keyBindFullscreen;
   public KeyBinding keyBindMenu;
   public KeyBinding keyBindWaypointMenu;
   public KeyBinding keyBindWaypoint;
   public KeyBinding keyBindMobToggle;
   public KeyBinding[] keyBindings;
   public Minecraft game;
   protected boolean coords;
   protected boolean showCaves;
   protected boolean lightmap;
   protected boolean heightmap;
   protected boolean slopemap;
   protected boolean filtering;
   protected boolean biomes;
   protected int biomeOverlay;
   protected boolean chunkGrid;
   protected boolean oldNorth;
   protected boolean welcome;
   protected int regularZoom;
   protected boolean realTimeTorches;
   private File settingsFile;
   private int availableProcessors;
   public boolean multicore;
   private boolean somethingChanged;

   public MapSettingsManager() {
      this.waterTransparency = this.multicore;
      this.blockTransparency = this.multicore;
      this.squareMap = false;
      this.showBeacons = true;
      this.showWaypoints = true;
      this.deathpoints = 1;
      this.maxWaypointDisplayDistance = 1000;
      this.zoom = 2;
      this.sizeModifier = 0;
      this.mapCorner = 1;
      this.radarAllowed = true;
      this.cavesAllowed = true;
      this.sort = 1;
      this.dlSafe = false;
      this.keyBindZoom = new KeyBinding("key.minimap.zoom", 44, "controls.minimap.title");
      this.keyBindFullscreen = new KeyBinding("key.minimap.togglefullscreen", 45, "controls.minimap.title");
      this.keyBindMenu = new KeyBinding("key.minimap.voxelmapmenu", 50, "controls.minimap.title");
      this.keyBindWaypointMenu = new KeyBinding("key.minimap.waypointmenu", 0, "controls.minimap.title");
      this.keyBindWaypoint = new KeyBinding("key.minimap.waypointhotkey", 48, "controls.minimap.title");
      this.keyBindMobToggle = new KeyBinding("key.minimap.togglemobs", 0, "controls.minimap.title");
      this.game = null;
      this.coords = true;
      this.showCaves = true;
      this.lightmap = true;
      this.heightmap = this.multicore;
      this.slopemap = true;
      this.filtering = true;
      this.biomes = this.multicore;
      this.biomeOverlay = 0;
      this.chunkGrid = false;
      this.oldNorth = false;
      this.welcome = true;
      this.regularZoom = 2;
      this.realTimeTorches = false;
      this.availableProcessors = Runtime.getRuntime().availableProcessors();
      this.multicore = this.availableProcessors > 0;
      instance = this;
      this.game = Minecraft.getMinecraft();
      this.keyBindings = new KeyBinding[]{this.keyBindMenu, this.keyBindWaypoint, this.keyBindZoom, this.keyBindFullscreen, this.keyBindMobToggle};
   }

   public static String getKeyDisplayString(int par0) {
      return par0 < 0 ? I18n.format("key.mouseButton", par0 + 101) : Keyboard.getKeyName(par0);
   }

   public void setRadarSettings(RadarSettingsManager radarSettings) {
      this.radarOptions = radarSettings;
   }

   public void loadAll() {
      this.settingsFile = new File(this.game.mcDataDir, "mods/VoxelMods/voxelmap.properties");

      try {
         if (this.settingsFile.exists()) {
            BufferedReader in;
            String sCurrentLine;
            for(in = new BufferedReader(new FileReader(this.settingsFile)); (sCurrentLine = in.readLine()) != null; KeyBinding.resetKeyBindingArrayAndHash()) {
               String[] curLine = sCurrentLine.split(":");
               if (curLine[0].equals("Zoom Level")) {
                  this.zoom = Integer.parseInt(curLine[1]);
               } else if (curLine[0].equals("Hide Minimap")) {
                  this.hide = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Show Coordinates")) {
                  this.coords = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Enable Cave Mode")) {
                  this.showCaves = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Dynamic Lighting")) {
                  this.lightmap = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Height Map")) {
                  this.heightmap = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Slope Map")) {
                  this.slopemap = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Filtering")) {
                  this.filtering = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Water Transparency")) {
                  this.waterTransparency = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Block Transparency")) {
                  this.blockTransparency = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Biomes")) {
                  this.biomes = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Biome Overlay")) {
                  this.biomeOverlay = Integer.parseInt(curLine[1]);
               } else if (curLine[0].equals("Chunk Grid")) {
                  this.chunkGrid = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Square Map")) {
                  this.squareMap = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Old North")) {
                  this.oldNorth = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Waypoint Beacons")) {
                  this.showBeacons = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Waypoint Signs")) {
                  this.showWaypoints = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Deathpoints")) {
                  this.deathpoints = Integer.parseInt(curLine[1]);
               } else if (curLine[0].equals("Waypoint Max Distance")) {
                  this.maxWaypointDisplayDistance = Integer.parseInt(curLine[1]);
               } else if (curLine[0].equals("Waypoint Sort By")) {
                  this.sort = Integer.parseInt(curLine[1]);
               } else if (curLine[0].equals("Welcome Message")) {
                  this.welcome = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("World Download Compatibility")) {
                  this.dlSafe = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Real Time Torch Flicker")) {
                  this.realTimeTorches = Boolean.parseBoolean(curLine[1]);
               } else if (curLine[0].equals("Map Corner")) {
                  this.mapCorner = Integer.parseInt(curLine[1]);
               } else if (curLine[0].equals("Map Size")) {
                  this.sizeModifier = Integer.parseInt(curLine[1]);
               } else if (curLine[0].equals("Zoom Key")) {
                  this.keyBindZoom.setKeyCode(Keyboard.getKeyIndex(curLine[1]));
               } else if (curLine[0].equals("Fullscreen Key")) {
                  this.keyBindFullscreen.setKeyCode(Keyboard.getKeyIndex(curLine[1]));
               } else if (curLine[0].equals("Menu Key")) {
                  this.keyBindMenu.setKeyCode(Keyboard.getKeyIndex(curLine[1]));
               } else if (curLine[0].equals("Waypoint Key")) {
                  this.keyBindWaypoint.setKeyCode(Keyboard.getKeyIndex(curLine[1]));
               } else if (curLine[0].equals("Mob Key")) {
                  this.keyBindMobToggle.setKeyCode(Keyboard.getKeyIndex(curLine[1]));
               }
            }

            if (this.radarOptions != null) {
               this.radarOptions.loadSettings(this.settingsFile);
            }

            in.close();
         }

         this.saveAll();
      } catch (Exception var4) {
         ;
      }

   }

   public void saveAll() {
      File settingsFileDir = new File(this.game.mcDataDir, "/mods/VoxelMods/");
      if (!settingsFileDir.exists()) {
         settingsFileDir.mkdirs();
      }

      this.settingsFile = new File(settingsFileDir, "voxelmap.properties");

      try {
         PrintWriter out = new PrintWriter(new FileWriter(this.settingsFile));
         out.println("Zoom Level:" + Integer.toString(this.zoom));
         out.println("Hide Minimap:" + Boolean.toString(this.hide));
         out.println("Show Coordinates:" + Boolean.toString(this.coords));
         out.println("Enable Cave Mode:" + Boolean.toString(this.showCaves));
         out.println("Dynamic Lighting:" + Boolean.toString(this.lightmap));
         out.println("Height Map:" + Boolean.toString(this.heightmap));
         out.println("Slope Map:" + Boolean.toString(this.slopemap));
         out.println("Filtering:" + Boolean.toString(this.filtering));
         out.println("Water Transparency:" + Boolean.toString(this.waterTransparency));
         out.println("Block Transparency:" + Boolean.toString(this.blockTransparency));
         out.println("Biomes:" + Boolean.toString(this.biomes));
         out.println("Biome Overlay:" + Integer.toString(this.biomeOverlay));
         out.println("Chunk Grid:" + Boolean.toString(this.chunkGrid));
         out.println("Square Map:" + Boolean.toString(this.squareMap));
         out.println("Old North:" + Boolean.toString(this.oldNorth));
         out.println("Waypoint Beacons:" + Boolean.toString(this.showBeacons));
         out.println("Waypoint Signs:" + Boolean.toString(this.showWaypoints));
         out.println("Deathpoints:" + Integer.toString(this.deathpoints));
         out.println("Waypoint Max Distance:" + Integer.toString(this.maxWaypointDisplayDistance));
         out.println("Waypoint Sort By:" + Integer.toString(this.sort));
         out.println("Welcome Message:" + Boolean.toString(this.welcome));
         out.println("Map Corner:" + Integer.toString(this.mapCorner));
         out.println("Map Size:" + Integer.toString(this.sizeModifier));
         out.println("Zoom Key:" + getKeyDisplayString(this.keyBindZoom.getKeyCode()));
         out.println("Fullscreen Key:" + getKeyDisplayString(this.keyBindFullscreen.getKeyCode()));
         out.println("Menu Key:" + getKeyDisplayString(this.keyBindMenu.getKeyCode()));
         out.println("Waypoint Key:" + getKeyDisplayString(this.keyBindWaypoint.getKeyCode()));
         out.println("Mob Key:" + getKeyDisplayString(this.keyBindMobToggle.getKeyCode()));
         if (this.radarOptions != null) {
            this.radarOptions.saveAll(out);
         }

         out.close();
      } catch (Exception var3) {
         ChatUtils.chatInfo(EnumChatFormatting.YELLOW + "Error Saving Settings " + var3.getLocalizedMessage());
      }

   }

   public String getKeyText(EnumOptionsMinimap par1EnumOptions) {
      String s = I18nUtils.getString(par1EnumOptions.getEnumString()) + ": ";
      if (par1EnumOptions.getEnumFloat()) {
         float f = this.getOptionFloatValue(par1EnumOptions);
         if (par1EnumOptions == EnumOptionsMinimap.ZOOM) {
            return s + (int)f;
         } else if (par1EnumOptions == EnumOptionsMinimap.WAYPOINTDISTANCE) {
            return f < 0.0F ? s + I18nUtils.getString("options.minimap.waypoints.infinite") : s + (int)f;
         } else {
            return f == 0.0F ? s + I18nUtils.getString("options.off") : s + (int)f + "%";
         }
      } else if (par1EnumOptions.getEnumBoolean()) {
         boolean flag = this.getOptionBooleanValue(par1EnumOptions);
         return flag ? s + I18nUtils.getString("options.on") : s + I18nUtils.getString("options.off");
      } else if (par1EnumOptions.getEnumList()) {
         String state = this.getOptionListValue(par1EnumOptions);
         return s + state;
      } else {
         return s;
      }
   }

   public float getOptionFloatValue(EnumOptionsMinimap par1EnumOptions) {
      if (par1EnumOptions == EnumOptionsMinimap.ZOOM) {
         return (float)this.zoom;
      } else {
         return par1EnumOptions == EnumOptionsMinimap.WAYPOINTDISTANCE ? (float)this.maxWaypointDisplayDistance : 0.0F;
      }
   }

   public boolean getOptionBooleanValue(EnumOptionsMinimap par1EnumOptions) {
      switch(par1EnumOptions) {
      case COORDS:
         return this.coords;
      case HIDE:
         return this.hide;
      case CAVEMODE:
         return this.cavesAllowed.booleanValue() && this.showCaves;
      case LIGHTING:
         return this.lightmap;
      case SQUARE:
         return this.squareMap;
      case OLDNORTH:
         return this.oldNorth;
      case WELCOME:
         return this.welcome;
      case FILTERING:
         return this.filtering;
      case WATERTRANSPARENCY:
         return this.waterTransparency;
      case BLOCKTRANSPARENCY:
         return this.blockTransparency;
      case BIOMES:
         return this.biomes;
      case CHUNKGRID:
         return this.chunkGrid;
      default:
         throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + par1EnumOptions.getEnumString() + ". (possibly not a boolean)");
      }
   }

   public String getOptionListValue(EnumOptionsMinimap par1EnumOptions) {
      switch(par1EnumOptions) {
      case TERRAIN:
         if (this.slopemap && this.heightmap) {
            return I18nUtils.getString("options.minimap.terrain.both");
         } else if (this.heightmap) {
            return I18nUtils.getString("options.minimap.terrain.height");
         } else {
            if (this.slopemap) {
               return I18nUtils.getString("options.minimap.terrain.slope");
            }

            return I18nUtils.getString("options.off");
         }
      case BEACONS:
         if (this.showBeacons && this.showWaypoints) {
            return I18nUtils.getString("options.minimap.ingamewaypoints.both");
         } else if (this.showBeacons) {
            return I18nUtils.getString("options.minimap.ingamewaypoints.beacons");
         } else {
            if (this.showWaypoints) {
               return I18nUtils.getString("options.minimap.ingamewaypoints.signs");
            }

            return I18nUtils.getString("options.off");
         }
      case LOCATION:
         if (this.mapCorner == 0) {
            return I18nUtils.getString("options.minimap.location.topleft");
         } else if (this.mapCorner == 1) {
            return I18nUtils.getString("options.minimap.location.topright");
         } else if (this.mapCorner == 2) {
            return I18nUtils.getString("options.minimap.location.bottomright");
         } else {
            if (this.mapCorner == 3) {
               return I18nUtils.getString("options.minimap.location.bottomleft");
            }

            return "Error";
         }
      case SIZE:
         if (this.sizeModifier == -1) {
            return I18nUtils.getString("options.minimap.size.small");
         } else if (this.sizeModifier == 0) {
            return I18nUtils.getString("options.minimap.size.medium");
         } else {
            if (this.sizeModifier == 1) {
               return I18nUtils.getString("options.minimap.size.large");
            }

            return "error";
         }
      case BIOMEOVERLAY:
         if (this.biomeOverlay == 0) {
            return I18nUtils.getString("options.off");
         } else if (this.biomeOverlay == 1) {
            return I18nUtils.getString("options.minimap.biomeoverlay.solid");
         } else {
            if (this.biomeOverlay == 2) {
               return I18nUtils.getString("options.minimap.biomeoverlay.transparent");
            }

            return "error";
         }
      case DEATHPOINTS:
         if (this.deathpoints == 0) {
            return I18nUtils.getString("options.off");
         } else if (this.deathpoints == 1) {
            return I18nUtils.getString("options.minimap.waypoints.deathpoints.mostrecent");
         } else {
            if (this.deathpoints == 2) {
               return I18nUtils.getString("options.minimap.waypoints.deathpoints.all");
            }

            return "error";
         }
      default:
         throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + par1EnumOptions.getEnumString() + ". (possibly not a list value)");
      }
   }

   public void setOptionFloatValue(EnumOptionsMinimap par1EnumOptions, float par2) {
      if (par1EnumOptions == EnumOptionsMinimap.WAYPOINTDISTANCE) {
         float distance = par2 * 9951.0F + 50.0F;
         if (distance > 10000.0F) {
            distance = -1.0F;
         }

         this.maxWaypointDisplayDistance = (int)distance;
      }

   }

   public void setOptionValue(EnumOptionsMinimap par1EnumOptions, int i) {
      switch(par1EnumOptions) {
      case COORDS:
         this.coords = !this.coords;
         break;
      case HIDE:
         this.hide = !this.hide;
         break;
      case CAVEMODE:
         this.showCaves = !this.showCaves;
         break;
      case LIGHTING:
         this.lightmap = !this.lightmap;
         break;
      case SQUARE:
         this.squareMap = !this.squareMap;
         break;
      case OLDNORTH:
         this.oldNorth = !this.oldNorth;
         break;
      case WELCOME:
         this.welcome = !this.welcome;
         break;
      case FILTERING:
         this.filtering = !this.filtering;
         break;
      case WATERTRANSPARENCY:
         this.waterTransparency = !this.waterTransparency;
         break;
      case BLOCKTRANSPARENCY:
         this.blockTransparency = !this.blockTransparency;
         break;
      case BIOMES:
         this.biomes = !this.biomes;
         break;
      case CHUNKGRID:
         this.chunkGrid = !this.chunkGrid;
         break;
      case TERRAIN:
         if (this.slopemap && this.heightmap) {
            this.slopemap = false;
            this.heightmap = false;
         } else if (this.slopemap) {
            this.slopemap = false;
            this.heightmap = true;
         } else if (this.heightmap) {
            this.slopemap = true;
            this.heightmap = true;
         } else {
            this.slopemap = true;
            this.heightmap = false;
         }
         break;
      case BEACONS:
         if (this.showBeacons && this.showWaypoints) {
            this.showBeacons = false;
            this.showWaypoints = false;
         } else if (this.showBeacons) {
            this.showBeacons = false;
            this.showWaypoints = true;
         } else if (this.showWaypoints) {
            this.showWaypoints = true;
            this.showBeacons = true;
         } else {
            this.showBeacons = true;
            this.showWaypoints = false;
         }
         break;
      case LOCATION:
         this.mapCorner = this.mapCorner >= 3 ? 0 : this.mapCorner + 1;
         break;
      case SIZE:
         this.sizeModifier = this.sizeModifier >= 1 ? -1 : this.sizeModifier + 1;
         break;
      case BIOMEOVERLAY:
         ++this.biomeOverlay;
         if (this.biomeOverlay > 2) {
            this.biomeOverlay = 0;
         }
         break;
      case DEATHPOINTS:
         ++this.deathpoints;
         if (this.deathpoints > 2) {
            this.deathpoints = 0;
         }
         break;
      default:
         throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + par1EnumOptions.getEnumString());
      }

      this.somethingChanged = true;
   }

   public String getKeyBindingDescription(int par1) {
      return this.keyBindings[par1].getKeyDescription().equals("key.minimap.voxelmapmenu") ? I18nUtils.getString("key.minimap.menu") : I18nUtils.getString(this.keyBindings[par1].getKeyDescription());
   }

   public String getOptionDisplayString(int par1) {
      int var2 = this.keyBindings[par1].getKeyCode();
      return getKeyDisplayString(var2);
   }

   public void setKeyBinding(int par1, int par2) {
      this.keyBindings[par1].setKeyCode(par2);
      this.saveAll();
   }

   public void setSort(int sort) {
      if (sort != this.sort && sort != -this.sort) {
         this.sort = sort;
      } else {
         this.sort = -this.sort;
      }

   }

   public boolean isChanged() {
      if (this.somethingChanged) {
         this.somethingChanged = false;
         return true;
      } else {
         return false;
      }
   }
}
