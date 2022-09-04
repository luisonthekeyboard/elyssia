package com.luisonthekeyboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

public class EliteJournalTailer implements TailerListener {

    private final long startTime;
    private Tailer tailer;

    private boolean inFSS;

    public EliteJournalTailer(long start) {

        startTime = start;
        inFSS = false;
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

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(line);
            String event = jsonNode.get("event").asText();
            String timestamp = jsonNode.get("timestamp").asText();

            final String eventTime = (System.nanoTime() - startTime) + " :: " + timestamp;

            String output = switch (event) {
                case "FSDJump" -> {

                    inFSS = false;

                    String destination = jsonNode.get("StarSystem").asText();
                    String jumpDistance = jsonNode.get("JumpDist").asText();

                    yield String.format("%s | %s >>>>>>> %s, %s", eventTime, event, destination, jumpDistance);
                }
                case "FSSDiscoveryScan" -> {

                    boolean systemScanComplete = jsonNode.get("Progress").asText().startsWith("1.0");
                    String bodyCount = jsonNode.get("BodyCount").asText();

                    yield systemScanComplete ?
                            String.format("%s | %s >>>>>>> Honked %s bodies, DONE.", eventTime, event, bodyCount) :
                            String.format("%s | %s >>>>>>> Honked %s bodies.", eventTime, event, bodyCount);
                }
                case "Music" -> {
                    boolean isFSS = jsonNode.get("MusicTrack").asText().startsWith("SystemAndSurfaceScanner");

                    if (isFSS) {
                        inFSS = true;
                        yield String.format("%s | %s >>>>>>> Entered FSS.", eventTime, event);
                    } else {
                        if (inFSS) {
                            inFSS = false;
                            yield String.format("%s | %s >>>>>>> Left FSS.", eventTime, event);
                        } else {
                            yield "";
                        }
                    }
                }
                case "FSSAllBodiesFound" -> {

                    String count = jsonNode.get("Count").asText();

                    yield String.format("%s | %s >>>>>>> %s bodies, DONE.", eventTime, event, count);
                }
                case "SAAScanComplete" -> {

                    String bodyName = jsonNode.get("BodyName").asText();
                    String probesUsed = jsonNode.get("ProbesUsed").asText();
                    String efficiencyTarget = jsonNode.get("EfficiencyTarget").asText();

                    yield String.format("%s | %s >>>>>>> %s scanned, used %s out of %s probes.",
                            eventTime, event, bodyName, probesUsed, efficiencyTarget);
                }
                case "Shutdown" -> {
                    yield String.format("%s | %s >>>>>>> Closing session.", eventTime, event);
                }
                case "Scan" -> {

                    String bodyName = jsonNode.get("BodyName").asText();
                    String wasDiscovered = jsonNode.get("WasDiscovered").asBoolean() ? "Already discovered." : "Not yet discovered.";
                    String wasMapped = jsonNode.get("WasMapped").asBoolean() ? "Already mapped." : "Not yet mapped.";

                    // is it an asteroid belt?
                    if (bodyName.contains("Belt Cluster")) {

                        yield String.format("%s | %s >>>>>>> %s scanned -- Asteroid Belt Cluster. %s %s",
                                eventTime, event, bodyName, wasDiscovered, wasMapped);
                    }

                    // is it a star
                    jsonNode.get("StarType");
                    if (jsonNode.get("StarType") != null) {

                        String starType = jsonNode.get("StarType").asText();

                        yield String.format("%s | %s >>>>>>> %s scanned -- Class %s Star. %s %s",
                                eventTime, event, bodyName, starType, wasDiscovered, wasMapped);

                    }

                    String planetClass = jsonNode.get("PlanetClass").asText();
                    String terraformState = jsonNode.get("TerraformState").asText().equalsIgnoreCase("terraformable") ?
                            " TERRAFORMABLE" :
                            "";

                    yield String.format("%s | %s >>>>>>> %s scanned -- %s%s. %s %s",
                            eventTime, event, bodyName, planetClass, terraformState, wasDiscovered, wasMapped);

                }
                default -> "";
            };

            if (!output.isEmpty()) {
                System.out.println(output);
            }

        } catch (JsonProcessingException e) {
            handle(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(Exception ex) {
        tailer.stop();
        ex.printStackTrace();
    }
}
