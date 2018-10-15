package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.Action;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractMapActiveAction;

/**
 * Abstract control action.
 *
 * @author Ed
 */
public class AbstractControlAction extends AbstractMapActiveAction
{
   private final String controlKey;

   private final PropertyListener keyListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel model )
      {
         updateSelectedState();
      }
   };

   /**
    * @param bundle Resource bundle
    * @param key the base key for the action.
    * @param toggle whether the action is a toggle action or not.
    * @param controlKey the key of the control layer to activate.
    */
   public AbstractControlAction( final ResourceBundle bundle, final String key, final boolean toggle, final String controlKey )
   {
      super( bundle, key, toggle );
      this.controlKey = controlKey;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      final PropertyModel model = getModel();
      if( model != null )
      {
         model.set( Properties.SelectedControl, this.controlKey );
      }
      updateSelectedState();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void mapModelChanged( final PropertyModel oldModel, final PropertyModel newModel )
   {
      super.mapModelChanged( oldModel, newModel );
      if( oldModel != null )
      {
         oldModel.removePropertyListener( Properties.SelectedControl, this.keyListener );
      }
      if( newModel != null )
      {
         newModel.addPropertyListener( Properties.SelectedControl, this.keyListener );
         updateSelectedState();
      }
   }

   private void updateSelectedState()
   {
      final String currentKey = getModel().get( Properties.SelectedControl );
      final boolean selected = this.controlKey.equals( currentKey );
      putValue( Action.SELECTED_KEY, selected );
   }
}
