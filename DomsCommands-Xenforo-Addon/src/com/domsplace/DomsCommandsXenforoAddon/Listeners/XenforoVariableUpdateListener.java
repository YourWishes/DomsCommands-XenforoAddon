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

package com.domsplace.DomsCommandsXenforoAddon.Listeners;

import com.domsplace.DomsCommands.Bases.DomsListener;
import com.domsplace.DomsCommands.Events.DomsPlayerUpdateSavedVariablesEvent;
import com.domsplace.DomsCommands.Objects.DomsPlayer;
import com.domsplace.DomsCommandsXenforoAddon.Threads.XenforoUpdateThread;
import org.bukkit.event.EventHandler;

/**
 *
 * @author Dominic Masters
 */
public class XenforoVariableUpdateListener extends DomsListener {
    @EventHandler
    public void updateHandler(DomsPlayerUpdateSavedVariablesEvent e) {
        DomsPlayer player = e.getPlayer();
        if(player == null) return;
        
        //Lookup
        new XenforoUpdateThread(player);
    }
}
