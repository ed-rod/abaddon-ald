package uk.co.eduardo.abaddon.ald.data.mapmodel;

/**
 * Property key that can be added to a {@link PropertyModel}.
 *
 * @author Ed
 * @param <T> the type of the property.
 */
public class Property< T >
{
   private final String key;

   /**
    * @param key the unique key for the property.
    */
   public Property( final String key )
   {
      if( key == null )
      {
         throw new NullPointerException( "Property key cannot be null" ); //$NON-NLS-1$
      }
      this.key = key;
   }

   /**
    * @return the property key.
    */
   public String getKey()
   {
      return this.key;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return this.key;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals( final Object obj )
   {
      if( obj instanceof Property< ? > )
      {
         return ( (Property< ? >) obj ).key.equals( this.key );
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return this.key.hashCode();
   }

   /**
    * Gets a layer-specific version of a property. Normally each property has only one value. Creating layer properties from a base
    * property allows for extending base properties and getting layer-specific values.
    *
    * @param layer the required layer index of the property to get.
    * @param property the raw property
    * @return the layer-specific version of the property.
    * @param <T> the type of the property.
    */
   public static < T > Property< T > getLayerProperty( final int layer, final Property< T > property )
   {
      String newKey = property.getKey();
      if( layer > 0 )
      {
         newKey = newKey + Integer.toString( layer );
      }
      return new Property<>( newKey );
   }
}
