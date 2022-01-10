package us.zonix.client.module.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import us.zonix.client.Client;
import us.zonix.client.module.modules.AbstractModule;
import us.zonix.client.setting.impl.BooleanSetting;
import us.zonix.client.setting.impl.ColorSetting;
import us.zonix.client.setting.impl.LabelSetting;
import us.zonix.client.setting.impl.StringSetting;
import us.zonix.client.util.HUDUtils;
import us.zonix.client.util.RenderUtil;
import us.zonix.client.util.font.ZFontRenderer;

public final class DirectionHUD extends AbstractModule {
   private static final ResourceLocation CARET_DOWN = new ResourceLocation("icon/caret-down.png");
   private static final ResourceLocation COMPASS = new ResourceLocation("compass.png");
   private static final Map directionMap = new HashMap();
   private static final StringSetting DISPLAY_TYPE = new StringSetting("Display Type", new String[]{"Zonix", "Zonix", "Classic", "Classic - Dark"});
   private static final BooleanSetting DRAW_BACKGROUND = new BooleanSetting("Draw Background");
   private static final ColorSetting FOREGROUND = new ColorSetting("Foreground", -1);
   private static final ColorSetting BACKGROUND = new ColorSetting("Background", 1862270976);

   public DirectionHUD() {
      super("Direction HUD");
      this.addSetting(new LabelSetting("General Settings"));
      this.addSetting(DRAW_BACKGROUND);
      this.addSetting(DISPLAY_TYPE);
      this.addSetting(new LabelSetting("Color Settings"));
      this.addSetting(FOREGROUND);
      this.addSetting(BACKGROUND);
   }

   public void renderReal() {
      switch(DISPLAY_TYPE.getIndex()) {
      case 0:
         this.renderZonix();
         break;
      case 1:
         this.renderClassic(false);
         break;
      case 2:
         this.renderClassic(true);
      }

   }

   private void renderZonix() {
      int hex = FOREGROUND.getValue().intValue();
      Color color = new Color(FOREGROUND.getValue().intValue());
      int opaque = (new Color(color.getRed(), color.getGreen(), color.getBlue(), 169)).getRGB();

      float direction;
      for(direction = this.mc.thePlayer.rotationYaw; direction > 360.0F; direction -= 360.0F) {
         ;
      }

      while(direction < 0.0F) {
         direction += 360.0F;
      }

      GL11.glPushMatrix();
      float y = this.y + 17.5F;
      GL11.glPushMatrix();
      RenderUtil.startScissorBox(this.y, y + 25.0F, this.x, this.x + 300.0F);
      if (DRAW_BACKGROUND.getValue().booleanValue()) {
         RenderUtil.drawRect(this.x, this.y, this.x + 300.0F, this.y + 30.0F, BACKGROUND.getValue().intValue());
      }

      GL11.glTranslatef(-direction * 2.0F + 150.0F, 0.0F, 0.0F);
      float steps = 15.0F;
      List directions = new LinkedList();

      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 360; j = (int)((float)j + steps)) {
            directions.add(j);
         }
      }

      float x = this.x - (float)(directions.size() / 2) * steps;

      for(Iterator var14 = directions.iterator(); var14.hasNext(); x += steps * 2.0F) {
         Integer j = (Integer)var14.next();
         String mapped = (String)directionMap.get(j);
         if (mapped != null) {
            ZFontRenderer fontRenderer = Client.getInstance().getLargeBoldFontRenderer();
            if (mapped.length() == 2) {
               fontRenderer = Client.getInstance().getRegularFontRenderer();
            }

            RenderUtil.drawCenteredString(fontRenderer, mapped, x, y + 5.0F, hex, false);
         } else {
            RenderUtil.drawRect(x, y + 1.5F, x + 1.0F, y + 5.0F, opaque);
            RenderUtil.drawCenteredString(Client.getInstance().getTinyFontRenderer(), String.valueOf(j), x, y + 10.0F, hex, false);
         }
      }

      RenderUtil.endScissorBox();
      GL11.glPopMatrix();
      steps = 7.5F;
      GL11.glPushMatrix();
      color = new Color(hex);
      GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
      RenderUtil.drawSquareTexture(CARET_DOWN, steps, steps + 5.0F, this.x + 150.0F - steps / 2.0F, this.y + 5.0F);
      RenderUtil.drawCenteredString(Client.getInstance().getSmallFontRenderer(), String.valueOf((int)direction), this.x + 150.0F, this.y + (float)Client.getInstance().getSmallFontRenderer().getHeight(), hex, false);
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      this.setHeight(35);
      this.setWidth(300);
   }

   private void renderClassic(boolean dark) {
      int direction = MathHelper.floor_double((double)(this.mc.thePlayer.rotationYaw * 256.0F / 360.0F) + 0.5D) & 255;
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderUtil.bindTexture(COMPASS);
      if (direction < 128) {
         HUDUtils.drawTexturedModalRect((int)this.getX(), (int)this.getY(), direction, dark ? 0 : 24, 65, 12, -100.0F);
      } else {
         HUDUtils.drawTexturedModalRect((int)this.getX(), (int)this.getY(), direction - 128, 12 + (dark ? 0 : 24), 65, 12, -100.0F);
      }

      RenderUtil.drawString(EnumChatFormatting.RED + "|", this.getX() + 32.0F, this.getY() + 1.0F, 16777215);
      RenderUtil.drawString(EnumChatFormatting.RED + "|" + EnumChatFormatting.RESET, this.getX() + 32.0F, this.getY() + 5.0F, 16777215);
      GL11.glPopMatrix();
      this.setHeight(12);
      this.setWidth(65);
   }

   static {
      directionMap.put(Integer.valueOf(0), "S");
      directionMap.put(Integer.valueOf(45), "SW");
      directionMap.put(Integer.valueOf(90), "W");
      directionMap.put(Integer.valueOf(135), "NW");
      directionMap.put(Integer.valueOf(180), "N");
      directionMap.put(Integer.valueOf(225), "NE");
      directionMap.put(Integer.valueOf(270), "E");
      directionMap.put(Integer.valueOf(315), "SE");
   }
}
