package com.thevoxelbox.voxelmap.interfaces;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Set;

public interface IColorManager {
   int COLOR_FAILED_LOAD = -65025;

   BufferedImage getColorPicker();

   BufferedImage getBlockImage(int var1, int var2);

   boolean checkForChanges();

   int colorAdder(int var1, int var2);

   int colorMultiplier(int var1, int var2);

   int getBlockColorWithDefaultTint(int var1, int var2, int var3);

   int getBlockColor(int var1, int var2, int var3);

   void setSkyColor(int var1);

   int getMapImageInt();

   Set getBiomeTintsAvailable();

   boolean isOptifineInstalled();

   HashMap getBlockTintTables();

   int getAirColor();
}
