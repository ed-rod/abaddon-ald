package uk.co.eduardo.abaddon.ald.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractItemAction;

/**
 * Action to exit the application.
 *
 * @author Ed
 */
public class ExitAction extends AbstractItemAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final Frame host;

   /**
    * Creates an action to exit the application.
    *
    * @param host the window to which this action is attached.
    */
   public ExitAction( final Frame host )
   {
      super( resources, "uk.co.eduardo.abaddon.action.exit" ); //$NON-NLS-1$
      this.host = host;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      ProjectManager.getInstance().unlockProject();

      // Dispose of the main application frame.
      this.host.dispose();
   }
}
