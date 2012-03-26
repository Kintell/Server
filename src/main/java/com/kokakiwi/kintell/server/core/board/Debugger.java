package com.kokakiwi.kintell.server.core.board;

import java.util.Arrays;
import java.util.List;

import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.board.Board.RegisteredProgram;
import com.kokakiwi.kintell.server.core.exec.annotations.NonAccessible;
import com.kokakiwi.kintell.spec.net.msg.DebugMessage;

public class Debugger
{
    private final Board                   board;
    private final Board.RegisteredProgram program;
    
    public Debugger(Board board, RegisteredProgram program)
    {
        this.board = board;
        this.program = program;
    }
    
    public void log(String message)
    {
        log(Arrays.asList(message));
    }
    
    public void log(List<String> messages)
    {
        final DebugMessage msg = new DebugMessage();
        
        for (final String message : messages)
        {
            final StringBuilder sb = new StringBuilder();
            sb.append("[Program '");
            sb.append(program.getId());
            sb.append("'] ");
            sb.append(message);
            
            msg.getMessages().add(sb.toString());
            
            System.out.println(sb.toString());
        }
        
        for (final User user : board.getViewers())
        {
            if (user.equals(program.getProgram().getOwner().getOwner()))
            {
                user.sendMessage(msg);
            }
        }
    }
    
    @NonAccessible
    public Board getBoard()
    {
        return board;
    }
    
    @NonAccessible
    public Board.RegisteredProgram getProgram()
    {
        return program;
    }
}
