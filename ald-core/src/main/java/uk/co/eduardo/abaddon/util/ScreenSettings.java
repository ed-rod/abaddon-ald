package uk.co.eduardo.abaddon.util;

/**
 * Screen parameters.
 * <p>
 * For performance reasons we expose the fields so they can be accessed
 * directly.
 * 
 * @author Ed
 */
public final class ScreenSettings
{
   //================|  Fields             |====================================
   
   /** Width of the screen in pixels */
   public static int width;
   
   /** Height of the screen in pixels */
   public static int height;
   
   /** Centre of the screen in pixels */
   public static int xCentre;
   
   /** Centre of the screen in pixels */
   public static int yCentre;
   
   /** The size of the tiles in pixels */
   public static int tileSize;
   
   /** The height of the sprites in pixels. (the sprite width is always tileSize) */
   public static int spriteHeight;
   
   /** The display density. */
   public static int densityDpi;
   
   //================|  Constructors       |====================================
   
   /**
    * Hide the constructor for the utility class.
    */
   private ScreenSettings()
   {
      // hide constructor.
   }
   
   //================|  Public Methods     |====================================
   
   /**
    * @param width the width of the screen in pixels.
    */
   public static void setWidth( final int width )
   {
      ScreenSettings.width = width;
      ScreenSettings.xCentre = width >> 1;
   }
   
   /**
    * @param height the height of the screen in pixels.
    */
   public static void setHeight( final int height )
   {
      ScreenSettings.height = height;
      ScreenSettings.yCentre = height >> 1;
   }
   
   /**
    * @return the centre of the screen in pixels
    */
   public static Coordinate getCentre()
   {
      return new Coordinate( xCentre, yCentre );
   }
}
