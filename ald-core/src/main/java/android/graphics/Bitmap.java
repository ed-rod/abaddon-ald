package android.graphics;

/**
 * Placeholder class for android.graphics.Bitmap
 *
 * @author Ed
 */
@SuppressWarnings( "all" )
public class Bitmap
{
   /**
    * Inner config class for Bitmap types.
    */
   public static class Config
   {
      /** 4 channel bitmap type. */
      public static final Config ARGB_8888 = new Config();
   }

   public static Bitmap createBitmap( final int w, final int h, final Config config )
   {
      return new Bitmap();
   }

   public static Bitmap createBitmap( final Bitmap tileMap,
                                      final int currentX,
                                      final int currentY,
                                      final int tileSize,
                                      final int tileSize2 )
   {
      return new Bitmap();
   }
}
