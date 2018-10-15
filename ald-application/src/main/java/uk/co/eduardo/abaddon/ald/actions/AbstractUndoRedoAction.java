package uk.co.eduardo.abaddon.ald.actions;

import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManager;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManagerListener;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractMapActiveAction;

/**
 * Abstract action that is aware of the current undo/redo stack.
 *
 * @author Ed
 */
public abstract class AbstractUndoRedoAction extends AbstractMapActiveAction
{
   private final UndoManagerListener undoManagerListener = new UndoManagerListener()
   {
      @Override
      public void stackChanged()
      {
         setEnabled( updateEnabled() );
      }
   };

   private UndoManager undoManager;

   /**
    * @param bundle Resource bundle
    * @param key the base key for the action.
    */
   public AbstractUndoRedoAction( final ResourceBundle bundle, final String key )
   {
      super( bundle, key );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void mapModelChanged( final PropertyModel oldModel, final PropertyModel newModel )
   {
      if( this.undoManager != null )
      {
         this.undoManager.removeUndoManagerListener( this.undoManagerListener );
         this.undoManager = null;
      }
      if( newModel != null )
      {
         this.undoManager = newModel.get( Properties.UndoManager );
         this.undoManager.addUndoManagerListener( this.undoManagerListener );
      }
      setEnabled( updateEnabled() );
   }

   /**
    * @return the current undo manager.
    */
   protected UndoManager getUndoManager()
   {
      return this.undoManager;
   }
}
