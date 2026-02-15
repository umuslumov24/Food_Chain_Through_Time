package animals;

import main.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Future-era apex predator with enhanced mobility abilities,
 * including teleportation-based movement.
 */

public class FutureApexPredator extends ApexPredator {

    public FutureApexPredator(String name) {
        super(name, 3);
    }

    @Override
    public Set<Position> getSpecialPositions(Grid grid) {
        Set<Position> specials = new HashSet<>();

        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                if (dx == 0 && dy == 0) continue;
                if (Math.abs(dx) + Math.abs(dy) > 3) continue;

                Position newPos = new Position(position.getX() + dx, position.getY() + dy);
                if (grid.isValid(newPos) && isValidTarget(grid.getEntityAt(newPos))) {
                    specials.add(newPos);
                }
            }
        }
        return specials;
    }
}