package uk.co.eduardo.map.sections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Reads/writes a {@link MapSection}
 *
 * @author Ed
 */
public class MapSectionProducer extends AbstractFileSectionProducer< MapSection >
{
   // ================| Constructors |====================================

   /**
    * Default Constructor.
    */
   public MapSectionProducer()
   {
      super( MapSection.class );
   }

   // ================| Public Methods |====================================

   /**
    * {@inheritDoc}
    */
   @Override
   public MapSection readSection( final DataInputStream stream ) throws IOException
   {
      // Read the dimensions of the map
      final int width = stream.readShort();
      final int height = stream.readShort();

      // The number of sparse layers defined in this map
      final int sparseLayerCount = stream.read();

      final int[][][] maps = new int[ MapSection.MAX_SPARSE_MAPS + 1 ][ height ][ width ];

      for( int y = 0; y < height; y++ )
      {
         for( int x = 0; x < width; x++ )
         {
            // Read the tile indices
            maps[ 0 ][ y ][ x ] = stream.readShort();
            for( int layer = 1; layer < ( MapSection.MAX_SPARSE_MAPS + 1 ); layer++ )
            {
               maps[ layer ][ y ][ x ] = -1;
            }
         }
      }

      for( int layer = 0; layer < sparseLayerCount; layer++ )
      {
         final int entryCount = stream.readShort();

         for( int entry = 0; entry < entryCount; entry++ )
         {
            final int x = stream.readShort();
            final int y = stream.readShort();
            maps[ layer + 1 ][ y ][ x ] = stream.readShort();
         }
      }
      return new MapSection( width, height, maps );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeSection( final DataOutputStream stream, final MapSection section ) throws IOException
   {
      final int[][][] maps = section.getMaps();

      stream.writeShort( section.getWidth() );
      stream.writeShort( section.getHeight() );
      stream.write( section.getSparseMapCount() );

      for( int y = 0; y < section.getHeight(); y++ )
      {
         for( int x = 0; x < section.getWidth(); x++ )
         {
            // Write the base map tile indices
            stream.writeShort( maps[ 0 ][ y ][ x ] );
         }
      }

      for( int layer = 0; layer < section.getSparseMapCount(); layer++ )
      {
         final ArrayList< Integer > sparseIdx = new ArrayList<>();
         final ArrayList< Coordinate > sparseCoords = new ArrayList<>();
         for( int y = 0; y < section.getHeight(); y++ )
         {
            for( int x = 0; x < section.getWidth(); x++ )
            {
               if( maps[ layer + 1 ][ y ][ x ] != -1 )
               {
                  sparseIdx.add( maps[ layer + 1 ][ y ][ x ] );
                  sparseCoords.add( new Coordinate( x, y ) );
               }
            }
         }

         stream.writeShort( sparseIdx.size() );
         for( int s = 0; s < sparseIdx.size(); s++ )
         {
            stream.writeShort( sparseCoords.get( s ).x );
            stream.writeShort( sparseCoords.get( s ).y );
            stream.writeShort( sparseIdx.get( s ) );
         }
      }
   }
}
