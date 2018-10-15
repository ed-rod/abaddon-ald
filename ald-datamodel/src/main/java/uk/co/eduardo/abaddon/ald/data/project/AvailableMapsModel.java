package uk.co.eduardo.abaddon.ald.data.project;

import java.io.File;
import java.util.List;

/**
 * Manages the list of maps that are currently available (but not necessarily open).
 *
 * @author Ed
 */
public class AvailableMapsModel extends AbstractDirectoryModel
{
   private static final String MAP_DIR_NAME = "Maps"; //$NON-NLS-1$

   private static final String MAP_SUFFIX = ".map"; //$NON-NLS-1$

   /**
    * Initializes a model for the available map files.
    *
    * @param rootDir the directory to scan for map files.
    */
   public AvailableMapsModel( final File rootDir )
   {
      super( rootDir, MAP_DIR_NAME, MAP_SUFFIX );
      rescan();
   }

   /**
    * Gets a read-only list of available map files.
    *
    * @return the list of available map files.
    */
   public List< File > getAvailableMapFiles()
   {
      return getAvailableFiles();
   }

   /**
    * @param mapName the name of the map.
    * @return the file for the map with the given name or <code>null</code> if no map with that name exists.
    */
   public File getMapFile( final String mapName )
   {
      final String toCheck = mapName + MAP_SUFFIX;
      for( final File file : getAvailableMapFiles() )
      {
         if( file.getName().equalsIgnoreCase( toCheck ) )
         {
            return file;
         }
      }
      return null;
   }

   /**
    * @param mapFile the file for the map.
    * @return the name of the map.
    */
   public static String getMapName( final File mapFile )
   {
      final String mapName = mapFile.getName();
      if( mapName.toLowerCase().endsWith( MAP_SUFFIX ) )
      {
         return mapName.substring( 0, mapName.lastIndexOf( MAP_SUFFIX ) );
      }
      return null;
   }
}
