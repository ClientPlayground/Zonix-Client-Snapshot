package com.thevoxelbox.voxelmap.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public interface IMap {
   String getCurrentWorldName();

   void forceFullRender(boolean var1);

   void drawMinimap(Minecraft var1);

   void chunkCalc(Chunk var1);

   float getPercentX();

   float getPercentY();

   void onTickInGame(Minecraft var1);

   void setPermissions(boolean var1, boolean var2);
}
