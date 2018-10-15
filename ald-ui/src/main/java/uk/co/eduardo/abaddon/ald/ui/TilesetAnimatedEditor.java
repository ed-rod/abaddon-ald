package uk.co.eduardo.abaddon.ald.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.tileset.TileDescription;

/**
 * UI Component for selecting the animated tiles.
 *
 * @author Ed
 */
public class TilesetAnimatedEditor extends JComponent
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final TilesetDisplay tilesetUI;

   private final PropertyListener tileListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel model )
      {
         TilesetAnimatedEditor.this.animated = model.get( Properties.SelectedTiles );
      }
   };

   private final int[] walkable;

   private int[] animated;

   /**
    * @param tilesetDirectory Directory in which the tilesets are saved
    * @param tilesetName the name of the tileset to edit
    * @param settings the current project settings.
    */
   public TilesetAnimatedEditor( final File tilesetDirectory, final String tilesetName, final ProjectSettings settings )
   {
      // Read the tileset
      final TilesetData data = new TilesetData( tilesetDirectory, tilesetName, settings );
      final PropertyModel propertyModel = createModel( data );

      propertyModel.addPropertyListener( Properties.SelectedTiles, this.tileListener );
      this.tilesetUI = new TilesetDisplay( propertyModel, Properties.Tileset, Properties.SelectedTiles, true, true, true );

      this.walkable = data.getTileDescription().walkable;
      this.animated = data.getTileDescription().animated;

      createUI();
   }

   /**
    * @return the tile description
    */
   public TileDescription getTileDescription()
   {
      return new TileDescription( this.walkable, this.animated );
   }

   private PropertyModel createModel( final TilesetData data )
   {
      final PropertyModel model = new PropertyModel();
      model.add( Properties.Tileset, data );
      model.add( Properties.SelectedTiles, data.getTileDescription().animated );
      return model;
   }

   private void createUI()
   {
      setLayout( new BorderLayout() );
      this.tilesetUI.setBorder( BorderFactory.createLineBorder( Color.black ) );

      final JLabel hintLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.tileset.editor.dialog.animated.hint" ) ); //$NON-NLS-1$
      final FormLayout layout = new FormLayout( "p, $ug, 100dlu:grow" ); //$NON-NLS-1$
      final DefaultFormBuilder builder = new DefaultFormBuilder( layout );

      builder.append( this.tilesetUI );
      builder.append( hintLabel );

      add( builder.getPanel() );
   }
}
