package uk.co.eduardo.abaddon.ald.layer.control;

import java.awt.Cursor;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManager;
import uk.co.eduardo.abaddon.ald.layer.AbstractDraggingLayer;
import uk.co.eduardo.abaddon.ald.ui.Cursors;

/**
 * Allows the user to draw on the underlying map.
 *
 * @author Ed
 */
public class PencilControlLayer extends AbstractDraggingLayer
{
   private final Random rng = new Random();

   private final Property< int[] > selectedTilesProperty;

   private final Property< Integer > selectedLayerProperty;

   private final UndoManager undoManager;

   private CompoundEdit compoundEdit;

   /**
    * @param model the current model.
    * @param host the host for the layer.
    * @param tilesetProperty property for the currently selected tileset.
    * @param selectedLayerProperty property for the currently selected layer.
    * @param selectedTilesProperty property for the selected tile IDs.
    * @param undoManagerProperty property for the undo manager while this map is locked.
    */
   public PencilControlLayer( final PropertyModel model,
                              final JComponent host,
                              final Property< TilesetData > tilesetProperty,
                              final Property< Integer > selectedLayerProperty,
                              final Property< int[] > selectedTilesProperty,
                              final Property< UndoManager > undoManagerProperty )
   {
      super( model, host, tilesetProperty, false, true );
      this.selectedLayerProperty = selectedLayerProperty;
      this.selectedTilesProperty = selectedTilesProperty;
      this.undoManager = model.get( undoManagerProperty );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Cursor getCursor()
   {
      return Cursors.PENCIL;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void clicked( final int tileX, final int tileY, final int modifiers )
   {
      final UndoableEdit edit = setTile( tileX, tileY, modifiers );
      if( edit != null )
      {
         addEdit( edit );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void dragStart( final int tileX, final int tileY, final int modifiers )
   {
      this.compoundEdit = new CompoundEdit();
      super.dragStart( tileX, tileY, modifiers );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void dragUpdate( final int tileX, final int tileY, final int width, final int height, final int modifiers )
   {
      if( this.compoundEdit != null )
      {
         final UndoableEdit edit = setTile( tileX, tileY, modifiers );
         if( edit != null )
         {
            this.compoundEdit.addEdit( edit );
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void dragComplete( final int tileX, final int tileY, final int width, final int height, final int modifiers )
   {
      if( this.compoundEdit != null )
      {
         this.compoundEdit.end();
         addEdit( this.compoundEdit );
         this.compoundEdit = null;
      }
   }

   private UndoableEdit setTile( final int tileX, final int tileY, final int modifiers )
   {
      // We only want to process left-clicks
      if( ( modifiers & MIDDLE_CLICK ) != 0 )
      {
         return null;
      }
      final int selectedLayer = getModel().get( this.selectedLayerProperty );
      final int tile;
      if( ( ( modifiers & RIGHT_CLICK ) != 0 ) && ( selectedLayer > 0 ) )
      {
         // Delete tile
         tile = -1;
      }
      else
      {
         final int[] tiles = getModel().get( this.selectedTilesProperty );
         if( ( tiles == null ) || ( tiles.length == 0 ) )
         {
            return null;
         }
         tile = tiles[ this.rng.nextInt( tiles.length ) ];
      }
      return getMapData().setData( selectedLayer, tileY, tileX, tile );
   }

   private void addEdit( final UndoableEdit edit )
   {
      this.undoManager.addEdit( edit );
   }
}
