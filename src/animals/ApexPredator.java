package animals;

import entities.*;
import load.*;
import main.*;
import exception.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for apex predators implementing aggressive
 * target-seeking behavior.
 */

public abstract class ApexPredator extends Animal {

    public ApexPredator(String name, int cooldownDuration) {
        super(name, cooldownDuration);
    }

    @Override
    public boolean isTarget(Entity entity) {
        return entity instanceof Prey || entity instanceof Predator;
    }

    @Override
    protected void eat(Entity target, Grid grid) {
        addPoints(1);  // Apex always gets +1 point, no matter who it eats
        Animal victim = (Animal) target;
        String victimRole = (victim instanceof Prey) ? "Prey" : "Predator";

        GameLogger.log(name + " (Apex Predator) ate " + victim.getName() + " (" + victimRole + ") and gained +1 point");

        victim.addPoints(-1);
        GameLogger.log(victim.getName() + " (" + victimRole + ") was eaten and lost -1 point");

        victim.respawn(grid);
        GameLogger.log(victim.getName() + " (" + victimRole + ") respawned at new location");
    }

    //Computer logic for the apex. Apex hunts the closest target
    @Override
    public void aiMove(Game game) {
        Grid grid = game.getGrid();
        Animal prey = game.getPrey();
        Animal predator = game.getPredator();

        // This set represents the positions the apex can possibly go at the moment
        Set<Position> possible = new HashSet<>(getWalkPositions(grid));

        if (isSpecialAvailable()) {
            possible.addAll(getSpecialPositions(grid));
        }

        if (possible.isEmpty()) return;

        int distPrey = position.distanceTo(prey.getPosition());
        int distPred = position.distanceTo(predator.getPosition());
        // The main target is what is the nearest at the moment
        Animal target = (distPrey <= distPred) ? prey : predator;

        Position best = null;
        int bestDist = 2007; //just a big number so it is bigger than anything, it is reduced below

        for (Position pos : possible) {
            int dist = pos.distanceTo(target.getPosition());
            if (dist < bestDist) {
                bestDist = dist;
                best = pos;
            }
        }

        boolean special = isSpecialAvailable() && best != null && !getWalkPositions(grid).contains(best);
        try {
            if (best != null) {
                performMove(best, grid, special);
            }
        } catch (GameException e) {}
    }
}
