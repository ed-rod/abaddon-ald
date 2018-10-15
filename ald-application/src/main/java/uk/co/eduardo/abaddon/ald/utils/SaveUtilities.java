package uk.co.eduardo.abaddon.ald.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.map.MapDefinition;
import uk.co.eduardo.abaddon.map.MapFactory;
import uk.co.eduardo.map.sections.ActionSection;
import uk.co.eduardo.map.sections.HeaderSection;
import uk.co.eduardo.map.sections.MapSection;
import uk.co.eduardo.map.sections.MonsterSection;
import uk.co.eduardo.map.sections.NpcSection;

/**
 * Provides utility methods for saving a map.
 *
 * @author Ed
 */
public final class SaveUtilities
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private SaveUtilities()
   {
      // Hide constructor for utility class.
   }

   /**
    * Commits any changes to disc.
    *
    * @param project the project to which the map belongs.
    * @param map the map to save.
    */
   public static void saveMap( final Project project, final PropertyModel map )
   {
      if( ( project == null ) || ( map == null ) )
      {
         return;
      }
      if( !map.get( Properties.UncommittedChanges ) )
      {
         return;
      }
      final File mapDirectory = project.getAvailableMapsModel().getDirectory();
      final File mapFile = new File( mapDirectory, map.get( Properties.MapName ) );
      saveMap( project, map, mapFile );
   }

   /**
    * Commits any changes to disc.
    *
    * @param project the project to which the map belongs.
    * @param map the map to save.
    * @param mapFile the output file
    */
   public static void saveMap( final Project project, final PropertyModel map, final File mapFile )
   {
      if( ( project == null ) || ( map == null ) )
      {
         return;
      }
      final MapDefinition mapDefinition = getMapDefinition( map );
      // Write the file
      OutputStream stream = null;
      try
      {
         stream = new BufferedOutputStream( new FileOutputStream( mapFile ) );
         MapFactory.writeMap( stream, mapDefinition );
         map.set( Properties.UncommittedChanges, false );
      }
      catch( final IOException exception )
      {
         final String errorFormat = resources.getString( "uk.co.eduardo.abaddon.map.save.error" ); //$NON-NLS-1$
         final String errorTitle = resources.getString( "uk.co.eduardo.abaddon.title" ); //$NON-NLS-1$
         final String error = MessageFormat.format( errorFormat, new Object[]
         {
            mapFile
         } );
         JOptionPane.showMessageDialog( null, error, errorTitle, JOptionPane.ERROR_MESSAGE );
      }
      finally
      {
         if( stream != null )
         {
            try
            {
               stream.close();
            }
            catch( final IOException e )
            {
               // Do nothing.
            }
         }
      }
   }

   private static MapDefinition getMapDefinition( final PropertyModel map )
   {
      // Header section
      final HeaderSection header = map.get( Properties.HeaderData ).createFileSection();
      final NpcSection npcs = map.get( Properties.NpcData ).createFileSection();
      final ActionSection actions = map.get( Properties.ActionData ).createFileSection();
      final MapSection maps = map.get( Properties.MapData ).createFileSection();
      final MonsterSection monsters = map.get( Properties.MonsterData ).createFileSection();

      return new MapDefinition( header, npcs, actions, maps, monsters );
   }
}
