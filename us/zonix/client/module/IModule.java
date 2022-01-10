package us.zonix.client.module;

import java.util.List;
import java.util.Map;

public interface IModule {
   Map getSettingMap();

   List getSortedSettings();

   String getName();

   boolean isEnabled();

   void setEnabled(boolean var1);

   int getWidth();

   int getHeight();

   float getX();

   float getY();

   void setX(float var1);

   void setY(float var1);

   default void renderPreview() {
      this.renderReal();
   }

   default void renderReal() {
   }

   default void onPostPlayerUpdate() {
   }

   default void onPrePlayerUpdate() {
   }
}
