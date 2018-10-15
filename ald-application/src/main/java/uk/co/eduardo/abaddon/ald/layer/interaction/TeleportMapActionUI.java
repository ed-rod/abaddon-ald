package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.util.ResourceBundle;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter;
import uk.co.eduardo.abaddon.map.actions.TeleportAction;

/**
 *
 * @author Ed
 */
public class TeleportMapActionUI implements MapActionUI
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   /**
    * {@inheritDoc}
    */
   @Override
   public int getSupportedType()
   {
      return TeleportAction.TELEPORT_ACTION_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void populateContextMenu( final JComponent component, final ActionAdapter action )
   {
      component.add( new TeleportActionContextEditor( action ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName()
   {
      return resources.getString( "uk.co.eduardo.abaddon.interactive.action.teleport" ); //$NON-NLS-1$
   }
}