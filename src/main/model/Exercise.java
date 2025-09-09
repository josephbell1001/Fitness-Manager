package model;

import org.json.JSONObject;

import persistence.Writable;

// Represents an exercise having a name, target muscle (main muscle group that the exercise is
// meant to target), weight (in pounds (lbs)), and reps (repetitions the exercise should be done for)
public class Exercise implements Writable {
    private String name; // exercises name
    private String targetMuscle; // targeted muscle group
    private int weight; // 0 if bodyweight
    private int reps; // reptitions per set

    // REQUIRES: reps > 0 & weight >= 0
    // EFFECTS: constructs an Exercise with a name, targetMuscle, weight, and reps
    public Exercise(String name, String targetMuscle, int weight, int reps) {
        this.name = name;
        this.targetMuscle = targetMuscle;
        this.weight = weight;
        this.reps = reps;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("targetMuscle", targetMuscle);
        json.put("weight", weight);
        json.put("reps", reps);
        return json;
    }

    // GETTERS:

    // EFFECTS: returns the name of the exercise
    public String getName() {
        return name;
    }

    // EFFECTS: returns the target muscle
    public String getTargetMuscle() {
        return targetMuscle;
    }

    // EFFECTS: returns the weight for the exercise
    public int getWeight() {
        return weight;
    }

    // EFFECTS: returns the reps per set of the exercise
    public int getReps() {
        return reps;
    }

    // SETTERS:

    // MODIFIES: this
    // EFFECTS: sets the exercise name
    public void setName(String name) {
        this.name = name;

        EventLog.getInstance().logEvent(new Event("Changed exercise name to " + name + "!"));
    }

    // MODIFIES: this
    // EFFECTS: sets the targetMuscle
    public void setTargetMuscle(String targetMuscle) {
        this.targetMuscle = targetMuscle;

        EventLog.getInstance().logEvent(new Event("Changed exercise target muscle to " + targetMuscle + "!"));
    }

    // REQUIRES: weight >= 0
    // MODIFIES: this
    // EFFECTS: sets the weight for the exercise
    public void setWeight(int weight) {
        this.weight = weight;

        EventLog.getInstance().logEvent(new Event("Changed exercise weight to " + weight + "lbs!"));
    }

    // REQUIRES: reps > 0
    // MODIFIES: this
    // EFFECTS: sets the number of reps for the exercise
    public void setReps(int reps) {
        this.reps = reps;

        EventLog.getInstance().logEvent(new Event("Changed exercise reps to " + reps + " per set!"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Exercise exercise = (Exercise) o;
        return weight == exercise.weight
                && reps == exercise.reps
                && name.equals(exercise.name)
                && targetMuscle.equals(exercise.targetMuscle);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + targetMuscle.hashCode();
        result = 31 * result + weight;
        result = 31 * result + reps;
        return result;
    }

}
