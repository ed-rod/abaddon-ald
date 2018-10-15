package uk.co.eduardo.abaddon.ald.layer.interaction;

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

import uk.co.eduardo.abaddon.ald.data.HeaderData;
import uk.co.eduardo.abaddon.ald.data.HeaderData.HeaderDataListener;
import uk.co.eduardo.abaddon.ald.data.MapData;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.ald.ui.DirectionSelector;
import uk.co.eduardo.abaddon.ald.ui.DirectionSelectorListener;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractUndoableAction;
import uk.co.eduardo.abaddon.graphics.layer.Direction;

/**
 * Context editor for the hero character
 *
 * @author Ed
 */
public class HeroContextEditor extends JPanel
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final JLabel layersLabel = new JLabel();

   private final ChangeLayerAction raiseHeroAction;

   private final ChangeLayerAction lowerHeroAction;

   private final HeaderData header;

   private final ProjectSettings settings;

   /**
    * nitializes a component that allows quick editing of the hero.
    *
    * @param header the header data
    * @param settings the current project settings.
    */
   public HeroContextEditor( final HeaderData header, final ProjectSettings settings )
   {
      this.header = header;
      this.settings = settings;
      this.raiseHeroAction = new ChangeLayerAction( 1 );
      this.lowerHeroAction = new ChangeLayerAction( -1 );
      createUI();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      super.addNotify();
      this.header.addHeaderDataListener( this.raiseHeroAction );
      this.header.addHeaderDataListener( this.lowerHeroAction );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      this.header.removeHeaderDataListener( this.raiseHeroAction );
      this.header.removeHeaderDataListener( this.lowerHeroAction );
      super.removeNotify();
   }

   private void createUI()
   {
      final FormLayout layout = new FormLayout( "p, $rg, fill:p", "fill:p" ); //$NON-NLS-1$ //$NON-NLS-2$
      final DefaultFormBuilder builder = new DefaultFormBuilder( layout );

      addSetDirectionUI( builder );
      addLayerUI( builder );

      this.add( builder.getPanel() );
   }

   private void addSetDirectionUI( final DefaultFormBuilder builder )
   {
      final JLabel directionLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.interactive.npc.direction.label" ) ); //$NON-NLS-1$
      final DirectionSelector directionSelector = new DirectionSelector( this.header.getDirection(), this.settings, true );
      directionSelector.addDirectionSelectorListener( new ChangeDirectionAction() );

      builder.append( directionLabel );
      builder.append( directionSelector );
   }

   private void addLayerUI( final DefaultFormBuilder builder )
   {
      final JLabel layerLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.interactive.npc.change.layer.label" ) ); //$NON-NLS-1$
      final JToolBar layerToolbar = new JToolBar();
      layerToolbar.setFloatable( false );
      layerToolbar.add( this.raiseHeroAction.createToolBarItem() );
      layerToolbar.add( this.lowerHeroAction.createToolBarItem() );
      layerToolbar.add( this.layersLabel );
      builder.append( layerLabel );
      builder.append( layerToolbar );
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
         HeroContextEditor.this.header.setDirection( this.newDirection );
         return new ChangeDirectionUndoableEdit( HeroContextEditor.this.header, this.oldDirection, this.newDirection );
      }
   }

   private static class ChangeDirectionUndoableEdit extends AbstractUndoableEdit
   {
      private final HeaderData header;

      private final Direction oldDirection;

      private final Direction newDirection;

      private ChangeDirectionUndoableEdit( final HeaderData header, final Direction oldDirection, final Direction newDirection )
      {
         this.header = header;
         this.oldDirection = oldDirection;
         this.newDirection = newDirection;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         this.header.setDirection( this.oldDirection );
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         this.header.setDirection( this.newDirection );
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.sprite.direction" ); //$NON-NLS-1$
      }
   }

   private class ChangeLayerAction extends AbstractUndoableAction implements HeaderDataListener
   {
      private final int delta;

      private ChangeLayerAction( final int delta )
      {
         super( resources, "uk.co.eduardo.abaddon.interactive.npc.change.layer" + delta ); //$NON-NLS-1$
         this.delta = delta;
         headerChanged();
      }

      @Override
      public UndoableEdit performAction( final ActionEvent e )
      {
         final int oldLayerIndex = HeroContextEditor.this.header.getLayerIndex();
         final int newLayerIndex = oldLayerIndex + this.delta;
         HeroContextEditor.this.header.setLayerIndex( newLayerIndex );
         headerChanged();
         return new ChangeLayerUndoableEdit( HeroContextEditor.this.header, oldLayerIndex, newLayerIndex );
      }

      @Override
      public void headerChanged()
      {
         updateState();
         updateText();
      }

      private void updateState()
      {
         final int newIndex = HeroContextEditor.this.header.getLayerIndex() + this.delta;
         setEnabled( ( newIndex >= 0 ) && ( newIndex < MapData.MAX_LAYERS ) );
      }

      private void updateText()
      {
         final int currentLayer = HeroContextEditor.this.header.getLayerIndex();
         HeroContextEditor.this.layersLabel.setText( Integer.toString( currentLayer + 1 ) );
      }
   }

   private static class ChangeLayerUndoableEdit extends AbstractUndoableEdit
   {
      private final HeaderData header;

      private final int oldLayer;

      private final int newLayer;

      private ChangeLayerUndoableEdit( final HeaderData header, final int oldLayer, final int newLayer )
      {
         this.header = header;
         this.oldLayer = oldLayer;
         this.newLayer = newLayer;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         this.header.setLayerIndex( this.oldLayer );
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         this.header.setLayerIndex( this.newLayer );
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.sprite.layer" ); //$NON-NLS-1$
      }
   }
}
