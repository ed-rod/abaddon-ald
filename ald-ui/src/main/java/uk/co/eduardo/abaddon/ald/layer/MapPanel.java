package uk.co.eduardo.abaddon.ald.layer;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import uk.co.eduardo.abaddon.ald.data.MapData;
import uk.co.eduardo.abaddon.ald.data.MapDataListener;
import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Host for {@link MapLayer}s.
 *
 * @author Ed
 */
public class MapPanel extends JComponent implements Scrollable
{
   // Tried the EnumMap but it always returns null
   private final Map< Integer, List< MapLayer > > layerMap = new HashMap<>();

   private final MapMouseListener mouseListener = new MapMouseListener();

   private final MapDataListener mapDataListener = new MapDataListener()
   {
      @Override
      public void mapUpdated( final int startTileX, final int startTileY, final int width, final int height )
      {
         for( final MapLayer layer : getLayers() )
         {
            layer.updateSection( startTileX, startTileY, width, height );
         }
      }
   };

   private final MapData mapData;

   private TilesetData tilesetData = null;

   private Collection< MapLayer > cachedSortedLayers;

   /**
    * @param model the current model.
    */
   public MapPanel( final PropertyModel model )
   {
      this.tilesetData = model.get( Properties.Tileset );
      this.mapData = model.get( Properties.MapData );
      this.mapData.addMapDataListener( this.mapDataListener );
      model.addPropertyListener( Properties.Tileset, new PropertyListener()
      {
         @Override
         public void propertyChanged( final PropertyModel s )
         {
            MapPanel.this.tilesetData = s.get( Properties.Tileset );
         }
      } );
   }

