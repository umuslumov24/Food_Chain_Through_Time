package animals;
import main.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Future-era predator capable of executing high-speed
 * dash movements.
 */

public class FuturePredator extends Predator {

    public FuturePredator(String name) {
        super(name, 2);
    }

   
    @Override
    public Set<Position> getSpecialPositions(Grid grid) {
        Set<Position> specials = new HashSet<>();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx == 0 && dy == 0) continue;
                if (Math.abs(dx) + Math.abs(dy) != 2) continue; // Exactly 2 cells

                Position newPos = new Position(position.getX() + dx, position.getY() + dy);
                if (grid.isValid(newPos) && isValidTarget(grid.getEntityAt(newPos))) {
                    specials.add(newPos);
                }
            }
        }
        return specials;
    }
}
