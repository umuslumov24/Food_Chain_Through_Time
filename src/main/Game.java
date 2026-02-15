package main;

import exception.*;
import entities.*;
import animals.*;
import load.*;
import javax.swing.*;
import java.io.File;
import java.io.*;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.*;

/**
 * Core game engine responsible for managing the turn loop,
 * entity interactions, scoring, and overall game state.
 */


public class Game {
    private final SecureRandom random = new SecureRandom();

    private final Grid grid;
    private final Animal prey;
    private final Animal predator;
    private final Animal apexPredator;
    private final Food food;

    private int currentRound = 1;
    private final int totalRounds;
    private final Mode mode;
    private final boolean playerControlsPrey; // Becomes true if player chooses prey, false when player chooses predator
    private int currentTurn = 0; // 0=Prey, 1=Predator, 2=Apex

    public Game(Mode mode, int gridSize, int totalRounds, boolean playerControlsPrey, String preyName, String predatorName, String apexName, String foodName) {
        // Gives the first instructions for the creation of the game
        this.mode = mode;
        this.totalRounds = totalRounds;
        this.playerControlsPrey = playerControlsPrey;
        grid = new Grid(gridSize);

        // Create animals based on mode
        prey = createAnimal(mode, "Prey", preyName);
        predator = createAnimal(mode, "Predator", predatorName);
        apexPredator = createAnimal(mode, "Apex", apexName);
        food = new Food(foodName);

        // Logging the early game messages
        GameLogger.log("New Game Started");
        GameLogger.log("Era: " + mode);
        GameLogger.log("Total Rounds: " + totalRounds);
        GameLogger.log("Player controls: " + (playerControlsPrey ? prey.getName() + " (Prey)" : predator.getName() + " (Predator)"));
        GameLogger.log("Animals: Prey=" + prey.getName() + ", Predator=" + predator.getName() + ", Apex=" + apexPredator.getName() + ", Food=" + food.getName());
        placeAllEntitiesRandomly();
    }
    
    
    public void setCurrentRound(int round) {
        this.currentRound = round;
    }

    public void setCurrentTurn(int turn) {
        this.currentTurn = turn;
    }

    // This method creates the animal required
    private Animal createAnimal(Mode mode, String role, String name) {
       
        return switch (mode) {
            case PAST -> switch (role) {
                case "Prey" -> new PastPrey(name);
                case "Predator" -> new PastPredator(name);
                case "Apex" -> new PastApexPredator(name);
                default -> throw new IllegalArgumentException("Unknown role");
            };
            case PRESENT -> switch (role) {
                case "Prey" -> new PresentPrey(name);
                case "Predator" -> new PresentPredator(name);
                case "Apex" -> new PresentApexPredator(name);
                default -> throw new IllegalArgumentException("Unknown role");
            };
            case FUTURE -> switch (role) {
                case "Prey" -> new FuturePrey(name);
                case "Predator" -> new FuturePredator(name);
                case "Apex" -> new FutureApexPredator(name);
                default -> throw new IllegalArgumentException("Unknown role");
            };
        };
    }


    private void placeAllEntitiesRandomly() {
        placeEntityRandomly(prey);
        placeEntityRandomly(predator);
        placeEntityRandomly(apexPredator);
        placeEntityRandomly(food);
    }

    // Places the entity at a random location using the SecureRandom, with ensuring there is no one in the cell selected
    private void placeEntityRandomly(Entity entity) {
        Position pos;
        do {
            pos = new Position(random.nextInt(grid.getSize()), random.nextInt(grid.getSize()));
        } while (grid.getEntityAt(pos) != null);
        entity.setPosition(pos);
        grid.setEntityAt(pos, entity);
    }

    public boolean isPlayerControllingPrey() {
        return playerControlsPrey;
    }

    // Getters
    public Grid getGrid() {return grid;}
    public Animal getPrey() {return prey;}
    public Animal getPredator() {return predator;}
    public Animal getApexPredator() {return apexPredator;}
    public Food getFood() {return food;}
    public int getCurrentRound() {return currentRound;}
    public int getTotalRounds() {return totalRounds;}

