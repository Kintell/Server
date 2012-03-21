package com.kokakiwi.kintell.server.core.exec;

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;
import com.kokakiwi.kintell.server.core.User;

public class Machine
{
    private final Machines             parent;
    private final String               id;
    private User                       owner;
    
    private File                       root     = null;
    
    private final Map<String, Program> programs = Maps.newLinkedHashMap();
    
    public Machine(Machines parent, String id, User owner)
    {
        this.parent = parent;
        this.id = id;
        this.owner = owner;
        
        setRoot(new File(owner.getRoot(), id));
    }
    
    public Machines getParent()
    {
        return parent;
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
    
    public String getId()
    {
        return id;
    }
    
    public Map<String, Program> getPrograms()
    {
        return programs;
    }
    
    public Program getProgram(String id)
    {
        return programs.get(id);
    }
    
    public Program createProgram(String id,
            ProgramExecutorFactory<? extends ProgramExecutor> executorFactory)
    {
        return createProgram(id, id, executorFactory);
    }
    
    public Program createProgram(String id, String name,
            ProgramExecutorFactory<? extends ProgramExecutor> executorFactory)
    {
        Program program = createProgram(id, name);
        program.setExecutor(executorFactory);
        
        return program;
    }
    
    public Program createProgram(String id, String name)
    {
        Program program = null;
        
        if (!programs.containsKey(id))
        {
            program = new Program(this, id, name);
            
            programs.put(id, program);
        }
        
        return program;
    }
    
    public User getOwner()
    {
        return owner;
    }
    
    public void setOwner(User owner)
    {
        this.owner = owner;
    }
}
