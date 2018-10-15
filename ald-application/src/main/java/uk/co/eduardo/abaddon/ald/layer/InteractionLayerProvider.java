package uk.co.eduardo.abaddon.ald.layer;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * Add interaction layers.
 *
 * @author Ed
 */
public class InteractionLayerProvider implements LayerProvider
{
   /**
    * {@inheritDoc}
    */
   @Override
   public void populateLayerHost( final MapPanel host, final PropertyModel model )
   {
      host.addLayer( Level.Editor, new TileHoverLayer( model, host, Properties.Tileset, Properties.MouseTile ) );
   }
}
