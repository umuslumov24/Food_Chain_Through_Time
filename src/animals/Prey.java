package animals;

import entities.*;
import main.*;
import load.*;
import exception.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for prey animals implementing avoidance
 * and resource-seeking behaviors.
 */

public abstract class Prey extends Animal {

	public Prey(String name, int cooldownDuration) {
		super(name, cooldownDuration);
	}

  @Override
  	protected boolean isTarget(Entity entity) {
	  	return entity instanceof Food;
  	}

  @Override
  	protected void eat(Entity target, Grid grid) {
	  	if (target instanceof Food) {
		  	addPoints(3);
		  	GameLogger.log(name + " (Prey) ate " + target.getName() + " (Food) " + "and gained +3 points");
	
			Food food = (Food) target;
		  	food.respawn(grid);
		  	GameLogger.log(target.getName() + " (Food) respawned at new location");
	  	}
  	}

  /*
   * Computer's moving logic for prey
   * Method is based on a simple algorithm, wants to maximize the distance to the closest predator and minimize the distance to food.
   */
  @Override
  	public void aiMove(Game game) {
	  
	    Grid grid = game.getGrid();
	    Food food = game.getFood();
	    Animal predator = game.getPredator();
	    Animal apex = game.getApexPredator();
	    Set<Position> possibleMoves = new HashSet<>(getWalkPositions(grid));

	    if (isSpecialAvailable()) {
	        possibleMoves.addAll(getSpecialPositions(grid));
	    }
	
	    if (possibleMoves.isEmpty()) {
	    	return;
	    }
	
	    Position bestPos = null;
	    double bestScore = -2007; //just a small number

	    for (Position pos : possibleMoves) {
	    	int distToPred = Math.min(pos.distanceTo(predator.getPosition()), pos.distanceTo(apex.getPosition()));
	    	int distToFood = pos.distanceTo(food.getPosition());
	    	double score = distToPred - distToFood;  //farther from predators, closer to food

	    	if (bestPos == null || score > bestScore) {
	    		bestScore = score;
	    		bestPos = pos;
	    	}
	    }

        boolean isSpecial = bestPos != null && !getWalkPositions(grid).contains(bestPos);
        try {
            if (bestPos != null) {
                performMove(bestPos, grid, isSpecial);
            }
        } catch (GameException e) {
        }
    }
}


