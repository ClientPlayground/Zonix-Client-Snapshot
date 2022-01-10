package net.minecraft.block.material;

public class Material {
   public static final Material air;
   public static final Material grass;
   public static final Material ground;
   public static final Material wood;
   public static final Material rock;
   public static final Material iron;
   public static final Material anvil;
   public static final Material water;
   public static final Material lava;
   public static final Material leaves;
   public static final Material plants;
   public static final Material vine;
   public static final Material sponge;
   public static final Material cloth;
   public static final Material fire;
   public static final Material sand;
   public static final Material circuits;
   public static final Material carpet;
   public static final Material glass;
   public static final Material redstoneLight;
   public static final Material tnt;
   public static final Material coral;
   public static final Material ice;
   public static final Material field_151598_x;
   public static final Material field_151597_y;
   public static final Material craftedSnow;
   public static final Material field_151570_A;
   public static final Material field_151571_B;
   public static final Material field_151572_C;
   public static final Material dragonEgg;
   public static final Material Portal;
   public static final Material field_151568_F;
   public static final Material field_151569_G;
   public static final Material piston;
   private boolean canBurn;
   private boolean replaceable;
   private boolean isTranslucent;
   private final MapColor materialMapColor;
   private boolean requiresNoTool = true;
   private int mobilityFlag;
   private boolean isAdventureModeExempt;
   private static final String __OBFID = "CL_00000542";

   public Material(MapColor p_i2116_1_) {
      this.materialMapColor = p_i2116_1_;
   }

   public boolean isLiquid() {
      return false;
   }

   public boolean isSolid() {
      return true;
   }

   public boolean getCanBlockGrass() {
      return true;
   }

   public boolean blocksMovement() {
      return true;
   }

   private Material setTranslucent() {
      this.isTranslucent = true;
      return this;
   }

   protected Material setRequiresTool() {
      this.requiresNoTool = false;
      return this;
   }

   protected Material setBurning() {
      this.canBurn = true;
      return this;
   }

   public boolean getCanBurn() {
      return this.canBurn;
   }

   public Material setReplaceable() {
      this.replaceable = true;
      return this;
   }

   public boolean isReplaceable() {
      return this.replaceable;
   }

   public boolean isOpaque() {
      return this.isTranslucent ? false : this.blocksMovement();
   }

   public boolean isToolNotRequired() {
      return this.requiresNoTool;
   }

   public int getMaterialMobility() {
      return this.mobilityFlag;
   }

   protected Material setNoPushMobility() {
      this.mobilityFlag = 1;
      return this;
   }

   protected Material setImmovableMobility() {
      this.mobilityFlag = 2;
      return this;
   }

   protected Material setAdventureModeExempt() {
      this.isAdventureModeExempt = true;
      return this;
   }

   public boolean isAdventureModeExempt() {
      return this.isAdventureModeExempt;
   }

   public MapColor getMaterialMapColor() {
      return this.materialMapColor;
   }

   static {
      air = new MaterialTransparent(MapColor.field_151660_b);
      grass = new Material(MapColor.field_151661_c);
      ground = new Material(MapColor.field_151664_l);
      wood = (new Material(MapColor.field_151663_o)).setBurning();
      rock = (new Material(MapColor.field_151665_m)).setRequiresTool();
      iron = (new Material(MapColor.field_151668_h)).setRequiresTool();
      anvil = (new Material(MapColor.field_151668_h)).setRequiresTool().setImmovableMobility();
      water = (new MaterialLiquid(MapColor.field_151662_n)).setNoPushMobility();
      lava = (new MaterialLiquid(MapColor.field_151656_f)).setNoPushMobility();
      leaves = (new Material(MapColor.field_151669_i)).setBurning().setTranslucent().setNoPushMobility();
      plants = (new MaterialLogic(MapColor.field_151669_i)).setNoPushMobility();
      vine = (new MaterialLogic(MapColor.field_151669_i)).setBurning().setNoPushMobility().setReplaceable();
      sponge = new Material(MapColor.field_151659_e);
      cloth = (new Material(MapColor.field_151659_e)).setBurning();
      fire = (new MaterialTransparent(MapColor.field_151660_b)).setNoPushMobility();
      sand = new Material(MapColor.field_151658_d);
      circuits = (new MaterialLogic(MapColor.field_151660_b)).setNoPushMobility();
      carpet = (new MaterialLogic(MapColor.field_151659_e)).setBurning();
      glass = (new Material(MapColor.field_151660_b)).setTranslucent().setAdventureModeExempt();
      redstoneLight = (new Material(MapColor.field_151660_b)).setAdventureModeExempt();
      tnt = (new Material(MapColor.field_151656_f)).setBurning().setTranslucent();
      coral = (new Material(MapColor.field_151669_i)).setNoPushMobility();
      ice = (new Material(MapColor.field_151657_g)).setTranslucent().setAdventureModeExempt();
      field_151598_x = (new Material(MapColor.field_151657_g)).setAdventureModeExempt();
      field_151597_y = (new MaterialLogic(MapColor.field_151666_j)).setReplaceable().setTranslucent().setRequiresTool().setNoPushMobility();
      craftedSnow = (new Material(MapColor.field_151666_j)).setRequiresTool();
      field_151570_A = (new Material(MapColor.field_151669_i)).setTranslucent().setNoPushMobility();
      field_151571_B = new Material(MapColor.field_151667_k);
      field_151572_C = (new Material(MapColor.field_151669_i)).setNoPushMobility();
      dragonEgg = (new Material(MapColor.field_151669_i)).setNoPushMobility();
      Portal = (new MaterialPortal(MapColor.field_151660_b)).setImmovableMobility();
      field_151568_F = (new Material(MapColor.field_151660_b)).setNoPushMobility();
      field_151569_G = (new Material(MapColor.field_151659_e) {
         private static final String __OBFID = "CL_00000543";

         public boolean blocksMovement() {
            return false;
         }
      }).setRequiresTool().setNoPushMobility();
      piston = (new Material(MapColor.field_151665_m)).setImmovableMobility();
   }
}
