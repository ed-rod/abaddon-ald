package uk.co.eduardo.abaddon.ald.data.project;

import java.io.File;
import java.util.List;

/**
 * Model for the available Player Character files.
 *
 * @author Ed
 */
public class AvailablePcsModel extends AbstractDirectoryModel
{
   private static final String PCS_DIR_NAME = "PCs"; //$NON-NLS-1$

   private static final String PC_SUFFIX = ".png"; //$NON-NLS-1$

   /**
    * Initializes a model for the available Player Character files.
    *
    * @param rootDir the project directory
    */
   public AvailablePcsModel( final File rootDir )
   {
      super( rootDir, PCS_DIR_NAME, PC_SUFFIX );
      rescan();
   }

   /**
    * Gets a read-only list of available Player Character files.
    *
    * @return the list of available Player Character files.
    */
   public List< File > getAvailablePcFiles()
   {
      return getAvailableFiles();
   }
}
