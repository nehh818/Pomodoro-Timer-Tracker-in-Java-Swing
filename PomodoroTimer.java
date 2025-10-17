import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

public class PomodoroTimer extends JFrame {
    private JLabel timerLabel, statusLabel, sessionLabel;
    private JButton startButton, pauseButton, stopButton, statsButton, todoButton;
    private int timeLeft; // in seconds
    private boolean isRunning = false;
    private boolean isBreak = false;
    private Timer timer;
    private int sessionCount = 0;

    public PomodoroTimer() {
        setTitle("🍅 Cute Pomodoro Timer");
        setSize(500, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(255, 240, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        // Status Label
        statusLabel = new JLabel("Focus Time ✨", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Verdana", Font.BOLD, 22));
        statusLabel.setForeground(new Color(255, 105, 180));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 5;
        add(statusLabel, gbc);

        // Timer Label
        timerLabel = new JLabel("25:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        timerLabel.setForeground(new Color(220, 20, 60));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 5;
        add(timerLabel, gbc);

        // Buttons
        startButton = new JButton("Start 🍓");
        pauseButton = new JButton("Pause ⏸️");
        stopButton = new JButton("Stop 🔥");
        statsButton = new JButton("View Stats 📊");
        todoButton = new JButton("To-Do List 📝");

        JButton[] buttons = {startButton, pauseButton, stopButton, statsButton, todoButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setBackground(new Color(255, 182, 193)); // Light pink
        }

        gbc.gridwidth = 1;
        gbc.gridy = 2; gbc.gridx = 0; add(startButton, gbc);
        gbc.gridx = 1; add(pauseButton, gbc);
        gbc.gridx = 2; add(stopButton, gbc);
        gbc.gridx = 3; add(statsButton, gbc);
        gbc.gridx = 4; add(todoButton, gbc);

        // Session Label
        sessionLabel = new JLabel("Sessions Completed: 0", SwingConstants.CENTER);
        sessionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        sessionLabel.setForeground(new Color(75, 0, 130));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 5;
        add(sessionLabel, gbc);

        timeLeft = 25 * 60;

        // Button Actions
        startButton.addActionListener(e -> { if (!isRunning) startTimer(); });
        pauseButton.addActionListener(e -> pauseTimer());
        stopButton.addActionListener(e -> resetTimer());
        statsButton.addActionListener(e -> showStats());
        todoButton.addActionListener(e -> showTodoList());
    }

    // ---------------- Timer Functions ----------------
    private void startTimer() {
        isRunning = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                    SwingUtilities.invokeLater(() -> updateTimerLabel());
                } else {
                    timer.cancel();
                    isRunning = false;
                    SwingUtilities.invokeLater(() -> {
                        if (isBreak) {
                            isBreak = false;
                            sessionCount++;
                            sessionLabel.setText("Sessions Completed: " + sessionCount);
                            JOptionPane.showMessageDialog(null, "Break over! Time to focus 🌻");
                            resetToFocus();
                        } else {
                            isBreak = true;
                            JOptionPane.showMessageDialog(null, "Focus session complete! Take a short break ☕");
                            resetToBreak();
                        }
                    });
                }
            }
        }, 1000, 1000);
    }

    private void pauseTimer() { if (timer != null) timer.cancel(); isRunning = false; }

    private void resetTimer() {
        if (timer != null) timer.cancel();
        isRunning = false;
        isBreak = false;
        timeLeft = 25 * 60;
        statusLabel.setText("Focus Time ✨");
        timerLabel.setText("25:00");
        timerLabel.setForeground(new Color(220, 20, 60));
    }

    private void resetToBreak() {
        statusLabel.setText("Break Time 💫");
        timeLeft = 5 * 60;
        timerLabel.setForeground(new Color(60, 179, 113));
        updateTimerLabel();
    }

    private void resetToFocus() {
        statusLabel.setText("Focus Time ✨");
        timeLeft = 25 * 60;
        timerLabel.setForeground(new Color(220, 20, 60));
        updateTimerLabel();
    }

    private void updateTimerLabel() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    // ---------------- Stats Page ----------------
    private void showStats() {
        JFrame statsFrame = new JFrame("📊 Pomodoro Stats");
        statsFrame.setSize(400, 300);
        statsFrame.setLocationRelativeTo(this);

        JPanel graphPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 182, 193));
                int barHeight = sessionCount * 20;
                g.fillRect(80, 200 - barHeight, 80, barHeight);
                g.setColor(Color.BLACK);
                g.drawString("Sessions Completed: " + sessionCount + " 🌟", 60, 220);
            }
        };
        statsFrame.add(graphPanel);
        statsFrame.setVisible(true);
    }

    // ---------------- To-Do List Page ----------------
    private void showTodoList() {
        JFrame todoFrame = new JFrame("📝 To-Do List");
        todoFrame.setSize(400, 400);
        todoFrame.setLocationRelativeTo(this);
        todoFrame.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> taskList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(taskList);

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(255, 240, 245));
        JTextField taskField = new JTextField(15);
        JButton addButton = new JButton("Add ✅");
        JButton removeButton = new JButton("Remove ❌");
        addButton.setBackground(new Color(255, 182, 193));
        removeButton.setBackground(new Color(255, 182, 193));
        inputPanel.add(taskField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);

        addButton.addActionListener(e -> {
            String task = taskField.getText().trim();
            if (!task.isEmpty()) {
                listModel.addElement(task);
                taskField.setText("");
            }
        });

        removeButton.addActionListener(e -> {
            int selected = taskList.getSelectedIndex();
            if (selected != -1) listModel.remove(selected);
        });

        todoFrame.add(scrollPane, BorderLayout.CENTER);
        todoFrame.add(inputPanel, BorderLayout.SOUTH);
        todoFrame.setVisible(true);
    }

    // ---------------- Main ----------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PomodoroTimer frame = new PomodoroTimer();
            frame.setVisible(true);
        });
    }
}
