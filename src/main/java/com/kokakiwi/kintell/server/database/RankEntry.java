package com.kokakiwi.kintell.server.database;

import javax.persistence.*;

@Entity
@Table(name = "ranks")
public class RankEntry
{
    @Id
    @Column(name = "id")
    private int    id;
    
    @Basic
    @Column(name = "program")
    private String program;
    
    @Basic
    @Column(name = "score")
    private int    score;
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getProgram()
    {
        return program;
    }
    
    public void setProgram(String program)
    {
        this.program = program;
    }
    
    public int getScore()
    {
        return score;
    }
    
    public void setScore(int score)
    {
        this.score = score;
    }
}
