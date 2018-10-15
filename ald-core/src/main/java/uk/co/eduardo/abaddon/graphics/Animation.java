package uk.co.eduardo.abaddon.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Creates an animation from an image resource composed of many frames.
 * <p>
 * Each frame is a rectangular subsection of the image. The image must have a width and height that is a multiple of the dimensions
 * of each frame.
 *
 * @author Ed
 */
public class Animation
{
   // ================| Fields |====================================

   /** The dimension of each frame in pixels */
   private final int frameWidth;

   /** The dimension of each frame in pixels */
   private final int frameHeight;

   /** The number of frames in this <code>Animation</code> */
   private final int numFrames;

   /** The whole image */
   private final Bitmap fullImage;

   /** Offsets into the main image for the different frames */
   private final Rect[] offsets;

   // ================| Constructors |====================================

   /**
    * Constructs an <code>Animation</code>
    *
    * @param resource the image resource
    * @param frameWidth the width of each frame in pixels
    * @param frameHeight the height of each frame in pixels
    */
   public Animation( final Drawable resource, final int frameWidth, final int frameHeight )
   {
      final int width = resource.getIntrinsicWidth();
      final int height = resource.getIntrinsicHeight();

      // Check that the image is the correct size
      if( ( ( width % frameWidth ) != 0 ) || ( ( height % frameHeight ) != 0 ) )
      {
         throw new IllegalArgumentException( String.format( "Animation image dimensions are not a multiple of the frame size %dx%d %dx%d", //$NON-NLS-1$
                                                            width,
                                                            height,
                                                            frameWidth,
                                                            frameHeight ) );
      }

      // Copy the frames into each Bitmap
      this.fullImage = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
      final Canvas canvas = new Canvas( this.fullImage );
      resource.setBounds( 0, 0, width, height );
      resource.draw( canvas );

      // Break the image into separate tiles
      final int xBlocks = width / frameWidth;
      final int yBlocks = height / frameHeight;

      // Initialise member variables
      this.frameWidth = frameWidth;
      this.frameHeight = frameHeight;

      this.numFrames = width * height;

      this.offsets = new Rect[ this.numFrames ];

      int index = 0;
      for( int y = 0; y < yBlocks; y++ )
      {
         for( int x = 0; x < xBlocks; x++ )
         {
            this.offsets[ index++ ] = new Rect( x * frameWidth, y * frameHeight, ( x + 1 ) * frameWidth, ( y + 1 ) * frameHeight );
         }
      }
   }

   // ================| Public Methods |====================================

   /**
    * @return the number of frames in this <code>Animation</code>
    */
   public int getFrameCount()
   {
      return this.numFrames;
   }

   /**
    * @return the whole animation image for all the frames
    */
   public Bitmap getBitmap()
   {
      return this.fullImage;
   }

   /**
    * Returns a rectangle for where the specified frame of animation occurs in the animation image
    *
    * @param index the index of the animation frame.
    * @return the <code>Rect</code> for an animation frame
    */
   public Rect getFrameOffset( final int index )
   {
      return this.offsets[ index ];
   }

   /**
    * @return the width of each frame in pixels
    */
   public int getFrameWidth()
   {
      return this.frameWidth;
   }

   /**
    * @return the height of each frame in pixels
    */
   public int getFrameHeight()
   {
      return this.frameHeight;
   }
}
