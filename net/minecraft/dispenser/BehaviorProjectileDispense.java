package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {
   private static final String __OBFID = "CL_00001394";

   public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      World var3 = p_82487_1_.getWorld();
      IPosition var4 = BlockDispenser.func_149939_a(p_82487_1_);
      EnumFacing var5 = BlockDispenser.func_149937_b(p_82487_1_.getBlockMetadata());
      IProjectile var6 = this.getProjectileEntity(var3, var4);
      var6.setThrowableHeading((double)var5.getFrontOffsetX(), (double)((float)var5.getFrontOffsetY() + 0.1F), (double)var5.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
      var3.spawnEntityInWorld((Entity)var6);
      p_82487_2_.splitStack(1);
      return p_82487_2_;
   }

   protected void playDispenseSound(IBlockSource p_82485_1_) {
      p_82485_1_.getWorld().playAuxSFX(1002, p_82485_1_.getXInt(), p_82485_1_.getYInt(), p_82485_1_.getZInt(), 0);
   }

   protected abstract IProjectile getProjectileEntity(World var1, IPosition var2);

   protected float func_82498_a() {
      return 6.0F;
   }

   protected float func_82500_b() {
      return 1.1F;
   }
}
