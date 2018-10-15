package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractProjectAction;

/**
 * Closes the currently active project.
 *
 * @author Ed
 */
public class ProjectCloseAction extends AbstractProjectAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   /**
    * Initializes an action that will close the currently active project.
    */
   public ProjectCloseAction()
   {
      super( resources, "uk.co.eduardo.abaddon.action.project.close" ); //$NON-NLS-1$
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      ProjectManager.getInstance().unlockProject();
   }
}
