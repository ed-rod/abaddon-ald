package uk.co.eduardo.map.sections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Abstract implementation that provides utility methods for reading from streams.
 *
 * @author Ed
 * @param <T> the type of file section that is supported by this file section factory.
 */
public abstract class AbstractFileSectionProducer< T extends FileSection > implements FileSectionProducer< T >
{
   // ================| Fields |====================================

   private final Class< T > fileSectionClass;

   // ================| Constructors |====================================

   /**
    * @param fileSectionClass the class of the file section that is supported.
    */
   public AbstractFileSectionProducer( final Class< T > fileSectionClass )
   {
      this.fileSectionClass = fileSectionClass;
   }

   // ================| Public Methods |====================================

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean canWriteSection( final FileSection section )
   {
      return section.getClass() == this.fileSectionClass;
   }

   // ================| Protected Methods |====================================

   /**
    * Reads a string from the stream.
    * <p>
    * It is assumed that the length of the string is encoded as a short followed by the byte data for the string.
    *
    * @param stream the stream from which to read.
    * @return a string read from the stream.
    * @throws IOException if a problem occurs while reading from this stream
    */
   protected String readString( final DataInputStream stream ) throws IOException
   {
      return readString( stream, true );
   }

   /**
    * Reads a string from the stream.
    *
    * @param stream the stream from which to read.
    * @param shortLength Whether the length of the string is encoded as a short (<code>true</code>) or as a byte
    *           (<code>false</code>).
    * @return a string read from the stream.
    * @throws IOException if a problem occurs while reading from this stream
    */
   protected String readString( final DataInputStream stream, final boolean shortLength ) throws IOException
   {
      final int length = shortLength ? stream.readShort() & 0xFFFF : stream.read() & 0xFF;

      // Now read in the string value.
      final byte[] buffer = new byte[ length ];
      stream.read( buffer );
      return new String( buffer );
   }

   /**
    * Writes a string to the stream.
    * <p>
    * First the length of the string is written as a short followed by the byte data for the string.
    *
    * @param stream the stream to which the data is to be written.
    * @param string the string to write.
    * @throws IOException if a problem occurs while writing to the stream.
    */
   protected void writeString( final DataOutputStream stream, final String string ) throws IOException
   {
      // First write out the length of the string
      stream.writeShort( string.length() );
      stream.write( string.getBytes() );
   }
}
