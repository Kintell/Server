package com.kokakiwi.kintell.server.core.board;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.kokakiwi.kintell.server.core.KintellServerCore;
import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.exec.Program;
import com.kokakiwi.kintell.server.core.exec.ProgramExecutor;
import com.kokakiwi.kintell.spec.net.msg.BoardMessage;
import com.kokakiwi.kintell.spec.utils.data.Encodable;

public abstract class Board implements Runnable
{
    public final static long          BOARD_TIMEOUT      = 60 * 30L;
    
    protected final KintellServerCore core;
    protected int                     id                 = 0;
    
    private final List<Program>       registeredPrograms = new LinkedList<Program>();
    private final List<User>          viewers            = new LinkedList<User>();
    
    private long                      timeout            = 30000L;
    private long                      started            = 0;
    
    private boolean                   running            = false;
    private Iterator<Program>         iterator           = null;
    private Program                   next;
    
    public Board(KintellServerCore core)
    {
        this.core = core;
    }
    
    public final List<Program> getRegisteredPrograms()
    {
        return registeredPrograms;
    }
    
    public final void registerProgram(Program program)
    {
        registeredPrograms.add(program);
    }
    
    public final boolean isRunning()
    {
        return running;
    }
    
    public final void setRunning(boolean running)
    {
        this.running = running;
    }
    
    public final KintellServerCore getCore()
    {
        return core;
    }
    
    public final List<User> getViewers()
    {
        return viewers;
    }
    
    public final long getTimeout()
    {
        return timeout;
    }
    
    public final void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }
    
    public final int getId()
    {
        return id;
    }
    
    public final void setId(int id)
    {
        this.id = id;
    }
    
    public final void configureExecutors()
    {
        for (Program program : registeredPrograms)
        {
            ProgramExecutor executor = program.getExecutor();
            executor.reset();
            if (executor != null)
            {
                configureExecutor(executor);
            }
        }
    }
    
    public final void run()
    {
        running = true;
        started = System.currentTimeMillis();
        
        init();
        
        while (running)
        {
            iterator = null;
            tick();
            checkTimeout();
        }
        
        stop();
    }
    
    private final void checkTimeout()
    {
        long diff = System.currentTimeMillis() - started;
        if (diff > BOARD_TIMEOUT)
        {
            System.out.println("[TIMEOUT] Board '" + id + "'");
            running = false;
        }
    }
    
    protected final boolean hasNext()
    {
        if (iterator == null)
        {
            iterator = registeredPrograms.iterator();
        }
        
        boolean hasNext = iterator.hasNext();
        
        if (hasNext)
        {
            next = iterator.next();
        }
        
        return hasNext;
    }
    
    protected final Program next()
    {
        return next;
    }
    
    protected final void initProgram()
    {
        runProgram(true);
    }
    
    protected final void tickProgram()
    {
        runProgram(false);
    }
    
    @SuppressWarnings("deprecation")
    private final void runProgram(boolean init)
    {
        BoardProgramExecutor programExecutor = new BoardProgramExecutor(next,
                init);
        
        Thread thread = new Thread(programExecutor);
        thread.start();
        
        try
        {
            thread.join(timeout);
            if (thread.isAlive())
            {
                System.out.println("[TIMEOUT] Program '"
                        + next.getOwner().getOwner().getId() + "."
                        + next.getOwner().getId() + "." + next.getId() + "'");
                thread.stop();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    protected final void sendData(String opcode, Encodable data)
    {
        BoardMessage msg = new BoardMessage();
        msg.setId(id);
        msg.setOpcode(opcode);
        data.encode(msg.getData());
        
        for (User user : viewers)
        {
            user.getChannel().write(msg);
        }
    }
    
    private final void stop()
    {
        core.getBoards().remove(id);
    }
    
    public abstract void configureExecutor(ProgramExecutor executor);
    
    public abstract void init();
    
    public abstract void tick();
    
    private final static class BoardProgramExecutor implements Runnable
    {
        private final Program program;
        private final boolean init;
        
        public BoardProgramExecutor(Program program, boolean init)
        {
            this.program = program;
            this.init = init;
        }
        
        public void run()
        {
            if (init)
            {
                program.getExecutor().init();
            }
            else
            {
                program.getExecutor().tick();
            }
        }
    }
}
