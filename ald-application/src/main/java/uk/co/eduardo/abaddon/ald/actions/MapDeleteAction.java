package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.ui.WindowAncestorUtilities;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractProjectAction;

/**
 * Action to delete a map.
 *
 * @author Ed
 */
public class MapDeleteAction extends AbstractProjectAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String MESSAGE = resources.getString( "uk.co.eduardo.abaddon.delete.map.prompt" ); //$NON-NLS-1$

   private static final String TITLE = resources.getString( "uk.co.eduardo.abaddon.delete.map.prompt.title" ); //$NON-NLS-1$

   private final String map;

   /**
    * Constructs an action that will delete the currently active map.
    */
   public MapDeleteAction()
   {
      this( null );
   }

   /**
    * Constructs an action that will delete the specified map.
    *
    * @param mapName name of the map to delete or <code>null</code> if the currently active map is to be deleted.
    */
   public MapDeleteAction( final String mapName )
   {
      super( resources, "uk.co.eduardo.abaddon.action.map.delete" ); //$NON-NLS-1$
      this.map = mapName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      String mapName = this.map;
      if( mapName == null )
      {
         // if the name is null then we're supposed to delete the active map.
         final PropertyModel active = getProject().getOpenMapsModel().getActiveMap();
         if( active == null )
         {
            // If there's no active map then we cannot do anything.
            return;
         }
         mapName = active.get( Properties.MapName );
      }
      final File mapFile = new File( getProject().getAvailableMapsModel().getDirectory(), mapName );

      if( !mapFile.exists() )
      {
         // Cannot delete as the file does not exist.
      }
      // First prompt the user to see if they really do want to delete the map
      if( JOptionPane.showConfirmDialog( WindowAncestorUtilities.getWindow( e ),
                                         MESSAGE,
                                         TITLE,
                                         JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION )
      {
         PropertyModel toDelete = null;
         if( this.map == null )
         {
            // Attempt to delete the active map.
            toDelete = getProject().getOpenMapsModel().getActiveMap();
         }
         else
         {
            // Look for a map with the given name.
            toDelete = getProject().getOpenMapsModel().getMap( this.map );
         }
         if( toDelete != null )
         {
            // First close the map if it is open. (We mark it has having no modified changes
            // or else the user would be prompted to save it first before deleting!
            toDelete.set( Properties.UncommittedChanges, false );
            getProject().getOpenMapsModel().closeMap( toDelete );
         }

         // Now delete the file.
         mapFile.delete();
      }
   }
}
