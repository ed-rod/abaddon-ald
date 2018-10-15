package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.util.ResourceBundle;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter;
import uk.co.eduardo.abaddon.map.actions.ChangeLayerAction;

/**
 * Provides the UI for editing a {@link ChangeLayerAction}
 *
 * @author Ed
 */
public class ChangeLayerMapActionUI implements MapActionUI
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   /**
    * {@inheritDoc}
    */
   @Override
   public int getSupportedType()
   {
      return ChangeLayerAction.CHANGE_LAYER_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void populateContextMenu( final JComponent component, final ActionAdapter action )
   {
      component.add( new ChangeLayerActionContextEditor( action ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName()
   {
      return resources.getString( "uk.co.eduardo.abaddon.interactive.action.change.layer" ); //$NON-NLS-1$
   }
}
