package uk.co.eduardo.abaddon.ald.layer.control;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import uk.co.eduardo.abaddon.ald.data.NpcData;
import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.adapter.NpcAdapter;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManager;
import uk.co.eduardo.abaddon.ald.data.utils.TileConversionUtilities;
import uk.co.eduardo.abaddon.ald.layer.AbstractTilesetAwareLayer;
import uk.co.eduardo.abaddon.ald.layer.interaction.InteractiveElement;
import uk.co.eduardo.abaddon.ald.layer.interaction.NpcInteractiveElement;
import uk.co.eduardo.abaddon.ald.ui.Cursors;
import uk.co.eduardo.abaddon.ald.ui.PopupWindow;
import uk.co.eduardo.abaddon.graphics.layer.Direction;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Control layer to drop/edit a sprite.
 *
 * @author Ed
 */
public class SpriteCreationControlLayer extends AbstractTilesetAwareLayer
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final NpcData npcData;

   private final Property< UndoManager > undoManagerProperty;

   private final ProjectSettings settings;

   /**
    * @param model the current model.
    * @param host the host for the layer.
    * @param tilesetProperty property for the currently selected tileset.
    * @param npcDataProperty property for the NPC data.
    * @param undoManagerProperty property for the undo manager while this model is locked.
    * @param settingsProperty property for the current project settings.
    */
   public SpriteCreationControlLayer( final PropertyModel model,
                                      final JComponent host,
                                      final Property< TilesetData > tilesetProperty,
                                      final Property< NpcData > npcDataProperty,
                                      final Property< UndoManager > undoManagerProperty,
                                      final Property< ProjectSettings > settingsProperty )
   {
      super( model, host, tilesetProperty );
      this.npcData = model.get( npcDataProperty );
      this.undoManagerProperty = undoManagerProperty;
      this.settings = model.get( settingsProperty );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Cursor getCursor()
   {
      return Cursors.SPRITE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void clicked( final int tileX, final int tileY, final int modifiers )
   {
      // Create a sprite on that tile.
      final Coordinate position = new Coordinate( tileX, tileY );
      final NpcAdapter newNpc = new NpcAdapter( this.npcData, 0, position, Direction.DOWN, 0, false );
      this.npcData.addNpc( newNpc );
      final UndoManager undoManager = getUndoManager();
      {
         if( undoManager != null )
         {
            undoManager.addEdit( new CreateSpriteUndoableEdit( newNpc ) );
         }
      }
      getHost().repaint();

      // We don't want to display the context right on top of the item
      final Coordinate offset = new Coordinate( position.x + 1, position.y - 1 );
      final Point displayPoint = TileConversionUtilities.convertToPixel( getTilesetData(), offset );

      // Open the editor right next to the dropped sprite.
      final InteractiveElement npcElement = new NpcInteractiveElement( newNpc, this.settings );
      // Create a new panel for the contents
      final JPanel panel = new JPanel( new BorderLayout() );
      // Add a heading.
      panel.add( new JLabel( npcElement.toString() ), BorderLayout.NORTH );

      final JPanel customPanel = new JPanel();
      panel.add( customPanel, BorderLayout.CENTER );
      npcElement.populateContextMenu( customPanel );

      final PopupWindow popup = new PopupWindow( SwingUtilities.getWindowAncestor( getHost() ) );
      popup.setTitle( "Context popup" ); //$NON-NLS-1$
      popup.getContentPane().add( panel );
      popup.show( getHost(), displayPoint.x, displayPoint.y );
   }

   /**
    * @return the current Undo Manager.
    */
   protected UndoManager getUndoManager()
   {
      return getModel().get( this.undoManagerProperty );
   }

   private static final class CreateSpriteUndoableEdit extends AbstractUndoableEdit
   {
      private final NpcAdapter npc;

      private CreateSpriteUndoableEdit( final NpcAdapter npc )
      {
         this.npc = npc;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         this.npc.delete();
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         this.npc.undelete();
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.sprite.create" ); //$NON-NLS-1$
      }
   }
}
