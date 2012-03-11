package com.kokakiwi.kintell.server;

import java.io.File;

import com.kokakiwi.kintell.server.core.KintellServerCore;
import com.kokakiwi.kintell.server.net.Server;
import com.kokakiwi.kintell.server.plugins.ServerPluginsManager;
import com.kokakiwi.kintell.spec.console.ConsoleOutputManager;
import com.kokakiwi.kintell.spec.utils.Configuration;

public class KintellServer
{
    private final Configuration        configuration = new Configuration();
    
    private final ServerPluginsManager pluginsManager;
    private final Server               server;
    private final KintellServerCore    core;
    
    public KintellServer()
    {
        ConsoleOutputManager.register("server");
        
        // Loading configuration
        System.out.println("Loading configuration...");
        try
        {
            configuration.load(
                    KintellServer.class.getResourceAsStream("/config.yml"),
                    "yaml");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        // Initialize components
        System.out.println("Initializing components...");
        
        pluginsManager = new ServerPluginsManager(this);
        pluginsManager.setPluginsDir(new File(configuration
                .getString("plugins.path")));
        
        server = new Server(this);
        
        core = new KintellServerCore(this);
    }
    
    public void start()
    {
        System.out.println("Starting...");
        
        try
        {
            System.out.println("Loading plugins...");
            pluginsManager.loadPlugins();
            System.out.println("Enabling plugins...");
            pluginsManager.enablePlugins();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        System.out.println("Initialize core...");
        core.init();
        System.out.println("Starting server...");
        server.start();
    }
    
    public void stop()
    {
        System.out.println("Stopping...");
        
        pluginsManager.disablePlugins();
        
        server.stop();
        
        System.exit(0);
    }
    
    public Configuration getConfiguration()
    {
        return configuration;
    }
    
    public ServerPluginsManager getPluginsManager()
    {
        return pluginsManager;
    }
    
    public Server getServer()
    {
        return server;
    }
    
    public KintellServerCore getCore()
    {
        return core;
    }
    
    public static void main(String[] args)
    {
        KintellServer main = new KintellServer();
        main.start();
    }
}
