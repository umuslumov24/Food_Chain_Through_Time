package entities;

import main.*;

/**
 * Represents a consumable entity that animals can interact with
 * to gain score or trigger effects.
 */

public class Food implements Entity {
    private Position position;
    private String name;
    private String imagePath;

    public Food(String name) {
        this.name = name;
        this.imagePath = "images/" + name.replace(" ", "_").toLowerCase() + ".png";
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    //respawns at a random empty position
    public void respawn(Grid grid) {
        Position newPos = grid.getRandomEmptyPosition();
        setPosition(newPos);
        grid.setEntityAt(newPos, this);
    }
}

