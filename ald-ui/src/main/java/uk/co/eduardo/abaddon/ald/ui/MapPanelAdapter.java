package uk.co.eduardo.abaddon.ald.ui;

import java.util.ServiceLoader;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.layer.ControlLayerProvider;
import uk.co.eduardo.abaddon.ald.layer.LayerProvider;
import uk.co.eduardo.abaddon.ald.layer.Level;
import uk.co.eduardo.abaddon.ald.layer.MapPanel;
import uk.co.eduardo.abaddon.ald.layer.SelectionLayer;

/**
 * Given a map {@link PropertyModel} that has had all the properties installed, this will initialize a {@link MapPanel} with all the
 * necessary layers.
 *
 * @author Ed
 */
public class MapPanelAdapter
{
   private final PropertyModel map;

   /**
    * @param map the map model from which to initialize a new map display panel.
    */
   public MapPanelAdapter( final PropertyModel map )
   {
      this.map = map;
   }

   /**
    * Creates and installs all layers into a new {@link MapPanel}.
    *
    * @return a newly initialized {@link MapPanel} for the map model
    */
   public MapPanel initialize()
   {
      final MapPanel host = new MapPanel( this.map );

      // Add all the layers
      populateHost( this.map, host );

      // Add the control layers.
      populateControls( this.map, host );

      return host;
   }

   private void populateHost( final PropertyModel model, final MapPanel host )
   {
      for( final LayerProvider provider : ServiceLoader.load( LayerProvider.class ) )
      {
         provider.populateLayerHost( host, model );
      }
   }

   private void populateControls( final PropertyModel model, final MapPanel host )
   {
      final SelectionLayer< String > controlLayer = new SelectionLayer<>( model, host, Properties.SelectedControl );

      for( final ControlLayerProvider provider : ServiceLoader.load( ControlLayerProvider.class ) )
      {
         controlLayer.addLayer( provider.getKey(), provider.getControlLayer( model, host ) );
         if( provider.isDefault() )
         {
            model.set( Properties.SelectedControl, provider.getKey() );
         }
      }
      host.addLayer( Level.Overlay, controlLayer );
   }
}
