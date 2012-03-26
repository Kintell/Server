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
import com.kokakiwi.kintell.server.net.handlers.ConnectMessageHandler;
import com.kokakiwi.kintell.server.net.handlers.CreateMachineMessageHandler;
import com.kokakiwi.kintell.server.net.handlers.CreateProgramMessageHandler;
import com.kokakiwi.kintell.server.net.handlers.LaunchMessageHandler;
import com.kokakiwi.kintell.server.net.handlers.ProgramsListMessageHandler;
import com.kokakiwi.kintell.server.net.handlers.RankEntriesMessageHandler;
import com.kokakiwi.kintell.server.net.handlers.SendSourceMessageHandler;
import com.kokakiwi.kintell.server.net.handlers.StopMessageHandler;
import com.kokakiwi.kintell.spec.net.CodecResolver;
import com.kokakiwi.kintell.spec.net.msg.ConnectMessage;
import com.kokakiwi.kintell.spec.net.msg.CreateMachineMessage;
import com.kokakiwi.kintell.spec.net.msg.CreateProgramMessage;
import com.kokakiwi.kintell.spec.net.msg.LaunchMessage;
import com.kokakiwi.kintell.spec.net.msg.ProgramsListMessage;
import com.kokakiwi.kintell.spec.net.msg.RankEntriesMessage;
import com.kokakiwi.kintell.spec.net.msg.SendSourceMessage;
import com.kokakiwi.kintell.spec.net.msg.StopMessage;

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
        codec.registerHandler(StopMessage.class, new StopMessageHandler(this));
        codec.registerHandler(RankEntriesMessage.class,
                new RankEntriesMessageHandler(this));
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
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void stop()
    {
        channels.disconnect();
        
        final ChannelFuture future = channel.close();
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
