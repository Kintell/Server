package com.kokakiwi.kintell.server.core;

import java.io.File;
import java.security.MessageDigest;
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
            final ChannelFuture future = channel.write(msg);
            try
            {
                future.await(30000L);
            }
            catch (final InterruptedException e)
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
        
        final ExpressionList<UserEntry> query = m.getCore().getMain()
                .getDatabase().getEbean().find(UserEntry.class).where()
                .eq("name", id);
        
        if (query.findRowCount() > 0)
        {
            entry = query.findUnique();
        }
        
        return entry;
    }
    
    public static String hashPassword(String password) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        
        byte[] bytes = password.getBytes();
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        
        byte[] digested = digest.digest(bytes);
        String hex = getHexString(digested);
        sb.append(hex);
        
        return sb.toString();
    }
    
    public static String getHexString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
        {
            sb.append(String.format("%x", b));
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof User))
        {
            return false;
        }
        User other = (User) obj;
        if (id == null)
        {
            if (other.id != null)
            {
                return false;
            }
        }
        else if (!id.equals(other.id))
        {
            return false;
        }
        return true;
    }
}
