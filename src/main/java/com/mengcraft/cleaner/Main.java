package com.mengcraft.cleaner;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        getCommand("cleaner").setExecutor(new Executor(this));
        
        String[] strings = {
                ChatColor.GREEN + "梦梦家高性能服务器出租店",
                ChatColor.GREEN + "shop105595113.taobao.com"
        };
        
        getServer().getConsoleSender().sendMessage(strings);
    }

    public List<String> whiteList() {
        return getConfig().getStringList("default.ignore-list");
    }
    
}
