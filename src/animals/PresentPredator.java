package animals;

import main.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Present-era predator with adaptive movement capabilities
 * based on situational conditions.
 */

public class PresentPredator extends Predator {

    public PresentPredator(String name) {
        // 0 means no cooldown tracking needed
        super(name, 0);
    }

    @Override
    public Set<Position> getSpecialPositions(Grid grid) {
        // For PresentPredator this returns an empty set because its special is different and is not triggered
        // For the special ability of PresentPredator, there should be another method which may also take apex's positon as a parameter
        return new HashSet<>();
    }

    // Special method we use only in main.Game class to get double-move positions when near Apex
    public Set<Position> getDoubleMovePositions(Grid grid, Position apexPose) {
        Set<Position> doubles = new HashSet<>();

        // Check if Apex is adjacent (8 directions)
        boolean nearApex = false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Position adj = new Position(position.getX() + dx, position.getY() + dy);
                if (adj.equals(apexPose)) {
                    nearApex = true;
                    break;
                }
            }
        }

        if (!nearApex) {
            return doubles;
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                Position newPos = new Position(position.getX() + dx, position.getY() + dy);
                if (grid.isValid(newPos) && isValidTarget(grid.getEntityAt(newPos))) {
                    doubles.add(newPos);
                }
            }
        }

        return doubles;
    }
}