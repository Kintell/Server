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
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void save() throws IOException
    {
        Configuration conf = new Configuration();
        conf.set("name", name);
        conf.set("contentType", executorFactory.getContentType());
        conf.save(new File(root, "def.yml"));
    }
    
    public void load()
    {
        Configuration conf = new Configuration();
        conf.load(new File(root, "def.yml"));
        
        String contentType = conf.getString("contentType");
        for (final ProgramExecutorFactory<? extends ProgramExecutor> executorFactory : owner
                .getParent().getCore().getExecutorFactories().values())
        {
            if (executorFactory.getContentType().equalsIgnoreCase(contentType))
            {
                Thread thread = new Thread(new Runnable() {
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
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        name = conf.getString("name");
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
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
