package uk.co.eduardo.abaddon.ald.data;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import uk.co.eduardo.abaddon.ald.data.utils.TileConversionUtilities;
import uk.co.eduardo.abaddon.util.Coordinate;
import uk.co.eduardo.map.sections.MapSection;

/**
 * Wrapper for the map data being currently edited. the {@link MapSection} is immutable and this is a mutable version from which a
 * {@link MapSection} can be created.
 *
 * @author Ed
 */
public class MapData implements FileSectionAdaptor
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   /** The maximum number of layers supported by the map. */
   public static final int MAX_LAYERS = 3;

   private static final int MAX_COLS = 120;

   private static final int MAX_ROWS = 120;

   private final int[][][] map = new int[ MAX_LAYERS ][ MAX_ROWS ][ MAX_COLS ];

   private final List< MapDataListener > listeners = new CopyOnWriteArrayList<>();

   private final List< Coordinate > updatedTiles = new ArrayList<>();

   private int width;

   private int height;

   /**
    * Creates a new map data
    * <p>
    * if the <code>section</code> is <code>null</code> then a default data is initialized.
    *
    * @param section the section from which to initialize the data.
    */
   public MapData( final MapSection section )
   {
      final int mapWidth = section == null ? 0 : section.getWidth();
      final int mapHeight = section == null ? 0 : section.getHeight();

      final int[][][] original = section == null ? null : section.getMaps();

      for( int layer = 0; layer < MAX_LAYERS; layer++ )
      {
         final int val = layer == 0 ? 0 : -1;
         for( int row = 0; row < mapHeight; row++ )
         {
            for( int col = 0; col < mapWidth; col++ )
            {
               if( original != null )
               {
                  this.map[ layer ][ row ][ col ] = original[ layer ][ row ][ col ];
               }
               else
               {
                  this.map[ layer ][ row ][ col ] = val;
               }
            }
            for( int col = mapWidth; col < MAX_COLS; col++ )
            {
               this.map[ layer ][ row ][ col ] = val;
            }
         }
         for( int row = mapHeight; row < MAX_ROWS; row++ )
         {
            for( int col = 0; col < MAX_COLS; col++ )
            {
               this.map[ layer ][ row ][ col ] = val;
            }
         }
      }
      this.width = section == null ? MAX_COLS / 4 : mapWidth;
      this.height = section == null ? MAX_ROWS / 4 : mapHeight;
   }

   /**
    * Adds a listener that will be notified when the map is updated.
    *
    * @param listener the listener to add.
    */
   public void addMapDataListener( final MapDataListener listener )
   {
      if( ( listener != null ) && !this.listeners.contains( listener ) )
      {
         this.listeners.add( listener );
      }
   }

   /**
    * @param listener the listener to remove
    */
   public void removeMapDataListener( final MapDataListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * @return the width of the map in tiles.
    */
   public int getWidth()
   {
      return this.width;
   }

   /**
    * Set the number of tiles horizontally in the map.
    *
    * @param width the width to set.
    */
   public void setWidth( final int width )
   {
      this.width = width;
      fireMapUpdated( 0, 0, width, this.height );
   }

   /**
    * @return the height of the map in tiles.
    */
   public int getHeight()
   {
      return this.height;
   }

   /**
    * Set the number of tiles vertically in the map.
    *
    * @param height the height to set
    */
   public void setHeight( final int height )
   {
      this.height = height;
      fireMapUpdated( 0, 0, this.width, height );
   }

   /**
    * Sets the width and the height of the map in tiles.
    *
    * @param width the width to set in tiles
    * @param height the height to set in tiles.
    */
   public void setWidthAndHeight( final int width, final int height )
   {
      this.width = width;
      this.height = height;
      fireMapUpdated( 0, 0, width, height );
   }

   /**
    * Get the map data. This is three-dimensional array of the form: map[layer][row][column].
    * <p>
    * This returns the actual underlying map. <em>Take care!!</em> do not reassign these arrays or even modify their values! To do
    * that use the proper {@link #setData(int, int, int, int)} methods to ensure that all listeners are notified of changes made.
    *
    * @return the map data.
    */
   public int[][][] getData()
   {
      return this.map;
   }

   /**
    * @param layer the map layer to update
    * @param row the row within the layer to update.
    * @param col the column within the layer to update.
    * @param value the value to set.
    * @return an edit that enables the change to be undone.
    */
   public UndoableEdit setData( final int layer, final int row, final int col, final int value )
   {
      return setData( layer, row, col, value, false );
   }

   /**
    * @param layer the map layer to update
    * @param row the row within the layer to update.
    * @param col the column within the layer to update.
    * @param newValue the value to set.
    * @param isAdjusting set to <code>true</code> when another setData call is going to be made right after this one. This will
    *           group the edits together to avoid having to notify listeners too often.
    * @return an edit that enables the change to be undone.
    */
   public UndoableEdit setData( final int layer, final int row, final int col, final int newValue, final boolean isAdjusting )
   {
      if( ( col < 0 ) || ( col >= this.width ) || ( row < 0 ) || ( row >= this.height ) )
      {
         return null;
      }
      final int currentValue = this.map[ layer ][ row ][ col ];
      if( currentValue == newValue )
      {
         return null;
      }

      this.map[ layer ][ row ][ col ] = newValue;
      this.updatedTiles.add( new Coordinate( col, row ) );

      if( !isAdjusting )
      {
         try
         {
            final Coordinate[] tiles = this.updatedTiles.toArray( new Coordinate[ 0 ] );
            final Rectangle rect = TileConversionUtilities.tileUnion( tiles );
            fireMapUpdated( rect.x, rect.y, rect.width, rect.height );
         }
         finally
         {
            this.updatedTiles.clear();
         }
      }
      return new MapUndoableEdit( this, layer, new Coordinate( col, row ), currentValue, newValue );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MapSection createFileSection()
   {
      final int[][][] data = new int[ MAX_LAYERS ][ this.height ][ this.width ];
      for( int layer = 0; layer < MAX_LAYERS; layer++ )
      {
         for( int row = 0; row < this.height; row++ )
         {
            for( int col = 0; col < this.width; col++ )
            {
               data[ layer ][ row ][ col ] = this.map[ layer ][ row ][ col ];
            }
         }
      }
      final MapSection mapSection = new MapSection( this.width, this.height, data );
      return mapSection;
   }

   /**
    * Notifies all listeners that an update to the map was made between the specified bounding box.
    *
    * @param x the x position of the tile that is the top-left of the bounding box.
    * @param y the y position of the tile that is the top-left of the bounding box.
    * @param boxWidth the width of the edit in tiles.
    * @param boxHeight the height of the edit in tiles.
    */
   protected void fireMapUpdated( final int x, final int y, final int boxWidth, final int boxHeight )
   {
      for( final MapDataListener listener : this.listeners )
      {
         listener.mapUpdated( x, y, boxWidth, boxHeight );
      }
   }

   private static final class MapUndoableEdit extends AbstractUndoableEdit
   {
      private static final String EDIT_NAME = resources.getString( "uk.co.eduardo.abaddon.undoable.map.edit" ); //$NON-NLS-1$

      private final MapData data;

      private final int layer;

      private final Coordinate tile;

      private final int oldValue;

      private final int newValue;

      private MapUndoableEdit( final MapData data, final int layer, final Coordinate tile, final int oldValue, final int newValue )
      {
         this.data = data;
         this.layer = layer;
         this.tile = tile;
         this.oldValue = oldValue;
         this.newValue = newValue;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         this.data.setData( this.layer, this.tile.y, this.tile.x, this.oldValue );
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         this.data.setData( this.layer, this.tile.y, this.tile.x, this.newValue );
      }

      @Override
      public String getPresentationName()
      {
         return EDIT_NAME;
      }
   }
}
