package entities;

import main.*;

/**
 * Base interface for all objects that can exist on the game grid.
 */

public interface Entity {

  // Gets the position of the entity
  Position getPosition();

  // Sets the position of the entity
  void setPosition(Position position);

  // Gets the name of the entity
  String getName();

  // Gets the image path for the entity
  String getImagePath();
}
