package uk.co.eduardo.abaddon.ald.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.utils.TileConversionUtilities;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Shows a border around the tile currently being hovered over.
 *
 * @author Ed
 */
public class TileHoverLayer extends AbstractTilesetAwareLayer
{
   private static final Color HIGHLIGHT = new Color( 0x2A, 0x6E, 0xBB, 0x7F );

   private Coordinate last = new Coordinate( -1, -1 );

   private final Property< Coordinate > positionProperty;

   /**
    * @param model the current model.
    * @param host the host for the layer.
    * @param tilesetProperty property for the currently selected tileset.
    * @param positionProperty property for the current mouse tile position.
    */
   public TileHoverLayer( final PropertyModel model,
                          final JComponent host,
                          final Property< TilesetData > tilesetProperty,
                          final Property< Coordinate > positionProperty )
   {
      super( model, host, tilesetProperty );
      this.positionProperty = positionProperty;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void moved( final int tileX, final int tileY, final int modifiers )
   {
      if( ( getMapData() != null ) && ( getTilesetData() != null ) )
      {
         final Coordinate old = this.last;

         if( ( tileX != this.last.x ) || ( tileY != this.last.y ) )
         {
            this.last = new Coordinate( tileX, tileY );
            getHost().repaint( TileConversionUtilities.pixelUnion( getTilesetData(), old, this.last ) );
         }

         final Coordinate toSet = this.last.equals( new Coordinate( -1, -1 ) ) ? null : this.last;
         getModel().set( this.positionProperty, toSet );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void paint( final Graphics2D g2d )
   {
      if( ( getMapData() != null ) && ( getTilesetData() != null ) )
      {
         final Color oldColor = g2d.getColor();
         g2d.setColor( HIGHLIGHT );
         final Rectangle rect = TileConversionUtilities.pixelUnion( getTilesetData(), this.last );
         for( int length = 1; length < 8; length++ )
         {
            Corner.TOP_LEFT.draw( g2d, new Point( rect.x, rect.y ), length );
            Corner.TOP_RIGHT.draw( g2d, new Point( ( rect.x + rect.width ) - 1, rect.y ), length );
            Corner.BOTTOM_LEFT.draw( g2d, new Point( rect.x, ( rect.y + rect.height ) - 1 ), length );
            Corner.BOTTOM_RIGHT.draw( g2d, new Point( ( rect.x + rect.width ) - 1, ( rect.y + rect.height ) - 1 ), length );
         }

         g2d.setColor( oldColor );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void exited( final int modifiers )
   {
      moved( -1, -1, modifiers );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void drag( final int tileX, final int tileY, final int modifiers )
   {
      moved( tileX, tileY, modifiers );
   }

   private enum Corner
   {
      TOP_LEFT( new Point( 1, 0 ), new Point( 0, 1 ) ),
      TOP_RIGHT( new Point( -1, 0 ), new Point( 0, 1 ) ),
      BOTTOM_LEFT( new Point( 1, 0 ), new Point( 0, -1 ) ),
      BOTTOM_RIGHT( new Point( -1, 0 ), new Point( 0, -1 ) );

      private final Point dir1;

      private final Point dir2;

      Corner( final Point dir1, final Point dir2 )
      {
         this.dir1 = dir1;
         this.dir2 = dir2;
      }

      void draw( final Graphics2D g, final Point point, final int length )
      {
         final Point p1 = new Point( point.x + ( length * this.dir1.x ), point.y + ( length * this.dir1.y ) );
         final Point p2 = new Point( point.x + ( length * this.dir2.x ), point.y + ( length * this.dir2.y ) );
         g.drawLine( point.x, point.y, p1.x, p1.y );
         g.drawLine( point.x, point.y, p2.x, p2.y );
      }
   }
}
