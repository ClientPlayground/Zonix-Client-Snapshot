package us.zonix.client.gui.component;

public interface IComponent {
   int getX();

   int getY();

   void setPosition(int var1, int var2);

   int getWidth();

   int getHeight();

   void setWidth(int var1);

   void setHeight(int var1);

   void onOpen();

   void tick();

   void draw(int var1, int var2);

   void onClick(int var1, int var2, int var3);

   default void onMouseEvent() {
   }

   default void onMouseRelease() {
   }

   default void onKeyPress(int code, char c) {
   }
}
