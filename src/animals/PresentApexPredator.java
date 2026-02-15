package animals;

import main.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Present-era apex predator balancing mobility and
 * controlled pursuit behavior.
 */

public class PresentApexPredator extends ApexPredator {

    public PresentApexPredator(String name) {
        super(name, 3);
    }

    @Override
    public Set<Position> getSpecialPositions(Grid grid) {
        Set<Position> specials = new HashSet<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                for (int steps = 1; steps <= 3; steps++) {
                    Position newPos = new Position(position.getX() + steps * dx, position.getY() + steps * dy);
                    if (grid.isValid(newPos) && isValidTarget(grid.getEntityAt(newPos))) {
                        specials.add(newPos);
                    } else if (!grid.isValid(newPos)) {
                        break;
                    }
                }
            }
        }
        return specials;
    }
}