package uk.co.eduardo.map.sections;

import uk.co.eduardo.abaddon.monsters.MonsterZone;

/**
 * Contains monster zone information from a map file.
 *
 * @author Ed
 */
public class MonsterSection implements FileSection
{
   // ================| Fields |====================================

   /** The zones read from file */
   private final MonsterZone[] zones;

   // ================| Constructors |====================================

   /**
    * Constructs a MonsterSection.
    *
    * @param zones an array of monster zones. Cannot be <code>null</code>.
    */
   public MonsterSection( final MonsterZone[] zones )
   {
      this.zones = zones;
   }

   // ================| Public Methods |====================================

   /**
    * @return the number of zones in the map.
    */
   public int getZoneCount()
   {
      return this.zones.length;
   }

   /**
    * @param index the index of the monster zone to get.
    * @return the MonsterZone at the given zone.
    */
   public MonsterZone getZone( final int index )
   {
      return this.zones[ index ];
   }

   /**
    * @return all the zones in the map.
    */
   public MonsterZone[] getZones()
   {
      return this.zones;
   }
}
