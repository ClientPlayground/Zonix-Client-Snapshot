package com.thevoxelbox.voxelmap.interfaces;

import com.thevoxelbox.voxelmap.util.Dimension;
import java.util.ArrayList;

public interface IDimensionManager {
   ArrayList getDimensions();

   Dimension getDimensionByID(int var1);

   void enteredDimension(int var1);

   void populateDimensions();
}
