package uk.co.eduardo.abaddon.ald.layer;

import java.awt.Graphics2D;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.HeaderData;
import uk.co.eduardo.abaddon.ald.data.HeaderData.HeaderDataListener;
import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.ald.sprite.SpriteUtilities;

/**
 * Layer that draws the hero.
 *
 * @author Ed
 */
public class HeroDisplayLayer extends SpriteDisplaylayer
{
   private final HeaderData headerData;

   private final Property< ProjectSettings > settingsProperty;

   /**
    * Initializes a layer that displays the hero when it has <code>layerIndex</code> number of sparse map layers below it.
    *
    * @param model the current model.
    * @param host the host for the layer.
    * @param layerIndex the map layer index of the player character.
    * @param tilesetProperty property for the currently selected tileset.
    * @param visibleProperty property that determines whether to draw the hero or not.
    * @param headerDataProperty property for the map header data.
    * @param settingsProperty property for the current project settings.
    */
   public HeroDisplayLayer( final PropertyModel model,
                            final JComponent host,
                            final int layerIndex,
                            final Property< TilesetData > tilesetProperty,
                            final Property< Boolean > visibleProperty,
                            final Property< HeaderData > headerDataProperty,
                            final Property< ProjectSettings > settingsProperty )
   {
      super( model, host, layerIndex, tilesetProperty, visibleProperty );
      this.headerData = model.get( headerDataProperty );
      this.settingsProperty = settingsProperty;

      this.headerData.addHeaderDataListener( new HeaderDataListener()
      {
         @Override
         public void headerChanged()
         {
            getHost().repaint();
         }
      } );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void paint( final Graphics2D g2d )
   {
      if( !isVisible() )
      {
         return;
      }
      if( ( getTilesetData() == null ) || ( getHeaderData() == null ) )
      {
         return;
      }
      if( getLayerIndex() == this.headerData.getLayerIndex() )
      {
         drawSprite( g2d,
                     SpriteUtilities.getPCImage( getModel().get( this.settingsProperty ), this.headerData.getDirection() ),
                     getHeaderData().getStartPosition() );
      }
   }

   private HeaderData getHeaderData()
   {
      return this.headerData;
   }
}
