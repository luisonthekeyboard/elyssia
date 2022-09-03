package com.luisonthekeyboard;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import java.io.File;
import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("Hello World!");

        TailerListener listener = new MyTailerListener();
        //File file = new File("C:\\Users\\Luis\\code\\elyssia\\test.log");
        File file = new File("C:\\Users\\Luis\\Saved Games\\Frontier Developments\\Elite Dangerous\\Journal.2022-09-03T170423.01.log");
        Tailer tailer = Tailer.create(file, listener, 1000);

        while (true) {
            //no op
        }
    }
}