    // Checks if it is the player's turn by the algorithm:
    // 1)if turn is 0 and player is prey, then it is prey's turn and player should make a move
    // 2)if turn is 1 and player is predator, then it is predator's turn and player will move
    public boolean isPlayerTurn() {
        return (currentTurn == 0 && playerControlsPrey) ||
                (currentTurn == 1 && !playerControlsPrey);
    }

    public Animal getCurrentAnimal() {
        return switch (currentTurn) {
            case 0 -> prey;
            case 1 -> predator;
            case 2 -> apexPredator;
            default -> null;
        };
    }

    /*
     * This method controls the way player makes the move.
     * It also prevents teh player from making any invalid move.
     */
    
    public void performPlayerMove(Position targetPos) throws GameException {
        Animal current = getCurrentAnimal();

        // Prevent any move after the game ended
        if (isGameOver()) {
            return;
        }

        // Prevents moving when it is not player's turn
        if (!isPlayerTurn()) {
            throw new GameException("Not player's turn");
        }

        // Allows staying still,by clicking own position
        if (targetPos.equals(current.getPosition())) {
            current.decrementCooldown();
            advanceTurn();
            return;
        }

        // Positions that can be gone with 'walking'
        Set<Position> walk = current.getWalkPositions(grid);
        // Positions that can be gone with special ability
        Set<Position> special = new HashSet<>();

        boolean specialReady = current.isSpecialAvailable();
        if (specialReady) {
            special.addAll(current.getSpecialPositions(grid));
        }

        // For PresentPredator double move is always tried to be added
        // Since inside getDoubleMovePositions method we already check if the apex is near or not, the special ability positions are not added all the time
        if (current instanceof PresentPredator pred) {
            Set<Position> doubleMoves = pred.getDoubleMovePositions(grid, apexPredator.getPosition());
            special.addAll(doubleMoves);
        }

        boolean isWalkMove = walk.contains(targetPos);
        boolean isSpecialMove = special.contains(targetPos) && !walk.contains(targetPos);

        // Does not allow players to go in the directions they are not allowed
        if (!isWalkMove && !isSpecialMove) {
            throw new GameException("Invalid move - not a valid position");
        }

        // Determines if we should start cooldown
        boolean usingSpecial = isSpecialMove && !(current instanceof PresentPredator);

        // Finally making the move
        if (current instanceof FuturePrey && usingSpecial) {
            ((FuturePrey) current).performMoveWithNoEat(targetPos, grid);
        } else {
            current.performMove(targetPos, grid, usingSpecial);
        }

        advanceTurn();
        GameLogger.log(current.getName() + " moved to " + targetPos + (usingSpecial ? " (used special)" : ""));
    }

    public void performAITurn() {
        if (isGameOver()) {
            return;
        }

        // If this is player's move, stops
        Animal current = getCurrentAnimal();
        if (isPlayerTurn()) {
            return;
        }

        GameLogger.log("AI turn: " + current.getName());
        current.aiMove(this);
        advanceTurn();
    }

    // This method is for changing who is going to make the move at the moment
    private void advanceTurn() {
        currentTurn++;
        if (currentTurn > 2) {
            GameLogger.log("Round " + currentRound + " ended");
            currentTurn = 0;
            currentRound++;
            GameLogger.log("Round " + currentRound + " started");
        }
    }

    public boolean isGameOver() {
        return currentRound >= totalRounds;
    }

    public String getWinner() {
        if (!isGameOver()) {
            return "";
        }

        int bestScore = prey.getScore();
        if (predator.getScore() > bestScore) {
            bestScore = predator.getScore();
        }
        if (apexPredator.getScore() > bestScore) {
            bestScore = apexPredator.getScore();
        }

        StringBuilder winners = new StringBuilder();
        boolean first = true;
        // This variable checks whether a winner is the first that appeared or not

        if (prey.getScore() == bestScore) {
            winners.append(prey.getName());
            first = false;
        }
        if (predator.getScore() == bestScore) {
            if (!first) {
                winners.append(", ");
            }
            winners.append(predator.getName());
            first = false;
        }
        if (apexPredator.getScore() == bestScore) {
            if (!first) {
                winners.append(", ");
            }
            winners.append(apexPredator.getName());
        }

        return "Winner(s): " + winners.toString();
    }

