package uk.co.eduardo.abaddon.ald.layer.control;

import java.awt.Cursor;
import java.util.Arrays;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.layer.MapLayer;
import uk.co.eduardo.abaddon.ald.ui.Cursors;

/**
 * Selects the tile from the map for painting.
 *
 * @author Ed
 */
public class EyeDropControlLayer extends MapLayer
{

   private final Property< Integer > selectedLayerProperty;

   private final Property< int[] > selectedTilesProperty;

   /**
    * @param model the current model.
    * @param host the host for the layer.
    * @param selectedLayerProperty property for the currently selected layer.
    * @param selectedTilesProperty property for the selected tile IDs.
    */
   public EyeDropControlLayer( final PropertyModel model,
                               final JComponent host,
                               final Property< Integer > selectedLayerProperty,
                               final Property< int[] > selectedTilesProperty )
   {
      super( model, host );
      this.selectedLayerProperty = selectedLayerProperty;
      this.selectedTilesProperty = selectedTilesProperty;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Cursor getCursor()
   {
      return Cursors.EYE_DROP;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void clicked( final int tileX, final int tileY, final int modifiers )
   {
      // We only want to process left-clicks
      if( ( ( modifiers & MIDDLE_CLICK ) != 0 ) || ( ( modifiers & RIGHT_CLICK ) != 0 ) )
      {
         return;
      }
      final int selectedLayer = getModel().get( this.selectedLayerProperty );
      final int tileId = getMapData().getData()[ selectedLayer ][ tileY ][ tileX ];
      if( tileId == -1 )
      {
         return;
      }

      // If it's a control click we want to add to the current selection
      if( ( modifiers & CONTROL ) != 0 )
      {
         final int[] currentSelection = getModel().get( this.selectedTilesProperty );
         if( ( currentSelection == null ) || ( currentSelection.length == 0 ) )
         {
            getModel().set( this.selectedTilesProperty, new int[]
            {
               tileId
            } );
         }
         else
         {
            // Only add it to the selection if it doesn't already exist in the selection.
            Arrays.sort( currentSelection );
            final int indexOf = Arrays.binarySearch( currentSelection, tileId );
            if( indexOf < 0 )
            {
               final int[] newSelection = Arrays.copyOf( currentSelection, currentSelection.length + 1 );
               newSelection[ newSelection.length - 1 ] = tileId;
               getModel().set( this.selectedTilesProperty, newSelection );
            }
         }
      }
      else
      {
         getModel().set( this.selectedTilesProperty, new int[]
         {
            tileId
         } );
      }
   }
}
