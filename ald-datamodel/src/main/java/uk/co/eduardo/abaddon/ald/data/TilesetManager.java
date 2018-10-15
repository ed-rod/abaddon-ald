package uk.co.eduardo.abaddon.ald.data;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Manages the list of available Tilesets.
 *
 * @author Ed
 */
public class TilesetManager
{
   private static final String DSC_SUFFIX = ".DSC"; //$NON-NLS-1$

   private static final FileFilter DSC_FILTER = new FileFilter()
   {
      @Override
      public boolean accept( final File pathname )
      {
         return pathname.isFile() && pathname.getName().toUpperCase().endsWith( DSC_SUFFIX );
      }
   };

   private static final TilesetManager INSTANCE = new TilesetManager();

   private final List< String > tilesetNames = new ArrayList<>();

   private TilesetManager()
   {
      refreshList();
   }

   /**
    * @return the singleton instance.
    */
   public static TilesetManager getInstance()
   {
      return INSTANCE;
   }

   /**
    * @return the list of available tileset names.
    */
   public List< String > getAvailableTilesets()
   {
      return Collections.unmodifiableList( this.tilesetNames );
   }

   private void refreshList()
   {
      this.tilesetNames.clear();

      final String dirName = TilesetManager.class.getPackage().getName().replace( '.', '/' );
      for( final String rootName : getClasspathDirectories() )
      {
         final File root = new File( rootName );
         final File dir = new File( root, dirName );
         final File[] files = dir.listFiles( DSC_FILTER );
         if( files != null )
         {
            for( final File file : files )
            {
               final String name = file.getName();
               this.tilesetNames.add( name.substring( 0, name.toUpperCase().indexOf( DSC_SUFFIX ) ) );
            }
         }
      }
   }

   private List< String > getClasspathDirectories()
   {
      final List< String > classpathDirs = new ArrayList<>();
      final StringTokenizer tokenizer = new StringTokenizer( System.getProperty( "java.class.path" ), //$NON-NLS-1$
                                                             ";" ); //$NON-NLS-1$
      while( tokenizer.hasMoreElements() )
      {
         classpathDirs.add( tokenizer.nextToken() );
      }
      return classpathDirs;
   }
}
