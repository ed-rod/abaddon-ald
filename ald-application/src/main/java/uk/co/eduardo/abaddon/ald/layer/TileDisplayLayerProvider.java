package uk.co.eduardo.abaddon.ald.layer;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * Installs the layers that display the map data.
 *
 * @author Ed
 */
public class TileDisplayLayerProvider implements LayerProvider
{
   /**
    * {@inheritDoc}
    */
   @Override
   public void populateLayerHost( final MapPanel host, final PropertyModel model )
   {
      for( int layerIndex = 0; layerIndex < 3; layerIndex++ )
      {
         final Property< Boolean > mapVisible = Property.getLayerProperty( layerIndex, Properties.LayerVisible );

         host.addLayer( Level.Display, new TileDisplayLayer( model, host, layerIndex, Properties.Tileset, mapVisible ) );

         host.addLayer( Level.Display,
                        new HeroDisplayLayer( model,
                                              host,
                                              layerIndex,
                                              Properties.Tileset,
                                              Properties.SpriteVisible,
                                              Properties.HeaderData,
                                              Properties.ProjectSettings ) );

         host.addLayer( Level.Display,
                        new NpcDisplayLayer( model,
                                             host,
                                             layerIndex,
                                             Properties.Tileset,
                                             Properties.SpriteVisible,
                                             Properties.NpcData,
                                             Properties.ProjectSettings ) );
      }
   }
}
