package com.kokakiwi.kintell.server.tests;

import org.junit.Test;

import com.kokakiwi.kintell.server.core.rank.EloRating;

public class EloRatingTest
{
    @Test
    public void test()
    {
        int winner = 1000;
        int looser = 1000;
        
        for (int i = 0; i < 10; i++)
        {
            System.out.println("== Round " + (i + 1) + " ==");
            
            final int delta_winner = EloRating.points(winner, looser, 1.0);
            final int delta_looser = EloRating.points(looser, winner, 0.0);
            
            winner += delta_winner;
            looser += delta_looser;
        }
        
        int best = 1000;
        
        System.out.println("== Extra round ==");
        
        final int delta_best = EloRating.points(best, winner, 1.0);
        final int delta_winner = EloRating.points(winner, best, 0.0);
        
        winner += delta_winner;
        best += delta_best;
    }
}
