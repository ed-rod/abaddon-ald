package uk.co.eduardo.abaddon.ald.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility methods for {@link File}s
 *
 * @author Ed
 */
public final class FileUtilities
{
   private FileUtilities()
   {
      // Hide constructor for utility class.
   }

   /**
    * Copies the source to the destination.
    * <p>
    * If <code>destination</code> is a directory then a new file with the same name as <code>source</code> will be created within
    * the output directory.
    * <p>
    * If <code>destination</code> does not exist then it will be assumed to be a target file name and that file will be created with
    * the source contents copied.
    *
    * @param source the file to copy. Must be a file.
    * @param destination the destination. If it already exists then it must be a directory in which case the source will be copied
    *           into there. If it does not exist then this will be assumed to be the output file name.
    * @throws IOException if an error occurred reading or writing
    */
   public static void copy( final File source, final File destination ) throws IOException
   {
      if( ( source == null ) || ( destination == null ) )
      {
         return;
      }
      if( !source.isFile() || !source.exists() )
      {
         return;
      }
      File output = destination;
      if( destination.exists() && destination.isDirectory() )
      {
         output = new File( destination, source.getName() );
      }
      else
      {
         output.getParentFile().mkdirs();
      }
      InputStream inStream = null;
      OutputStream outStream = null;
      try
      {
         inStream = new BufferedInputStream( new FileInputStream( source ) );
         outStream = new BufferedOutputStream( new FileOutputStream( output ) );
         copy( inStream, outStream );
      }
      finally
      {
         if( inStream != null )
         {
            try
            {
               inStream.close();
            }
            catch( final IOException e )
            {
               // Ignore
            }
         }
         if( outStream != null )
         {
            try
            {
               outStream.close();
            }
            catch( final IOException e )
            {
               // Ignore
            }
         }
      }
   }

   /**
    * @param inStream the input stream. Cannot be <code>null</code>.
    * @param outStream the output stream. Cannot be <code>null</code>.
    * @throws IOException if the copy fails.
    */
   public static void copy( final InputStream inStream, final OutputStream outStream ) throws IOException
   {
      // Transfer bytes from in to out
      final byte[] buf = new byte[ 1024 ];
      int len;
      while( ( len = inStream.read( buf ) ) > 0 )
      {
         outStream.write( buf, 0, len );
      }
   }

   /**
    * @param target the file in which to search
    * @param token the token to search for in the file
    * @param replacement the string with which the token should be replaced.
    * @throws IOException if an error occurred during reading/writing
    */
   public static void replace( final File target, final String token, final String replacement ) throws IOException
   {
      // First we create a temporary file.
      File tempFile = null;
      BufferedWriter output = null;
      BufferedReader input = null;
      try
      {
         tempFile = File.createTempFile( "ald", "replace" ); //$NON-NLS-1$ //$NON-NLS-2$
         output = new BufferedWriter( new FileWriter( tempFile ) );
         input = new BufferedReader( new FileReader( target ) );
         String line;
         while( ( line = input.readLine() ) != null )
         {
            // I understand that this only works by accident as '.' matches '.'.
            output.write( line.replaceFirst( token, replacement ) );
            output.newLine();
         }

      }
      finally
      {
         if( output != null )
         {
            try
            {
               output.close();
            }
            catch( final IOException e )
            {
               // Do nothing.
            }
         }
         if( input != null )
         {
            try
            {
               input.close();
            }
            catch( final IOException e )
            {
               // Do nothing.
            }
         }
      }
      // Now we replace the original file with our temporary one.
      if( tempFile != null )
      {
         target.delete();
         target.createNewFile();
         copy( tempFile, target );
      }
   }
}
