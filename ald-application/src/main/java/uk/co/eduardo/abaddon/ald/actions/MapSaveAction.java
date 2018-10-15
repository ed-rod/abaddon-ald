package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractMapActiveAction;
import uk.co.eduardo.abaddon.ald.utils.SaveUtilities;

/**
 * Action to save the current map.
 *
 * @author Ed
 */
public class MapSaveAction extends AbstractMapActiveAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final PropertyListener listener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel model )
      {
         setEnabled( updateEnabled() );
      }
   };

   /**
    * Initializes an action that will save the current model.
    */
   public MapSaveAction()
   {
      super( resources, "uk.co.eduardo.abaddon.action.map.save" ); //$NON-NLS-1$
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      SaveUtilities.saveMap( getProject(), getModel() );
      getModel().set( Properties.UncommittedChanges, false );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void mapModelChanged( final PropertyModel oldModel, final PropertyModel newModel )
   {
      if( oldModel != null )
      {
         oldModel.removePropertyListener( Properties.UncommittedChanges, this.listener );
      }
      if( newModel != null )
      {
         newModel.addPropertyListener( Properties.UncommittedChanges, this.listener );
      }
      setEnabled( updateEnabled() );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected boolean updateEnabled()
   {
      if( !super.updateEnabled() )
      {
         return false;
      }
      return getModel().get( Properties.UncommittedChanges );
   }
}
