package android.content.res;

import android.graphics.drawable.Drawable;

/**
 * Placeholder for android.content.res.Resources
 *
 * @author Ed
 */
public class Resources
{
   /**
    * @param key unused
    * @return a <code>Drawable</code>
    */
   public Drawable getDrawable( final int key )
   {
      return new Drawable();
   }

   /**
    * Placeholder for android.content.res.Resources.NotFoundException
    *
    * @author Ed
    */
   public static class NotFoundException extends RuntimeException
   {
      /**
       * Constructor for message.
       *
       * @param message exception message.
       */
      public NotFoundException( final String message )
      {
         super( message );
      }
   }
}
