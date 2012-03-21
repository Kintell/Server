package com.kokakiwi.kintell.server.core.board;

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
        StringBuilder sb = new StringBuilder();
        sb.append("[Program '");
        sb.append(program.getId());
        sb.append("'] ");
        sb.append(message);
        
        DebugMessage msg = new DebugMessage();
        msg.setMessage(sb.toString());
        
        for (User user : board.getViewers())
        {
            if (user.getChannel() != null && user.getChannel().isWritable())
            {
                user.getChannel().write(msg);
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
