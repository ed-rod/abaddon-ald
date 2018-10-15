package uk.co.eduardo.abaddon.monsters;

import android.graphics.Rect;

/**
 * Represents a zone of monsters. A zone is characterized by a rectangle in map tile coordinates and a set of monsters that inhabit
 * the zone.
 * <p>
 * If the PC's (tile) coordinates fall within the zone then the PC is subject to being attacked by one of the monsters that inhabit
 * the zone.
 *
 * @author Ed
 */
public class MonsterZone
{
   // ================| Fields |====================================

   /** Rectangle that defines the bounds of the zone in tile coordinates. */
   private final Rect zoneBounds;

   /** The Monsters that inhabit the zone. */
   private final Monster[] monsters;

   // ================| Constructors |====================================

   /**
    * Constructs a monster zone with the given bounds and monsters.
    *
    * @param bounds the bounds of the zone.
    * @param monsters the monsters in the zone.
    */
   public MonsterZone( final Rect bounds, final Monster[] monsters )
   {
      this.zoneBounds = bounds;
      this.monsters = monsters;
   }

   // ================| Public Methods |====================================

   /**
    * @return the number of monsters in this zone.
    */
   public int getMonsterCount()
   {
      return this.monsters.length;
   }

   /**
    * @param index the index of the monster.
    * @return the Monster at the given index.
    */
   public Monster getMonster( final int index )
   {
      return this.monsters[ index ];
   }

   /**
    * @return the bounding rectangle of the monster zone in tile coordinates.
    */
   public Rect getBounds()
   {
      return this.zoneBounds;
   }

   /**
    * @return x tile coordinate of top-left point of zone bounding rectangle.
    */
   public int getTopLeftX()
   {
      return this.zoneBounds.left;
   }

   /**
    * @return y tile coordinate of top-left point of zone bounding rectangle
    */
   public int getTopLeftY()
   {
      return this.zoneBounds.top;
   }

   /**
    * @return x tile coordinate of bottom-right point of zone bounding rectangle
    */
   public int getBottomRightX()
   {
      return this.zoneBounds.right;
   }

   /**
    * @return y tile coordinate of bottom-right point of zone bounding rectangle
    */
   public int getBottomRightY()
   {
      return this.zoneBounds.bottom;
   }

}
