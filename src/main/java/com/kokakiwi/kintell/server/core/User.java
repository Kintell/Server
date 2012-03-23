package com.kokakiwi.kintell.server.core;

import java.io.File;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import com.avaje.ebean.ExpressionList;
import com.google.common.collect.Maps;
import com.kokakiwi.kintell.server.core.exec.Machine;
import com.kokakiwi.kintell.server.core.exec.Machines;
import com.kokakiwi.kintell.server.database.UserEntry;
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
        if (channel.isWritable())
        {
            ChannelFuture future = channel.write(msg);
            try
            {
                future.await(30000L);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
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
    
    public UserEntry getEntry()
    {
        UserEntry entry = null;
        
        ExpressionList<UserEntry> query = m.getCore().getMain().getDatabase()
                .getEbean().find(UserEntry.class).where().eq("name", id);
        
        if (query.findRowCount() > 0)
        {
            entry = query.findUnique();
        }
        
        return entry;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((m == null) ? 0 : m.hashCode());
        result = prime * result
                + ((machines == null) ? 0 : machines.hashCode());
        result = prime * result + ((root == null) ? 0 : root.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }
}
