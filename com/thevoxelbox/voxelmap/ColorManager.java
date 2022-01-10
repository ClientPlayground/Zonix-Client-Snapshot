package com.thevoxelbox.voxelmap;

import com.thevoxelbox.minecraft.src.VoxelMapProtectedFieldsHelper;
import com.thevoxelbox.voxelmap.interfaces.IColorManager;
import com.thevoxelbox.voxelmap.interfaces.IVoxelMap;
import com.thevoxelbox.voxelmap.util.BlockIDRepository;
import com.thevoxelbox.voxelmap.util.GLUtils;
import com.thevoxelbox.voxelmap.util.ImageUtils;
import com.thevoxelbox.voxelmap.util.ReflectionUtils;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeGenBase;

public class ColorManager implements IColorManager {
   private static int COLOR_NOT_LOADED = -65281;
   private final Object tpLoadLock = new Object();
   Minecraft game = null;
   private IVoxelMap master;
   private MapSettingsManager options = null;
   private List packs = null;
   private BufferedImage terrainBuff = null;
   private BufferedImage colorPicker;
   private int mapImageInt = -1;
   private int[] blockColors = new int[86016];
   private int[] blockColorsWithDefaultTint = new int[86016];
   private Set biomeTintsAvailable = new HashSet();
   private boolean hdInstalled = false;
   private boolean optifineInstalled = false;
   private HashMap blockTintTables = new HashMap();
   private Set biomeTextureAvailable = new HashSet();
   private HashMap blockBiomeSpecificColors = new HashMap();
   private String renderPassThreeBlendMode;

   public ColorManager(IVoxelMap master) {
      this.master = master;
      this.options = master.getMapOptions();
      this.game = Minecraft.getMinecraft();
      this.optifineInstalled = false;
      Field ofProfiler = null;

      try {
         ofProfiler = GameSettings.class.getDeclaredField("ofProfiler");
      } catch (SecurityException var8) {
         ;
      } catch (NoSuchFieldException var9) {
         ;
      } finally {
         if (ofProfiler != null) {
            this.optifineInstalled = true;
         }

      }

      this.hdInstalled = ReflectionUtils.classExists("com.prupe.mcpatcher.ctm.CTMUtils");
   }