   /**
    * Adds a layer to the map panel
    *
    * @param level the level at which the layer should be added.
    * @param layer the layer to add
    */
   public void addLayer( final Level level, final MapLayer layer )
   {
      List< MapLayer > layers = this.layerMap.get( level.ordinal() );
      if( layers == null )
      {
         layers = new ArrayList<>();
         this.layerMap.put( level.ordinal(), layers );
      }
      if( ( layer != null ) && !layers.contains( layer ) )
      {
         layers.add( layer );
         this.cachedSortedLayers = null;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      super.addNotify();
      addMouseListener( this.mouseListener );
      addMouseMotionListener( this.mouseListener );
      for( final MapLayer layer : getLayers() )
      {
         layer.updateAll();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      removeMouseListener( this.mouseListener );
      removeMouseMotionListener( this.mouseListener );
      super.removeNotify();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Dimension getPreferredSize()
   {
      if( this.tilesetData == null )
      {
         return new Dimension( 0, 0 );
      }

      final int width = this.mapData.getData()[ 0 ][ 0 ].length * this.tilesetData.getTileWidth();
      final int height = this.mapData.getData()[ 0 ].length * this.tilesetData.getTileHeight();
      return new Dimension( width, height );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Cursor getCursor()
   {
      Cursor cursor = super.getCursor();
      for( final MapLayer layer : getLayers() )
      {
         if( layer.getCursor() != null )
         {
            cursor = layer.getCursor();
         }
      }
      return cursor;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void paintComponent( final Graphics g )
   {
      for( final MapLayer layer : getLayers() )
      {
         layer.paint( (Graphics2D) g );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Dimension getPreferredScrollableViewportSize()
   {
      return new Dimension( 0, 0 );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getScrollableUnitIncrement( final Rectangle visibleRect, final int orientation, final int direction )
   {
      return getScrollableIncrement( visibleRect, orientation, direction, 1 );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getScrollableBlockIncrement( final Rectangle visibleRect, final int orientation, final int direction )
   {
      return getScrollableIncrement( visibleRect, orientation, direction, 3 );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean getScrollableTracksViewportWidth()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean getScrollableTracksViewportHeight()
   {
      return false;
   }

   private int getScrollableIncrement( final Rectangle visibleRect, final int orientation, final int direction, final int count )
   {
      // Try to scroll by one tile
      final Dimension dim = getPreferredSize();
      final int x = visibleRect.x + visibleRect.width;
      final int y = visibleRect.y + visibleRect.height;
      final int tileWidth = this.tilesetData == null ? 0 : this.tilesetData.getTileWidth();
      final int tileHeight = this.tilesetData == null ? 0 : this.tilesetData.getTileHeight();

      if( orientation == SwingConstants.HORIZONTAL )
      {
         if( direction < 0 )
         {
            // Left
            return Math.min( tileWidth * count, visibleRect.x );
         }
         // Right
         return Math.min( tileWidth * count, dim.width - x );
      }
      // Vertical
      if( direction < 0 )
      {
         // Up
         return Math.min( tileHeight * count, visibleRect.y );
      }
      // Down
      return Math.min( tileHeight * count, dim.height - y );
   }

   private Collection< MapLayer > getLayers()
   {
      if( this.cachedSortedLayers == null )
      {
         final Collection< MapLayer > layers = new ArrayList<>();
         for( final Level level : Level.values() )
         {
            if( this.layerMap.get( level.ordinal() ) != null )
            {
               layers.addAll( this.layerMap.get( level.ordinal() ) );
            }
         }
         this.cachedSortedLayers = layers;
      }
      return this.cachedSortedLayers;
   }

   private class MapMouseListener extends MouseAdapter
   {
      private boolean dragging = false;

      @Override
      public void mouseClicked( final MouseEvent e )
      {
         if( MapPanel.this.tilesetData == null )
         {
            return;
         }
         final int x = getTileCoordinate( e ).x;
         final int y = getTileCoordinate( e ).y;
         final int modifiers = getModifiers( e );
         for( final MapLayer layer : getLayers() )
         {
            layer.clicked( x, y, modifiers );
         }
      }

      @Override
      public void mouseDragged( final MouseEvent e )
      {
         if( MapPanel.this.tilesetData == null )
         {
            return;
         }
         if( !this.dragging )
         {
            try
            {
               // Notify drag start.
               final int x = getTileCoordinate( e ).x;
               final int y = getTileCoordinate( e ).y;
               final int modifiers = getModifiers( e );
               for( final MapLayer layer : getLayers() )
               {
                  layer.dragStart( x, y, modifiers );
               }
            }
            finally
            {
               this.dragging = true;
            }
         }
         final int x = getTileCoordinate( e ).x;
         final int y = getTileCoordinate( e ).y;
         final int modifiers = getModifiers( e );
         for( final MapLayer layer : getLayers() )
         {
            layer.drag( x, y, modifiers );
         }
      }

      @Override
      public void mouseReleased( final MouseEvent e )
      {
         if( MapPanel.this.tilesetData == null )
         {
            return;
         }
         if( this.dragging )
         {
            try
            {
               // Notify drag end.
               final int x = getTileCoordinate( e ).x;
               final int y = getTileCoordinate( e ).y;
               final int modifiers = getModifiers( e );
               for( final MapLayer layer : getLayers() )
               {
                  layer.dragEnd( x, y, modifiers );
               }
            }
            finally
            {
               this.dragging = false;
            }
         }
      }

      @Override
      public void mouseMoved( final MouseEvent e )
      {
         if( MapPanel.this.tilesetData == null )
         {
            return;
         }
         final int x = getTileCoordinate( e ).x;
         final int y = getTileCoordinate( e ).y;
         final int modifiers = getModifiers( e );
         for( final MapLayer layer : getLayers() )
         {
            layer.moved( x, y, modifiers );
         }
      }

      @Override
      public void mouseExited( final MouseEvent e )
      {
         final int modifiers = getModifiers( e );
         for( final MapLayer layer : getLayers() )
         {
            layer.exited( modifiers );
         }
      }

      private Coordinate getTileCoordinate( final MouseEvent event )
      {
         if( MapPanel.this.tilesetData == null )
         {
            return null;
         }

         return new Coordinate( event.getX() / MapPanel.this.tilesetData.getTileWidth(),
                                event.getY() / MapPanel.this.tilesetData.getTileHeight() );
      }

      private int getModifiers( final MouseEvent event )
      {
         int modifiers = 0;

         // Right click
         modifiers = modifiers | ( SwingUtilities.isRightMouseButton( event ) ? MapLayer.RIGHT_CLICK : 0 );

         // Middle click
         modifiers = modifiers | ( SwingUtilities.isMiddleMouseButton( event ) ? MapLayer.MIDDLE_CLICK : 0 );

         // Double-click
         modifiers = modifiers | ( event.getClickCount() > 1 ? MapLayer.DOUBLE_CLICK : 0 );

         // Alt key pressed
         modifiers = modifiers | ( ( event.getModifiers() & InputEvent.ALT_MASK ) != 0 ? MapLayer.ALT : 0 );

         // Control key pressed
         modifiers = modifiers | ( ( event.getModifiers() & InputEvent.CTRL_MASK ) != 0 ? MapLayer.CONTROL : 0 );

         return modifiers;
      }
   }
}
