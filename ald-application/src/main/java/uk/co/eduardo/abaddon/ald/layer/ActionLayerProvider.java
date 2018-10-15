package uk.co.eduardo.abaddon.ald.layer;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * Adds layers related to action tiles.
 *
 * @author Ed
 */
public class ActionLayerProvider implements LayerProvider
{
   /**
    * {@inheritDoc}
    */
   @Override
   public void populateLayerHost( final MapPanel host, final PropertyModel model )
   {
      host.addLayer( Level.Overlay, new ActionDisplayLayer( model, host, Properties.Tileset, Properties.ActionData ) );
   }
}
