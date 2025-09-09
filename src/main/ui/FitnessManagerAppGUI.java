package ui;

import model.*;
import persistence.*;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;


public class FitnessManagerAppGUI extends JFrame {
    private static final String JSON_STORE = "./data/fitness_manager.json";
    private FitnessManager manager;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private JTextArea displayArea;

    public FitnessManagerAppGUI() {
        super("Fitness Manager");
        manager = new FitnessManager();
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);

        initializeGraphics();
    }

    private void initializeGraphics() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        createTitlePanel();
        createDisplayArea();
        createButtonPanel();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ImageIcon leftIcon = new ImageIcon("/ui/img/LeftBicep.png");
        ImageIcon rightIcon = new ImageIcon("/ui/img/LeftRicep.png");

        JLabel leftLabel = new JLabel(leftIcon);
        JLabel rightLabel = new JLabel(rightIcon);

        JLabel titleLabel = new JLabel("Fitness Manager");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));

        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(leftLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0))); // 10px space
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0))); // 10px space
        titlePanel.add(rightLabel);
        titlePanel.add(Box.createHorizontalGlue());

        add(titlePanel, BorderLayout.NORTH);
    }

    private void createDisplayArea() {
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    @SuppressWarnings("methodlength")
    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton createExerciseButton = new JButton("Create Exercise");
        JButton editExerciseButton = new JButton("Edit Exercise");
        JButton addExerciseToSessionButton = new JButton("Add Exercise to Session");

        JButton createSessionButton = new JButton("Create Training Session");
        JButton clearSessionButton = new JButton("Clear Training Session");
        JButton viewSessionsAndExercisesButton = new JButton("View Sessions and Exercises");

        JButton saveButton = new JButton("Save Program");
        JButton loadButton = new JButton("Load Program");
        JButton quitButton = new JButton("Quit Program");

        buttonPanel.add(createExerciseButton);
        buttonPanel.add(editExerciseButton);
        buttonPanel.add(addExerciseToSessionButton);

        buttonPanel.add(createSessionButton);
        buttonPanel.add(clearSessionButton);
        buttonPanel.add(viewSessionsAndExercisesButton);

        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(quitButton);

        createExerciseButton.addActionListener(e -> showExerciseCreationMenu());
        editExerciseButton.addActionListener(e -> editExercise());
        addExerciseToSessionButton.addActionListener(e -> addExerciseToSession());

        createSessionButton.addActionListener(e -> showSessionCreationMenu());
        clearSessionButton.addActionListener(e -> clearTrainingSession());
        viewSessionsAndExercisesButton.addActionListener(e -> showViewOptionsMenu());

        saveButton.addActionListener(e -> saveData());
        loadButton.addActionListener(e -> loadData());
        quitButton.addActionListener(e -> {
            printEventLog();
            System.exit(0);
        });

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void printEventLog() {
        System.out.println("\nEvent Log:");
        for (model.Event e : model.EventLog.getInstance()) {
            System.out.println(e.toString());
        }
    }
    

    @SuppressWarnings("methodlength")
    private void showExerciseCreationMenu() {
        JFrame exerciseFrame = new JFrame("Create Exercise");
        exerciseFrame.setSize(400, 300);
        exerciseFrame.setLayout(new GridLayout(5, 2));

        JTextField nameField = new JTextField();
        JTextField muscleField = new JTextField();
        JTextField weightField = new JTextField();
        JTextField repsField = new JTextField();
        JButton submitButton = new JButton("Add Exercise");

        exerciseFrame.add(new JLabel("Name:"));
        exerciseFrame.add(nameField);
        exerciseFrame.add(new JLabel("Muscle Group:"));
        exerciseFrame.add(muscleField);
        exerciseFrame.add(new JLabel("Weight (lbs):"));
        exerciseFrame.add(weightField);
        exerciseFrame.add(new JLabel("Reps:"));
        exerciseFrame.add(repsField);
        exerciseFrame.add(submitButton);

        submitButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String muscle = muscleField.getText();
                int weight = Integer.parseInt(weightField.getText());
                int reps = Integer.parseInt(repsField.getText());
                manager.createExercise(name, muscle, weight, reps);
                displayArea.setText("Exercise '" + name + "' added successfully.");
                exerciseFrame.dispose();
            } catch (NumberFormatException ex) {
                displayArea.setText("Invalid input. Please enter valid numbers.");
            }
        });

        exerciseFrame.setLocationRelativeTo(this);
        exerciseFrame.setVisible(true);
    }

    @SuppressWarnings("methodlength")
    private void editExercise() {
        if (manager.getExercises().isEmpty()) {
            displayArea.setText("No exercises to edit.");
            return;
        }

        String[] exerciseNames = manager.getExercises().stream()
                .map(Exercise::getName)
                .toArray(String[]::new);

        String exerciseName = (String) JOptionPane.showInputDialog(
                this, "Select an exercise to edit:", "Edit Exercise",
                JOptionPane.PLAIN_MESSAGE, null, exerciseNames, exerciseNames[0]);

        if (exerciseName == null) {
            displayArea.setText("Edit cancelled.");
            return;
        }

        Exercise exercise = manager.findExerciseByName(exerciseName);

        if (exercise == null) {
            displayArea.setText("Exercise not found.");
            return;
        }

        JTextField newNameField = new JTextField(exercise.getName());
        JTextField newMuscleField = new JTextField(exercise.getTargetMuscle());
        JTextField newWeightField = new JTextField(String.valueOf(exercise.getWeight()));
        JTextField newRepsField = new JTextField(String.valueOf(exercise.getReps()));

        Object[] fields = {
                "New Name:", newNameField,
                "New Muscle Group:", newMuscleField,
                "New Weight (lbs):", newWeightField,
                "New Reps:", newRepsField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Edit Exercise", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                exercise.setName(newNameField.getText());
                exercise.setTargetMuscle(newMuscleField.getText());
                exercise.setWeight(Integer.parseInt(newWeightField.getText()));
                exercise.setReps(Integer.parseInt(newRepsField.getText()));
                displayArea.setText("Exercise updated successfully.");
            } catch (NumberFormatException ex) {
                displayArea.setText("Invalid input. Please enter valid numbers.");
            }
        }
    }

    private void showSessionCreationMenu() {
        String name = JOptionPane.showInputDialog(this, "Enter Training Session name:");
        if (name != null && !name.trim().isEmpty()) {
            manager.createSession(name);
            displayArea.setText("Training session '" + name + "' created successfully.");
        } else {
            displayArea.setText("Invalid session name.");
        }
    }

    @SuppressWarnings("methodlength")
    private void addExerciseToSession() {
        if (manager.getExercises().isEmpty() || manager.getSessions().isEmpty()) {
            displayArea.setText("Need at least 1 exercise and 1 session to add.");
            return;
        }

        String[] sessionNames = manager.getSessions().stream()
                .map(TrainingSession::getName)
                .toArray(String[]::new);
        String[] exerciseNames = manager.getExercises().stream()
                .map(Exercise::getName)
                .toArray(String[]::new);

        String session = (String) JOptionPane.showInputDialog(this, "Select session:",
                "Add Exercise to Session", JOptionPane.PLAIN_MESSAGE, null, sessionNames, sessionNames[0]);
        if (session == null) {
            displayArea.setText("Action cancelled.");
            return;
        }

        String exercise = (String) JOptionPane.showInputDialog(this, "Select exercise:",
                "Add Exercise to Session", JOptionPane.PLAIN_MESSAGE, null, exerciseNames, exerciseNames[0]);
        if (exercise == null) {
            displayArea.setText("Action cancelled.");
            return;
        }

        String setsStr = JOptionPane.showInputDialog(this, "Enter number of sets:");
        if (setsStr == null) {
            displayArea.setText("Add cancelled.");
            return;
        }

        try {
            int sets = Integer.parseInt(setsStr);
            manager.addExerciseToSession(session, exercise, sets);
            displayArea.setText("Exercise added to session successfully.");
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid set number. Add cancelled.");
        }
    }

    private void clearTrainingSession() {
        if (manager.getSessions().isEmpty()) {
            displayArea.setText("No sessions to clear.");
            return;
        }

        String[] sessionNames = manager.getSessions().stream()
                .map(TrainingSession::getName)
                .toArray(String[]::new);

        String selectedSession = (String) JOptionPane.showInputDialog(this, "Select session to clear:",
                "Clear Session", JOptionPane.PLAIN_MESSAGE, null, sessionNames, sessionNames[0]);

        if (selectedSession == null) {
            displayArea.setText("Clear cancelled.");
            return;
        }

        TrainingSession session = manager.findSessionByName(selectedSession);
        session.clearSession();
        displayArea.setText("Session cleared.");
    }

    private void showViewOptionsMenu() {
        String[] options = { "View Exercise Details", "View Session Exercises", "View All Sessions and Exercises" };
        int choice = JOptionPane.showOptionDialog(this, "Select an option", "View Options",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0:
                viewExerciseDetails();
                break;
            case 1:
                viewSessionExercises();
                break;
            case 2:
                viewAllExercisesAndSessions();
                break;
            default:
                break;
        }

    }

    private void viewExerciseDetails() {
        if (manager.getExercises().isEmpty()) {
            displayArea.setText("No exercises to view.");
            return;
        }

        String[] exerciseNames = manager.getExercises().stream()
                .map(Exercise::getName)
                .toArray(String[]::new);

        String exercise = (String) JOptionPane.showInputDialog(this, "Select exercise to view:",
                "Exercise Details", JOptionPane.PLAIN_MESSAGE, null, exerciseNames, exerciseNames[0]);

        if (exercise == null) {
            displayArea.setText("View cancelled.");
            return;
        }

        Exercise e = manager.findExerciseByName(exercise);
        displayArea.setText("Exercise Details:\n" 
                + "Name: " + e.getName() + "\n" 
                + "Muscle Group: " + e.getTargetMuscle() + "\n" 
                + "Weight: " + e.getWeight() + " lbs\n" 
                + "Reps: " + e.getReps());
    }

    private void viewSessionExercises() {
        if (manager.getSessions().isEmpty()) {
            displayArea.setText("No sessions to view.");
            return;
        }

        String[] sessionNames = manager.getSessions().stream()
                .map(TrainingSession::getName)
                .toArray(String[]::new);

        String session = (String) JOptionPane.showInputDialog(this, "Select session to view:",
                "Session Exercises", JOptionPane.PLAIN_MESSAGE, null, sessionNames, sessionNames[0]);

        if (session == null) {
            displayArea.setText("View cancelled.");
            return;
        }

        TrainingSession s = manager.findSessionByName(session);
        StringBuilder sb = new StringBuilder("Exercises in session:\n");

        for (Map.Entry<Exercise, Integer> entry : s.getExerciseSets().entrySet()) {
            sb.append(entry.getKey().getName()).append(" - ").append(entry.getValue()).append(" sets\n");
        }

        displayArea.setText(sb.length() > 0 ? sb.toString() : "No exercises in session.");
    }

    private void viewAllExercisesAndSessions() {
        StringBuilder sb = new StringBuilder("All Exercises:\n");
        for (Exercise exercise : manager.getExercises()) {
            sb.append("- ").append(exercise.getName()).append(" (").append(exercise.getTargetMuscle())
                    .append(", ").append(exercise.getWeight()).append(" lbs, ").append(exercise.getReps())
                    .append(" reps)\n");
        }

        sb.append("\nAll Training Sessions:\n");
        for (TrainingSession session : manager.getSessions()) {
            sb.append("- ").append(session.getName()).append(" (").append(session.getExerciseSets().size())
                    .append(" exercises)\n");
        }

        displayArea.setText(sb.toString());
    }

    private void saveData() {
        try {
            jsonWriter.open();
            jsonWriter.write(manager);
            jsonWriter.close();
            displayArea.setText("Data saved successfully.");
        } catch (FileNotFoundException e) {
            displayArea.setText("Error: Unable to save data.");
        }
    }

    private void loadData() {
        try {
            manager = jsonReader.read();
            displayArea.setText("Data loaded successfully.");
        } catch (IOException e) {
            displayArea.setText("Error: Unable to load data.");
        }
    }

    public static void main(String[] args) {
        new FitnessManagerAppGUI();
    }
}
