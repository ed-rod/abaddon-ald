package uk.co.eduardo.abaddon.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Reads/writes a {@link MapDefinition}.
 *
 * @author Ed
 */
public interface MapVersion
{
   /**
    * @return the unique version number for the map format which this factory supports.
    */
   int getVersion();

   /**
    * @param stream the stream from which the map is to be read.
    * @return the read map definition.
    * @throws IOException if a problem occurs reading from the stream.
    */
   MapDefinition readMap( DataInputStream stream ) throws IOException;

   /**
    * @param stream the stream to which the map is to be written.
    * @param map the map to write.
    * @throws IOException if a problem occurs reading from the stream.
    */
   void writeMap( DataOutputStream stream, MapDefinition map ) throws IOException;
}
