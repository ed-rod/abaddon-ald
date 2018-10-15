package uk.co.eduardo.abaddon.ald.data.undo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.undo.UndoableEdit;

/**
 * Manages a stack of undo actions.
 * <p>
 * Some actions can be consolidated into a single undo action.
 *
 * @author Ed
 */
public final class UndoManager
{
   private final List< UndoManagerListener > listeners = new CopyOnWriteArrayList<>();

   private final List< UndoableEdit > edits = new ArrayList<>();

   private int insertionPoint = 0;

   /**
    * Adds a listener that will be notified whenever an undo or redo is performed with this undo manager.
    *
    * @param listener the listener to add.
    */
   public void addUndoManagerListener( final UndoManagerListener listener )
   {
      if( ( listener != null ) && !this.listeners.contains( listener ) )
      {
         this.listeners.add( listener );
      }
   }

   /**
    * Removes a listener.
    *
    * @param listener the listener to remove.
    */
   public void removeUndoManagerListener( final UndoManagerListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * Adds an {@link UndoableEdit} to the Undo stack.
    *
    * @param edit the edit to add to the stack.
    */
   public void addEdit( final UndoableEdit edit )
   {
      // First clear all the edits after the insertion point.
      for( int erase = this.insertionPoint; erase < this.edits.size(); erase++ )
      {
         this.edits.remove( this.insertionPoint );
      }
      this.edits.add( this.insertionPoint++, edit );
      fireStackChanged();
   }

   /**
    * @return whether there are any edits that can be undone
    */
   public boolean canUndo()
   {
      return this.insertionPoint > 0;
   }

   /**
    * @return whether there are any edits that can be redone.
    */
   public boolean canRedo()
   {
      return this.insertionPoint < this.edits.size();
   }

   /**
    * If calling {@link #canUndo()} returns <code>true</code> then calling this method will undo the last edit, remove it from the
    * undo stack and place it on the redo stack.
    * <p>
    * If {@link #canUndo()} returns <code>false</code> then this method has no effect.
    */
   public void undo()
   {
      if( canUndo() )
      {
         // Pop the last edit
         final UndoableEdit lastEdit = this.edits.get( --this.insertionPoint );
         lastEdit.undo();
         fireStackChanged();
      }
   }

   /**
    * If calling {@link #canRedo()} returns <code>true</code> then calling this method will redo the last edit, remove it from the
    * redo stack and place it on the undo stack.
    * <p>
    * If {@link #canRedo()} returns <code>false</code> then this method has no effect.
    */
   public void redo()
   {
      if( canRedo() )
      {
         final UndoableEdit nextEdit = this.edits.get( this.insertionPoint++ );
         nextEdit.redo();
         fireStackChanged();
      }
   }

   /**
    * @return the display name for next edit in the undo stack.
    */
   public String getUndoPresentationName()
   {
      if( canUndo() )
      {
         final UndoableEdit lastEdit = this.edits.get( this.insertionPoint - 1 );
         return lastEdit.getPresentationName();
      }
      return ""; //$NON-NLS-1$
   }

   /**
    * @return the display name for the next edit in the redo stack.
    */
   public String getRedoPresentationName()
   {
      if( canRedo() )
      {
         final UndoableEdit nextEdit = this.edits.get( this.insertionPoint );
         return nextEdit.getPresentationName();
      }
      return ""; //$NON-NLS-1$
   }

   /**
    * @return the number of items on the undo stack.
    */
   public int getUndoStackSize()
   {
      return this.insertionPoint;
   }

   /**
    * Notify listeners that an edit happened.
    */
   protected void fireStackChanged()
   {
      for( final UndoManagerListener listener : this.listeners )
      {
         listener.stackChanged();
      }
   }
}
