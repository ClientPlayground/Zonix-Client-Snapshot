package com.thevoxelbox.voxelmap.interfaces;

import com.thevoxelbox.voxelmap.util.Waypoint;
import java.util.ArrayList;

public interface IWaypointManager {
   ArrayList getWaypoints();

   void deleteWaypoint(Waypoint var1);

   void saveWaypoints();

   void addWaypoint(Waypoint var1);

   void check2dWaypoints();

   void handleDeath();

   void loadWaypoints();

   void moveWaypointEntityToBack();

   void newSubWorldName(String var1);

   void newSubWorldHash(String var1);

   void newWorld(int var1);

   String getCurrentSubworldDescriptor();
}
