package com.luisonthekeyboard;

import org.apache.commons.io.input.TailerListenerAdapter;

public class MyTailerListener extends TailerListenerAdapter {
    public void handle(String line) {
        System.out.println("line? "+line);
    }
}
