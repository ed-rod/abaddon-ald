package uk.co.eduardo.abaddon.graphics;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import uk.co.eduardo.abaddon.util.Res;
import uk.co.eduardo.abaddon.util.ScreenSettings;

/**
 * Many sprites will have the same animation. This class checks if the sprite graphic has already been loaded and if so, returns the
 * same instance. If not, it will load it.
 *
 * @author Ed
 */
public class AnimationFactory
{
   // ================| Fields |====================================

   /** The sprite animations */
   private static final HashMap< Integer, WeakReference< Animation > > MAP = new HashMap<>();

   /** The width in pixels of each of the animation frames in the image */
   private static final int ANIM_WIDTH = ScreenSettings.tileSize;

   /** The height in pixels of each of the animation frames in the image */
   private static final int ANIM_HEIGHT = ScreenSettings.spriteHeight;

   // ================| Constructors |====================================

   /**
    * Private constructor to prevent instantiation
    */
   private AnimationFactory()
   {
      // Nothing here
   }

   // ================| Public Methods |====================================

   /**
    * @param key the String identifier for the sprite
    * @return an animation for that sprite
    */
   public static Animation getAnimation( final int key )
   {
      final WeakReference< Animation > animRef = MAP.get( key );
      if( animRef == null )
      {
         // Does not exist in the map, we'll need to add it
      }
      else
      {
         final Animation anim = animRef.get();
         if( anim == null )
         {
            // has been garbage collected need to re-add to map
         }
         else
         {
            return anim;
         }
      }
      // Add it to the map
      final Drawable d = Res.resources.getDrawable( key );
      if( ( ANIM_WIDTH == 0 ) || ( ANIM_HEIGHT == 0 ) )
      {
         return null;
      }
      final Animation newAnim = new Animation( d, ANIM_WIDTH, ANIM_HEIGHT );

      // Add a new weak reference to the animation to our map
      MAP.put( key, new WeakReference<>( newAnim ) );

      return newAnim;
   }
}
