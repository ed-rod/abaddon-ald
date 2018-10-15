package uk.co.eduardo.abaddon.ald.layer;

import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * {@link LayerProvider}s populate a {@link MapPanel} with layers.
 *
 * @author Ed
 */
public interface LayerProvider
{
   /**
    * @param host the host to which layers are to be added.
    * @param model the current model.
    */
   void populateLayerHost( final MapPanel host, final PropertyModel model );
}
