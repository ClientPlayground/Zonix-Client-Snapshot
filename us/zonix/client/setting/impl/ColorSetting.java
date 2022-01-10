package us.zonix.client.setting.impl;

import java.awt.Color;
import us.zonix.client.setting.ISetting;

public final class ColorSetting implements ISetting {
   private final String name;
   private final Integer defaultValue;
   private int[] pickerLocation;
   private boolean draggingAlpha;
   private boolean draggingAll;
   private boolean draggingHue;
   private boolean picking;
   private boolean chroma;
   private float brightness;
   private float saturation;
   private float alpha;
   private float hue;
   private int state;
   private int value;

   public ColorSetting(String name, int defaultValue) {
      this.name = name;
      this.defaultValue = defaultValue;
      this.value = this.defaultValue.intValue();
      Color color = new Color(this.defaultValue.intValue());
      this.alpha = (float)color.getAlpha();
      float[] floats = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null);
      this.saturation = floats[1];
      this.brightness = floats[2];
      this.hue = floats[0];
      if (this.name.equals("Background")) {
         this.saturation = 1.0F;
         this.state = 2;
      }

   }

   public Integer getValue() {
      int color = this.value;
      if (this.name.equals("Background")) {
         return color & 16777215 | 1862270976;
      } else {
         return this.chroma ? Color.HSBtoRGB((float)(System.currentTimeMillis() % 1000L) / 1000.0F, 0.8F, 0.8F) : color;
      }
   }

   public void setValue(Integer integer) {
      this.value = integer.intValue();
   }

   public String getName() {
      return this.name;
   }

   public Integer getDefaultValue() {
      return this.defaultValue;
   }

   public boolean isDraggingAlpha() {
      return this.draggingAlpha;
   }

   public boolean isDraggingAll() {
      return this.draggingAll;
   }

   public boolean isDraggingHue() {
      return this.draggingHue;
   }

   public boolean isPicking() {
      return this.picking;
   }

   public boolean isChroma() {
      return this.chroma;
   }

   public float getBrightness() {
      return this.brightness;
   }

   public float getSaturation() {
      return this.saturation;
   }

   public float getAlpha() {
      return this.alpha;
   }

   public float getHue() {
      return this.hue;
   }

   public int getState() {
      return this.state;
   }

   public int[] getPickerLocation() {
      return this.pickerLocation;
   }

   public void setPickerLocation(int[] pickerLocation) {
      this.pickerLocation = pickerLocation;
   }

   public void setDraggingAlpha(boolean draggingAlpha) {
      this.draggingAlpha = draggingAlpha;
   }

   public void setDraggingAll(boolean draggingAll) {
      this.draggingAll = draggingAll;
   }

   public void setDraggingHue(boolean draggingHue) {
      this.draggingHue = draggingHue;
   }

   public void setPicking(boolean picking) {
      this.picking = picking;
   }

   public void setChroma(boolean chroma) {
      this.chroma = chroma;
   }

   public void setBrightness(float brightness) {
      this.brightness = brightness;
   }

   public void setSaturation(float saturation) {
      this.saturation = saturation;
   }

   public void setAlpha(float alpha) {
      this.alpha = alpha;
   }

   public void setHue(float hue) {
      this.hue = hue;
   }

   public void setState(int state) {
      this.state = state;
   }
}
