package com.thevoxelbox.voxelmap.util;

import java.util.ArrayList;
import java.util.Iterator;

public class CustomMobsManager {
   public static ArrayList mobs = new ArrayList();

   public static void add(String name, boolean enabled) {
      CustomMob mob = getCustomMobByName(name);
      if (mob != null) {
         mob.enabled = enabled;
      } else {
         mobs.add(new CustomMob(name, enabled));
      }

   }

   public static void add(String name, boolean isHostile, boolean isNeutral) {
      CustomMob mob = getCustomMobByName(name);
      if (mob != null) {
         mob.isHostile = isHostile;
         mob.isNeutral = isNeutral;
      } else {
         mobs.add(new CustomMob(name, isHostile, isNeutral));
      }

   }

   public static CustomMob getCustomMobByName(String name) {
      Iterator var1 = mobs.iterator();

      CustomMob mob;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         mob = (CustomMob)var1.next();
      } while(!mob.name.equals(name));

      return mob;
   }
}
