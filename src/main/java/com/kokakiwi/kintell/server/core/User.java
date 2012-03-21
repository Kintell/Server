package com.kokakiwi.kintell.server.core;

import java.io.File;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import com.google.common.collect.Maps;
import com.kokakiwi.kintell.server.core.exec.Machine;
import com.kokakiwi.kintell.server.core.exec.Machines;
import com.kokakiwi.kintell.spec.net.msg.Message;

public class User
{
    private final String               id;
    private Channel                    channel  = null;
    
    private final Map<String, Machine> machines = Maps.newLinkedHashMap();
    private final Machines             m;
    
    private File                       root;
    
    public User(Machines machines, String id)
    {
        this(machines, id, null);
    }
    
    public User(Machines machines, String id, Channel channel)
    {
        this.id = id;
        this.channel = channel;
        m = machines;
        
        setRoot(new File(machines.getRoot(), id.toLowerCase()));
    }
    
    public Channel getChannel()
    {
        return channel;
    }
    
    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }
    
    public void sendMessage(Message msg)
    {
        ChannelFuture future = channel.write(msg);
        try
        {
            future.await(30000L);
            if (future.isSuccess())
            {
                System.out.println("Message sent!");
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    public String getId()
    {
        return id;
    }
    
    public Map<String, Machine> getMachines()
    {
        return machines;
    }
    
    public Machine getMachine(String id)
    {
        return machines.get(id);
    }
    
    public void addMachine(Machine machine)
    {
        machines.put(machine.getId(), machine);
    }
    
    public File getRoot()
    {
        return root;
    }
    
    public void setRoot(File root)
    {
        this.root = root;
        if (!root.exists())
        {
            root.mkdirs();
        }
    }
    
    public Machines getM()
    {
        return m;
    }
}
