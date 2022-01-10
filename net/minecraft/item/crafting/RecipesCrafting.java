package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipesCrafting {
   private static final String __OBFID = "CL_00000095";

   public void addRecipes(CraftingManager p_77589_1_) {
      p_77589_1_.addRecipe(new ItemStack(Blocks.chest), "###", "# #", "###", '#', Blocks.planks);
      p_77589_1_.addRecipe(new ItemStack(Blocks.trapped_chest), "#-", '#', Blocks.chest, '-', Blocks.tripwire_hook);
      p_77589_1_.addRecipe(new ItemStack(Blocks.ender_chest), "###", "#E#", "###", '#', Blocks.obsidian, 'E', Items.ender_eye);
      p_77589_1_.addRecipe(new ItemStack(Blocks.furnace), "###", "# #", "###", '#', Blocks.cobblestone);
      p_77589_1_.addRecipe(new ItemStack(Blocks.crafting_table), "##", "##", '#', Blocks.planks);
      p_77589_1_.addRecipe(new ItemStack(Blocks.sandstone), "##", "##", '#', new ItemStack(Blocks.sand, 1, 0));
      p_77589_1_.addRecipe(new ItemStack(Blocks.sandstone, 4, 2), "##", "##", '#', Blocks.sandstone);
      p_77589_1_.addRecipe(new ItemStack(Blocks.sandstone, 1, 1), "#", "#", '#', new ItemStack(Blocks.stone_slab, 1, 1));
      p_77589_1_.addRecipe(new ItemStack(Blocks.quartz_block, 1, 1), "#", "#", '#', new ItemStack(Blocks.stone_slab, 1, 7));
      p_77589_1_.addRecipe(new ItemStack(Blocks.quartz_block, 2, 2), "#", "#", '#', new ItemStack(Blocks.quartz_block, 1, 0));
      p_77589_1_.addRecipe(new ItemStack(Blocks.stonebrick, 4), "##", "##", '#', Blocks.stone);
      p_77589_1_.addRecipe(new ItemStack(Blocks.iron_bars, 16), "###", "###", '#', Items.iron_ingot);
      p_77589_1_.addRecipe(new ItemStack(Blocks.glass_pane, 16), "###", "###", '#', Blocks.glass);
      p_77589_1_.addRecipe(new ItemStack(Blocks.redstone_lamp, 1), " R ", "RGR", " R ", 'R', Items.redstone, 'G', Blocks.glowstone);
      p_77589_1_.addRecipe(new ItemStack(Blocks.beacon, 1), "GGG", "GSG", "OOO", 'G', Blocks.glass, 'S', Items.nether_star, 'O', Blocks.obsidian);
      p_77589_1_.addRecipe(new ItemStack(Blocks.nether_brick, 1), "NN", "NN", 'N', Items.netherbrick);
   }
}
