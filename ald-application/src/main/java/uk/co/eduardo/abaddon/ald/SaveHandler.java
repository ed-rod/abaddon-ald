package uk.co.eduardo.abaddon.ald;

import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapAdapter;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapListener;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectListener;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;
import uk.co.eduardo.abaddon.ald.utils.SaveUtilities;

/**
 * Handles saving of files.
 *
 * @author Ed
 */
public class SaveHandler
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final OpenMapListener OPEN_MAP_LISTENER = new OpenMapAdapter()
   {
      @Override
      public void mapClosed( final PropertyModel closed )
      {
         offerToSave( closed );
      }
   };

   private static final ProjectListener PROJECT_LISTENER = new ProjectListener()
   {
      @Override
      public void projectOpened( final Project project )
      {
         if( project != null )
         {
            project.getOpenMapsModel().addMapModelListener( OPEN_MAP_LISTENER );
         }
      }

      @Override
      public void projectClosed( final Project project )
      {
         if( project != null )
         {

            // Ensure that all projects are closed first.
            final List< PropertyModel > openMaps = project.getOpenMapsModel().getOpenMaps();
            for( final PropertyModel open : openMaps.toArray( new PropertyModel[ 0 ] ) )
            {
               project.getOpenMapsModel().closeMap( open );
            }
            project.getOpenMapsModel().removeMapModelListener( OPEN_MAP_LISTENER );
         }
      }
   };

   static
   {
      ProjectManager.getInstance().addProjectListener( PROJECT_LISTENER );
   }

   /**
    * Installs the Save handler that will prompt the user to save all maps before they are closed.
    */
   public static void install()
   {
      // Does nothing except ensure that the static initializer is run. Calling multiple times
      // has not subsequent effect.
   }

   /**
    * This method will mark all open maps as not needing to be saved. This doesn't actually commit any changes but it is useful if
    * you want to close all maps without offering to save each one individually.
    */
   public static void markAllOpenMapsSaved()
   {
      final Project project = ProjectManager.getInstance().getLockedProject();
      if( project == null )
      {
         return;
      }
      for( final PropertyModel open : project.getOpenMapsModel().getOpenMaps() )
      {
         open.set( Properties.UncommittedChanges, false );
      }
   }

   private static void offerToSave( final PropertyModel map )
   {
      if( !map.get( Properties.UncommittedChanges ) )
      {
         // Do nothing there are no changes to save.
         return;
      }
      final String message = resources.getString( "uk.co.eduardo.abaddon.save.file.offer" ); //$NON-NLS-1$
      final String title = resources.getString( "uk.co.eduardo.abaddon.title" ); //$NON-NLS-1$
      final int option = JOptionPane.showConfirmDialog( null, message, title, JOptionPane.YES_NO_OPTION );
      if( option == JOptionPane.YES_OPTION )
      {
         // Get the save location
         final Project project = ProjectManager.getInstance().getLockedProject();
         SaveUtilities.saveMap( project, map );
      }
   }
}
