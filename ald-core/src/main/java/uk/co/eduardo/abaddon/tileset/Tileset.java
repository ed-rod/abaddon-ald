package uk.co.eduardo.abaddon.tileset;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import uk.co.eduardo.abaddon.util.ScreenSettings;

/**
 * Creates tileset from a <code>Drawable</code> by breaking it up into chunks.
 *
 * @author Ed
 */
public class Tileset
{
   // ================| Fields |====================================

   /** The tiles in this <code>Tileset</code> */
   private final Bitmap[] tiles;

   /** Describes which tiles can be walked on and which are animated */
   private final TileDescription description;

   /** True if the tile with the same index is animated */
   private final boolean[] animated;

   /** The time at which the last animation was done */
   private long lastAnimate = 0;

   /** 0 or 1 for the animated frames */
   private int animFrame = 0;

   /** Number of milliseconds between animation updates */
   private final static long ANIM_UPDATE = 500;

   // ================| Constructors |====================================

   /**
    * @param tileImage the image containing all the tiles.
    * @param description the tileset description.
    */
   public Tileset( final Drawable tileImage, final TileDescription description )
   {
      final int tileSize = ScreenSettings.tileSize;

      this.description = description;

      // First, create a bitmap from the Drawable.
      final int width = tileImage.getIntrinsicWidth();
      final int height = tileImage.getIntrinsicHeight();

      // Check that the image is the correct size
      if( ( ( width % tileSize ) != 0 ) || ( ( height % tileSize ) != 0 ) )
      {
         throw new IllegalArgumentException( "Tile image dimensions are not a multiple of the tile size" ); //$NON-NLS-1$
      }
      // Create a bitmap from the drawable
      final Bitmap tileMap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );

      final Canvas canvas = new Canvas( tileMap );
      tileImage.setBounds( 0, 0, width, height );
      tileImage.draw( canvas );

      // Break the image into separate tiles
      final int xBlocks = width / tileSize;
      final int yBlocks = height / tileSize;

      this.tiles = new Bitmap[ xBlocks * yBlocks ];
      this.animated = new boolean[ xBlocks * yBlocks ];

      int index = 0;
      int currentY = 0;
      for( int y = 0; y < yBlocks; y++ )
      {
         int currentX = 0;
         for( int x = 0; x < xBlocks; x++ )
         {
            this.animated[ index ] = false;
            this.tiles[ index++ ] = Bitmap.createBitmap( tileMap, currentX, currentY, tileSize, tileSize );
            currentX += tileSize;
         }
         currentY += tileSize;
      }
      for( final int anim : description.animated )
      {
         this.animated[ anim ] = true;
      }
   }

   // ================| Public Methods |====================================

   /**
    * @return the number of tiles in this <code>Tileset</code>
    */
   public int getTileCount()
   {
      if( this.tiles != null )
      {
         return this.tiles.length;
      }
      return 0;
   }

   /**
    * @param index the index of the tile to retrieve
    * @return a tile <code>Bitmap</code>
    */
   public Bitmap getTile( final int index )
   {
      final int offset = this.animated[ index ] ? this.animFrame : 0;
      return this.tiles != null ? this.tiles[ index + offset ] : null;
   }

   /**
    * Check to see if the tile can be walked on. This check is performed by seeing if there is at least one edge of the tile that is
    * not blocked.
    *
    * @param index the tile index to check
    * @return true if the tile can be walked on
    */
   public boolean canWalk( final int index )
   {
      if( index == -1 )
      {
         return true;
      }
      final int walk = this.description.walkable[ index ];
      return ( ( walk & TileDescription.LEFT ) == 0 ) || ( ( walk & TileDescription.RIGHT ) == 0 ) ||
             ( ( walk & TileDescription.TOP ) == 0 ) || ( ( walk & TileDescription.BOTTOM ) == 0 ) ||
             ( ( walk & TileDescription.TL_BR_DIAG ) == 0 ) || ( ( walk & TileDescription.TR_BL_DIAG ) == 0 );
   }

   /**
    * @param index the tile index to check
    * @return a composition of bitmasks indicating which entry and exit directions are blocked for a walkable tile
    */
   public int getWalkDirections( final int index )
   {
      if( index < 0 )
      {
         // -1 represents an empty tile in a sparse map.
         // It's possible to walk on those from any direction
         return 0;
      }
      if( index >= this.description.walkable.length )
      {
         // Sanity check. Shouldn't ever happen but just in case we block it from all directions
         return TileDescription.LEFT | TileDescription.RIGHT | TileDescription.TOP | TileDescription.BOTTOM;
      }
      return this.description.walkable[ index ];
   }

   /**
    * Animate the frames
    */
   public void animate()
   {
      final long now = System.currentTimeMillis();
      if( ( now - this.lastAnimate ) > ANIM_UPDATE )
      {
         this.animFrame = this.animFrame == 0 ? 1 : 0;
         this.lastAnimate = now;
      }
   }
}
