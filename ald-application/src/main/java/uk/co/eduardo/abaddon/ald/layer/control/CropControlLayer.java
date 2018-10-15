package uk.co.eduardo.abaddon.ald.layer.control;

import java.awt.Cursor;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.layer.AbstractDraggingLayer;
import uk.co.eduardo.abaddon.ald.ui.Cursors;

/**
 * Allows the user to select the region of the map.
 *
 * @author Ed
 */
public class CropControlLayer extends AbstractDraggingLayer
{
   /**
    * @param model the current map model.
    * @param host the host for the layer.
    * @param tilesetProperty property for the currently selected tileset
    */
   public CropControlLayer( final PropertyModel model, final JComponent host, final Property< TilesetData > tilesetProperty )
   {
      super( model, host, tilesetProperty, false, false );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Cursor getCursor()
   {
      return Cursors.CROP;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void dragComplete( final int tileX, final int tileY, final int width, final int height, final int modifiers )
   {
      // Do nothing.
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void dragUpdate( final int tileX, final int tileY, final int width, final int height, final int modifiers )
   {
      getMapData().setWidthAndHeight( tileX + 1, tileY + 1 );
   }
}
