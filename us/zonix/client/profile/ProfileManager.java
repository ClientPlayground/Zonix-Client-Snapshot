package us.zonix.client.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;

public final class ProfileManager {
   public static final Gson PRETTY_GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final Map loadedProfiles = new HashMap();
   private Profile activeConfig;
   private String activeConfigName;

   public void loadConfig(String name) {
      Profile profile = (Profile)this.loadedProfiles.get(name.toLowerCase());
      if (profile == null) {
         Logger.getGlobal().severe("Invalid config supplied when loading profile (" + name + ")");
      } else {
         profile.load();
         this.activeConfigName = profile.getName();
         this.activeConfig = profile;
         this.saveSettings();
         Logger.getGlobal().info("Loaded config " + profile.getName());
      }
   }

   public Profile createConfig(String name) {
      Profile profile = new Profile(name);
      profile.save();
      this.activeConfigName = profile.getName();
      this.activeConfig = profile;
      this.saveSettings();
      return (Profile)this.loadedProfiles.put(name.toLowerCase(), profile);
   }

   public void saveSettings() {
      File dir = new File(Minecraft.getMinecraft().mcDataDir, "Zonix");
      if (!dir.exists()) {
         dir.mkdirs();
      }

      File settings = new File(dir, "settings.json");
      if (!settings.exists()) {
         try {
            settings.createNewFile();
         } catch (IOException var16) {
            throw new RuntimeException("Error creating settings.json");
         }
      }

      try {
         PrintWriter writer = new PrintWriter(settings);
         Throwable var4 = null;

         try {
            JsonObject object = new JsonObject();
            if (this.activeConfig != null) {
               object.addProperty("active-config", this.activeConfig.getName());
            } else {
               object.addProperty("active-config", "default");
            }

            writer.println(PRETTY_GSON.toJson(object));
         } catch (Throwable var15) {
            var4 = var15;
            throw var15;
         } finally {
            if (writer != null) {
               if (var4 != null) {
                  try {
                     writer.close();
                  } catch (Throwable var14) {
                     var4.addSuppressed(var14);
                  }
               } else {
                  writer.close();
               }
            }

         }

      } catch (FileNotFoundException var18) {
         throw new RuntimeException("Error writing settings.json");
      }
   }

   public void saveConfig() {
      if (this.activeConfig != null) {
         this.activeConfig.save();
         this.saveSettings();
      } else {
         this.createConfig("default");
      }

   }

   public void load() {
      File dir = new File(Minecraft.getMinecraft().mcDataDir, "Zonix/profiles");
      if (!dir.exists()) {
         dir.mkdirs();
      } else {
         File settings = new File(dir.getParentFile(), "settings.json");
         if (!settings.exists()) {
            try {
               settings.createNewFile();
            } catch (IOException var18) {
               throw new RuntimeException("Error creating settings.json");
            }
         } else {
            try {
               BufferedReader reader = new BufferedReader(new FileReader(settings));
               Throwable var4 = null;

               try {
                  JsonObject object = (new JsonParser()).parse(reader).getAsJsonObject();
                  this.activeConfigName = object.get("active-config").getAsString();
               } catch (Throwable var17) {
                  var4 = var17;
                  throw var17;
               } finally {
                  if (reader != null) {
                     if (var4 != null) {
                        try {
                           reader.close();
                        } catch (Throwable var16) {
                           var4.addSuppressed(var16);
                        }
                     } else {
                        reader.close();
                     }
                  }

               }
            } catch (IOException var20) {
               throw new RuntimeException("Error reading settings.json");
            } catch (IllegalStateException | JsonParseException var21) {
               settings.delete();
               throw new RuntimeException("Error parsing settings.json... Resetting.");
            }
         }

         File[] var22 = (File[])Objects.requireNonNull(dir.listFiles());
         int var23 = var22.length;

         for(int var24 = 0; var24 < var23; ++var24) {
            File file = var22[var24];
            if (file.getName().endsWith(".json")) {
               String name = file.getName().replace(".json", "");
               if (!name.equalsIgnoreCase("settings")) {
                  Profile profile = new Profile(name);
                  this.loadedProfiles.put(name.toLowerCase(), profile);
                  if (name.equalsIgnoreCase(this.activeConfigName)) {
                     this.activeConfig = profile;
                     profile.load();
                  }
               }
            }
         }

         this.saveSettings();
      }
   }

   public Map getLoadedProfiles() {
      return this.loadedProfiles;
   }

   public Profile getActiveConfig() {
      return this.activeConfig;
   }

   public String getActiveConfigName() {
      return this.activeConfigName;
   }
}
