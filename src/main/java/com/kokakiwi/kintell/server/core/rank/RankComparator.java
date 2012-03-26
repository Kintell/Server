package com.kokakiwi.kintell.server.core.rank;

import java.util.Comparator;

import com.kokakiwi.kintell.server.database.RankEntry;

public class RankComparator implements Comparator<RankEntry>
{
    public int compare(RankEntry o1, RankEntry o2)
    {
        if (o1.getPoints() > o2.getPoints())
        {
            return -1;
        }
        
        if (o1.getPoints() < o2.getPoints())
        {
            return 1;
        }
        
        return 0;
    }
}
