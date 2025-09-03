package persistence;

import model.Exercise;

import static org.junit.jupiter.api.Assertions.*;

// JsonReader class referenced from the WorkRoomApp
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

public class JsonTest {
    protected void checkExercise(String name, String targetMuscle, int weight, int reps, Exercise exercise) {
        assertEquals(name, exercise.getName());
        assertEquals(targetMuscle, exercise.getTargetMuscle());
        assertEquals(weight, exercise.getWeight());
        assertEquals(reps, exercise.getReps());
    }
}
