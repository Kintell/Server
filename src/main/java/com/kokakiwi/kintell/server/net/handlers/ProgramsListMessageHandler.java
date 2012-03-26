package com.kokakiwi.kintell.server.net.handlers;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.exec.Machine;
import com.kokakiwi.kintell.server.core.exec.Program;
import com.kokakiwi.kintell.server.net.Server;
import com.kokakiwi.kintell.spec.net.MessageHandler;
import com.kokakiwi.kintell.spec.net.msg.ProgramsListMessage;

public class ProgramsListMessageHandler extends
        MessageHandler<ProgramsListMessage>
{
    private final Server server;
    
    public ProgramsListMessageHandler(Server server)
    {
        this.server = server;
    }
    
    @Override
    public boolean handle(ChannelHandlerContext ctx, MessageEvent e,
            ProgramsListMessage msg)
    {
        final ProgramsListMessage ret = new ProgramsListMessage();
        
        for (final User user : server.getMain().getCore().getUsers().values())
        {
            for (final Machine machine : user.getMachines().values())
            {
                for (final Program program : machine.getPrograms().values())
                {
                    final ProgramsListMessage.Program p = new ProgramsListMessage.Program();
                    p.setUser(user.getId());
                    p.setId(program.getId());
                    p.setName(program.getName());
                    p.setMachine(machine.getId());
                    
                    ret.getPrograms().add(p);
                }
            }
        }
        e.getChannel().write(ret);
        
        return true;
    }
    
}
