package uk.co.eduardo.abaddon.ald.layer;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * Adds the layer that watches for changes to the map.
 *
 * @author Ed
 */
public class MarkModifiedLayerProvider implements LayerProvider
{
   /**
    * {@inheritDoc}
    */
   @Override
   public void populateLayerHost( final MapPanel host, final PropertyModel model )
   {
      host.addLayer( Level.Editor, new MarkModifiedLayer( model, host, Properties.UndoManager, Properties.UncommittedChanges ) );
   }
}
