package main;

import entities.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game world grid and stores the positions
 * of all entities within the simulation.
 */

public class Grid {
    private final int size;
    private final Entity[][] cells;
    private final SecureRandom random = new SecureRandom();

    public Grid(int size) {
        this.size = size;
        this.cells = new Entity[size][size];
    }


    public Entity getEntityAt(Position pos) {
        if (pos == null) {
            return null;
        }
        return cells[pos.getX()][pos.getY()];
    }

    public void setEntityAt(Position pos, Entity entity) {
        if (pos == null) {
            return;
        }
        cells[pos.getX()][pos.getY()] = entity;
    }

    // Checks if the position is within bounds
    public boolean isValid(Position pos) {
        return (pos.getX() >= 0) && (pos.getX() < size) && (pos.getY() >= 0) && (pos.getY() < size);
    }


    public List<Position> getEmptyPositions() {
        List<Position> empty = new ArrayList<>();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (cells[x][y] == null) {
                    empty.add(new Position(x, y));
                }
            }
        }
        return empty;
    }


    public Position getRandomEmptyPosition() {
        List<Position> empty = getEmptyPositions();
        return empty.get(random.nextInt(empty.size()));
    }


    public int getSize() {
        return size;
    }
}
