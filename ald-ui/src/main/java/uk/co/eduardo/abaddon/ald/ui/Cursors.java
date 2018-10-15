package uk.co.eduardo.abaddon.ald.ui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Defines custom cursors for use in the appplication.
 *
 * @author Ed
 */
public final class Cursors
{
   private Cursors()
   {
      // hide constructor
   }

   /** Pencil Cursor. */
   public static final Cursor PENCIL = createCursor( "uk/co/eduardo/abaddon/ald/tools/cursorpencil.png", //$NON-NLS-1$
                                                     new Point( 0, 31 ),
                                                     "Pencil" ); //$NON-NLS-1$

   /** Crop Cursor. */
   public static final Cursor CROP = createCursor( "uk/co/eduardo/abaddon/ald/tools/cursorcrop.png", //$NON-NLS-1$
                                                   new Point( 8, 23 ),
                                                   "Crop" ); //$NON-NLS-1$

   /** Fill Cursor. */
   public static final Cursor FILL = createCursor( "uk/co/eduardo/abaddon/ald/tools/cursorfill.png", //$NON-NLS-1$
                                                   new Point( 14, 29 ),
                                                   "Fill" ); //$NON-NLS-1$

   /** Sprite Creation Cursor. */
   public static final Cursor SPRITE = createCursor( "uk/co/eduardo/abaddon/ald/tools/cursorsprite.png", //$NON-NLS-1$
                                                     new Point( 7, 31 ),
                                                     "Sprite" ); //$NON-NLS-1$

   /** Eye Drop Cursor. */
   public static final Cursor EYE_DROP = createCursor( "uk/co/eduardo/abaddon/ald/tools/cursoreyedrop.png", //$NON-NLS-1$
                                                       new Point( 1, 31 ),
                                                       "Eye Drop" ); //$NON-NLS-1$

   private static final Cursor createCursor( final String path, final Point hotspot, final String name )
   {
      final BufferedImage image = getImage( path );
      return Toolkit.getDefaultToolkit().createCustomCursor( image, hotspot, name );
   }

   private static final BufferedImage getImage( final String path )
   {
      if( ( path == null ) || path.isEmpty() )
      {
         return null;
      }

      final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      final URL url = classLoader.getResource( path );
      try
      {
         if( url != null )
         {
            return ImageIO.read( url );
         }
      }
      catch( final IOException e )
      {
         // Return null;
      }
      return null;
   }
}
