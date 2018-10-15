package uk.co.eduardo.abaddon.ald.data.adapter;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.map.actions.MapAction;
import uk.co.eduardo.abaddon.map.actions.TeleportAction;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Saves and restores a {@link TeleportAction}.
 *
 * @author Ed
 */
public class TeleportActionAdapterType extends AbstractActionAdapterType
{
   /**
    * Property for the teleport action destination map.
    */
   public static final Property< String > TA_DESTINATION_MAP = new Property<>( "TA DestinationMap" ); //$NON-NLS-1$

   /**
    * Property for the teleport action destination coordinate.
    */
   public static final Property< Coordinate > TA_DESTINATION = new Property<>( "TA Destination" ); //$NON-NLS-1$

   /**
    * {@inheritDoc}
    */
   @Override
   public int getSupportedType()
   {
      return TeleportAction.TELEPORT_ACTION_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void saveSpecializationParameters( final MapAction action, final PropertyModel properties )
   {
      if( action instanceof TeleportAction )
      {
         final TeleportAction teleportAction = (TeleportAction) action;
         checkAndSet( properties, TA_DESTINATION_MAP, teleportAction.getDestinationMapName() );
         checkAndSet( properties, TA_DESTINATION, teleportAction.getDestination() );
      }
      else
      {
         throw new IllegalArgumentException( "Only TeleportActions are supported" ); //$NON-NLS-1$
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TeleportAction createAction( final Coordinate source, final PropertyModel properties )
   {
      return new TeleportAction( source, properties.get( TA_DESTINATION ), properties.get( TA_DESTINATION_MAP ) );
   }
}