   private static void findResourcesDirectory(File base, String namespace, String directory, String suffix, boolean recursive, boolean directories, Collection resources) {
      File subdirectory = new File(base, directory);
      String[] list = subdirectory.list();
      if (list != null) {
         String pathComponent = directory + "/";
         String[] var10 = list;
         int var11 = list.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            String s = var10[var12];
            File entry = new File(subdirectory, s);
            String resourceName = pathComponent + s;
            if (entry.isDirectory()) {
               if (directories && s.endsWith(suffix)) {
                  resources.add(new ResourceLocation(namespace, resourceName));
               }

               if (recursive) {
                  findResourcesDirectory(base, namespace, pathComponent + s, suffix, recursive, directories, resources);
               }
            } else if (s.endsWith(suffix) && !directories) {
               resources.add(new ResourceLocation(namespace, resourceName));
            }
         }
      }

   }

   public int getMapImageInt() {
      return this.mapImageInt;
   }

   public int getAirColor() {
      return this.blockColors[BlockIDRepository.airID];
   }

   public Set getBiomeTintsAvailable() {
      return this.biomeTintsAvailable;
   }

   public boolean isOptifineInstalled() {
      return this.optifineInstalled;
   }

   public HashMap getBlockTintTables() {
      return this.blockTintTables;
   }

   public BufferedImage getColorPicker() {
      return this.colorPicker;
   }

   public boolean checkForChanges() {
      if (this.packs != null && this.packs.equals(this.game.gameSettings.resourcePacks)) {
         return false;
      } else {
         this.loadColors();
         return true;
      }
   }

   public void loadColors() {
      this.packs = new ArrayList(this.game.gameSettings.resourcePacks);
      BlockIDRepository.getIDs();
      this.loadColorPicker();
      this.loadMapImage();
      this.loadTexturePackTerrainImage();
      Object var1 = this.tpLoadLock;
      synchronized(this.tpLoadLock) {
         try {
            (new Thread(new Runnable() {
               public void run() {
                  Arrays.fill(ColorManager.this.blockColors, ColorManager.COLOR_NOT_LOADED);
                  Arrays.fill(ColorManager.this.blockColorsWithDefaultTint, ColorManager.COLOR_NOT_LOADED);
                  ColorManager.this.loadSpecialColors();
                  ColorManager.this.biomeTintsAvailable.clear();
                  ColorManager.this.biomeTextureAvailable.clear();
                  ColorManager.this.blockBiomeSpecificColors.clear();
                  if (ColorManager.this.hdInstalled || ColorManager.this.optifineInstalled) {
                     try {
                        ColorManager.this.processCTM();
                     } catch (Exception var4) {
                        System.err.println("error loading CTM " + var4.getLocalizedMessage());
                        var4.printStackTrace();
                     }
                  }

                  try {
                     ColorManager.this.loadBiomeColors(ColorManager.this.options.biomes);
                  } catch (Exception var3) {
                     System.err.println("error setting default biome shading " + var3.getLocalizedMessage());
                  }

                  if (ColorManager.this.hdInstalled || ColorManager.this.optifineInstalled) {
                     ColorManager.this.blockTintTables.clear();

                     try {
                        ColorManager.this.processColorProperties();
                        if (ColorManager.this.optifineInstalled) {
                           ColorManager.this.processColorProperty(new ResourceLocation("mcpatcher/colormap/water.png"), "" + BlockIDRepository.flowingWaterID + " " + BlockIDRepository.waterID);
                           ColorManager.this.processColorProperty(new ResourceLocation("mcpatcher/colormap/watercolor.png"), "" + BlockIDRepository.flowingWaterID + " " + BlockIDRepository.waterID);
                           ColorManager.this.processColorProperty(new ResourceLocation("mcpatcher/colormap/watercolorX.png"), "" + BlockIDRepository.flowingWaterID + " " + BlockIDRepository.waterID);
                           ColorManager.this.processColorProperty(new ResourceLocation("mcpatcher/colormap/swampgrass.png"), "" + BlockIDRepository.grassID + " " + BlockIDRepository.tallGrassID + ":1,2 " + BlockIDRepository.tallFlowerID + ":2,3");
                           ColorManager.this.processColorProperty(new ResourceLocation("mcpatcher/colormap/swampgrasscolor.png"), "" + BlockIDRepository.grassID + " " + BlockIDRepository.tallGrassID + ":1,2 " + BlockIDRepository.tallFlowerID + ":2,3");
                           ColorManager.this.processColorProperty(new ResourceLocation("mcpatcher/colormap/swampfoliage.png"), "" + BlockIDRepository.leavesID + ":0,4,8,12 " + BlockIDRepository.vineID);
                           ColorManager.this.processColorProperty(new ResourceLocation("mcpatcher/colormap/swampfoliagecolor.png"), "" + BlockIDRepository.leavesID + ":0,4,8,12 " + BlockIDRepository.vineID);
                           ColorManager.this.processColorProperty(new ResourceLocation("mcpatcher/colormap/pine.png"), "" + BlockIDRepository.leavesID + ":1,5,9,13");
                           ColorManager.this.processColorProperty(new ResourceLocation("mcpatcher/colormap/pinecolor.png"), "" + BlockIDRepository.leavesID + ":1,5,9,13");
                           ColorManager.this.processColorProperty(new ResourceLocation("mcpatcher/colormap/birch.png"), "" + BlockIDRepository.leavesID + ":2,6,10,14");
                           ColorManager.this.processColorProperty(new ResourceLocation("mcpatcher/colormap/birchcolor.png"), "" + BlockIDRepository.leavesID + ":2,6,10,14");
                        }
                     } catch (Exception var2) {
                        System.err.println("error loading custom color properties " + var2.getLocalizedMessage());
                        var2.printStackTrace();
                     }
                  }

                  ColorManager.this.master.getMap().forceFullRender(true);
               }
            }, "Voxelmap Load Resourcepack Thread")).start();
         } catch (Exception var4) {
            System.err.println("error loading pack");
            var4.printStackTrace();
         }
      }

      if (this.master.getRadar() != null) {
         this.master.getRadar().loadTexturePackIcons();
      }

   }

   public final BufferedImage getBlockImage(int blockID, int metadata) {
      try {
         IIcon icon = ((Block)Block.blockRegistry.getObjectForID(blockID)).getIcon(3, metadata);
         int left = (int)(icon.getMinU() * (float)this.terrainBuff.getWidth());
         int right = (int)(icon.getMaxU() * (float)this.terrainBuff.getWidth());
         int top = (int)(icon.getMinV() * (float)this.terrainBuff.getHeight());
         int bottom = (int)(icon.getMaxV() * (float)this.terrainBuff.getHeight());
         return this.terrainBuff.getSubimage(left, top, right - left, bottom - top);
      } catch (Exception var8) {
         return null;
      }
   }

   private void loadColorPicker() {
      try {
         InputStream is = this.game.getResourceManager().getResource(new ResourceLocation("voxelmap/images/colorPicker.png")).getInputStream();
         Image picker = ImageIO.read(is);
         is.close();
         this.colorPicker = new BufferedImage(picker.getWidth((ImageObserver)null), picker.getHeight((ImageObserver)null), 2);
         Graphics gfx = this.colorPicker.createGraphics();
         gfx.drawImage(picker, 0, 0, (ImageObserver)null);
         gfx.dispose();
      } catch (Exception var4) {
         System.err.println("Error loading color picker: " + var4.getLocalizedMessage());
      }

   }

   private void loadMapImage() {
      if (this.mapImageInt != -1) {
         GLUtils.glah(this.mapImageInt);
      }

      BufferedImage tpMap;
      try {
         InputStream is = this.game.getResourceManager().getResource(new ResourceLocation("voxelmap/images/squaremap.png")).getInputStream();
         Image tpMap = ImageIO.read(is);
         tpMap = new BufferedImage(tpMap.getWidth((ImageObserver)null), tpMap.getHeight((ImageObserver)null), 2);
         Graphics2D gfx = tpMap.createGraphics();
         gfx.drawImage(tpMap, 0, 0, (ImageObserver)null);
         this.mapImageInt = GLUtils.tex(tpMap);
      } catch (Exception var8) {
         try {
            InputStream is = this.game.getResourceManager().getResource(new ResourceLocation("textures/map/map_background.png")).getInputStream();
            tpMap = ImageIO.read(is);
            is.close();
            BufferedImage mapImage = new BufferedImage(tpMap.getWidth((ImageObserver)null), tpMap.getHeight((ImageObserver)null), 2);
            Graphics2D gfx = mapImage.createGraphics();
            if (!GLUtils.fboEnabled && !GLUtils.hasAlphaBits) {
               gfx.setColor(Color.DARK_GRAY);
               gfx.fillRect(0, 0, mapImage.getWidth(), mapImage.getHeight());
            }

            gfx.drawImage(tpMap, 0, 0, (ImageObserver)null);
            int border = mapImage.getWidth() * 8 / 128;
            gfx.setComposite(AlphaComposite.Clear);
            gfx.fillRect(border, border, mapImage.getWidth() - border * 2, mapImage.getHeight() - border * 2);
            gfx.dispose();
            this.mapImageInt = GLUtils.tex(mapImage);
         } catch (Exception var7) {
            System.err.println("Error loading texture pack's map image: " + var7.getLocalizedMessage());
         }
      }

   }

   public void setSkyColor(int skyColor) {
      for(int t = 0; t < 16; ++t) {
         this.blockColors[this.blockColorID(BlockIDRepository.airID, t)] = skyColor;
      }

   }

   private void loadTexturePackTerrainImage() {
      try {
         TextureManager textureManager = this.game.getTextureManager();
         textureManager.bindTexture(textureManager.getResourceLocation(0));
         BufferedImage terrainStitched = ImageUtils.createBufferedImageFromCurrentGLImage();
         this.terrainBuff = new BufferedImage(terrainStitched.getWidth((ImageObserver)null), terrainStitched.getHeight((ImageObserver)null), 6);
         Graphics gfx = this.terrainBuff.createGraphics();
         gfx.drawImage(terrainStitched, 0, 0, (ImageObserver)null);
         gfx.dispose();
      } catch (Exception var4) {
         System.err.println("Error processing new resource pack: " + var4.getLocalizedMessage());
         var4.printStackTrace();
      }

   }

   private void loadSpecialColors() {
      this.blockColors[this.blockColorID(BlockIDRepository.tallGrassID, 0)] = this.colorMultiplier(this.getColor(BlockIDRepository.tallGrassID, 0), -1);

      for(int t = 0; t < 16; ++t) {
         this.blockColors[this.blockColorID(BlockIDRepository.cobwebID, t)] = this.getColor(BlockIDRepository.cobwebID, t, false);
      }

      VoxelMapProtectedFieldsHelper.setLightOpacity(Block.getBlockFromName("minecraft:flowing_lava"), 1);
      VoxelMapProtectedFieldsHelper.setLightOpacity(Block.getBlockFromName("minecraft:lava"), 1);
   }

   private void loadBiomeColors(boolean biomes) {
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.grassID, 0)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.grassID, 0), ColorizerGrass.getGrassColor(0.7D, 0.8D) | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 0)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 0), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 1)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 1), ColorizerFoliage.getFoliageColorPine() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 2)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 2), ColorizerFoliage.getFoliageColorBirch() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 3)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 3), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 4)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 4), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 5)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 5), ColorizerFoliage.getFoliageColorPine() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 6)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 6), ColorizerFoliage.getFoliageColorBirch() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 7)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 7), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 8)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 8), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 9)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 9), ColorizerFoliage.getFoliageColorPine() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 10)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 10), ColorizerFoliage.getFoliageColorBirch() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 11)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 11), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 12)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 12), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 13)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 13), ColorizerFoliage.getFoliageColorPine() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 14)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 14), ColorizerFoliage.getFoliageColorBirch() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leavesID, 15)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leavesID, 15), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leaves2ID, 0)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leaves2ID, 0), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leaves2ID, 1)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leaves2ID, 1), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leaves2ID, 4)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leaves2ID, 4), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.leaves2ID, 5)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.leaves2ID, 5), ColorizerFoliage.getFoliageColorBasic() | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.tallGrassID, 1)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.tallGrassID, 1), ColorizerGrass.getGrassColor(0.7D, 0.8D) | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.tallGrassID, 2)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.tallGrassID, 2), ColorizerGrass.getGrassColor(0.7D, 0.8D) | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.tallFlowerID, 2)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.tallFlowerID, 2), ColorizerGrass.getGrassColor(0.7D, 0.8D) | -16777216);
      this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.tallFlowerID, 3)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.tallFlowerID, 3), ColorizerGrass.getGrassColor(0.7D, 0.8D) | -16777216);

      for(int t = 0; t < 16; ++t) {
         this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.reedsID, 0)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.reedsID, 0), ColorizerGrass.getGrassColor(0.7D, 0.8D) | -16777216);
         this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.vineID, t)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.vineID, t), ColorizerFoliage.getFoliageColor(0.7D, 0.8D) | -16777216);
      }

      this.loadWaterColor(biomes);
   }

   private void loadWaterColor(boolean biomes) {
      int waterRGB = true;
      int waterRGB = this.getBlockColor(BlockIDRepository.waterID, 0);
      InputStream is = null;
      int waterMult = -1;
      BufferedImage waterColorBuff = null;

      try {
         is = this.game.getResourceManager().getResource(new ResourceLocation("mcpatcher/colormap/water.png")).getInputStream();
      } catch (IOException var14) {
         is = null;
      }

      if (is != null) {
         try {
            Image waterColor = ImageIO.read(is);
            is.close();
            waterColorBuff = new BufferedImage(waterColor.getWidth((ImageObserver)null), waterColor.getHeight((ImageObserver)null), 1);
            Graphics gfx = waterColorBuff.createGraphics();
            gfx.drawImage(waterColor, 0, 0, (ImageObserver)null);
            gfx.dispose();
            BiomeGenBase genBase = BiomeGenBase.forest;
            double var1 = (double)MathHelper.clamp_float(genBase.getFloatTemperature(0, 64, 0), 0.0F, 1.0F);
            double var2 = (double)MathHelper.clamp_float(genBase.getFloatRainfall(), 0.0F, 1.0F);
            var2 *= var1;
            var1 = 1.0D - var1;
            var2 = 1.0D - var2;
            waterMult = waterColorBuff.getRGB((int)((double)(waterColorBuff.getWidth() - 1) * var1), (int)((double)(waterColorBuff.getHeight() - 1) * var2)) & 16777215;
         } catch (Exception var13) {
            ;
         }
      }

      if (waterMult != -1 && waterMult != 0) {
         waterRGB = this.colorMultiplier(waterRGB, waterMult | -16777216);
      } else {
         waterRGB = this.colorMultiplier(waterRGB, BiomeGenBase.forest.waterColorMultiplier | -16777216);
      }

      for(int t = 0; t < 16; ++t) {
         this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.flowingWaterID, t)] = waterRGB;
         this.blockColorsWithDefaultTint[this.blockColorID(BlockIDRepository.waterID, t)] = waterRGB;
      }

   }

   private final int blockColorID(int blockid, int meta) {
      return blockid | meta << 12;
   }

   public final int getBlockColorWithDefaultTint(int blockID, int metadata, int biomeID) {
      int col = this.blockColorsWithDefaultTint[this.blockColorID(blockID, metadata)];
      return col != COLOR_NOT_LOADED ? col : this.getBlockColor(blockID, metadata);
   }

   public final int getBlockColor(int blockID, int metadata, int biomeID) {
      if ((this.hdInstalled || this.optifineInstalled) && this.biomeTextureAvailable.contains(blockID)) {
         Integer col = (Integer)this.blockBiomeSpecificColors.get("" + this.blockColorID(blockID, metadata) + " " + biomeID);
         if (col != null) {
            return col.intValue();
         }
      }

      return this.getBlockColor(blockID, metadata);
   }

   private final int getBlockColor(int blockID, int metadata) {
      Object var3 = this.tpLoadLock;
      synchronized(this.tpLoadLock) {
         byte var10000;
         try {
            if (this.blockColors[this.blockColorID(blockID, metadata)] == COLOR_NOT_LOADED) {
               this.blockColors[this.blockColorID(blockID, metadata)] = this.getColor(blockID, metadata);
            }

            int col = this.blockColors[this.blockColorID(blockID, metadata)];
            int var8;
            if (col != -65025) {
               var8 = col;
               return var8;
            }

            if (this.blockColors[this.blockColorID(blockID, 0)] == COLOR_NOT_LOADED) {
               this.blockColors[this.blockColorID(blockID, 0)] = this.getColor(blockID, 0);
            }

            col = this.blockColors[this.blockColorID(blockID, 0)];
            if (col != -65025) {
               var8 = col;
               return var8;
            }

            var10000 = 0;
         } catch (ArrayIndexOutOfBoundsException var6) {
            return -65025;
         }

         return var10000;
      }
   }

   private int getColor(int blockID, int metadata, boolean retainTransparency) {
      int color = this.getColor(blockID, metadata);
      if (!retainTransparency) {
         color |= -16777216;
      }

      return color;
   }

   private int getColor(int blockID, int metadata) {
      try {
         IIcon icon = null;
         if (blockID == BlockIDRepository.redstoneID) {
            return 419430400 | (30 + metadata * 15 & 255) << 16 | 0 | 0;
         } else {
            icon = Block.getBlockById(blockID).getIcon(1, metadata);
            int color = this.iconToColor(icon, this.terrainBuff);
            if (Arrays.asList(BlockIDRepository.shapedIDS).contains(blockID)) {
               color = this.applyShape(blockID, metadata, color);
            }

            if ((color >> 24 & 255) < 27) {
               color |= 452984832;
            }

            if (blockID != BlockIDRepository.grassID && blockID != BlockIDRepository.leavesID && blockID != BlockIDRepository.leaves2ID && blockID != BlockIDRepository.tallGrassID && blockID != BlockIDRepository.reedsID && blockID != BlockIDRepository.vineID && blockID != BlockIDRepository.tallFlowerID && blockID != BlockIDRepository.waterID && blockID != BlockIDRepository.flowingWaterID) {
               int tint = Block.getBlockById(blockID).colorMultiplier(this.game.theWorld, this.game.thePlayer.serverPosX, 78, (int)this.game.thePlayer.posZ) | -16777216;
               if (tint != 16777215 && tint != -1) {
                  this.biomeTintsAvailable.add(blockID);
                  this.blockColorsWithDefaultTint[this.blockColorID(blockID, metadata)] = this.colorMultiplier(color, tint);
               }
            }

            return color;
         }
      } catch (Exception var6) {
         System.err.println("failed getting color: " + blockID + " " + metadata);
         var6.printStackTrace();
         return -65025;
      }
   }

   private int iconToColor(IIcon icon, BufferedImage imageBuff) {
      int color = 0;
      if (icon != null) {
         int left = (int)(icon.getMinU() * (float)imageBuff.getWidth());
         int right = (int)(icon.getMaxU() * (float)imageBuff.getWidth());
         int top = (int)(icon.getMinV() * (float)imageBuff.getHeight());
         int bottom = (int)(icon.getMaxV() * (float)imageBuff.getHeight());
         BufferedImage blockTexture = imageBuff.getSubimage(left, top, right - left, bottom - top);
         Image singlePixel = blockTexture.getScaledInstance(1, 1, 4);
         BufferedImage singlePixelBuff = new BufferedImage(1, 1, imageBuff.getType());
         Graphics gfx = singlePixelBuff.createGraphics();
         gfx.drawImage(singlePixel, 0, 0, (ImageObserver)null);
         gfx.dispose();
         color = singlePixelBuff.getRGB(0, 0);
      }

      return color;
   }

   private int applyShape(int blockID, int metadata, int color) {
      int alpha = color >> 24 & 255;
      int red = color >> 16 & 255;
      int green = color >> 8 & 255;
      int blue = color >> 0 & 255;
      if (blockID != BlockIDRepository.signID && blockID != BlockIDRepository.wallSignID) {
         if (blockID != BlockIDRepository.woodDoorID && blockID != BlockIDRepository.ironDoorID) {
            if (blockID != BlockIDRepository.ladderID && blockID != BlockIDRepository.vineID) {
               if (blockID != BlockIDRepository.stoneButtonID && blockID != BlockIDRepository.woodButtonID) {
                  if (blockID != BlockIDRepository.fenceID && blockID != BlockIDRepository.netherFenceID) {
                     if (blockID == BlockIDRepository.fenceGateID) {
                        alpha = 92;
                     } else if (blockID == BlockIDRepository.cobbleWallID) {
                        alpha = 153;
                     }
                  } else {
                     alpha = 95;
                  }
               } else {
                  alpha = 11;
               }
            } else {
               alpha = 15;
            }
         } else {
            alpha = 47;
         }
      } else {
         alpha = 31;
      }

      color = (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
      return color;
   }

   public int colorMultiplier(int color1, int color2) {
      int alpha1 = color1 >> 24 & 255;
      int red1 = color1 >> 16 & 255;
      int green1 = color1 >> 8 & 255;
      int blue1 = color1 >> 0 & 255;
      int alpha2 = color2 >> 24 & 255;
      int red2 = color2 >> 16 & 255;
      int green2 = color2 >> 8 & 255;
      int blue2 = color2 >> 0 & 255;
      int alpha = alpha1 * alpha2 / 255;
      int red = red1 * red2 / 255;
      int green = green1 * green2 / 255;
      int blue = blue1 * blue2 / 255;
      return (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
   }

   public int colorAdder(int color1, int color2) {
      float topAlpha = (float)(color1 >> 24 & 255) / 255.0F;
      float red1 = (float)(color1 >> 16 & 255) * topAlpha;
      float green1 = (float)(color1 >> 8 & 255) * topAlpha;
      float blue1 = (float)(color1 >> 0 & 255) * topAlpha;
      float bottomAlpha = (float)(color2 >> 24 & 255) / 255.0F;
      float red2 = (float)(color2 >> 16 & 255) * bottomAlpha * (1.0F - topAlpha);
      float green2 = (float)(color2 >> 8 & 255) * bottomAlpha * (1.0F - topAlpha);
      float blue2 = (float)(color2 >> 0 & 255) * bottomAlpha * (1.0F - topAlpha);
      float alpha = topAlpha + bottomAlpha * (1.0F - topAlpha);
      float red = (red1 + red2) / alpha;
      float green = (green1 + green2) / alpha;
      float blue = (blue1 + blue2) / alpha;
      return ((int)(alpha * 255.0F) & 255) << 24 | ((int)red & 255) << 16 | ((int)green & 255) << 8 | (int)blue & 255;
   }

   private void processCTM() {
      this.renderPassThreeBlendMode = "alpha";
      Properties properties = new Properties();
      ResourceLocation propertiesFile = new ResourceLocation("minecraft", "mcpatcher/renderpass.properties");

      try {
         InputStream input = this.game.getResourceManager().getResource(propertiesFile).getInputStream();
         if (input != null) {
            properties.load(input);
            input.close();
            this.renderPassThreeBlendMode = properties.getProperty("blend.3");
         }
      } catch (IOException var9) {
         this.renderPassThreeBlendMode = "alpha";
      }

      String namespace = "minecraft";
      Iterator var4 = this.findResources(namespace, "/mcpatcher/ctm", ".properties", true, false, true).iterator();

      while(var4.hasNext()) {
         ResourceLocation s = (ResourceLocation)var4.next();

         try {
            this.loadCTM(s);
         } catch (NumberFormatException var7) {
            ;
         } catch (IllegalArgumentException var8) {
            ;
         }
      }

      for(int t = 0; t < this.blockColors.length; ++t) {
         if (this.blockColors[t] != -65025 && this.blockColors[t] != COLOR_NOT_LOADED && (this.blockColors[t] >> 24 & 255) < 27) {
            this.blockColors[t] |= 452984832;
         }
      }

   }

   private void loadCTM(ResourceLocation propertiesFile) {
      if (propertiesFile != null) {
         Properties properties = new Properties();

         try {
            InputStream input = this.game.getResourceManager().getResource(propertiesFile).getInputStream();
            if (input != null) {
               properties.load(input);
               input.close();
            }
         } catch (IOException var34) {
            return;
         }

         RenderBlocks renderBlocks = new RenderBlocks();
         String filePath = propertiesFile.getResourcePath();
         String method = properties.getProperty("method", "").trim().toLowerCase();
         String faces = properties.getProperty("faces", "").trim().toLowerCase();
         String matchBlocks = properties.getProperty("matchBlocks", "").trim().toLowerCase();
         String matchTiles = properties.getProperty("matchTiles", "").trim().toLowerCase();
         String metadata = properties.getProperty("metadata", "").trim().toLowerCase();
         String tiles = properties.getProperty("tiles", "").trim();
         String biomes = properties.getProperty("biomes", "").trim().toLowerCase();
         String renderPass = properties.getProperty("renderPass", "").trim().toLowerCase();
         String[] blockNames = this.parseStringList(matchBlocks);
         int[] blockInts = new int[blockNames.length];

         for(int t = 0; t < blockNames.length; ++t) {
            blockInts[t] = this.parseBlockName(blockNames[t]);
         }

         int[] metadataInts = this.parseIntegerList(metadata, 0, 255);
         String directory = filePath.substring(0, filePath.lastIndexOf("/") + 1);
         String[] tilesParsed = this.parseStringList(tiles);
         String tilePath = directory + "0";
         if (tilesParsed.length > 0) {
            tilePath = tilesParsed[0].trim();
         }

         if (tilePath.startsWith("~")) {
            tilePath = tilePath.replace("~", "mcpatcher");
         } else {
            tilePath = directory + tilePath;
         }

         if (!tilePath.toLowerCase().endsWith(".png")) {
            tilePath = tilePath + ".png";
         }

         String[] biomesArray = biomes.split(" ");
         int t;
         int r;
         int s;
         if (blockInts.length == 0) {
            t = -1;
            Pattern pattern = Pattern.compile(".*/block([\\d]+)[a-zA-Z]*.properties");
            Matcher matcher = pattern.matcher(filePath);
            if (matcher.find()) {
               t = Integer.parseInt(matcher.group(1));
            } else {
               String tileNameToMatch = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".properties"));

               for(int t = 0; t < 4096; ++t) {
                  Block block = (Block)Block.blockRegistry.getObjectForID(t);
                  if (block != null) {
                     String tileNameOfBlock = "";
                     if (metadataInts.length > 0) {
                        for(s = 0; s < metadataInts.length; ++s) {
                           try {
                              tileNameOfBlock = renderBlocks.getBlockIconFromSideAndMetadata(block, 1, metadataInts[s]).getIconName();
                           } catch (Exception var31) {
                              tileNameOfBlock = "";
                           }

                           if (tileNameOfBlock.equals(tileNameToMatch)) {
                              t = t;
                           }
                        }
                     } else {
                        ArrayList tmpList = new ArrayList();

                        for(r = 0; r < 16; ++r) {
                           try {
                              tileNameOfBlock = renderBlocks.getBlockIconFromSideAndMetadata(block, 1, r).getIconName();
                           } catch (Exception var30) {
                              tileNameOfBlock = "";
                           }

                           if (tileNameOfBlock.equals(tileNameToMatch)) {
                              t = t;
                              tmpList.add(r);
                           }
                        }

                        metadataInts = new int[tmpList.size()];

                        for(r = 0; r < metadataInts.length; ++r) {
                           metadataInts[r] = ((Integer)tmpList.get(r)).intValue();
                        }
                     }
                  }
               }
            }

            if (t != -1) {
               blockInts = new int[]{t};
            }
         }

         if (metadataInts.length == 0) {
            metadataInts = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
         }

         if (blockInts.length != 0) {
            if (!method.equals("horizontal") && (method.equals("sandstone") || method.equals("top") || faces.contains("top") || faces.contains("all") || faces.length() == 0)) {
               try {
                  for(t = 0; t < blockInts.length; ++t) {
                     ResourceLocation pngResource = new ResourceLocation(propertiesFile.getResourceDomain(), tilePath);
                     InputStream is = this.game.getResourceManager().getResource(pngResource).getInputStream();
                     Image top = ImageIO.read(is);
                     is.close();
                     Image top = top.getScaledInstance(1, 1, 4);
                     BufferedImage topBuff = new BufferedImage(top.getWidth((ImageObserver)null), top.getHeight((ImageObserver)null), 6);
                     Graphics gfx = topBuff.createGraphics();
                     gfx.drawImage(top, 0, 0, (ImageObserver)null);
                     gfx.dispose();
                     int topRGB = topBuff.getRGB(0, 0);
                     if (blockInts[t] == BlockIDRepository.cobwebID) {
                        topRGB |= -16777216;
                     }

                     if (renderPass.equals("3")) {
                        topRGB = this.processRenderPassThree(topRGB);
                        s = this.blockColors[this.blockColorID(blockInts[t], metadataInts[0])];
                        if (s != -65025 && s != COLOR_NOT_LOADED) {
                           topRGB = this.colorMultiplier(s, topRGB);
                        }
                     }

                     if (Arrays.asList(BlockIDRepository.shapedIDS).contains(blockInts[t])) {
                        topRGB = this.applyShape(blockInts[t], metadataInts[0], topRGB);
                     }

                     for(s = 0; s < metadataInts.length; ++s) {
                        try {
                           if (!biomes.equals("")) {
                              this.biomeTextureAvailable.add(blockInts[t]);

                              for(r = 0; r < biomesArray.length; ++r) {
                                 int biomeInt = this.parseBiomeName(biomesArray[r]);
                                 if (biomeInt != -1) {
                                    this.blockBiomeSpecificColors.put("" + this.blockColorID(blockInts[t], metadataInts[s]) + " " + biomeInt, topRGB);
                                 }
                              }
                           } else {
                              this.blockColors[this.blockColorID(blockInts[t], metadataInts[s])] = topRGB;
                           }
                        } catch (Exception var32) {
                           System.err.println("blockID + metadata (" + blockInts[t] + ", " + metadataInts[s] + ") out of range");
                        }
                     }
                  }
               } catch (IOException var33) {
                  System.err.println("error getting CTM block: " + filePath + " " + blockInts[0] + " " + metadataInts[0] + " " + tilePath);
               }
            }

         }
      }
   }

   private int processRenderPassThree(int rgb) {
      if (this.renderPassThreeBlendMode.equals("color") || this.renderPassThreeBlendMode.equals("overlay")) {
         int alpha = rgb >> 24 & 255;
         int red = rgb >> 16 & 255;
         int green = rgb >> 8 & 255;
         int blue = rgb >> 0 & 255;
         float colorAverage = (float)(red + blue + green) / 3.0F;
         float lighteningFactor = (colorAverage - 127.5F) * 2.0F;
         red += (int)((float)red * (lighteningFactor / 255.0F));
         blue += (int)((float)red * (lighteningFactor / 255.0F));
         green += (int)((float)red * (lighteningFactor / 255.0F));
         int newAlpha = (int)Math.abs(lighteningFactor);
         rgb = newAlpha << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
      }

      return rgb;
   }

   private int[] parseIntegerList(String list, int minValue, int maxValue) {
      ArrayList tmpList = new ArrayList();
      String[] var5 = list.replace(',', ' ').split("\\s+");
      int i = var5.length;

      for(int var7 = 0; var7 < i; ++var7) {
         String token = var5[var7];
         token = token.trim();

         try {
            if (token.matches("^\\d+$")) {
               tmpList.add(Integer.parseInt(token));
            } else {
               String[] t;
               int id;
               int max;
               if (token.matches("^\\d+-\\d+$")) {
                  t = token.split("-");
                  id = Integer.parseInt(t[0]);
                  max = Integer.parseInt(t[1]);

                  for(int i = id; i <= max; ++i) {
                     tmpList.add(i);
                  }
               } else if (token.matches("^\\d+:\\d+$")) {
                  t = token.split(":");
                  id = Integer.parseInt(t[0]);
                  max = Integer.parseInt(t[1]);
                  tmpList.add(id);
               }
            }
         } catch (NumberFormatException var13) {
            ;
         }
      }

      if (minValue <= maxValue) {
         int i = 0;

         label48:
         while(true) {
            while(true) {
               if (i >= tmpList.size()) {
                  break label48;
               }

               if (((Integer)tmpList.get(i)).intValue() >= minValue && ((Integer)tmpList.get(i)).intValue() <= maxValue) {
                  ++i;
               } else {
                  tmpList.remove(i);
               }
            }
         }
      }

      int[] a = new int[tmpList.size()];

      for(i = 0; i < a.length; ++i) {
         a[i] = ((Integer)tmpList.get(i)).intValue();
      }

      return a;
   }

   private String[] parseStringList(String list) {
      ArrayList tmpList = new ArrayList();
      String[] a = list.replace(',', ' ').split("\\s+");
      int i = a.length;

      for(int var5 = 0; var5 < i; ++var5) {
         String token = a[var5];
         token = token.trim();

         try {
            if (token.matches("^\\d+$")) {
               tmpList.add("" + Integer.parseInt(token));
            } else if (token.matches("^\\d+-\\d+$")) {
               String[] t = token.split("-");
               int min = Integer.parseInt(t[0]);
               int max = Integer.parseInt(t[1]);

               for(int i = min; i <= max; ++i) {
                  tmpList.add("" + i);
               }
            } else if (token != null && token != "") {
               tmpList.add(token);
            }
         } catch (NumberFormatException var11) {
            ;
         }
      }

      a = new String[tmpList.size()];

      for(i = 0; i < a.length; ++i) {
         a[i] = (String)tmpList.get(i);
      }

      return a;
   }

   private int parseBiomeName(String name) {
      if (name.matches("^\\d+$")) {
         return Integer.parseInt(name);
      } else {
         for(int t = 0; t < BiomeGenBase.getBiomeGenArray().length; ++t) {
            if (BiomeGenBase.getBiomeGenArray()[t] != null && BiomeGenBase.getBiomeGenArray()[t].biomeName.toLowerCase().replace(" ", "").equalsIgnoreCase(name)) {
               return t;
            }
         }

         return -1;
      }
   }

   private List getResourcePacks(String namespace) {
      List list = new ArrayList();
      IResourceManager superResourceManager = this.game.getResourceManager();
      if (superResourceManager instanceof SimpleReloadableResourceManager) {
         java.util.Map nameSpaceToResourceManager = null;
         Object nameSpaceToResourceManagerObj = ReflectionUtils.getPrivateFieldValueByType(superResourceManager, SimpleReloadableResourceManager.class, java.util.Map.class);
         if (nameSpaceToResourceManagerObj == null) {
            return list;
         }

         nameSpaceToResourceManager = (java.util.Map)nameSpaceToResourceManagerObj;
         Iterator var6 = nameSpaceToResourceManager.entrySet().iterator();

         label29:
         while(true) {
            Entry entry;
            do {
               if (!var6.hasNext()) {
                  break label29;
               }

               entry = (Entry)var6.next();
            } while(namespace != null && !namespace.equals(entry.getKey()));

            FallbackResourceManager resourceManager = (FallbackResourceManager)entry.getValue();
            List resourcePacks = null;
            Object resourcePacksObj = ReflectionUtils.getPrivateFieldValueByType(resourceManager, FallbackResourceManager.class, List.class);
            if (resourcePacksObj == null) {
               return list;
            }

            resourcePacks = (List)resourcePacksObj;
            list.addAll(resourcePacks);
         }
      }

      Collections.reverse(list);
      return list;
   }

   private List findResources(String namespace, String directory, String suffix, boolean recursive, boolean directories, boolean sortByFilename) {
      if (directory == null) {
         directory = "";
      }

      if (directory.startsWith("/")) {
         directory = directory.substring(1);
      }

      if (suffix == null) {
         suffix = "";
      }

      ArrayList resources = new ArrayList();
      Iterator var8 = this.getResourcePacks(namespace).iterator();

      while(var8.hasNext()) {
         IResourcePack resourcePack = (IResourcePack)var8.next();
         if (!(resourcePack instanceof DefaultResourcePack)) {
            Object baseObj;
            if (resourcePack instanceof FileResourcePack) {
               baseObj = ReflectionUtils.getPrivateFieldValueByType(resourcePack, FileResourcePack.class, ZipFile.class);
               if (baseObj == null) {
                  return resources;
               }

               ZipFile zipFile = (ZipFile)baseObj;
               if (zipFile != null) {
                  this.findResourcesZip(zipFile, namespace, "assets/" + namespace, directory, suffix, recursive, directories, resources);
               }
            } else if (resourcePack instanceof AbstractResourcePack) {
               baseObj = ReflectionUtils.getPrivateFieldValueByType(resourcePack, AbstractResourcePack.class, File.class);
               if (baseObj == null) {
                  return resources;
               }

               File base = (File)baseObj;
               if (base != null && base.isDirectory()) {
                  base = new File(base, "assets/" + namespace);
                  if (base.isDirectory()) {
                     findResourcesDirectory(base, namespace, directory, suffix, recursive, directories, resources);
                  }
               }
            }
         }
      }

      if (sortByFilename) {
         Collections.sort(resources, new Comparator() {
            public int compare(ResourceLocation o1, ResourceLocation o2) {
               String f1 = o1.getResourcePath().replaceAll(".*/", "").replaceFirst("\\.properties", "");
               String f2 = o2.getResourcePath().replaceAll(".*/", "").replaceFirst("\\.properties", "");
               int result = f1.compareTo(f2);
               return result != 0 ? result : o1.getResourcePath().compareTo(o2.getResourcePath());
            }
         });
      } else {
         Collections.sort(resources, new Comparator() {
            public int compare(ResourceLocation o1, ResourceLocation o2) {
               return o1.getResourcePath().compareTo(o2.getResourcePath());
            }
         });
      }

      return resources;
   }

   private void findResourcesZip(ZipFile zipFile, String namespace, String root, String directory, String suffix, boolean recursive, boolean directories, Collection resources) {
      String base = root + "/" + directory;
      Iterator var10 = Collections.list(zipFile.entries()).iterator();

      while(true) {
         String name;
         do {
            while(true) {
               do {
                  do {
                     ZipEntry entry;
                     do {
                        if (!var10.hasNext()) {
                           return;
                        }

                        entry = (ZipEntry)var10.next();
                     } while(entry.isDirectory() != directories);

                     name = entry.getName().replaceFirst("^/", "");
                  } while(!name.startsWith(base));
               } while(!name.endsWith(suffix));

               if (directory.equals("")) {
                  break;
               }

               String subpath = name.substring(base.length());
               if ((subpath.equals("") || subpath.startsWith("/")) && (recursive || subpath.equals("") || !subpath.substring(1).contains("/"))) {
                  resources.add(new ResourceLocation(namespace, name.substring(root.length() + 1)));
               }
            }
         } while(!recursive && name.contains("/"));

         resources.add(new ResourceLocation(namespace, name));
      }
   }

   private void processColorProperties() {
      List unusedPNGs = new ArrayList();
      unusedPNGs.addAll(this.findResources("minecraft", "/mcpatcher/colormap/blocks", ".png", true, false, true));
      Properties properties = new Properties();

      try {
         InputStream input = this.game.getResourceManager().getResource(new ResourceLocation("mcpatcher/color.properties")).getInputStream();
         if (input != null) {
            properties.load(input);
            input.close();
         }
      } catch (IOException var10) {
         ;
      }

      int lilypadMultiplier = 2129968;
      String lilypadMultiplierString = properties.getProperty("lilypad");
      if (lilypadMultiplierString != null) {
         lilypadMultiplier = Integer.parseInt(lilypadMultiplierString, 16);
      }

      for(int t = 0; t < 16; ++t) {
         this.blockColors[this.blockColorID(BlockIDRepository.lilypadID, t)] = this.colorMultiplier(this.getBlockColor(BlockIDRepository.lilypadID, t), lilypadMultiplier | -16777216);
      }

      Enumeration e = properties.propertyNames();

      String name;
      while(e.hasMoreElements()) {
         String key = (String)e.nextElement();
         if (key.startsWith("palette.block")) {
            name = key.substring("palette.block.".length());
            name = name.replace("~", "mcpatcher");
            this.processColorProperty(new ResourceLocation(name), properties.getProperty(key));
         }
      }

      Iterator var14 = this.findResources("minecraft", "/mcpatcher/colormap/blocks", ".properties", true, false, true).iterator();

      ResourceLocation resource;
      while(var14.hasNext()) {
         resource = (ResourceLocation)var14.next();
         Properties colorProperties = new Properties();

         try {
            InputStream input = this.game.getResourceManager().getResource(resource).getInputStream();
            if (input != null) {
               properties.load(input);
               input.close();
            }
         } catch (IOException var11) {
            break;
         }

         String names = colorProperties.getProperty("blocks");
         ResourceLocation resourcePNG = new ResourceLocation(resource.getResourceDomain(), resource.getResourcePath().replace(".properties", ".png"));
         unusedPNGs.remove(resourcePNG);
         this.processColorProperty(resourcePNG, names);
      }

      var14 = unusedPNGs.iterator();

      while(var14.hasNext()) {
         resource = (ResourceLocation)var14.next();
         name = resource.getResourcePath();
         System.out.println("processing name: " + name);
         name = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf(".png"));
         System.out.println("processed name: " + name);
         this.processColorProperty(resource, "minecraft:" + name);
      }

   }

   private void processColorProperty(ResourceLocation resource, String list) {
      Integer[] tints = new Integer[BiomeGenBase.getBiomeGenArray().length];
      boolean swamp = resource.getResourcePath().contains("/swampgrass") || resource.getResourcePath().contains("/swampfoliage");
      BufferedImage tintColors = null;

      try {
         InputStream is = this.game.getResourceManager().getResource(resource).getInputStream();
         tintColors = ImageIO.read(is);
         is.close();
      } catch (IOException var19) {
         return;
      }

      for(int t = 0; t < BiomeGenBase.getBiomeGenArray().length; ++t) {
         tints[t] = Integer.valueOf(-1);
      }

      BufferedImage tintColorsBuff = new BufferedImage(tintColors.getWidth((ImageObserver)null), tintColors.getHeight((ImageObserver)null), 1);
      Graphics gfx = tintColorsBuff.createGraphics();
      gfx.drawImage(tintColors, 0, 0, (ImageObserver)null);
      gfx.dispose();

      for(int t = 0; t < BiomeGenBase.getBiomeGenArray().length; ++t) {
         if (BiomeGenBase.getBiomeGenArray()[t] != null) {
            BiomeGenBase genBase = BiomeGenBase.getBiomeGenArray()[t];
            double var1 = (double)MathHelper.clamp_float(genBase.getFloatTemperature(0, 64, 0), 0.0F, 1.0F);
            double var2 = (double)MathHelper.clamp_float(genBase.getFloatRainfall(), 0.0F, 1.0F);
            var2 *= var1;
            var1 = 1.0D - var1;
            var2 = 1.0D - var2;
            int tintMult = tintColorsBuff.getRGB((int)((double)(tintColorsBuff.getWidth() - 1) * var1), (int)((double)(tintColorsBuff.getHeight() - 1) * var2)) & 16777215;
            if (tintMult != 0 && (!swamp || t == BiomeGenBase.swampland.biomeID)) {
               tints[t] = tintMult;
            }
         }
      }

      String[] var23 = list.split("\\s+");
      int var24 = var23.length;

      for(int var25 = 0; var25 < var24; ++var25) {
         String token = var23[var25];
         token = token.trim();
         String metadataString = "";
         int id = true;
         int[] metadata = new int[0];

         try {
            String name;
            int t;
            if (token.matches(".*:[-0-9, ]+")) {
               t = token.lastIndexOf(58);
               metadataString = token.substring(t + 1);
               name = token.substring(0, t);
            } else {
               name = token;
            }

            int id = this.parseBlockName(name);
            if (id > 0) {
               this.biomeTintsAvailable.add(id);
               metadata = this.parseIntegerList(metadataString, 0, 15);
               if (metadata.length == 0) {
                  metadata = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
               }

               for(t = 0; t < metadata.length; ++t) {
                  Integer[] previousTints = (Integer[])((Integer[])this.blockTintTables.get(id + " " + metadata[t]));
                  if (swamp && previousTints == null) {
                     ResourceLocation defaultResource;
                     if (resource.getResourcePath().endsWith("/swampgrass.png")) {
                        defaultResource = new ResourceLocation("textures/colormap/grass.png");
                     } else {
                        defaultResource = new ResourceLocation("textures/colormap/foliage.png");
                     }

                     this.processColorProperty(defaultResource, "" + id + ":" + metadata[t]);
                     previousTints = (Integer[])((Integer[])this.blockTintTables.get(id + " " + metadata[t]));
                  }

                  if (previousTints != null) {
                     for(int s = 0; s < BiomeGenBase.getBiomeGenArray().length; ++s) {
                        if (tints[s].intValue() == -1) {
                           tints[s] = previousTints[s];
                        }
                     }
                  }

                  this.blockColorsWithDefaultTint[this.blockColorID(id, metadata[t])] = this.colorMultiplier(this.getBlockColor(id, metadata[t]), tints[4].intValue() | -16777216);
                  this.blockTintTables.put(id + " " + metadata[t], tints);
               }
            }
         } catch (NumberFormatException var20) {
            ;
         }
      }

   }

   private int parseBlockName(String name) {
      Block block = Block.getBlockFromName(name);
      return block != null ? Block.getIdFromBlock(block) : -1;
   }
}
