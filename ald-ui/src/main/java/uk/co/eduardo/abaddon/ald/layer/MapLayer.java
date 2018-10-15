package uk.co.eduardo.abaddon.ald.layer;

import java.awt.Cursor;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.MapData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * The {@link MapPanel} renders all layers
 *
 * @author Ed
 */
public abstract class MapLayer
{
   /** Control key depressed. */
   protected static int CONTROL = 0x01;

   /** Alt key depressed. */
   protected static int ALT = 0x02;

   // Implicitly, a click mouse that does't have the right or middle modifiers set is a left-click
   /** Right click. */
   protected static int RIGHT_CLICK = 0x04;

   /** Middle click. */
   protected static int MIDDLE_CLICK = 0x08;

   /** Double click. */
   protected static int DOUBLE_CLICK = 0x10;

   private final MapData mapData;

   private final PropertyModel model;

   private final JComponent host;

   /**
    * @param model the current model.
    * @param host the host for the layer.
    */
   public MapLayer( final PropertyModel model, final JComponent host )
   {
      this.model = model;
      this.host = host;
      this.mapData = model.get( Properties.MapData );
      updateAll();
   }

   /**
    * Called when the layer is asked to render itself.
    *
    * @param g2d the graphics on which to paint.
    */
   public void paint( final Graphics2D g2d )
   {
      // Do nothing
   }

   /**
    * Called when a portion of the map needs to be updated. The portion to update is a rectangular area defined in tiles.
    *
    * @param startX the X tile coordinate of the top-left of the dirty rectangle.
    * @param startY the Y tile coordinate of the top-left of the dirty rectangle.
    * @param width the number of tiles wide the dirty rectangle is.
    * @param height the number of tiles high the dirty rectangle is.
    */
   public void updateSection( final int startX, final int startY, final int width, final int height )
   {
      // Do nothing
   }

   /**
    * Notification that the mouse has moved over the specified tile.
    *
    * @param tileX the X tile coordinate of the tile which was moved over
    * @param tileY the Y tile coordinate of the tile which was moved over
    * @param modifiers bitmask of modifiers.
    */
   public void moved( final int tileX, final int tileY, final int modifiers )
   {
      // Do nothing
   }

   /**
    * Notification that the specified tile was clicked.
    *
    * @param tileX the X tile coordinate of the tile which was clicked
    * @param tileY the Y tile coordinate of the tile which was clicked
    * @param modifiers bitmask of modifiers.
    */
   public void clicked( final int tileX, final int tileY, final int modifiers )
   {
      // Do nothing
   }

   /**
    * Notification that a drag started on the specified coordinate.
    *
    * @param tileX the X tile coordinate of the tile which the drag was initiated
    * @param tileY the Y tile coordinate of the tile which the drag was initiated
    * @param modifiers bitmask of modifiers.
    */
   public void dragStart( final int tileX, final int tileY, final int modifiers )
   {
      // Do nothing
   }

   /**
    * Notification that a drag is underway.
    *
    * @param tileX the X tile coordinate of the tile which is being dragged over
    * @param tileY the Y tile coordinate of the tile which is being dragged over
    * @param modifiers bitmask of modifiers.
    */
   public void drag( final int tileX, final int tileY, final int modifiers )
   {
      // Do nothing
   }

   /**
    * Notification that a drag ended on the specified coordinate.
    *
    * @param tileX the X tile coordinate of the tile on which the drag ended
    * @param tileY the Y tile coordinate of the tile on which the drag ended
    * @param modifiers bitmask of modifiers.
    */
   public void dragEnd( final int tileX, final int tileY, final int modifiers )
   {
      // Do nothing
   }

   /**
    * Notification that the mouse is no longer over the layer.
    *
    * @param modifiers bitmask of modifiers.
    */
   public void exited( final int modifiers )
   {
      // Do nothing.
   }

   /**
    * Override this method to detach any listeners installed.
    */
   public void detach()
   {
      // Empty
   }

   /**
    * Override and return non-null value for a custom cursor.
    *
    * @return a custom cursor for a layer.
    */
   public Cursor getCursor()
   {
      return null;
   }

   /**
    * @return the current map data.
    */
   protected final MapData getMapData()
   {
      return this.mapData;
   }

   /**
    * @return the host component for this layer.
    */
   protected final JComponent getHost()
   {
      return this.host;
   }

   /**
    * @return the current model.
    */
   protected final PropertyModel getModel()
   {
      return this.model;
   }

   /**
    * call to update the display
    */
   protected final void updateAll()
   {
      updateSection( 0, 0, getMapData().getWidth(), getMapData().getHeight() );
   }
}
