package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.ui.action.AbstractMapActiveAction;

/**
 * Action that pastes the clipboard contents into the map.
 *
 * @author Ed
 */
public class PasteAction extends AbstractMapActiveAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   /**
    * Initializes an action to paste the content of the clipboard.
    */
   public PasteAction()
   {
      super( resources, "uk.co.eduardo.abaddon.action.paste" ); //$NON-NLS-1$
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
