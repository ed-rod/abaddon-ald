package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.ui.action.AbstractMapActiveAction;

/**
 * Action to copy the selected content into the clipboard.
 *
 * @author Ed
 */
public class CopyAction extends AbstractMapActiveAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   /**
    * Initializes an action to copy the selected content into the clipboard.
    */
   public CopyAction()
   {
      super( resources, "uk.co.eduardo.abaddon.action.copy" ); //$NON-NLS-1$
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      // TODO Auto-generated method stub
   }
}
