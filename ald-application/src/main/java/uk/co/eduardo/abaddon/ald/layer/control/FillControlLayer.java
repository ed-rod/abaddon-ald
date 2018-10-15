package uk.co.eduardo.abaddon.ald.layer.control;

import java.awt.Cursor;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import uk.co.eduardo.abaddon.ald.data.MapData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManager;
import uk.co.eduardo.abaddon.ald.layer.MapLayer;
import uk.co.eduardo.abaddon.ald.ui.Cursors;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Performs a flood fill using the currently selected tiles.
 *
 * @author Ed
 */
public class FillControlLayer extends MapLayer
{
   private final Property< int[] > selectedTileProperty;

   private final Property< Integer > activeLayerProperty;

   private final UndoManager undoManager;

   private final Property< Boolean > visibleProperty;

   /**
    * @param model the current model
    * @param host the host for the layer.
    * @param activeLayerProperty property for the currently selected layer.
    * @param visibleProperty property for the visibility of the layers.
    * @param selectedTileProperty property for the currently selected tiles.
    * @param undoManagerProperty property for the undo manager.
    */
   public FillControlLayer( final PropertyModel model,
                            final JComponent host,
                            final Property< Integer > activeLayerProperty,
                            final Property< Boolean > visibleProperty,
                            final Property< int[] > selectedTileProperty,
                            final Property< UndoManager > undoManagerProperty )
   {
      super( model, host );
      this.activeLayerProperty = activeLayerProperty;
      this.visibleProperty = visibleProperty;
      this.selectedTileProperty = selectedTileProperty;
      this.undoManager = model.get( undoManagerProperty );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Cursor getCursor()
   {
      return Cursors.FILL;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void clicked( final int tileX, final int tileY, final int modifiers )
   {
      // Only accept single left click
      if( ( ( modifiers & RIGHT_CLICK ) != 0 ) || ( ( modifiers & MIDDLE_CLICK ) != 0 ) || ( ( modifiers & DOUBLE_CLICK ) != 0 ) )
      {
         return;
      }
      final int[] tileIds = getModel().get( this.selectedTileProperty );
      if( ( tileIds == null ) || ( tileIds.length == 0 ) )
      {
         return;
      }
      final int selectedLayer = getModel().get( this.activeLayerProperty );
      // Check to see if this layer is visible
      final Property< Boolean > layerVisibilityProperty = Property.getLayerProperty( selectedLayer, this.visibleProperty );

      if( !getModel().get( layerVisibilityProperty ) )
      {
         // TODO Show error.
         return;
      }

      final Coordinate seed = new Coordinate( tileX, tileY );
      final FloodFillerContext filler = new FloodFillerContext( getMapData(), selectedLayer );
      final UndoableEdit edit = filler.floodFill( seed, tileIds );
      if( edit != null )
      {
         this.undoManager.addEdit( edit );
      }
   }

   private static final class FloodFillerContext
   {
      private final int layer;

      private final MapData mapData;

      private FloodFillerContext( final MapData mapData, final int layer )
      {
         this.mapData = mapData;
         this.layer = layer;
      }

      UndoableEdit floodFill( final Coordinate seed, final int[] newTiles )
      {
         final int width = this.mapData.getWidth();
         final int height = this.mapData.getHeight();

         if( ( seed.x < 0 ) || ( seed.x >= width ) || ( seed.y < 0 ) || ( seed.y >= height ) )
         {
            // Do nothing.
            return null;
         }

         final int[][] map = this.mapData.getData()[ this.layer ];
         final int current = map[ seed.y ][ seed.x ];

         final CompoundEdit edit = new CompoundEdit();
         flood( map, seed.x, seed.y, newTiles, current, edit );
         edit.end();
         return edit;
      }

      private void flood( final int[][] map,
                          final int x,
                          final int y,
                          final int[] newTiles,
                          final int old,
                          final CompoundEdit edit )
      {
         final int width = this.mapData.getWidth();
         final int height = this.mapData.getHeight();

         // Scan left filling along the way.
         int fillL = x;
         do
         {
            set( fillL--, y, newTiles, edit );
         }
         while( ( fillL >= 0 ) && ( map[ y ][ fillL ] == old ) );
         fillL++;

         // Scan right filling along the way.
         int fillR = x;
         do
         {
            set( fillR++, y, newTiles, edit );
         }
         while( ( fillR < width ) && ( map[ y ][ fillR ] == old ) );
         fillR--;

         // Iterate along the scanline checking above and below each point
         // to see if a scanline can be drawn there.
         for( int i = fillL; i <= fillR; i++ )
         {
            if( ( y > 0 ) && ( map[ y - 1 ][ i ] == old ) )
            {
               flood( map, i, y - 1, newTiles, old, edit );
            }
            if( ( y < height ) && ( map[ y + 1 ][ i ] == old ) )
            {
               flood( map, i, y + 1, newTiles, old, edit );
            }
         }
      }

      private void set( final int x, final int y, final int[] newTiles, final CompoundEdit edit )
      {
         final Random rng = new Random();
         final int nextTile = newTiles[ rng.nextInt( newTiles.length ) ];
         final UndoableEdit newEdit = this.mapData.setData( this.layer, y, x, nextTile );
         if( newEdit != null )
         {
            edit.addEdit( newEdit );
         }
      }
   }
}
