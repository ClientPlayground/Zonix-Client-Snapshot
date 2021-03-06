package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class InventoryMerchant implements IInventory {
   private final IMerchant theMerchant;
   private ItemStack[] theInventory = new ItemStack[3];
   private final EntityPlayer thePlayer;
   private MerchantRecipe currentRecipe;
   private int currentRecipeIndex;
   private static final String __OBFID = "CL_00001756";

   public InventoryMerchant(EntityPlayer p_i1820_1_, IMerchant p_i1820_2_) {
      this.thePlayer = p_i1820_1_;
      this.theMerchant = p_i1820_2_;
   }

   public int getSizeInventory() {
      return this.theInventory.length;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return this.theInventory[p_70301_1_];
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      if (this.theInventory[p_70298_1_] != null) {
         ItemStack var3;
         if (p_70298_1_ == 2) {
            var3 = this.theInventory[p_70298_1_];
            this.theInventory[p_70298_1_] = null;
            return var3;
         } else if (this.theInventory[p_70298_1_].stackSize <= p_70298_2_) {
            var3 = this.theInventory[p_70298_1_];
            this.theInventory[p_70298_1_] = null;
            if (this.inventoryResetNeededOnSlotChange(p_70298_1_)) {
               this.resetRecipeAndSlots();
            }

            return var3;
         } else {
            var3 = this.theInventory[p_70298_1_].splitStack(p_70298_2_);
            if (this.theInventory[p_70298_1_].stackSize == 0) {
               this.theInventory[p_70298_1_] = null;
            }

            if (this.inventoryResetNeededOnSlotChange(p_70298_1_)) {
               this.resetRecipeAndSlots();
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   private boolean inventoryResetNeededOnSlotChange(int p_70469_1_) {
      return p_70469_1_ == 0 || p_70469_1_ == 1;
   }

   public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
      if (this.theInventory[p_70304_1_] != null) {
         ItemStack var2 = this.theInventory[p_70304_1_];
         this.theInventory[p_70304_1_] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.theInventory[p_70299_1_] = p_70299_2_;
      if (p_70299_2_ != null && p_70299_2_.stackSize > this.getInventoryStackLimit()) {
         p_70299_2_.stackSize = this.getInventoryStackLimit();
      }

      if (this.inventoryResetNeededOnSlotChange(p_70299_1_)) {
         this.resetRecipeAndSlots();
      }

   }

   public String getInventoryName() {
      return "mob.villager";
   }

   public boolean isInventoryNameLocalized() {
      return false;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
      return this.theMerchant.getCustomer() == p_70300_1_;
   }

   public void openInventory() {
   }

   public void closeInventory() {
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      return true;
   }

   public void onInventoryChanged() {
      this.resetRecipeAndSlots();
   }

   public void resetRecipeAndSlots() {
      this.currentRecipe = null;
      ItemStack var1 = this.theInventory[0];
      ItemStack var2 = this.theInventory[1];
      if (var1 == null) {
         var1 = var2;
         var2 = null;
      }

      if (var1 == null) {
         this.setInventorySlotContents(2, (ItemStack)null);
      } else {
         MerchantRecipeList var3 = this.theMerchant.getRecipes(this.thePlayer);
         if (var3 != null) {
            MerchantRecipe var4 = var3.canRecipeBeUsed(var1, var2, this.currentRecipeIndex);
            if (var4 != null && !var4.isRecipeDisabled()) {
               this.currentRecipe = var4;
               this.setInventorySlotContents(2, var4.getItemToSell().copy());
            } else if (var2 != null) {
               var4 = var3.canRecipeBeUsed(var2, var1, this.currentRecipeIndex);
               if (var4 != null && !var4.isRecipeDisabled()) {
                  this.currentRecipe = var4;
                  this.setInventorySlotContents(2, var4.getItemToSell().copy());
               } else {
                  this.setInventorySlotContents(2, (ItemStack)null);
               }
            } else {
               this.setInventorySlotContents(2, (ItemStack)null);
            }
         }
      }

      this.theMerchant.func_110297_a_(this.getStackInSlot(2));
   }

   public MerchantRecipe getCurrentRecipe() {
      return this.currentRecipe;
   }

   public void setCurrentRecipeIndex(int p_70471_1_) {
      this.currentRecipeIndex = p_70471_1_;
      this.resetRecipeAndSlots();
   }
}
