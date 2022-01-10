package us.zonix.client;

import net.minecraft.util.ResourceLocation;
import us.zonix.client.cape.CapeManager;
import us.zonix.client.module.ModuleManager;
import us.zonix.client.profile.ProfileManager;
import us.zonix.client.social.PartyManager;
import us.zonix.client.social.friend.FriendManager;
import us.zonix.client.util.font.ZFontRenderer;

public final class Client {
   private static Client instance;
   private final ProfileManager profileManager;
   private final FriendManager friendManager;
   private final ModuleManager moduleManager;
   private final PartyManager partyManager;
   private final CapeManager capeManager;
   private final ZFontRenderer hugeBoldFontRenderer;
   private final ZFontRenderer hugeFontRenderer;
   private final ZFontRenderer largeBoldFontRenderer;
   private final ZFontRenderer largeFontRenderer;
   private final ZFontRenderer mediumBoldFontRenderer;
   private final ZFontRenderer mediumFontRenderer;
   private final ZFontRenderer regularBoldFontRenderer;
   private final ZFontRenderer regularFontRenderer;
   private final ZFontRenderer regularMediumBoldFontRenderer;
   private final ZFontRenderer regularMediumFontRenderer;
   private final ZFontRenderer smallFontRenderer;
   private final ZFontRenderer smallBoldFontRenderer;
   private final ZFontRenderer tinyFontRenderer;
   private final ZFontRenderer tinyBoldFontRenderer;

   public Client() {
      instance = this;
      this.profileManager = new ProfileManager();
      this.friendManager = new FriendManager();
      this.moduleManager = new ModuleManager();
      this.partyManager = new PartyManager();
      this.capeManager = new CapeManager();
      this.profileManager.load();
      Runtime var10000 = Runtime.getRuntime();
      ProfileManager var10003 = this.profileManager;
      this.profileManager.getClass();
      var10000.addShutdownHook(new Thread(var10003::saveConfig));
      this.hugeBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Bold.ttf"), 40.0F);
      this.hugeFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Regular.ttf"), 40.0F);
      this.largeBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Bold.ttf"), 36.0F);
      this.largeFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Regular.ttf"), 36.0F);
      this.mediumBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Bold.ttf"), 28.0F);
      this.mediumFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Regular.ttf"), 28.0F);
      this.regularMediumBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Bold.ttf"), 24.0F);
      this.regularMediumFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Regular.ttf"), 24.0F);
      this.regularBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Bold.ttf"), 20.0F);
      this.regularFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Regular.ttf"), 20.0F);
      this.smallBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Bold.ttf"), 16.0F);
      this.smallFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Regular.ttf"), 16.0F);
      this.tinyBoldFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Bold.ttf"), 12.0F);
      this.tinyFontRenderer = new ZFontRenderer(new ResourceLocation("font/Roboto-Regular.ttf"), 12.0F);
   }

   public ProfileManager getProfileManager() {
      return this.profileManager;
   }

   public FriendManager getFriendManager() {
      return this.friendManager;
   }

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   public PartyManager getPartyManager() {
      return this.partyManager;
   }

   public CapeManager getCapeManager() {
      return this.capeManager;
   }

   public ZFontRenderer getHugeBoldFontRenderer() {
      return this.hugeBoldFontRenderer;
   }

   public ZFontRenderer getHugeFontRenderer() {
      return this.hugeFontRenderer;
   }

   public ZFontRenderer getLargeBoldFontRenderer() {
      return this.largeBoldFontRenderer;
   }

   public ZFontRenderer getLargeFontRenderer() {
      return this.largeFontRenderer;
   }

   public ZFontRenderer getMediumBoldFontRenderer() {
      return this.mediumBoldFontRenderer;
   }

   public ZFontRenderer getMediumFontRenderer() {
      return this.mediumFontRenderer;
   }

   public ZFontRenderer getRegularBoldFontRenderer() {
      return this.regularBoldFontRenderer;
   }

   public ZFontRenderer getRegularFontRenderer() {
      return this.regularFontRenderer;
   }

   public ZFontRenderer getRegularMediumBoldFontRenderer() {
      return this.regularMediumBoldFontRenderer;
   }

   public ZFontRenderer getRegularMediumFontRenderer() {
      return this.regularMediumFontRenderer;
   }

   public ZFontRenderer getSmallFontRenderer() {
      return this.smallFontRenderer;
   }

   public ZFontRenderer getSmallBoldFontRenderer() {
      return this.smallBoldFontRenderer;
   }

   public ZFontRenderer getTinyFontRenderer() {
      return this.tinyFontRenderer;
   }

   public ZFontRenderer getTinyBoldFontRenderer() {
      return this.tinyBoldFontRenderer;
   }

   public static Client getInstance() {
      return instance;
   }
}
