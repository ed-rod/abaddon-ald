package uk.co.eduardo.abaddon.util;

import java.lang.reflect.Field;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import uk.co.eduardo.abaddon.R;

/**
 * Utility methods for accessing resources by name.
 *
 * @author Ed
 */
public final class ResourceUtilities
{
   // ================| Constructors |====================================

   private ResourceUtilities()
   {
      // Hide constructor for utility class.
   }

   // ================| Public Methods |====================================

   /**
    * Gets the resource id of the named raw resource.
    *
    * @param name the name of the resource.
    * @return the id of the resource.
    * @throws NotFoundException if a problem occurred accessing the resource.
    */
   public static int getRawResourceId( final String name ) throws Resources.NotFoundException
   {
      return getResourceId( R.raw.class, name );
   }

   /**
    * Gets the resource id of the named drawable resource.
    *
    * @param name the name of the resource.
    * @return the id of the resource.
    * @throws NotFoundException if a problem occurred accessing the resource.
    */
   public static int getDrawableResourceId( final String name ) throws Resources.NotFoundException
   {
      return getResourceId( R.drawable.class, name );
   }

   private static < T > int getResourceId( final Class< T > clazz, final String name ) throws Resources.NotFoundException
   {
      try
      {
         final Field field = clazz.getDeclaredField( name );
         final Integer id = (Integer) field.get( null );
         return id;
      }
      catch( final SecurityException e )
      {
         throw new Resources.NotFoundException( name );
      }
      catch( final NoSuchFieldException e )
      {
         throw new Resources.NotFoundException( name );
      }
      catch( final IllegalArgumentException e )
      {
         throw new Resources.NotFoundException( name );
      }
      catch( final IllegalAccessException e )
      {
         throw new Resources.NotFoundException( name );
      }
   }
}
