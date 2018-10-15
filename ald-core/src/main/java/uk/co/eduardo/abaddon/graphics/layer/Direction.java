package uk.co.eduardo.abaddon.graphics.layer;

import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Frame offsets within an animation for different directions
 *
 * @author Ed
 */
@SuppressWarnings( "all" )
public enum Direction
{
   LEFT( 0, -1, 0 ),

   RIGHT( Direction.FRAMES, 1, 0 ),

   DOWN( 2 * Direction.FRAMES, 0, 1 ),

   UP( 3 * Direction.FRAMES, 0, -1 ),

   UP_LEFT( 4 * Direction.FRAMES, -1, -1 ),

   UP_RIGHT( 5 * Direction.FRAMES, 1, -1 ),

   DOWN_RIGHT( 6 * Direction.FRAMES, 1, 1 ),

   DOWN_LEFT( 7 * Direction.FRAMES, -1, 1 );

   public final int offset;

   public final Coordinate vector;

   /** The number of animated frames in each direction */
   public static final int FRAMES = 4;

   Direction( final int offset, final int vecX, final int vecY )
   {
      this.offset = offset;
      this.vector = new Coordinate( vecX, vecY );
   }

   public static Direction fromOffset( final int offsetToFind )
   {
      for( final Direction direction : Direction.values() )
      {
         if( direction.offset == offsetToFind )
         {
            return direction;
         }
      }
      return null;
   }

   public static Direction fromPoints( final boolean left, final boolean right, final boolean up, final boolean down )
   {
      if( up && left )
      {
         return UP_LEFT;
      }
      else if( up && right )
      {
         return UP_RIGHT;
      }
      else if( down && left )
      {
         return DOWN_LEFT;
      }
      else if( down && right )
      {
         return DOWN_RIGHT;
      }
      else if( left )
      {
         return LEFT;
      }
      else if( right )
      {
         return RIGHT;
      }
      else if( up )
      {
         return UP;
      }
      else if( down )
      {
         return DOWN;
      }

      // Default, show a sprite facing down
      return DOWN;
   }
}