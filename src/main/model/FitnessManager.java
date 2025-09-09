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

    // MODIFIES: this
// EFFECTS: deletes an exercise by name, removing it from the library AND all sessions.
//          returns true if deleted; false if not found.
public boolean deleteExercise(String name) {
    Exercise e = findExerciseByName(name);
    if (e == null) {
        System.out.println("Error: exercise '" + name + "' not found!");
        return false;
    }

    // remove from master list
    boolean removedFromLibrary = exercises.remove(e);

    // remove from all sessions (only if present to avoid misleading logs)
    for (TrainingSession s : sessions) {
        if (s.getExerciseSets().containsKey(e)) {
            s.removeExercise(e); // TrainingSession logs its own event
        }
    }

    if (removedFromLibrary) {
        EventLog.getInstance().logEvent(new Event("Deleted exercise " + e.getName() + " from library and all sessions!"));
    }
    return removedFromLibrary;
}

    // MODIFIES: this
    // EFFECTS: removes a specific exercise from a particular session.
    //          returns true if the exercise existed in the session and was removed.
    public boolean removeExerciseFromSession(String sessionName, String exerciseName) {
        TrainingSession s = findSessionByName(sessionName);
        Exercise e = findExerciseByName(exerciseName);
        if (s == null || e == null) {
            System.out.println("Error: session or exercise not found!");
            return false;
        }
        if (!s.getExerciseSets().containsKey(e)) {
            System.out.println("Info: exercise not present in session.");
            return false;
        }

        s.removeExercise(e); // TrainingSession logs its own event
        EventLog.getInstance().logEvent(new Event("Removed " + e.getName() + " from session " + s.getName() + "."));
        return true;
    }


}
