package net.minecraft.client.settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;

public class KeyBinding implements Comparable {
   private static final List keybindArray = new ArrayList();
   private static final IntHashMap hash = new IntHashMap();
   private static final Set keybindSet = new HashSet();
   private final String keyDescription;
   private final int keyCodeDefault;
   private final String keyCategory;
   private int keyCode;
   private boolean pressed;
   private int presses;
   private static final String __OBFID = "CL_00000628";

   public static void onTick(int p_74507_0_) {
      if (p_74507_0_ != 0) {
         KeyBinding var1 = (KeyBinding)hash.lookup(p_74507_0_);
         if (var1 != null) {
            ++var1.presses;
         }
      }

   }

   public static boolean getKeyBindState(int key) {
      if (key != 0) {
         KeyBinding var2 = (KeyBinding)hash.lookup(key);
         if (var2 != null) {
            return var2.pressed;
         }
      }

      return false;
   }

   public static void setKeyBindState(int p_74510_0_, boolean p_74510_1_) {
      if (p_74510_0_ != 0) {
         KeyBinding var2 = (KeyBinding)hash.lookup(p_74510_0_);
         if (var2 != null) {
            var2.pressed = p_74510_1_;
         }
      }

   }

   public static void unPressAllKeys() {
      Iterator var0 = keybindArray.iterator();

      while(var0.hasNext()) {
         KeyBinding var1 = (KeyBinding)var0.next();
         var1.unpressKey();
      }

   }

   public static void resetKeyBindingArrayAndHash() {
      hash.clearMap();
      Iterator var0 = keybindArray.iterator();

      while(var0.hasNext()) {
         KeyBinding var1 = (KeyBinding)var0.next();
         hash.addKey(var1.keyCode, var1);
      }

   }

   public static Set func_151467_c() {
      return keybindSet;
   }

   public KeyBinding(String p_i45001_1_, int p_i45001_2_, String p_i45001_3_) {
      this.keyDescription = p_i45001_1_;
      this.keyCode = p_i45001_2_;
      this.keyCodeDefault = p_i45001_2_;
      this.keyCategory = p_i45001_3_;
      keybindArray.add(this);
      hash.addKey(p_i45001_2_, this);
      keybindSet.add(p_i45001_3_);
   }

   public boolean getIsKeyPressed() {
      return this.pressed;
   }

   public String getKeyCategory() {
      return this.keyCategory;
   }

   public boolean isPressed() {
      if (this.presses == 0) {
         return false;
      } else {
         --this.presses;
         return true;
      }
   }

   private void unpressKey() {
      this.presses = 0;
      this.pressed = false;
   }

   public String getKeyDescription() {
      return this.keyDescription;
   }

   public int getKeyCodeDefault() {
      return this.keyCodeDefault;
   }

   public int getKeyCode() {
      return this.keyCode;
   }

   public void setKeyCode(int p_151462_1_) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.gameSettings != null) {
         if (this != mc.gameSettings.keyBindAttack && this != mc.gameSettings.keyBindUseItem) {
            if (this == mc.gameSettings.keyBindSprint && p_151462_1_ != 0) {
               if (p_151462_1_ == mc.gameSettings.keyBindSneak.keyCode) {
                  mc.gameSettings.keyBindSneak.keyCode = 0;
               }
            } else if (this == mc.gameSettings.keyBindSneak && p_151462_1_ != 0 && p_151462_1_ == mc.gameSettings.keyBindSprint.keyCode) {
               mc.gameSettings.keyBindSprint.keyCode = 0;
            }
         } else if (p_151462_1_ != -100 && p_151462_1_ != -99) {
            return;
         }
      }

      this.keyCode = p_151462_1_;
   }

   public int compareTo(KeyBinding p_compareTo_1_) {
      int var2 = I18n.format(this.keyCategory).compareTo(I18n.format(p_compareTo_1_.keyCategory));
      if (var2 == 0) {
         var2 = I18n.format(this.keyDescription).compareTo(I18n.format(p_compareTo_1_.keyDescription));
      }

      return var2;
   }

   public int compareTo(Object p_compareTo_1_) {
      return this.compareTo((KeyBinding)p_compareTo_1_);
   }
}
