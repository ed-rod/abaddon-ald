package uk.co.eduardo.abaddon.ald.data.mapmodel;

/**
 * When attached to a {@link PropertyModel} implementors are notified when a property changes.
 *
 * @author Ed
 */
public interface PropertyListener
{
   /**
    * @param model the model in which the property changed.
    */
   void propertyChanged( final PropertyModel model );
}
