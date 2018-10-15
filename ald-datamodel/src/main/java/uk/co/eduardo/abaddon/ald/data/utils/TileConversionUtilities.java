package uk.co.eduardo.abaddon.ald.data.utils;

import java.awt.Point;
import java.awt.Rectangle;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Provies utility methods for converting tile coordinates to pixel coordinates.
 *
 * @author Ed
 */
public final class TileConversionUtilities
{
   private TileConversionUtilities()
   {
      // Hide constructor for utility class
   }

   /**
    * @param tilesetData the current tileset data.
    * @param tile the tile to convert to pixel coordinates.
    * @return the converted point into pixel coordinates.
    */
   public static Point convertToPixel( final TilesetData tilesetData, final Coordinate tile )
   {
      final Rectangle bounds = pixelUnion( tilesetData, tile );
      if( bounds != null )
      {
         return new Point( bounds.x, bounds.y );
      }
      return null;
   }

   /**
    * Calculate the bounding box for an array of points in tile coordinates.
    *
    * @param tilePoints an array of points in tile coordinates.
    * @return a bounding rectangle for the specified points in tile coordinates.
    */
   public static Rectangle tileUnion( final Coordinate... tilePoints )
   {
      if( tilePoints == null )
      {
         return null;
      }
      Rectangle rect = null;
      for( final Coordinate point : tilePoints )
      {
         if( point == null )
         {
            continue;
         }
         final Rectangle current = new Rectangle( point.x, point.y, 1, 1 );
         if( rect == null )
         {
            rect = current;
         }
         else
         {
            rect = rect.union( current );
         }
      }
      return rect;
   }

   /**
    * Calculate the bounding box for an array of points in pixel coordinates.
    *
    * @param tilesetData the current tileset data.
    * @param tilePoints an array of points in tile coordinates.
    * @return a bounding rectangle for the specified points in pixel coordinates.
    */
   public static Rectangle pixelUnion( final TilesetData tilesetData, final Coordinate... tilePoints )
   {
      Rectangle rect = tileUnion( tilePoints );
      if( rect != null )
      {
         rect = new Rectangle( rect.x * tilesetData.getTileWidth(),
                               rect.y * tilesetData.getTileHeight(),
                               rect.width * tilesetData.getTileWidth(),
                               rect.height * tilesetData.getTileHeight() );
      }
      return rect;
   }
}
