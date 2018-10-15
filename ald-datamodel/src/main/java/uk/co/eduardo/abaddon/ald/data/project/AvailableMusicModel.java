package uk.co.eduardo.abaddon.ald.data.project;

import java.io.File;
import java.util.List;

/**
 * Model for the available music files.
 *
 * @author Ed
 */
public class AvailableMusicModel extends AbstractDirectoryModel
{
   private static final String PCS_DIR_NAME = "Music"; //$NON-NLS-1$

   @SuppressWarnings( "nls" )
   private static final String[] SUFFIXES =
   {
      ".mid",
      ".midi",
      ".mp3",
   };

   /**
    * Initializes a model for the available music files.
    *
    * @param rootDir the project directory
    */
   public AvailableMusicModel( final File rootDir )
   {
      super( rootDir, PCS_DIR_NAME, SUFFIXES );
      rescan();
   }

   /**
    * Gets a read-only list of available music files.
    *
    * @return the list of available music files.
    */
   public List< File > getAvailableMusicFiles()
   {
      return getAvailableFiles();
   }
}
