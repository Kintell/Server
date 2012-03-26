package com.kokakiwi.kintell.server.plugins;

import java.lang.reflect.Constructor;

import com.kokakiwi.kintell.server.KintellServer;
import com.kokakiwi.kintell.spec.plugin.PluginDescriptionFile;
import com.kokakiwi.kintell.spec.plugin.PluginLoader;
import com.kokakiwi.kintell.spec.plugin.PluginsManager;

public class ServerPluginsManager extends PluginsManager<ServerPlugin>
{
    private final KintellServer main;
    
    public ServerPluginsManager(KintellServer main)
    {
        this.main = main;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ServerPlugin createPlugin(PluginDescriptionFile pdf,
            PluginLoader loader)
    {
        ServerPlugin plugin = null;
        
        try
        {
            final String pluginMainClassName = pdf.getMain();
            final Class<? extends ServerPlugin> pluginClass = (Class<? extends ServerPlugin>) loader
                    .loadClass(pluginMainClassName);
            final Constructor<? extends ServerPlugin> constructor = pluginClass
                    .getConstructor();
            plugin = constructor.newInstance();
            plugin.setServer(main);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        
        return plugin;
    }
    
}
