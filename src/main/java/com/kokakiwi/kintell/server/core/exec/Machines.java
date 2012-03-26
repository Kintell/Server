package com.kokakiwi.kintell.server.core.exec;

import java.io.File;
import java.io.FileFilter;

import com.kokakiwi.kintell.server.core.KintellServerCore;
import com.kokakiwi.kintell.server.core.User;

public class Machines
{
    private final KintellServerCore core;
    
    private File                    root = null;
    
    public Machines(KintellServerCore core)
    {
        this.core = core;
        
        setRoot(new File(core.getMain().getConfiguration()
                .getString("core.machines.path")));
    }
    
    public void init()
    {
        final File[] usersFolders = root.listFiles(new FileFilter() {
            
            public boolean accept(File pathname)
            {
                return pathname.isDirectory();
            }
        });
        for (final File userFolder : usersFolders)
        {
            final User user = core.createUser(userFolder.getName(), null);
            
            final File[] machinesFolders = userFolder
                    .listFiles(new FileFilter() {
                        
                        public boolean accept(File pathname)
                        {
                            return pathname.isDirectory();
                        }
                    });
            for (final File machineFolder : machinesFolders)
            {
                final Machine machine = createMachine(machineFolder.getName(),
                        user);
                user.addMachine(machine);
                
                final File[] programsFolders = machineFolder
                        .listFiles(new FileFilter() {
                            
                            public boolean accept(File pathname)
                            {
                                return pathname.isDirectory();
                            }
                        });
                for (final File programFolder : programsFolders)
                {
                    final Program program = machine.createProgram(
                            programFolder.getName(), "");
                    program.load();
                }
            }
        }
    }
    
    public KintellServerCore getCore()
    {
        return core;
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
    
    public Machine createMachine(String id, User owner)
    {
        final Machine machine = new Machine(this, id, owner);
        
        return machine;
    }
    
    public Program getProgram(User user, String machineId, String id)
    {
        Program program = null;
        
        final Machine machine = user.getMachine(machineId);
        if (machine != null)
        {
            program = machine.getProgram(id);
        }
        
        return program;
    }
}
