package uk.co.eduardo.abaddon.ald.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractItemAction;
import uk.co.eduardo.abaddon.tileset.TileDescription;

/**
 * Compund UI component for a tileset editor.
 *
 * @author Ed
 */
public class TilesetWalkDirectionEditor extends JComponent
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final int CLEARED_TILE = 0;

   private static final int FILLED_TILE = TileDescription.LEFT | TileDescription.RIGHT | TileDescription.TOP |
                                          TileDescription.BOTTOM | TileDescription.TL_BR_DIAG | TileDescription.TR_BL_DIAG;

   private final TileDescription tileset;

   private final TilesetDisplay tilesetUI;

   private final TileEditor editor;

   private final JComponent tileDisplay;

   private final PropertyListener tileListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel model )
      {
         updateTile( model.get( Properties.SelectedTiles )[ 0 ] );
      }
   };

   private int selectedTileId;

   /**
    * @param tilesetDirectory Directory in which the tilesets are saved
    * @param tilesetName the name of the tileset to edit
    * @param settings the current project settings.
    */
   public TilesetWalkDirectionEditor( final File tilesetDirectory, final String tilesetName, final ProjectSettings settings )
   {
      // Read the tileset
      final TilesetData data = new TilesetData( tilesetDirectory, tilesetName, settings );
      this.tileset = data.getTileDescription();

      // Create a model to which the TilesetDisplay UI will be attached.
      final PropertyModel propertyModel = createModel( data, this.selectedTileId );
      propertyModel.addPropertyListener( Properties.SelectedTiles, this.tileListener );
      this.tilesetUI = new TilesetDisplay( propertyModel, Properties.Tileset, Properties.SelectedTiles, false, false, false );

      // Create an editor
      this.editor = new TileEditor( this.tileset.walkable[ this.selectedTileId ] );
      this.tileDisplay = new TileDisplay( data, propertyModel, Properties.SelectedTiles );

      this.editor.addPropertyChangeListener( TileEditor.TILE_EDITOR_TYPE, new PropertyChangeListener()
      {
         @Override
         public void propertyChange( final PropertyChangeEvent evt )
         {
            updateTile( TilesetWalkDirectionEditor.this.selectedTileId );
         }
      } );
      updateTile( this.selectedTileId );
      createUI();
   }

   /**
    * @return the tile description
    */
   public TileDescription getTileDescription()
   {
      return this.tileset;
   }

   private PropertyModel createModel( final TilesetData data, final int initialSelection )
   {
      final PropertyModel model = new PropertyModel();
      model.add( Properties.Tileset, data );
      model.add( Properties.SelectedTiles, new int[]
      {
         initialSelection
      } );
      return model;
   }

   private void updateTile( final int newTileId )
   {
      // Save existing type
      this.tileset.walkable[ this.selectedTileId ] = this.editor.getType();

      // Set the new type
      this.selectedTileId = newTileId;
      this.editor.setType( this.tileset.walkable[ this.selectedTileId ] );
   }

   private void createUI()
   {
      setLayout( new BorderLayout() );
      this.tilesetUI.setBorder( BorderFactory.createLineBorder( Color.black ) );

      // create the mini toolbar above the tile editor
      final JToolBar options = new JToolBar();
      options.setFloatable( false );
      options.add( Box.createHorizontalGlue() );
      options.add( new ClearAction().createToolBarItem() );
      options.add( new FillAction().createToolBarItem() );
      options.add( Box.createHorizontalGlue() );

      final FormLayout layout = new FormLayout( "$ug:grow, fill:p, $ug:grow, fill:p", //$NON-NLS-1$
                                                "bottom:p:grow, fill:p, p:grow" ); //$NON-NLS-1$

      layout.setColumnGroups( new int[][]
      {
         {
            2,
            4
         }
      } );
      final DefaultFormBuilder builder = new DefaultFormBuilder( layout );
      final CellConstraints cc = new CellConstraints();

      builder.add( this.tileDisplay, cc.xy( 2, 2 ) );
      builder.add( options, cc.xy( 4, 1 ) );
      builder.add( this.editor, cc.xy( 4, 2 ) );

      final FormLayout outerLayout = new FormLayout( "p, p:grow", "p:grow" ); //$NON-NLS-1$ //$NON-NLS-2$
      final DefaultFormBuilder outerBuilder = new DefaultFormBuilder( outerLayout );
      outerBuilder.add( this.tilesetUI, cc.xy( 1, 1 ) );
      outerBuilder.add( builder.getPanel(), cc.xy( 2, 1 ) );
      add( outerBuilder.getPanel() );
   }

   private static class TileDisplay extends JComponent
   {
      private final TilesetData data;

      private BufferedImage image;

      private TileDisplay( final TilesetData data, final PropertyModel model, final Property< int[] > selectedTiles )
      {
         this.data = data;
         model.addPropertyListener( selectedTiles, new PropertyListener()
         {
            @Override
            public void propertyChanged( final PropertyModel m )
            {
               selectionChanged( m.get( selectedTiles ) );
            }
         } );
         selectionChanged( model.get( selectedTiles ) );
         setBorder( BorderFactory.createLineBorder( Color.black ) );
      }

      private void selectionChanged( final int[] tiles )
      {
         if( ( tiles != null ) && ( tiles.length > 0 ) )
         {
            this.image = this.data.getTile( tiles[ 0 ] );
            repaint();
         }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected void paintComponent( final Graphics g )
      {
         super.paintComponent( g );
         if( this.image == null )
         {
            return;
         }

         final Graphics2D g2d = (Graphics2D) g;

         // Save state.
         final Object oldHint = g2d.getRenderingHint( RenderingHints.KEY_INTERPOLATION );

         g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );

         g2d.drawImage( this.image, 0, 0, getWidth(), getHeight(), null );

         // restore; state
         if( oldHint != null )
         {
            g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION, oldHint );
         }
      }
   }

   private class FillAction extends AbstractItemAction
   {
      private FillAction()
      {
         super( resources, "uk.co.eduardo.abaddon.action.tileset.fill" ); //$NON-NLS-1$
      }

      @Override
      public void actionPerformed( final ActionEvent e )
      {
         TilesetWalkDirectionEditor.this.editor.setType( FILLED_TILE );
      }
   }

   private class ClearAction extends AbstractItemAction
   {
      private ClearAction()
      {
         super( resources, "uk.co.eduardo.abaddon.action.tileset.clear" ); //$NON-NLS-1$
      }

      @Override
      public void actionPerformed( final ActionEvent e )
      {
         TilesetWalkDirectionEditor.this.editor.setType( CLEARED_TILE );
      }
   }
}
