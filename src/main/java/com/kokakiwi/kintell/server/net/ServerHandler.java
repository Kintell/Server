package com.kokakiwi.kintell.server.net;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.google.common.collect.Maps;
import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.spec.net.MessageHandler;
import com.kokakiwi.kintell.spec.net.msg.Message;

public class ServerHandler extends SimpleChannelUpstreamHandler
{
    private final Server server;
    
    public ServerHandler(Server server)
    {
        super();
        this.server = server;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception
    {
        Object message = e.getMessage();
        if (message instanceof Message)
        {
            Message msg = (Message) message;
            Class<Message> clazz = (Class<Message>) msg.getClass();
            MessageHandler<Message> handler = server.getCodec().getHandler(
                    clazz);
            if (handler != null)
            {
                PacketExecutor executor = new PacketExecutor(handler, ctx, e);
                server.getExecutor().execute(executor);
                return;
            }
        }
        
        super.messageReceived(ctx, e);
    }
    
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        server.getChannels().add(e.getChannel());
        Map<String, Object> attach = Maps.newLinkedHashMap();
        ctx.setAttachment(attach);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
            ChannelStateEvent e) throws Exception
    {
        server.getChannels().remove(e.getChannel());
        
        if (((Map<String, Object>) ctx.getAttachment()).containsKey("user"))
        {
            User user = (User) ((Map<String, Object>) ctx.getAttachment())
                    .get("user");
            System.out.println("User disconnected : " + user.getId());
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception
    {
        e.getCause().printStackTrace();
    }
}
