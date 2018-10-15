package uk.co.eduardo.abaddon.ald.layer;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManager;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManagerListener;

/**
 * This layer listens for changes to the undo manager and marks the map as needing to be saved whenever a change is made.
 * <p>
 * It also tracks undo/redo events and resets the save state when we reach a point of last save.
 *
 * @author Ed
 */
public class MarkModifiedLayer extends MapLayer
{
   private final UndoManager undoManager;

   private int lastSaveUndoStackSize;

   /** Listens for changes in the save state */
   private final PropertyListener saveListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel model )
      {
         if( !model.get( MarkModifiedLayer.this.uncommittedChangesProperty ) )
         {
            // map model was just saved.
            MarkModifiedLayer.this.lastSaveUndoStackSize = MarkModifiedLayer.this.undoManager.getUndoStackSize();
         }
      }
   };

   private final UndoManagerListener undoManagerListener = new UndoManagerListener()
   {
      @Override
      public void stackChanged()
      {
         final int currentSize = MarkModifiedLayer.this.undoManager.getUndoStackSize();
         final boolean changes = currentSize != MarkModifiedLayer.this.lastSaveUndoStackSize;
         getModel().set( MarkModifiedLayer.this.uncommittedChangesProperty, changes );
      }
   };

   private final Property< Boolean > uncommittedChangesProperty;

   /**
    * @param model the current model.
    * @param host the host for the layer.
    * @param undoManagerProperty property for the undo manager.
    * @param uncommittedChangesProperty property for the uncommitted changes flag.
    */
   public MarkModifiedLayer( final PropertyModel model,
                             final JComponent host,
                             final Property< UndoManager > undoManagerProperty,
                             final Property< Boolean > uncommittedChangesProperty )
   {
      super( model, host );
      this.uncommittedChangesProperty = uncommittedChangesProperty;
      this.undoManager = model.get( undoManagerProperty );
      this.lastSaveUndoStackSize = this.undoManager.getUndoStackSize();

      this.undoManager.addUndoManagerListener( this.undoManagerListener );
      model.addPropertyListener( uncommittedChangesProperty, this.saveListener );
   }
}
