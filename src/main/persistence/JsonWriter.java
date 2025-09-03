package persistence;

import model.FitnessManager;
import org.json.JSONObject;

import java.io.*;

// JsonWriter class referenced from the WorkRoomApp
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

// Represents a writer that writes JSON representation of FitnessManager to file
public class JsonWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String destination; // File path for JSON output

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if file cannot be opened
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of FitnessManager to file
    public void write(FitnessManager fm) {
        JSONObject json = fm.toJson();
        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}
