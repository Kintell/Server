package com.kokakiwi.kintell.server.net.handlers;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.board.Board;
import com.kokakiwi.kintell.server.net.Server;
import com.kokakiwi.kintell.spec.net.MessageHandler;
import com.kokakiwi.kintell.spec.net.msg.StopMessage;

public class StopMessageHandler extends MessageHandler<StopMessage>
{
    private final Server server;
    
    public StopMessageHandler(Server server)
    {
        this.server = server;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean handle(ChannelHandlerContext ctx, MessageEvent e,
            StopMessage msg)
    {
        Board board = server.getMain().getCore().getBoard(msg.getId());
        if (board != null)
        {
            Map<String, Object> attach = (Map<String, Object>) ctx
                    .getAttachment();
            User user = (User) attach.get("user");
            
            board.getViewers().remove(user);
        }
        
        return true;
    }
    
}
