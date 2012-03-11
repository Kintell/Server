package com.kokakiwi.kintell.server.core.board;

import com.kokakiwi.kintell.server.core.KintellServerCore;

public interface BoardFactory<T extends Board>
{
    public String getId();
    
    public String getName();
    
    public T createBoard(KintellServerCore core);
}
