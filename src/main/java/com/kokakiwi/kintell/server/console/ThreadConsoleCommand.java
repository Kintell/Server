package com.kokakiwi.kintell.server.console;

import java.util.List;
import java.util.Scanner;

import com.google.common.collect.Lists;
import com.kokakiwi.kintell.server.KintellServer;
import com.kokakiwi.kintell.server.core.User;
import com.kokakiwi.kintell.server.core.board.Board;
import com.kokakiwi.kintell.spec.net.msg.DebugMessage;

public class ThreadConsoleCommand implements Runnable
{
    private final KintellServer main;
    private final Thread        thread;
    
    private boolean             running = false;
    
    private final Scanner       scanner = new Scanner(System.in);
    
    public ThreadConsoleCommand(KintellServer main)
    {
        this.main = main;
        thread = new Thread(this);
        thread.start();
    }
    
    public void run()
    {
        running = true;
        
        while (running)
        {
            final String command = scanner.nextLine();
            
            if (command.equalsIgnoreCase("stop"))
            {
                main.stop();
            }
            else if (command.equalsIgnoreCase("stopmatchs"))
            {
                System.out.println("Stop ALL the matchs!");
                for (final Board board : main.getCore().getBoards().values())
                {
                    board.setRunning(false);
                }
            }
            else
            {
                final String[] parts = command.split(" ");
                if (parts.length > 0)
                {
                    final String sub = parts[0];
                    if (sub.equalsIgnoreCase("debug") && parts.length > 2)
                    {
                        final String username = parts[1];
                        final StringBuilder sb = new StringBuilder();
                        for (int i = 2; i < parts.length; i++)
                        {
                            sb.append(parts[i]);
                            if (i < parts.length - 1)
                            {
                                sb.append(' ');
                            }
                        }
                        
                        final User user = main.getCore().getUser(username);
                        final DebugMessage msg = new DebugMessage();
                        
                        final List<String> messages = Lists.newLinkedList();
                        messages.add(sb.toString());
                        
                        msg.setMessages(messages);
                        
                        user.sendMessage(msg);
                    }
                }
            }
        }
    }
}
