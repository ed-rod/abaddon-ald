package uk.co.eduardo.abaddon.ald.layer.interaction;

import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Abstract implementation of {@link InteractiveElementProvider}.
 *
 * @author Ed
 */
public abstract class AbstractInteractiveElementProvider implements InteractiveElementProvider
{
   private PropertyModel model;

   /**
    * {@inheritDoc}
    */
   @Override
   public final void setModel( final PropertyModel model )
   {
      if( this.model != null )
      {
         throw new IllegalStateException( "The model can only be set once" ); //$NON-NLS-1$
      }
      this.model = model;
      initialize( model );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public InteractiveElement[] getElements( final Coordinate tile )
   {
      return getElements( tile, 1, 1 );
   }

   /**
    * Called when a model has been set on this {@link InteractiveElementProvider}. This method can be overridden to perform any
    * specific intialization;
    *
    * @param currentModel the model that has been set..
    */
   protected void initialize( final PropertyModel currentModel )
   {
      // Do nothing.
   }

   /**
    * @return the current model.
    */
   protected PropertyModel getModel()
   {
      return this.model;
   }
}
