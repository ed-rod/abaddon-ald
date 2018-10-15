package uk.co.eduardo.abaddon.ald.data.mapmodel;

import java.io.File;

import uk.co.eduardo.abaddon.ald.data.ActionData;
import uk.co.eduardo.abaddon.ald.data.HeaderData;
import uk.co.eduardo.abaddon.ald.data.MapData;
import uk.co.eduardo.abaddon.ald.data.MonsterData;
import uk.co.eduardo.abaddon.ald.data.NpcData;
import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManager;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Keys for attributes used in the application
 *
 * @author Ed
 */
public final class Properties
{
   private Properties()
   {
      // hide constructor.
   }

   /** Property for the name of the map. */
   public static final Property< String > MapName = new Property<>( "MapName" ); //$NON-NLS-1$

   /** Property for the project settings. */
   public static final Property< ProjectSettings > ProjectSettings = new Property<>( "ProjectSettings" ); //$NON-NLS-1$

   /** Property for the map action data. */
   public static final Property< ActionData > ActionData = new Property<>( "ActionData" ); //$NON-NLS-1$

   /** Property for the map header data. */
   public static final Property< HeaderData > HeaderData = new Property<>( "HeaderData" ); //$NON-NLS-1$

   /** Property for the map tile data. */
   public static final Property< MapData > MapData = new Property<>( "MapData" ); //$NON-NLS-1$

   /** Property for the map monster data. */
   public static final Property< MonsterData > MonsterData = new Property<>( "MonsterData" ); //$NON-NLS-1$

   /** Property for the map NPC data. */
   public static final Property< NpcData > NpcData = new Property<>( "NpcData" ); //$NON-NLS-1$

   /** The current tileset. */
   public static final Property< TilesetData > Tileset = new Property<>( "Tileset" ); //$NON-NLS-1$

   /** The location to which the map is to be saved. */
   public static final Property< File > SaveLocation = new Property<>( "SaveLocation" ); //$NON-NLS-1$

   /** Whether the map has been edited since last save. */
   public static final Property< Boolean > UncommittedChanges = new Property<>( "UncommittedChanges" ); //$NON-NLS-1$

   /** The currently selected tool. */
   public static final Property< Object > SelectedTool = new Property<>( "SelectedTool" ); //$NON-NLS-1$

   /** The key for the currently selected control */
   public static final Property< String > SelectedControl = new Property<>( "SelectedControl" ); //$NON-NLS-1$

   /** The number of map layers. */
   public static final Property< Integer > LayerCount = new Property<>( "LayerCount" ); //$NON-NLS-1$

   /** The active layer that is being edited. */
   public static final Property< Integer > ActiveLayer = new Property<>( "ActiveLayer" ); //$NON-NLS-1$

   /** This property determines whether a particular layer is visible. */
   public static final Property< Boolean > LayerVisible = new Property<>( "LayerVisible" ); //$NON-NLS-1$

   /** This property determines whether the sprites are to be displayed or not. */
   public static final Property< Boolean > SpriteVisible = new Property<>( "SpriteVisible" ); //$NON-NLS-1$

   /** Tile coordinate of the current tile the mouse is hovering over. */
   public static final Property< Coordinate > MouseTile = new Property<>( "MouseTile" ); //$NON-NLS-1$

   /** The selected tile IDs */
   public static final Property< int[] > SelectedTiles = new Property<>( "SelectedTiles" ); //$NON-NLS-1$

   /** Maintains the undo/redo stack. */
   public static final Property< UndoManager > UndoManager = new Property<>( "UndoManager" ); //$NON-NLS-1$
}
