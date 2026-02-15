package animals;

import main.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Past-era prey utilizing unpredictable movement patterns
 * to evade threats.
 */

public class PastPrey extends Prey {

    public PastPrey(String name) {
        super(name, 2);
    }

    @Override
    public Set<Position> getSpecialPositions(Grid grid) {
        Set<Position> specials = new HashSet<>();

        // Try all 8 adjacent directions
        for (int dx1 = -1; dx1 <= 1; dx1++) {
            for (int dy1 = -1; dy1 <= 1; dy1++) {
                if (dx1 == 0 && dy1 == 0) {
                	continue;
                }

                Position mid = new Position(position.getX() + dx1, position.getY() + dy1);
                if (!grid.isValid(mid)) {
                	continue;
                }

                // 2 sideways directions
                int dx2 = -dy1;
                int dy2 = dx1;

                Position end1 = new Position(mid.getX() + dx2, mid.getY() + dy2);
                Position end2 = new Position(mid.getX() - dx2, mid.getY() - dy2);

                if (grid.isValid(end1) && isValidTarget(grid.getEntityAt(end1))) {
                    specials.add(end1);
                }
                if (grid.isValid(end2) && isValidTarget(grid.getEntityAt(end2))) {
                    specials.add(end2);
                }
            }
        }
        return specials;
    }
}

