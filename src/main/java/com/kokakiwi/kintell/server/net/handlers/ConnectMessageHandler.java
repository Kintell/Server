package com.kokakiwi.kintell.server.net.handlers;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.exec.Machine;
import com.kokakiwi.kintell.server.core.exec.Program;
import com.kokakiwi.kintell.server.core.exec.ProgramExecutor;
import com.kokakiwi.kintell.server.core.exec.ProgramExecutorFactory;
import com.kokakiwi.kintell.server.database.UserEntry;
import com.kokakiwi.kintell.server.net.Server;
import com.kokakiwi.kintell.spec.net.MessageHandler;
import com.kokakiwi.kintell.spec.net.msg.ConnectMessage;
import com.kokakiwi.kintell.spec.net.msg.WorkspaceInitMessage;
import com.kokakiwi.kintell.spec.net.msg.WrongPasswordMessage;

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
        final Map<String, Object> attachment = (Map<String, Object>) ctx
                .getAttachment();
        User user = server.getMain().getCore()
                .getUser(msg.getPseudo().toLowerCase());
        if (user == null)
        {
            user = server.getMain().getCore()
                    .createUser(msg.getPseudo(), e.getChannel());
        }
        
        UserEntry entry = user.getEntry();
        if (entry == null)
        {
            try
            {
                entry = new UserEntry();
                entry.setName(msg.getPseudo());
                entry.setPassword(User.hashPassword(msg.getPassword()));
                server.getMain().getDatabase().getEbean().save(entry);
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
        else
        {
            try
            {
                if (!entry.getPassword().equals(
                        User.hashPassword(msg.getPassword())))
                {
                    final WrongPasswordMessage wrongPasswordMessage = new WrongPasswordMessage();
                    e.getChannel().write(wrongPasswordMessage);
                    
                    System.out
                            .println("Somebody trying to connect with wrong password.");
                    
                    return false;
                }
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
        
        user.setChannel(e.getChannel());
        System.out.println("Connected: " + user.getId());
        
        attachment.put("user", user);
        
        final WorkspaceInitMessage workspace = new WorkspaceInitMessage();
        
        for (final ProgramExecutorFactory<? extends ProgramExecutor> executorFactory : server
                .getMain().getCore().getExecutorFactories().values())
        {
            final WorkspaceInitMessage.ContentType contentType = new WorkspaceInitMessage.ContentType();
            contentType.setId(executorFactory.getId());
            contentType.setName(executorFactory.getName());
            contentType.setContentType(executorFactory.getContentType());
            
            workspace.getContentTypes().add(contentType);
        }
        
        for (final Machine machine : user.getMachines().values())
        {
            final WorkspaceInitMessage.Machine m = new WorkspaceInitMessage.Machine();
            m.setId(machine.getId());
            
            for (final Program program : machine.getPrograms().values())
            {
                final WorkspaceInitMessage.Program p = new WorkspaceInitMessage.Program();
                p.setId(program.getId());
                p.setName(program.getName());
                p.setContentType(program.getExecutorFactory().getContentType());
                
                final ProgramExecutor executor = program.getExecutorFactory()
                        .createExecutor(program);
                p.setSource(executor.getSource());
                
                m.getPrograms().add(p);
            }
            
            workspace.getMachines().add(m);
        }
        
        user.sendMessage(workspace);
        
        return true;
    }
    
}
