package main;

import java.util.Objects;

/**
 * Represents a coordinate on the game grid.
 * Provides utility methods for spatial calculations.
 */

public class Position {
    private int x;
    private int y;

    // Constructs a Position with the given coordinates
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Gets the x-coordinate
    public int getX() {
        return x;
    }

    // Gets the y-coordinate
    public int getY() {
        return y;
    }

    // Calculates the distance between positions
    public int distanceTo(Position other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    // This method checks if two postions are the same 
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }
    
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}