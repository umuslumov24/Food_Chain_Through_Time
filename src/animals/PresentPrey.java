package animals;

import main.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Present-era prey optimized for moderate mobility
 * and threat avoidance.
 */

public class PresentPrey extends Prey {

    public PresentPrey(String name) {
        super(name, 3);
    }

    @Override
    public Set<Position> getSpecialPositions(Grid grid) {
        Set<Position> specials = new HashSet<>();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx == 0 && dy == 0) {
                	continue;
                }
                // Must be exactly 2 cells away
                if (Math.abs(dx) + Math.abs(dy) != 2 && Math.abs(dx) != 2 && Math.abs(dy) != 2) {
                	continue;
                }

                Position newPos = new Position(position.getX() + dx, position.getY() + dy);
                if (grid.isValid(newPos) && isValidTarget(grid.getEntityAt(newPos))) {
                    specials.add(newPos);
                }
            }
        }
        return specials;
    }
}