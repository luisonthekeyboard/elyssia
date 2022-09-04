package com.luisonthekeyboard;

import org.apache.commons.io.input.Tailer;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {

        long start = System.nanoTime();
        Map<String, Tailer> tailers = new HashMap<>();
        WatchService watchService = FileSystems.getDefault().newWatchService();

        String pathForSavedGames = "C:\\Users\\Luis\\Saved Games\\Frontier Developments\\Elite Dangerous\\";
        Path path = Paths.get(pathForSavedGames);
        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while ((key = watchService.take()) != null) {

            for (WatchEvent<?> event : key.pollEvents()) {

                if (event.kind().type().equals(Path.class)) {

                    WatchEvent<Path> eventAsPath = (WatchEvent<Path>) event;
                    String filename =  eventAsPath.context().toString();

                    if (filename.startsWith("Journal") && !tailers.containsKey(filename)) {

                        tailers.put(
                                filename,
                                Tailer.create(
                                        new File(pathForSavedGames + filename),
                                        new EliteJournalTailer(start),
                                        1000,
                                        false));

                    }
                }
            }

            key.reset();
        }
    }
}
