package uk.co.eduardo.abaddon.ald.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * UI Component that allows for the selection of one or more tiles for use when painting.
 *
 * @author Ed
 */
public class TilesetDisplay extends JComponent
{
   private static final Color OUTER_COLOR = new Color( 255, 0, 0, 200 );

   private static final Color INNER_COLOR = new Color( 255, 255, 0, 150 );

   private final boolean allowMultiSelection;

   private final boolean allowNullSelection;

   private final boolean toggleMode;

   private final PropertyModel model;

   private final Set< Integer > selectedTiles = new HashSet<>();

   private final Property< int[] > selectedTilesProperty;

   private final Property< TilesetData > tilesetProperty;

   private final MouseAdapter mouseListener = new MouseAdapter()
   {
      @Override
      public void mouseReleased( final MouseEvent e )
      {
         final Set< Integer > currentTiles = TilesetDisplay.this.selectedTiles;
         final boolean toggling = ( ( e.getModifiers() & InputEvent.CTRL_MASK ) == 0 ) && !TilesetDisplay.this.toggleMode;

         final int newTile = getTileId( e.getPoint() );
         if( toggling || !TilesetDisplay.this.allowMultiSelection )
         {
            currentTiles.clear();
            currentTiles.add( newTile );
         }
         else
         {
            final boolean allowToRemove = ( currentTiles.size() > 1 ) || TilesetDisplay.this.allowNullSelection;

            // If it's already there then remove it from the selection.
            if( currentTiles.contains( newTile ) && allowToRemove )
            {
               currentTiles.remove( newTile );
            }
            else
            {
               currentTiles.add( newTile );
            }
         }
         updateSelection();
         repaint();
      }
   };

   private final PropertyListener selectedTilesListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel s )
      {
         TilesetDisplay.this.selectedTiles.clear();
         for( final int tileId : s.get( TilesetDisplay.this.selectedTilesProperty ) )
         {
            TilesetDisplay.this.selectedTiles.add( tileId );
         }
         repaint();
      }
   };

   private final PropertyListener tilesetListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel s )
      {
         TilesetDisplay.this.tileset = s.get( TilesetDisplay.this.tilesetProperty );
         TilesetDisplay.this.selectedTiles.clear();
         updateSelection();
      }
   };

   private TilesetData tileset;

   /**
    * Initializes a new tileset display component.
    *
    * @param model the current model.
    * @param tilesetProperty the property for the tileset.
    * @param selectedTilesProperty property for the selected tiles.
    * @param allowMultiSelection whether the user can select multiple tiles or not.
    * @param allowNullSelection whether it's allowed to have zero tiles selected.
    * @param toggleMode whether the tiles are selected in toggle mode. This is only read if <code>allowMultiSelection</code> is
    *           <code>true</code>. This has the effect of always adding to the selection as if the Control key were always pressed.
    */
   public TilesetDisplay( final PropertyModel model,
                          final Property< TilesetData > tilesetProperty,
                          final Property< int[] > selectedTilesProperty,
                          final boolean allowMultiSelection,
                          final boolean allowNullSelection,
                          final boolean toggleMode )
   {
      this.allowMultiSelection = allowMultiSelection;
      this.model = model;
      this.tilesetProperty = tilesetProperty;
      this.selectedTilesProperty = selectedTilesProperty;
      this.allowNullSelection = allowNullSelection;
      this.toggleMode = toggleMode;
      this.tileset = model.get( tilesetProperty );

      model.addPropertyListener( tilesetProperty, this.tilesetListener );

      this.selectedTilesListener.propertyChanged( model );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void paintComponent( final Graphics g )
   {
      g.drawImage( this.tileset.getTileset(), 0, 0, null );
      final Color oldColor = g.getColor();

      for( final Integer tileId : this.selectedTiles )
      {
         final Rectangle bounds = getBounds( tileId );
         g.setColor( OUTER_COLOR );
         g.drawRect( bounds.x, bounds.y, bounds.width - 1, bounds.height - 1 );
         g.setColor( INNER_COLOR );
         g.drawRect( bounds.x + 1, bounds.y + 1, bounds.width - 3, bounds.height - 3 );
      }
      g.setColor( oldColor );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      super.addNotify();
      addMouseListener( this.mouseListener );
      this.model.addPropertyListener( this.selectedTilesProperty, this.selectedTilesListener );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      removeMouseListener( this.mouseListener );
      this.model.removePropertyListener( this.selectedTilesProperty, this.selectedTilesListener );
      super.removeNotify();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Dimension getPreferredSize()
   {
      if( this.tileset != null )
      {
         return new Dimension( this.tileset.getTileset().getWidth(), this.tileset.getTileset().getHeight() );
      }
      return new Dimension( 0, 0 );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Dimension getMinimumSize()
   {
      return getPreferredSize();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Cursor getCursor()
   {
      return Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );
   }

   private int getTileId( final Point point )
   {
      final int xTiles = this.tileset.getTileset().getWidth() / this.tileset.getTileWidth();

      final int x = point.x / this.tileset.getTileWidth();
      final int y = point.y / this.tileset.getTileHeight();

      return ( y * xTiles ) + x;
   }

   private Rectangle getBounds( final Integer tileId )
   {
      final int xTiles = this.tileset.getTileset().getWidth() / this.tileset.getTileWidth();

      final int rows = tileId / xTiles;
      final int cols = tileId % xTiles;

      final int y = rows * this.tileset.getTileHeight();
      final int x = cols * this.tileset.getTileWidth();
      return new Rectangle( x, y, this.tileset.getTileWidth(), this.tileset.getTileHeight() );
   }

   private void updateSelection()
   {
      final int[] tiles = new int[ this.selectedTiles.size() ];
      int count = 0;
      for( final Integer tile : this.selectedTiles )
      {
         tiles[ count++ ] = tile;
      }
      this.model.set( this.selectedTilesProperty, tiles );
   }
}
