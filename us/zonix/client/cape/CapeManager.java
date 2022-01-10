package us.zonix.client.cape;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class CapeManager {
   private final Map capeCache = new HashMap();

   private void giveCape(UUID uuid, String cape) {
      EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByUUID(uuid);
      if (player instanceof AbstractClientPlayer) {
         AbstractClientPlayer clientPlayer = (AbstractClientPlayer)player;
         ResourceLocation resourceLocation = new ResourceLocation("capes/" + cape);
         TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
         CapeManager.CapeImageBuffer buffer = new CapeManager.CapeImageBuffer(clientPlayer, resourceLocation);
         ThreadDownloadImageData textureCape = new ThreadDownloadImageData((File)null, "https://zonix.us/api/client/cape/download/" + uuid.toString(), (ResourceLocation)null, buffer);
         textureManager.loadTexture(resourceLocation, textureCape);
      }
   }

   private void setCape(AbstractClientPlayer player, ResourceLocation location) {
      if (location != null) {
         player.func_152121_a(Type.CAPE, location);
      }
   }

   public void onSpawn(final UUID uuid) {
      String cape = (String)this.capeCache.get(uuid);
      if (cape == null) {
         (new Thread(new CapeDownloadThread(uuid, new CapeDownloadThread.CapeCallback() {
            public void callback(String name) {
               CapeManager.this.capeCache.put(uuid, name);
               CapeManager.this.giveCape(uuid, name);
            }
         }))).start();
      } else {
         this.giveCape(uuid, cape);
      }
   }

   public void onDestroy(UUID uuid) {
      this.capeCache.remove(uuid);
   }

   private static BufferedImage parseCape(BufferedImage img) {
      int imageWidth = 64;
      int imageHeight = 32;
      int srcWidth = img.getWidth();

      for(int srcHeight = img.getHeight(); imageWidth < srcWidth || imageHeight < srcHeight; imageHeight *= 2) {
         imageWidth *= 2;
      }

      BufferedImage image = new BufferedImage(imageWidth, imageHeight, 2);
      Graphics graphics = image.getGraphics();
      graphics.drawImage(img, 0, 0, (ImageObserver)null);
      graphics.dispose();
      return image;
   }

   private class CapeImageBuffer implements IImageBuffer {
      private final WeakReference playerWeakReference;
      private final ResourceLocation location;

      CapeImageBuffer(AbstractClientPlayer player, ResourceLocation location) {
         this.playerWeakReference = new WeakReference(player);
         this.location = location;
      }

      public BufferedImage parseUserSkin(BufferedImage bufferedImage) {
         return CapeManager.parseCape(bufferedImage);
      }

      public void func_152634_a() {
         CapeManager.this.setCape((AbstractClientPlayer)this.playerWeakReference.get(), this.location);
      }
   }
}
