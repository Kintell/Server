package com.kokakiwi.kintell.server.plugins;

import com.kokakiwi.kintell.server.KintellServer;
import com.kokakiwi.kintell.server.core.KintellServerCore;
import com.kokakiwi.kintell.spec.plugin.Plugin;

public abstract class ServerPlugin extends Plugin
{
    private KintellServer server;
    
    public KintellServer getServer()
    {
        return server;
    }
    
    public KintellServerCore getCore()
    {
        return server.getCore();
    }
    
    public void setServer(KintellServer server)
    {
        this.server = server;
    }
}
