package net.minecraft.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityWitherSkull extends EntityFireball {
   private static final String __OBFID = "CL_00001728";

   public EntityWitherSkull(World p_i1793_1_) {
      super(p_i1793_1_);
      this.setSize(0.3125F, 0.3125F);
   }

   public EntityWitherSkull(World p_i1794_1_, EntityLivingBase p_i1794_2_, double p_i1794_3_, double p_i1794_5_, double p_i1794_7_) {
      super(p_i1794_1_, p_i1794_2_, p_i1794_3_, p_i1794_5_, p_i1794_7_);
      this.setSize(0.3125F, 0.3125F);
   }

   protected float getMotionFactor() {
      return this.isInvulnerable() ? 0.73F : super.getMotionFactor();
   }

   public EntityWitherSkull(World p_i1795_1_, double p_i1795_2_, double p_i1795_4_, double p_i1795_6_, double p_i1795_8_, double p_i1795_10_, double p_i1795_12_) {
      super(p_i1795_1_, p_i1795_2_, p_i1795_4_, p_i1795_6_, p_i1795_8_, p_i1795_10_, p_i1795_12_);
      this.setSize(0.3125F, 0.3125F);
   }

   public boolean isBurning() {
      return false;
   }

   public float func_145772_a(Explosion p_145772_1_, World p_145772_2_, int p_145772_3_, int p_145772_4_, int p_145772_5_, Block p_145772_6_) {
      float var7 = super.func_145772_a(p_145772_1_, p_145772_2_, p_145772_3_, p_145772_4_, p_145772_5_, p_145772_6_);
      if (this.isInvulnerable() && p_145772_6_ != Blocks.bedrock && p_145772_6_ != Blocks.end_portal && p_145772_6_ != Blocks.end_portal_frame && p_145772_6_ != Blocks.command_block) {
         var7 = Math.min(0.8F, var7);
      }

      return var7;
   }

   protected void onImpact(MovingObjectPosition p_70227_1_) {
      if (!this.worldObj.isClient) {
         if (p_70227_1_.entityHit != null) {
            if (this.shootingEntity != null) {
               if (p_70227_1_.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 8.0F) && !p_70227_1_.entityHit.isEntityAlive()) {
                  this.shootingEntity.heal(5.0F);
               }
            } else {
               p_70227_1_.entityHit.attackEntityFrom(DamageSource.magic, 5.0F);
            }

            if (p_70227_1_.entityHit instanceof EntityLivingBase) {
               byte var2 = 0;
               if (this.worldObj.difficultySetting == EnumDifficulty.NORMAL) {
                  var2 = 10;
               } else if (this.worldObj.difficultySetting == EnumDifficulty.HARD) {
                  var2 = 40;
               }

               if (var2 > 0) {
                  ((EntityLivingBase)p_70227_1_.entityHit).addPotionEffect(new PotionEffect(Potion.wither.id, 20 * var2, 1));
               }
            }
         }

         this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, 1.0F, false, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
         this.setDead();
      }

   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return false;
   }

   protected void entityInit() {
      this.dataWatcher.addObject(10, 0);
   }

   public boolean isInvulnerable() {
      return this.dataWatcher.getWatchableObjectByte(10) == 1;
   }

   public void setInvulnerable(boolean p_82343_1_) {
      this.dataWatcher.updateObject(10, (byte)(p_82343_1_ ? 1 : 0));
   }
}
