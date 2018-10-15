package uk.co.eduardo.abaddon.ald.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.utils.TileConversionUtilities;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Abstract layer that supports dragging a rectangular regions.
 *
 * @author Ed
 */
public abstract class AbstractDraggingLayer extends AbstractTilesetAwareLayer
{
   private Coordinate start;

   private Coordinate end;

   private final boolean drawDragRect;

   private final boolean constrainToBounds;

   /**
    * @param model the current model
    * @param host the host for the layer.
    * @param tilesetProperty property for the currently selected tileset
    * @param drawDragRect whether to draw the dragging rectangle
    * @param constrainToBounds whether the drag events should be constrained to the current map width and height.
    */
   public AbstractDraggingLayer( final PropertyModel model,
                                 final JComponent host,
                                 final Property< TilesetData > tilesetProperty,
                                 final boolean drawDragRect,
                                 final boolean constrainToBounds )
   {
      super( model, host, tilesetProperty );
      this.drawDragRect = drawDragRect;
      this.constrainToBounds = constrainToBounds;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void dragStart( final int tileX, final int tileY, final int modifiers )
   {
      if( ( getMapData() == null ) || ( getTilesetData() == null ) )
      {
         return;
      }
      this.start = constrainPoint( new Coordinate( tileX, tileY ) );
      this.end = constrainPoint( new Coordinate( tileX, tileY ) );
      dragUpdate( tileX, tileY, 1, 1, modifiers );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void drag( final int tileX, final int tileY, final int modifiers )
   {
      final Coordinate old = this.end;
      this.end = constrainPoint( new Coordinate( tileX, tileY ) );
      if( !this.end.equals( old ) )
      {
         final Rectangle dirtyRect = TileConversionUtilities.pixelUnion( getTilesetData(), old, this.start, this.end );

         final Rectangle tileRect = TileConversionUtilities.tileUnion( this.start, this.end );
         dragUpdate( tileX, tileY, tileRect.width, tileRect.height, modifiers );
         if( dirtyRect != null )
         {
            getHost().repaint( dirtyRect );
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void dragEnd( final int tileX, final int tileY, final int modifiers )
   {
      final Rectangle pixelBounds = TileConversionUtilities.pixelUnion( getTilesetData(), this.start, this.end );
      final Rectangle tileBounds = TileConversionUtilities.tileUnion( this.start, this.end );

      this.start = null;
      this.end = null;

      if( ( pixelBounds != null ) && ( tileBounds != null ) )
      {
         getHost().repaint();
         dragComplete( tileBounds.x, tileBounds.y, tileBounds.width, tileBounds.height, modifiers );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void paint( final Graphics2D g2d )
   {
      if( !this.drawDragRect )
      {
         return;
      }
      if( ( this.start == null ) || ( this.end == null ) )
      {
         return;
      }
      final Color oldColor = g2d.getColor();

      // Red outer box
      g2d.setColor( Color.red );
      final Rectangle rect = TileConversionUtilities.pixelUnion( getTilesetData(), this.start, this.end );
      g2d.drawRect( rect.x, rect.y, rect.width - 1, rect.height - 1 );

      // yellow inner line
      g2d.setColor( Color.yellow );
      g2d.drawRect( rect.x + 1, rect.y + 1, rect.width - 3, rect.height - 3 );

      g2d.setColor( oldColor );
   }

   /**
    * @param tileX the tile x coordinate of the top-left corner of the selection
    * @param tileY the tile y coordinate of the top-left corner of the selection
    * @param width the width of the selection in tiles
    * @param height the height of the selection in tiles
    * @param modifiers bitmask of modifiers
    */
   protected abstract void dragComplete( int tileX, int tileY, int width, int height, final int modifiers );

   /**
    * @param tileX the tile x coordinate of the top-left corner of the selection
    * @param tileY the tile y coordinate of the top-left corner of the selection
    * @param width the width of the selection in tiles
    * @param height the height of the selection in tiles
    * @param modifiers bitmask of modifiers
    */
   protected abstract void dragUpdate( int tileX, int tileY, int width, int height, final int modifiers );

   private Coordinate constrainPoint( final Coordinate point )
   {
      if( ( getMapData() == null ) || ( getTilesetData() == null ) )
      {
         return null;
      }
      if( !this.constrainToBounds )
      {
         return point;
      }
      return new Coordinate( Math.min( Math.max( 0, point.x ), getMapData().getWidth() - 1 ),
                             Math.min( Math.max( 0, point.y ), getMapData().getHeight() - 1 ) );
   }
}
