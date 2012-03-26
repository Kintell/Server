package com.kokakiwi.kintell.server.core.exec;

import java.io.File;
import java.io.IOException;

import com.kokakiwi.kintell.spec.utils.Configuration;

public class Program
{
    public final static long                                  TIMEOUT = 3000L;
    
    private final Machine                                     owner;
    private final String                                      id;
    private String                                            name;
    
    private File                                              root;
    private ProgramExecutorFactory<? extends ProgramExecutor> executorFactory;
    
    public Program(Machine owner, String id)
    {
        this(owner, id, id);
    }
    
    public Program(Machine owner, String id, String name)
    {
        this.owner = owner;
        this.id = id;
        this.name = name;
        
        setRoot(new File(owner.getRoot(), id));
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Machine getOwner()
    {
        return owner;
    }
    
    public String getId()
    {
        return id;
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
    
    public ProgramExecutorFactory<? extends ProgramExecutor> getExecutorFactory()
    {
        return executorFactory;
    }
    
    public void setExecutor(
            ProgramExecutorFactory<? extends ProgramExecutor> executorFactory)
    {
        this.executorFactory = executorFactory;
        try
        {
            save();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void save() throws IOException
    {
        final Configuration conf = new Configuration();
        conf.set("name", name);
        conf.set("contentType", executorFactory.getContentType());
        conf.save(new File(root, "def.yml"));
    }
    
    public void load()
    {
        final Configuration conf = new Configuration();
        conf.load(new File(root, "def.yml"));
        
        final String contentType = conf.getString("contentType");
        for (final ProgramExecutorFactory<? extends ProgramExecutor> executorFactory : owner
                .getParent().getCore().getExecutorFactories().values())
        {
            if (executorFactory.getContentType().equalsIgnoreCase(contentType))
            {
                final Thread thread = new Thread(new Runnable() {
                    public void run()
                    {
                        Program.this.executorFactory = executorFactory;
                    }
                });
                thread.start();
                try
                {
                    thread.join(TIMEOUT);
                    if (thread.isAlive())
                    {
                        System.out.println("[TIMEOUT (" + TIMEOUT
                                + "ms)] Creating executor for program '"
                                + Program.this + "'");
                        getOwner().getPrograms().remove(id);
                    }
                }
                catch (final InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        name = conf.getString("name");
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
        if (!(obj instanceof Program))
        {
            return false;
        }
        Program other = (Program) obj;
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
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        if (owner == null)
        {
            if (other.owner != null)
            {
                return false;
            }
        }
        else if (!owner.equals(other.owner))
        {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Program [owner=");
        builder.append(owner);
        builder.append(", id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", root=");
        builder.append(root);
        builder.append(", executorFactory=");
        builder.append(executorFactory);
        builder.append("]");
        return builder.toString();
    }
}
