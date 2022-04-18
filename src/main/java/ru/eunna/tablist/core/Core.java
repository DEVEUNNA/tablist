

package ru.eunna.tablist.core;

import net.minecraftforge.common.config.*;
import ru.eunna.tablist.events.*;
import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.common.network.*;
import net.minecraft.client.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.event.*;
import java.io.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.*;

@Mod(modid = "tablistmod", version = "1.0.0", name = "TabList Fix")
public class Core
{
    Configuration config;
    public static String url;
    public static String admins_prefixes;
    public static boolean use_scoreboards;
    public static String copyright;
    public static boolean personal_calc;
    public static String server_name;
    
    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void postClientInit(final FMLPostInitializationEvent e) {
        this.register(new GuiEvents());
        this.register(this);
    }
    
    @SubscribeEvent
    public void onConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (Minecraft.getMinecraft().func_147104_D() != null && Minecraft.getMinecraft().func_147104_D().serverName != "") {
            Core.server_name = Minecraft.getMinecraft().func_147104_D().serverName;
        }
    }
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent e) {
        (this.config = new Configuration(new File("config/tabmenu.cfg"))).load();
        Core.url = this.config.get("General", "SkinsPath", "http://skins.mcskill.ru/MinecraftSkins/").getString();
        Core.admins_prefixes = this.config.get("General", "AdminsPrefixes", "&4&8;&4&2;&4&a;&4&6;&4&f;").getString();
        Core.use_scoreboards = this.config.get("General", "UseScoreboards", true).getBoolean();
        Core.copyright = this.config.get("General", "Copyright", "&eMinecraft 1.7.10 &8| &6McSkill.ru &8(C)").getString().replace("&", "�");
        Core.personal_calc = this.config.get("General", "Calc Personal", true).getBoolean();
        this.config.save();
    }
    
    private void register(final Object... objects) {
        for (final Object o : objects) {
            MinecraftForge.EVENT_BUS.register(o);
            FMLCommonHandler.instance().bus().register(o);
        }
    }
    
    static {
        Core.url = "http://skins.mcskill.ru/MinecraftSkins/";
        Core.admins_prefixes = "&8;";
        Core.use_scoreboards = true;
        Core.copyright = "2019 (\u0421)";
        Core.personal_calc = true;
        Core.server_name = "Minecraft Server";
    }
}
