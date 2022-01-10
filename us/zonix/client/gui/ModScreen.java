package us.zonix.client.gui;

import java.awt.Color;
import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.gui.component.IComponent;
import us.zonix.client.gui.component.impl.menu.MenuComponent;
import us.zonix.client.module.IModule;
import us.zonix.client.module.impl.ZansMinimap;
import us.zonix.client.util.RenderUtil;

public final class ModScreen extends GuiScreen {
   public static final int NORMAL_COLOR = (new Color(169, 169, 169, 200)).getRGB();
   public static final int HOVER_COLOR = (new Color(255, 0, 117, 200)).getRGB();
   private final Set components = new HashSet();
   private ModScreen.DragMod dragging;
   private boolean open;

   private void addButtons() {
      this.components.add(new MenuComponent());
   }

   public void initGui() {
      this.open = true;
      (new Thread(() -> {
         while(this.open) {
            Iterator var1 = this.components.iterator();

            while(var1.hasNext()) {
               IComponent component = (IComponent)var1.next();
               component.tick();
            }

            try {
               Thread.sleep(10L);
            } catch (InterruptedException var3) {
               var3.printStackTrace();
            }
         }

      })).start();
      this.mc.entityRenderer.setBlur(true);
      if (this.components.isEmpty()) {
         this.addButtons();
      }

      Iterator var1 = this.components.iterator();

      while(var1.hasNext()) {
         IComponent component = (IComponent)var1.next();
         component.onOpen();
      }

   }

   public void onGuiClosed() {
      this.open = false;
      this.mc.entityRenderer.setBlur(false);
   }

   protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
      this.dragging = null;
      this.components.forEach(IComponent::onMouseRelease);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution resolution = new ScaledResolution(this.mc);
      float setX;
      float setY;
      if (this.dragging != null) {
         setX = (float)resolution.getScaledWidth();
         setY = (float)resolution.getScaledHeight();
         RenderUtil.drawRect(0.0F, 2.0F, setX, 3.0F, -287162561);
         RenderUtil.drawRect(2.0F, 0.0F, 3.0F, setY, -287162561);
         RenderUtil.drawRect(setX - 2.0F, 0.0F, setX - 3.0F, setY, -287162561);
         RenderUtil.drawRect(0.0F, setY - 2.0F, setX, setY - 3.0F, -287162561);
         RenderUtil.drawRect(0.0F, setY / 2.0F - 0.5F, setX, setY / 2.0F + 0.5F, -287162561);
         RenderUtil.drawRect(setX / 2.0F - 0.5F, 0.0F, setX / 2.0F + 0.5F, setY, -287162561);
      }

      Iterator var11 = Client.getInstance().getModuleManager().getEnabledModules().iterator();

      int width;
      while(var11.hasNext()) {
         IModule module = (IModule)var11.next();
         module.renderPreview();
         GL11.glPushMatrix();
         boolean mouseOver = this.isMouseOver(module, mouseX, mouseY);
         width = mouseOver ? HOVER_COLOR : NORMAL_COLOR;
         RenderUtil.drawBorderedRect(module.getX(), module.getY(), module.getX() + (float)module.getWidth(), module.getY() + (float)module.getHeight(), 1.0F, width, 452984831);
         GL11.glPopMatrix();
      }

      if (this.dragging != null) {
         setX = (float)(mouseX - this.dragging.x);
         setY = (float)(mouseY - this.dragging.y);
         if ((float)mouseX - this.dragging.module.getX() != (float)this.dragging.x || (float)mouseY - this.dragging.module.getY() != (float)this.dragging.y) {
            this.dragging.moved = true;
         }

         int height = this.dragging.module.getHeight();
         width = this.dragging.module.getWidth();
         if (setX < 2.0F) {
            setX = 2.0F;
         } else if (setX + (float)width > (float)(resolution.getScaledWidth() - 2)) {
            setX = (float)(resolution.getScaledWidth() - width - 2);
         }

         if (setY < 2.0F) {
            setY = 2.0F;
         } else if (setY + (float)height > (float)(resolution.getScaledHeight() - 2)) {
            setY = (float)(resolution.getScaledHeight() - height - 2);
         }

         this.dragging.module.setX(setX);
         this.dragging.module.setY(setY);
         Iterator var9 = Client.getInstance().getModuleManager().getEnabledModules().iterator();

         while(var9.hasNext()) {
            IModule module = (IModule)var9.next();
            if (module != this.dragging.module && module.getWidth() != 0 && module.getHeight() != 0) {
               this.snapToModule(module);
            }
         }

         this.snapToGuideLines(resolution);
      }

