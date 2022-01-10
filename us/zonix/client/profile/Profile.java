package us.zonix.client.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import us.zonix.client.Client;
import us.zonix.client.module.IModule;
import us.zonix.client.setting.ISetting;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.FloatSetting;
import us.zonix.client.setting.impl.StringSetting;
import us.zonix.client.setting.impl.TextSetting;

public final class Profile {
   private final String name;
   private boolean enabled;

   public void load() {
      File file = new File(Minecraft.getMinecraft().mcDataDir, "Zonix/profiles/" + this.name + ".json");
      if (!file.exists()) {
         throw new RuntimeException("Can't load empty file.");
      } else {
         try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Throwable var3 = null;

            try {
               JsonArray array = (new JsonParser()).parse(reader).getAsJsonArray();
               Iterator var5 = array.iterator();

               while(true) {
                  JsonObject object;
                  IModule module;
                  do {
                     if (!var5.hasNext()) {
                        return;
                     }

                     JsonElement element = (JsonElement)var5.next();
                     object = element.getAsJsonObject();
                     String name = object.get("name").getAsString();
                     module = Client.getInstance().getModuleManager().getModule(name);
                  } while(module == null);

                  module.setEnabled(object.get("enabled").getAsBoolean());
                  module.setX(object.get("x").getAsFloat());
                  module.setY(object.get("y").getAsFloat());
                  JsonArray settings = object.getAsJsonArray("settings");
                  Iterator var11 = settings.iterator();

                  while(var11.hasNext()) {
                     JsonElement jsonElement = (JsonElement)var11.next();
                     JsonObject settingObject = jsonElement.getAsJsonObject();
                     String settingName = settingObject.get("name").getAsString();
                     ISetting setting = (ISetting)module.getSettingMap().get(settingName.toLowerCase());
                     if (setting != null) {
                        JsonElement value = settingObject.get("value");
                        if (value != null && !value.isJsonNull()) {
                           if (setting instanceof FloatSetting) {
                              ((FloatSetting)setting).setValue(value.getAsFloat());
                           } else if (setting instanceof ColorSetting) {
                              ((ColorSetting)setting).setValue(value.getAsInt());
                              JsonElement chromaElement = settingObject.get("chroma");
                              if (chromaElement != null && !chromaElement.isJsonNull()) {
                                 ((ColorSetting)setting).setChroma(chromaElement.getAsBoolean());
                              }
                           } else if (setting instanceof BooleanSetting) {
                              ((BooleanSetting)setting).setValue(value.getAsBoolean());
                           } else if (setting instanceof StringSetting) {
                              ((StringSetting)setting).setValue(value.getAsString());
                           } else if (setting instanceof TextSetting) {
                              ((TextSetting)setting).setValue(value.getAsString());
                           }
                        }
                     }
                  }
               }
            } catch (Throwable var27) {
               var3 = var27;
               throw var27;
            } finally {
               if (reader != null) {
                  if (var3 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var26) {
                        var3.addSuppressed(var26);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         } catch (IOException var29) {
            throw new RuntimeException("Error loading profile (" + this.name + ")", var29);
         } catch (JsonParseException var30) {
            Logger.getGlobal().warning("Error loading profile (" + this.name + ") : " + var30.getMessage());
         }
      }
   }

   public void save() {
      JsonArray mods = new JsonArray();
      Iterator var2 = Client.getInstance().getModuleManager().getModules().iterator();

      while(var2.hasNext()) {
         IModule module = (IModule)var2.next();
         JsonObject object = new JsonObject();
         object.addProperty("name", module.getName());
         object.addProperty("x", module.getX());
         object.addProperty("y", module.getY());
         object.addProperty("enabled", module.isEnabled());
         JsonArray settings = new JsonArray();

         JsonObject settingObject;
         for(Iterator var6 = module.getSettingMap().values().iterator(); var6.hasNext(); settings.add(settingObject)) {
            ISetting setting = (ISetting)var6.next();
            settingObject = new JsonObject();
            settingObject.addProperty("name", setting.getName().toLowerCase());
            if (setting instanceof FloatSetting) {
               settingObject.addProperty("value", (Number)setting.getValue());
            } else if (setting instanceof ColorSetting) {
               boolean chroma = ((ColorSetting)setting).isChroma();
               settingObject.addProperty("chroma", chroma);
               ((ColorSetting)setting).setChroma(false);
               settingObject.addProperty("value", (Number)setting.getValue());
               ((ColorSetting)setting).setChroma(chroma);
            } else if (setting instanceof BooleanSetting) {
               settingObject.addProperty("value", (Boolean)setting.getValue());
            } else if (setting instanceof StringSetting || setting instanceof TextSetting) {
               settingObject.addProperty("value", (String)setting.getValue());
            }
         }

         object.add("settings", settings);
         mods.add(object);
      }

      File file = new File(Minecraft.getMinecraft().mcDataDir, "Zonix/profiles/" + this.name + ".json");
      if (!file.exists()) {
         file.getParentFile().mkdirs();

         try {
            file.createNewFile();
         } catch (IOException var20) {
            throw new RuntimeException("Error saving profile (" + this.name + ")", var20);
         }
      }

      try {
         PrintWriter writer = new PrintWriter(file);
         Throwable var25 = null;

         try {
            Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
            writer.println(gson.toJson(mods));
         } catch (Throwable var19) {
            var25 = var19;
            throw var19;
         } finally {
            if (writer != null) {
               if (var25 != null) {
                  try {
                     writer.close();
                  } catch (Throwable var18) {
                     var25.addSuppressed(var18);
                  }
               } else {
                  writer.close();
               }
            }

         }

      } catch (FileNotFoundException var22) {
         throw new RuntimeException("Error saving profile (" + this.name + ")", var22);
      }
   }

   public Profile(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Profile)) {
         return false;
      } else {
         Profile other = (Profile)o;
         Object this$name = this.getName();
         Object other$name = other.getName();
         if (this$name == null) {
            if (other$name == null) {
               return this.isEnabled() == other.isEnabled();
            }
         } else if (this$name.equals(other$name)) {
            return this.isEnabled() == other.isEnabled();
         }

         return false;
      }
   }

   public int hashCode() {
      int PRIME = true;
      int result = 1;
      Object $name = this.getName();
      int result = result * 59 + ($name == null ? 43 : $name.hashCode());
      result = result * 59 + (this.isEnabled() ? 79 : 97);
      return result;
   }

   public String toString() {
      return "Profile(name=" + this.getName() + ", enabled=" + this.isEnabled() + ")";
   }
}
