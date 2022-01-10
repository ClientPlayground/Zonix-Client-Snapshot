package us.zonix.client.setting.impl;

import us.zonix.client.setting.ISetting;

public final class TextSetting implements ISetting {
   private final String name;
   private final String defaultValue;
   private String value;
   private boolean editing;
   private boolean valued;
   private long valueFlipTime;

   public TextSetting(String name, String value) {
      this.name = name;
      this.defaultValue = this.value = value;
   }

   public void setValue(String value) {
      this.value = value;
      if (this.value.isEmpty()) {
         this.value = this.defaultValue;
      }

   }

   public String getName() {
      return this.name;
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }

   public String getValue() {
      return this.value;
   }

   public boolean isEditing() {
      return this.editing;
   }

   public boolean isValued() {
      return this.valued;
   }

   public long getValueFlipTime() {
      return this.valueFlipTime;
   }

   public void setEditing(boolean editing) {
      this.editing = editing;
   }

   public void setValued(boolean valued) {
      this.valued = valued;
   }

   public void setValueFlipTime(long valueFlipTime) {
      this.valueFlipTime = valueFlipTime;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TextSetting)) {
         return false;
      } else {
         TextSetting other;
         label56: {
            other = (TextSetting)o;
            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null) {
               if (other$name == null) {
                  break label56;
               }
            } else if (this$name.equals(other$name)) {
               break label56;
            }

            return false;
         }

         label49: {
            Object this$defaultValue = this.getDefaultValue();
            Object other$defaultValue = other.getDefaultValue();
            if (this$defaultValue == null) {
               if (other$defaultValue == null) {
                  break label49;
               }
            } else if (this$defaultValue.equals(other$defaultValue)) {
               break label49;
            }

            return false;
         }

         Object this$value = this.getValue();
         Object other$value = other.getValue();
         if (this$value == null) {
            if (other$value != null) {
               return false;
            }
         } else if (!this$value.equals(other$value)) {
            return false;
         }

         if (this.isEditing() != other.isEditing()) {
            return false;
         } else if (this.isValued() != other.isValued()) {
            return false;
         } else if (this.getValueFlipTime() != other.getValueFlipTime()) {
            return false;
         } else {
            return true;
         }
      }
   }

   public int hashCode() {
      int PRIME = true;
      int result = 1;
      Object $name = this.getName();
      int result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $defaultValue = this.getDefaultValue();
      result = result * 59 + ($defaultValue == null ? 43 : $defaultValue.hashCode());
      Object $value = this.getValue();
      result = result * 59 + ($value == null ? 43 : $value.hashCode());
      result = result * 59 + (this.isEditing() ? 79 : 97);
      result = result * 59 + (this.isValued() ? 79 : 97);
      long $valueFlipTime = this.getValueFlipTime();
      result = result * 59 + (int)($valueFlipTime >>> 32 ^ $valueFlipTime);
      return result;
   }

   public String toString() {
      return "TextSetting(name=" + this.getName() + ", defaultValue=" + this.getDefaultValue() + ", value=" + this.getValue() + ", editing=" + this.isEditing() + ", valued=" + this.isValued() + ", valueFlipTime=" + this.getValueFlipTime() + ")";
   }
}
