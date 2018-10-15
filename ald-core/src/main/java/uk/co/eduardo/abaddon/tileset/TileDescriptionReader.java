package uk.co.eduardo.abaddon.tileset;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Reads a {@link TileDescription} from a resource <code>InputStream</code>
 *
 * Tile descriptions are stored in the following format. The name of the variable and the number of bytes are shown:
 *
 * <pre>
 * _____________________________________________________________________________
 *
 * nWalkable: 2           // All tiles with an index below this can be walked on
 * foreach nWalkable
 *   walkDirections: 1    // A composition of allowable entry directions
 * end
 *
 * nAnimated: 1           // The number of animated tiles
 * foreach nAnimated
 *   animTileId: 2        // The tile index of the animated tile
 * end
 * _____________________________________________________________________________
 * </pre>
 *
 * @author Ed
 */
public class TileDescriptionReader
{
   // ================| Constructors |====================================

   /**
    * Prevent instatiation
    */
   private TileDescriptionReader()
   {
      // private constructor
   }

   // ================| Public Methods |====================================

   /**
    * @param stream the stream from which to read
    * @return a {@link TileDescription} or null if the stream could not be read
    */
   public static TileDescription readStream( final InputStream stream )
   {
      TileDescription retVal = null;

      try
      {
         final int nWalkable = readShort( stream );
         final int[] walkDirections = new int[ nWalkable ];

         for( int tile = 0; tile < nWalkable; tile++ )
         {
            walkDirections[ tile ] = stream.read();
         }

         final int nAnimated = stream.read();
         final int[] animTiles = new int[ nAnimated ];

         for( int tile = 0; tile < nAnimated; tile++ )
         {
            animTiles[ tile ] = readShort( stream );
         }

         retVal = new TileDescription( walkDirections, animTiles );
      }
      catch( final IOException exception )
      {
         // return a null description
      }
      return retVal;
   }

   /**
    * @param walkable the array of walkable tile information.
    * @param anim the array of animated tile information.
    * @param stream the stream to which these are to be written.
    */
   public static void writeStream( final int[] walkable, final int[] anim, final OutputStream stream )
   {
      try
      {
         // Write the number of walkable tiles
         writeShort( walkable.length, stream );

         for( final int walk : walkable )
         {
            // Write each walkable directions
            stream.write( walk );
         }

         // write the number of animated tiles
         stream.write( anim.length );
         for( final int a : anim )
         {
            // Write the tile index for the animated tile
            writeShort( a, stream );
         }
      }
      catch( final Exception e )
      {
         // nothing.
      }
   }

   // ================| Private Methods |====================================

   private static short readShort( final InputStream stream ) throws IOException
   {
      final int high = stream.read();
      final int low = stream.read();

      return (short) ( low | ( high << 8 ) );
   }

   private static void writeShort( final int data, final OutputStream stream ) throws IOException
   {
      final byte loByte = (byte) ( data & 0x00FF );
      final byte hiByte = (byte) ( ( data & 0xFF00 ) >> 8 );

      stream.write( hiByte );
      stream.write( loByte );
   }
}
