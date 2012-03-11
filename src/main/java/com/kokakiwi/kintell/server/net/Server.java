package com.kokakiwi.kintell.server.net;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.kokakiwi.kintell.server.KintellServer;
import com.kokakiwi.kintell.server.net.handlers.*;
import com.kokakiwi.kintell.spec.net.msg.*;
import com.kokakiwi.kintell.spec.net.CodecResolver;

public class Server
{
    private final KintellServer   main;
    
    private final ServerBootstrap bootstrap;
    
    private Channel               channel;
    private final ChannelGroup    channels = new DefaultChannelGroup(
                                                   "KintellServer");
    
    private final CodecResolver   codec    = new CodecResolver();
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    public Server(KintellServer main)
    {
        this.main = main;
        
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new ServerChannelPipelineFactory(this));
        
        // Register handlers
        codec.registerHandler(ConnectMessage.class, new ConnectMessageHandler(
                this));
        codec.registerHandler(SendSourceMessage.class,
                new SendSourceMessageHandler(this));
        codec.registerHandler(CreateMachineMessage.class,
                new CreateMachineMessageHandler(this));
        codec.registerHandler(CreateProgramMessage.class,
                new CreateProgramMessageHandler(this));
        codec.registerHandler(ProgramsListMessage.class,
                new ProgramsListMessageHandler(this));
        codec.registerHandler(LaunchMessage.class, new LaunchMessageHandler(
                this));
    }
    
    public void start()
    {
        try
        {
            System.out.println("Bind to port "
                    + main.getConfiguration().getInteger("server.port"));
            channel = bootstrap.bind(new InetSocketAddress(main
                    .getConfiguration().getInteger("server.port")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void stop()
    {
        channels.disconnect();
        
        ChannelFuture future = channel.close();
        future.awaitUninterruptibly();
        if (!future.isSuccess())
        {
            System.err.println("Error during closing main channel!");
        }
        
        bootstrap.releaseExternalResources();
    }
    
    public KintellServer getMain()
    {
        return main;
    }
    
    public ServerBootstrap getBootstrap()
    {
        return bootstrap;
    }
    
    public Channel getChannel()
    {
        return channel;
    }
    
    public ChannelGroup getChannels()
    {
        return channels;
    }
    
    public CodecResolver getCodec()
    {
        return codec;
    }
    
    public ExecutorService getExecutor()
    {
        return executor;
    }
}
