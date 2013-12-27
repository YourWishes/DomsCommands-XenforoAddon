/*
 * Copyright 2013 Dominic Masters.
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

package com.domsplace.DomsCommandsXenforoAddon.Threads;

import com.domsplace.DomsCommands.Bases.DomsThread;
import com.domsplace.DomsCommands.Objects.DomsPlayer;
import com.domsplace.DomsCommandsXenforoAddon.DataManagers.XenforoManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Dominic Masters
 */
public class XenforoUpdateThread extends DomsThread {
    public static final long TIMEOUT = 10000;
    public static final String APPEND = "index.php?register/validate-field";
    private final String USER_AGENT = "Mozilla/5.0";
    
    private DomsPlayer player;
    
    public XenforoUpdateThread(DomsPlayer who) {
        super(0, Long.MAX_VALUE, true);
        this.player = who;
    }
    
    private String encode(String x) {
        try {
            return URLEncoder.encode(x, "ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
            return x;
        }
    }
    
    @Override
    public void run() {
        debug("THREAD STARTED");
        long start = getNow();
        while(start+TIMEOUT <= getNow() && player == null) {}
        this.stopThread();
        this.deregister();
        if(player == null) return;
        
        boolean alreadyActivated = false;
        boolean isActivated = false;
        
        try {
            String x = player.getSavedVariable("xenforo");
            alreadyActivated = x.equalsIgnoreCase("yes");
        } catch(Exception e) {}
        
        try {
            String url = XenforoManager.XENFORO_MANAGER.yml.getString("xenforo.root", "") + APPEND;
            if(url.equals(APPEND)) return;
            
            String encodedData = "name=" + encode("username") + "&value=" + encode(this.player.getPlayer()) + "&_xfResponseType=json";
            //String rawData = "name=username";
            String type = "application/x-www-form-urlencoded; charset=UTF-8";
            //String encodedData = URLEncoder.encode(rawData, "ISO-8859-1"); 
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", type );
            conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Referer", XenforoManager.XENFORO_MANAGER.yml.getString("xenforo.root", ""));
            conn.setUseCaches(false);
            
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream ());
            wr.write(encodedData);
            wr.flush();
      
            InputStream in = conn.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8")); 
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) responseStrBuilder.append(inputStr);
            
            String out = responseStrBuilder.toString();
            JSONObject o = (JSONObject) JSONValue.parse(out);
            try {
                isActivated = o.get("_redirectMessage").toString().equalsIgnoreCase("ok");
            } catch(NullPointerException e) {
                isActivated = true;
            }
            debug("GOT: " + isActivated);
        } catch(Exception e) {
            if(DebugMode) e.printStackTrace();
            return;
        }
        
        player.setSavedVariable("xenforo", (isActivated ? "yes" : "no"));
        player.save();
        
        if(isActivated && !alreadyActivated) {
            runCommands(XenforoManager.XENFORO_MANAGER.yml.getStringList("commands.onRegistered"));
        }
        
        if(!isActivated && alreadyActivated) {
            runCommands(XenforoManager.XENFORO_MANAGER.yml.getStringList("commands.onDeRegistered"));
        }
    }
    
    public void runCommands(List<String> commands) {
        for(String s : commands) {
            s = s.replaceAll("\\{NAME\\}", this.player.getPlayer());
            s = s.replaceAll("\\{DISPLAYNAME\\}", this.player.getDisplayName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
        }
    }
}
