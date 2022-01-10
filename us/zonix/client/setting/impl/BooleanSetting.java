package us.zonix.client.setting.impl;

import java.beans.ConstructorProperties;
import us.zonix.client.setting.ISetting;

public final class BooleanSetting implements ISetting {
   private final String name;
   private Boolean value = false;

   public BooleanSetting(String name, Boolean value) {
      this.name = name;
      this.value = value;
   }

   @ConstructorProperties({"name"})
   public BooleanSetting(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public Boolean getValue() {
      return this.value;
   }

   public void setValue(Boolean value) {
      this.value = value;
   }
}
