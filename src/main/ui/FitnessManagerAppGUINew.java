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
import javax.swing.border.CompoundBorder;


public class FitnessManagerAppGUINew extends JFrame {

    // ---------- Data / Persistence ----------
    private static final String JSON_STORE = "./data/fitness_manager.json";
    private final JsonWriter jsonWriter = new JsonWriter(JSON_STORE);
    private final JsonReader jsonReader = new JsonReader(JSON_STORE);
    private FitnessManager manager = new FitnessManager();


    // ---------- UI: top / left / main ----------
    private JPanel topBar;
    private JPanel sideBar;
    private JPanel mainArea;          // CardLayout
    private CardLayout cards;
    

    // Compact list styling
    private static final int COMPACT_ROW_HEIGHT = 28;   // fixed row height
    private static final int COMPACT_HPAD = 8;          // left/right padding
    private static final Font ROW_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Insets BTN_INSETS = new Insets(2, 8, 2, 8); // compact button padding

    // max chars for names shown in list rows
    private static final int MAX_NAME_LEN = 10;
    private String ellipsize(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(0, max - 3)) + "...";
    }

    // Dashboard widgets
    private DefaultListModel<String> activityModel;
    private JList<String> activityList;
    private JLabel totalExercisesLbl;
    private JLabel totalSessionsLbl;
    private JLabel programsSavedLbl;
    private int programsSavedCount = 0;

    // dynamic list containers inside the two big cards
    private JPanel exercisesListPanel;   // vertical list of exercise rows
    private JPanel sessionsListPanel;    // vertical list of session rows

    // Session detail page
    private JPanel sessionDetailPage;    // card for a single session view
    private JLabel sessionDetailTitle;
    private JPanel sessionDetailList;
    private JPanel sessionDetailHeader;
    private JButton sessionBackBtn;
    private JPanel exerciseDetailPage;
    private JLabel exerciseDetailTitle;
    private JPanel exerciseDetailList;
    private JPanel exerciseDetailHeader;
    private JButton exerciseBackBtn;

    // CTAs under the two cards
    private JButton startExerciseCTA;
    private JButton planBtnCTA;

    // Simple text output page (kept for “View All …” options)
    private JTextArea outputArea;

    // ui

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

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));

        JLabel leftIcon = new JLabel(loadScaledIcon("/ui/img/LeftBicep.png", 32, 25));
        JLabel title = new JLabel("  Fitness Manager  ");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel rightIcon = new JLabel(loadScaledIcon("/ui/img/RightBicep.png", 32, 25));

        // click-to-home on icons + title
        title.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        title.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { goHome(); }
        });
        leftIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        leftIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { goHome(); }
        });
        rightIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { goHome(); }
        });

        left.add(leftIcon);
        left.add(title);
        left.add(rightIcon);

        JPanel right = new JPanel();
        right.setOpaque(false);
        JButton saveBtn = ghostButton("\uD83D\uDCBE  Save Program");
        JButton loadBtn = ghostButton("\uD83D\uDCC2  Load Program");


        saveBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        loadBtn.setBorder(new EmptyBorder(8, 16, 8, 16));

        saveBtn.addActionListener(e -> saveData());
        loadBtn.addActionListener(e -> {
            loadData();
            refreshAllUI();
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

        sideBar.add(sectionLabel("MENU:"));
        sideBar.add(groupLabel("EXERCISE MANAGER"));

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
        sideBar.add(groupLabel("SESSION MANAGER"));

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
        sideBar.add(groupLabel("SAVE/LOAD & QUIT"));

        JButton saveBtn = leftNav("\uD83D\uDCBE  Save Program");
        saveBtn.addActionListener(e -> saveData());

        JButton loadBtn = leftNav("\uD83D\uDCC2  Load Program");
        loadBtn.addActionListener(e -> {
            loadData();
            refreshAllUI();
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

        // dashboard (home)
        JPanel home = new JPanel(new BorderLayout());
        home.setBorder(new EmptyBorder(16, 16, 16, 16));
        home.setBackground(new Color(248, 250, 252));

        // top row: 2 big cards with live lists
        JPanel topRow = new JPanel(new GridLayout(1, 2, 16, 16));
        topRow.setOpaque(false);

        // Exercises card (title + description + live list + CTA)
        JPanel exercisesCard = new JPanel(new BorderLayout());
        exercisesCard.setBackground(Color.WHITE);
        exercisesCard.setBorder(new LineBorder(new Color(230, 232, 236)));

        JPanel exHeader = cardHeader("\uD83D\uDCAA  Exercises",
                "Create and manage your exercise library. Provide a name, target muscle, weight, and reps for a given exercise.");
        exercisesCard.add(exHeader, BorderLayout.NORTH);

        exercisesListPanel = new JPanel();
        exercisesListPanel.setOpaque(false);
        exercisesListPanel.setLayout(new BoxLayout(exercisesListPanel, BoxLayout.Y_AXIS));

        JScrollPane exScroll = new JScrollPane(exercisesListPanel);
        exScroll.setBorder(null);
        exScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        exScroll.getVerticalScrollBar().setUnitIncrement(16);
        exercisesCard.add(exScroll, BorderLayout.CENTER);

        // keep a field so we can toggle visibility
        startExerciseCTA = ghostButton("Start by creating your first exercise");
        startExerciseCTA.setHorizontalAlignment(SwingConstants.LEFT);
        startExerciseCTA.addActionListener(e -> showCreateExerciseDialog());
        exercisesCard.add(startExerciseCTA, BorderLayout.SOUTH);


        // Sessions card (title + description + live list + CTA)
        JPanel sessionsCard = new JPanel(new BorderLayout());
        sessionsCard.setBackground(Color.WHITE);
        sessionsCard.setBorder(new LineBorder(new Color(230, 232, 236)));

        JPanel sesHeader = cardHeader("\uD83D\uDCC5  Training Sessions",
                "Build custom training sessions by combining various exercises and listing each ones number of sets.");
        sessionsCard.add(sesHeader, BorderLayout.NORTH);

        sessionsListPanel = new JPanel();
        sessionsListPanel.setOpaque(false);
        sessionsListPanel.setLayout(new BoxLayout(sessionsListPanel, BoxLayout.Y_AXIS));

        JScrollPane sesScroll = new JScrollPane(sessionsListPanel);
        sesScroll.setBorder(null);
        sesScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sesScroll.getVerticalScrollBar().setUnitIncrement(16);
        sessionsCard.add(sesScroll, BorderLayout.CENTER);

        // keep a field so we can toggle visibility
        planBtnCTA = ghostButton("Plan your training schedule");
        planBtnCTA.setHorizontalAlignment(SwingConstants.LEFT);
        planBtnCTA.addActionListener(e -> showCreateSessionDialog());
        sessionsCard.add(planBtnCTA, BorderLayout.SOUTH);


        topRow.add(exercisesCard);
        topRow.add(sessionsCard);

        // recent Activity
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

        // counters
        JPanel counters = new JPanel(new GridLayout(1, 3, 16, 16));
        counters.setOpaque(false);
        counters.setBorder(new EmptyBorder(16, 0, 0, 0));
        totalExercisesLbl = metricNumber("0");
        totalSessionsLbl = metricNumber("0");
        programsSavedLbl = metricNumber(String.valueOf(programsSavedCount));
        counters.add(wrapMetric(totalExercisesLbl, "Total Exercises"));
        counters.add(wrapMetric(totalSessionsLbl, "Training Sessions"));
        counters.add(wrapMetric(programsSavedLbl, "Programs Saved"));

        home.add(buildWelcomeHeader(), BorderLayout.NORTH);
        home.add(topRow, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(recent, BorderLayout.CENTER);
        bottom.add(counters, BorderLayout.SOUTH);
        home.add(bottom, BorderLayout.SOUTH);

        // Text output page (kept)
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JPanel textPage = new JPanel(new BorderLayout());
        textPage.setBorder(new EmptyBorder(16, 16, 16, 16));
        textPage.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // session detail page
        sessionDetailPage = new JPanel(new BorderLayout());
        sessionDetailPage.setBorder(new EmptyBorder(16, 16, 16, 16));

        sessionDetailTitle = new JLabel("Session");
        sessionDetailTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        // keep a persistent header we can rebuild on each session open
        sessionDetailHeader = new JPanel(new BorderLayout());

        // make Back to Dashboard bigger
        sessionBackBtn = ghostButton("\u2190  Back to Dashboard");
        sessionBackBtn.setBorder(new EmptyBorder(8, 16, 8, 16));   // bigger padding
        sessionBackBtn.addActionListener(e -> goHome());

        // temporary initial layout; openSessionDetail(...) will replace the WEST side
        sessionDetailHeader.add(sessionDetailTitle, BorderLayout.WEST);
        sessionDetailHeader.add(sessionBackBtn, BorderLayout.EAST);
        sessionDetailPage.add(sessionDetailHeader, BorderLayout.NORTH);

        sessionDetailList = new JPanel();
        sessionDetailList.setLayout(new BoxLayout(sessionDetailList, BoxLayout.Y_AXIS));
        JScrollPane detailScroll = new JScrollPane(sessionDetailList);
        detailScroll.setBorder(new LineBorder(new Color(230, 232, 236)));
        sessionDetailPage.add(detailScroll, BorderLayout.CENTER);

        // --- Exercise Detail page ---
        exerciseDetailPage = new JPanel(new BorderLayout());
        exerciseDetailPage.setBorder(new EmptyBorder(16, 16, 16, 16));

        exerciseDetailTitle = new JLabel("Exercise");
        exerciseDetailTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        // header we can rebuild per-exercise
        exerciseDetailHeader = new JPanel(new BorderLayout());

        // Back button (bigger), same as session
        exerciseBackBtn = ghostButton("\u2190  Back to Dashboard");
        exerciseBackBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        exerciseBackBtn.addActionListener(e -> goHome());

        // initial header layout (will be replaced in openExerciseDetail)
        exerciseDetailHeader.add(exerciseDetailTitle, BorderLayout.WEST);
        exerciseDetailHeader.add(exerciseBackBtn, BorderLayout.EAST);
        exerciseDetailPage.add(exerciseDetailHeader, BorderLayout.NORTH);

        // list/body
        exerciseDetailList = new JPanel();
        exerciseDetailList.setLayout(new BoxLayout(exerciseDetailList, BoxLayout.Y_AXIS));
        JScrollPane exDetailScroll = new JScrollPane(exerciseDetailList);
        exDetailScroll.setBorder(new LineBorder(new Color(230, 232, 236)));
        exerciseDetailPage.add(exDetailScroll, BorderLayout.CENTER);

        // register card
        mainArea.add(exerciseDetailPage, "exercise-detail");


        mainArea.add(home, "home");
        mainArea.add(textPage, "text");
        mainArea.add(sessionDetailPage, "session-detail");

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

    // ---------- Dialogs / Actions (functionality preserved + list refresh) ----------

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
            refreshAllUI();
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

        openEditExerciseDialog(e);
    }

    private void openEditExerciseDialog(Exercise e) {
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
            refreshAllUI();
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
        refreshAllUI();
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
            refreshAllUI();
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
            refreshAllUI();
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
        openExerciseDetail(e);
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
        openSessionDetail(s);
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
            jsonWriter.open();
            jsonWriter.write(manager);
            jsonWriter.close();
            programsSavedCount++;
            refreshAllUI();
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

    private void refreshAllUI() {
        refreshHomeCounts();
        rebuildExerciseList();
        rebuildSessionList();
        if (startExerciseCTA != null) {
            startExerciseCTA.setVisible(manager.getExercises().isEmpty());
        }
        if (planBtnCTA != null) {
            planBtnCTA.setVisible(manager.getSessions().isEmpty());
        }
    }

    private void rebuildExerciseList() {
        exercisesListPanel.removeAll();
        if (manager.getExercises().isEmpty()) {
            exercisesListPanel.add(emptyHint("No exercises yet."));
        } else {
            for (Exercise e : manager.getExercises()) {
                exercisesListPanel.add(exerciseRow(e));
                exercisesListPanel.add(Box.createVerticalStrut(4));
            }
        }
        exercisesListPanel.revalidate();
        exercisesListPanel.repaint();
    }

    private void rebuildSessionList() {
        sessionsListPanel.removeAll();
        if (manager.getSessions().isEmpty()) {
            sessionsListPanel.add(emptyHint("No training sessions yet."));
        } else {
            for (TrainingSession s : manager.getSessions()) {
                sessionsListPanel.add(sessionRow(s));
                sessionsListPanel.add(Box.createVerticalStrut(4));
            }
        }
        sessionsListPanel.revalidate();
        sessionsListPanel.repaint();
    }

    private JPanel exerciseRow(Exercise e) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new LineBorder(new Color(230, 232, 236)));
        row.setPreferredSize(new Dimension(Integer.MAX_VALUE, COMPACT_ROW_HEIGHT));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, COMPACT_ROW_HEIGHT));
        row.setMinimumSize(new Dimension(0, COMPACT_ROW_HEIGHT));

        String left = ellipsize(e.getName(), MAX_NAME_LEN) + " — " + e.getTargetMuscle()
                + " | " + e.getWeight() + " lbs | " + e.getReps() + " reps";
        JLabel lbl = new JLabel("  " + left);
        lbl.setFont(ROW_FONT);
        lbl.setBorder(new EmptyBorder(0, COMPACT_HPAD, 0, 0));
        row.add(lbl, BorderLayout.CENTER);

        // JButton edit = ghostButton("Edit");
        // edit.setFocusable(false);
        // fitCompact(edit);
        // edit.addActionListener(a -> {
        //     openEditExerciseDialog(e);
        //     // refresh rows after edit
        //     refreshAllUI();
        // });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 3));
        right.setOpaque(false);
        // right.add(edit);
        // row.add(right, BorderLayout.EAST);

        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent ev) { openExerciseDetail(e); }
        });


        return row;
    }


    private JPanel sessionRow(TrainingSession s) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new LineBorder(new Color(230, 232, 236)));
        row.setPreferredSize(new Dimension(Integer.MAX_VALUE, COMPACT_ROW_HEIGHT));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, COMPACT_ROW_HEIGHT));
        row.setMinimumSize(new Dimension(0, COMPACT_ROW_HEIGHT));

        // clickable row (opens detail)
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { openSessionDetail(s); }
        });

        JLabel lbl = new JLabel("  " + ellipsize(s.getName(), MAX_NAME_LEN));
        lbl.setFont(ROW_FONT);
        lbl.setBorder(new EmptyBorder(0, COMPACT_HPAD, 0, 0));
        row.add(lbl, BorderLayout.CENTER);

        // JButton view = ghostButton("View");
        // view.setFocusable(false);
        // fitCompact(view);
        // view.addActionListener(a -> openSessionDetail(s)); // button still works

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 3));
        right.setOpaque(false);
        // right.add(view);
        row.add(right, BorderLayout.EAST);

        return row;
    }

    private void openSessionDetail(TrainingSession s) {
        sessionDetailTitle.setText("Session — " + s.getName());
        sessionDetailList.removeAll();

        // Header left: Title + [+ Add Exercise] + [✖ Remove Exercise]
        JButton addBtn = ghostButton("\u2795  Add Exercise");
        addBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
        addBtn.addActionListener(e -> addExerciseToThisSessionDialog(s));

        JButton removeBtn = ghostButton("\u274C  Remove Exercise");
        removeBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
        removeBtn.addActionListener(e -> removeExerciseFromThisSessionDialog(s));

        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftGroup.setOpaque(false);
        leftGroup.add(sessionDetailTitle);
        leftGroup.add(addBtn);
        leftGroup.add(removeBtn);

        // Replace header contents
        sessionDetailHeader.removeAll();
        sessionDetailHeader.add(leftGroup, BorderLayout.WEST);
        sessionDetailHeader.add(sessionBackBtn, BorderLayout.EAST);
        sessionDetailHeader.revalidate();
        sessionDetailHeader.repaint();

        // Body
        if (s.getExerciseSets().isEmpty()) {
            sessionDetailList.add(emptyHint("No exercises in this session yet."));
        } else {
            for (Map.Entry<Exercise, Integer> entry : s.getExerciseSets().entrySet()) {
                Exercise e = entry.getKey();
                int sets = entry.getValue();

                String text = e.getName() + " — " + e.getTargetMuscle()
                        + " | " + e.getWeight() + " lbs | " + e.getReps()
                        + " reps | " + sets + " sets";

                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(new LineBorder(new Color(230, 232, 236)));
                row.add(new JLabel("  " + text), BorderLayout.CENTER);

                JButton editExercise = ghostButton("Edit Exercise");
                editExercise.setFont(ROW_FONT);
                editExercise.setMargin(BTN_INSETS);
                editExercise.addActionListener(a -> {
                    openEditExerciseDialog(e);
                    openSessionDetail(s);   // refresh this page after edit
                    refreshAllUI();
                });

                JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
                right.setOpaque(false);
                right.add(editExercise);
                row.add(right, BorderLayout.EAST);

                row.setPreferredSize(new Dimension(Integer.MAX_VALUE, COMPACT_ROW_HEIGHT));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, COMPACT_ROW_HEIGHT));
                row.setMinimumSize(new Dimension(0, COMPACT_ROW_HEIGHT));

                sessionDetailList.add(row);
                sessionDetailList.add(Box.createVerticalStrut(6));
            }
        }

        sessionDetailList.revalidate();
        sessionDetailList.repaint();
        cards.show(mainArea, "session-detail");
    }


    private void openExerciseDetail(Exercise e) {
        // Title + [Edit Exercise] on the left, Back on the right
        exerciseDetailTitle.setText("Exercise \u2014 " + e.getName());

        JButton editBtn = ghostButton("\u270E  Edit Exercise");
        editBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
        editBtn.addActionListener(a -> {
            openEditExerciseDialog(e);
            // refresh detail and dashboard after edit
            openExerciseDetail(e);
            refreshAllUI();
        });

        // Title + [Delete Exercise] to the left of the Edit Exercise button
        JButton deleteBtn = ghostButton("\u274C  Delete Exercise");
        deleteBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
        deleteBtn.addActionListener(a -> {
            int r = JOptionPane.showConfirmDialog(
                    this,
                    "Delete exercise '" + e.getName() + "'?\n\nThis also removes it from every training session.",
                    "Confirm Delete",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (r == JOptionPane.OK_OPTION) {
                boolean ok = manager.deleteExercise(e.getName());
                if (ok) {
                    pushActivity("Deleted exercise: " + e.getName());
                    refreshAllUI();
                    toast("Exercise '" + e.getName() + "' deleted.");
                    goHome();
                } else {
                    toastError("Could not delete exercise.");
                }
            }
        });


        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftGroup.setOpaque(false);
        leftGroup.add(exerciseDetailTitle);
        leftGroup.add(editBtn);
        leftGroup.add(deleteBtn);

        exerciseDetailHeader.removeAll();
        exerciseDetailHeader.add(leftGroup, BorderLayout.WEST);
        exerciseDetailHeader.add(exerciseBackBtn, BorderLayout.EAST);
        exerciseDetailHeader.revalidate();
        exerciseDetailHeader.repaint();

        // Body: show one compact row with the full details
        exerciseDetailList.removeAll();

        String text = e.getName() + " \u2014 " + e.getTargetMuscle()
                + " | " + e.getWeight() + " lbs | " + e.getReps() + " reps";

        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new LineBorder(new Color(230, 232, 236)));
        row.add(new JLabel("  " + text), BorderLayout.CENTER);
        row.setPreferredSize(new Dimension(Integer.MAX_VALUE, COMPACT_ROW_HEIGHT));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, COMPACT_ROW_HEIGHT));
        row.setMinimumSize(new Dimension(0, COMPACT_ROW_HEIGHT));

        exerciseDetailList.add(row);
        exerciseDetailList.add(Box.createVerticalStrut(6));

        exerciseDetailList.revalidate();
        exerciseDetailList.repaint();

        cards.show(mainArea, "exercise-detail");
    }


    // helpers

    private void removeExerciseFromThisSessionDialog(TrainingSession s) {
        if (s.getExerciseSets().isEmpty()) {
            toast("No exercises to remove in '" + s.getName() + "'.");
            return;
        }

        // Build list of exercise names IN THIS SESSION (not the whole library)
        String[] exerciseNames = s.getExerciseSets()
                .keySet()
                .stream()
                .map(Exercise::getName)
                .toArray(String[]::new);

        JComboBox<String> exerciseBox = new JComboBox<>(exerciseNames);

        Object[] fields = {
                "Exercise to remove from " + s.getName() + ":", exerciseBox
        };

        int result = JOptionPane.showConfirmDialog(
                this, fields, "Remove Exercise",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) return;

        String selectedName = (String) exerciseBox.getSelectedItem();
        if (selectedName == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Remove '" + selectedName + "' from session '" + s.getName() + "'?",
                "Confirm Removal",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm != JOptionPane.OK_OPTION) return;

        boolean ok = manager.removeExerciseFromSession(s.getName(), selectedName);
        if (ok) {
            pushActivity("Removed " + selectedName + " from " + s.getName());
            openSessionDetail(s); // redraw this page
            refreshAllUI();
            toast("Removed from session.");
        } else {
            toastError("Could not remove from session.");
        }
    }


    private void addExerciseToThisSessionDialog(TrainingSession s) {
        if (manager.getExercises().isEmpty()) {
            toast("No exercises available. Create one first.");
            return;
        }

        String[] exerciseNames = manager.getExercises().stream()
                .map(Exercise::getName).toArray(String[]::new);

        JComboBox<String> exercise = new JComboBox<>(exerciseNames);
        JTextField setsField = new JTextField();

        Object[] fields = {
                "Exercise:", exercise,
                "Sets:",     setsField
        };

        int result = JOptionPane.showConfirmDialog(this, fields,
                "Add Exercise to " + s.getName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        try {
            int sets = Integer.parseInt(setsField.getText().trim());
            manager.addExerciseToSession(s.getName(), (String) exercise.getSelectedItem(), sets);
            pushActivity("Added " + exercise.getSelectedItem() + " (" + sets + " sets) to " + s.getName());
            // refresh the detail page and dashboard
            openSessionDetail(s);
            refreshAllUI();
            toast("Exercise added to session.");
        } catch (NumberFormatException ex) {
            toastError("Invalid set number.");
        }
    }


    private void toast(String message) {
        JOptionPane.showMessageDialog(this, message, "Fitness Manager",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void toastError(String message) {
        JOptionPane.showMessageDialog(this, message, "Fitness Manager",
                JOptionPane.ERROR_MESSAGE);
    }


    public FitnessManagerAppGUINew() {
        super("Fitness Manager");
        buildUI();
        setMinimumSize(new Dimension(1200, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                printEventLogToConsole();
                System.exit(0);
            }
        });
        setVisible(true);
    }

    private void pushActivity(String text) {
        activityModel.add(0, "• " + text);
        if (activityModel.size() > 100) activityModel.removeElementAt(activityModel.size() - 1);
    }

    private void refreshHomeCounts() {
        totalExercisesLbl.setText(String.valueOf(manager.getExercises().size()));
        totalSessionsLbl.setText(String.valueOf(manager.getSessions().size()));
        programsSavedLbl.setText(String.valueOf(programsSavedCount));
    }

    private JPanel cardHeader(String title, String body) {
        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(16, 16, 8, 16));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel b = new JLabel("<html><body style='width: 520px'>" + body + "</body></html>");
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setForeground(new Color(90, 96, 104));

        inner.add(t);
        inner.add(Box.createVerticalStrut(8));
        inner.add(b);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(inner, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel metricNumber(String number) {
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
        b.setHorizontalAlignment(SwingConstants.LEFT);     // keep left-aligned inside the box
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        b.setBackground(Color.WHITE);

        
        b.setMargin(new Insets(0, 0, 0, 0));               // margin = 0, use inner padding below
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 232, 236)),                  // outer line
                new EmptyBorder(0, 28, 0, 12)             // inner padding: LEFT=28px, RIGHT=12px
        ));
        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFocusable(false);
        b.setBackground(Color.WHITE);
        b.setBorder(new LineBorder(new Color(210, 214, 220)));
        return b;
    }

    private JComponent emptyHint(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(120, 126, 134));
        l.setBorder(new EmptyBorder(8, 12, 8, 12));
        return l;
    }

    private ImageIcon loadScaledIcon(String resourcePath, int w, int h) {
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

    private static class BufferedImageStub extends java.awt.image.BufferedImage {
        public BufferedImageStub(int w, int h) { super(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB); }
    }

    private void goHome() {
        if (cards != null && mainArea != null) {
            cards.show(mainArea, "home");
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        new FitnessManagerAppGUINew();
    }
}

// I KNOW YOU SEE THIS, TRY TO FIX THE GITHUB POSTING IT FROM MY OTHER ACCOUNT IDK WHY ITS DOING THAT THIS IS A TEST TO SEE THAT BUT ALSO IMPORTANT
//fix menus!