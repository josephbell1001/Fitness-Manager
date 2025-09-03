package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestExercise {
    private Exercise testExercise;

    @BeforeEach
    void runBefore() {
        testExercise = new Exercise("Curl", "Bicep", 30, 12);
    }

    @Test
    void testConstructor() {
        assertEquals("Curl", testExercise.getName());
        assertEquals("Bicep", testExercise.getTargetMuscle());
        assertEquals(30, testExercise.getWeight());
        assertEquals(12, testExercise.getReps());
    }

    @Test
    void testSetters() {
        testExercise.setName("Forearm-Curl");
        testExercise.setTargetMuscle("Forearm");
        testExercise.setWeight(15);
        testExercise.setReps(20);

        assertEquals("Forearm-Curl", testExercise.getName());
        assertEquals("Forearm", testExercise.getTargetMuscle());
        assertEquals(15, testExercise.getWeight());
        assertEquals(20, testExercise.getReps());
    }
}
