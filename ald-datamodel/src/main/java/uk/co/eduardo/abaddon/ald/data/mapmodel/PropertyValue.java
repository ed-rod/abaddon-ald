package uk.co.eduardo.abaddon.ald.data.mapmodel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Holds the value associated with a {@link Property} key.
 *
 * @author Ed
 * @param <T> the type of the property
 */
public class PropertyValue< T >
{
   private final PropertyModel model;

   private final List< PropertyListener > listeners = new CopyOnWriteArrayList<>();

   private T value;

   private boolean recursiveCheck;

   /**
    * Creates a property value.
    *
    * @param model the model to which the value has been added.
    * @param value the value of the property.
    */
   public PropertyValue( final PropertyModel model, final T value )
   {
      this.model = model;
      this.value = value;
   }

   /**
    * Adds a listener that will be notified when the property value changes.
    *
    * @param listener the listener to add.
    */
   public void addPropertyListener( final PropertyListener listener )
   {
      if( ( listener != null ) && !this.listeners.contains( listener ) )
      {
         this.listeners.add( listener );
      }
   }

   /**
    * Removes a listener.
    *
    * @param listener the listener to remove.
    */
   public void removePropertyListener( final PropertyListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * @return the value
    */
   public T getValue()
   {
      return this.value;
   }

   /**
    * @param value the value to set.
    */
   public void setValue( final T value )
   {
      if( !sameValue( this.value, value ) )
      {
         if( this.recursiveCheck )
         {
            throw new IllegalStateException( "recursive change forbidden" ); //$NON-NLS-1$
         }
         this.value = value;

         try
         {
            this.recursiveCheck = true;

            for( final PropertyListener listener : this.listeners )
            {
               listener.propertyChanged( this.model );
            }
         }
         finally
         {
            this.recursiveCheck = false;
         }
      }
   }

   private boolean sameValue( final T first, final T second )
   {
      if( ( first == null ) && ( second == null ) )
      {
         return true;
      }
      if( ( first == null ) || ( second == null ) )
      {
         return false;
      }
      return first.equals( second );
   }
}
