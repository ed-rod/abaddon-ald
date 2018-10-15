package uk.co.eduardo.abaddon.ald.actions;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.ald.ui.WindowAncestorUtilities;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractProjectAction;
import uk.co.eduardo.abaddon.ald.utils.FileUtilities;

/**
 * This action packages together all the game resources in the project into a resource bundle ready to plug into the game engine.
 *
 * @author Ed
 */
public class ProjectExportAction extends AbstractProjectAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String RAW = "res/raw"; //$NON-NLS-1$

   private static final String DRAWABLE = "res/drawable"; //$NON-NLS-1$

   private static final String LAYOUT = "res/layout"; //$NON-NLS-1$

   private static final String GAME_LAYOUT_FILE = "game_layout.xml"; //$NON-NLS-1$

   /**
    * Initializes an action that exports all project files into a bundle ready for use with the game engine.
    */
   public ProjectExportAction()
   {
      super( resources, "uk.co.eduardo.abaddon.action.project.export" ); //$NON-NLS-1$
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      final File exportDirectory = getExportDirectory( WindowAncestorUtilities.getWindow( e ) );
      if( exportDirectory == null )
      {
         return;
      }

      try
      {
         setupBaseFiles( exportDirectory );

         exportMaps( exportDirectory );
         exportTilesets( exportDirectory );
         exportNpcs( exportDirectory );
         exportPcs( exportDirectory );
         exportMusic( exportDirectory );
         exportSettings( exportDirectory );
      }
      catch( final IOException exception )
      {
         JOptionPane.showMessageDialog( WindowAncestorUtilities.getWindow( e ),
                                        resources.getString( "uk.co.eduardo.abaddon.map.export.error" ), //$NON-NLS-1$
                                        resources.getString( "uk.co.eduardo.abaddon.title" ), //$NON-NLS-1$
                                        JOptionPane.INFORMATION_MESSAGE );
      }
   }

   private File getExportDirectory( final Window parent )
   {
      final JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
      chooser.showSaveDialog( parent );

      return chooser.getSelectedFile();
   }

   private void setupBaseFiles( final File exportDirectory ) throws IOException
   {
      // Unzip the contents of the base files to the export directory
      ZipFile zip = null;
      try
      {
         final URL url = ClassLoader.getSystemResource( "uk/co/eduardo/abaddon/ald/package.zip" ); //$NON-NLS-1$
         final File zipFile = new File( url.toURI() );
         zip = new ZipFile( zipFile );
         final Enumeration< ? extends ZipEntry > entries = zip.entries();
         while( entries.hasMoreElements() )
         {
            final ZipEntry entry = entries.nextElement();
            final File output = new File( exportDirectory, entry.getName() );
            if( entry.isDirectory() )
            {
               output.mkdirs();
            }
            else
            {
               output.createNewFile();
               InputStream in = null;
               OutputStream out = null;
               try
               {
                  in = zip.getInputStream( entry );
                  out = new BufferedOutputStream( new FileOutputStream( output ) );
                  FileUtilities.copy( in, out );
               }
               finally
               {
                  if( in != null )
                  {
                     in.close();
                  }
                  if( out != null )
                  {
                     out.close();
                  }
               }
            }
         }
      }
      catch( final URISyntaxException e )
      {
         throw new IOException( "Failed to find zip file.", e ); //$NON-NLS-1$
      }
      finally
      {
         if( zip != null )
         {
            zip.close();
         }
      }
   }

   private void exportMaps( final File exportDirectory ) throws IOException
   {
      final File output = getRawDir( exportDirectory );
      output.mkdirs();
      for( final File map : getProject().getAvailableMapsModel().getAvailableMapFiles() )
      {
         FileUtilities.copy( map, output );
      }
   }

   private void exportTilesets( final File exportDirectory ) throws IOException
   {
      // Copy ".png" files to the drawble directory.
      final File drawable = getDrawableDir( exportDirectory );
      drawable.mkdirs();
      for( final File png : getProject().getAvailableTilesetsModel().getAvailablePngTilesetFiles() )
      {
         FileUtilities.copy( png, drawable );
      }

      // Copy ".dsc" files to the raw directory.
      final File raw = getRawDir( exportDirectory );
      raw.mkdirs();
      for( final File dsc : getProject().getAvailableTilesetsModel().getAvailableDscTilesetFiles() )
      {
         FileUtilities.copy( dsc, raw );
      }

   }

   private void exportNpcs( final File exportDirectory ) throws IOException
   {
      final File drawable = getDrawableDir( exportDirectory );
      drawable.mkdirs();
      for( final File npc : getProject().getAvailableNpcsModel().getAvailableNpcFiles() )
      {
         FileUtilities.copy( npc, drawable );
      }
   }

   private void exportPcs( final File exportDirectory ) throws IOException
   {
      final File drawable = getDrawableDir( exportDirectory );
      drawable.mkdirs();
      for( final File pc : getProject().getAvailablePcsModel().getAvailablePcFiles() )
      {
         FileUtilities.copy( pc, drawable );
      }
   }

   private void exportMusic( final File exportDirectory ) throws IOException
   {
      final File raw = getRawDir( exportDirectory );
      raw.mkdirs();
      for( final File music : getProject().getAvailableMusicModel().getAvailableMusicFiles() )
      {
         FileUtilities.copy( music, raw );
      }
   }

   private void exportSettings( final File exportDirectory ) throws IOException
   {
      // We want to replace the placeholder tokens in the res/layout/game_layout.xml file
      final File target = new File( getLayoutDir( exportDirectory ), GAME_LAYOUT_FILE );
      final ProjectSettings settings = getProject().getSettings();

      // First replace the tileSize token
      final String tileSize = Integer.toString( settings.get( ProjectSettings.TILE_WIDTH ) );
      FileUtilities.replace( target, ProjectSettings.TILE_WIDTH, tileSize );

      // Now replace the spriteHeight token
      final String spriteHeight = Integer.toString( settings.get( ProjectSettings.SPRITE_HEIGHT ) );
      FileUtilities.replace( target, ProjectSettings.SPRITE_HEIGHT, spriteHeight );
   }

   private static File getRawDir( final File root )
   {
      return new File( root, RAW );
   }

   private static File getDrawableDir( final File root )
   {
      return new File( root, DRAWABLE );
   }

   private static File getLayoutDir( final File root )
   {
      return new File( root, LAYOUT );
   }
}
