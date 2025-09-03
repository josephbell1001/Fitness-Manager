package persistence;

import model.Exercise;
import model.FitnessManager;
import model.TrainingSession;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest extends JsonTest {

    @Test
    void testWriterInvalidFile() {
        assertThrows(IOException.class, () -> {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
        });
    }

    @Test
    void testWriterEmptyFitnessManager() {
        try {
            FitnessManager manager = new FitnessManager();
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyFitnessManager.json");
            writer.open();
            writer.write(manager);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyFitnessManager.json");
            FitnessManager loaded = reader.read();
            assertEquals(0, loaded.getExercises().size());
            assertEquals(0, loaded.getSessions().size());

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    @SuppressWarnings("methodlength")
    void testWriterGeneralFitnessManager() {
        try {
            FitnessManager manager = new FitnessManager();
            Exercise e1 = new Exercise("Bench Press", "Chest", 185, 10);
            Exercise e2 = new Exercise("Squat", "Legs", 225, 8);
            manager.getExercises().add(e1);
            manager.getExercises().add(e2);

            TrainingSession session = new TrainingSession("Leg Day");
            session.getExerciseSets().put(e2, 4);
            manager.getSessions().add(session);

            JsonWriter writer = new JsonWriter("./data/testWriterGeneralFitnessManager.json");
            writer.open();
            writer.write(manager);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralFitnessManager.json");
            FitnessManager loaded = reader.read();
            assertEquals(2, loaded.getExercises().size());
            checkExercise("Bench Press", "Chest", 185, 10, loaded.getExercises().get(0));
            checkExercise("Squat", "Legs", 225, 8, loaded.getExercises().get(1));

            assertEquals(1, loaded.getSessions().size());
            TrainingSession loadedSession = loaded.getSessions().get(0);
            assertEquals("Leg Day", loadedSession.getName());
            assertTrue(loadedSession.getExerciseSets().containsKey(e2));
            assertEquals(4, loadedSession.getExerciseSets().get(e2));

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testFitnessManagerToJson() {
        FitnessManager manager = new FitnessManager();
        Exercise ex = new Exercise("Lunges", "Legs", 135, 12);
        manager.getExercises().add(ex);
        assertNotNull(manager.toJson());
    }

    @Test
    void testTrainingSessionToJson() {
        TrainingSession session = new TrainingSession("Push Day");
        Exercise ex = new Exercise("Push-up", "Chest", 0, 20);
        session.addExercise(ex, 5);
        assertNotNull(session.toJson());
    }

    @Test
    void testExerciseToJson() {
        Exercise ex = new Exercise("Pull-up", "Back", 0, 15);
        assertNotNull(ex.toJson());
    }
}
