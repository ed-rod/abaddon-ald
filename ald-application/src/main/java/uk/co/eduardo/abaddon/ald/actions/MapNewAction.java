package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import uk.co.eduardo.abaddon.ald.data.MapData;
import uk.co.eduardo.abaddon.ald.data.project.AvailableFileListener;
import uk.co.eduardo.abaddon.ald.data.project.AvailableTilesetsModel;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.ui.InputDialog;
import uk.co.eduardo.abaddon.ald.ui.WindowAncestorUtilities;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractProjectAction;
import uk.co.eduardo.abaddon.ald.utils.OpenUtilities;
import uk.co.eduardo.abaddon.graphics.layer.Direction;
import uk.co.eduardo.abaddon.graphics.layer.NPC;
import uk.co.eduardo.abaddon.map.MapDefinition;
import uk.co.eduardo.abaddon.map.MapFactory;
import uk.co.eduardo.abaddon.map.actions.MapAction;
import uk.co.eduardo.abaddon.monsters.MonsterZone;
import uk.co.eduardo.abaddon.util.Coordinate;
import uk.co.eduardo.map.sections.ActionSection;
import uk.co.eduardo.map.sections.HeaderSection;
import uk.co.eduardo.map.sections.MapSection;
import uk.co.eduardo.map.sections.MonsterSection;
import uk.co.eduardo.map.sections.NpcSection;

/**
 * Creates a new map.
 *
 * @author Ed
 */
public class MapNewAction extends AbstractProjectAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String NO_TILESET_MESSAGE = resources.getString( "uk.co.eduardo.abaddon.new.map.tileset.message" ); //$NON-NLS-1$

   private static final String NO_TILESET_TITLE = resources.getString( "uk.co.eduardo.abaddon.title" ); //$NON-NLS-1$

   private static final String MESSAGE = resources.getString( "uk.co.eduardo.abaddon.file.input.message" ); //$NON-NLS-1$

   private static final String PREFIX = resources.getString( "uk.co.eduardo.abaddon.file.input.prefix" ); //$NON-NLS-1$

   private static final String MAP_EXTENSION = ".map"; //$NON-NLS-1$

   /**
    * Initializes an action that will
    */
   public MapNewAction()
   {
      super( resources, "uk.co.eduardo.abaddon.action.map.new" ); //$NON-NLS-1$
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent event )
   {
      // If there are no tilesets defined then we cannot generate a map
      if( getProject().getAvailableTilesetsModel().getAvailableDscTilesetFiles().size() == 0 )
      {
         JOptionPane.showMessageDialog( WindowAncestorUtilities.getWindow( event ),
                                        NO_TILESET_MESSAGE,
                                        NO_TILESET_TITLE,
                                        JOptionPane.INFORMATION_MESSAGE );
         return;
      }
      // First get the list of taken map names
      final List< String > names = new ArrayList<>();
      for( final File file : getProject().getAvailableMapsModel().getAvailableMapFiles() )
      {
         names.add( file.getName() );
      }
      final String name = InputDialog.showFileInputDialog( WindowAncestorUtilities.getWindow( event ),
                                                           MESSAGE,
                                                           PREFIX,
                                                           names,
                                                           MAP_EXTENSION );
      if( name != null )
      {
         String fileName = name;
         // Check for the presence of the extension
         if( !name.toLowerCase().endsWith( MAP_EXTENSION ) )
         {
            fileName = fileName + MAP_EXTENSION;
         }
         createDefaultMap( name, fileName );
      }
   }

   private void createDefaultMap( final String mapName, final String fullName )
   {
      // We wait for this map to pop-up in the model and then we open it
      final AvailableFileListener mapListener = new AvailableFileListener()
      {
         @Override
         public void fileRemoved( final File removedMap )
         {
            // Not interested
         }

         @Override
         public void fileAdded( final File addedMap )
         {
            if( addedMap.getName().toLowerCase().equals( fullName.toLowerCase() ) )
            {
               getProject().getAvailableMapsModel().removeAvailableFileListener( this );
               OpenUtilities.openMap( getProject(), addedMap );
            }
         }
      };

      final MapDefinition map = new NewMapAdapter( getProject(), mapName ).createDefaultMapDefinition();
      // Save this to file
      final File mapFile = new File( getProject().getAvailableMapsModel().getDirectory(), fullName );
      OutputStream stream = null;
      try
      {
         getProject().getAvailableMapsModel().addAvailableFileListener( mapListener );

         stream = new BufferedOutputStream( new FileOutputStream( mapFile ) );
         MapFactory.writeMap( stream, map );
      }
      catch( final IOException exception )
      {
         getProject().getAvailableMapsModel().removeAvailableFileListener( mapListener );
      }
      finally
      {
         if( stream != null )
         {
            try
            {
               stream.close();
            }
            catch( final IOException e )
            {
               // Can't really do anything here.
            }
         }
      }
   }

   private static final class NewMapAdapter
   {
      private static final int MAP_WIDTH = 40;

      private static final int MAP_HEIGHT = 40;

      private final Project project;

      private final String mapName;

      private NewMapAdapter( final Project project, final String mapName )
      {
         this.project = project;
         this.mapName = mapName;
      }

      private MapDefinition createDefaultMapDefinition()
      {
         // Header ( use the first tileset in the list)
         final AvailableTilesetsModel availableTilesets = this.project.getAvailableTilesetsModel();
         final String tileset = availableTilesets.getAvailableTilesetNames().get( 0 );
         final HeaderSection header = new HeaderSection( this.mapName, tileset, new Coordinate( 0, 0 ), 0, Direction.DOWN );

         // No NPCs
         final NpcSection npcs = new NpcSection( new NPC[ 0 ] );

         // No Actions
         final ActionSection actions = new ActionSection( new MapAction[ 0 ] );

         // Fill base layer with tile 0 and put nothing (-1) in the other layers
         final int[][][] mapData = new int[ MapData.MAX_LAYERS ][ MAP_HEIGHT ][ MAP_WIDTH ];
         for( int layer = 0; layer < mapData.length; layer++ )
         {
            final int fill = layer == 0 ? 0 : -1;

            final int[][] map = mapData[ layer ];
            for( int row = 0; row < map.length; row++ )
            {
               final int[] rowData = map[ row ];
               Arrays.fill( rowData, fill );
            }
         }
         final MapSection maps = new MapSection( MAP_WIDTH, MAP_HEIGHT, mapData );

         // Monsters
         final MonsterSection monsters = new MonsterSection( new MonsterZone[ 0 ] );

         return new MapDefinition( header, npcs, actions, maps, monsters );
      }
   }
}
