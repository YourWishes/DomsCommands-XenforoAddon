/*
 * Copyright 2013 Dominic.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domsplace.DomsCommandsXenforoAddon;

import com.domsplace.DomsCommands.Bases.*;
import com.domsplace.DomsCommands.DomsCommandsPlugin;
import com.domsplace.DomsCommands.Objects.DomsCommandsAddon;
import com.domsplace.DomsCommandsXenforoAddon.DataManagers.XenforoManager;
import com.domsplace.DomsCommandsXenforoAddon.Threads.XenforoCheckThread;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author      Dominic
 * @since       29/11/2013
 */
public class DomsCommandsXenforoAddonPlugin extends JavaPlugin {
    private boolean enabled = false;
    
    @Override
    public void onEnable() {
        //Try Hooking into DomsCommands
        if(getDomsCommands() == null) {
            Bukkit.getLogger().info("Failed to find DomsCommands! Xenforo Addon Disabled.");
            this.disable();
            return;
        }
        
        new DomsCommandsAddon(this) {
            @Override
            public void disable() {
                Bukkit.getPluginManager().disablePlugin(this.getPlugin());
            }
        };
        
        //Load Data
        if(!XenforoManager.XENFORO_MANAGER.load()) {
            this.disable();
            return;
        }
        
        new XenforoCheckThread();
        
        this.enabled = true;
        Base.log("Finished Loading " + this.getName() + ", " + BukkitCommand.getCommands().size() + " commands registered.");
    }
    
    @Override
    public void onDisable() {
        if(!enabled) {
            return;
        }
    }
    
    public void disable() {
        Bukkit.getPluginManager().disablePlugin(this);
    }
    
    public DomsCommandsPlugin getDomsCommands() {
        try {
            Plugin p = Bukkit.getPluginManager().getPlugin("DomsCommands");
            if(!p.isEnabled()) return null;
            DomsCommandsPlugin plugin = (DomsCommandsPlugin) p;
            if(Base.getDouble(DataManager.PLUGIN_MANAGER.getVersion()) < 1.10) {
                Base.log("The Version of DomsCommands you're running is too old.");
                return null;
            }
            return plugin;
        } catch(Exception e) {} catch(Error e) {}
        return null;
    }
}
