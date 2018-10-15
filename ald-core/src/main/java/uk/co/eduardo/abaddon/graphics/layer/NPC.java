package uk.co.eduardo.abaddon.graphics.layer;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import uk.co.eduardo.abaddon.graphics.Animation;
import uk.co.eduardo.abaddon.util.Coordinate;
import uk.co.eduardo.abaddon.util.ScreenSettings;

/**
 * An NPC is a sprite that walks about in the game and can be interacted with
 *
 * @author Ed
 */
public class NPC extends Sprite
{
   // ================| Fields |====================================

   /** X tile position */
   private int x;

   /** Y tile position */
   private int y;

   /** The type of the NPC. Each different type has a different sprite image. */
   private final int type;

   /** True if the NPC does not move */
   private final boolean isFixed;

   /** utterances based on game events */
   private final String[] speeches;

   /** The game event id that triggers each of the above speeches */
   private final int[] events;

   /** Random number generator */
   private static final Random RNG = new Random();

   // ------------------Walking related------------------------------------------

   /** The number of pixels the NPC moves during each walking frame */
   private final static int WALK_INCREMENT = 1;

   /** Probability that the NPC will start walking is 1-in-START_WALK_ODDS */
   private final static int START_WALK_ODDS = 150;

   /** Probability that the NPC will stop walking is 1-in-STOP_WALK_ODDS */
   private final static int STOP_WALK_ODDS = 10;

   /** is the NPC walking */
   private boolean walking = false;

   /** How many pixels has the NPC walked. This ranges between 0 and tileSize */
   private int walkOffset = 0;

   /** An NPC will move when shoved */
   private final static int SHOVE_FRAMES = 15;

   /** Number of times this NPC has been shoved */
   private int shoves = 0;

   /** True when the NPC is walking due to a shove */
   private boolean shoved = false;

   /** The location where the sprite will be drawn (in pixel coordinates) **/
   private final Rect destination;

   // ================| Constructors |====================================

   /**
    * Constructs a new NPC
    *
    * @param anim The sprite's animation
    * @param xPos the starting X tile position.
    * @param yPos the starting Y tile position.
    * @param type the id for the type of NPC. This governs the NPC's appearance.
    * @param isFixed whether or not the NPC can move (<code>false</code>) or not (<code>true</code>).
    * @param speechCount the number of different things the NPC can say.
    */
   public NPC( final Animation anim, final int xPos, final int yPos, final int type, final boolean isFixed, final int speechCount )
   {
      super( anim );

      this.x = xPos;
      this.y = yPos;
      this.type = type;
      this.isFixed = isFixed;
      this.speeches = new String[ speechCount ];
      this.events = new int[ speechCount ];
      this.xOffset = 0;
      this.yOffset = -ScreenSettings.tileSize;
      this.destination = new Rect( 0, 0, 0, 0 );
   }

   /**
    * Constructs an NPC from a drawable resource. This resource contains all the different individual animation frames for the NPC
    * in a single image. This image is broken into frames (top-left to bottom-right in a left-to-right then top-to-bottom manner).
    *
    * @param resource the drawable resource. This will be split into animation frames.
    * @param frameWidth the width of a single frame within the larger image.
    * @param frameHeight the height of a single frame within the larger image.
    * @param xPos the starting X tile position.
    * @param yPos the starting Y tile position.
    * @param type the id for the type of NPC. This governs the NPC's appearance.
    * @param isFixed whether or not the NPC can move (<code>false</code>) or not (<code>true</code>).
    * @param speechCount the number of different things the NPC can say.
    */
   public NPC( final Drawable resource,
               final int frameWidth,
               final int frameHeight,
               final int xPos,
               final int yPos,
               final int type,
               final boolean isFixed,
               final int speechCount )
   {
      super( resource, frameWidth, frameHeight );

      this.x = xPos;
      this.y = yPos;
      this.type = type;
      this.isFixed = isFixed;
      this.speeches = new String[ speechCount ];
      this.events = new int[ speechCount ];
      this.xOffset = 0;
      this.yOffset = -ScreenSettings.tileSize;
      this.destination = new Rect( 0, 0, 0, 0 );
   }

