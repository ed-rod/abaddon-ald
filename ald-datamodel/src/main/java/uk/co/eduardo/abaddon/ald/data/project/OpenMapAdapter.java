package uk.co.eduardo.abaddon.ald.data.project;

import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * Basic {@link OpenMapListener} with null implementations of all the methods.
 *
 * @author Ed
 */
public class OpenMapAdapter implements OpenMapListener
{
   /**
    * {@inheritDoc}
    */
   @Override
   public void mapActivated( final PropertyModel activeMap )
   {
      // Nothing.
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void mapInactivated( final PropertyModel inactivated )
   {
      // Nothing.
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void mapOpened( final PropertyModel opened )
   {
      // Nothing.
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void mapClosed( final PropertyModel closed )
   {
      // Nothing.
   }
}
