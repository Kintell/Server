package com.kokakiwi.kintell.server.net;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.kokakiwi.kintell.spec.net.MessageHandler;
import com.kokakiwi.kintell.spec.net.msg.Message;

public class PacketExecutor implements Runnable
{
    private final MessageHandler<Message> handler;
    private final ChannelHandlerContext   ctx;
    private final MessageEvent            e;
    
    public PacketExecutor(MessageHandler<Message> handler,
            ChannelHandlerContext ctx, MessageEvent e)
    {
        this.handler = handler;
        this.ctx = ctx;
        this.e = e;
    }
    
    public void run()
    {
        handler.handle(ctx, e);
    }
    
}
