package com.mengcraft.cleaner;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    @Override
    public void onEnable() {
        getCommand("cleaner").setExecutor(new Executor(this));
        
        String[] strings = {
                ChatColor.GREEN + "梦梦家高性能服务器出租店",
                ChatColor.GREEN + "shop105595113.taobao.com"
        };
        
        getServer().getConsoleSender().sendMessage(strings);
    }
    
}
