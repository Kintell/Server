package com.kokakiwi.kintell.server.net.handlers;

import java.util.List;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.google.common.collect.Lists;
import com.kokakiwi.kintell.server.core.exec.Program;
import com.kokakiwi.kintell.server.database.RankEntry;
import com.kokakiwi.kintell.server.net.Server;
import com.kokakiwi.kintell.spec.net.MessageHandler;
import com.kokakiwi.kintell.spec.net.msg.RankEntriesMessage;

public class RankEntriesMessageHandler extends
        MessageHandler<RankEntriesMessage>
{
    private final Server server;
    
    public RankEntriesMessageHandler(Server server)
    {
        this.server = server;
    }
    
    @Override
    public boolean handle(ChannelHandlerContext ctx, MessageEvent e,
            RankEntriesMessage msg)
    {
        RankEntriesMessage ret = new RankEntriesMessage();
        for (String board : server.getMain().getCore().getBoardFactories()
                .keySet())
        {
            List<RankEntry> entries = server.getMain().getCore().getRanking()
                    .getRanks(board);
            
            List<RankEntriesMessage.Rank> ranks = Lists.newLinkedList();
            for (RankEntry entry : entries)
            {
                RankEntriesMessage.Rank rank = new RankEntriesMessage.Rank();
                rank.setPoints(entry.getPoints());
                
                Program program = server.getMain().getCore()
                        .getUser(entry.getUser())
                        .getMachine(entry.getMachine())
                        .getProgram(entry.getProgram());
                rank.setProgram(program.getName());
                
                ranks.add(rank);
            }
            
            ret.getRanks().put(board, ranks);
        }
        e.getChannel().write(ret);
        
        return true;
    }
    
}
