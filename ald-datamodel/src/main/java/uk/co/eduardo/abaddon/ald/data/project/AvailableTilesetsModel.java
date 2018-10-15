package uk.co.eduardo.abaddon.ald.data.project;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Manages the list of tilesets that are currently available.
 *
 * @author Ed
 */
public class AvailableTilesetsModel extends AbstractDirectoryModel
{
   private static final String TILESETS_DIR_NAME = "Tilesets"; //$NON-NLS-1$

   private static final String DSC_SUFFIX = ".dsc"; //$NON-NLS-1$

   private static final String PNG_SUFFIX = ".png"; //$NON-NLS-1$

   private final int tileSize;

   /**
    * Initializes a model for the available tileset files.
    * <p>
    * A tileset exists as two files: <i>name</i>.png and <i>name</i>.dsc where <i>name</i> is the tileset name.
    * <p>
    * The ".dsc" file is the tileset descriptor. it contains information about each tile in the tileset.
    * <p>
    * The ".png" file is the tileset image. The width has be be a multiple of the tile width and the height must be a multiple of
    * the tile height.
    *
    * @param rootDir the directory to scan for tileset files.
    * @param tileSize the tile dimensions in pixels.
    */
   public AvailableTilesetsModel( final File rootDir, final int tileSize )
   {
      super( rootDir, TILESETS_DIR_NAME, DSC_SUFFIX );
      this.tileSize = tileSize;
      rescan();
   }

   /**
    * Gets a read-only list of available tileset files.
    *
    * @return the list of available tileset descriptor files.
    */
   public List< File > getAvailableDscTilesetFiles()
   {
      return getAvailableFiles();
   }

   /**
    * Gets a read-only list of available tileset image files.
    *
    * @return a list of available tileset png files.
    */
   public List< File > getAvailablePngTilesetFiles()
   {
      final List< File > pngFiles = new ArrayList<>();
      for( final File dscFile : getAvailableDscTilesetFiles() )
      {
         pngFiles.add( getPngFileFor( dscFile ) );
      }
      return Collections.unmodifiableList( pngFiles );
   }

   /**
    * @return a list of the tileset names.
    */
   public List< String > getAvailableTilesetNames()
   {
      final List< String > names = new ArrayList<>();
      for( final File file : getAvailableFiles() )
      {
         names.add( file.getName().substring( 0, file.getName().toLowerCase().indexOf( DSC_SUFFIX ) ) );
      }
      return names;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected boolean accept( final File dscFile )
   {
      final String path = dscFile.getAbsolutePath();

      // Only if it also contains a PNG file
      final String rootName = path.substring( 0, path.length() - DSC_SUFFIX.length() );
      final File pngFile = new File( rootName + PNG_SUFFIX );
      if( pngFile.exists() && pngFile.isFile() )
      {
         // Read the image and determine whether its dimensions are multiples of the allowed tile size
         try
         {
            final BufferedImage image = ImageIO.read( pngFile );
            if( image != null )
            {
               return ( image.getWidth() > 0 ) && ( ( image.getWidth() % this.tileSize ) == 0 ) && ( image.getHeight() > 0 ) &&
                      ( ( image.getHeight() % this.tileSize ) == 0 );
            }
         }
         catch( final IOException e )
         {
            return false;
         }
      }
      return false;
   }

   /**
    * @param dscFile a tileset description file (".dsc")
    * @return the corresponding PNG file for that descriptor.
    */
   public static File getPngFileFor( final File dscFile )
   {
      final File directory = dscFile.getParentFile();
      final String pngFileName = getPngFileNameFor( dscFile.getName() );
      return new File( directory, pngFileName );
   }

   /**
    * Given a tileset name this returns the name of the corresponding png file.
    * <p>
    * 
    * <pre>
    * input  : towntiles
    * returns: towntiles.png
    *
    * input  : towntiles.dsc
    * returns: towntiles.png
    * </pre>
    *
    * @param dscFileName A tileset name (with, or without, the ".dsc" extension)
    * @return the corresponding PNG filename with the .png extension.
    */
   public static String getPngFileNameFor( final String dscFileName )
   {
      final int dscIndex = dscFileName.toLowerCase().lastIndexOf( DSC_SUFFIX );
      final String stripped = dscIndex != -1 ? dscFileName.substring( 0, dscIndex ) : dscFileName;
      return stripped.toLowerCase() + PNG_SUFFIX;
   }

   /**
    * @param tilesetName A tileset name (with, or without, the ".dsc" extension)
    * @return the file name for the tileset description.
    */
   public static String getDscFileNameFor( final String tilesetName )
   {
      if( tilesetName.toLowerCase().endsWith( DSC_SUFFIX ) )
      {
         return tilesetName;
      }
      return tilesetName.toLowerCase() + DSC_SUFFIX;
   }
}