      if (this.dragging == null) {
         var11 = this.components.iterator();

         while(var11.hasNext()) {
            IComponent component = (IComponent)var11.next();
            component.draw(mouseX, mouseY);
         }
      }

   }

   public void handleMouseInput() {
      super.handleMouseInput();
      if (this.dragging == null) {
         this.components.forEach(IComponent::onMouseEvent);
      }

   }

   protected void keyTyped(char c, int key) {
      super.keyTyped(c, key);
      this.components.forEach((iComponent) -> {
         iComponent.onKeyPress(key, c);
      });
   }

   protected void mouseClicked(int mouseX, int mouseY, int button) {
      if (this.dragging == null) {
         this.components.forEach((component) -> {
            if (component instanceof MenuComponent) {
               component.onClick(mouseX, mouseY, button);
            } else {
               if (mouseX > component.getX() && mouseX < component.getX() + component.getWidth() && mouseY > component.getY() && mouseY < component.getY() + component.getHeight()) {
                  component.onClick(mouseX, mouseY, button);
               }

            }
         });
      }

      Iterator var4 = Client.getInstance().getModuleManager().getEnabledModules().iterator();

      while(var4.hasNext()) {
         IModule module = (IModule)var4.next();
         if (!(module instanceof ZansMinimap) && this.isMouseOver(module, mouseX, mouseY)) {
            if (button != 1) {
               int x = (int)((float)mouseX - module.getX());
               int y = (int)((float)mouseY - module.getY());
               this.dragging = new ModScreen.DragMod(module, x, y);
            } else if (module.getSettingMap().size() > 1) {
               MenuComponent menuComponent = (MenuComponent)((IComponent[])this.components.toArray(new IComponent[0]))[0];
               if (menuComponent.getMenuType() == MenuComponent.EnumMenuType.MODS) {
                  menuComponent.setScrollAmount(0);
                  menuComponent.setSwitchTime(0);
               }

               menuComponent.setMenuType(MenuComponent.EnumMenuType.MOD);
               menuComponent.setEditing(module);
            }
            break;
         }
      }

   }

   private boolean isMouseOver(IModule module, int mouseX, int mouseY) {
      float minX = module.getX();
      float minY = module.getY();
      float maxX = minX + (float)module.getWidth();
      float maxY = minY + (float)module.getHeight();
      return (float)mouseX > minX && (float)mouseY > minY && (float)mouseX < maxX && (float)mouseY < maxY;
   }

   private void snapToModule(IModule module) {
      IModule dragging = this.dragging.module;
      float minToMinX = module.getX() - dragging.getX();
      float maxToMaxX = module.getX() + (float)module.getWidth() - (dragging.getX() + (float)dragging.getWidth());
      float maxToMinX = module.getX() + (float)module.getWidth() - dragging.getX();
      float minToMaxX = module.getX() - (dragging.getX() + (float)dragging.getWidth());
      float minToMinY = module.getY() - dragging.getY();
      float maxToMaxY = module.getY() + (float)module.getHeight() - (dragging.getY() + (float)dragging.getHeight());
      float maxToMinY = module.getY() + (float)module.getHeight() - dragging.getY();
      float minToMaxY = module.getY() - (dragging.getY() + (float)dragging.getHeight());
      boolean xSnap = false;
      boolean ySnap = false;
      if (minToMinX >= -2.0F && minToMinX <= 2.0F) {
         dragging.setX(dragging.getX() + minToMinX);
         xSnap = true;
      }

      if (maxToMaxX >= -2.0F && maxToMaxX <= 2.0F && !xSnap) {
         dragging.setX(dragging.getX() + maxToMaxX);
         xSnap = true;
      }

      if (minToMaxX >= -2.0F && minToMaxX <= 2.0F && !xSnap) {
         dragging.setX(dragging.getX() + minToMaxX);
         xSnap = true;
      }

      if (maxToMinX >= -2.0F && maxToMinX <= 2.0F && !xSnap) {
         dragging.setX(dragging.getX() + maxToMinX);
      }

      if (minToMinY >= -2.0F && minToMinY <= 2.0F) {
         dragging.setY(dragging.getY() + minToMinY);
         ySnap = true;
      }

      if (maxToMaxY >= -2.0F && maxToMaxY <= 2.0F && !ySnap) {
         dragging.setY(dragging.getY() + maxToMaxY);
         ySnap = true;
      }

      if (minToMaxY >= -2.0F && minToMaxY <= 2.0F && !ySnap) {
         dragging.setY(dragging.getY() + minToMaxY);
         ySnap = true;
      }

      if (maxToMinY >= -2.0F && maxToMinY <= 2.0F && !ySnap) {
         dragging.setY(dragging.getY() + maxToMinY);
      }

   }

   private void snapToGuideLines(ScaledResolution resolution) {
      IModule dragging = this.dragging.module;
      float height = (float)(resolution.getScaledHeight() / 2);
      float width = (float)(resolution.getScaledWidth() / 2);
      float draggingMinX = dragging.getX();
      float draggingMaxX = draggingMinX + (float)this.dragging.module.getWidth();
      float draggingHalfX = draggingMinX + (float)(this.dragging.module.getWidth() / 2);
      float draggingMinY = dragging.getY();
      float draggingMaxY = draggingMinY + (float)this.dragging.module.getHeight();
      float draggingHalfY = draggingMinY + (float)(this.dragging.module.getHeight() / 2);
      if (this.checkBounds(draggingMinX, width)) {
         dragging.setX(width);
      }

      if (this.checkBounds(draggingMinY, height)) {
         dragging.setY(height);
      }

      if (this.checkBounds(draggingMaxX, width)) {
         dragging.setX(width - (float)dragging.getWidth());
      }

      if (this.checkBounds(draggingMaxY, height)) {
         dragging.setY(height - (float)dragging.getHeight());
      }

      if (this.checkBounds(draggingHalfX, width)) {
         dragging.setX(width - (float)(dragging.getWidth() / 2));
      }

      if (this.checkBounds(draggingHalfY, height)) {
         dragging.setY(height - (float)(dragging.getHeight() / 2));
      }

   }

   private boolean checkBounds(float f1, float f2) {
      return f1 >= f2 - 2.0F && f1 <= f2 + 2.0F;
   }

   private final class DragMod {
      private final IModule module;
      private boolean moved;
      private int x;
      private int y;

      @ConstructorProperties({"module", "x", "y"})
      public DragMod(IModule module, int x, int y) {
         this.module = module;
         this.x = x;
         this.y = y;
      }
   }
}
