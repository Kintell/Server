package com.kokakiwi.kintell.server.core.board;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;
import com.kokakiwi.kintell.server.core.KintellServerCore;
import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.exec.Program;
import com.kokakiwi.kintell.server.core.exec.ProgramExecutor;
import com.kokakiwi.kintell.spec.net.msg.BoardMessage;
import com.kokakiwi.kintell.spec.utils.data.Encodable;

public abstract class Board implements Runnable
{
    public final static long                     BOARD_TIMEOUT      = 60 * 30 * 1000L;
    
    protected final KintellServerCore            core;
    protected int                                id                 = 0;
    
    private final Map<String, RegisteredProgram> registeredPrograms = Maps.newLinkedHashMap();
    private final List<User>                     viewers            = new LinkedList<User>();
    
    private long                                 timeout            = 30000L;
    private long                                 started            = 0;
    private long                                 sleep              = 10L;
    
    private boolean                              running            = false;
    private Iterator<RegisteredProgram>          iterator           = null;
    private RegisteredProgram                    next;
    
    public Board(KintellServerCore core)
    {
        this.core = core;
    }
    
    public final Map<String, RegisteredProgram> getRegisteredPrograms()
    {
        return registeredPrograms;
    }
    
    public final RegisteredProgram registerProgram(Program program)
    {
        RegisteredProgram registeredProgram = new RegisteredProgram(
                generateId(program), program);
        registeredPrograms.put(registeredProgram.getId(), registeredProgram);
        
        return registeredProgram;
    }
    
    private final String generateId(Program program)
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append(program.getId());
        sb.append('-');
        
        Random random = new Random();
        int programId = Math.abs(random.nextInt());
        
        while (registeredPrograms.containsKey(sb.toString()
                + String.valueOf(programId)))
        {
            programId = Math.abs(random.nextInt());
        }
        
        sb.append(programId);
        
        return sb.toString();
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
    
    public long getSleep()
    {
        return sleep;
    }
    
    public void setSleep(long sleep)
    {
        this.sleep = sleep;
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
        for (RegisteredProgram program : registeredPrograms.values())
        {
            ProgramExecutor executor = program.getExecutor();
            if (executor != null)
            {
                executor.reset();
                configureProgram(program);
                program.configureExecutors(this);
            }
        }
    }
    
    public final void run()
    {
        running = true;
        started = System.currentTimeMillis();
        
        configureExecutors();
        init();
        
        while (running)
        {
            iterator = null;
            tick();
            checkTimeout();
            try
            {
                Thread.sleep(sleep);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
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
            iterator = registeredPrograms.values().iterator();
        }
        
        boolean hasNext = iterator.hasNext();
        
        if (hasNext)
        {
            next = iterator.next();
        }
        
        return hasNext;
    }
    
    protected final RegisteredProgram next()
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
                        + next.getProgram().getOwner().getOwner().getId() + "."
                        + next.getProgram().getOwner().getId() + "."
                        + next.getId() + "'");
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
        msg.getData().copyWritedBytesToReadableBytes();
        
        for (User user : viewers)
        {
            if (user.getChannel() != null && user.getChannel().isWritable())
            {
                user.getChannel().write(msg);
            }
        }
    }
    
    private final void stop()
    {
        core.getBoards().remove(id);
    }
    
    public abstract void configureProgram(RegisteredProgram program);
    
    public abstract void init();
    
    public abstract void tick();
    
    private final static class BoardProgramExecutor implements Runnable
    {
        private final RegisteredProgram program;
        private final boolean           init;
        
        public BoardProgramExecutor(RegisteredProgram program, boolean init)
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
    
    public final static class RegisteredProgram
    {
        private final String          id;
        private final Program         program;
        private final ProgramExecutor executor;
        private Debugger              debugger;
        
        private RegisteredProgram(String id, Program program)
        {
            this.id = id;
            this.program = program;
            this.executor = program.getExecutorFactory()
                    .createExecutor(program);
        }
        
        public String getId()
        {
            return id;
        }
        
        public Program getProgram()
        {
            return program;
        }
        
        public ProgramExecutor getExecutor()
        {
            return executor;
        }
        
        public Debugger getDebugger()
        {
            return debugger;
        }
        
        public void configureExecutors(Board board)
        {
            debugger = new Debugger(board, this);
            executor.set("debug", debugger);
        }
    }
}
