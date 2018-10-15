package uk.co.eduardo.abaddon.ald.data.mapmodel;

import java.util.HashMap;
import java.util.Map;

/**
 * A property model can contain any data. It is a type-safe collection of any data with support for listeners that are notified when
 * any of the properties change.
 * <p>
 * This is <em>NOT</em> thread-safe. It should only be accessed and modified on a single thread to guarantee correctness.
 *
 * @author Ed
 */
public class PropertyModel
{
   private final Map< Property< ? >, PropertyValue< ? > > properties = new HashMap<>();

   /**
    * Adds a listener that will be notified whenever the specified property changes.
    *
    * @param property the property
    * @param listener the listener to add.
    * @param <T> the type of the property to get.
    */
   public < T > void addPropertyListener( final Property< T > property, final PropertyListener listener )
   {
      final PropertyValue< T > propertyValue = getPropertyValue( property );
      propertyValue.addPropertyListener( listener );
   }

   /**
    * Removes a listener.
    *
    * @param property the property being listened to.
    * @param listener the listener to remove.
    * @param <T> the type of the property to get.
    */
   public < T > void removePropertyListener( final Property< T > property, final PropertyListener listener )
   {
      final PropertyValue< T > propertyValue = getPropertyValue( property );
      propertyValue.removePropertyListener( listener );
   }

   /**
    * @param property the property to check for.
    * @return whether the property has been added to the model or not.
    * @param <T> the type of the property.
    */
   public < T > boolean contains( final Property< T > property )
   {
      return this.properties.containsKey( property );
   }

   /**
    * @param property the property to get.
    * @return the current value of the property.
    * @param <T> the type of the property.
    */
   public < T > T get( final Property< T > property )
   {
      if( property == null )
      {
         return null;
      }
      final PropertyValue< T > propertyValue = getPropertyValue( property );
      return propertyValue.getValue();
   }

   /**
    * Adds a property to the model.
    *
    * @param property the property to add.
    * @param value the value of the property.
    * @param <T> the type of the property.
    */
   public < T > void add( final Property< T > property, final T value )
   {
      if( contains( property ) )
      {
         throw new IllegalStateException( String.format( "The property %s already exists", property ) ); //$NON-NLS-1$
      }
      this.properties.put( property, new PropertyValue<>( this, value ) );
   }

   /**
    * @param property the property to set.
    * @param value the value to set.
    * @param <T> the type of the property.
    */
   public < T > void set( final Property< T > property, final T value )
   {
      if( !contains( property ) )
      {
         throw new IllegalStateException( String.format( "The property %s has not been added", property ) ); //$NON-NLS-1$
      }
      final PropertyValue< T > propertyValue = getPropertyValue( property );
      propertyValue.setValue( value );
   }

   @SuppressWarnings( "unchecked" )
   private < T > PropertyValue< T > getPropertyValue( final Property< T > property )
   {
      if( !this.properties.containsKey( property ) )
      {
         throw new IllegalStateException( String.format( "Property %s does not exist", property ) ); //$NON-NLS-1$
      }
      return (PropertyValue< T >) this.properties.get( property );
   }
}
