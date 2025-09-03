package ui;

import model.Exercise;
import model.FitnessManager;
import model.TrainingSession;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

// Referenced from the TellerApp
// https://github.students.cs.ubc.ca/CPSC210/TellerApp

public class FitnessManagerApp {
    private static final String JSON_STORE = "./data/fitness_manager.json";

    private FitnessManager manager;
    private Scanner input;

    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECTS: runs the fitness manager application
    public FitnessManagerApp() {
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);

        runFitnessManager();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runFitnessManager() {
        boolean keepGoing = true;
        String command = null;

        init();

        while (keepGoing) {
            displayMenu();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("q")) {
                keepGoing = false;
                printEventLog();
            } else {
                processCommand(command);
            }
        }

        System.out.println("\nGoodbye!");
    }


    // MODIFIES: this
    // EFFECTS: processes user command
    @SuppressWarnings("methodlength")
    private void processCommand(String command) {
        if (command.equals("e")) {
            createExercise();
        } else if (command.equals("t")) {
            createSession();
        } else if (command.equals("a")) {
            addExerciseToSession();
        } else if (command.equals("r")) {
            removeExerciseFromSession();
        } else if (command.equals("c")) {
            clearExercisesFromSession();
        } else if (command.equals("m")) {
            modifyExercise();
        } else if (command.equals("l")) {
            listExercisesAndSessions();
        } else if (command.equals("v")) {
            viewSession();
        } else if (command.equals("s")) {
            viewExercise();
        } else if (command.equals("save")) {
            saveData();
        } else if (command.equals("load")) {
            loadData();
        } else {
            System.out.println("Selection not valid!");
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes accounts
    private void init() {
        manager = new FitnessManager();
        input = new Scanner(System.in);
    }

    // EFFECTS: displays menu of options to user
    private void displayMenu() {
        System.out.println("\nSelect from:");
        System.out.println("\te -> Create Exercise");
        System.out.println("\tt -> Create Training Session");
        System.out.println("\ta -> Add Exercise to Training Session");
        System.out.println("\tr -> Remove Exercise from Training Session");
        System.out.println("\tc -> Clear all Exercises from Training Session");
        System.out.println("\tm -> Modify an Exercise");
        System.out.println("\tl -> List all Exercises and Training Sessions");
        System.out.println("\tv -> View all Exercises and Sets in a Training Sessions");
        System.out.println("\ts -> See Target Muscle, Weight, and Reps for an Exercise");
        System.out.println("\tsave -> Save the programs current state");
        System.out.println("\tload -> Load the programs previously saved state");
        System.out.println("\tq -> Quit Application");
    }

    private void printEventLog() {
        System.out.println("\nEvent Log:");
        for (model.Event e : model.EventLog.getInstance()) {
            System.out.println(e.toString());
        }
    }    

    // MODIFIES: this
    // EFFECTS: allows user to create a new exercise
    private void createExercise() {
        input.nextLine();

        System.out.print("Enter Exercise name: ");
        String name = input.nextLine();

        System.out.print("Enter Target Muscle: ");
        String targetMuscle = input.nextLine();

        System.out.print("Enter Weight (lbs): ");
        int weight = input.nextInt();

        input.nextLine();
        System.out.print("Enter Reps: ");
        int reps = input.nextInt();
        input.nextLine();
        ;
        manager.createExercise(name, targetMuscle, weight, reps);
        System.out.println("Succesfully created " + name + " Exercise!");
    }

    // MODIFIES: this
    // EFFECTS: allows user to create a new training session
    private void createSession() {
        input.nextLine();

        System.out.print("Enter Training Session name: ");
        String name = input.nextLine();

        manager.createSession(name);
        System.out.print("Succesfully created " + name + " Training Session!");
    }

    // MODIFIES: this
    // EFFECTS: adds an existing exercise with a specified number of sets to an
    // existing training session
    private void addExerciseToSession() {
        input.nextLine();

        System.out.println("Enter Training Session name: ");
        String sessionName = input.nextLine();

        System.out.println("Enter Exercise name: ");
        String exerciseName = input.nextLine();

        System.out.println("Enter Set count: ");
        int sets = input.nextInt();

        manager.addExerciseToSession(sessionName, exerciseName, sets);
        System.out.print("Succesfully added " + exerciseName + " to " + sessionName + " for " + sets + " sets!");
    }

    // MODIFIES: this
    // EFFECTS: removes an exercise from a training session
    private void removeExerciseFromSession() {
        input.nextLine();

        System.out.println("Enter Training Session name: ");
        String sessionName = input.nextLine();

        System.out.println("Enter Exercise name: ");
        String exerciseName = input.nextLine();

        TrainingSession session = manager.findSessionByName(sessionName);
        Exercise exercise = manager.findExerciseByName(exerciseName);

        if (session == null) {
            System.out.println("Error: Invalid Training Session name entered!");
        } else if (exercise == null) {
            System.out.println("Error: Invalid Exercise name entered!");
        } else {
            session.removeExercise(exercise);
            System.out.println(exerciseName + " removed from " + sessionName + " !");
        }
    }

    // MODIFIES: this
    // EFFECTS: clear all exercises from a training session
    private void clearExercisesFromSession() {
        input.nextLine();

        System.out.println("Enter Training Session name: ");
        String sessionName = input.nextLine();

        TrainingSession session = manager.findSessionByName(sessionName);

        if (session == null) {
            System.out.println("Error: Invalid Training Session name entered!");
        } else {
            session.clearSession();
            System.out.println("Cleared all exercises from " + sessionName + " !");
        }
    }

    // MODIFIES: this
    // EFFECTS: allows user to modify target muscle, weight, and reps for an
    // exercise
    private void modifyExercise() {
        input.nextLine();

        System.out.println("Enter Exercise name: ");
        String name = input.nextLine();
        Exercise exercise = manager.findExerciseByName(name);

        if (exercise == null) {
            System.out.println("Error: Invalid Exercise name entered!");
        } else {
            System.out.println("Enter new Exercise name: ");
            exercise.setName(input.nextLine());

            System.out.println("Enter new Target Muscle: ");
            exercise.setTargetMuscle(input.nextLine());

            System.out.print("Enter new Weight (lbs): ");
            exercise.setWeight(Integer.parseInt(input.nextLine()));

            System.out.print("Enter new Rep count: ");
            exercise.setReps(Integer.parseInt(input.nextLine()));

            System.out.println("Succesfully modified Exercise!");
        }
    }

    // EFFECTS: lists all exercises and training sessions created
    private void listExercisesAndSessions() {
        System.out.println("\nExercises: ");
        for (Exercise e : manager.getExercises()) {
            System.out.println(e.getName());
        }

        System.out.println("\nTraining Sessions: ");
        for (TrainingSession s : manager.getSessions()) {
            System.out.println(s.getName());
        }
    }

    // EFFECTS: views exercises and their corresponding set counds in a training
    // session
    private void viewSession() {
        input.nextLine();

        System.out.println("Enter Training Session name: ");
        String sessionName = input.nextLine();
        TrainingSession session = manager.findSessionByName(sessionName);

        if (session == null) {
            System.out.println("Error: Invalid Training Session name entered!");
        } else {
            System.out.println("Exercises in " + sessionName + ":");
            for (Map.Entry<Exercise, Integer> entry : session.getExerciseSets().entrySet()) {
                Exercise exercise = entry.getKey();
                int sets = entry.getValue();
                System.out.println("- " + exercise.getName() + " for " + sets + " sets");
            }
        }
    }

    // EFFECTS: views an exercises targeting muscle, weight, and reps
    private void viewExercise() {
        input.nextLine();

        System.out.println("Enter Exercise name: ");
        String name = input.nextLine();
        Exercise exercise = manager.findExerciseByName(name);

        if (exercise == null) {
            System.out.println("Error: Invalid Exercise name entered!");
        } else {
            System.out.println("Name: " + name + ", Target Muscle: " + exercise.getTargetMuscle() + ", Weight: "
                    + exercise.getWeight() + "lbs, Reps: " + exercise.getReps());
        }
    }

    // EFFECTS: saves the current state of the FitnessManager to file
    private void saveData() {
        try {
            jsonWriter.open();
            jsonWriter.write(manager);
            jsonWriter.close();
            System.out.println("Saved application state to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads the previously saved state of the FitnessManager from file
    private void loadData() {
        try {
            manager = jsonReader.read();
            System.out.println("Loaded application state from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }

}
