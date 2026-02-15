package animals;
import main.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Past-era apex predator focused on burst mobility
 * through sprint-based movement.
 */

public class PastApexPredator extends ApexPredator {

    public PastApexPredator(String name) {
        super(name, 2);
    }

    @Override
    public Set<Position> getSpecialPositions(Grid grid) {
        Set<Position> specials = new HashSet<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Position newPos = new Position(position.getX() + 2*dx, position.getY() + 2*dy);
                if (grid.isValid(newPos) && isValidTarget(grid.getEntityAt(newPos))) {
                    specials.add(newPos);
                }
            }
        }
        return specials;
    }
}