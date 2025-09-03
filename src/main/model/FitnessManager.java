package model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import persistence.Writable;

// Manages all training sessions and exercises
public class FitnessManager implements Writable {
    private List<Exercise> exercises;
    private List<TrainingSession> sessions;

    // EFFECTS: constructs an empty FitnessManager
    public FitnessManager() {
        this.exercises = new ArrayList<>();
        this.sessions = new ArrayList<>();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        JSONArray exercisesArray = new JSONArray();
        for (Exercise ex : exercises) {
            exercisesArray.put(ex.toJson());
        }
        json.put("exercises", exercisesArray);

        JSONArray sessionsArray = new JSONArray();
        for (TrainingSession session : sessions) {
            sessionsArray.put(session.toJson());
        }
        json.put("sessions", sessionsArray);

        return json;
    }

    // MODIFIES: this
    // EFFECTS: creates and stores a new exercise
    public Exercise createExercise(String name, String targetMuscle, int weight, int reps) {
        Exercise exercise = new Exercise(name, targetMuscle, weight, reps);
        exercises.add(exercise);

        EventLog.getInstance().logEvent(new Event("Created new exercise " + name + "!"));

        return exercise;
    }

    // MODIFIES: this
    // EFFECTS: creates and stores a new training session
    public TrainingSession createSession(String name) {
        TrainingSession session = new TrainingSession(name);
        sessions.add(session);

        EventLog.getInstance().logEvent(new Event("Created new session " + name + "!"));

        return session;
    }

    // EFFECTS: finds and returns an exercise by name or null
    public Exercise findExerciseByName(String name) {
        for (Exercise e : exercises) {
            if (e.getName().equalsIgnoreCase(name.trim())) {
                return e;
            }
        }
        System.out.println("Exercise Not Found.");
        return null;
    }

    // EFFECTS: finds and returns a training session by name
    public TrainingSession findSessionByName(String name) {
        for (TrainingSession s : sessions) {
            if (s.getName().equalsIgnoreCase(name.trim())) {
                return s;
            }
        }
        System.out.println("Session Not Found.");
        return null;
    }

    // MODIFIES: this
    // EFFECTS: adds an exercise and number of sets to a session
    public void addExerciseToSession(String sessionName, String exerciseName, int sets) {
        TrainingSession session = findSessionByName(sessionName);
        Exercise exercise = findExerciseByName(exerciseName);

        if (session == null) {
            System.out.println("Error: " + sessionName + "training session not found!");
            return;
        }
        if (exercise == null) {
            System.out.println("Error: " + exerciseName + " exercise not found!");
            return;
        }
        if (session.getExerciseSets().containsKey(exercise)) {
            System.out.println("Error: " + exerciseName + " already exists in session!");
            return;
        }

        session.addExercise(exercise, sets);

        EventLog.getInstance().logEvent(new Event("Added " + sets + "sets of " + exerciseName
                + " to " + sessionName + "!"));

        System.out.println("Added " + sets + "sets of " + exerciseName + " to " + sessionName + "!");
    }

    // GETTERS:

    // EFFECTS: returns the list of all exercises
    public List<Exercise> getExercises() {
        return exercises;
    }

    // EFFECTS: returns the list of all training sessions
    public List<TrainingSession> getSessions() {
        return sessions;
    }

}
