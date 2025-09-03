package model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import persistence.Writable;

// Represents a training session having a name and a map of exercises to their
// corresponding number of sets, where the key is an Exercise object and the
// value is the number of sets that the specific exercise should be executed for
public class TrainingSession implements Writable {
    private String name;                           // training session name
    private Map<Exercise, Integer> exerciseSets;   // maps Exercise to its necessary sets

    // EFFECTS: initializes a TrainingSession with a name and empty map
    public TrainingSession(String name) {
        this.name = name;
        this.exerciseSets = new HashMap<>();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);

        JSONArray exercisesArray = new JSONArray();
        for (Map.Entry<Exercise, Integer> entry : exerciseSets.entrySet()) {
            JSONObject exerciseJson = entry.getKey().toJson();
            exerciseJson.put("sets", entry.getValue());
            exercisesArray.put(exerciseJson);
        }

        json.put("exerciseSets", exercisesArray);
        return json;
    }

    // REQUIRES: sets > 0
    // MODIFIES: this
    // EFFECTS: adds an exercise and its number of sets to a training session
    public void addExercise(Exercise exercise, int sets) {
        if (exerciseSets.containsKey(exercise)) {
            System.out.println("This Exercise has already been added!");
        } else {
            exerciseSets.put(exercise, sets);
        }

        EventLog.getInstance().logEvent(new Event("Added " + exercise.getName() + " to " + name + "!"));
    }

    // MODIFIES: this
    // EFFECTS: removes an exercise from a training session
    public void removeExercise(Exercise exercise) {
        exerciseSets.remove(exercise);

        EventLog.getInstance().logEvent(new Event("Removed " + exercise.getName() + " from " + name + "!"));
    }

    // MODIFIES: this
    // EFFECTS: removes all exercises from a training session
    public void clearSession() {
        exerciseSets.clear();

        EventLog.getInstance().logEvent(new Event("Cleared session " + name + "!"));
    }

    // GETTERS:

    // EFFECTS: returns name of training session
    public String getName() {
        return name;
    }

    // EFFECTS: returns a training sessions map of exercises and sets
    public Map<Exercise, Integer> getExerciseSets() {
        return exerciseSets;
    }

    // SETTERS:

    public void setName(String name) {
        this.name = name;
    }

}


