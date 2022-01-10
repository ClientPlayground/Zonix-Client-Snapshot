package com.thevoxelbox.voxelmap.interfaces;

import com.thevoxelbox.voxelmap.util.LayoutVariables;
import net.minecraft.client.Minecraft;

public interface IRadar {
   void loadTexturePackIcons();

   void OnTickInGame(Minecraft var1, LayoutVariables var2);
}
