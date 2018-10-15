package uk.co.eduardo.abaddon.map.actions;

import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Teleport the character to another tile position in the current or another map
 *
 * @author Ed
 */
public class TeleportAction extends MapAction
{
   // ================| Fields |====================================

   /** The type id for all TeleportActions */
   public static final int TELEPORT_ACTION_TYPE = 0;

   /** The tile coordinate that the hero should teleport to */
   private final Coordinate destination;

   /** The map name of the destination */
   private final String destinationMapName;

   // ================| Constructors |====================================

   /**
    * @param source the position of the action.
    * @param destination the target position on the target map.
    * @param destinationMapName the name of the target map.
    */
   public TeleportAction( final Coordinate source, final Coordinate destination, final String destinationMapName )
   {
      super( source, TELEPORT_ACTION_TYPE );
      this.destination = destination;
      this.destinationMapName = destinationMapName;
   }

   // ================| Public Methods |====================================

   /**
    * @return the destination
    */
   public Coordinate getDestination()
   {
      return this.destination;
   }

   /**
    * @return the destination map name
    */
   public String getDestinationMapName()
   {
      return this.destinationMapName;
   }
}
