package entities;

import main.*;
import exception.*;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base class for all animals.
 * Encapsulates shared state such as position, score,
 * cooldown mechanics, and movement logic.
 */

public abstract class Animal implements Entity {
    protected Position position;
    protected int score = 0;
    protected int cooldown = 0;
    protected String name;
    protected String imagePath;
    protected final SecureRandom random = new SecureRandom();
    protected final int cooldownDuration;

    // Creating an animal with the given name and cooldown duration for special ability
    public Animal(String name, int cooldownDuration) {
        this.name = name;
        this.cooldownDuration = cooldownDuration;
        // We assume that image exists. Also, replace empty spaces with underscore for it to match with the picture in the memory
        this.imagePath = "images/" + name.replace(" ", "_").toLowerCase() + ".png";
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getScore() {
        return score;
    }

    public void addPoints(int points) {
        score += points;
    }

    // Returns true if special ability is usable
    public boolean isSpecialAvailable() {
        return cooldown == 0;
    }

    // Restarts the cooldown for special ability
    public void startCooldown() {
        cooldown = cooldownDuration;
    }

    // Decreases the cooldown by 1 in every move
    public void decrementCooldown() {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    // Gets the current cooldown value. 0 means special is available
    public int getCooldown() {
        return cooldown;
    }

    // Checks if target entity is edible
    protected abstract boolean isTarget(Entity entity);
    protected abstract void eat(Entity target, Grid grid);

    // Checks if the cell is valid for moving. It should be empty or there should be something edible there
    protected boolean isValidTarget(Entity entity) {
        return entity == null || isTarget(entity);
    }

    // Stores possible positions where animal can walk to
    public Set<Position> getWalkPositions(Grid grid) {
        Set<Position> positions = new HashSet<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Position newPos = new Position(position.getX() + dx, position.getY() + dy);
                if (grid.isValid(newPos) && isValidTarget(grid.getEntityAt(newPos))) {
                    positions.add(newPos);
                }
            }
        }
        return positions;
    }

    // Gets possible positions where animals can go with their special abilities
    public abstract Set<Position> getSpecialPositions(Grid grid);

    
    public void performMove(Position newPos, Grid grid, boolean isSpecial) throws GameException {
        if (!grid.isValid(newPos)) {
            throw new GameException("Invalid position: out of bounds");
        }
        Entity target = grid.getEntityAt(newPos);
        if (target != null && !isTarget(target)) {
            throw new GameException("Cannot move to non-target entity");
        }

        grid.setEntityAt(position, null);
        setPosition(newPos);
        grid.setEntityAt(newPos, this);

        if (target != null && isTarget(target)) {
            eat(target, grid);
        }

        if (isSpecial) {
            startCooldown();
        } else {
            decrementCooldown();
        }
    }

    
    public void respawn(Grid grid) {
        Position newPos = grid.getRandomEmptyPosition();
        setPosition(newPos);
        grid.setEntityAt(newPos, this);
    }

    // Computer decision for move
    public abstract void aiMove(Game game);
    
    public void setCooldown(int value) {
        this.cooldown = value;
    }
}
