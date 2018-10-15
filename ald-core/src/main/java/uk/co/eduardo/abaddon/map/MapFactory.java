package uk.co.eduardo.abaddon.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads/writes {@link MapDefinition}
 *
 * @author Ed
 */
public class MapFactory
{
   private static final List< MapVersion > Versions = new ArrayList<>();

   static
   {
      Versions.add( new Version0Map() );
   }

   /**
    * Detects the version number of the serialized map and uses the appropriate read to deserialize the map.
    *
    * @param stream the stream from which to read.
    * @return the read {@link MapDefinition}.
    * @throws IOException if a problem occurs reading from the stream.
    */
   public static MapDefinition readMap( final InputStream stream ) throws IOException
   {
      final DataInputStream dis = new DataInputStream( stream );
      final int version = dis.readShort();

      return getMapVersion( version ).readMap( dis );
   }

   /**
    * Uses the latest encoding for serializing the map.
    *
    * @param stream the stream to which the map is to be written.
    * @param map the map to serialize.
    * @throws IOException if a problem occurs writing to the stream.
    */
   public static void writeMap( final OutputStream stream, final MapDefinition map ) throws IOException
   {
      final DataOutputStream dos = new DataOutputStream( stream );
      final MapVersion latestMapVersion = getLatestMapVersion();
      dos.writeShort( latestMapVersion.getVersion() );
      latestMapVersion.writeMap( dos, map );
   }

   private static MapVersion getMapVersion( final int version )
   {
      for( final MapVersion mapVersion : Versions )
      {
         if( mapVersion.getVersion() == version )
         {
            return mapVersion;
         }
      }
      return null;
   }

   private static MapVersion getLatestMapVersion()
   {
      MapVersion latestVersion = Versions.get( 0 );
      int latest = latestVersion.getVersion();

      for( final MapVersion mapVersion : Versions )
      {
         if( mapVersion.getVersion() > latest )
         {
            latestVersion = mapVersion;
            latest = latestVersion.getVersion();
         }
      }
      return latestVersion;
   }
}
