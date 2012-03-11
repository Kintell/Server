package com.kokakiwi.kintell.server.core.exec;

public interface ProgramExecutorFactory<T extends ProgramExecutor>
{
    public String getId();
    
    public String getContentType();
    
    public String getName();
    
    public T createExecutor(Program program);
}
