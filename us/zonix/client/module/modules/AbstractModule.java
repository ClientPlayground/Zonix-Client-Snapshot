package us.zonix.client.module.modules;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import us.zonix.client.module.IModule;
import us.zonix.client.setting.ISetting;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.FloatSetting;

public abstract class AbstractModule implements IModule {
   private final Map settingMap = new HashMap();
   private final List sortedSettings = new LinkedList();
   protected final Minecraft mc = Minecraft.getMinecraft();
   private final String name;
   protected float x = 2.0F;
   protected float y = 2.0F;
   private int height;
   private int width;

   protected AbstractModule(String name) {
      this.name = name;
      this.addSetting(new BooleanSetting("Enabled", true));
   }

   public boolean isEnabled() {
      return this.getBooleanSetting("Enabled").getValue().booleanValue();
   }

   public void setEnabled(boolean enabled) {
      this.getBooleanSetting("Enabled").setValue(enabled);
   }

   protected void addSetting(ISetting t) {
      this.settingMap.put(t.getName().toLowerCase(), t);
      this.sortedSettings.add(t);
   }

   private ISetting getSetting(String name) {
      return (ISetting)this.settingMap.get(name.toLowerCase());
   }

   protected BooleanSetting getBooleanSetting(String name) {
      return (BooleanSetting)this.getSetting(name);
   }

   protected FloatSetting getFloatSetting(String name) {
      return (FloatSetting)this.getSetting(name);
   }

   protected ColorSetting getColorSetting(String name) {
      return (ColorSetting)this.getSetting(name);
   }

   public Map getSettingMap() {
      return this.settingMap;
   }

   public List getSortedSettings() {
      return this.sortedSettings;
   }

   public Minecraft getMc() {
      return this.mc;
   }

   public String getName() {
      return this.name;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public void setX(float x) {
      this.x = x;
   }

   public void setY(float y) {
      this.y = y;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public void setWidth(int width) {
      this.width = width;
   }
}
