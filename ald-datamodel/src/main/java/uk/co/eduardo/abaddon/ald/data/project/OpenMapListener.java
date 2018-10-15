package uk.co.eduardo.abaddon.ald.data.project;

import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * Implementors of this interface (when added to the {@link OpenMapModel}) will be notified when a map model becomes active or
 * becomes inactive.
 *
 * @author Ed
 */
public interface OpenMapListener
{
   /**
    * @param activeMap the map model that has been set as the active map.
    */
   void mapActivated( final PropertyModel activeMap );

   /**
    * @param inactivated the map model that was active but has just become inactive.
    */
   void mapInactivated( final PropertyModel inactivated );

   /**
    * @param opened the map model that has been opened.
    */
   void mapOpened( final PropertyModel opened );

   /**
    * @param closed the map model that has been closed.
    */
   void mapClosed( final PropertyModel closed );
}
