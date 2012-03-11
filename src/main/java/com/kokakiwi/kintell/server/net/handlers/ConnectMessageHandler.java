package com.kokakiwi.kintell.server.net.handlers;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.exec.Machine;
import com.kokakiwi.kintell.server.core.exec.Program;
import com.kokakiwi.kintell.server.core.exec.ProgramExecutor;
import com.kokakiwi.kintell.server.core.exec.ProgramExecutorFactory;
import com.kokakiwi.kintell.server.net.Server;
import com.kokakiwi.kintell.spec.net.MessageHandler;
import com.kokakiwi.kintell.spec.net.msg.ConnectMessage;
import com.kokakiwi.kintell.spec.net.msg.WorkspaceInitMessage;

public class ConnectMessageHandler extends MessageHandler<ConnectMessage>
{
    private final Server server;
    
    public ConnectMessageHandler(Server server)
    {
        this.server = server;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean handle(ChannelHandlerContext ctx, MessageEvent e,
            ConnectMessage msg)
    {
        Map<String, Object> attachment = (Map<String, Object>) ctx
                .getAttachment();
        User user = server.getMain().getCore()
                .getUser(msg.getPseudo().toLowerCase());
        if (user == null)
        {
            user = server.getMain().getCore()
                    .createUser(msg.getPseudo(), e.getChannel());
        }
        
        user.setChannel(e.getChannel());
        
        attachment.put("user", user);
        
        WorkspaceInitMessage workspace = new WorkspaceInitMessage();
        
        for (ProgramExecutorFactory<? extends ProgramExecutor> executorFactory : server
                .getMain().getCore().getExecutorFactories().values())
        {
            WorkspaceInitMessage.ContentType contentType = new WorkspaceInitMessage.ContentType();
            contentType.setId(executorFactory.getId());
            contentType.setName(executorFactory.getName());
            contentType.setContentType(executorFactory.getContentType());
            
            workspace.getContentTypes().add(contentType);
        }
        
        for (Machine machine : user.getMachines().values())
        {
            WorkspaceInitMessage.Machine m = new WorkspaceInitMessage.Machine();
            m.setId(machine.getId());
            
            for (Program program : machine.getPrograms().values())
            {
                WorkspaceInitMessage.Program p = new WorkspaceInitMessage.Program();
                p.setId(program.getId());
                p.setName(program.getName());
                p.setContentType(program.getExecutor().getContentType());
                p.setSource(program.getExecutor().getSource());
                
                m.getPrograms().add(p);
            }
            
            workspace.getMachines().add(m);
        }
        
        user.getChannel().write(workspace);
        
        return true;
    }
    
}
