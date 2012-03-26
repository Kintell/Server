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
        final Program program = createProgram(id, name);
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
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if (!(obj instanceof Machine))
        {
            return false;
        }
        Machine other = (Machine) obj;
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
}
