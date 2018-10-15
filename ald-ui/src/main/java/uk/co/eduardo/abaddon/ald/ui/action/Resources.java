package uk.co.eduardo.abaddon.ald.ui.action;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 * Resource utility methods.
 *
 * @author Ed
 */
public class Resources
{
   /**
    * Gets the resource specified by <code>key</code>.
    * <p>
    * Returns <code>null</code> if the value could not be located. The advantage of using this utility method is that it doesn't
    * throw {@link MissingResourceException}.
    *
    * @param bundle the resource bundle
    * @param key the key of the name of the resource
    * @return the string, or <code>null</code> if the resource is not present.
    */
   public static String getString( final ResourceBundle bundle, final String key )
   {
      if( bundle.containsKey( key ) )
      {
         return bundle.getString( key );
      }
      return null;
   }

   /**
    * Gets the {@link ImageIcon} specified by the <code>imageNameKey</code> when the key is resolve to an image name from the
    * supplied <code>bundle</code>
    * <p>
    * Returns <code>null</code> if the image could not be located or the image key does not exist in the bundle
    *
    * @param bundle the resource bundle
    * @param imageNameKey the key of the name of the image resource
    * @return the icon, or <code>null</code> if the resource is not present.
    */
   public static ImageIcon getImageIcon( final ResourceBundle bundle, final String imageNameKey )
   {
      if( bundle.containsKey( imageNameKey ) )
      {
         final String imageName = bundle.getString( imageNameKey );
         return getImageIcon( imageName );
      }
      return null;
   }

   /**
    * Gets the {@link ImageIcon} specified by <code>imageName</code>.
    * <p>
    * Returns <code>null</code> if the image could not be located
    *
    * @param imageName the name of the image resource
    * @return the icon, or <code>null</code> if the resource is not present.
    */
   public static ImageIcon getImageIcon( final String imageName )
   {
      if( ( imageName == null ) || imageName.isEmpty() )
      {
         return null;
      }

      final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      final URL url = classLoader.getResource( imageName );

      if( url != null )
      {
         return new ImageIcon( url );
      }
      return null;
   }
}
