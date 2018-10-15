package uk.co.eduardo.abaddon.ald.layer;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * Layer that has access to the selected tileset.
 *
 * @author Ed
 */
public abstract class AbstractTilesetAwareLayer extends MapLayer
{
   private final PropertyListener tilesetListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel model )
      {
         setTileset( model.get( Properties.Tileset ) );
      }
   };

   private TilesetData tilesetData;

   /**
    * @param model the current model
    * @param host the host for the layer.
    * @param tilesetProperty property for the currently selected tileset.
    */
   public AbstractTilesetAwareLayer( final PropertyModel model,
                                     final JComponent host,
                                     final Property< TilesetData > tilesetProperty )
   {
      super( model, host );
      model.addPropertyListener( tilesetProperty, this.tilesetListener );
      this.tilesetData = model.get( tilesetProperty );
   }

   /**
    * @return the tileset data.
    */
   protected TilesetData getTilesetData()
   {
      return this.tilesetData;
   }

   private final void setTileset( final TilesetData tilesetData )
   {
      this.tilesetData = tilesetData;
      updateAll();
   }
}
