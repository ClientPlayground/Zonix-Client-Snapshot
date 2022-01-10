package net.minecraft.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemShears;

public class Items {
   public static final Item iron_shovel;
   public static final Item iron_pickaxe;
   public static final Item iron_axe;
   public static final Item flint_and_steel;
   public static final Item apple;
   public static final ItemBow bow;
   public static final Item arrow;
   public static final Item coal;
   public static final Item diamond;
   public static final Item iron_ingot;
   public static final Item gold_ingot;
   public static final Item iron_sword;
   public static final Item wooden_sword;
   public static final Item wooden_shovel;
   public static final Item wooden_pickaxe;
   public static final Item wooden_axe;
   public static final Item stone_sword;
   public static final Item stone_shovel;
   public static final Item stone_pickaxe;
   public static final Item stone_axe;
   public static final Item diamond_sword;
   public static final Item diamond_shovel;
   public static final Item diamond_pickaxe;
   public static final Item diamond_axe;
   public static final Item stick;
   public static final Item bowl;
   public static final Item mushroom_stew;
   public static final Item golden_sword;
   public static final Item golden_shovel;
   public static final Item golden_pickaxe;
   public static final Item golden_axe;
   public static final Item string;
   public static final Item feather;
   public static final Item gunpowder;
   public static final Item wooden_hoe;
   public static final Item stone_hoe;
   public static final Item iron_hoe;
   public static final Item diamond_hoe;
   public static final Item golden_hoe;
   public static final Item wheat_seeds;
   public static final Item wheat;
   public static final Item bread;
   public static final ItemArmor leather_helmet;
   public static final ItemArmor leather_chestplate;
   public static final ItemArmor leather_leggings;
   public static final ItemArmor leather_boots;
   public static final ItemArmor chainmail_helmet;
   public static final ItemArmor chainmail_chestplate;
   public static final ItemArmor chainmail_leggings;
   public static final ItemArmor chainmail_boots;
   public static final ItemArmor iron_helmet;
   public static final ItemArmor iron_chestplate;
   public static final ItemArmor iron_leggings;
   public static final ItemArmor iron_boots;
   public static final ItemArmor diamond_helmet;
   public static final ItemArmor diamond_chestplate;
   public static final ItemArmor diamond_leggings;
   public static final ItemArmor diamond_boots;
   public static final ItemArmor golden_helmet;
   public static final ItemArmor golden_chestplate;
   public static final ItemArmor golden_leggings;
   public static final ItemArmor golden_boots;
   public static final Item flint;
   public static final Item porkchop;
   public static final Item cooked_porkchop;
   public static final Item painting;
   public static final Item golden_apple;
   public static final Item sign;
   public static final Item wooden_door;
   public static final Item bucket;
   public static final Item water_bucket;
   public static final Item lava_bucket;
   public static final Item minecart;
   public static final Item saddle;
   public static final Item iron_door;
   public static final Item redstone;
   public static final Item snowball;
   public static final Item boat;
   public static final Item leather;
   public static final Item milk_bucket;
   public static final Item brick;
   public static final Item clay_ball;
   public static final Item reeds;
   public static final Item paper;
   public static final Item book;
   public static final Item slime_ball;
   public static final Item chest_minecart;
   public static final Item furnace_minecart;
   public static final Item egg;
   public static final Item compass;
   public static final ItemFishingRod fishing_rod;
   public static final Item clock;
   public static final Item glowstone_dust;
   public static final Item fish;
   public static final Item cooked_fished;
   public static final Item dye;
   public static final Item bone;
   public static final Item sugar;
   public static final Item cake;
   public static final Item bed;
   public static final Item repeater;
   public static final Item cookie;
   public static final ItemMap filled_map;
   public static final ItemShears shears;
   public static final Item melon;
   public static final Item pumpkin_seeds;
   public static final Item melon_seeds;
   public static final Item beef;
   public static final Item cooked_beef;
   public static final Item chicken;
   public static final Item cooked_chicken;
   public static final Item rotten_flesh;
   public static final Item ender_pearl;
   public static final Item blaze_rod;
   public static final Item ghast_tear;
   public static final Item gold_nugget;
   public static final Item nether_wart;
   public static final ItemPotion potionitem;
   public static final Item glass_bottle;
   public static final Item spider_eye;
   public static final Item fermented_spider_eye;
   public static final Item blaze_powder;
   public static final Item magma_cream;
   public static final Item brewing_stand;
   public static final Item cauldron;
   public static final Item ender_eye;
   public static final Item speckled_melon;
   public static final Item spawn_egg;
   public static final Item experience_bottle;
   public static final Item fire_charge;
   public static final Item writable_book;
   public static final Item written_book;
   public static final Item emerald;
   public static final Item item_frame;
   public static final Item flower_pot;
   public static final Item carrot;
   public static final Item potato;
   public static final Item baked_potato;
   public static final Item poisonous_potato;
   public static final ItemEmptyMap map;
   public static final Item golden_carrot;
   public static final Item skull;
   public static final Item carrot_on_a_stick;
   public static final Item nether_star;
   public static final Item pumpkin_pie;
   public static final Item fireworks;
   public static final Item firework_charge;
   public static final ItemEnchantedBook enchanted_book;
   public static final Item comparator;
   public static final Item netherbrick;
   public static final Item quartz;
   public static final Item tnt_minecart;
   public static final Item hopper_minecart;
   public static final Item iron_horse_armor;
   public static final Item golden_horse_armor;
   public static final Item diamond_horse_armor;
   public static final Item lead;
   public static final Item name_tag;
   public static final Item command_block_minecart;
   public static final Item record_13;
   public static final Item record_cat;
   public static final Item record_blocks;
   public static final Item record_chirp;
   public static final Item record_far;
   public static final Item record_mall;
   public static final Item record_mellohi;
   public static final Item record_stal;
   public static final Item record_strad;
   public static final Item record_ward;
   public static final Item record_11;
   public static final Item record_wait;
   private static final String __OBFID = "CL_00000044";

