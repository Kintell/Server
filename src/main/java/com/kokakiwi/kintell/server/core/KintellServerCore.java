package com.kokakiwi.kintell.server.core;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.Channel;

import com.google.common.collect.Maps;
import com.kokakiwi.kintell.server.KintellServer;
import com.kokakiwi.kintell.server.core.board.Board;
import com.kokakiwi.kintell.server.core.board.BoardFactory;
import com.kokakiwi.kintell.server.core.exec.Machines;
import com.kokakiwi.kintell.server.core.exec.ProgramExecutor;
import com.kokakiwi.kintell.server.core.exec.ProgramExecutorFactory;

public class KintellServerCore
{
    private final KintellServer                                                  main;
    
    private final Machines                                                       machines;
    
    private final Map<String, ProgramExecutorFactory<? extends ProgramExecutor>> executorFactories = Maps.newLinkedHashMap();
    private final Map<String, BoardFactory<? extends Board>>                     boardFactories    = Maps.newLinkedHashMap();
    
    private final Map<String, User>                                              users             = Maps.newLinkedHashMap();
    
    private final Random                                                         random            = new Random();
    private final Map<Integer, Board>                                            boards            = Maps.newLinkedHashMap();
    private final ExecutorService                                                executor          = Executors
                                                                                                           .newCachedThreadPool();
    
    public KintellServerCore(KintellServer main)
    {
        this.main = main;
        
        machines = new Machines(this);
    }
    
    public void init()
    {
        machines.init();
    }
    
    public KintellServer getMain()
    {
        return main;
    }
    
    public Machines getMachines()
    {
        return machines;
    }
    
    public Map<String, ProgramExecutorFactory<? extends ProgramExecutor>> getExecutorFactories()
    {
        return executorFactories;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends ProgramExecutor, V extends ProgramExecutorFactory<T>> V getExecutorFactory(
            String id)
    {
        return (V) executorFactories.get(id);
    }
    
    public <T extends ProgramExecutor, V extends ProgramExecutorFactory<T>> void registerExecutorFactory(
            V executorFactory)
    {
        executorFactories.put(executorFactory.getId(), executorFactory);
    }
    
    public Map<String, BoardFactory<? extends Board>> getBoardFactories()
    {
        return boardFactories;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Board, V extends BoardFactory<T>> V getBoardFactory(
            String id)
    {
        return (V) boardFactories.get(id);
    }
    
    public <T extends Board, V extends BoardFactory<T>> void registerBoardFactory(
            V boardFactory)
    {
        boardFactories.put(boardFactory.getId(), boardFactory);
    }
    
    public Map<String, User> getUsers()
    {
        return users;
    }
    
    public User getUser(String id)
    {
        return users.get(id);
    }
    
    public User createUser(String id, Channel channel)
    {
        User user = new User(machines, id, channel);
        
        users.put(id, user);
        
        return user;
    }
    
    public Map<Integer, Board> getBoards()
    {
        return boards;
    }
    
    public int createBoard(String id)
    {
        int boardId = -1;
        
        BoardFactory<? extends Board> boardFactory = getBoardFactory(id);
        
        if (boardFactory != null)
        {
            Board board = boardFactory.createBoard(this);
            do
            {
                boardId = random.nextInt();
            } while (boards.containsKey(boardId));
            board.setId(boardId);
            boards.put(boardId, board);
        }
        return boardId;
    }
    
    public Board getBoard(int id)
    {
        return boards.get(id);
    }
    
    public ExecutorService getExecutor()
    {
        return executor;
    }
}
