package uk.co.eduardo.abaddon.ald.layer;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManager;
import uk.co.eduardo.abaddon.ald.data.utils.TileConversionUtilities;
import uk.co.eduardo.abaddon.ald.layer.interaction.InteractiveElement;
import uk.co.eduardo.abaddon.ald.layer.interaction.InteractiveElementProvider;
import uk.co.eduardo.abaddon.ald.ui.PopupWindow;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Allows the user to edit map elements.
 *
 * @author Ed
 */
public class InteractiveElementsEditorLayer extends AbstractDraggingLayer
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final List< InteractiveElementProvider > providers = new ArrayList<>();

   private final Property< Coordinate > mouseTileProperty;

   private final Property< UndoManager > undoManagerProperty;

   private final Set< InteractiveElement > selected = new HashSet<>();

   private final Set< InteractiveElement > hover = new HashSet<>();

   private boolean draggingElements;

   private boolean draggingBoundingBox;

   private Coordinate lastDragLocation;

   private int maxInteractiveElementPixelHeight;

   private CompoundEdit dragEdit;

   /**
    * @param model the current model
    * @param host the host for the layer.
    * @param tilesetProperty property for the currently selected tileset.
    * @param mouseTileProperty property for the current mouse position.
    * @param undoManagerProperty property for the current undo manager.
    */
   public InteractiveElementsEditorLayer( final PropertyModel model,
                                          final JComponent host,
                                          final Property< TilesetData > tilesetProperty,
                                          final Property< Coordinate > mouseTileProperty,
                                          final Property< UndoManager > undoManagerProperty )
   {
      super( model, host, tilesetProperty, true, true );
      this.mouseTileProperty = mouseTileProperty;
      this.undoManagerProperty = undoManagerProperty;

      model.addPropertyListener( mouseTileProperty, new PropertyListener()
      {
         @Override
         public void propertyChanged( final PropertyModel s )
         {
            update();
         }
      } );

      for( final InteractiveElementProvider provider : ServiceLoader.load( InteractiveElementProvider.class ) )
      {
         this.providers.add( provider );
         provider.setModel( model );
      }

      update();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void paint( final Graphics2D g2d )
   {
      if( this.draggingBoundingBox )
      {
         super.paint( g2d );
      }
      if( ( this.selected.size() == 0 ) && ( this.hover.size() == 0 ) )
      {
         return;
      }
      final Set< InteractiveElement > toPaint = new HashSet<>( this.selected );
      toPaint.addAll( this.hover );

      for( final InteractiveElement interaction : toPaint )
      {
         drawSprite( g2d, interaction.getInteractingImage(), interaction.getPosition().x, interaction.getPosition().y );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void clicked( final int tileX, final int tileY, final int modifiers )
   {
      final Coordinate clickPoint = new Coordinate( tileX, tileY );
      // If you right-click, or double click an item then display
      // contextual information
      final boolean isRightClick = ( modifiers & RIGHT_CLICK ) != 0;
      final boolean isDoubleClick = ( modifiers & DOUBLE_CLICK ) != 0;
      if( isRightClick || isDoubleClick )
      {
         showContext( clickPoint );
      }
      else
      {
         if( this.hover.size() > 0 )
         {
            if( ( modifiers & CONTROL ) == 0 )
            {
               clearSelected();
            }
            for( final InteractiveElement element : this.hover )
            {
               addToSelected( element );
            }
            getHost().repaint();
         }
         else
         {
            clearSelected();
            getHost().repaint();
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void dragStart( final int tileX, final int tileY, final int modifiers )
   {
      this.lastDragLocation = new Coordinate( tileX, tileY );
      if( this.hover.size() > 0 )
      {
         final boolean notPressingControl = ( modifiers & CONTROL ) == 0;
         boolean selectingNew = true;
         for( final InteractiveElement element : this.hover )
         {
            if( this.selected.contains( element ) )
            {
               selectingNew = false;
               break;
            }
         }
         if( notPressingControl && selectingNew )
         {
            clearSelected();
         }
         for( final InteractiveElement element : this.hover )
         {
            addToSelected( element );
         }
         clearHover();
         this.draggingElements = true;
         this.draggingBoundingBox = false;

         // Create a new edit.
         this.dragEdit = new CompoundEdit();
      }
      else
      {
         this.draggingElements = false;
         this.draggingBoundingBox = true;
         removeOverlay();
      }
      if( this.draggingBoundingBox )
      {
         super.dragStart( tileX, tileY, modifiers );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void dragEnd( final int tileX, final int tileY, final int modifiers )
   {
      if( this.draggingBoundingBox )
      {
         super.dragEnd( tileX, tileY, modifiers );
      }
      this.draggingBoundingBox = false;
      this.draggingElements = false;

      if( this.dragEdit != null )
      {
         this.dragEdit.end();

         // Add edit to UndoManager
         addEdit( this.dragEdit );
         this.dragEdit = null;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void drag( final int tileX, final int tileY, final int modifiers )
   {
      if( this.draggingBoundingBox )
      {
         super.drag( tileX, tileY, modifiers );
      }
      else
      {
         // Calculate the delta in position.
         final int dx = tileX - this.lastDragLocation.x;
         final int dy = tileY - this.lastDragLocation.y;
         this.lastDragLocation = new Coordinate( tileX, tileY );

         for( final InteractiveElement interaction : this.selected )
         {
            final Coordinate position = interaction.getPosition();
            interaction.setPosition( new Coordinate( position.x + dx, position.y + dy ) );
         }
         // Create the undoable edit for this move.
         final UndoableEdit currentEdit = new MoveEdit( this.selected, dx, dy );
         this.dragEdit.addEdit( currentEdit );

         if( this.selected.size() > 0 )
         {
            getHost().repaint();
         }
      }
   }

   /**
    * Notification that dragging a rectangular section has completed.
    *
    * {@inheritDoc}
    */
   @Override
   protected void dragComplete( final int tileX, final int tileY, final int width, final int height, final int modifiers )
   {
      selectElements( tileX, tileY, width, height );
   }

   /**
    * Notification that the rectangular dragging area has been updated.
    *
    * {@inheritDoc}
    */
   @Override
   protected void dragUpdate( final int tileX, final int tileY, final int width, final int height, final int modifiers )
   {
      selectElements( tileX, tileY, width, height );
   }

   private void drawSprite( final Graphics2D g2d, final BufferedImage image, final int tileX, final int tileY )
   {
      final int heightDifference = image.getHeight() - getTilesetData().getTileHeight();
      final int offset = heightDifference + ( heightDifference / 2 );

      final int x = tileX * getTilesetData().getTileWidth();
      final int y = ( tileY * getTilesetData().getTileHeight() ) - offset;
      g2d.drawImage( image, x, y, null );
   }

   private void update()
   {
      // Only if we're not dragging already
      if( this.draggingElements || this.draggingBoundingBox )
      {
         return;
      }
      final Coordinate position = getModel().get( this.mouseTileProperty );
      if( ( position != null ) && !position.equals( new Coordinate( -1, -1 ) ) )
      {
         // Check to see if there is an NPC at this location.
         boolean found = false;
         for( final InteractiveElementProvider provider : this.providers )
         {
            final InteractiveElement[] elements = provider.getElements( position );
            if( ( elements != null ) && ( elements.length > 0 ) )
            {
               for( final InteractiveElement element : elements )
               {
                  addToHover( element );
                  break;
               }
               final List< Coordinate > updatePoints = new ArrayList<>();
               updatePoints.add( position );
               for( final InteractiveElement interaction : this.selected )
               {
                  updatePoints.add( interaction.getPosition() );
               }
               // Make the sprite glow.
               found = true;
               getHost().repaint( getDirtyRegion( getTilesetData(), updatePoints.toArray( new Coordinate[ 0 ] ) ) );
               break;
            }
         }
         if( !found )
         {
            removeOverlay();
         }
      }
      else
      {
         removeOverlay();
      }
   }

   private void removeOverlay()
   {
      if( this.draggingElements || this.draggingBoundingBox )
      {
         return;
      }
      if( ( this.selected.size() > 0 ) || ( this.hover.size() > 0 ) )
      {
         final List< Coordinate > updatePoints = new ArrayList<>();
         for( final InteractiveElement element : this.hover )
         {
            updatePoints.add( element.getPosition() );
         }
         for( final InteractiveElement element : this.selected )
         {
            updatePoints.add( element.getPosition() );
         }

         clearHover();
         getHost().repaint( getDirtyRegion( getTilesetData(), updatePoints.toArray( new Coordinate[ 0 ] ) ) );
      }
   }

   private void selectElements( final int x, final int y, final int width, final int height )
   {
      clearSelected();
      for( final InteractiveElementProvider provider : this.providers )
      {
         for( final InteractiveElement element : provider.getElements( new Coordinate( x, y ), width, height ) )
         {
            addToSelected( element );
            this.selected.add( element );
         }
      }
   }

   private int getElementHeightInTiles( final int elementPixelHeight )
   {
      if( getTilesetData() == null )
      {
         return 0;
      }
      return (int) ( Math.ceil( elementPixelHeight / (float) getTilesetData().getTileHeight() ) );
   }

   private Rectangle getDirtyRegion( final TilesetData tilesetData, final Coordinate... tiles )
   {
      final Rectangle rect = TileConversionUtilities.pixelUnion( tilesetData, tiles );

      // Unfortunately the interactive elements can be more than one tile high each.
      // We calculate how many extra tiles high they are and make sure the region
      // to invalidate is large enough to accommodate the entire element.
      final int spriteTileHeight = getElementHeightInTiles( this.maxInteractiveElementPixelHeight );
      final int excessTiles = spriteTileHeight - 1;
      final int excessPixels = excessTiles * getTilesetData().getTileHeight();
      rect.setLocation( rect.x, rect.y - excessPixels );
      rect.setSize( rect.width, rect.height + excessPixels );
      return rect;
   }

   private void recalculateMaxHeight()
   {
      int maxHeight = 0;
      for( final InteractiveElement element : this.selected )
      {
         maxHeight = Math.max( maxHeight, element.getInteractingImage().getHeight() );
      }
      for( final InteractiveElement element : this.hover )
      {
         maxHeight = Math.max( maxHeight, element.getInteractingImage().getHeight() );
      }
      this.maxInteractiveElementPixelHeight = maxHeight;
   }

   private void clearSelected()
   {
      this.selected.clear();
   }

   private void addToSelected( final InteractiveElement element )
   {
      this.selected.add( element );
      recalculateMaxHeight();
   }

   private void addToHover( final InteractiveElement element )
   {
      this.hover.add( element );
      recalculateMaxHeight();
   }

   private void clearHover()
   {
      this.hover.clear();
   }

   private void showContext( final Coordinate clickPoint )
   {
      // We don't want to display the context right on top of the item
      final Coordinate offset = new Coordinate( clickPoint.x + 1, clickPoint.y - 1 );
      final Point displayPoint = TileConversionUtilities.convertToPixel( getTilesetData(), offset );

      // There may be more than one interactive element at this point. We build a popup menu for
      // each of them.
      final List< Action > elementActions = new ArrayList<>();
      for( final InteractiveElement element : this.hover )
      {
         // This next check is redundant, really. If they're in the hover set it's because
         // they're directly under the current mouse position (which is also the click position).
         if( element.getPosition().equals( clickPoint ) )
         {
            // Create a new panel for the contents
            final JPanel panel = new JPanel( new BorderLayout() );
            // Add a heading.
            panel.add( new JLabel( element.toString() ), BorderLayout.NORTH );

            final JPanel customPanel = new JPanel();
            panel.add( customPanel, BorderLayout.CENTER );
            element.populateContextMenu( customPanel );

            final PopupWindow popup = new PopupWindow( SwingUtilities.getWindowAncestor( getHost() ) );
            popup.setTitle( "Context popup" ); //$NON-NLS-1$
            popup.getContentPane().add( panel );

            final Action action = new AbstractAction( element.toString() )
            {
               @Override
               public void actionPerformed( final ActionEvent e )
               {
                  clearSelected();
                  clearHover();
                  getHost().repaint();
                  popup.show( getHost(), displayPoint.x, displayPoint.y );
               }
            };
            elementActions.add( action );
         }
      }
      // If there's only one of them we can just show it directly.
      if( elementActions.size() == 1 )
      {
         elementActions.get( 0 ).actionPerformed( null );
      }
      else if( elementActions.size() > 1 )
      {
         // Otherwise we construct a separate popup menu for the to select the action first.
         final PopupWindow popup = new PopupWindow( SwingUtilities.getWindowAncestor( getHost() ) );
         for( final Action action : elementActions )
         {
            popup.add( action );
         }
         popup.show( getHost(), displayPoint.x, displayPoint.y );
      }
   }

   private void addEdit( final UndoableEdit edit )
   {
      final UndoManager undoManager = getModel().get( this.undoManagerProperty );
      if( undoManager != null )
      {
         undoManager.addEdit( edit );
      }
   }

   private static final class MoveEdit extends AbstractUndoableEdit
   {
      private final List< InteractiveElement > elements;

      private final int dx;

      private final int dy;

      private MoveEdit( final Collection< InteractiveElement > elements, final int dx, final int dy )
      {
         this.elements = new ArrayList<>( elements );
         this.dx = dx;
         this.dy = dy;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         for( final InteractiveElement element : this.elements )
         {
            element.setPosition( new Coordinate( element.getPosition().x - this.dx, element.getPosition().y - this.dy ) );
         }
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         for( final InteractiveElement element : this.elements )
         {
            element.setPosition( new Coordinate( element.getPosition().x + this.dx, element.getPosition().y + this.dy ) );
         }
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.element.move" ); //$NON-NLS-1$
      }
   }
}
