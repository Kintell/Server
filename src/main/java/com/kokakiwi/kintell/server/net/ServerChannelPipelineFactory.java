package com.kokakiwi.kintell.server.net;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import com.kokakiwi.kintell.spec.net.CodecFrameDecoder;

public class ServerChannelPipelineFactory implements ChannelPipelineFactory
{
    private final Server server;
    
    public ServerChannelPipelineFactory(Server server)
    {
        this.server = server;
    }
    
    public ChannelPipeline getPipeline() throws Exception
    {
        ChannelPipeline pipeline = Channels.pipeline();
        
        pipeline.addLast("framer", new CodecFrameDecoder());
        
        pipeline.addLast("decoder", new ServerDecoder(server));
        pipeline.addLast("encoder", new ServerEncoder(server));
        
        pipeline.addLast("handler", new ServerHandler(server));
        
        return pipeline;
    }
    
}
