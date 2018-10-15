package uk.co.eduardo.abaddon.ald.sprite;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.util.Arrays;

/**
 * Performs some simple operations on {@link BufferedImage}s.
 * 
 * @author Ed
 */
public final class ImageFiltering
{
   private ImageFiltering()
   {
      // Hide constructor.
   }
   
   private static final RenderingHints HINTS = new RenderingHints( RenderingHints.KEY_ANTIALIASING, 
                                                                   RenderingHints.VALUE_ANTIALIAS_ON );
   
   // Blur using the following weights for the kernel
   // 2  4  2       1/16  1/8   1/16
   // 4  8  4   ->  1/8   1/4   1/8
   // 2  4  2       1/16  1/8   1/16
   private static final BufferedImageOp BLUR = 
      new ConvolveOp( new Kernel( 3, 
                                  3, 
                                  new float[] { 0.0625f, 0.125f, 0.0625f, 
                                                0.125f,  0.25f,  0.125f, 
                                                0.0625f, 0.125f, 0.0625f } ), 
                      ConvolveOp.EDGE_NO_OP, 
                      HINTS );
   
   private static final BufferedImageOp ALPHA;
   
   static
   {
      final byte[] ones = new byte[ 256 ];
      Arrays.fill( ones, (byte) 0xFF );
      final byte[] ascending = new byte[ 256 ];
      for( int i = 0; i < 256; i++ )
      {
         ascending[ i ] = (byte)( ( i / 2 ) & 0xFF );
      }
      ALPHA = new LookupOp( new ByteLookupTable( 0, new byte[][] { ones, ones, ones, ascending } ),
                            HINTS );
   }
   
   /**
    * Blurs using a convolution filter. The relative weights for the components are:
    * <pre>
    *  2  4  2
    *  4  8  4
    *  2  4  2</pre>
    * 
    * Those weights add up to 32 which gives the following kernel:
    * <pre>
    *  1/16  1/8  1/16
    *  1/8   1/4  1/8
    *  1/16  1/8  1/16</pre>
    * 
    * @param input the image to blur.
    * @return a blurred image.
    */
   public static BufferedImage blur( final BufferedImage input )
   {
      return BLUR.filter( input, null );
   }
   
   /**
    * This filter sets all pixels to white maintaining. Only the alpha channel has content. The
    * output alpha values are half of the input values.
    * 
    * @param input the image to filter
    * @return a ghosted image.
    */
   public static BufferedImage ghost( final BufferedImage input )
   {
      return ALPHA.filter( input, null );
   }
}
