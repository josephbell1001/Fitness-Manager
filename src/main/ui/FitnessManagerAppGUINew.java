package ui;

import model.Exercise;
import model.FitnessManager;
import model.TrainingSession;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Dashboard-style GUI that matches the provided mockup while preserving all
 * behaviors from FitnessManagerAppGUI (create/edit exercises, sessions,
 * add-to-session, clear, view, save/load, quit w/ EventLog).
 *
 * Drop this file in src/main/ui and run.
 */
public class FitnessManagerAppGUINew extends JFrame {

    // ---------- Data / Persistence ----------
    private static final String JSON_STORE = "./data/fitness_manager.json";
    private final JsonWriter jsonWriter = new JsonWriter(JSON_STORE);
    private final JsonReader jsonReader = new JsonReader(JSON_STORE);
    private FitnessManager manager = new FitnessManager();

    // ---------- UI: top / left / main / footer ----------
    private JPanel topBar;
    private JPanel sideBar;
    private JPanel mainArea;          // CardLayout
    private CardLayout cards;

    // "Home" widgets
    private DefaultListModel<String> activityModel;
    private JList<String> activityList;
    private JLabel totalExercisesLbl;
    private JLabel totalSessionsLbl;
    private JLabel programsSavedLbl;
    private int programsSavedCount = 0;

    // Single reusable text area page (for simple views)
    private JTextArea outputArea;

