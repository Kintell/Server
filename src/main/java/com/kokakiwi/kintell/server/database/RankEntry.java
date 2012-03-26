package com.kokakiwi.kintell.server.database;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ranks")
public class RankEntry
{
    @Id
    @Column(name = "id")
    private int    id;
    
    @Basic
    @Column(name = "board")
    private String board;
    
    @Basic
    @Column(name = "user")
    private String user;
    
    @Basic
    @Column(name = "machine")
    private String machine;
    
    @Basic
    @Column(name = "program")
    private String program;
    
    @Basic
    @Column(name = "points")
    private int    points;
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getBoard()
    {
        return board;
    }
    
    public void setBoard(String board)
    {
        this.board = board;
    }
    
    public String getUser()
    {
        return user;
    }
    
    public void setUser(String user)
    {
        this.user = user;
    }
    
    public String getMachine()
    {
        return machine;
    }
    
    public void setMachine(String machine)
    {
        this.machine = machine;
    }
    
    public String getProgram()
    {
        return program;
    }
    
    public void setProgram(String program)
    {
        this.program = program;
    }
    
    public int getPoints()
    {
        return points;
    }
    
    public void setPoints(int points)
    {
        this.points = points;
    }
}
