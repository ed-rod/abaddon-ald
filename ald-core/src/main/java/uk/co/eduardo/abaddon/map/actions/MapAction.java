package uk.co.eduardo.abaddon.map.actions;

import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Certain tiles on the map have some actions
 *
 * @author Ed
 */
public abstract class MapAction
{
   // ================| Fields |====================================

   /** The coordinate on the map for which this action occurs */
   private Coordinate source;

   /** The action type. */
   private final int actionType;

   // ================| Constructors |====================================

   /**
    * @param source the map coordinate where this tile action occurs
    * @param actionType the action identifier type.
    */
   public MapAction( final Coordinate source, final int actionType )
   {
      this.source = source;
      this.actionType = actionType;
   }

   // ================| Public Methods |====================================

   /**
    * @return the coordinate where the action occurs
    */
   public Coordinate getSource()
   {
      return this.source;
   }

   /**
    * @param source the tile coordinate that triggers the action
    */
   public void setSource( final Coordinate source )
   {
      this.source = source;
   }

   /**
    * @return the action type for this action
    */
   public int getActionType()
   {
      return this.actionType;
   }
}
