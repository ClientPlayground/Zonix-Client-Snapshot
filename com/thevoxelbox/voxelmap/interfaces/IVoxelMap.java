package com.thevoxelbox.voxelmap.interfaces;

import com.thevoxelbox.voxelmap.MapSettingsManager;
import com.thevoxelbox.voxelmap.RadarSettingsManager;

public interface IVoxelMap {
   IObservableChunkChangeNotifier getNotifier();

   MapSettingsManager getMapOptions();

   RadarSettingsManager getRadarOptions();

   IMap getMap();

   IRadar getRadar();

   IColorManager getColorManager();

   IWaypointManager getWaypointManager();

   IDimensionManager getDimensionManager();

   void setPermissions(boolean var1, boolean var2);

   void newSubWorldName(String var1);

   void newSubWorldHash(String var1);
}
