package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;
import uk.co.eduardo.abaddon.ald.ui.WindowAncestorUtilities;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractItemAction;

/**
 * Action to open an existing project
 *
 * @author Ed
 */
public class ProjectOpenAction extends AbstractItemAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   /**
    * Initializes an action that allows the user to open a project
    */
   public ProjectOpenAction()
   {
      super( resources, "uk.co.eduardo.abaddon.action.project.open" ); //$NON-NLS-1$
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      final JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
      chooser.showOpenDialog( WindowAncestorUtilities.getWindow( e ) );

      final File project = chooser.getSelectedFile();
      if( project != null )
      {
         final Project opened = new Project( project );
         ProjectManager.getInstance().lockProject( opened );
      }
   }
}
