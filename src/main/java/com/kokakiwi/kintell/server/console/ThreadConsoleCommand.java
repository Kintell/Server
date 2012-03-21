package com.kokakiwi.kintell.server.console;

import java.util.Scanner;

import com.kokakiwi.kintell.server.KintellServer;
import com.kokakiwi.kintell.server.core.board.Board;

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
            String command = scanner.nextLine();
            
            if (command.equalsIgnoreCase("stop"))
            {
                main.stop();
            }
            else if (command.equalsIgnoreCase("stopmatchs"))
            {
                System.out.println("Stop ALL the matchs!");
                for (Board board : main.getCore().getBoards().values())
                {
                    board.setRunning(false);
                }
            }
        }
    }
}