    // In this method, we write all the information of the game that we want to save to a filed
    public void saveGame() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("savegame.txt"))) {
            writer.println(mode);
            writer.println(grid.getSize());
            writer.println(totalRounds);
            writer.println(currentRound);
            writer.println(playerControlsPrey);
            writer.println(currentTurn);
            writer.println(prey.getName());
            writer.println(predator.getName());
            writer.println(apexPredator.getName());
            writer.println(food.getName());
            writer.println(prey.getScore());
            writer.println(predator.getScore());
            writer.println(apexPredator.getScore());
            writer.println(prey.getCooldown());
            writer.println(predator.getCooldown());
            writer.println(apexPredator.getCooldown());
            writer.println(prey.getPosition().getX() + "," + prey.getPosition().getY());
            writer.println(predator.getPosition().getX() + "," + predator.getPosition().getY());
            writer.println(apexPredator.getPosition().getX() + "," + apexPredator.getPosition().getY());
            writer.println(food.getPosition().getX() + "," + food.getPosition().getY());
            GameLogger.log("Game saved successfully");
            JOptionPane.showMessageDialog(null, "Game Saved!", "Save", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            GameLogger.log("Save failed: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Save Failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Saved game is being loaded and created where it is left off
    public static Game loadGame() {

        File file = new File("savegame.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "No saved game found!", "Load", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try (Scanner scanner = new Scanner(file)) {
            Mode mode = Mode.valueOf(scanner.nextLine());
            int gridSize = Integer.parseInt(scanner.nextLine());
            int totalRounds = Integer.parseInt(scanner.nextLine());
            int currentRound = Integer.parseInt(scanner.nextLine());
            boolean playerControlsPrey = Boolean.parseBoolean(scanner.nextLine());
            int currentTurn = Integer.parseInt(scanner.nextLine());
            String preyName = scanner.nextLine();
            String predatorName = scanner.nextLine();
            String apexName = scanner.nextLine();
            String foodName = scanner.nextLine();

            // Creating the game with loaded values
            Game newGame = new Game(mode, gridSize, totalRounds, playerControlsPrey, preyName, predatorName, apexName, foodName);

            // Restoring the other properties
            newGame.prey.addPoints(Integer.parseInt(scanner.nextLine()));
            newGame.predator.addPoints(Integer.parseInt(scanner.nextLine()));
            newGame.apexPredator.addPoints(Integer.parseInt(scanner.nextLine()));
            newGame.prey.setCooldown(Integer.parseInt(scanner.nextLine()));
            newGame.predator.setCooldown(Integer.parseInt(scanner.nextLine()));
            newGame.apexPredator.setCooldown(Integer.parseInt(scanner.nextLine()));
            String[] parts = scanner.nextLine().split(",");
            Position pos = new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            newGame.grid.setEntityAt(newGame.prey.getPosition(), null);
            newGame.prey.setPosition(pos);
            newGame.grid.setEntityAt(pos, newGame.prey);

            parts = scanner.nextLine().split(",");
            pos = new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            newGame.grid.setEntityAt(newGame.predator.getPosition(), null);
            newGame.predator.setPosition(pos);
            newGame.grid.setEntityAt(pos, newGame.predator);

            parts = scanner.nextLine().split(",");
            pos = new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            newGame.grid.setEntityAt(newGame.apexPredator.getPosition(), null);
            newGame.apexPredator.setPosition(pos);
            newGame.grid.setEntityAt(pos, newGame.apexPredator);

            parts = scanner.nextLine().split(",");
            pos = new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            newGame.grid.setEntityAt(newGame.food.getPosition(), null);
            newGame.food.setPosition(pos);
            newGame.grid.setEntityAt(pos, newGame.food);
            newGame.setCurrentRound(currentRound);
            newGame.setCurrentTurn(currentTurn);

            GameLogger.log("Game loaded successfully");
            return newGame;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Load failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}