package us.zonix.client.setting.impl;

import us.zonix.client.setting.ISetting;

public final class FloatSetting implements ISetting {
   private final String name;
   private final Float min;
   private final Float max;
   private Float value;

   public FloatSetting(String name, Float min, Float max, Float value) {
      this.name = name;
      this.min = min;
      this.max = max;
      this.value = value;
   }

   public void setValue(Float value) {
      this.value = Math.max(this.min.floatValue(), Math.min(this.max.floatValue(), value.floatValue()));
   }

   public String getName() {
      return this.name;
   }

   public Float getValue() {
      return this.value;
   }

   public Float getMin() {
      return this.min;
   }

   public Float getMax() {
      return this.max;
   }
}
