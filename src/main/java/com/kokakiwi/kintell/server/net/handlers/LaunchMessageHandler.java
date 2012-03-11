package com.kokakiwi.kintell.server.net.handlers;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.board.Board;
import com.kokakiwi.kintell.server.core.exec.Program;
import com.kokakiwi.kintell.server.net.Server;
import com.kokakiwi.kintell.spec.net.MessageHandler;
import com.kokakiwi.kintell.spec.net.msg.LaunchMessage;
import com.kokakiwi.kintell.spec.net.msg.ProgramsListMessage;

public class LaunchMessageHandler extends MessageHandler<LaunchMessage>
{
    private final Server server;
    
    public LaunchMessageHandler(Server server)
    {
        this.server = server;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean handle(ChannelHandlerContext ctx, MessageEvent e,
            LaunchMessage msg)
    {
        int id = server.getMain().getCore().createBoard(msg.getBoard());
        Board board = server.getMain().getCore().getBoard(id);
        
        Map<String, Object> attachment = (Map<String, Object>) ctx
                .getAttachment();
        User user = (User) attachment.get("user");
        board.getViewers().add(user);
        
        for (ProgramsListMessage.Program program : msg.getPrograms())
        {
            Program p = server.getMain().getCore().getUser(program.getUser())
                    .getMachine(program.getMachine())
                    .getProgram(program.getId());
            board.registerProgram(p);
        }
        
        LaunchMessage ret = new LaunchMessage();
        ret.setId(id);
        ret.setBoard(msg.getBoard());
        ret.setPrograms(msg.getPrograms());
        
        e.getChannel().write(msg);
        
        server.getMain().getCore().getExecutor().execute(board);
        
        return true;
    }
    
}
