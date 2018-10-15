package uk.co.eduardo.map.sections;

import uk.co.eduardo.abaddon.graphics.layer.NPC;

/**
 * Contains NPC information from a map file
 *
 * @author Ed
 */
public class NpcSection implements FileSection
{
   // ================| Fields |====================================

   /** An array of NPCs */
   private final NPC[] npcs;

   // ================| Constructors |====================================

   /**
    * @param npcs the npcs read from the map file
    */
   public NpcSection( final NPC[] npcs )
   {
      this.npcs = npcs;
   }

   // ================| Public Methods |====================================

   /**
    * @return the number of NPCs in this map.
    */
   public int getNpcCount()
   {
      return this.npcs.length;
   }

   /**
    * @param index the index of the NPC to get.
    * @return the NPC with the specified index.
    */
   public NPC getNpc( final int index )
   {
      return this.npcs[ index ];
   }

   /**
    * @return all the NPCs
    */
   public NPC[] getNpcs()
   {
      return this.npcs;
   }
}
