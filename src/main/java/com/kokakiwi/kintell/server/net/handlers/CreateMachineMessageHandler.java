package com.kokakiwi.kintell.server.net.handlers;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.kokakiwi.kintell.server.core.KintellServerCore;
import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.exec.Machine;
import com.kokakiwi.kintell.server.net.Server;
import com.kokakiwi.kintell.spec.net.MessageHandler;
import com.kokakiwi.kintell.spec.net.msg.CreateMachineMessage;

public class CreateMachineMessageHandler extends
        MessageHandler<CreateMachineMessage>
{
    private final Server server;
    
    public CreateMachineMessageHandler(Server server)
    {
        this.server = server;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean handle(ChannelHandlerContext ctx, MessageEvent e,
            CreateMachineMessage msg)
    {
        KintellServerCore core = server.getMain().getCore();
        Map<String, Object> attach = (Map<String, Object>) ctx.getAttachment();
        User user = (User) attach.get("user");
        if (user.getMachine(msg.getId()) == null)
        {
            Machine machine = core.getMachines().createMachine(msg.getId(),
                    user);
            user.addMachine(machine);
        }
        
        return true;
    }
    
}
