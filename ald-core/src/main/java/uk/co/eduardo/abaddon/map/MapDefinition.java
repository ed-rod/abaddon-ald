package uk.co.eduardo.abaddon.map;

import uk.co.eduardo.abaddon.tileset.Tileset;
import uk.co.eduardo.map.sections.ActionSection;
import uk.co.eduardo.map.sections.HeaderSection;
import uk.co.eduardo.map.sections.MapSection;
import uk.co.eduardo.map.sections.MonsterSection;
import uk.co.eduardo.map.sections.NpcSection;

/**
 * Holds an array of {@link Tileset} indices as a definition of a game map
 *
 * @author Ed
 */
public class MapDefinition
{
   // ================| Fields |====================================

   /** The name of the map */
   private String mapName;

   /** Map header information */
   public final HeaderSection headerSection;

   /** Map NPC information */
   public final NpcSection npcsSection;

   /** Map Action Information */
   public final ActionSection actionsSection;

   /** Map tile array information */
   public final MapSection mapsSection;

   /** Map monster information */
   public final MonsterSection monstersSection;

   /** Towns have this string in their map name. */
   private static final String TOWN_ID = "town"; //$NON-NLS-1$

   /** Caves have this string in their map name. */
   private static final String CAVE_ID = "cave"; //$NON-NLS-1$

   /** Interiors have this string in their map name. */
   private static final String INTERIOR_ID = "interior"; //$NON-NLS-1$

   // ================| Constructors |====================================

   /**
    * @param header the header section of the map.
    * @param npcs the NPC section of the map.
    * @param actions the actions section of the map.
    * @param maps the tile section of the map.
    * @param monsters the monster section of the map.
    */
   public MapDefinition( final HeaderSection header,
                         final NpcSection npcs,
                         final ActionSection actions,
                         final MapSection maps,
                         final MonsterSection monsters )
   {
      if( ( header == null ) || ( npcs == null ) || ( actions == null ) || ( maps == null ) || ( monsters == null ) )
      {
         throw new IllegalArgumentException();
      }

      this.headerSection = header;
      this.npcsSection = npcs;
      this.actionsSection = actions;
      this.mapsSection = maps;
      this.monstersSection = monsters;
   }

   // ================| Public Methods |====================================

   /**
    * Sets the name of the map
    *
    * @param mapName the name of the map to set.
    */
   public void setMapName( final String mapName )
   {
      this.mapName = mapName;
   }

   /**
    * @return the name of the map
    */
   public String getMapName()
   {
      return this.mapName;
   }

   /**
    * @return the map name without identifying suffixes
    */
   public String getStrippedName()
   {
      String strippedName = this.mapName.replace( TOWN_ID, "" ); //$NON-NLS-1$
      strippedName = strippedName.replace( INTERIOR_ID, "" ); //$NON-NLS-1$
      strippedName = strippedName.replace( CAVE_ID, "" ); //$NON-NLS-1$
      return strippedName;
   }

   /**
    * @return true if the name of this map is that of a town.
    */
   public boolean isTownMap()
   {
      return this.mapName != null ? this.mapName.contains( TOWN_ID ) : false;
   }

   /**
    * @return true if the name of this map is that of a cave.
    */
   public boolean isCaveMap()
   {
      return this.mapName != null ? this.mapName.contains( CAVE_ID ) : false;
   }

   /**
    * @return true if the name of this map is that of an interior.
    */
   public boolean isInteriorMap()
   {
      return this.mapName != null ? this.mapName.contains( INTERIOR_ID ) : false;
   }
}
