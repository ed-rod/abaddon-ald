package uk.co.eduardo.abaddon.ald.ui;

import uk.co.eduardo.abaddon.graphics.layer.Direction;

/**
 * Listener interface for DirectionSelector.
 *
 * @author Ed
 */
public interface DirectionSelectorListener
{
   /**
    * Notification that a new direction has been selected.
    *
    * @param oldDirection the old direction
    * @param newDirection the new direction in which it should face.
    */
   void directionChanged( final Direction oldDirection, final Direction newDirection );
}
