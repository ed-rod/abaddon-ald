package uk.co.eduardo.abaddon.ald.layer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Abstract layer for drawing sprites on the map. This layer will only draw sprites defined on a particular map layer.
 *
 * @author Ed
 */
public abstract class SpriteDisplaylayer extends AbstractTilesetAwareLayer
{
   private final int layerIndex;

   private final Property< Boolean > visibleProperty;

   /**
    * Initializes a layer that displays all sprites that exist on the map layer <code>layerIndex</code>.
    *
    * @param model the current moddel.
    * @param host the host for the layer.
    * @param layerIndex the layer index.
    * @param tilesetProperty property for the currently selected tileset.
    * @param visibleProperty property that determines whether to draw the sprites or not.
    */
   public SpriteDisplaylayer( final PropertyModel model,
                              final JComponent host,
                              final int layerIndex,
                              final Property< TilesetData > tilesetProperty,
                              final Property< Boolean > visibleProperty )
   {
      super( model, host, tilesetProperty );
      this.layerIndex = layerIndex;
      this.visibleProperty = visibleProperty;
   }

   /**
    * @return whether this layer is visible or not.
    */
   protected boolean isVisible()
   {
      return getModel().get( this.visibleProperty );
   }

   /**
    * This layer will render all sprites that exist on this layer index.
    *
    * @return the layer index.
    */
   protected int getLayerIndex()
   {
      return this.layerIndex;
   }

   /**
    * @param g2d the graphics onto which the sprite is to be drawn.
    * @param spriteImage the sprite image to draw.
    * @param position the sprite tile coordinate.
    */
   protected void drawSprite( final Graphics2D g2d, final BufferedImage spriteImage, final Coordinate position )
   {
      final Rectangle rect = getDisplayPixelBounds( spriteImage, position );
      g2d.drawImage( spriteImage, rect.x, rect.y, null );
   }

   /**
    * @param image the sprite image to display
    * @param position the tile coordinates of the image.
    * @return the rectangular bounds in screen pixel coordinates to display the sprite.
    */
   protected Rectangle getDisplayPixelBounds( final BufferedImage image, final Coordinate position )
   {
      final int heightDifference = image.getHeight() - getTilesetData().getTileHeight();
      final int offset = heightDifference + ( heightDifference / 2 );

      final int x = position.x * getTilesetData().getTileWidth();
      final int y = ( position.y * getTilesetData().getTileHeight() ) - offset;

      return new Rectangle( x, y, image.getWidth(), image.getHeight() );
   }
}
