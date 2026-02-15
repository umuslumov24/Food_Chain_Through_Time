package animals;

import entities.*;
import exception.*;
import load.*;
import main.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for predators implementing prey-seeking and
 * survival-oriented decision logic.
 */

public abstract class Predator extends Animal {

    public Predator(String name, int cooldownDuration) {
        super(name, cooldownDuration);
    }

    @Override
    protected boolean isTarget(Entity entity) {
        return entity instanceof Prey;
    }

    @Override
    protected void eat(Entity target, Grid grid) {
        addPoints(3);
        GameLogger.log(name + " (Predator) ate " + ((Animal)target).getName() + " (Prey) and gained +3 points");

        Animal prey = (Animal) target;
        prey.addPoints(-1);
        GameLogger.log(prey.getName() + " (Prey) was eaten and lost -1 point");

        prey.respawn(grid);
        GameLogger.log(prey.getName() + " (Prey) respawned at new location");
    }

    // Computer moving logic: Hunt the Prey while trying to stay away from Apex
    @Override
    public void aiMove(Game game) {
        Grid grid = game.getGrid();
        Animal prey = game.getPrey();
        Animal apex = game.getApexPredator();

        Set<Position> possible = new HashSet<>(getWalkPositions(grid));

        if (isSpecialAvailable()) {
            possible.addAll(getSpecialPositions(grid));
        }

        // Only for PresentPredator: double move always available if near apex
        if (this instanceof PresentPredator) {
            PresentPredator presentPred = (PresentPredator) this;
            Set<Position> doubleMoves = presentPred.getDoubleMovePositions(grid, apex.getPosition());
            possible.addAll(doubleMoves);
        }

        if (possible.isEmpty()) {
        	return;
        }

        Position best = null;
        double bestScore = -2007; //just a very small number

        for (Position pos : possible) {
            int distToPrey = pos.distanceTo(prey.getPosition());
            int distToApex = pos.distanceTo(apex.getPosition());
            double score = -distToPrey + (distToApex * 0.5);

            if (best == null || score > bestScore) {
                bestScore = score;
                best = pos;
            }
        }

        boolean special = isSpecialAvailable() && best != null && !getWalkPositions(grid).contains(best);
        // For PresentPredator, double move doesn't trigger cooldown
        boolean usingSpecial = special && !(this instanceof PresentPredator);

        try {
            if (best != null) {
                performMove(best, grid, usingSpecial);
            }
        } catch (GameException e) {}
    }
}