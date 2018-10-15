package uk.co.eduardo.abaddon.ald.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import uk.co.eduardo.abaddon.ald.data.adapter.NpcAdapter;
import uk.co.eduardo.abaddon.ald.data.adapter.NpcAdapter.NpcAdapterListener;
import uk.co.eduardo.abaddon.graphics.layer.NPC;
import uk.co.eduardo.map.sections.NpcSection;

/**
 * Wrapper for the NPC data being currently edited. the {@link NpcSection} is immutable and this is a mutable version from which a
 * {@link NpcSection} can be created.
 *
 * @author Ed
 */
public class NpcData implements FileSectionAdaptor, Iterable< NpcAdapter >
{
   private final List< NpcAdapter > npcs = new ArrayList<>();

   private final List< NpcDataListener > listeners = new CopyOnWriteArrayList<>();

   private final NpcAdapterListener individualNpcListener = new NpcAdapterListener()
   {
      @Override
      public void npcChanged( final NpcAdapter npc )
      {
         fireNpcUpdated( npc );
      }
   };

   /**
    * Initializes a new NPC data.
    * <p>
    * if the <code>section</code> is <code>null</code> then a default data is initialized.
    *
    * @param section the section from which to initialize the section.
    */
   public NpcData( final NpcSection section )
   {
      if( section != null )
      {
         for( final NPC npc : section.getNpcs() )
         {
            final NpcAdapter newNpc = new NpcAdapter( this, npc );
            newNpc.addNpcAdapterListener( this.individualNpcListener );
            this.npcs.add( newNpc );
         }
      }
   }

   /**
    * Adds an NPC to the list of NPCs.
    *
    * @param npc the NPC to add.
    */
   public void addNpc( final NpcAdapter npc )
   {
      if( npc != null )
      {
         npc.addNpcAdapterListener( this.individualNpcListener );
         this.npcs.add( npc );
      }
   }

   /**
    * Removes an NPC from the list of current NPCs.
    *
    * @param npc the NPC to remove
    */
   public void removeNpc( final NpcAdapter npc )
   {
      npc.removeNpcAdapterListener( this.individualNpcListener );
      this.npcs.remove( npc );
      fireNpcRemoved( npc );
   }

   /**
    * @return all the NPCs.
    */
   public List< NpcAdapter > getNpcs()
   {
      return Collections.unmodifiableList( this.npcs );
   }

   /**
    * @return the number of NPCs
    */
   public int getCount()
   {
      return this.npcs.size();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterator< NpcAdapter > iterator()
   {
      return getNpcs().iterator();
   }

   /**
    * Adds a listener that will be notified when changes are made to the {@link NpcData}
    *
    * @param listener the listener to add.
    */
   public void addNpcDataListener( final NpcDataListener listener )
   {
      if( ( listener != null ) && !this.listeners.contains( listener ) )
      {
         this.listeners.add( listener );
      }
   }

   /**
    * Removes a listener.
    *
    * @param listener the listener to remove.
    */
   public void removeNpcDataListener( final NpcDataListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * Notifies all listeners that an NPC was added.
    *
    * @param npc the NPC that was added.
    */
   protected void fireNpcAdded( final NpcAdapter npc )
   {
      for( final NpcDataListener listener : this.listeners )
      {
         listener.npcAdded( npc );
      }
   }

   /**
    * Notifies all listeners that an NPC was removed.
    *
    * @param npc the NPC that was removed.
    */
   protected void fireNpcRemoved( final NpcAdapter npc )
   {
      for( final NpcDataListener listener : this.listeners )
      {
         listener.npcRemoved( npc );
      }
   }

   /**
    * Notifies all listeners that an NPC was updated.
    *
    * @param npc the NPC that was updated.
    */
   protected void fireNpcUpdated( final NpcAdapter npc )
   {
      for( final NpcDataListener listener : this.listeners )
      {
         listener.npcUpdated( npc );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NpcSection createFileSection()
   {
      final List< NPC > adapted = new ArrayList<>();
      for( final NpcAdapter npc : this.npcs )
      {
         adapted.add( npc.createNpc() );
      }
      return new NpcSection( adapted.toArray( new NPC[ 0 ] ) );
   }
}
