package animals;

import java.util.HashSet;
import java.util.Set;
import main.*;

/**
 * Past-era predator emphasizing direct and linear
 * pursuit strategies.
 */

public class PastPredator extends Predator {

    public PastPredator(String name) {
        super(name, 2);
    }

    @Override
    public Set<Position> getSpecialPositions(Grid grid) {
        Set<Position> specials = new HashSet<>();
        int[][] directions = {{0,1},{0,-1},{1,0},{-1,0}}; // only straight, no diagonal

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            Position newPos = new Position(position.getX() + 2*dx, position.getY() + 2*dy);
            if (grid.isValid(newPos) && isValidTarget(grid.getEntityAt(newPos))) {
                specials.add(newPos);
            }
        }
        return specials;
    }
}