   static {
      iron_shovel = (Item)Item.itemRegistry.getObject("iron_shovel");
      iron_pickaxe = (Item)Item.itemRegistry.getObject("iron_pickaxe");
      iron_axe = (Item)Item.itemRegistry.getObject("iron_axe");
      flint_and_steel = (Item)Item.itemRegistry.getObject("flint_and_steel");
      apple = (Item)Item.itemRegistry.getObject("apple");
      bow = (ItemBow)Item.itemRegistry.getObject("bow");
      arrow = (Item)Item.itemRegistry.getObject("arrow");
      coal = (Item)Item.itemRegistry.getObject("coal");
      diamond = (Item)Item.itemRegistry.getObject("diamond");
      iron_ingot = (Item)Item.itemRegistry.getObject("iron_ingot");
      gold_ingot = (Item)Item.itemRegistry.getObject("gold_ingot");
      iron_sword = (Item)Item.itemRegistry.getObject("iron_sword");
      wooden_sword = (Item)Item.itemRegistry.getObject("wooden_sword");
      wooden_shovel = (Item)Item.itemRegistry.getObject("wooden_shovel");
      wooden_pickaxe = (Item)Item.itemRegistry.getObject("wooden_pickaxe");
      wooden_axe = (Item)Item.itemRegistry.getObject("wooden_axe");
      stone_sword = (Item)Item.itemRegistry.getObject("stone_sword");
      stone_shovel = (Item)Item.itemRegistry.getObject("stone_shovel");
      stone_pickaxe = (Item)Item.itemRegistry.getObject("stone_pickaxe");
      stone_axe = (Item)Item.itemRegistry.getObject("stone_axe");
      diamond_sword = (Item)Item.itemRegistry.getObject("diamond_sword");
      diamond_shovel = (Item)Item.itemRegistry.getObject("diamond_shovel");
      diamond_pickaxe = (Item)Item.itemRegistry.getObject("diamond_pickaxe");
      diamond_axe = (Item)Item.itemRegistry.getObject("diamond_axe");
      stick = (Item)Item.itemRegistry.getObject("stick");
      bowl = (Item)Item.itemRegistry.getObject("bowl");
      mushroom_stew = (Item)Item.itemRegistry.getObject("mushroom_stew");
      golden_sword = (Item)Item.itemRegistry.getObject("golden_sword");
      golden_shovel = (Item)Item.itemRegistry.getObject("golden_shovel");
      golden_pickaxe = (Item)Item.itemRegistry.getObject("golden_pickaxe");
      golden_axe = (Item)Item.itemRegistry.getObject("golden_axe");
      string = (Item)Item.itemRegistry.getObject("string");
      feather = (Item)Item.itemRegistry.getObject("feather");
      gunpowder = (Item)Item.itemRegistry.getObject("gunpowder");
      wooden_hoe = (Item)Item.itemRegistry.getObject("wooden_hoe");
      stone_hoe = (Item)Item.itemRegistry.getObject("stone_hoe");
      iron_hoe = (Item)Item.itemRegistry.getObject("iron_hoe");
      diamond_hoe = (Item)Item.itemRegistry.getObject("diamond_hoe");
      golden_hoe = (Item)Item.itemRegistry.getObject("golden_hoe");
      wheat_seeds = (Item)Item.itemRegistry.getObject("wheat_seeds");
      wheat = (Item)Item.itemRegistry.getObject("wheat");
      bread = (Item)Item.itemRegistry.getObject("bread");
      leather_helmet = (ItemArmor)Item.itemRegistry.getObject("leather_helmet");
      leather_chestplate = (ItemArmor)Item.itemRegistry.getObject("leather_chestplate");
      leather_leggings = (ItemArmor)Item.itemRegistry.getObject("leather_leggings");
      leather_boots = (ItemArmor)Item.itemRegistry.getObject("leather_boots");
      chainmail_helmet = (ItemArmor)Item.itemRegistry.getObject("chainmail_helmet");
      chainmail_chestplate = (ItemArmor)Item.itemRegistry.getObject("chainmail_chestplate");
      chainmail_leggings = (ItemArmor)Item.itemRegistry.getObject("chainmail_leggings");
      chainmail_boots = (ItemArmor)Item.itemRegistry.getObject("chainmail_boots");
      iron_helmet = (ItemArmor)Item.itemRegistry.getObject("iron_helmet");
      iron_chestplate = (ItemArmor)Item.itemRegistry.getObject("iron_chestplate");
      iron_leggings = (ItemArmor)Item.itemRegistry.getObject("iron_leggings");
      iron_boots = (ItemArmor)Item.itemRegistry.getObject("iron_boots");
      diamond_helmet = (ItemArmor)Item.itemRegistry.getObject("diamond_helmet");
      diamond_chestplate = (ItemArmor)Item.itemRegistry.getObject("diamond_chestplate");
      diamond_leggings = (ItemArmor)Item.itemRegistry.getObject("diamond_leggings");
      diamond_boots = (ItemArmor)Item.itemRegistry.getObject("diamond_boots");
      golden_helmet = (ItemArmor)Item.itemRegistry.getObject("golden_helmet");
      golden_chestplate = (ItemArmor)Item.itemRegistry.getObject("golden_chestplate");
      golden_leggings = (ItemArmor)Item.itemRegistry.getObject("golden_leggings");
      golden_boots = (ItemArmor)Item.itemRegistry.getObject("golden_boots");
      flint = (Item)Item.itemRegistry.getObject("flint");
      porkchop = (Item)Item.itemRegistry.getObject("porkchop");
      cooked_porkchop = (Item)Item.itemRegistry.getObject("cooked_porkchop");
      painting = (Item)Item.itemRegistry.getObject("painting");
      golden_apple = (Item)Item.itemRegistry.getObject("golden_apple");
      sign = (Item)Item.itemRegistry.getObject("sign");
      wooden_door = (Item)Item.itemRegistry.getObject("wooden_door");
      bucket = (Item)Item.itemRegistry.getObject("bucket");
      water_bucket = (Item)Item.itemRegistry.getObject("water_bucket");
      lava_bucket = (Item)Item.itemRegistry.getObject("lava_bucket");
      minecart = (Item)Item.itemRegistry.getObject("minecart");
      saddle = (Item)Item.itemRegistry.getObject("saddle");
      iron_door = (Item)Item.itemRegistry.getObject("iron_door");
      redstone = (Item)Item.itemRegistry.getObject("redstone");
      snowball = (Item)Item.itemRegistry.getObject("snowball");
      boat = (Item)Item.itemRegistry.getObject("boat");
      leather = (Item)Item.itemRegistry.getObject("leather");
      milk_bucket = (Item)Item.itemRegistry.getObject("milk_bucket");
      brick = (Item)Item.itemRegistry.getObject("brick");
      clay_ball = (Item)Item.itemRegistry.getObject("clay_ball");
      reeds = (Item)Item.itemRegistry.getObject("reeds");
      paper = (Item)Item.itemRegistry.getObject("paper");
      book = (Item)Item.itemRegistry.getObject("book");
      slime_ball = (Item)Item.itemRegistry.getObject("slime_ball");
      chest_minecart = (Item)Item.itemRegistry.getObject("chest_minecart");
      furnace_minecart = (Item)Item.itemRegistry.getObject("furnace_minecart");
      egg = (Item)Item.itemRegistry.getObject("egg");
      compass = (Item)Item.itemRegistry.getObject("compass");
      fishing_rod = (ItemFishingRod)Item.itemRegistry.getObject("fishing_rod");
      clock = (Item)Item.itemRegistry.getObject("clock");
      glowstone_dust = (Item)Item.itemRegistry.getObject("glowstone_dust");
      fish = (Item)Item.itemRegistry.getObject("fish");
      cooked_fished = (Item)Item.itemRegistry.getObject("cooked_fished");
      dye = (Item)Item.itemRegistry.getObject("dye");
      bone = (Item)Item.itemRegistry.getObject("bone");
      sugar = (Item)Item.itemRegistry.getObject("sugar");
      cake = (Item)Item.itemRegistry.getObject("cake");
      bed = (Item)Item.itemRegistry.getObject("bed");
      repeater = (Item)Item.itemRegistry.getObject("repeater");
      cookie = (Item)Item.itemRegistry.getObject("cookie");
      filled_map = (ItemMap)Item.itemRegistry.getObject("filled_map");
      shears = (ItemShears)Item.itemRegistry.getObject("shears");
      melon = (Item)Item.itemRegistry.getObject("melon");
      pumpkin_seeds = (Item)Item.itemRegistry.getObject("pumpkin_seeds");
      melon_seeds = (Item)Item.itemRegistry.getObject("melon_seeds");
      beef = (Item)Item.itemRegistry.getObject("beef");
      cooked_beef = (Item)Item.itemRegistry.getObject("cooked_beef");
      chicken = (Item)Item.itemRegistry.getObject("chicken");
      cooked_chicken = (Item)Item.itemRegistry.getObject("cooked_chicken");
      rotten_flesh = (Item)Item.itemRegistry.getObject("rotten_flesh");
      ender_pearl = (Item)Item.itemRegistry.getObject("ender_pearl");
      blaze_rod = (Item)Item.itemRegistry.getObject("blaze_rod");
      ghast_tear = (Item)Item.itemRegistry.getObject("ghast_tear");
      gold_nugget = (Item)Item.itemRegistry.getObject("gold_nugget");
      nether_wart = (Item)Item.itemRegistry.getObject("nether_wart");
      potionitem = (ItemPotion)Item.itemRegistry.getObject("potion");
      glass_bottle = (Item)Item.itemRegistry.getObject("glass_bottle");
      spider_eye = (Item)Item.itemRegistry.getObject("spider_eye");
      fermented_spider_eye = (Item)Item.itemRegistry.getObject("fermented_spider_eye");
      blaze_powder = (Item)Item.itemRegistry.getObject("blaze_powder");
      magma_cream = (Item)Item.itemRegistry.getObject("magma_cream");
      brewing_stand = (Item)Item.itemRegistry.getObject("brewing_stand");
      cauldron = (Item)Item.itemRegistry.getObject("cauldron");
      ender_eye = (Item)Item.itemRegistry.getObject("ender_eye");
      speckled_melon = (Item)Item.itemRegistry.getObject("speckled_melon");
      spawn_egg = (Item)Item.itemRegistry.getObject("spawn_egg");
      experience_bottle = (Item)Item.itemRegistry.getObject("experience_bottle");
      fire_charge = (Item)Item.itemRegistry.getObject("fire_charge");
      writable_book = (Item)Item.itemRegistry.getObject("writable_book");
      written_book = (Item)Item.itemRegistry.getObject("written_book");
      emerald = (Item)Item.itemRegistry.getObject("emerald");
      item_frame = (Item)Item.itemRegistry.getObject("item_frame");
      flower_pot = (Item)Item.itemRegistry.getObject("flower_pot");
      carrot = (Item)Item.itemRegistry.getObject("carrot");
      potato = (Item)Item.itemRegistry.getObject("potato");
      baked_potato = (Item)Item.itemRegistry.getObject("baked_potato");
      poisonous_potato = (Item)Item.itemRegistry.getObject("poisonous_potato");
      map = (ItemEmptyMap)Item.itemRegistry.getObject("map");
      golden_carrot = (Item)Item.itemRegistry.getObject("golden_carrot");
      skull = (Item)Item.itemRegistry.getObject("skull");
      carrot_on_a_stick = (Item)Item.itemRegistry.getObject("carrot_on_a_stick");
      nether_star = (Item)Item.itemRegistry.getObject("nether_star");
      pumpkin_pie = (Item)Item.itemRegistry.getObject("pumpkin_pie");
      fireworks = (Item)Item.itemRegistry.getObject("fireworks");
      firework_charge = (Item)Item.itemRegistry.getObject("firework_charge");
      enchanted_book = (ItemEnchantedBook)Item.itemRegistry.getObject("enchanted_book");
      comparator = (Item)Item.itemRegistry.getObject("comparator");
      netherbrick = (Item)Item.itemRegistry.getObject("netherbrick");
      quartz = (Item)Item.itemRegistry.getObject("quartz");
      tnt_minecart = (Item)Item.itemRegistry.getObject("tnt_minecart");
      hopper_minecart = (Item)Item.itemRegistry.getObject("hopper_minecart");
      iron_horse_armor = (Item)Item.itemRegistry.getObject("iron_horse_armor");
      golden_horse_armor = (Item)Item.itemRegistry.getObject("golden_horse_armor");
      diamond_horse_armor = (Item)Item.itemRegistry.getObject("diamond_horse_armor");
      lead = (Item)Item.itemRegistry.getObject("lead");
      name_tag = (Item)Item.itemRegistry.getObject("name_tag");
      command_block_minecart = (Item)Item.itemRegistry.getObject("command_block_minecart");
      record_13 = (Item)Item.itemRegistry.getObject("record_13");
      record_cat = (Item)Item.itemRegistry.getObject("record_cat");
      record_blocks = (Item)Item.itemRegistry.getObject("record_blocks");
      record_chirp = (Item)Item.itemRegistry.getObject("record_chirp");
      record_far = (Item)Item.itemRegistry.getObject("record_far");
      record_mall = (Item)Item.itemRegistry.getObject("record_mall");
      record_mellohi = (Item)Item.itemRegistry.getObject("record_mellohi");
      record_stal = (Item)Item.itemRegistry.getObject("record_stal");
      record_strad = (Item)Item.itemRegistry.getObject("record_strad");
      record_ward = (Item)Item.itemRegistry.getObject("record_ward");
      record_11 = (Item)Item.itemRegistry.getObject("record_11");
      record_wait = (Item)Item.itemRegistry.getObject("record_wait");
   }
}
