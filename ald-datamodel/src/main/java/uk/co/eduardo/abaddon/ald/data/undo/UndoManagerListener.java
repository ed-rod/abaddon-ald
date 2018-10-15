package uk.co.eduardo.abaddon.ald.data.undo;

/**
 * Implementors should register with a {@link UndoManager} to receive notification of changes to the undo/redo stack.
 *
 * @author Ed
 */
public interface UndoManagerListener
{
   /**
    * Notification that the undo/redo stack has changed.
    */
   void stackChanged();
}
