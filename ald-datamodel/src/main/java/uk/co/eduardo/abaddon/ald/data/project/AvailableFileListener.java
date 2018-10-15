package uk.co.eduardo.abaddon.ald.data.project;

import java.io.File;

/**
 * Notification that a file has becom available or unavailable from the filesystem.
 *
 * @author Ed
 */
public interface AvailableFileListener
{
   /**
    * @param file the file that was added
    */
   void fileAdded( final File file );

   /**
    * @param file the file that was removed.
    */
   void fileRemoved( final File file );
}
