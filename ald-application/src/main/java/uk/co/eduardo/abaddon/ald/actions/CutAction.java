package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.ui.action.AbstractMapActiveAction;

/**
 * Action that copies the selected content into the clipboard and then removes it.
 *
 * @author Ed
 */
public class CutAction extends AbstractMapActiveAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   /**
    * Initializes an action to to cut the content into the clipboard.
    */
   public CutAction()
   {
      super( resources, "uk.co.eduardo.abaddon.action.cut" ); //$NON-NLS-1$
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
