package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.util.ArrayList;
import java.util.List;

import uk.co.eduardo.abaddon.ald.data.ActionData;
import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Provides the interactive element for modifying map actions.
 *
 * @author Ed
 */
public class ActionInteractiveElementProvider extends AbstractInteractiveElementProvider
{
   private TilesetData tilesetData;

   private ActionData actionData;

   private PropertyModel currentModel;

   private final PropertyListener tilesetListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel model )
      {
         ActionInteractiveElementProvider.this.tilesetData = model.get( Properties.Tileset );
      }
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public InteractiveElement[] getElements( final Coordinate tile, final int width, final int height )
   {
      if( this.tilesetData == null )
      {
         return new InteractiveElement[ 0 ];
      }
      final List< InteractiveElement > elements = new ArrayList<>();
      final int x = tile.x;
      final int y = tile.y;
      for( final ActionAdapter action : this.actionData.getActions() )
      {
         if( ( action.getPosition().x >= x ) && ( action.getPosition().x < ( x + width ) ) && ( action.getPosition().y >= y ) &&
             ( action.getPosition().y < ( y + height ) ) )
         {
            elements.add( new ActionInteractiveElement( action, this.tilesetData ) );
         }
      }
      return elements.toArray( new InteractiveElement[ 0 ] );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void initialize( final PropertyModel newModel )
   {
      if( this.currentModel != null )
      {
         this.currentModel.removePropertyListener( Properties.Tileset, this.tilesetListener );
      }
      this.actionData = newModel.get( Properties.ActionData );
      this.tilesetData = newModel.get( Properties.Tileset );

      this.currentModel = newModel;
      this.currentModel.addPropertyListener( Properties.Tileset, this.tilesetListener );
   }
}
