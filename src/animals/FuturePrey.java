package animals;

import exception.*;
import entities.*;
import main.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Future-era prey specialized in extended-distance movement
 * and evasive maneuvers.
 */

public class FuturePrey extends Prey {

    public FuturePrey(String name) {
        super(name, 2);
    }


    @Override
    public Set<Position> getSpecialPositions(Grid grid) {
        Set<Position> specials = new HashSet<>();

        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                if (dx == 0 && dy == 0) continue;
                if (Math.abs(dx) + Math.abs(dy) > 3 || Math.abs(dx) + Math.abs(dy) <= 1) continue;

                Position newPos = new Position(position.getX() + dx, position.getY() + dy);
                if (grid.isValid(newPos)) {
                    Entity occupant = grid.getEntityAt(newPos);
                    // Only allow empty cells for special hop
                    if (occupant == null) {
                        specials.add(newPos);
                    }
                }
            }
        }
        return specials;
    }

    // Special move without eating
    public void performMoveWithNoEat(Position newPos, Grid grid) throws GameException {
        if (!grid.isValid(newPos)) {
        	throw new GameException("Invalid move");
        }

        grid.setEntityAt(position, null);
        setPosition(newPos);
        grid.setEntityAt(newPos, this);

        startCooldown();
    }
}