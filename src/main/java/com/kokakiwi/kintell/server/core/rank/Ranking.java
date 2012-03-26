package com.kokakiwi.kintell.server.core.rank;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.avaje.ebean.ExpressionList;
import com.google.common.collect.Lists;
import com.kokakiwi.kintell.server.core.KintellServerCore;
import com.kokakiwi.kintell.server.core.exec.Program;
import com.kokakiwi.kintell.server.database.Database;
import com.kokakiwi.kintell.server.database.RankEntry;

public class Ranking
{
    public final static int         INIT_POINTS = 1000;
    public final static int         LIMIT       = EloRating.K * 2;
    
    private final KintellServerCore core;
    private final Database          db;
    private final RankComparator    comparator;
    
    public Ranking(KintellServerCore core)
    {
        this.core = core;
        db = core.getMain().getDatabase();
        comparator = new RankComparator();
    }
    
    public KintellServerCore getCore()
    {
        return core;
    }
    
    public RankEntry createRank(String board, Program program)
    {
        final RankEntry rank = new RankEntry();
        rank.setBoard(board);
        rank.setUser(program.getOwner().getOwner().getId());
        rank.setMachine(program.getOwner().getId());
        rank.setProgram(program.getId());
        rank.setPoints(INIT_POINTS);
        
        updateRank(rank);
        
        return rank;
    }
    
    public RankEntry getRank(String board, Program program)
    {
        RankEntry rank = null;
        
        final ExpressionList<RankEntry> query = db.getEbean()
                .find(RankEntry.class).where().eq("board", board)
                .eq("program", program.getId());
        
        if (query.findRowCount() > 0)
        {
            rank = query.findUnique();
        }
        
        return rank;
    }
    
    public List<RankEntry> getRanks(String board)
    {
        final List<RankEntry> ranks = Lists.newLinkedList();
        
        final ExpressionList<RankEntry> query = db.getEbean()
                .find(RankEntry.class).where().eq("board", board);
        
        if (query.findRowCount() > 0)
        {
            ranks.addAll(query.findList());
        }
        
        Collections.sort(ranks, comparator);
        
        return ranks;
    }
    
    public void updateRank(RankEntry rank)
    {
        db.getEbean().save(rank);
    }
    
    public void updateRanks(String board, Collection<Program> programs,
            Program winner)
    {
        if (programs.size() == 1)
        {
            return;
        }
        
        int opponentsPoints = 0;
        int opponentsNum = 0;
        for (Program program : programs)
        {
            if (!program.equals(winner))
            {
                opponentsNum++;
                
                RankEntry rank = getRank(board, program);
                if (rank == null)
                {
                    rank = createRank(board, program);
                }
                
                opponentsPoints += rank.getPoints();
            }
        }
        
        int average = opponentsPoints / opponentsNum;
        
        RankEntry winnerRank = getRank(board, winner);
        if (winnerRank == null)
        {
            winnerRank = createRank(board, winner);
        }
        
        if ((winnerRank.getPoints() - average) < LIMIT)
        {
            int delta_winner = EloRating.points(winnerRank.getPoints(),
                    average, 1.0);
            int delta_looser = EloRating.points(average,
                    winnerRank.getPoints(), 0.0);
            
            for (Program program : programs)
            {
                RankEntry rank = getRank(board, program);
                if (rank == null)
                {
                    rank = createRank(board, program);
                }
                
                int points = rank.getPoints();
                
                if (program.equals(winner))
                {
                    points += delta_winner;
                }
                else
                {
                    points += delta_looser;
                }
                
                rank.setPoints(points);
                
                updateRank(rank);
            }
        }
    }
}
