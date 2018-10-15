package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.Action;

/**
 * Action to undo the last edit.
 *
 * @author Ed
 */
public class UndoAction extends AbstractUndoRedoAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String FORMAT = resources.getString( "uk.co.eduardo.abaddon.undo.format" ); //$NON-NLS-1$

   private final String defaultName;

   /**
    * Initializes an action to undo the last action performed.
    */
   public UndoAction()
   {
      super( resources, "uk.co.eduardo.abaddon.action.undo" ); //$NON-NLS-1$
      this.defaultName = (String) getValue( Action.NAME );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      getUndoManager().undo();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected boolean updateEnabled()
   {
      if( !super.updateEnabled() )
      {
         setActionName( this.defaultName );
         return false;
      }
      if( getUndoManager() == null )
      {
         setActionName( this.defaultName );
         return false;
      }
      final boolean isEnabled = getUndoManager().canUndo();
      final String itemName = getUndoManager().getUndoPresentationName();
      final String actionName = MessageFormat.format( FORMAT, new Object[]
      {
         itemName
      } );

      setActionName( actionName );
      return isEnabled;
   }

   private void setActionName( final String name )
   {
      if( name != null )
      {
         putValue( Action.NAME, name );
      }
   }
}
