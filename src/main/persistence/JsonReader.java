package persistence;

import model.Exercise;
import model.FitnessManager;
import model.TrainingSession;

import org.json.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

// JsonReader class referenced from the WorkRoomApp
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

// Represents a reader that reads FitnessManager from JSON data stored in file
public class JsonReader {
    private String source; // Source file path

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads FitnessManager from file and returns it;
    // throws IOException if an error occurs reading data from file
    public FitnessManager read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseFitnessManager(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses FitnessManager from JSON object and returns it
    private FitnessManager parseFitnessManager(JSONObject jsonObject) {
        FitnessManager fm = new FitnessManager();
        addExercises(fm, jsonObject);
        addSessions(fm, jsonObject);
        return fm;
    }

    // MODIFIES: fm
    // EFFECTS: parses exercises from JSON object and adds them to FitnessManager
    private void addExercises(FitnessManager fm, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("exercises");
        for (Object obj : jsonArray) {
            JSONObject exerciseJson = (JSONObject) obj;
            String name = exerciseJson.getString("name");
            String muscle = exerciseJson.getString("targetMuscle");
            int weight = exerciseJson.getInt("weight");
            int reps = exerciseJson.getInt("reps");
            Exercise e = new Exercise(name, muscle, weight, reps);
            fm.getExercises().add(e);
        }
    }

    // MODIFIES: fm
    // EFFECTS: parses training sessions from JSON object and adds them to FitnessManager
    private void addSessions(FitnessManager fm, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("sessions");
        for (Object obj : jsonArray) {
            JSONObject sessionJson = (JSONObject) obj;
            String name = sessionJson.getString("name");
            TrainingSession session = new TrainingSession(name);

            JSONArray exerciseSets = sessionJson.getJSONArray("exerciseSets");
            for (Object setObj : exerciseSets) {
                JSONObject setJson = (JSONObject) setObj;
                String exerciseName = setJson.getString("name");
                String muscle = setJson.getString("targetMuscle");
                int weight = setJson.getInt("weight");
                int reps = setJson.getInt("reps");
                int sets = setJson.getInt("sets");

                Exercise e = new Exercise(exerciseName, muscle, weight, reps);
                session.getExerciseSets().put(e, sets);
            }
            fm.getSessions().add(session);
        }
    }
}
