package com.luisonthekeyboard;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

public class EliteJournalTailer implements TailerListener {

    private Tailer tailer;
    private final long startTime;

    public EliteJournalTailer(long start) {
        startTime = start;
    }

    @Override
    public void init(Tailer tailer) {
        this.tailer = tailer;
    }

    @Override
    public void fileNotFound() {
        tailer.stop();
        System.err.println("fileNotFound() called from EliteJournalTailer.");
    }

    @Override
    public void fileRotated() {
        System.out.println("fileRotated() called from EliteJournalTailer.");
    }

    public void handle(String line) {
        System.out.println(System.nanoTime() - startTime + " | >>>>>>> | " + line);
    }

    @Override
    public void handle(Exception ex) {
        tailer.stop();
        ex.printStackTrace();
    }
}
