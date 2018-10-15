package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import uk.co.eduardo.abaddon.ald.data.MapData;
import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter;
import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter.ActionAdapterListener;
import uk.co.eduardo.abaddon.ald.data.adapter.ChangeLayerActionAdapterType;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractUndoableAction;

/**
 * Context editor for a change layer action.
 *
 * @author Ed
 */
class ChangeLayerActionContextEditor extends JPanel
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final ActionAdapter action;

   private final JLabel layerIndexLabel = new JLabel();

   private final ChangeLayerAction raiseAction;

   private final ChangeLayerAction lowerAction;

   private final ActionAdapterListener actionListener = new ActionAdapterListener()
   {
      @Override
      public void actionChanged( final ActionAdapter source )
      {
         updateLayerIndexLabel();
      }
   };

   ChangeLayerActionContextEditor( final ActionAdapter action )
   {
      super( new BorderLayout() );
      this.action = action;
      this.raiseAction = new ChangeLayerAction( action, 1 );
      this.lowerAction = new ChangeLayerAction( action, -1 );
      createUI();
   }

   @Override
   public void addNotify()
   {
      super.addNotify();
      this.action.addActionAdapterListener( this.raiseAction );
      this.action.addActionAdapterListener( this.lowerAction );
      this.action.addActionAdapterListener( this.actionListener );
   }

   @Override
   public void removeNotify()
   {
      this.action.removeActionAdapterListener( this.raiseAction );
      this.action.removeActionAdapterListener( this.lowerAction );
      this.action.removeActionAdapterListener( this.actionListener );
      super.removeNotify();
   }

   private Integer getLayer()
   {
      return this.action.getSpecializedProperty( ChangeLayerActionAdapterType.CLA_LAYER_INDEX );
   }

   private void createUI()
   {
      final FormLayout layout = new FormLayout( "p, $rg, fill:p", "fill:p" ); //$NON-NLS-1$ //$NON-NLS-2$
      final DefaultFormBuilder builder = new DefaultFormBuilder( layout );

      addLayerUI( builder );

      add( builder.getPanel() );
   }

   private void addLayerUI( final DefaultFormBuilder builder )
   {
      final JLabel layerLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.interactive.action.change.layer.label" ) ); //$NON-NLS-1$
      final JToolBar layerToolbar = new JToolBar();
      layerToolbar.setFloatable( false );
      layerToolbar.add( this.raiseAction.createToolBarItem() );
      layerToolbar.add( this.lowerAction.createToolBarItem() );
      layerToolbar.add( this.layerIndexLabel );
      builder.append( layerLabel );
      builder.append( layerToolbar );

      updateLayerIndexLabel();
   }

   private void updateLayerIndexLabel()
   {
      this.layerIndexLabel.setText( Integer.toString( getLayer() + 1 ) );
   }

   private static class ChangeLayerAction extends AbstractUndoableAction implements ActionAdapterListener
   {
      private final int delta;

      private final ActionAdapter action;

      private ChangeLayerAction( final ActionAdapter action, final int delta )
      {
         super( resources, "uk.co.eduardo.abaddon.interactive.action.change.layer" + delta ); //$NON-NLS-1$
         this.action = action;
         this.delta = delta;
         setEnabled( updateEnabled() );
      }

      @Override
      protected UndoableEdit performAction( final ActionEvent event )
      {
         final int oldLayerIndex = getLayer();
         final int newLayerIndex = oldLayerIndex + this.delta;
         setLayer( newLayerIndex );
         setEnabled( updateEnabled() );
         return new ChangeLayerUndoableEdit( this.action, oldLayerIndex, newLayerIndex );
      }

      @Override
      protected boolean updateEnabled()
      {
         if( !super.updateEnabled() )
         {
            return false;
         }
         if( this.action != null )
         {
            final int index = getLayer() + this.delta;
            return ( ( index >= 0 ) && ( index < MapData.MAX_LAYERS ) );
         }
         return false;
      }

      private Integer getLayer()
      {
         return this.action.getSpecializedProperty( ChangeLayerActionAdapterType.CLA_LAYER_INDEX );
      }

      private void setLayer( final int layer )
      {
         this.action.setSpecializedProperty( ChangeLayerActionAdapterType.CLA_LAYER_INDEX, layer );
      }

      @Override
      public void actionChanged( final ActionAdapter source )
      {
         setEnabled( updateEnabled() );
      }
   }

   private static final class ChangeLayerUndoableEdit extends AbstractUndoableEdit
   {
      private final ActionAdapter action;

      private final int oldLayerIndex;

      private final int newLayerIndex;

      private ChangeLayerUndoableEdit( final ActionAdapter action, final int oldLayerIndex, final int newLayerIndex )
      {
         this.action = action;
         this.oldLayerIndex = oldLayerIndex;
         this.newLayerIndex = newLayerIndex;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         setLayer( this.oldLayerIndex );
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         setLayer( this.newLayerIndex );
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.action.change.layer.name" ); //$NON-NLS-1$
      }

      private void setLayer( final int layer )
      {
         this.action.setSpecializedProperty( ChangeLayerActionAdapterType.CLA_LAYER_INDEX, layer );
      }
   }
}
