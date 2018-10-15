package uk.co.eduardo.abaddon.ald.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import uk.co.eduardo.abaddon.ald.data.MapData;
import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Displays the tile over which the mouse is currently hovering.
 *
 * @author Ed
 */
public class HoverTileDisplay extends JLabel
{
   private final PropertyModel model;

   private final Property< MapData > mapProperty;

   private final Property< TilesetData > tilesetProperty;

   private final Property< Coordinate > positionProperty;

   private final Property< Integer > activeLayerProperty;

   private final PropertyListener updateListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel s )
      {
         updateImage();
      }
   };

   /**
    * @param model the current model.
    * @param mapProperty property for the map data.
    * @param tilesetProperty property for the tileset
    * @param positionProperty property for the current mouse position.
    * @param activeLayerProperty property for the active layer.
    */
   public HoverTileDisplay( final PropertyModel model,
                            final Property< MapData > mapProperty,
                            final Property< TilesetData > tilesetProperty,
                            final Property< Coordinate > positionProperty,
                            final Property< Integer > activeLayerProperty )
   {
      this.model = model;
      this.mapProperty = mapProperty;
      this.tilesetProperty = tilesetProperty;
      this.positionProperty = positionProperty;
      this.activeLayerProperty = activeLayerProperty;

      setBorder( BorderFactory.createLineBorder( Color.black ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      super.addNotify();

      this.model.addPropertyListener( this.tilesetProperty, this.updateListener );
      this.model.addPropertyListener( this.positionProperty, this.updateListener );
      this.model.addPropertyListener( this.activeLayerProperty, this.updateListener );

      updateImage();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      this.model.removePropertyListener( this.tilesetProperty, this.updateListener );
      this.model.removePropertyListener( this.positionProperty, this.updateListener );
      this.model.removePropertyListener( this.activeLayerProperty, this.updateListener );

      super.removeNotify();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Dimension getPreferredSize()
   {
      final TilesetData tileset = this.model.get( this.tilesetProperty );
      if( tileset != null )
      {
         return new Dimension( tileset.getTileWidth(), tileset.getTileHeight() );
      }
      return super.getPreferredSize();
   }

   private void updateImage()
   {
      final MapData mapData = this.model.get( this.mapProperty );
      final TilesetData tileset = this.model.get( this.tilesetProperty );
      final Coordinate position = this.model.get( this.positionProperty );
      final Integer layer = this.model.get( this.activeLayerProperty );

      Icon icon = null;
      if( ( mapData != null ) && ( tileset != null ) && ( position != null ) && ( layer != null ) )
      {
         if( ( position.x != -1 ) && ( position.y != -1 ) )
         {
            final int tileIndex = mapData.getData()[ layer ][ position.y ][ position.x ];
            if( tileIndex >= 0 )
            {
               icon = new ImageIcon( tileset.getTile( tileIndex ) );
            }
         }
      }
      setIcon( icon );
   }
}
