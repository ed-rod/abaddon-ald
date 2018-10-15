package uk.co.eduardo.abaddon.graphics.layer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import uk.co.eduardo.abaddon.graphics.Animation;
import uk.co.eduardo.abaddon.util.Coordinate;
import uk.co.eduardo.abaddon.util.ScreenSettings;

/**
 * An animated character on screen.
 * <p>
 * Sprites can have either 16 or 32 frames. 16 framed sprites consist of 4 frames for each direction (up, down, left and right). 32
 * framed sprites have an extra four directions (up-left, up-right, dowm-right and down-left).
 *
 * @author Ed
 */
public class Sprite implements Layer
{
   // ================| Fields |====================================

   /** Number of game frames between this sprite's animated frames */
   private static final int FRAME_INCREMENT = 2;

   /** Maximum number of tiles a Sprite can occupy */
   private static final int MAX_OCCUPY = 10;

   /** The animated frames for this sprite */
   protected final Animation anim;

   /** The current frame of animation (between 0 and 3) */
   protected int animFrame = 0;

   /** How many game frames we've been on animFrame */
   private int frameCounter = 0;

   /** The current direction the sprite is travelling in */
   protected Direction direction = Direction.DOWN; // Start looking down

   /** True if this layer should be rendered */
   protected boolean visible;

   /** Sprite drawing offset */
   protected int xOffset;

   /** Sprite drawing offset */
   protected int yOffset;

   /** The layer on which the sprite is walking. */
   protected int layerIndex;

   /** Sprite's x pixel position. Exposed as public. Use with caution. */
   public int xPixel;

   /** Sprite's y pixel position. Exposed as public. Use with caution. */
   public int yPixel;

   /** The list of x tile coordinates that this sprite is occupying */
   private final int[] xOccupied = new int[ MAX_OCCUPY ];

   /** The list of y tile coordinates that this sprite is occupying. */
   private final int[] yOccupied = new int[ MAX_OCCUPY ];

   /** The number of tiles this sprite is occupying. */
   private int occupiedCount = 0;

   /** The location where the sprite will be drawn (in pixel coordinates ) */
   private final Rect destination;

   // ================| Enumerations |====================================

   /**
    * Construct a Sprite from an animation sequence
    *
    * @param animation the animated frames for this sprite
    */
   public Sprite( final Animation animation )
   {
      if( animation == null )
      {
         // throw new IllegalArgumentException();
         this.xOffset = 0;
         this.yOffset = 0;
      }
      else
      {
         this.xOffset = animation.getFrameWidth() / 2;
         this.yOffset = animation.getFrameHeight();
      }
      this.anim = animation;
      this.visible = true;
      this.destination = new Rect( 0, 0, 0, 0 );
   }

