package uk.co.eduardo.abaddon.ald.data;

import uk.co.eduardo.abaddon.ald.data.adapter.NpcAdapter;

/**
 * Listeners, when registered with {@link NpcData}, are notified when changes are made to the NPCs
 *
 * @author Ed
 */
public interface NpcDataListener
{
   /**
    * Notification that an NPC was added.
    *
    * @param npc the NPC that was added.
    */
   void npcAdded( NpcAdapter npc );

   /**
    * Notification that an NPC was removed.
    *
    * @param npc the NPC that was removed.
    */
   void npcRemoved( NpcAdapter npc );

   /**
    * Notification that an NPC was updated.
    *
    * @param npc the NPC that was updated.
    */
   void npcUpdated( NpcAdapter npc );
}
