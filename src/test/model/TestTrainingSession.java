package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestTrainingSession {
    private TrainingSession testTrainingSessionLegs;
    private TrainingSession testTrainingSessionFull;
    private Exercise benchPress;
    private Exercise deadlift;
    private Exercise squat;
    private Exercise shoulderPress;
    private Exercise dip;
    private Exercise hackSquat;
    private Exercise bicepCurl;
    private Exercise calfRaise;

    @BeforeEach
    void runBefore() {
        testTrainingSessionLegs = new TrainingSession("Leg Day");
        testTrainingSessionFull = new TrainingSession("Full Body");

        benchPress = new Exercise("Bench Press", "Chest", 185, 8);
        deadlift = new Exercise("Deadlift", "Back", 315, 6);
        squat = new Exercise("Barbell Squat", "Quads", 405, 6);
        shoulderPress = new Exercise("Dumbell Should Press", "Front Delt", 80, 12);
        dip = new Exercise("Dip", "Tricep", 0, 20);
        hackSquat = new Exercise("Hack Squat", "Glutes", 225, 16);
        bicepCurl = new Exercise("Bicep Curls", "Biceps", 45, 10);
        calfRaise = new Exercise("Calf Raise", "Calves", 315, 12);

        testTrainingSessionFull.addExercise(benchPress, 1);
        testTrainingSessionFull.addExercise(deadlift, 1);
        testTrainingSessionFull.addExercise(squat, 1);
        testTrainingSessionFull.addExercise(shoulderPress, 1);
        testTrainingSessionFull.addExercise(dip, 1);
        testTrainingSessionFull.addExercise(hackSquat, 1);
        testTrainingSessionFull.addExercise(bicepCurl, 1);
        testTrainingSessionFull.addExercise(calfRaise, 1);
    }

    @Test
    void testConstructor() {
        assertEquals("Leg Day", testTrainingSessionLegs.getName());
        assertEquals(0, testTrainingSessionLegs.getExerciseSets().size());
    }

    @Test
    void testAddExercises() {
        testTrainingSessionLegs.addExercise(squat, 3);
        testTrainingSessionLegs.addExercise(hackSquat, 2);
        testTrainingSessionLegs.addExercise(calfRaise, 1);

        assertEquals(3, testTrainingSessionLegs.getExerciseSets().size());
        assertEquals(3, testTrainingSessionLegs.getExerciseSets().get(squat)); //key and value
        assertEquals(2, testTrainingSessionLegs.getExerciseSets().get(hackSquat));
        assertEquals(1, testTrainingSessionLegs.getExerciseSets().get(calfRaise));


        testTrainingSessionLegs.addExercise(squat, 10); //adding duplicate exercise
        assertEquals(3, testTrainingSessionLegs.getExerciseSets().size()); //session size remaions
        assertEquals(3, testTrainingSessionLegs.getExerciseSets().get(squat)); //exercise sets remain
    }

    @Test
    void testRemoveExercises() {
        assertEquals(8, testTrainingSessionFull.getExerciseSets().size());

        testTrainingSessionFull.removeExercise(deadlift);
        testTrainingSessionFull.removeExercise(bicepCurl);
        assertEquals(6, testTrainingSessionFull.getExerciseSets().size());

        testTrainingSessionFull.removeExercise(bicepCurl); //already removed
        assertEquals(6, testTrainingSessionFull.getExerciseSets().size());
    }


    @Test
    void testClearSession() {
        assertFalse(testTrainingSessionFull.getExerciseSets().isEmpty());

        testTrainingSessionFull.clearSession();
        assertTrue(testTrainingSessionFull.getExerciseSets().isEmpty());
    }

    @Test
    void testSetTrainingSessionName() {
        assertEquals("Full Body", testTrainingSessionFull.getName());

        testTrainingSessionFull.setName("Full Body Day");
        assertEquals("Full Body Day", testTrainingSessionFull.getName());
    }

}
