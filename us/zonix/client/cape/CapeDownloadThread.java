package us.zonix.client.cape;

import java.awt.image.BufferedImage;
import java.beans.ConstructorProperties;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;

public class CapeDownloadThread implements Runnable {
   private final UUID uniqueId;
   private final CapeDownloadThread.CapeCallback callback;

   @ConstructorProperties({"uniqueId", "callback"})
   public CapeDownloadThread(UUID uniqueId, CapeDownloadThread.CapeCallback callback) {
      this.uniqueId = uniqueId;
      this.callback = callback;
   }

   public void run() {
      File dir = new File(Minecraft.getMinecraft().mcDataDir, "capes");
      dir.mkdirs();
      String cape = "kitten";
      File capeFile = new File(dir, cape + ".png");
      if (!capeFile.exists()) {
         try {
            BufferedImage image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("assets/minecraft/capes/" + cape + ".png"));
            ImageIO.write(image, "png", capeFile);
            image.flush();
         } catch (IOException var6) {
            var6.printStackTrace();
         }

         this.callback.callback(cape);
      } else {
         this.callback.callback(cape);
      }
   }

   public interface CapeCallback {
      void callback(String var1);
   }
}