   /**
    * Construct a Sprite from an image resource
    *
    * @param resource the image resource from which to create the sprite
    * @param frameWidth the width of each animation frame in pixels
    * @param frameHeight the height of each animation frame in pixels
    */
   public Sprite( final Drawable resource, final int frameWidth, final int frameHeight )
   {
      this.anim = new Animation( resource, frameWidth, frameHeight );
      this.visible = true;
      this.xOffset = -( this.anim.getFrameWidth() / 2 );
      this.yOffset = -this.anim.getFrameHeight();
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

      final int xDraw = ( this.xPixel - xPos ) + ScreenSettings.xCentre + this.xOffset;
      final int yDraw = ( this.yPixel - yPos ) + ScreenSettings.yCentre + this.yOffset;

      final Rect source = this.anim.getFrameOffset( this.direction.offset + this.animFrame );

      final Rect dest = this.destination;
      dest.left = xDraw;
      dest.top = yDraw;
      dest.right = xDraw + this.anim.getFrameWidth();
      dest.bottom = yDraw + this.anim.getFrameHeight();

      canvas.drawBitmap( this.anim.getBitmap(), source, dest, paint );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setVisible( final boolean show )
   {
      this.visible = show;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isVisible()
   {
      return this.visible;
   }

   /**
    * Change the sprite's animation frame
    */
   public void animate()
   {
      if( this.frameCounter++ >= FRAME_INCREMENT )
      {
         this.animFrame = ( this.animFrame + 1 ) % Direction.FRAMES;
         this.frameCounter = 0;
      }
   }

   /**
    * Manually set the animation frame
    *
    * @param frame the animation frame index.
    */
   public void setFrame( final int frame )
   {
      this.animFrame = frame % Direction.FRAMES;
   }

   /**
    * Sets the animation to the rest frame for the sprite's current direction
    */
   public void rest()
   {
      this.frameCounter = FRAME_INCREMENT;
      this.animFrame = 0;
   }

   /**
    * @param direction the direction in which the sprite is to face
    */
   public void setDirection( final Direction direction )
   {
      this.direction = direction;
   }

   /**
    * @return the direction in which the sprite is facing.
    */
   public Direction getDirection()
   {
      return this.direction;
   }

   /**
    * Sets the Sprite's pixel (NOT TILE) position on the map
    *
    * @param x the X pixel coordinate.
    * @param y the Y pixel coordinate.
    */
   public void setPixelPosition( final int x, final int y )
   {
      this.occupiedCount = 0;

      this.xPixel = x;
      this.yPixel = y;

      // Convert pixel to tile position
      final int tileSize = ScreenSettings.tileSize;

      occupy( x / tileSize, y / tileSize );
   }

   /**
    * @return the Sprite's pixel (NOT TILE) position
    */
   public Coordinate getPixelPosition()
   {
      return new Coordinate( this.xPixel, this.yPixel );
   }

   /**
    * @return the Sprite's pixel (NOT TILE) position offset by the sprite's drawing offsets. This is the pixel position of the
    *         top-left point of the sprite where it should be drawn.
    */
   public Coordinate getOffsetPixelPosition()
   {
      return new Coordinate( this.xPixel + this.xOffset, this.yPixel + this.yOffset );
   }

   /**
    * @return the tile coordinate of where the sprite is currently standing
    */
   public Coordinate getTilePosition()
   {
      final int tileSize = ScreenSettings.tileSize;
      return new Coordinate( this.xPixel / tileSize, this.yPixel / tileSize );
   }

   /**
    * @param x the X tile coordinate
    * @param y the Y tile coordinate.
    * @return <code>true</code> if this sprite is occupying the tile at (x, y)
    */
   public boolean isOccupying( final int x, final int y )
   {
      for( int coordIdx = 0; coordIdx < this.occupiedCount; coordIdx++ )
      {
         if( ( this.xOccupied[ coordIdx ] == x ) && ( this.yOccupied[ coordIdx ] == y ) )
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Sets this sprite to occupy the specified tile coordinate
    *
    * @param xTile the X tile coordinate.
    * @param yTile the Y tile coordinate.
    */
   public void occupy( final int xTile, final int yTile )
   {
      if( !isOccupying( xTile, yTile ) )
      {
         this.xOccupied[ this.occupiedCount ] = xTile;
         this.yOccupied[ this.occupiedCount ] = yTile;
         this.occupiedCount++;
      }
   }

   /**
    * If the sprite is occupying the specified coordinate, we de-occupy it
    *
    * @param xTile the X tile coordinate.
    * @param yTile the Y tile coordinate.
    */
   public void deoccupy( final int xTile, final int yTile )
   {
      int found = -1;
      for( int coordIdx = 0; coordIdx < this.occupiedCount; coordIdx++ )
      {
         if( ( this.xOccupied[ coordIdx ] == xTile ) && ( this.yOccupied[ coordIdx ] == yTile ) )
         {
            found = coordIdx;
            break;
         }
      }
      if( found >= 0 )
      {
         for( int coordIdx = found; coordIdx < ( this.occupiedCount - 1 ); coordIdx++ )
         {
            this.xOccupied[ coordIdx ] = this.xOccupied[ coordIdx + 1 ];
            this.yOccupied[ coordIdx ] = this.yOccupied[ coordIdx + 1 ];
         }
         this.occupiedCount--;
      }
   }

   /**
    * @return the index of the layer on which the sprite exists.
    */
   public int getLayerIndex()
   {
      return this.layerIndex;
   }

   /**
    * @param layerIndex the index of the layer on which the sprite is to exist.
    */
   public void setLayerIndex( final int layerIndex )
   {
      this.layerIndex = layerIndex;
   }
}
