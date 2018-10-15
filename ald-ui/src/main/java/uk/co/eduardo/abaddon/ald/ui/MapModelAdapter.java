package uk.co.eduardo.abaddon.ald.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import uk.co.eduardo.abaddon.ald.data.ActionData;
import uk.co.eduardo.abaddon.ald.data.HeaderData;
import uk.co.eduardo.abaddon.ald.data.MapData;
import uk.co.eduardo.abaddon.ald.data.MonsterData;
import uk.co.eduardo.abaddon.ald.data.NpcData;
import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManager;
import uk.co.eduardo.abaddon.map.MapDefinition;
import uk.co.eduardo.abaddon.map.MapFactory;
import uk.co.eduardo.map.sections.ActionSection;
import uk.co.eduardo.map.sections.HeaderSection;
import uk.co.eduardo.map.sections.MapSection;
import uk.co.eduardo.map.sections.MonsterSection;
import uk.co.eduardo.map.sections.NpcSection;

/**
 * Creates and installs a new map model from a Map file.
 *
 * @author Ed
 */
public class MapModelAdapter
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final Project project;

   private final File mapFile;

   /**
    * @param project the project to which this map belongs.
    * @param mapFile the the map to open.
    */
   public MapModelAdapter( final Project project, final File mapFile )
   {
      this.project = project;
      if( mapFile == null )
      {
         throw new NullPointerException( "mapFile cannot be null" ); //$NON-NLS-1$
      }
      this.mapFile = mapFile;
   }

   /**
    * Initialize and install a new map model.
    *
    * @return a map model populated with data read from a map file.
    */
   public PropertyModel initialize()
   {
      final PropertyModel newModel = new PropertyModel();

      try
      {
         ActionSection actionSection = null;
         HeaderSection headerSection = null;
         MapSection mapSection = null;
         MonsterSection monsterSection = null;
         NpcSection npcSection = null;
         MapDefinition mapDefinition = null;
         InputStream stream = null;
         try
         {
            stream = new BufferedInputStream( new FileInputStream( this.mapFile ) );
            mapDefinition = MapFactory.readMap( stream );
            actionSection = mapDefinition.actionsSection;
            headerSection = mapDefinition.headerSection;
            mapSection = mapDefinition.mapsSection;
            monsterSection = mapDefinition.monstersSection;
            npcSection = mapDefinition.npcsSection;
         }
         catch( final IOException exception )
         {
            final String messageFormat = resources.getString( "uk.co.eduardo.abaddon.map.load.error" ); //$NON-NLS-1$
            final String message = MessageFormat.format( messageFormat, new Object[]
            {
               this.mapFile
            } );
            showError( message );
         }
         catch( final Throwable t )
         {
            t.printStackTrace();
         }
         finally
         {
            if( stream != null )
            {
               stream.close();
            }
         }
         final ActionData actionData = new ActionData( actionSection );
         final HeaderData headerData = new HeaderData( headerSection );
         final MapData mapData = new MapData( mapSection );
         final MonsterData monsterData = new MonsterData( monsterSection );
         final NpcData npcData = new NpcData( npcSection );
         final TilesetData tilesetData = new TilesetData( this.project.getAvailableTilesetsModel().getDirectory(),
                                                          headerData.getTilesetName(),
                                                          this.project.getSettings() );

         newModel.add( Properties.MapName, this.mapFile.getName() );
         newModel.add( Properties.ProjectSettings, this.project.getSettings() );
         newModel.add( Properties.ActionData, actionData );
         newModel.add( Properties.HeaderData, headerData );
         newModel.add( Properties.MapData, mapData );
         newModel.add( Properties.MonsterData, monsterData );
         newModel.add( Properties.NpcData, npcData );
         newModel.add( Properties.Tileset, tilesetData );
         newModel.add( Properties.SaveLocation, this.mapFile );
         newModel.add( Properties.UncommittedChanges, false );
         newModel.add( Properties.LayerCount, MapData.MAX_LAYERS );
         newModel.add( Properties.ActiveLayer, 0 );
         newModel.add( Properties.SpriteVisible, true );
         newModel.add( Properties.MouseTile, null );
         newModel.add( Properties.SelectedControl, null );
         newModel.add( Properties.SelectedTiles, new int[]
         {
            0
         } );
         newModel.add( Properties.UndoManager, new UndoManager() );

         // Now add the per-layer properties
         for( int layer = 0; layer < MapData.MAX_LAYERS; layer++ )
         {
            newModel.add( Property.getLayerProperty( layer, Properties.LayerVisible ), true );
         }

         return newModel;
      }
      catch( final Exception e )
      {
         final String messageFormat = resources.getString( "uk.co.eduardo.abaddon.map.load.error" ); //$NON-NLS-1$
         final String message = MessageFormat.format( messageFormat, new Object[]
         {
            this.mapFile
         } );
         showError( message );
      }
      return null;
   }

   private void showError( final String message )
   {
      if( !SwingUtilities.isEventDispatchThread() )
      {
         SwingUtilities.invokeLater( new Runnable()
         {
            @Override
            public void run()
            {
               showError( message );
            }
         } );
         return;
      }
      final String title = resources.getString( "uk.co.eduardo.abaddon.title" ); //$NON-NLS-1$
      JOptionPane.showMessageDialog( null, message, title, JOptionPane.INFORMATION_MESSAGE );
   }
}