   // ================| Public Methods |====================================

   /**
    * {@inheritDoc}
    */
   @Override
   public void draw( final int xPos, final int yPos, final boolean below, final Canvas canvas, final Paint paint )
   {
      if( !this.visible )
      {
         return;
      }
      final int tileSize = ScreenSettings.tileSize;
      final Coordinate vec = this.direction.vector;
      final int offset = this.direction.offset;
      final int xWalk = vec.x * this.walkOffset;
      final int yWalk = vec.y * this.walkOffset;
      final int xDraw = ( ( ( this.x * tileSize ) + this.xOffset + xWalk ) - xPos ) + ScreenSettings.xCentre;

      final int yDraw = ( ( ( this.y * tileSize ) + this.yOffset + yWalk ) - yPos ) + ScreenSettings.yCentre;

      final Rect source = this.anim.getFrameOffset( offset + this.animFrame );
      final Rect dest = this.destination;
      dest.left = xDraw;
      dest.top = yDraw;
      dest.right = xDraw + this.anim.getFrameWidth();
      dest.bottom = yDraw + this.anim.getFrameHeight();

      canvas.drawBitmap( this.anim.getBitmap(), source, dest, paint );
   }

   /**
    * @return the NPC's x position
    */
   @Override
   public Coordinate getTilePosition()
   {
      return new Coordinate( this.x, this.y );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Coordinate getPixelPosition()
   {
      final Coordinate vec = this.direction.vector;
      final int xWalk = vec.x * this.walkOffset;
      final int yWalk = vec.y * this.walkOffset;
      final int tileSize = ScreenSettings.tileSize;

      return new Coordinate( ( this.x * tileSize ) + xWalk, ( this.y * tileSize ) + yWalk );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Coordinate getOffsetPixelPosition()
   {
      final Coordinate vec = this.direction.vector;
      final int xWalk = vec.x * this.walkOffset;
      final int yWalk = vec.y * this.walkOffset;
      final int tileSize = ScreenSettings.tileSize;

      return new Coordinate( ( this.x * tileSize ) + this.xOffset + xWalk, ( this.y * tileSize ) + this.yOffset + yWalk );
   }

   /**
    * @return the type identifier for this NPC
    */
   public int getType()
   {
      return this.type;
   }

   /**
    * @return true if the NPC does not move
    */
   public boolean isFixed()
   {
      return this.isFixed;
   }

   /**
    * @return the number of different utterances the NPC has
    */
   public int getSpeechCount()
   {
      return this.speeches.length;
   }

   /**
    * @param index the index of the speech to return
    * @return the speech for that index
    */
   public String getSpeech( final int index )
   {
      return this.speeches[ index ];
   }

   /**
    * @param index the index of the event to return
    * @return the id of the game event that triggers the speech with the same id
    */
   public int getSpeechEvent( final int index )
   {
      return this.events[ index ];
   }

   /**
    * @param index index of the speech to set.
    * @param speech the text of the speech.
    * @param event the event that will trigger the NPC to say that.
    */
   public void setSpeech( final int index, final String speech, final int event )
   {
      this.speeches[ index ] = speech;
      this.events[ index ] = event;
   }

   /**
    * May start a sprite walking if it is not walking. If it is already walking animate the sprite and check to see if it should
    * stop walking.
    *
    */
   public void walk()
   {
      if( this.isFixed )
      {
         // If this NPC does not move, return now
         return;
      }

      if( !this.walking )
      {
         // Check to see if we should start walking.
         if( RNG.nextInt( START_WALK_ODDS ) == 1 )
         {
            final int d = RNG.nextInt( 4 );
            switch( d )
            {
               case 0:
                  this.direction = Direction.UP;
                  break;
               case 1:
                  this.direction = Direction.DOWN;
                  break;
               case 2:
                  this.direction = Direction.LEFT;
                  break;
               default:
                  this.direction = Direction.RIGHT;
                  break;
            }

            // Check that the tile in that direction is free
            final Coordinate vec = this.direction.vector;
            if( canMoveInDirection( vec ) )
            {
               this.walking = true;
               this.walkOffset = 0;

               // Occupy the tile we want to move to
               occupy( this.x + vec.x, this.y + vec.y );
            }
            else
            {
               // Do nothing
               return;
            }
         }
         else
         {
            // Do nothing
            return;
         }
      }

      // We must be walking
      this.walkOffset += WALK_INCREMENT;
      animate();

      if( ( this.walkOffset % ScreenSettings.tileSize ) == 0 )
      {
         // We just finished walking a single tile. Free the tile we came from
         deoccupy( this.x, this.y );

         // Update our tile position
         final Coordinate vec = this.direction.vector;
         this.x += vec.x;
         this.y += vec.y;
         this.walkOffset = 0;

         // If we are walking due to a shove, we should stop after a single tile
         // I.e. now.
         if( this.shoved )
         {
            this.shoved = false;
            this.walking = false;
            rest();
            return;
         }

         // Check to see if we should stop walking
         if( RNG.nextInt( STOP_WALK_ODDS ) == 1 )
         {
            this.walking = false;
            rest();
            return;
         }
         // We're going to carry on walking. Check to see if the next tile
         // is available and occupy that.
         if( canMoveInDirection( vec ) )
         {
            occupy( this.x + vec.x, this.y + vec.y );
         }
         else
         {
            // we have to stop walking
            this.walking = false;
            rest();
         }
      }
   }

   /**
    * @return true if the NPC is walking
    */
   public boolean isWalking()
   {
      return this.walking;
   }

   /**
    * Tell this NPC something wants to move into one of its occupied tiles
    */
   public void shove()
   {
      if( this.isFixed || this.walking )
      {
         // Fixed sprites and walking sprites cannot be shoved
         return;
      }

      if( ++this.shoves == SHOVE_FRAMES )
      {
         this.shoves = 0;
         // See if there are any available directions around the sprite
         for( int d = 0; d < 4; d++ )
         {
            switch( d )
            {
               case 0:
                  this.direction = Direction.UP;
                  break;
               case 1:
                  this.direction = Direction.DOWN;
                  break;
               case 2:
                  this.direction = Direction.LEFT;
                  break;
               default:
                  this.direction = Direction.RIGHT;
                  break;
            }

            // Check that the tile in that direction is free
            final Coordinate vec = this.direction.vector;
            if( canMoveInDirection( vec ) )
            {
               this.walking = true;
               this.walkOffset = 0;
               this.shoved = true;

               // Occupy the tile we want to move to
               occupy( this.x + vec.x, this.y + vec.y );
               break;
            }
         }
      }
   }

   // ================| Private Methods |====================================

   /**
    * @param vec the direction vector from the current position
    * @return true if the tile in the intended move direction from our current position is free
    */
   @SuppressWarnings( "unused" )
   private boolean canMoveInDirection( final Coordinate vec )
   {
      final int ts = ScreenSettings.tileSize;
      final int halfTs = ts / 2;

      final int curPixelX = ( this.x * ts ) + halfTs;
      final int curPixelY = ( this.y * ts ) + halfTs;

      final int checkPixelX = ( ( this.x + vec.x ) * ts ) + halfTs;
      final int checkPixelY = ( ( this.y + vec.y ) * ts ) + halfTs;

      // return LayerManager.canMove( curPixelX, curPixelY, checkPixelX, checkPixelY, this, false, false ) == 1;
      return true;
   }
}
