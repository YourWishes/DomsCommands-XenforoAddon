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

package com.domsplace.DomsCommandsXenforoAddon.DataManagers;

import com.domsplace.DomsCommands.Bases.DataManager;
import com.domsplace.DomsCommands.Enums.ManagerType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Dominic Masters
 */
public class XenforoManager extends DataManager {
    public static final ManagerType XENFORO = new ManagerType("Xenforo");
    public static final XenforoManager XENFORO_MANAGER = new XenforoManager();
    
    public YamlConfiguration yml;
    
    public XenforoManager() {
        super(XenforoManager.XENFORO);  
    }
    
    @Override
    public void tryLoad() throws IOException {
        File f = new File(getDataFolder(), "xenforo.yml");
        if(!f.exists()) f.createNewFile();
        
        this.yml = YamlConfiguration.loadConfiguration(f);
        
        df("xenforo.root", "http://yoursite.com/forums/");
        
        List<String> commands = new ArrayList<String>();
        commands.add("say {NAME} has registered on the forums!");
        commands.add("say Thanks {DISPLAYNAME}!");
        df("commands.onRegistered", commands);
        
        commands = new ArrayList<String>();
        commands.add("say {NAME} has unregistered.");
        df("commands.onDeRegistered", commands);
        
        this.trySave();
    }
    
    @Override
    public void trySave() throws IOException {
        File f = new File(getDataFolder(), "xenforo.yml");
        if(!f.exists()) f.createNewFile();
        this.yml.save(f);
    }
    
    public void df(String key, Object def) {
        if(yml.contains(key)) return;
        yml.set(key, def);
    }
}
