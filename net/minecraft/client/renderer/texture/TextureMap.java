package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.Item;
import net.minecraft.src.Config;
import net.minecraft.src.ConnectedTextures;
import net.minecraft.src.CustomItems;
import net.minecraft.src.TextureUtils;
import net.minecraft.src.WrUpdates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureMap extends AbstractTexture implements ITickableTextureObject, IIconRegister {
   private static final Logger logger = LogManager.getLogger();
   public static final ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
   public static final ResourceLocation locationItemsTexture = new ResourceLocation("textures/atlas/items.png");
   private final List listAnimatedSprites;
   private final Map mapRegisteredSprites;
   private final Map mapUploadedSprites;
   public final int textureType;
   public final String basePath;
   private int field_147636_j;
   private int field_147637_k;
   private final TextureAtlasSprite missingImage;
   public static TextureMap textureMapBlocks = null;
   public static TextureMap textureMapItems = null;
   private TextureAtlasSprite[] iconGrid;
   private int iconGridSize;
   private int iconGridCountX;
   private int iconGridCountY;
   private double iconGridSizeU;
   private double iconGridSizeV;
   private static final boolean ENABLE_SKIP = Boolean.parseBoolean(System.getProperty("fml.skipFirstTextureLoad", "true"));
   private boolean skipFirst;
   private String[] cancelPack;
   private String[] blackList;

   public TextureMap(int par1, String par2Str) {
      this(par1, par2Str, false);
   }

   public TextureMap(int par1, String par2Str, boolean skipFirst) {
      this.cancelPack = new String[]{"cobblestone", "stone", "grass_side", "grass_top"};
      this.blackList = new String[]{"bedrock", "bookshelf", "brick", "clay", "coal_block", "coal_ore", "cobblestone", "cobblestone_mossy", "command_block", "crafting_table_front", "crafting_table_side", "crafting_table_top", " diamond_block", "diamond_ore", "dirt", "dirt_podzol_side", "dirt_podzol_top", "dispenser_front_horizontal", "dispenser_front_vertical", "dropper_front_horizontal", "dropper_front_vertical", "emerald_block", "emerald_ore", "end_stone", "furnace_front_off", " furnace_front_on", "furnace_side", "furnace_top", "glowstone", "gold_block", "gold_ore", "grass_side", "grass_side_snowed", "grass_top", "hardened_clay", "hardened_clay_stained_black", "hardened_clay_stained_blue", "hardened_clay_stained_brown", "hardened_clay_stained_cyan", "hardened_clay_stained_gray", "hardened_clay_stained_green", "hardened_clay_stained_light_blue", "hardened_clay_stained_lime", "hardened_clay_stained_magenta", "hardened_clay_stained_orange", "hardened_clay_stained_pink", "hardened_clay_stained_purple", "hardened_clay_stained_red", "hardened_clay_stained_silver", "hardened_clay_stained_white", "hardened_clay_stained_yellow", "hay_block_top", "hay_block_side", "ice_packed", "iron_block", "iron_ore", "jukebox_side", "jukebox_top", "lapis_block", "lapis_ore", "leaves_acacia_opaque", "leaves_big_oak_opaque", "leaves_birch_opaque", "leaves_jungle_opaque", "leaves_oak_opaque", "leaves_spruce_opaque", "log_acacia", "log_big_oak", "log_birch", "log_jugle", "log_oak", "log_spruce", "log_acacia_top", "log_big_oak_top", "log_birch_top", "log_jugle_top", "log_oak_top", "log_spruce_top", "melon_side", "melon_top", "mushroom_block_inside", "mushroom_block_skin_brown", "mushroom_block_skin_red", "mushroom_block_skin_stem", "mycelium_side", "mycelium_top", "nether_brick", "netherrack", "noteblock", "obsidian", "planks_acacia", "planks_big_oak", "planks_birch", "planks_jungle", "planks_oak", "planks_spruce", "pumpkin_side", "pumpkin_top", "quartz_block_bottom", "quartz_block_chiseled", "quartz_block_chiseled_top", "quartz_block_lines", "quartz_block_lines_top", "quartz_block_side", "quartz_block_top", "quartz_ore", "red_sand", "redstone_block", "redstone_lamp_off", "redstone_lamp_on", "redstone_ore", "sand", "sandstone_bottom", "sandstone_carved", "sandstone_normal", "sandstone_top", "snow", "soul_sand", "sponge", "stone", "stone_slab_side", "stone_slab_top", "stonebrick", "stonebrick_carved", "stonebrick_cracked", "stonebrick_mossy", "tnt_bottom", "tnt_side", "tnt_top", "wool_colored_black", "wool_colored_blue", "wool_colored_brown", "wool_colored_cyan", "wool_colored_gray", "wool_colored_green", "wool_colored_light_blue", "wool_colored_lime", "wool_colored_magenta", "wool_colored_orange", "wool_colored_pink", "wool_colored_purple", "wool_colored_red", "wool_colored_silver", "wool_colored_white", "wool_colored_yellow"};
      this.listAnimatedSprites = Lists.newArrayList();
      this.mapRegisteredSprites = Maps.newHashMap();
      this.mapUploadedSprites = Maps.newHashMap();
      this.field_147637_k = 1;
      this.missingImage = new TextureAtlasSprite("missingno");
      this.iconGrid = null;
      this.iconGridSize = -1;
      this.iconGridCountX = -1;
      this.iconGridCountY = -1;
      this.iconGridSizeU = -1.0D;
      this.iconGridSizeV = -1.0D;
      this.skipFirst = false;
      this.textureType = par1;
      this.basePath = par2Str;
      if (this.textureType == 0) {
         textureMapBlocks = this;
      }

      if (this.textureType == 1) {
         textureMapItems = this;
      }

      this.registerIcons();
      this.skipFirst = skipFirst && ENABLE_SKIP;
   }

   private void initMissingImage() {
      int[] var1;
      if ((float)this.field_147637_k > 1.0F) {
         boolean var5 = true;
         boolean var3 = true;
         boolean var4 = true;
         this.missingImage.setIconWidth(32);
         this.missingImage.setIconHeight(32);
         var1 = new int[1024];
         System.arraycopy(TextureUtil.missingTextureData, 0, var1, 0, TextureUtil.missingTextureData.length);
         TextureUtil.func_147948_a(var1, 16, 16, 8);
      } else {
         var1 = TextureUtil.missingTextureData;
         this.missingImage.setIconWidth(16);
         this.missingImage.setIconHeight(16);
      }

      int[][] var51 = new int[this.field_147636_j + 1][];
      var51[0] = var1;
      this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][]{var51}));
      this.missingImage.setIndexInMap(0);
   }

   public void loadTexture(IResourceManager par1ResourceManager) throws IOException {
      this.initMissingImage();
      this.func_147631_c();
      this.loadTextureAtlas(par1ResourceManager);
   }

   private boolean check(String blockname) {
      String[] var2 = this.blackList;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String s = var2[var4];
         if (blockname.equalsIgnoreCase(s)) {
            return true;
         }
      }

      return false;
   }

   private boolean checkCancel(String blockName) {
      String[] var2 = this.cancelPack;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String s = var2[var4];
         if (blockName.equalsIgnoreCase(s)) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasTransparency(BufferedImage img) {
      int width = img.getWidth();
      int height = img.getHeight();

      for(int x = 0; x < width; ++x) {
         for(int y = 0; y < height; ++y) {
            int rgb = img.getRGB(x, y);
            int alpha = rgb >> 24 & 255;
            if (alpha != 255) {
               return true;
            }
         }
      }

      return false;
   }

   public void loadTextureAtlas(IResourceManager par1ResourceManager) {
      Config.dbg("Loading texture map: " + this.basePath);
      WrUpdates.finishCurrentUpdate();
      this.registerIcons();
      int var2 = Minecraft.getGLMaximumTextureSize();
      Stitcher var3 = new Stitcher(var2, var2, true, 0, this.field_147636_j);
      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
      int var4 = Integer.MAX_VALUE;
      Iterator var5 = this.mapRegisteredSprites.entrySet().iterator();

      TextureAtlasSprite var8;
      while(var5.hasNext() && !this.skipFirst) {
         Entry var24 = (Entry)var5.next();
         ResourceLocation var25 = new ResourceLocation((String)var24.getKey());
         var8 = (TextureAtlasSprite)var24.getValue();
         ResourceLocation sheetWidth = this.func_147634_a(var25, 0);
         if (var8.hasCustomLoader(par1ResourceManager, var25)) {
            if (!var8.load(par1ResourceManager, var25)) {
               var4 = Math.min(var4, Math.min(var8.getIconWidth(), var8.getIconHeight()));
               var3.addSprite(var8);
            }

            Config.dbg("Custom loader: " + var8);
         } else {
            try {
               IResource sheetHeight = par1ResourceManager.getResource(sheetWidth);
               BufferedImage[] debugImage = new BufferedImage[1 + this.field_147636_j];
               debugImage[0] = ImageIO.read(sheetHeight.getInputStream());
               boolean transparency = hasTransparency(debugImage[0]);
               var8.setHasTransparency(transparency);
               int x;
               if (transparency && this.check((String)var24.getKey())) {
                  int width = debugImage[0].getWidth();
                  int height = debugImage[0].getHeight();
                  boolean checkCancel = this.checkCancel((String)var24.getKey());

                  for(x = 0; x < width; ++x) {
                     for(int y = 0; y < height; ++y) {
                        int rgb = debugImage[0].getRGB(x, y);
                        int alpha = rgb >> 24 & 255;
                        if (alpha != 255) {
                           if (checkCancel) {
                              Minecraft mc = Minecraft.getMinecraft();
                              mc.getResourcePackRepository().func_148527_a(Lists.newArrayList());
                              mc.gameSettings.resourcePacks.clear();
                              mc.gameSettings.saveOptions();
                              mc.refreshResources();
                              return;
                           }

                           debugImage[0].setRGB(x, y, (new Color(0, 0, 0)).getRGB());
                        }
                     }
                  }
               }

               TextureMetadataSection var26 = (TextureMetadataSection)sheetHeight.getMetadata("texture");
               if (var26 != null) {
                  List var28 = var26.func_148535_c();
                  int var30;
                  if (!var28.isEmpty()) {
                     x = debugImage[0].getWidth();
                     var30 = debugImage[0].getHeight();
                     if (MathHelper.roundUpToPowerOfTwo(x) != x || MathHelper.roundUpToPowerOfTwo(var30) != var30) {
                        throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                     }
                  }

                  Iterator var182 = var28.iterator();

                  while(var182.hasNext()) {
                     var30 = ((Integer)var182.next()).intValue();
                     if (var30 > 0 && var30 < debugImage.length - 1 && debugImage[var30] == null) {
                        ResourceLocation var32 = this.func_147634_a(var25, var30);

                        try {
                           debugImage[var30] = ImageIO.read(par1ResourceManager.getResource(var32).getInputStream());
                        } catch (IOException var23) {
                           logger.error("Unable to load miplevel {} from: {}", new Object[]{var30, var32, var23});
                        }
                     }
                  }
               }

               AnimationMetadataSection var281 = (AnimationMetadataSection)sheetHeight.getMetadata("animation");
               var8.func_147964_a(debugImage, var281, (float)this.field_147637_k > 1.0F);
            } catch (RuntimeException var25) {
               logger.error("Unable to parse metadata from " + sheetWidth, var25);
               continue;
            } catch (IOException var26) {
               logger.error("Using missing texture, unable to load " + sheetWidth + ", " + var26.getClass().getName());
               continue;
            }

            var4 = Math.min(var4, Math.min(var8.getIconWidth(), var8.getIconHeight()));
            var3.addSprite(var8);
         }
      }

      int var241 = MathHelper.calculateLogBaseTwo(var4);
      if (var241 < 0) {
         var241 = 0;
      }

      if (var241 < this.field_147636_j) {
         logger.info("{}: dropping miplevel from {} to {}, because of minTexel: {}", new Object[]{this.basePath, this.field_147636_j, var241, var4});
         this.field_147636_j = var241;
      }

      Iterator var251 = this.mapRegisteredSprites.values().iterator();

      while(var251.hasNext() && !this.skipFirst) {
         final TextureAtlasSprite sheetWidth1 = (TextureAtlasSprite)var251.next();

         try {
            sheetWidth1.func_147963_d(this.field_147636_j);
         } catch (Throwable var22) {
            CrashReport debugImage1 = CrashReport.makeCrashReport(var22, "Applying mipmap");
            CrashReportCategory var261 = debugImage1.makeCategory("Sprite being mipmapped");
            var261.addCrashSectionCallable("Sprite name", new Callable() {
               public String call() {
                  return sheetWidth1.getIconName();
               }
            });
            var261.addCrashSectionCallable("Sprite size", new Callable() {
               public String call() {
                  return sheetWidth1.getIconWidth() + " x " + sheetWidth1.getIconHeight();
               }
            });
            var261.addCrashSectionCallable("Sprite frames", new Callable() {
               public String call() {
                  return sheetWidth1.getFrameCount() + " frames";
               }
            });
            var261.addCrashSection("Mipmap levels", this.field_147636_j);
            throw new ReportedException(debugImage1);
         }
      }

      this.missingImage.func_147963_d(this.field_147636_j);
      var3.addSprite(this.missingImage);
      this.skipFirst = false;

      try {
         var3.doStitch();
      } catch (StitcherException var21) {
         throw var21;
      }

      Config.dbg("Texture size: " + this.basePath + ", " + var3.getCurrentWidth() + "x" + var3.getCurrentHeight());
      int sheetWidth2 = var3.getCurrentWidth();
      int sheetHeight1 = var3.getCurrentHeight();
      BufferedImage debugImage2 = null;
      if (System.getProperty("saveTextureMap", "false").equalsIgnoreCase("true")) {
         debugImage2 = this.makeDebugImage(sheetWidth2, sheetHeight1);
      }

      logger.info("Created: {}x{} {}-atlas", new Object[]{var3.getCurrentWidth(), var3.getCurrentHeight(), this.basePath});
      TextureUtil.func_147946_a(this.getGlTextureId(), this.field_147636_j, var3.getCurrentWidth(), var3.getCurrentHeight(), (float)this.field_147637_k);
      HashMap var262 = Maps.newHashMap(this.mapRegisteredSprites);
      Iterator var282 = var3.getStichSlots().iterator();

      while(var282.hasNext()) {
         var8 = (TextureAtlasSprite)var282.next();
         String var301 = var8.getIconName();
         var262.remove(var301);
         this.mapUploadedSprites.put(var301, var8);

         try {
            TextureUtil.func_147955_a(var8.func_147965_a(0), var8.getIconWidth(), var8.getIconHeight(), var8.getOriginX(), var8.getOriginY(), false, false);
            if (debugImage2 != null) {
               this.addDebugSprite(var8, debugImage2);
            }
         } catch (Throwable var24) {
            CrashReport var321 = CrashReport.makeCrashReport(var24, "Stitching texture atlas");
            CrashReportCategory var33 = var321.makeCategory("Texture being stitched together");
            var33.addCrashSection("Atlas path", this.basePath);
            var33.addCrashSection("Sprite", var8);
            throw new ReportedException(var321);
         }

         if (var8.hasAnimationMetadata()) {
            this.listAnimatedSprites.add(var8);
         } else {
            var8.clearFramesTextureData();
         }
      }

      var282 = var262.values().iterator();

      while(var282.hasNext()) {
         var8 = (TextureAtlasSprite)var282.next();
         var8.copyFrom(this.missingImage);
      }

      if (debugImage2 != null) {
         this.writeDebugImage(debugImage2, "debug_" + this.basePath.replace('/', '_') + ".png");
      }

   }

   private ResourceLocation func_147634_a(ResourceLocation p_147634_1_, int p_147634_2_) {
      return this.isAbsoluteLocation(p_147634_1_) ? (p_147634_2_ == 0 ? new ResourceLocation(p_147634_1_.getResourceDomain(), p_147634_1_.getResourcePath() + ".png") : new ResourceLocation(p_147634_1_.getResourceDomain(), p_147634_1_.getResourcePath() + "mipmap" + p_147634_2_ + ".png")) : (p_147634_2_ == 0 ? new ResourceLocation(p_147634_1_.getResourceDomain(), String.format("%s/%s%s", this.basePath, p_147634_1_.getResourcePath(), ".png")) : new ResourceLocation(p_147634_1_.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", this.basePath, p_147634_1_.getResourcePath(), p_147634_2_, ".png")));
   }

   private void registerIcons() {
      this.mapRegisteredSprites.clear();
      Iterator var1;
      if (this.textureType == 0) {
         var1 = Block.blockRegistry.iterator();

         while(var1.hasNext()) {
            Block var3 = (Block)var1.next();
            if (var3.getMaterial() != Material.air) {
               var3.registerBlockIcons(this);
            }
         }

         Minecraft.getMinecraft().renderGlobal.registerDestroyBlockIcons(this);
         RenderManager.instance.updateIcons(this);
         ConnectedTextures.updateIcons(this);
      }

      if (this.textureType == 1) {
         CustomItems.updateIcons(this);
      }

      var1 = Item.itemRegistry.iterator();

      while(var1.hasNext()) {
         Item var31 = (Item)var1.next();
         if (var31 != null && var31.getSpriteNumber() == this.textureType) {
            var31.registerIcons(this);
         }
      }

   }

   public TextureAtlasSprite getAtlasSprite(String par1Str) {
      TextureAtlasSprite var2 = (TextureAtlasSprite)this.mapUploadedSprites.get(par1Str);
      if (var2 == null) {
         var2 = this.missingImage;
      }

      return var2;
   }

   public void updateAnimations() {
      TextureUtil.bindTexture(this.getGlTextureId());
      Iterator var1 = this.listAnimatedSprites.iterator();

      while(true) {
         TextureAtlasSprite var2;
         while(true) {
            if (!var1.hasNext()) {
               return;
            }

            var2 = (TextureAtlasSprite)var1.next();
            if (this.textureType == 0) {
               if (!this.isTerrainAnimationActive(var2)) {
                  continue;
               }
               break;
            } else if (this.textureType != 1 || this.isItemAnimationActive(var2)) {
               break;
            }
         }

         var2.updateAnimation();
      }
   }

   private boolean isItemAnimationActive(TextureAtlasSprite ts) {
      return ts != TextureUtils.iconClock && ts != TextureUtils.iconCompass ? Config.isAnimatedItems() : true;
   }

   public IIcon registerIcon(String par1Str) {
      if (par1Str == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      } else if (par1Str.indexOf(92) != -1 && !this.isAbsoluteLocationPath(par1Str)) {
         throw new IllegalArgumentException("Name cannot contain slashes!");
      } else {
         Object var2 = (TextureAtlasSprite)this.mapRegisteredSprites.get(par1Str);
         if (var2 == null) {
            if (this.textureType == 1) {
               if ("clock".equals(par1Str)) {
                  var2 = new TextureClock(par1Str);
               } else if ("compass".equals(par1Str)) {
                  var2 = new TextureCompass(par1Str);
               } else {
                  var2 = new TextureAtlasSprite(par1Str);
               }
            } else {
               var2 = new TextureAtlasSprite(par1Str);
            }

            this.mapRegisteredSprites.put(par1Str, var2);
            if (var2 instanceof TextureAtlasSprite) {
               TextureAtlasSprite tas = (TextureAtlasSprite)var2;
               tas.setIndexInMap(this.mapRegisteredSprites.size());
            }
         }

         return (IIcon)var2;
      }
   }

   public int getTextureType() {
      return this.textureType;
   }

   public void tick() {
      this.updateAnimations();
   }

   public void func_147633_a(int p_147633_1_) {
      this.field_147636_j = p_147633_1_;
   }

   public void func_147632_b(int p_147632_1_) {
      this.field_147637_k = p_147632_1_;
   }

   public TextureAtlasSprite getTextureExtry(String name) {
      return (TextureAtlasSprite)this.mapRegisteredSprites.get(name);
   }

   public boolean setTextureEntry(String name, TextureAtlasSprite entry) {
      if (!this.mapRegisteredSprites.containsKey(name)) {
         this.mapRegisteredSprites.put(name, entry);
         entry.setIndexInMap(this.mapRegisteredSprites.size());
         return true;
      } else {
         return false;
      }
   }

   private boolean isAbsoluteLocation(ResourceLocation loc) {
      String path = loc.getResourcePath();
      return this.isAbsoluteLocationPath(path);
   }

   private boolean isAbsoluteLocationPath(String resPath) {
      String path = resPath.toLowerCase();
      return path.startsWith("mcpatcher/") || path.startsWith("optifine/");
   }

   public TextureAtlasSprite getIconSafe(String name) {
      return (TextureAtlasSprite)this.mapRegisteredSprites.get(name);
   }

   private int getStandardTileSize(Collection icons) {
      int[] sizeCounts = new int[16];
      Iterator mostUsedPo2 = icons.iterator();

      int value;
      int count;
      int var9;
      while(mostUsedPo2.hasNext()) {
         TextureAtlasSprite mostUsedCount = (TextureAtlasSprite)mostUsedPo2.next();
         if (mostUsedCount != null) {
            value = TextureUtils.getPowerOfTwo(mostUsedCount.getWidth());
            count = TextureUtils.getPowerOfTwo(mostUsedCount.getHeight());
            var9 = Math.max(value, count);
            if (var9 < sizeCounts.length) {
               ++sizeCounts[var9];
            }
         }
      }

      int var8 = 4;
      var9 = 0;

      for(value = 0; value < sizeCounts.length; ++value) {
         count = sizeCounts[value];
         if (count > var9) {
            var8 = value;
            var9 = count;
         }
      }

      if (var8 < 4) {
         var8 = 4;
      }

      value = TextureUtils.twoToPower(var8);
      return value;
   }

   private void updateIconGrid(int sheetWidth, int sheetHeight) {
      this.iconGridCountX = -1;
      this.iconGridCountY = -1;
      this.iconGrid = null;
      if (this.iconGridSize > 0) {
         this.iconGridCountX = sheetWidth / this.iconGridSize;
         this.iconGridCountY = sheetHeight / this.iconGridSize;
         this.iconGrid = new TextureAtlasSprite[this.iconGridCountX * this.iconGridCountY];
         this.iconGridSizeU = 1.0D / (double)this.iconGridCountX;
         this.iconGridSizeV = 1.0D / (double)this.iconGridCountY;
         Iterator it = this.mapUploadedSprites.values().iterator();

         while(it.hasNext()) {
            TextureAtlasSprite ts = (TextureAtlasSprite)it.next();
            double deltaU = 0.5D / (double)sheetWidth;
            double deltaV = 0.5D / (double)sheetHeight;
            double uMin = (double)Math.min(ts.getMinU(), ts.getMaxU()) + deltaU;
            double vMin = (double)Math.min(ts.getMinV(), ts.getMaxV()) + deltaV;
            double uMax = (double)Math.max(ts.getMinU(), ts.getMaxU()) - deltaU;
            double vMax = (double)Math.max(ts.getMinV(), ts.getMaxV()) - deltaV;
            int iuMin = (int)(uMin / this.iconGridSizeU);
            int ivMin = (int)(vMin / this.iconGridSizeV);
            int iuMax = (int)(uMax / this.iconGridSizeU);
            int ivMax = (int)(vMax / this.iconGridSizeV);

            for(int iu = iuMin; iu <= iuMax; ++iu) {
               if (iu >= 0 && iu < this.iconGridCountX) {
                  for(int iv = ivMin; iv <= ivMax; ++iv) {
                     if (iv >= 0 && iv < this.iconGridCountX) {
                        int index = iv * this.iconGridCountX + iu;
                        this.iconGrid[index] = ts;
                     } else {
                        Config.warn("Invalid grid V: " + iv + ", icon: " + ts.getIconName());
                     }
                  }
               } else {
                  Config.warn("Invalid grid U: " + iu + ", icon: " + ts.getIconName());
               }
            }
         }
      }

   }

   public TextureAtlasSprite getIconByUV(double u, double v) {
      if (this.iconGrid == null) {
         return null;
      } else {
         int iu = (int)(u / this.iconGridSizeU);
         int iv = (int)(v / this.iconGridSizeV);
         int index = iv * this.iconGridCountX + iu;
         return index >= 0 && index <= this.iconGrid.length ? this.iconGrid[index] : null;
      }
   }

   public TextureAtlasSprite getMissingSprite() {
      return this.missingImage;
   }

   public int getMaxTextureIndex() {
      return this.mapRegisteredSprites.size();
   }

   private boolean isTerrainAnimationActive(TextureAtlasSprite ts) {
      return ts != TextureUtils.iconWaterStill && ts != TextureUtils.iconWaterFlow ? (ts != TextureUtils.iconLavaStill && ts != TextureUtils.iconLavaFlow ? (ts != TextureUtils.iconFireLayer0 && ts != TextureUtils.iconFireLayer1 ? (ts == TextureUtils.iconPortal ? Config.isAnimatedPortal() : Config.isAnimatedTerrain()) : Config.isAnimatedFire()) : Config.isAnimatedLava()) : Config.isAnimatedWater();
   }

   public void loadTextureSafe(IResourceManager rm) {
      try {
         this.loadTexture(rm);
      } catch (IOException var3) {
         Config.warn("Error loading texture map: " + this.basePath);
         var3.printStackTrace();
      }

   }

   private BufferedImage makeDebugImage(int sheetWidth, int sheetHeight) {
      BufferedImage image = new BufferedImage(sheetWidth, sheetHeight, 2);
      Graphics2D g = image.createGraphics();
      g.setPaint(new Color(255, 255, 0));
      g.fillRect(0, 0, image.getWidth(), image.getHeight());
      return image;
   }

   private void addDebugSprite(TextureAtlasSprite ts, BufferedImage image) {
      if (ts.getFrameCount() < 1) {
         Config.warn("Debug sprite has no data: " + ts.getIconName());
      } else {
         int[] data = ts.func_147965_a(0)[0];
         image.setRGB(ts.getOriginX(), ts.getOriginY(), ts.getIconWidth(), ts.getIconHeight(), data, 0, ts.getIconWidth());
      }

   }

   private void writeDebugImage(BufferedImage image, String pngPath) {
      try {
         ImageIO.write(image, "png", new File(Config.getMinecraft().mcDataDir, pngPath));
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }
}
