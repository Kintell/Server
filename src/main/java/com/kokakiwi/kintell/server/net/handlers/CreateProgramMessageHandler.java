package com.kokakiwi.kintell.server.net.handlers;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.kokakiwi.kintell.server.core.KintellServerCore;
import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.exec.Machine;
import com.kokakiwi.kintell.server.core.exec.Program;
import com.kokakiwi.kintell.server.core.exec.ProgramExecutor;
import com.kokakiwi.kintell.server.core.exec.ProgramExecutorFactory;
import com.kokakiwi.kintell.server.net.Server;
import com.kokakiwi.kintell.spec.net.MessageHandler;
import com.kokakiwi.kintell.spec.net.msg.CreateProgramMessage;

public class CreateProgramMessageHandler extends
        MessageHandler<CreateProgramMessage>
{
    private final Server server;
    
    public CreateProgramMessageHandler(Server server)
    {
        this.server = server;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean handle(ChannelHandlerContext ctx, MessageEvent e,
            CreateProgramMessage msg)
    {
        final KintellServerCore core = server.getMain().getCore();
        final Map<String, Object> attach = (Map<String, Object>) ctx
                .getAttachment();
        final User user = (User) attach.get("user");
        final Machine machine = user.getMachine(msg.getMachine());
        if (machine != null)
        {
            final Program program = machine.createProgram(msg.getId(),
                    msg.getName());
            final ProgramExecutorFactory<? extends ProgramExecutor> executorFactory = core
                    .getExecutorFactory(msg.getContentType().getId());
            program.setExecutor(executorFactory);
        }
        
        return true;
    }
    
}
