package persistence;

import model.Exercise;

import static org.junit.jupiter.api.Assertions.*;

public class JsonTest {
    protected void checkExercise(String name, String targetMuscle, int weight, int reps, Exercise exercise) {
        assertEquals(name, exercise.getName());
        assertEquals(targetMuscle, exercise.getTargetMuscle());
        assertEquals(weight, exercise.getWeight());
        assertEquals(reps, exercise.getReps());
    }
}
