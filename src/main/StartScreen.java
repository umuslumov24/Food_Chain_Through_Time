package main;

import load.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Initial configuration screen allowing the player to select
 * game parameters before starting the simulation.
 */

public class StartScreen extends JFrame {
    private JComboBox<Mode> modeCombo;
    private JComboBox<Integer> gridSizeCombo;
    private JComboBox<Integer> roundsCombo;
    private JRadioButton preyButton;
    private JRadioButton predatorButton;

    public StartScreen() {
        setTitle("Food Chain Through Time - Start New Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Food Chain Through Time", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 32));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        // Mode
        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("Choose Era:"), gbc);
        modeCombo = new JComboBox<>(Mode.values());
        modeCombo.setSelectedItem(Mode.PAST);
        gbc.gridx = 1;
        add(modeCombo, gbc);

        // Grid Size
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Grid Size:"), gbc);
        gridSizeCombo = new JComboBox<>(new Integer[]{10, 15, 20});
        gbc.gridx = 1;
        add(gridSizeCombo, gbc);

        // Rounds
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Total Rounds:"), gbc);
        roundsCombo = new JComboBox<>(new Integer[]{10, 15, 20});
        gbc.gridx = 1;
        add(roundsCombo, gbc);

        // Player Role
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Control:"), gbc);
        preyButton = new JRadioButton("Prey");
        predatorButton = new JRadioButton("Predator");
        predatorButton.setSelected(true);
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(preyButton);
        roleGroup.add(predatorButton);

        JPanel rolePanel = new JPanel();
        rolePanel.add(preyButton);
        rolePanel.add(predatorButton);
        gbc.gridx = 1;
        add(rolePanel, gbc);

        // Start Button
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(startButton, gbc);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Mode mode = (Mode) modeCombo.getSelectedItem();
                int size = (Integer) gridSizeCombo.getSelectedItem();
                int rounds = (Integer) roundsCombo.getSelectedItem();
                boolean playerIsPrey = preyButton.isSelected();

                // Load animals from file and start game
                AnimalLoader loader = new AnimalLoader();
                try {
                    String[] chain = loader.getRandomFoodChain(mode);
                    new GameFrame(mode, size, rounds, playerIsPrey,
                            chain[0], chain[1], chain[2], chain[3]); // prey, pred, apex, food
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StartScreen.this,
                            "Error loading animals: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pack();
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }
}
