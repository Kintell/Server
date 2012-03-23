package com.kokakiwi.kintell.server.core.exec;

import com.kokakiwi.kintell.server.core.board.Result;

public abstract class ProgramExecutor
{
    protected final Program program;
    
    public ProgramExecutor(Program program)
    {
        this.program = program;
    }
    
    public Program getProgram()
    {
        return program;
    }
    
    /**
     * Return a content type (e.g. "text/javascript"), used by the client for
     * script editing.
     * 
     * @return Content type of script language.
     */
    public abstract String getContentType();
    
    public abstract Result init();
    
    public abstract Result tick();
    
    /**
     * Set a property that could be used by the program in the script.
     * 
     * @param name
     *            Property's name (Should be in lowercase)
     * @param value
     *            Property's value
     */
    public abstract void set(String name, Object value);
    
    /**
     * Reset the executor's context (variables registered, etc...)
     */
    public abstract Result reset();
    
    public abstract String getSource();
    
    public abstract void setSource(String source);
}
