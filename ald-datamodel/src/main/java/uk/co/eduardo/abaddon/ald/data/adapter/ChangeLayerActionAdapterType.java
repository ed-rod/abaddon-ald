package uk.co.eduardo.abaddon.ald.data.adapter;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.map.actions.ChangeLayerAction;
import uk.co.eduardo.abaddon.map.actions.MapAction;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Saves/restores a {@link ChangeLayerAction}.
 *
 * @author Ed
 */
public class ChangeLayerActionAdapterType extends AbstractActionAdapterType
{
   /**
    * Property for the change layer action layer index.
    */
   public static final Property< Integer > CLA_LAYER_INDEX = new Property<>( "CLALayerIndex" ); //$NON-NLS-1$

   /**
    * {@inheritDoc}
    */
   @Override
   public int getSupportedType()
   {
      return ChangeLayerAction.CHANGE_LAYER_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void saveSpecializationParameters( final MapAction action, final PropertyModel properties )
   {
      if( action instanceof ChangeLayerAction )
      {
         final ChangeLayerAction changeAction = (ChangeLayerAction) action;
         checkAndSet( properties, CLA_LAYER_INDEX, changeAction.getLayerIndex() );
      }
      else
      {
         throw new IllegalArgumentException( "Only ChangeLayerActions are supported" ); //$NON-NLS-1$
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ChangeLayerAction createAction( final Coordinate source, final PropertyModel properties )
   {
      return new ChangeLayerAction( source, properties.get( CLA_LAYER_INDEX ) );
   }
}
