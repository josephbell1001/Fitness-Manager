package persistence;

import model.Exercise;
import model.FitnessManager;
import model.TrainingSession;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        assertThrows(IOException.class, reader::read);
    }

    @Test
    void testReaderEmptyFitnessManager() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyFitnessManager.json");
        try {
            FitnessManager loaded = reader.read();
            assertEquals(0, loaded.getExercises().size());
            assertEquals(0, loaded.getSessions().size());
        } catch (IOException e) {
            fail("Couldn't read empty fitness manager");
        }
    }

    @Test
    void testReaderGeneralFitnessManager() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralFitnessManager.json");
        try {
            FitnessManager loaded = reader.read();

            assertEquals(2, loaded.getExercises().size());
            checkExercise("Incline Bench", "Chest", 100, 16, loaded.getExercises().get(0));
            checkExercise("Squat", "Legs", 315, 6, loaded.getExercises().get(1));

            List<TrainingSession> sessions = loaded.getSessions();
            assertEquals(1, sessions.size());

            TrainingSession session = sessions.get(0);
            assertEquals("Leg Day", session.getName());
            assertEquals(1, session.getExerciseSets().size());

            Exercise squat = loaded.getExercises().get(1);
            assertTrue(session.getExerciseSets().containsKey(squat));
            assertEquals(3, session.getExerciseSets().get(squat));

        } catch (IOException e) {
            fail("Couldn't read general fitness manager");
        }
    }

    @Test
    void testReaderEmptyArrays() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyArrays.json");
        try {
            FitnessManager loaded = reader.read();
            assertEquals(0, loaded.getExercises().size());
            assertEquals(0, loaded.getSessions().size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}
