package android.graphics.drawable;

import android.graphics.Canvas;

/**
 * Placeholder class for Drawable
 *
 * @author Ed
 */
public class Drawable
{
   /** The width in pixels of each of the animation frames in the image */
   private static final int ANIM_WIDTH = 16;

   /** The height in pixels of each of the animation frames in the image */
   private static final int ANIM_HEIGHT = 24;

   /**
    * @return the height of the animated sprite.
    */
   public int getIntrinsicHeight()
   {
      return ANIM_HEIGHT;
   }

   /**
    * @return the width of the animated sprite.
    */
   public int getIntrinsicWidth()
   {
      return ANIM_WIDTH;
   }

   /**
    * @param a unused
    * @param b unused
    * @param c unused
    * @param d unused
    */
   public void setBounds( final int a, final int b, final int c, final int d )
   {
      // nothing.
   }

   /**
    * @param canvas unused
    */
   public void draw( final Canvas canvas )
   {
      // nothing.
   }
}
