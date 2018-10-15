package uk.co.eduardo.abaddon.ald.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.ActionData;
import uk.co.eduardo.abaddon.ald.data.ActionDataListener;
import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.utils.TileConversionUtilities;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Displays action tiles
 *
 * @author Ed
 */
public class ActionDisplayLayer extends AbstractTilesetAwareLayer
{
   /** Color in which the action tiles are displayed. */
   public static final Color ACTION_COLOR = new Color( 128, 128, 128, 128 );

   private final ActionData actionData;

   private final ActionDataListener actionDataListener = new ActionDataListener()
   {
      @Override
      public void actionRemoved( final ActionAdapter action )
      {
         updateDisplay( action );
      }

      @Override
      public void actionAdded( final ActionAdapter action )
      {
         updateDisplay( action );
      }

      @Override
      public void actionUpdated( final ActionAdapter action )
      {
         updateDisplay( action );
      }
   };

   /**
    * @param model the current model
    * @param host the host for the layer.
    * @param tilesetProperty property for the currently selected tileset.
    * @param actionDataProperty property for the action data.
    */
   public ActionDisplayLayer( final PropertyModel model,
                              final JComponent host,
                              final Property< TilesetData > tilesetProperty,
                              final Property< ActionData > actionDataProperty )
   {
      super( model, host, tilesetProperty );
      this.actionData = model.get( actionDataProperty );
      this.actionData.addActionDataListener( this.actionDataListener );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void paint( final Graphics2D g2d )
   {
      for( final ActionAdapter action : this.actionData.getActions() )
      {
         final Coordinate point = new Coordinate( action.getPosition().x, action.getPosition().y );
         final Rectangle rect = TileConversionUtilities.pixelUnion( getTilesetData(), point );

         final Color oldColor = g2d.getColor();
         g2d.setColor( ACTION_COLOR );

         g2d.drawRect( rect.x, rect.y, rect.width - 1, rect.height - 1 );
         g2d.fillRect( rect.x, rect.y, rect.width, rect.height );

         g2d.setColor( oldColor );
      }
   }

   private void updateDisplay( final ActionAdapter action )
   {
      final Point display = TileConversionUtilities.convertToPixel( getTilesetData(), action.getPosition() );
      getHost().repaint( display.x, display.y, getTilesetData().getTileWidth(), getTilesetData().getTileHeight() );
   }
}