    public FitnessManagerAppGUINew() {
        super("Fitness Manager");
        buildUI();
        setMinimumSize(new Dimension(1200, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                // mirror prior behavior: print event log then exit
                printEventLogToConsole();
                System.exit(0);
            }
        });
        setVisible(true);
    }

    // ---------- UI Construction ----------

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252)); // subtle light gray
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSideBar(), BorderLayout.WEST);
        add(buildMainArea(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        topBar = new JPanel(new BorderLayout());
        topBar.setBorder(new EmptyBorder(12, 16, 12, 16));
        topBar.setBackground(Color.WHITE);

        // Left: Title with small icons (best-effort loading from /ui/img)
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));

        JLabel leftIcon = new JLabel(loadScaledIcon("/ui/img/LeftBicep.png", 32, 25));
        JLabel title = new JLabel("  Fitness Manager  ");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel rightIcon = new JLabel(loadScaledIcon("/ui/img/RightBicep.png", 32, 25));

        left.add(leftIcon);
        left.add(title);
        left.add(rightIcon);

        // Right: Save / Load buttons
        JPanel right = new JPanel();
        right.setOpaque(false);
        JButton saveBtn = primaryButton("\uD83D\uDCBE  Save Program");
        JButton loadBtn = ghostButton("\uD83D\uDCC2  Load Program");
        saveBtn.addActionListener(e -> saveData());
        loadBtn.addActionListener(e -> {
            loadData();
            refreshHomeCounts();
            pushActivity("Loaded program from disk.");
        });
        right.add(saveBtn);
        right.add(Box.createHorizontalStrut(8));
        right.add(loadBtn);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);
        topBar.setBorder(new LineBorder(new Color(230, 232, 236)));
        return topBar;
    }

    private JPanel buildSideBar() {
        sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBorder(new EmptyBorder(16, 12, 16, 12));
        sideBar.setBackground(new Color(246, 248, 251));
        sideBar.setPreferredSize(new Dimension(260, 100));

        // Section: Exercise Management
        sideBar.add(sectionLabel("ACTIONS"));
        sideBar.add(groupLabel("EXERCISE MANAGEMENT"));

        JButton createExercise = leftNav("\u2795  Create Exercise");
        createExercise.addActionListener(e -> showCreateExerciseDialog());

        JButton editExercise = leftNav("\u270E  Edit Exercise");
        editExercise.addActionListener(e -> editExerciseDialog());

        JButton addToSession = leftNav("\uD83D\uDCCC  Add Exercise to Session");
        addToSession.addActionListener(e -> addExerciseToSessionDialog());

        sideBar.add(createExercise);
        sideBar.add(editExercise);
        sideBar.add(addToSession);

        sideBar.add(Box.createVerticalStrut(16));
        sideBar.add(groupLabel("SESSION MANAGEMENT"));

        JButton createSession = leftNav("\uD83D\uDCC5  Create Training Session");
        createSession.addActionListener(e -> showCreateSessionDialog());

        JButton clearSession = leftNav("\u274C  Clear Training Session");
        clearSession.addActionListener(e -> clearTrainingSessionDialog());

        JButton viewSessions = leftNav("\uD83D\uDCCB  View Sessions & Exercises");
        viewSessions.addActionListener(e -> showViewOptionsMenu());

        sideBar.add(createSession);
        sideBar.add(clearSession);
        sideBar.add(viewSessions);

        sideBar.add(Box.createVerticalStrut(16));
        sideBar.add(groupLabel("PROGRAM MANAGEMENT"));

        JButton saveBtn = leftNav("\uD83D\uDCBE  Save Program");
        saveBtn.addActionListener(e -> saveData());

        JButton loadBtn = leftNav("\uD83D\uDCC2  Load Program");
        loadBtn.addActionListener(e -> {
            loadData();
            refreshHomeCounts();
            pushActivity("Loaded program from disk.");
        });

        JButton quitBtn = leftNav("\u23F9\uFE0F  Quit Program");
        quitBtn.addActionListener(e -> {
            printEventLogToConsole();
            System.exit(0);
        });

        sideBar.add(saveBtn);
        sideBar.add(loadBtn);
        sideBar.add(Box.createVerticalStrut(8));
        sideBar.add(quitBtn);

        return sideBar;
    }

    private JPanel buildMainArea() {
        cards = new CardLayout();
        mainArea = new JPanel(cards);

        // HOME (Dashboard)
        JPanel home = new JPanel(new BorderLayout());
        home.setBorder(new EmptyBorder(16, 16, 16, 16));
        home.setBackground(new Color(248, 250, 252));

        // Welcome + two feature cards
        JPanel topRow = new JPanel(new GridLayout(1, 2, 16, 16));
        topRow.setOpaque(false);

        JPanel exercisesCard = bigCard(
                "\uD83D\uDCAA  Exercises",
                "Create and manage your exercise library with detailed instructions "
                        + "and muscle groups."
        );
        JButton startExercise = ghostButton("Start by creating your first exercise");
        startExercise.setHorizontalAlignment(SwingConstants.LEFT);
        startExercise.addActionListener(e -> showCreateExerciseDialog());
        exercisesCard.add(startExercise, BorderLayout.SOUTH);

        JPanel sessionsCard = bigCard(
                "\uD83D\uDCC5  Training Sessions",
                "Build structured workout sessions by combining exercises with sets and reps."
        );
        JButton planBtn = ghostButton("Plan your training schedule");
        planBtn.setHorizontalAlignment(SwingConstants.LEFT);
        planBtn.addActionListener(e -> showCreateSessionDialog());
        sessionsCard.add(planBtn, BorderLayout.SOUTH);

        topRow.add(exercisesCard);
        topRow.add(sessionsCard);

        // Recent Activity
        JPanel recent = new JPanel(new BorderLayout());
        recent.setOpaque(false);
        recent.setBorder(new EmptyBorder(16, 0, 0, 0));

        JLabel raTitle = new JLabel("Recent Activity");
        raTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        recent.add(raTitle, BorderLayout.NORTH);

        activityModel = new DefaultListModel<>();
        activityList = new JList<>(activityModel);
        activityList.setVisibleRowCount(6);
        activityList.setFixedCellHeight(22);
        JScrollPane activityScroll = new JScrollPane(activityList);
        activityScroll.setBorder(new LineBorder(new Color(230, 232, 236)));
        recent.add(activityScroll, BorderLayout.CENTER);

        // Counters row
        JPanel counters = new JPanel(new GridLayout(1, 3, 16, 16));
        counters.setOpaque(false);
        counters.setBorder(new EmptyBorder(16, 0, 0, 0));
        totalExercisesLbl = metricCard("0", "Total Exercises");
        totalSessionsLbl = metricCard("0", "Training Sessions");
        programsSavedLbl = metricCard(String.valueOf(programsSavedCount), "Programs Saved");
        counters.add(wrapMetric(totalExercisesLbl, "Total Exercises"));
        counters.add(wrapMetric(totalSessionsLbl, "Training Sessions"));
        counters.add(wrapMetric(programsSavedLbl, "Programs Saved"));

        // Assemble home
        home.add(buildWelcomeHeader(), BorderLayout.NORTH);
        home.add(topRow, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(recent, BorderLayout.CENTER);
        bottom.add(counters, BorderLayout.SOUTH);
        home.add(bottom, BorderLayout.SOUTH);

        // TEXT OUTPUT PAGE (for some views)
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JPanel textPage = new JPanel(new BorderLayout());
        textPage.setBorder(new EmptyBorder(16, 16, 16, 16));
        textPage.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        mainArea.add(home, "home");
        mainArea.add(textPage, "text");

        cards.show(mainArea, "home");
        return mainArea;
    }

    private JPanel buildWelcomeHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Welcome to Fitness Manager");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Select an action from the sidebar to get started");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(90, 96, 104));

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.add(title);
        texts.add(Box.createVerticalStrut(4));
        texts.add(subtitle);

        panel.add(texts, BorderLayout.WEST);
        return panel;
    }

    // ---------- Dialogs / Actions (functionality preserved) ----------

    private void showCreateExerciseDialog() {
        JTextField nameField = new JTextField();
        JTextField muscleField = new JTextField();
        JTextField weightField = new JTextField();
        JTextField repsField = new JTextField();

        Object[] fields = {
                "Name:", nameField,
                "Muscle Group:", muscleField,
                "Weight (lbs):", weightField,
                "Reps:", repsField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Create Exercise",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String muscle = muscleField.getText().trim();
            int weight = Integer.parseInt(weightField.getText().trim());
            int reps = Integer.parseInt(repsField.getText().trim());

            manager.createExercise(name, muscle, weight, reps);
            refreshHomeCounts();
            pushActivity("Created exercise: " + name);
            toast("Exercise '" + name + "' added.");
        } catch (NumberFormatException ex) {
            toastError("Invalid numbers for weight/reps.");
        }
    }

    private void editExerciseDialog() {
        if (manager.getExercises().isEmpty()) {
            toast("No exercises to edit.");
            return;
        }

        String[] names = manager.getExercises().stream().map(Exercise::getName).toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(
                this, "Select an exercise to edit:", "Edit Exercise",
                JOptionPane.PLAIN_MESSAGE, null, names, names[0]
        );
        if (choice == null) return;

        Exercise e = manager.findExerciseByName(choice);
        if (e == null) {
            toastError("Exercise not found.");
            return;
        }

        JTextField nameField = new JTextField(e.getName());
        JTextField muscleField = new JTextField(e.getTargetMuscle());
        JTextField weightField = new JTextField(String.valueOf(e.getWeight()));
        JTextField repsField = new JTextField(String.valueOf(e.getReps()));

        Object[] fields = {
                "New Name:", nameField,
                "New Muscle Group:", muscleField,
                "New Weight (lbs):", weightField,
                "New Reps:", repsField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Edit Exercise",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            e.setName(nameField.getText().trim());
            e.setTargetMuscle(muscleField.getText().trim());
            e.setWeight(Integer.parseInt(weightField.getText().trim()));
            e.setReps(Integer.parseInt(repsField.getText().trim()));
            pushActivity("Updated exercise: " + e.getName());
            toast("Exercise updated.");
        } catch (NumberFormatException ex) {
            toastError("Invalid numbers for weight/reps.");
        }
    }

    private void showCreateSessionDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter Training Session name:");
        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) {
            toastError("Invalid session name.");
            return;
        }
        manager.createSession(name);
        refreshHomeCounts();
        pushActivity("Created session: " + name);
        toast("Training session created.");
    }

    private void clearTrainingSessionDialog() {
        if (manager.getSessions().isEmpty()) {
            toast("No sessions to clear.");
            return;
        }

        String[] names = manager.getSessions().stream().map(TrainingSession::getName).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(
                this, "Select session to clear:", "Clear Session",
                JOptionPane.PLAIN_MESSAGE, null, names, names[0]
        );
        if (selected == null) return;

        TrainingSession s = manager.findSessionByName(selected);
        if (s != null) {
            s.clearSession();
            pushActivity("Cleared session: " + selected);
            toast("Session cleared.");
        }
    }

    private void addExerciseToSessionDialog() {
        if (manager.getExercises().isEmpty() || manager.getSessions().isEmpty()) {
            toast("Need at least 1 exercise and 1 session first.");
            return;
        }
        String[] sessions = manager.getSessions().stream().map(TrainingSession::getName).toArray(String[]::new);
        String[] exercises = manager.getExercises().stream().map(Exercise::getName).toArray(String[]::new);

        String session = (String) JOptionPane.showInputDialog(
                this, "Select session:", "Add Exercise to Session",
                JOptionPane.PLAIN_MESSAGE, null, sessions, sessions[0]
        );
        if (session == null) return;

        String exercise = (String) JOptionPane.showInputDialog(
                this, "Select exercise:", "Add Exercise to Session",
                JOptionPane.PLAIN_MESSAGE, null, exercises, exercises[0]
        );
        if (exercise == null) return;

        String setsStr = JOptionPane.showInputDialog(this, "Enter number of sets:");
        if (setsStr == null) return;

        try {
            int sets = Integer.parseInt(setsStr.trim());
            manager.addExerciseToSession(session, exercise, sets);
            pushActivity("Added " + exercise + " (" + sets + " sets) to " + session);
            toast("Exercise added to session.");
        } catch (NumberFormatException ex) {
            toastError("Invalid set number.");
        }
    }

    private void showViewOptionsMenu() {
        String[] options = { "View Exercise Details", "View Session Exercises", "View All Sessions & Exercises" };
        int choice = JOptionPane.showOptionDialog(
                this, "Select an option", "View Options",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]
        );
        switch (choice) {
            case 0 -> viewExerciseDetails();
            case 1 -> viewSessionExercises();
            case 2 -> viewAllExercisesAndSessions();
            default -> { }
        }
    }

    private void viewExerciseDetails() {
        if (manager.getExercises().isEmpty()) {
            toast("No exercises to view.");
            return;
        }
        String[] names = manager.getExercises().stream().map(Exercise::getName).toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(
                this, "Select exercise to view:", "Exercise Details",
                JOptionPane.PLAIN_MESSAGE, null, names, names[0]
        );
        if (choice == null) return;

        Exercise e = manager.findExerciseByName(choice);
        if (e == null) {
            toastError("Exercise not found.");
            return;
        }
        String text = "Exercise Details\n\n" +
                "Name: " + e.getName() + "\n" +
                "Muscle Group: " + e.getTargetMuscle() + "\n" +
                "Weight: " + e.getWeight() + " lbs\n" +
                "Reps: " + e.getReps() + "\n";
        outputArea.setText(text);
        cards.show(mainArea, "text");
    }

    private void viewSessionExercises() {
        if (manager.getSessions().isEmpty()) {
            toast("No sessions to view.");
            return;
        }
        String[] names = manager.getSessions().stream().map(TrainingSession::getName).toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(
                this, "Select session to view:", "Session Exercises",
                JOptionPane.PLAIN_MESSAGE, null, names, names[0]
        );
        if (choice == null) return;

        TrainingSession s = manager.findSessionByName(choice);
        if (s == null) {
            toastError("Session not found.");
            return;
        }

        StringBuilder sb = new StringBuilder("Exercises in '").append(choice).append("'\n\n");
        for (Map.Entry<Exercise, Integer> entry : s.getExerciseSets().entrySet()) {
            sb.append("- ").append(entry.getKey().getName())
              .append(" — ").append(entry.getValue()).append(" sets\n");
        }
        if (s.getExerciseSets().isEmpty()) sb.append("(No exercises yet)");
        outputArea.setText(sb.toString());
        cards.show(mainArea, "text");
    }

    private void viewAllExercisesAndSessions() {
        StringBuilder sb = new StringBuilder("All Exercises\n\n");
        for (Exercise e : manager.getExercises()) {
            sb.append("- ").append(e.getName())
              .append(" (").append(e.getTargetMuscle()).append(", ")
              .append(e.getWeight()).append(" lbs, ")
              .append(e.getReps()).append(" reps)\n");
        }
        sb.append("\nAll Training Sessions\n\n");
        for (TrainingSession s : manager.getSessions()) {
            sb.append("- ").append(s.getName())
              .append(" (").append(s.getExerciseSets().size()).append(" exercises)\n");
        }
        outputArea.setText(sb.toString());
        cards.show(mainArea, "text");
    }

    private void saveData() {
        try {
            jsonWriter.open();            // will also create ./data first run
            jsonWriter.write(manager);
            jsonWriter.close();
            programsSavedCount++;
            refreshHomeCounts();
            pushActivity("Saved program to disk.");
            toast("Saved to " + new File(JSON_STORE).getPath());
        } catch (FileNotFoundException e) {
            toastError("Unable to write to file: " + JSON_STORE);
        }
    }

    private void loadData() {
        try {
            manager = jsonReader.read();
            toast("Loaded from " + new File(JSON_STORE).getPath());
        } catch (IOException e) {
            toastError("Unable to read from file: " + JSON_STORE);
        }
    }

    private void printEventLogToConsole() {
        System.out.println("\nEvent Log:");
        for (model.Event ev : model.EventLog.getInstance()) {
            System.out.println(ev.toString());
        }
    }

    // ---------- Small helpers / styling ----------

    private void pushActivity(String text) {
        activityModel.add(0, "• " + text);
        if (activityModel.size() > 100) activityModel.removeElementAt(activityModel.size() - 1);
    }

    private void refreshHomeCounts() {
        totalExercisesLbl.setText(String.valueOf(manager.getExercises().size()));
        totalSessionsLbl.setText(String.valueOf(manager.getSessions().size()));
        programsSavedLbl.setText(String.valueOf(programsSavedCount));
    }

    private JPanel bigCard(String title, String body) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(230, 232, 236)));
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel b = new JLabel("<html><body style='width: 520px'>" + body + "</body></html>");
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setForeground(new Color(90, 96, 104));

        inner.add(t);
        inner.add(Box.createVerticalStrut(8));
        inner.add(b);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    private JLabel metricCard(String number, String label) {
        JLabel lbl = new JLabel(number, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 26));
        lbl.setBorder(new EmptyBorder(16, 0, 4, 0));
        return lbl;
    }

    private JPanel wrapMetric(JLabel numberLbl, String caption) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(new Color(230, 232, 236)));
        JLabel cap = new JLabel(caption, SwingConstants.CENTER);
        cap.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cap.setForeground(new Color(90, 96, 104));
        p.add(numberLbl, BorderLayout.CENTER);
        p.add(cap, BorderLayout.SOUTH);
        return p;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(new Color(119, 125, 134));
        l.setBorder(new EmptyBorder(0, 2, 8, 0));
        return l;
    }

    private JLabel groupLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(new Color(119, 125, 134));
        l.setBorder(new EmptyBorder(8, 4, 8, 4));
        return l;
    }

    private JButton leftNav(String text) {
        JButton b = new JButton(text);
        b.setFocusable(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        b.setBackground(Color.WHITE);
        b.setBorder(new LineBorder(new Color(230, 232, 236)));
        return b;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusable(false);
        b.setBackground(new Color(28, 100, 242));
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(8, 12, 8, 12));
        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFocusable(false);
        b.setBackground(Color.WHITE);
        b.setBorder(new LineBorder(new Color(210, 214, 220)));
        return b;
    }

    private void toast(String message) {
        JOptionPane.showMessageDialog(this, message, "Fitness Manager", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toastError(String message) {
        JOptionPane.showMessageDialog(this, message, "Fitness Manager", JOptionPane.ERROR_MESSAGE);
    }

    private ImageIcon loadScaledIcon(String resourcePath, int w, int h) {
        // Try classpath first (recommended). If null, try file path relative to project root.
        java.net.URL url = getClass().getResource(resourcePath);
        Image img = null;
        if (url != null) {
            img = new ImageIcon(url).getImage();
        } else {
            File f = new File("." + resourcePath);
            if (f.exists()) img = new ImageIcon(f.getAbsolutePath()).getImage();
        }
        if (img == null) return new ImageIcon(new BufferedImageStub(w, h));
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    /**
     * Tiny transparent image to avoid null icons if resources aren’t found.
     */
    private static class BufferedImageStub extends java.awt.image.BufferedImage {
        public BufferedImageStub(int w, int h) { super(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB); }
    }

    public static void main(String[] args) {
        // Optional: light system L&F for nicer look on Windows
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        new FitnessManagerAppGUINew();
    }
}
