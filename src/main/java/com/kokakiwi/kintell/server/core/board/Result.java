package com.kokakiwi.kintell.server.core.board;

import java.util.List;

import com.google.common.collect.Lists;

public class Result
{
    private Type               type;
    private final List<String> messages = Lists.newLinkedList();
    
    public Result(Type type)
    {
        this.type = type;
    }
    
    public void setType(Type type)
    {
        this.type = type;
    }
    
    public Type getType()
    {
        return type;
    }
    
    public void addMessage(String message)
    {
        messages.add(message);
    }
    
    public List<String> getMessages()
    {
        return messages;
    }
    
    public static enum Type
    {
        OK, ERROR;
    }
}
