package com.thevoxelbox.voxelmap;

import com.thevoxelbox.voxelmap.interfaces.IVoxelMap;
import com.thevoxelbox.voxelmap.interfaces.IWaypointManager;
import com.thevoxelbox.voxelmap.util.ChatUtils;
import com.thevoxelbox.voxelmap.util.EntityWaypointContainer;
import com.thevoxelbox.voxelmap.util.FilesystemUtils;
import com.thevoxelbox.voxelmap.util.GameVariableAccessShim;
import com.thevoxelbox.voxelmap.util.Waypoint;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumChatFormatting;

public class WaypointManager implements IWaypointManager {
   public MapSettingsManager options = null;
   IVoxelMap master;
   private ArrayList wayPts = new ArrayList();
   private ArrayList old2dWayPts = new ArrayList();
   private ArrayList updatedPts;
   private String currentSubWorldName = "";
   private String currentSubWorldHash = "";
   private String currentSubWorldDescriptor = "";
   private EntityWaypointContainer entityWaypointContainer = null;
   private File settingsFile;
   private Object lock = new Object();

   public WaypointManager(IVoxelMap master) {
      this.master = master;
      this.options = master.getMapOptions();
   }

   public ArrayList getWaypoints() {
      return this.wayPts;
   }

   public void handleDeath() {
      TreeSet toDel = new TreeSet();
      Iterator var2 = this.wayPts.iterator();

      while(var2.hasNext()) {
         Waypoint pt = (Waypoint)var2.next();
         if (pt.name.equals("Latest Death")) {
            pt.name = "Previous Death";
         }

         if (pt.name.startsWith("Previous Death")) {
            if (this.options.deathpoints > 1) {
               int num = 0;

               try {
                  if (pt.name.length() > 15) {
                     num = Integer.parseInt(pt.name.substring(15));
                  }
               } catch (Exception var6) {
                  num = 0;
               }

               pt.red -= (pt.red - 0.5F) / 8.0F;
               pt.green -= (pt.green - 0.5F) / 8.0F;
               pt.blue -= (pt.blue - 0.5F) / 8.0F;
               pt.name = "Previous Death " + (num + 1);
            } else {
               toDel.add(this.wayPts.indexOf(pt));
            }
         }
      }

      if (this.options.deathpoints < 2 && toDel.size() > 0) {
         TreeSet toDelReverse = (TreeSet)toDel.descendingSet();
         Iterator var9 = toDelReverse.iterator();

         while(var9.hasNext()) {
            Integer index = (Integer)var9.next();
            this.deleteWaypoint(index.intValue());
         }
      }

      if (this.options.deathpoints > 0) {
         EntityClientPlayerMP thePlayer = Minecraft.getMinecraft().thePlayer;
         TreeSet dimensions = new TreeSet();
         dimensions.add(Minecraft.getMinecraft().thePlayer.dimension);
         this.addWaypoint(new Waypoint("Latest Death", thePlayer.dimension != -1 ? GameVariableAccessShim.xCoord() : GameVariableAccessShim.xCoord() * 8, thePlayer.dimension != -1 ? GameVariableAccessShim.zCoord() : GameVariableAccessShim.zCoord() * 8, GameVariableAccessShim.yCoord() - 1, true, 1.0F, 1.0F, 1.0F, "skull", this.currentSubWorldName, dimensions));
      }

   }

   public void newWorld(int dimension) {
      Object var2 = this.lock;
      synchronized(this.lock) {
         Iterator var3 = this.wayPts.iterator();

         while(true) {
            while(var3.hasNext()) {
               Waypoint pt = (Waypoint)var3.next();
               if (pt.dimensions.size() != 0 && !pt.dimensions.contains(dimension)) {
                  pt.inDimension = false;
               } else {
                  pt.inDimension = true;
               }
            }

            this.injectWaypointEntity();
            return;
         }
      }
   }

   public void newSubWorldDescriptor(String descriptor) {
      this.currentSubWorldDescriptor = descriptor;
      Object var3 = this.lock;
      synchronized(this.lock) {
         String currentSubWorldDescriptorScrubbed = this.scrubName(descriptor);
         Iterator var4 = this.wayPts.iterator();

         while(true) {
            while(var4.hasNext()) {
               Waypoint pt = (Waypoint)var4.next();
               if (currentSubWorldDescriptorScrubbed != "" && pt.world != "" && !currentSubWorldDescriptorScrubbed.equals(pt.world)) {
                  pt.inWorld = false;
               } else {
                  pt.inWorld = true;
               }
            }

            return;
         }
      }
   }

