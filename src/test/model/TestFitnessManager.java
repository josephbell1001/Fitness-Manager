package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestFitnessManager {
    private FitnessManager testManager;

    @BeforeEach
    void runBefore() {
        testManager = new FitnessManager();
    }

    @Test
    void testConstructor() {
        assertEquals(0, testManager.getExercises().size());
        assertEquals(0, testManager.getSessions().size());

    }

    @Test
    void testCreateExercise() {
        Exercise pushUp = testManager.createExercise("Push Up", "Chest", 0, 50);
        Exercise chinUp = testManager.createExercise("Chin Up", "Bicep", 0, 16);
        assertEquals(2, testManager.getExercises().size());
        assertTrue(testManager.getExercises().contains(pushUp));
        assertTrue(testManager.getExercises().contains(chinUp));
    }

    @Test
    void testCreateTrainingSession() {
        TrainingSession session = testManager.createSession("Calistenics Workout");

        assertEquals(1, testManager.getSessions().size());
        assertTrue(testManager.getSessions().contains(session));
    }

    @Test
    void testFindExerciseByName() {
        assertNull(testManager.findExerciseByName("Box Jump"));

        testManager.createExercise("Box Jump", "Legs", 15, 10);
        assertNotNull(testManager.findExerciseByName("Box Jump"));

        assertNull(testManager.findExerciseByName("Bench Press"));
    }

    @Test
    void testFindSessionByName() {
        assertNull(testManager.findSessionByName("Pull Day"));
        assertNull(testManager.findSessionByName("Push Day"));
        assertNull(testManager.findSessionByName("Leg Day"));

        testManager.createSession("Pull Day");
        testManager.createSession("Push Day");
        testManager.createSession("Leg Day");
        assertNotNull(testManager.findSessionByName("Pull Day"));
        assertNotNull(testManager.findSessionByName("Push Day"));
        assertNotNull(testManager.findSessionByName("Leg Day"));
        
        assertNull(testManager.findSessionByName("Full Body Day"));
    }

    @Test
    void testAddExerciseToSession() {
        testManager.createExercise("Tricep Pushdown", "Tricep", 180, 12); //make exercises
        testManager.createExercise("Weighted Dip", "Tricep", 50, 12);
        testManager.createExercise("Preacher Curl", "Bicep", 90, 12);  
        testManager.createSession("Arm Day"); //make session

        testManager.addExerciseToSession("Arm Day", "Tricep Pushdown", 2);
        testManager.addExerciseToSession("Arm Day", "Weighted Dip", 2);
        testManager.addExerciseToSession("Arm Day", "Preacher Curl", 3);

        testManager.addExerciseToSession("Arm Day", "Squat", 3); //exercise doesnt exist
        testManager.addExerciseToSession("Arm Day", "Preacher Curl", 3); //duplicate exercise
        testManager.addExerciseToSession("Leg Day", "Tricep Pushdown", 2); //session doesnt exist


        assertNotNull(testManager.findSessionByName("Arm Day"));            //test sessions & exercises that should
        assertNotNull(testManager.findExerciseByName("Tricep Pushdown"));   //be added are, and ones that shouldn't
        assertNotNull(testManager.findExerciseByName("Weighted Dip"));      //are not added
        assertNotNull(testManager.findExerciseByName("Preacher Curl"));
        assertNull(testManager.findSessionByName("Leg Day")); 
        assertNull(testManager.findExerciseByName("Squat"));

    }
}
