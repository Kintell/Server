package com.kokakiwi.kintell.server.core.rank;

public class EloRating
{
    public static int MULTFACTOR = 400;
    public static int K          = 32;
    
    /**
     * P(first-second)<br />
     * Expected chance first will beat second
     * 
     * @param first
     *            The first
     * @param second
     *            The second
     * @return Expected chance first will beat second
     */
    public static double probability(int first, int second)
    {
        final int diff = second - first;
        final double prob = 1.0 / (1.0 + Math.pow(10.0, diff / 400.0));
        
        return prob;
    }
    
    public static int points(int first, int second, double result)
    {
        return points(probability(first, second), result);
    }
    
    /**
     * 
     * @param prob
     * @param result
     * @return the number of points which were won or lost
     */
    public static int points(double prob, double result)
    {
        final int delta = (int) (K * (result - prob));
        
        return delta;
    }
}
