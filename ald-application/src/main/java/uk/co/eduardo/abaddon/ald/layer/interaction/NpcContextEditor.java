package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import uk.co.eduardo.abaddon.ald.data.MapData;
import uk.co.eduardo.abaddon.ald.data.adapter.NpcAdapter;
import uk.co.eduardo.abaddon.ald.data.adapter.NpcAdapter.NpcAdapterListener;
import uk.co.eduardo.abaddon.ald.data.project.AvailableNpcsModel;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.ald.sprite.SpriteUtilities;
import uk.co.eduardo.abaddon.ald.ui.DirectionSelector;
import uk.co.eduardo.abaddon.ald.ui.DirectionSelectorListener;
import uk.co.eduardo.abaddon.ald.ui.PopupWindow;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractUndoableAction;
import uk.co.eduardo.abaddon.graphics.layer.Direction;

/**
 * Context component for quick NPC editing.
 *
 * @author Ed
 */
public class NpcContextEditor extends JPanel
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final NpcAdapter npc;

   private final JLabel layersLabel = new JLabel();

   private final ChangeLayerAction raiseNpcAction;

   private final ChangeLayerAction lowerNpcAction;

   private final ProjectSettings settings;

   /**
    * Initializes a component that allows quick editing of an NPC.
    *
    * @param npc the NPC to edit.
    * @param settings the current project settings
    */
   public NpcContextEditor( final NpcAdapter npc, final ProjectSettings settings )
   {
      super( new BorderLayout() );
      this.npc = npc;
      this.settings = settings;
      this.raiseNpcAction = new ChangeLayerAction( 1 );
      this.lowerNpcAction = new ChangeLayerAction( -1 );
      createUI();
   }

   private void createUI()
   {
      final FormLayout layout = new FormLayout( "p, $rg, fill:p", "fill:p" ); //$NON-NLS-1$ //$NON-NLS-2$
      final DefaultFormBuilder builder = new DefaultFormBuilder( layout );

      addSelectTypeUI( builder );
      addSetDirectionUI( builder );
      addMobilityUI( builder );
      addLayerUI( builder );
      builder.appendSeparator();
      addDeleteUI( builder );

      this.add( builder.getPanel() );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      super.addNotify();
      // Attach listeners
      this.npc.addNpcAdapterListener( this.raiseNpcAction );
      this.npc.addNpcAdapterListener( this.lowerNpcAction );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      // Detach listeners.
      this.npc.removeNpcAdapterListener( this.raiseNpcAction );
      this.npc.removeNpcAdapterListener( this.lowerNpcAction );
      super.removeNotify();
   }

   private void addSelectTypeUI( final DefaultFormBuilder builder )
   {
      final Project project = ProjectManager.getInstance().getLockedProject();
      final int spriteWidth = project.getSettings().get( ProjectSettings.TILE_WIDTH );
      final int spriteHeight = project.getSettings().get( ProjectSettings.SPRITE_HEIGHT );
      final AvailableNpcsModel model = project.getAvailableNpcsModel();
      final File npcFileToSelect = model.getNpcFile( this.npc.getType() );

      final JLabel typeLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.interactive.npc.type.label" ) ); //$NON-NLS-1$
      final List< Icon > icons = new ArrayList<>();
      Icon initialSelected = null;
      for( final File npcFile : model.getAvailableNpcFiles() )
      {
         icons.add( new ImageIcon( SpriteUtilities.getSpriteImage( npcFile.getName(),
                                                                   model.getDirectory(),
                                                                   Direction.DOWN,
                                                                   this.settings ),
                                   npcFile.getName() ) );
         if( npcFile.equals( npcFileToSelect ) )
         {
            initialSelected = icons.get( icons.size() - 1 );
         }
      }
      final JComboBox< Icon > typeCombo = new JComboBox<>( icons.toArray( new Icon[ 0 ] ) );
      typeCombo.setRenderer( new ImageComboRenderer( new Dimension( spriteWidth, spriteHeight ) ) );
      typeCombo.setMaximumRowCount( 6 );
      typeCombo.setSelectedItem( initialSelected );
      typeCombo.addActionListener( new ChangeNpcTypeAction( typeCombo ) );
      builder.append( typeLabel );
      builder.append( typeCombo );
   }

   private void addSetDirectionUI( final DefaultFormBuilder builder )
   {
      final JLabel directionLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.interactive.npc.direction.label" ) ); //$NON-NLS-1$
      final DirectionSelector directionSelector = new DirectionSelector( this.npc.getDirection(), this.settings );
      directionSelector.addDirectionSelectorListener( new ChangeDirectionAction() );

      builder.append( directionLabel );
      builder.append( directionSelector );
   }

   private void addMobilityUI( final DefaultFormBuilder builder )
   {
      final JLabel movementLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.interactive.npc.mobility.label" ) ); //$NON-NLS-1$
      final JRadioButton mobile = new JRadioButton( resources.getString( "uk.co.eduardo.abaddon.interactive.npc.mobility.mobile" ) ); //$NON-NLS-1$
      final JRadioButton fixed = new JRadioButton( resources.getString( "uk.co.eduardo.abaddon.interactive.npc.mobility.fixed" ) ); //$NON-NLS-1$
      final ButtonGroup group = new ButtonGroup();
      group.add( mobile );
      group.add( fixed );
      mobile.setSelected( !this.npc.isFixed() );
      fixed.setSelected( this.npc.isFixed() );
      mobile.addActionListener( new MovementAction( false ) );
      fixed.addActionListener( new MovementAction( true ) );

      final Box radioBox = Box.createHorizontalBox();
      radioBox.add( mobile );
      radioBox.add( fixed );

      builder.append( movementLabel );
      builder.append( radioBox );
   }

   private void addLayerUI( final DefaultFormBuilder builder )
   {
      final JLabel layerLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.interactive.npc.change.layer.label" ) ); //$NON-NLS-1$
      final JToolBar layerToolbar = new JToolBar();
      layerToolbar.setFloatable( false );
      layerToolbar.add( this.raiseNpcAction.createToolBarItem() );
      layerToolbar.add( this.lowerNpcAction.createToolBarItem() );
      layerToolbar.add( this.layersLabel );
      builder.append( layerLabel );
      builder.append( layerToolbar );
   }

   private void addDeleteUI( final DefaultFormBuilder builder )
   {
      final JLabel deleteLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.interactive.npc.delete.label" ) ); //$NON-NLS-1$
      final JToolBar deleteToolbar = new JToolBar();
      deleteToolbar.setFloatable( false );
      deleteToolbar.add( new DeleteAction().createToolBarItem() );
      builder.append( deleteLabel );
      builder.append( deleteToolbar );
   }

   private static class ImageComboRenderer implements ListCellRenderer< Icon >
   {
      private final BasicComboBoxRenderer renderer;

      private ImageComboRenderer( final Dimension preferredSize )
      {
         this.renderer = new BasicComboBoxRenderer()
         {
            @Override
            public Dimension getPreferredSize()
            {
               return preferredSize;
            }
         };
      }

      @Override
      public Component getListCellRendererComponent( final JList< ? extends Icon > list,
                                                     final Icon value,
                                                     final int index,
                                                     final boolean isSelected,
                                                     final boolean cellHasFocus )
      {
         this.renderer.setOpaque( isSelected && ( index != -1 ) );

         final Component comp = this.renderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

         if( ( value != null ) && ( comp instanceof JLabel ) )
         {
            ( (JLabel) comp ).setText( value.toString() );
         }
         return comp;
      }

   }

   private class ChangeLayerAction extends AbstractUndoableAction implements NpcAdapterListener
   {
      private final int delta;

      private ChangeLayerAction( final int delta )
      {
         super( resources, "uk.co.eduardo.abaddon.interactive.npc.change.layer" + delta ); //$NON-NLS-1$
         this.delta = delta;
         npcChanged( NpcContextEditor.this.npc );
      }

      @Override
      public UndoableEdit performAction( final ActionEvent e )
      {
         final int oldLayerIndex = NpcContextEditor.this.npc.getLayerIndex();
         final int newLayerIndex = oldLayerIndex + this.delta;
         NpcContextEditor.this.npc.setLayerIndex( newLayerIndex );
         npcChanged( NpcContextEditor.this.npc );

         return new ChangeLayerUndoableEdit( NpcContextEditor.this.npc, oldLayerIndex, newLayerIndex );
      }

      private void updateState()
      {
         final int newIndex = NpcContextEditor.this.npc.getLayerIndex() + this.delta;
         setEnabled( ( newIndex >= 0 ) && ( newIndex < MapData.MAX_LAYERS ) );
      }

      private void updateText()
      {
         final int currentLayer = NpcContextEditor.this.npc.getLayerIndex();
         NpcContextEditor.this.layersLabel.setText( Integer.toString( currentLayer + 1 ) );
      }

      @Override
      public void npcChanged( final NpcAdapter updatedNpc )
      {
         updateState();
         updateText();
      }
   }

   private static class ChangeLayerUndoableEdit extends AbstractUndoableEdit
   {
      private final NpcAdapter npc;

      private final int oldLayer;

      private final int newLayer;

      private ChangeLayerUndoableEdit( final NpcAdapter npc, final int oldLayer, final int newLayer )
      {
         this.npc = npc;
         this.oldLayer = oldLayer;
         this.newLayer = newLayer;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         this.npc.setLayerIndex( this.oldLayer );
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         this.npc.setLayerIndex( this.newLayer );
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.sprite.layer" ); //$NON-NLS-1$
      }
   }

   private class DeleteAction extends AbstractUndoableAction
   {
      private DeleteAction()
      {
         super( resources, "uk.co.eduardo.abaddon.interactive.npc.delete" ); //$NON-NLS-1$
      }

      @Override
      public UndoableEdit performAction( final ActionEvent e )
      {
         NpcContextEditor.this.npc.delete();

         // Hide the popup.
         final Container ancestor = SwingUtilities.getAncestorOfClass( PopupWindow.class, NpcContextEditor.this );
         ancestor.setVisible( false );
         return new DeleteNpcUndoableEdit( NpcContextEditor.this.npc );
      }
   }

   private static class DeleteNpcUndoableEdit extends AbstractUndoableEdit
   {
      private final NpcAdapter npc;

      private DeleteNpcUndoableEdit( final NpcAdapter npc )
      {
         this.npc = npc;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         this.npc.undelete();
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         this.npc.delete();
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.sprite.delete" ); //$NON-NLS-1$
      }
   }

   private class ChangeNpcTypeAction extends AbstractUndoableAction
   {
      private final JComboBox< Icon > combo;

      private ChangeNpcTypeAction( final JComboBox< Icon > combo )
      {
         super( resources, "uk.co.eduardo.abaddon.interactive.npc.type" ); //$NON-NLS-1$
         this.combo = combo;
      }

      @Override
      public UndoableEdit performAction( final ActionEvent e )
      {
         final String selectedNpcName = this.combo.getSelectedItem().toString();
         final int oldType = NpcContextEditor.this.npc.getType();
         final int newType = AvailableNpcsModel.getType( selectedNpcName );
         NpcContextEditor.this.npc.setType( newType );
         return new ChangeNpcTypeUndoableEdit( NpcContextEditor.this.npc, oldType, newType );
      }
   }

   private static class ChangeNpcTypeUndoableEdit extends AbstractUndoableEdit
   {
      private final NpcAdapter npc;

      private final int oldType;

      private final int newType;

      private ChangeNpcTypeUndoableEdit( final NpcAdapter npc, final int oldType, final int newType )
      {
         this.npc = npc;
         this.oldType = oldType;
         this.newType = newType;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         this.npc.setType( this.oldType );
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         this.npc.setType( this.newType );
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.sprite.type" ); //$NON-NLS-1$
      }
   }

   private class ChangeDirectionAction extends AbstractUndoableAction implements DirectionSelectorListener
   {
      private Direction oldDirection;

      private Direction newDirection;

      private ChangeDirectionAction()
      {
         super( resources, "" ); //$NON-NLS-1$
      }

      @Override
      public void directionChanged( final Direction oldDir, final Direction newDir )
      {
         // This is a bit nasty but I just want to reuse the automatic registering of an
         // UndoableEdit that AbstractUndoableAction gives me.
         this.oldDirection = oldDir;
         this.newDirection = newDir;
         actionPerformed( null );
      }

      @Override
      protected UndoableEdit performAction( final ActionEvent event )
      {
         if( ( this.oldDirection == null ) || ( this.newDirection == null ) )
         {
            throw new IllegalStateException( "Cannot call actionPerformed before directionChanged" ); //$NON-NLS-1$
         }
         NpcContextEditor.this.npc.setDirection( this.newDirection );
         return new ChangeDirectionUndoableEdit( NpcContextEditor.this.npc, this.oldDirection, this.newDirection );
      }
   }

   private static class ChangeDirectionUndoableEdit extends AbstractUndoableEdit
   {
      private final NpcAdapter npc;

      private final Direction oldDirection;

      private final Direction newDirection;

      private ChangeDirectionUndoableEdit( final NpcAdapter npc, final Direction oldDirection, final Direction newDirection )
      {
         this.npc = npc;
         this.oldDirection = oldDirection;
         this.newDirection = newDirection;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         this.npc.setDirection( this.oldDirection );
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         this.npc.setDirection( this.newDirection );
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.sprite.direction" ); //$NON-NLS-1$
      }
   }

   private class MovementAction extends AbstractUndoableAction
   {
      private final boolean setFixed;

      private MovementAction( final boolean setFixed )
      {
         super( resources, "" ); //$NON-NLS-1$
         this.setFixed = setFixed;
      }

      @Override
      protected UndoableEdit performAction( final ActionEvent event )
      {
         if( NpcContextEditor.this.npc.isFixed() != this.setFixed )
         {
            NpcContextEditor.this.npc.setFixed( this.setFixed );
            return new MovementUndoableEdit( NpcContextEditor.this.npc, this.setFixed );
         }
         return null;
      }
   }

   private static class MovementUndoableEdit extends AbstractUndoableEdit
   {
      private final NpcAdapter npc;

      private final boolean setFixed;

      private MovementUndoableEdit( final NpcAdapter npc, final boolean setFixed )
      {
         this.npc = npc;
         this.setFixed = setFixed;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         this.npc.setFixed( !this.setFixed );
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         this.npc.setFixed( this.setFixed );
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.sprite.mobility" ); //$NON-NLS-1$
      }
   }
}
