package uk.co.eduardo.abaddon.ald.layer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.MapData;
import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * This layer is responsible for displaying one of the map layers.
 *
 * @author Ed
 */
public class TileDisplayLayer extends AbstractTilesetAwareLayer
{
   private BufferedImage buffer;

   private final int layerIndex;

   private final Property< Boolean > visibleProperty;

   private TilesetData lastData;

   /**
    * @param model the current model.
    * @param host the host for the layer.
    * @param layerIndex the layer of the map to paint.
    * @param tilesetProperty property for the currently selected tileset.
    * @param visibleProperty the property specifying whether the layer is currently visible or not
    */
   public TileDisplayLayer( final PropertyModel model,
                            final JComponent host,
                            final int layerIndex,
                            final Property< TilesetData > tilesetProperty,
                            final Property< Boolean > visibleProperty )
   {
      super( model, host, tilesetProperty );
      this.layerIndex = layerIndex;
      this.visibleProperty = visibleProperty;

      model.addPropertyListener( this.visibleProperty, new PropertyListener()
      {
         @Override
         public void propertyChanged( final PropertyModel s )
         {
            updateAll();
         }
      } );
      updateAll();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void paint( final Graphics2D g2d )
   {
      if( getModel().get( this.visibleProperty ) && ( this.buffer != null ) )
      {
         g2d.drawImage( this.buffer, 0, 0, null );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void updateSection( final int startX, final int startY, final int width, final int height )
   {
      updateBuffer();
      if( this.buffer != null )
      {
         final Graphics2D g2d = this.buffer.createGraphics();

         int y = startY * getTilesetData().getTileHeight();
         final int[][] layerMap = getMapData().getData()[ this.layerIndex ];
         for( int row = startY; row < ( startY + height ); row++ )
         {
            int x = startX * getTilesetData().getTileWidth();
            final int[] rowMap = layerMap[ row ];
            for( int col = startX; col < ( startX + width ); col++ )
            {
               final int tileId = rowMap[ col ];
               // If we're a sparse layer always clear the tile first
               if( this.layerIndex > 0 )
               {
                  clearTile( g2d, x, y );
               }
               if( tileId != -1 )
               {
                  final BufferedImage tile = getTilesetData().getTile( tileId );
                  g2d.drawImage( tile, x, y, null );
               }
               x += getTilesetData().getTileWidth();
            }
            y += getTilesetData().getTileHeight();
         }
         getHost().repaint();
      }
   }

   private void updateBuffer()
   {
      final TilesetData currentTileset = getTilesetData();
      if( currentTileset == null )
      {
         if( this.buffer != null )
         {
            this.buffer = null;
         }
         return;
      }

      if( this.layerIndex >= MapData.MAX_LAYERS )
      {
         return;
      }
      final int tileYCount = getMapData().getHeight();
      final int tileXCount = getMapData().getWidth();

      final int height = tileYCount * currentTileset.getTileHeight();
      final int width = tileXCount * currentTileset.getTileWidth();

      final boolean tilesetChanged = currentTileset != this.lastData;
      this.lastData = currentTileset;
      final boolean dimChanged = this.buffer != null ? ( this.buffer.getHeight() != height ) ||
                                                       ( this.buffer.getWidth() != width ) : true;

      if( dimChanged || tilesetChanged )
      {
         this.buffer = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
      }
   }

   private void clearTile( final Graphics2D g2d, final int xStart, final int yStart )
   {
      final Color oldColor = g2d.getColor();
      final Composite oldComposite = g2d.getComposite();
      g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC ) );
      g2d.setColor( new Color( 0, 0, 0, 0 ) );
      g2d.fillRect( xStart, yStart, getTilesetData().getTileWidth(), getTilesetData().getTileHeight() );
      g2d.setComposite( oldComposite );
      g2d.setColor( oldColor );
   }
}
