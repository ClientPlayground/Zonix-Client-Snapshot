package us.zonix.client.gui.component.impl;

import java.beans.ConstructorProperties;
import us.zonix.client.gui.ModScreen;
import us.zonix.client.gui.component.ILabelledComponent;
import us.zonix.client.util.RenderUtil;

public abstract class ButtonComponent implements ILabelledComponent {
   private String text;
   private int width;
   private int height;
   private int x;
   private int y;

   @ConstructorProperties({"text", "width", "height", "x", "y"})
   public ButtonComponent(String text, int width, int height, int x, int y) {
      this.text = text;
      this.width = width;
      this.height = height;
      this.x = x;
      this.y = y;
   }

   public void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void draw(int mouseX, int mouseY) {
      boolean hovering = mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
      RenderUtil.drawBorderedRoundedRect((float)this.x, (float)this.y, (float)(this.x + this.width), (float)(this.y + this.height), 1.0F, hovering ? ModScreen.HOVER_COLOR : ModScreen.NORMAL_COLOR, 452984831);
      RenderUtil.drawCenteredString(this.text, this.x + this.width / 2, this.y + this.height / 2, -16777216);
   }

   public String getText() {
      return this.text;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public void setText(String text) {
      this.text = text;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public void setHeight(int height) {
      this.height = height;
   }
}
