package us.zonix.client.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import us.zonix.client.module.impl.ArmorStatus;
import us.zonix.client.module.impl.CPS;
import us.zonix.client.module.impl.ComboDisplay;
import us.zonix.client.module.impl.Coordinates;
import us.zonix.client.module.impl.DirectionHUD;
import us.zonix.client.module.impl.FPS;
import us.zonix.client.module.impl.FPSBoost;
import us.zonix.client.module.impl.Keystrokes;
import us.zonix.client.module.impl.PotionCounter;
import us.zonix.client.module.impl.PotionEffects;
import us.zonix.client.module.impl.ReachDisplay;
import us.zonix.client.module.impl.Scoreboard;
import us.zonix.client.module.impl.TimeChanger;
import us.zonix.client.module.impl.ToggleSneak;
import us.zonix.client.module.impl.ZansMinimap;

public final class ModuleManager {
   private final Map moduleClassMap = new HashMap();
   private final Map moduleNameMap = new HashMap();

   public ModuleManager() {
      this.register(new ArmorStatus());
      this.register(new ComboDisplay());
      this.register(new Coordinates());
      this.register(new CPS());
      this.register(new DirectionHUD());
      this.register(new FPS());
      this.register(new Keystrokes());
      this.register(new FPSBoost());
      this.register(new PotionCounter());
      this.register(new PotionEffects());
      this.register(new ReachDisplay());
      this.register(new Scoreboard());
      this.register(new TimeChanger());
      this.register(new ToggleSneak());
      this.register(new ZansMinimap());
   }

   public List getEnabledModules() {
      List modules = new ArrayList();
      Iterator var2 = this.moduleNameMap.values().iterator();

      while(var2.hasNext()) {
         IModule module = (IModule)var2.next();
         if (module.isEnabled()) {
            modules.add(module);
         }
      }

      return modules;
   }

   public Collection getModules() {
      return this.moduleNameMap.values();
   }

   public IModule getModule(Class clazz) {
      return (IModule)this.moduleClassMap.get(clazz);
   }

   public IModule getModule(String name) {
      return (IModule)this.moduleNameMap.get(name.toLowerCase());
   }

   private void register(IModule module) {
      this.moduleNameMap.put(module.getName().toLowerCase(), module);
      this.moduleClassMap.put(module.getClass(), module);
   }
}
