package uk.co.eduardo.abaddon.ald.layer;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * A selection layer can contain may other layers. At most one of the layers is active at any moment. The selected layer is governed
 * by a property.
 *
 * @author Ed
 * @param <T> the type of the key that decides which layer is active.
 */
public class SelectionLayer< T > extends MapLayer
{
   private final Map< T, MapLayer > layers = new HashMap<>();

   private final Property< T > property;

   private MapLayer selectedLayer;

   /**
    * @param model the current model.
    * @param host the host for the layer.
    * @param property the property that governs the selected item.
    */
   public SelectionLayer( final PropertyModel model, final JComponent host, final Property< T > property )
   {
      super( model, host );
      this.property = property;

      model.addPropertyListener( property, new PropertyListener()
      {
         @Override
         public void propertyChanged( final PropertyModel s )
         {
            SelectionLayer.this.selectedLayer = getCurrentLayer();

            getHost().repaint();
         }
      } );
      this.selectedLayer = getCurrentLayer();
   }

   /**
    * @return the property that governs the selected layer.
    */
   public Property< T > getProperty()
   {
      return this.property;
   }

   /**
    * Add a layer to this selection layer. The added layer will be active when the SelectionLayer's property takes on the value of
    * <code>key</code>.
    *
    * @param key the value that will activate the layer
    * @param layer the layer.
    */
   public void addLayer( final T key, final MapLayer layer )
   {
      this.layers.put( key, layer );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void paint( final Graphics2D g2d )
   {
      if( this.selectedLayer != null )
      {
         this.selectedLayer.paint( g2d );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void updateSection( final int startX, final int startY, final int width, final int height )
   {
      if( this.selectedLayer != null )
      {
         this.selectedLayer.updateSection( startX, startY, width, height );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void moved( final int tileX, final int tileY, final int modifiers )
   {
      if( this.selectedLayer != null )
      {
         this.selectedLayer.moved( tileX, tileY, modifiers );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void clicked( final int tileX, final int tileY, final int modifiers )
   {
      if( this.selectedLayer != null )
      {
         this.selectedLayer.clicked( tileX, tileY, modifiers );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void dragStart( final int tileX, final int tileY, final int modifiers )
   {
      if( this.selectedLayer != null )
      {
         this.selectedLayer.dragStart( tileX, tileY, modifiers );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void drag( final int tileX, final int tileY, final int modifiers )
   {
      if( this.selectedLayer != null )
      {
         this.selectedLayer.drag( tileX, tileY, modifiers );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void dragEnd( final int tileX, final int tileY, final int modifiers )
   {
      if( this.selectedLayer != null )
      {
         this.selectedLayer.dragEnd( tileX, tileY, modifiers );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void exited( final int modifiers )
   {
      if( this.selectedLayer != null )
      {
         this.selectedLayer.exited( modifiers );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void detach()
   {
      super.detach();

      for( final MapLayer layer : this.layers.values() )
      {
         layer.detach();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Cursor getCursor()
   {
      if( this.selectedLayer != null )
      {
         return this.selectedLayer.getCursor();
      }
      return super.getCursor();
   }

   private MapLayer getCurrentLayer()
   {
      final T value = getModel().get( this.property );
      return this.layers.get( value );
   }
}
