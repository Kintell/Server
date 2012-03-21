package com.kokakiwi.kintell.server.net.handlers;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.exec.Machine;
import com.kokakiwi.kintell.server.core.exec.Program;
import com.kokakiwi.kintell.server.core.exec.ProgramExecutor;
import com.kokakiwi.kintell.server.net.Server;
import com.kokakiwi.kintell.spec.net.MessageHandler;
import com.kokakiwi.kintell.spec.net.msg.SendSourceMessage;

public class SendSourceMessageHandler extends MessageHandler<SendSourceMessage>
{
    @SuppressWarnings("unused")
    private Server server;
    
    public SendSourceMessageHandler(Server server)
    {
        this.server = server;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean handle(ChannelHandlerContext ctx, MessageEvent e,
            SendSourceMessage msg)
    {
        Map<String, Object> attach = (Map<String, Object>) ctx.getAttachment();
        User user = (User) attach.get("user");
        Machine machine = user.getMachine(msg.getMachine());
        if (machine != null)
        {
            Program program = machine.getProgram(msg.getProgram());
            if (program != null)
            {
                ProgramExecutor executor = program.getExecutorFactory()
                        .createExecutor(program);
                executor.setSource(msg.getSource());
            }
        }
        
        return true;
    }
    
}
