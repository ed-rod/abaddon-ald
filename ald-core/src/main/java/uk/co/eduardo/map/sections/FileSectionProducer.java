package uk.co.eduardo.map.sections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A handler for a file section with methods to read and write the section.
 *
 * @author Ed
 * @param <T> the type of file section.
 */
public interface FileSectionProducer< T extends FileSection >
{
   // ================| Public Methods |====================================

   /**
    * @param stream the InputStream from which to read.
    * @return the read section.
    * @throws IOException if a problem occurs reading from the stream.
    */
   T readSection( final DataInputStream stream ) throws IOException;

   /**
    * Writes the section to the stream. A previous call to {@link #canWriteSection(FileSection)} must be made before caling this
    * method.
    *
    * @param stream the OutputStream to which to write.
    * @param section the section to write.
    * @throws IOException if a problem occurs writing to the stream.
    */
   void writeSection( final DataOutputStream stream, T section ) throws IOException;

   /**
    * @param section the section to write.
    * @return whether or not this factory supports writing that section.
    */
   boolean canWriteSection( final FileSection section );
}
