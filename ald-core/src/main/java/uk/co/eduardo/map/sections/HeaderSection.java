package uk.co.eduardo.map.sections;

import uk.co.eduardo.abaddon.graphics.layer.Direction;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Contains header information from a map file
 *
 * @author Ed
 */
public class HeaderSection implements FileSection
{
   // ================| Fields |====================================

   /** Unique name of the map. */
   private final String mapName;

   /** Name of the map */
   private final String tilesetName;

   /** Character's starting position in this map */
   private final Coordinate startPos;

   /** The initial layer index for the hero. */
   private final int layerIndex;

   private final Direction startingDirection;

   // ================| Constructors |====================================

   /**
    * @param mapName the unique name for this map.
    * @param tilesetName name of the tileset associated with this map
    * @param startPos the character's starting position in the map.
    * @param layerIndex the initial layer index for the player character.
    * @param startingDirection the initial direction in which the character should face.
    */
   public HeaderSection( final String mapName,
                         final String tilesetName,
                         final Coordinate startPos,
                         final int layerIndex,
                         final Direction startingDirection )
   {
      this.mapName = mapName;
      this.tilesetName = tilesetName;
      this.startPos = new Coordinate( startPos );
      this.layerIndex = layerIndex;
      this.startingDirection = startingDirection;
   }

   // ================| Public Methods |====================================

   /**
    * @return the unique name of this map.
    */
   public String getMapName()
   {
      return this.mapName;
   }

   /**
    * @return the name of the tileset used with this map.
    */
   public String getTilesetName()
   {
      return this.tilesetName;
   }

   /**
    * @return the character's default starting position in this map.
    */
   public Coordinate getStartPos()
   {
      return this.startPos;
   }

   /**
    * @return the initial layer index for the character.
    */
   public int getLayerIndex()
   {
      return this.layerIndex;
   }

   /**
    * @return the initial direction in which the character should face.
    */
   public Direction getDirection()
   {
      return this.startingDirection;
   }
}
