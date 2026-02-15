package main;

import load.*;
import entities.*;
import exception.*;
import animals.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.border.TitledBorder;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

/**
 * Main application window responsible for rendering the game UI
 * and coordinating visual updates with the game engine.
 */

public class GameFrame extends JFrame {
    private Game game;
    private GamePanel gamePanel;
    private JPanel preyPanel;
    private JPanel predatorPanel;
    private JPanel apexPanel;
    private JPanel generalPanel;
    private JLabel roundLabel;
    private JLabel leaderLabel;
    private Map<String, Image> imageCache = new HashMap<>();

    public GameFrame(Mode mode, int gridSize, int totalRounds, boolean playerControlsPrey, String preyName, String predatorName, String apexName, String foodName) {

        this.game = new Game(mode, gridSize, totalRounds, playerControlsPrey, preyName, predatorName, apexName, foodName);
        setTitle("Food Chain Through Time - " + mode);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        // Right side: Info panels with borders
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        infoPanel.setPreferredSize(new Dimension(300, 0));

        // General Game Status Box
        generalPanel = new JPanel();
        generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
        generalPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Game Status"));
        generalPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        generalPanel.setMaximumSize(new Dimension(280, 150));

        JLabel eraLabel = new JLabel("Era: " + mode);
        eraLabel.setFont(new Font("Arial", Font.BOLD, 16));
        eraLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        generalPanel.add(eraLabel);
        generalPanel.add(Box.createVerticalStrut(10));

        roundLabel = new JLabel(updateRoundText());
        roundLabel.setFont(new Font("Arial", Font.BOLD, 16));
        roundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        generalPanel.add(roundLabel);
        generalPanel.add(Box.createVerticalStrut(10));

        leaderLabel = new JLabel("Leader: Calculating...");
        leaderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        leaderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        generalPanel.add(leaderLabel);

        infoPanel.add(generalPanel);
        infoPanel.add(Box.createVerticalStrut(30));

        // Create initial animal panels
        createAnimalPanels(playerControlsPrey);

        infoPanel.add(preyPanel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(predatorPanel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(apexPanel);

        add(infoPanel, BorderLayout.EAST);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save Game");
        JMenuItem loadItem = new JMenuItem("Load Game");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        loadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game loadedGame = Game.loadGame();
                if (loadedGame != null) {
                    game = loadedGame;
                    gamePanel.setPreferredSize(new Dimension(
                            game.getGrid().getSize() * 40 + 2,
                            game.getGrid().getSize() * 40 + 2
                    ));
                    pack();
                    gamePanel.repaint();
                    refreshUI();
                    processTurn();
                    JOptionPane.showMessageDialog(GameFrame.this,
                            "Game Loaded Successfully!\nContinuing from Round " + game.getCurrentRound(),
                            "Load", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.saveGame();
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        processTurn();
    }

    // Creates all the animal panels
    private void createAnimalPanels(boolean playerControlsPrey) {
        Color playerColor = new Color(50, 100, 255); // Blue border for player

        preyPanel = createAnimalInfoPanel(
                game.getPrey(),
                playerControlsPrey ? playerColor : Color.GRAY
        );

        predatorPanel = createAnimalInfoPanel(
                game.getPredator(),
                !playerControlsPrey ? playerColor : Color.GRAY
        );

        apexPanel = createAnimalInfoPanel(
                game.getApexPredator(),
                Color.GRAY
        );
    }
    
    // Creates 1 animal panel
    private JPanel createAnimalInfoPanel(Animal animal, Color borderColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderColor, 3),
                animal.getName(),
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16)
        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(260, 120));
        panel.setPreferredSize(new Dimension(260, 120));

        JLabel scoreLabel = new JLabel("Score: " + animal.getScore());
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // When special is ready, sets the color to green, red otherwise
        String cooldownText = animal.isSpecialAvailable()
                ? "<html><font color='green'>Special: Ready</font></html>"
                : "<html><font color='red'>Cooldown: " + animal.getCooldown() + "</font></html>";
        JLabel cooldownLabel = new JLabel(cooldownText);
        cooldownLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        cooldownLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(scoreLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(cooldownLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private String updateRoundText() {
        return "Round: " + game.getCurrentRound() + " / " + game.getTotalRounds();
    }

    private String getCurrentLeader() {
        Animal prey = game.getPrey();
        Animal predator = game.getPredator();
        Animal apex = game.getApexPredator();

        int maxScore = Math.max(prey.getScore(), Math.max(predator.getScore(), apex.getScore()));

        StringBuilder leader = new StringBuilder();
        if (prey.getScore() == maxScore) leader.append(prey.getName()).append(" ");
        if (predator.getScore() == maxScore) leader.append(predator.getName()).append(" ");
        if (apex.getScore() == maxScore) leader.append(apex.getName());

        return leader.toString().trim().isEmpty() ? "None" : leader.toString().trim();
    }

    private Image getImage(String path) {
        Image img = imageCache.get(path);
        if (img == null) {
            try {
                img = ImageIO.read(new File(path));
            } catch (Exception e) {
                System.err.println("Image not found: " + path);
                img = null;
            }
            imageCache.put(path, img);
        }
        return img;
    }

    private void refreshUI() {
        roundLabel.setText(updateRoundText());
        leaderLabel.setText("Leader: " + getCurrentLeader());

        // Remove old panels and add updated ones
        Container parent = preyPanel.getParent();
        if (parent != null) {
            parent.remove(preyPanel);
            parent.remove(predatorPanel);
            parent.remove(apexPanel);
            createAnimalPanels(game.isPlayerControllingPrey());
            parent.add(preyPanel, 2);
            parent.add(Box.createVerticalStrut(15), 3);
            parent.add(predatorPanel, 4);
            parent.add(Box.createVerticalStrut(15), 5);
            parent.add(apexPanel, 6);
            parent.revalidate();
            parent.repaint();
        }

        gamePanel.repaint();
        if (game.isGameOver()) {
            GameLogger.log("Game Over after " + game.getTotalRounds() + " rounds");
            GameLogger.log(game.getWinner());
            JOptionPane.showMessageDialog(this, game.getWinner(), "Game Over!", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    private void processTurn() {
        if (game.isGameOver()) {
            refreshUI();
            return;
        }

        refreshUI();
        if (!game.isPlayerTurn()) {
            Timer timer = new Timer(800, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.performAITurn();
                    processTurn();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    /**
     * Rendering panel responsible for drawing the grid, entities,
     * and handling user input events.
     */

    private class GamePanel extends JPanel {
        private final int cellSize = 40;

        public GamePanel() {
            setPreferredSize(new Dimension(game.getGrid().getSize() * cellSize + 2, game.getGrid().getSize() * cellSize + 2));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!game.isPlayerTurn()) return;

                    int x = e.getX() / cellSize;
                    int y = e.getY() / cellSize;
                    if (x < 0 || y < 0 || x >= game.getGrid().getSize() || y >= game.getGrid().getSize()) return;

                    Position clicked = new Position(x, y);
                    try {
                        game.performPlayerMove(clicked);
                        processTurn();
                    } catch (GameException ex) {
                    }
                }
            });
        }

        //In this method, everything is drawed
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Grid grid = game.getGrid();
            int size = grid.getSize();

            g.setColor(new Color(0, 100, 0));
            g.fillRect(0, 0, getWidth(), getHeight());

            if (game.isPlayerTurn()) {
                Animal player = game.getCurrentAnimal();
                Set<Position> walk = player.getWalkPositions(grid);
                Set<Position> special = new HashSet<>();

                if (player.isSpecialAvailable()) {
                    special.addAll(player.getSpecialPositions(grid));
                }

                // PresentPredator double move always shown
                if (player instanceof PresentPredator) {
                    special.addAll(((PresentPredator) player).getDoubleMovePositions(grid, game.getApexPredator().getPosition()));
                }

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

                g.setColor(new Color(100, 200, 255, 150));
                for (Position p : walk) {
                    g.fillRect(p.getX() * cellSize, p.getY() * cellSize, cellSize, cellSize);
                }

                g.setColor(new Color(100, 255, 100, 150));
                for (Position p : special) {
                    if (!walk.contains(p)) {
                        g.fillRect(p.getX() * cellSize, p.getY() * cellSize, cellSize, cellSize);
                    }
                }

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            g.setColor(Color.BLACK);
            for (int i = 0; i <= size; i++) {
                g.drawLine(i * cellSize, 0, i * cellSize, size * cellSize);
                g.drawLine(0, i * cellSize, size * cellSize, i * cellSize);
            }

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    Entity entity = grid.getEntityAt(new Position(x, y));
                    if (entity != null) {
                        Image img = getImage(entity.getImagePath());
                        if (img != null) {
                            g.drawImage(img, x * cellSize + 2, y * cellSize + 2,
                                    cellSize - 4, cellSize - 4, null);
                        } else {
                            g.setColor(Color.WHITE);
                            g.drawString(entity.getName().substring(0, Math.min(3, entity.getName().length())),
                                    x * cellSize + 10, y * cellSize + 25);
                        }
                    }
                }
            }
        }
    }
}
