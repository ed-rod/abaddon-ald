package uk.co.eduardo.abaddon.ald.ui.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.undo.UndoableEdit;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManager;

/**
 * An action that automatically adds an {@link UndoableEdit} to the {@link UndoManager}.
 *
 * @author Ed
 */
public abstract class AbstractUndoableAction extends AbstractMapActiveAction
{
   private UndoManager undoManager;

   /**
    * @param bundle Resource bundle
    * @param key the base key for the action.
    */
   public AbstractUndoableAction( final ResourceBundle bundle, final String key )
   {
      super( bundle, key );

      if( getModel() != null )
      {
         this.undoManager = getModel().get( Properties.UndoManager );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final void actionPerformed( final ActionEvent e )
   {
      final UndoableEdit edit = performAction( e );
      if( edit != null )
      {
         if( this.undoManager != null )
         {
            this.undoManager.addEdit( edit );
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void mapModelChanged( final PropertyModel oldModel, final PropertyModel newModel )
   {
      super.mapModelChanged( oldModel, newModel );
      this.undoManager = null;

      if( newModel != null )
      {
         this.undoManager = newModel.get( Properties.UndoManager );
      }
   }

   /**
    * @param event the event.
    * @return an {@link UndoableEdit} to undo the action performed.
    */
   protected abstract UndoableEdit performAction( ActionEvent event );
}
