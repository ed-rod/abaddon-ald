package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.ui.InputDialog;
import uk.co.eduardo.abaddon.ald.ui.WindowAncestorUtilities;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractMapActiveAction;
import uk.co.eduardo.abaddon.ald.utils.OpenUtilities;
import uk.co.eduardo.abaddon.ald.utils.SaveUtilities;

/**
 * Action to save the current map.
 *
 * @author Ed
 */
public class MapSaveAsAction extends AbstractMapActiveAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String MESSAGE = resources.getString( "uk.co.eduardo.abaddon.file.input.message" ); //$NON-NLS-1$

   private static final String PREFIX = resources.getString( "uk.co.eduardo.abaddon.file.input.prefix" ); //$NON-NLS-1$

   private static final String MAP_EXTENSION = ".map"; //$NON-NLS-1$

   /**
    * Initializes an action that will let the user select the location to which the map will be saved.
    */
   public MapSaveAsAction()
   {
      super( resources, "uk.co.eduardo.abaddon.action.map.save.as" ); //$NON-NLS-1$
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      // First get the list of taken map names
      final List< String > names = new ArrayList<>();
      for( final File file : getProject().getAvailableMapsModel().getAvailableMapFiles() )
      {
         names.add( file.getName() );
      }
      final String name = InputDialog.showFileInputDialog( WindowAncestorUtilities.getWindow( e ),
                                                           MESSAGE,
                                                           PREFIX,
                                                           names,
                                                           MAP_EXTENSION );
      if( name != null )
      {
         String fullName = name;
         // Check for the presence of the extension
         if( !name.toLowerCase().endsWith( MAP_EXTENSION ) )
         {
            fullName = fullName + MAP_EXTENSION;
         }
         final File mapFile = new File( getProject().getAvailableMapsModel().getDirectory(), fullName );
         SaveUtilities.saveMap( getProject(), getModel(), mapFile );

         // Close the current map and open the newly saved one
         getProject().getOpenMapsModel().closeMap( getModel() );
         OpenUtilities.openMap( getProject(), mapFile );
      }
   }
}