   public void newSubWorldName(String name) {
      this.currentSubWorldName = name;
      this.newSubWorldDescriptor(this.currentSubWorldName);
   }

   public void newSubWorldHash(String hash) {
      this.currentSubWorldHash = hash;
      if (this.currentSubWorldName.equals("")) {
         this.newSubWorldDescriptor(this.currentSubWorldHash);
      }

   }

   public String getCurrentSubworldDescriptor() {
      return this.currentSubWorldDescriptor;
   }

   public void saveWaypoints() {
      String worldNameSave = this.master.getMap().getCurrentWorldName();
      if (worldNameSave.endsWith(":25565")) {
         int portSepLoc = worldNameSave.lastIndexOf(":");
         if (portSepLoc != -1) {
            worldNameSave = worldNameSave.substring(0, portSepLoc);
         }
      }

      worldNameSave = this.scrubFileName(worldNameSave);
      File homeDir = FilesystemUtils.getAppDir("minecraft", true).getAbsoluteFile();
      File mcDir = Minecraft.getMinecraft().mcDataDir.getAbsoluteFile();
      if (!homeDir.equals(mcDir)) {
         String localDirName = "";
         if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
            localDirName = mcDir.getName();
            if ((localDirName.equalsIgnoreCase("minecraft") || localDirName.equalsIgnoreCase(".")) && mcDir.getParentFile() != null) {
               localDirName = mcDir.getParentFile().getName();
            }

            localDirName = "~" + localDirName;
         }

         this.settingsFile = new File(FilesystemUtils.getAppDir("minecraft/mods/VoxelMods/voxelMap", true), worldNameSave + localDirName + ".points");
         this.saveWaypointsTo(this.settingsFile);
         homeDir = new File(Minecraft.getMinecraft().mcDataDir, "/mods/VoxelMods/voxelMap/");
         if (!homeDir.exists()) {
            homeDir.mkdirs();
         }

         this.settingsFile = new File(Minecraft.getMinecraft().mcDataDir, "/mods/VoxelMods/voxelMap/" + worldNameSave + ".points");
         this.saveWaypointsTo(this.settingsFile);
      } else {
         this.settingsFile = new File(FilesystemUtils.getAppDir("minecraft/mods/VoxelMods/voxelMap", true), worldNameSave + ".points");
         this.saveWaypointsTo(this.settingsFile);
      }

   }

   public void saveWaypointsTo(File settingsFile) {
      try {
         PrintWriter out = new PrintWriter(new FileWriter(settingsFile));
         Date now = new Date();
         String timestamp = (new SimpleDateFormat("yyyyMMddHHmm")).format(now);
         out.println("filetimestamp:" + timestamp);
         Iterator var5 = this.wayPts.iterator();

         while(true) {
            Waypoint pt;
            do {
               do {
                  if (!var5.hasNext()) {
                     out.close();
                     return;
                  }

                  pt = (Waypoint)var5.next();
               } while(pt.isAutomated);
            } while(pt.name.startsWith("^"));

            String dimensionsString = "";

            Integer dimension;
            for(Iterator var8 = pt.dimensions.iterator(); var8.hasNext(); dimensionsString = dimensionsString + "" + dimension + "#") {
               dimension = (Integer)var8.next();
            }

            if (dimensionsString.equals("")) {
               dimensionsString = "-1#0#";
            }

            out.println("name:" + this.scrubName(pt.name) + ",x:" + pt.x + ",z:" + pt.z + ",y:" + pt.y + ",enabled:" + Boolean.toString(pt.enabled) + ",red:" + pt.red + ",green:" + pt.green + ",blue:" + pt.blue + ",suffix:" + pt.imageSuffix + ",world:" + this.scrubName(pt.world) + ",dimensions:" + dimensionsString);
         }
      } catch (Exception var10) {
         ChatUtils.chatInfo(EnumChatFormatting.YELLOW + "Error Saving Waypoints");
      }
   }

   public String scrubName(String input) {
      input = input.replace(":", "~colon~");
      input = input.replace(",", "~comma~");
      return input;
   }

   private String descrubName(String input) {
      input = input.replace("~colon~", ":");
      input = input.replace("~comma~", ",");
      return input;
   }

   public String scrubFileName(String input) {
      input = input.replace("<", "~less~");
      input = input.replace(">", "~greater~");
      input = input.replace(":", "~colon~");
      input = input.replace("\"", "~quote~");
      input = input.replace("/", "~slash~");
      input = input.replace("\\", "~backslash~");
      input = input.replace("|", "~pipe~");
      input = input.replace("?", "~question~");
      input = input.replace("*", "~star~");
      return input;
   }

   public void loadWaypoints() {
      Object var1 = this.lock;
      synchronized(this.lock) {
         boolean loaded = false;
         this.wayPts = new ArrayList();
         String worldNameStandard = this.master.getMap().getCurrentWorldName();
         if (worldNameStandard.endsWith(":25565")) {
            int portSepLoc = worldNameStandard.lastIndexOf(":");
            if (portSepLoc != -1) {
               worldNameStandard = worldNameStandard.substring(0, portSepLoc);
            }
         }

         worldNameStandard = this.scrubFileName(worldNameStandard);
         String worldNameWithPort = this.scrubFileName(this.master.getMap().getCurrentWorldName());
         String worldNameWithoutPort = this.master.getMap().getCurrentWorldName();
         int portSepLoc = worldNameWithoutPort.lastIndexOf(":");
         if (portSepLoc != -1) {
            worldNameWithoutPort = worldNameWithoutPort.substring(0, portSepLoc);
         }

         worldNameWithoutPort = this.scrubFileName(worldNameWithoutPort);
         String worldNameWithDefaultPort = this.scrubFileName(worldNameWithoutPort + "~colon~25565");
         loaded = this.loadWaypointsExtensible(worldNameStandard);
         if (!loaded) {
            loaded = this.loadOldWaypoints(worldNameWithoutPort, worldNameWithDefaultPort, worldNameWithPort);
         }

         if (!loaded) {
            loaded = this.findReiWaypoints(worldNameWithoutPort);
         }

         if (!loaded) {
            ChatUtils.chatInfo("§ENo waypoints exist for this world/server.");
         } else {
            this.populateOld2dWaypoints();
         }
      }

      this.newSubWorldDescriptor(this.currentSubWorldDescriptor);
   }

   private boolean loadWaypointsExtensible(String worldNameStandard) {
      File homeDir = FilesystemUtils.getAppDir("minecraft", false).getAbsoluteFile();
      File mcDir = Minecraft.getMinecraft().mcDataDir.getAbsoluteFile();
      if (!homeDir.equals(mcDir)) {
         long homeDate = -1L;
         long localDate = -1L;
         String localDirName = "";
         if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
            localDirName = mcDir.getName();
            if ((localDirName.equalsIgnoreCase("minecraft") || localDirName.equalsIgnoreCase(".")) && mcDir.getParentFile() != null) {
               localDirName = mcDir.getParentFile().getName();
            }

            localDirName = "~" + localDirName;
         }

         File settingsFileLocal = new File(Minecraft.getMinecraft().mcDataDir, "/mods/VoxelMods/voxelMap/" + worldNameStandard + ".points");
         localDate = this.getDateFromSave(settingsFileLocal);
         File settingsFileHome = new File(FilesystemUtils.getAppDir("minecraft/mods/VoxelMods/voxelMap", false), worldNameStandard + localDirName + ".points");
         if (!settingsFileHome.exists() && !settingsFileLocal.exists()) {
            settingsFileHome = new File(FilesystemUtils.getAppDir("minecraft/mods/VoxelMods/voxelMap", false), worldNameStandard + ".points");
         }

         homeDate = this.getDateFromSave(settingsFileHome);
         if (!settingsFileHome.exists() && !settingsFileLocal.exists()) {
            return false;
         }

         if (!settingsFileHome.exists()) {
            this.settingsFile = settingsFileLocal;
         } else if (!settingsFileLocal.exists()) {
            this.settingsFile = settingsFileHome;
         } else {
            this.settingsFile = homeDate > localDate ? settingsFileHome : settingsFileLocal;
         }
      } else {
         this.settingsFile = new File(FilesystemUtils.getAppDir("minecraft/mods/VoxelMods/voxelMap", false), worldNameStandard + ".points");
      }

      if (this.settingsFile.exists()) {
         try {
            BufferedReader in = new BufferedReader(new FileReader(this.settingsFile));

            while(true) {
               String sCurrentLine;
               do {
                  if ((sCurrentLine = in.readLine()) == null) {
                     in.close();
                     return true;
                  }
               } while(sCurrentLine.startsWith("filetimestamp"));

               String[] curLine = sCurrentLine.split(",");
               String name = "";
               int x = 0;
               int z = 0;
               int y = -1;
               boolean enabled = false;
               float red = 0.5F;
               float green = 0.0F;
               float blue = 0.0F;
               String suffix = "";
               String world = "";
               TreeSet dimensions = new TreeSet();

               for(int t = 0; t < curLine.length; ++t) {
                  String[] keyValuePair = curLine[t].split(":");
                  if (keyValuePair.length == 2) {
                     String key = keyValuePair[0];
                     String value = keyValuePair[1];
                     if (key.equals("name")) {
                        name = this.descrubName(value);
                     } else if (key.equals("x")) {
                        x = Integer.parseInt(value);
                     } else if (key.equals("z")) {
                        z = Integer.parseInt(value);
                     } else if (key.equals("y")) {
                        y = Integer.parseInt(value);
                     } else if (key.equals("enabled")) {
                        enabled = Boolean.parseBoolean(value);
                     } else if (key.equals("red")) {
                        red = Float.parseFloat(value);
                     } else if (key.equals("green")) {
                        green = Float.parseFloat(value);
                     } else if (key.equals("blue")) {
                        blue = Float.parseFloat(value);
                     } else if (key.equals("suffix")) {
                        suffix = value;
                     } else if (key.equals("world")) {
                        world = this.descrubName(value);
                     } else if (key.equals("dimensions")) {
                        String[] dimensionStrings = value.split("#");

                        for(int s = 0; s < dimensionStrings.length; ++s) {
                           dimensions.add(Integer.parseInt(dimensionStrings[s]));
                        }

                        if (dimensions.size() == 0) {
                           dimensions.add(Integer.valueOf(0));
                           dimensions.add(Integer.valueOf(-1));
                        }
                     }
                  }
               }

               if (!name.equals("")) {
                  this.loadWaypoint(name, x, z, y, enabled, red, green, blue, suffix, world, dimensions);
               }
            }
         } catch (Exception var24) {
            ChatUtils.chatInfo(EnumChatFormatting.YELLOW + "Error Loading Waypoints");
            System.err.println("waypoint load error: " + var24.getLocalizedMessage());
            return false;
         }
      } else {
         return false;
      }
   }

   private long getDateFromSave(File settingsFile) {
      if (settingsFile.exists()) {
         try {
            BufferedReader in = new BufferedReader(new FileReader(settingsFile));
            String sCurrentLine = in.readLine();
            String[] curLine = sCurrentLine.split(":");
            in.close();
            if (curLine[0].equals("filetimestamp")) {
               return Long.parseLong(curLine[1]);
            }
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

      return -1L;
   }

   private boolean loadOldWaypoints(String worldNameWithPort, String worldNameWithDefaultPort, String worldNameWithoutPort) {
      try {
         this.settingsFile = new File(FilesystemUtils.getAppDir("minecraft/mods/zan", false), worldNameWithPort + ".points");
         if (!this.settingsFile.exists()) {
            this.settingsFile = new File(FilesystemUtils.getAppDir("minecraft/mods/zan", false), worldNameWithDefaultPort + ".points");
         }

         if (!this.settingsFile.exists()) {
            this.settingsFile = new File(FilesystemUtils.getAppDir("minecraft/mods/zan", false), worldNameWithoutPort + ".points");
         }

         if (!this.settingsFile.exists()) {
            this.settingsFile = new File(FilesystemUtils.getAppDir("minecraft", false), worldNameWithoutPort + ".points");
         }

         if (!this.settingsFile.exists()) {
            return false;
         } else {
            TreeSet dimensions = new TreeSet();
            dimensions.add(Integer.valueOf(-1));
            dimensions.add(Integer.valueOf(0));
            BufferedReader in = new BufferedReader(new FileReader(this.settingsFile));

            while(true) {
               String sCurrentLine;
               while((sCurrentLine = in.readLine()) != null) {
                  String[] curLine = sCurrentLine.split(":");
                  if (curLine.length == 4) {
                     this.loadWaypoint(curLine[0], Integer.parseInt(curLine[1]), Integer.parseInt(curLine[2]), -1, Boolean.parseBoolean(curLine[3]), 0.0F, 1.0F, 0.0F, "", "", dimensions);
                  } else if (curLine.length == 7) {
                     this.loadWaypoint(curLine[0], Integer.parseInt(curLine[1]), Integer.parseInt(curLine[2]), -1, Boolean.parseBoolean(curLine[3]), Float.parseFloat(curLine[4]), Float.parseFloat(curLine[5]), Float.parseFloat(curLine[6]), "", "", dimensions);
                  } else if (curLine.length == 8) {
                     if (!curLine[3].contains("true") && !curLine[3].contains("false")) {
                        this.loadWaypoint(curLine[0], Integer.parseInt(curLine[1]), Integer.parseInt(curLine[2]), Integer.parseInt(curLine[3]), Boolean.parseBoolean(curLine[4]), Float.parseFloat(curLine[5]), Float.parseFloat(curLine[6]), Float.parseFloat(curLine[7]), "", "", dimensions);
                     } else {
                        this.loadWaypoint(curLine[0], Integer.parseInt(curLine[1]), Integer.parseInt(curLine[2]), -1, Boolean.parseBoolean(curLine[3]), Float.parseFloat(curLine[4]), Float.parseFloat(curLine[5]), Float.parseFloat(curLine[6]), curLine[7], "", dimensions);
                     }
                  } else if (curLine.length == 9) {
                     this.loadWaypoint(curLine[0], Integer.parseInt(curLine[1]), Integer.parseInt(curLine[2]), Integer.parseInt(curLine[3]), Boolean.parseBoolean(curLine[4]), Float.parseFloat(curLine[5]), Float.parseFloat(curLine[6]), Float.parseFloat(curLine[7]), curLine[8], "", dimensions);
                  }
               }

               in.close();
               return true;
            }
         }
      } catch (Exception var8) {
         ChatUtils.chatInfo(EnumChatFormatting.YELLOW + "Error Loading Waypoints");
         System.err.println("waypoint load error: " + var8.getLocalizedMessage());
         return false;
      }
   }

   private boolean findReiWaypoints(String worldNameWithoutPort) {
      boolean foundSome = false;
      this.settingsFile = new File(FilesystemUtils.getAppDir("minecraft/mods/rei_minimap", false), worldNameWithoutPort + ".points");
      if (!this.settingsFile.exists()) {
         this.settingsFile = new File(Minecraft.getMinecraft().mcDataDir, "/mods/rei_minimap/" + worldNameWithoutPort + ".points");
      }

      if (this.settingsFile.exists()) {
         this.loadReiWaypoints(this.settingsFile, 0);
         foundSome = true;
      } else {
         for(int t = -25; t < 25; ++t) {
            this.settingsFile = new File(FilesystemUtils.getAppDir("minecraft/mods/rei_minimap", false), worldNameWithoutPort + ".DIM" + t + ".points");
            if (!this.settingsFile.exists()) {
               this.settingsFile = new File(Minecraft.getMinecraft().mcDataDir, "/mods/rei_minimap/" + worldNameWithoutPort + ".DIM" + t + ".points");
            }

            if (this.settingsFile.exists()) {
               foundSome = true;
               this.loadReiWaypoints(this.settingsFile, t);
            }
         }
      }

      return foundSome;
   }

   private void loadReiWaypoints(File settingsFile, int dimension) {
      try {
         if (settingsFile.exists()) {
            TreeSet dimensions = new TreeSet();
            dimensions.add(dimension);
            BufferedReader in = new BufferedReader(new FileReader(settingsFile));

            String sCurrentLine;
            while((sCurrentLine = in.readLine()) != null) {
               String[] curLine = sCurrentLine.split(":");
               if (curLine.length == 6) {
                  int color = Integer.parseInt(curLine[5], 16);
                  float red = (float)(color >> 16 & 255) / 255.0F;
                  float green = (float)(color >> 8 & 255) / 255.0F;
                  float blue = (float)(color >> 0 & 255) / 255.0F;
                  int x = Integer.parseInt(curLine[1]);
                  int z = Integer.parseInt(curLine[3]);
                  if (dimension == -1) {
                     x *= 8;
                     z *= 8;
                  }

                  this.loadWaypoint(curLine[0], x, z, Integer.parseInt(curLine[2]), Boolean.parseBoolean(curLine[4]), red, green, blue, "", "", dimensions);
               }
            }

            in.close();
         }
      } catch (Exception var13) {
         ChatUtils.chatInfo(EnumChatFormatting.YELLOW + "Error Loading Old Rei Waypoints");
         System.err.println("waypoint load error: " + var13.getLocalizedMessage());
      }

   }

   public void loadWaypoint(String name, int x, int z, int y, boolean enabled, float red, float green, float blue, String suffix, String world, TreeSet dimensions) {
      Waypoint newWaypoint = new Waypoint(name, x, z, y, enabled, red, green, blue, suffix, world, dimensions);
      this.wayPts.add(newWaypoint);
   }

   public void populateOld2dWaypoints() {
      this.old2dWayPts = new ArrayList();
      Iterator var1 = this.wayPts.iterator();

      while(var1.hasNext()) {
         Waypoint wpt = (Waypoint)var1.next();
         if (wpt.getY() <= 0) {
            this.old2dWayPts.add(wpt);
         }
      }

   }

   public void check2dWaypoints() {
      if (Minecraft.getMinecraft().thePlayer.dimension == 0 && this.old2dWayPts.size() > 0) {
         this.updatedPts = new ArrayList();
         Iterator var1 = this.old2dWayPts.iterator();

         while(var1.hasNext()) {
            Waypoint pt = (Waypoint)var1.next();
            if (Math.abs(pt.getX() - GameVariableAccessShim.xCoord()) < 400 && Math.abs(pt.getZ() - GameVariableAccessShim.zCoord()) < 400 && Minecraft.getMinecraft().thePlayer.worldObj.getChunkFromBlockCoords(pt.getX(), pt.getZ()).isChunkLoaded) {
               pt.setY(Minecraft.getMinecraft().thePlayer.worldObj.getHeightValue(pt.getX(), pt.getZ()));
               this.updatedPts.add(pt);
               this.saveWaypoints();
            }
         }

         this.old2dWayPts.removeAll(this.updatedPts);
         System.out.println("remaining old 2d waypoints: " + this.old2dWayPts.size());
      }

   }

   private void deleteWaypoint(int i) {
      this.old2dWayPts.remove(this.wayPts.get(i));
      this.entityWaypointContainer.removeWaypoint((Waypoint)this.wayPts.get(i));
      this.wayPts.remove(i);
      this.saveWaypoints();
   }

   public void deleteWaypoint(Waypoint point) {
      this.old2dWayPts.remove(point);
      this.entityWaypointContainer.removeWaypoint(point);
      this.wayPts.remove(point);
      this.saveWaypoints();
   }

   public void addWaypoint(Waypoint newWaypoint) {
      this.wayPts.add(newWaypoint);
      this.entityWaypointContainer.addWaypoint(newWaypoint);
      this.saveWaypoints();
   }

   private void purgeWaypointEntity() {
      List entities = Minecraft.getMinecraft().theWorld.getLoadedEntityList();

      for(int t = 0; t < entities.size(); ++t) {
         Entity entity = (Entity)entities.get(t);
         if (entity instanceof EntityWaypointContainer) {
            entity.setDead();
         }
      }

   }

   public void injectWaypointEntity() {
      this.entityWaypointContainer = new EntityWaypointContainer(Minecraft.getMinecraft().theWorld);
      Iterator var1 = this.wayPts.iterator();

      while(var1.hasNext()) {
         Waypoint wpt = (Waypoint)var1.next();
         this.entityWaypointContainer.addWaypoint(wpt);
      }

      Minecraft.getMinecraft().theWorld.spawnEntityInWorld(this.entityWaypointContainer);
   }

   public void moveWaypointEntityToBack() {
      List entities = Minecraft.getMinecraft().theWorld.getLoadedEntityList();
      synchronized(entities) {
         int t = entities.indexOf(this.entityWaypointContainer);
         if (t == -1) {
            this.purgeWaypointEntity();
            this.injectWaypointEntity();
         } else {
            if (t != entities.size() - 1) {
               Collections.swap(entities, t, entities.size() - 1);
            }

         }
      }
   }
}
