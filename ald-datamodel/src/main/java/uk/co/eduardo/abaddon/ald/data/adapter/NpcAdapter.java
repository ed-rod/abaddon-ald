package uk.co.eduardo.abaddon.ald.data.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import uk.co.eduardo.abaddon.ald.data.NpcData;
import uk.co.eduardo.abaddon.graphics.Animation;
import uk.co.eduardo.abaddon.graphics.AnimationFactory;
import uk.co.eduardo.abaddon.graphics.layer.Direction;
import uk.co.eduardo.abaddon.graphics.layer.NPC;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * A mutable object from which an NPC can be generated.
 *
 * @author Ed
 */
public class NpcAdapter
{
   private final List< NpcAdapterListener > listeners = new CopyOnWriteArrayList<>();

   private final NpcData parentData;

   private final List< String > speeches = new ArrayList<>();

   private final List< Integer > speechEventTriggers = new ArrayList<>();

   private Coordinate position;

   private Direction direction;

   private int layerIndex;

   private int type;

   private boolean fixed;

   /**
    * @param parentData the data to which this NPC belongs.
    * @param npc the NPC to wrap.
    */
   public NpcAdapter( final NpcData parentData, final NPC npc )
   {
      this( parentData,
            npc.getType(),
            new Coordinate( npc.getTilePosition().x, npc.getTilePosition().y ),
            npc.getDirection(),
            npc.getLayerIndex(),
            npc.isFixed() );

      for( int speechIndex = 0; speechIndex < npc.getSpeechCount(); speechIndex++ )
      {
         this.speeches.add( npc.getSpeech( speechIndex ) );
         this.speechEventTriggers.add( npc.getSpeechEvent( speechIndex ) );
      }
   }

   /**
    * @param parentData the data to which this NPC belongs
    * @param type the type of the NPC
    * @param position the initial position for this NPC
    * @param direction the direction in which the NPC initially faces
    * @param layerIndex the layer index on which the NPC exists.
    * @param fixed whether the NPC is fixed (i.e. cannot move).
    */
   public NpcAdapter( final NpcData parentData,
                      final int type,
                      final Coordinate position,
                      final Direction direction,
                      final int layerIndex,
                      final boolean fixed )
   {
      this.parentData = parentData;
      this.position = position;
      this.direction = direction;
      this.layerIndex = layerIndex;
      this.type = type;
      this.fixed = fixed;
   }

   /**
    * Adds a listener that will be notified when this NPC's properties are updated.
    *
    * @param listener the listener to add.
    */
   public void addNpcAdapterListener( final NpcAdapterListener listener )
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
   public void removeNpcAdapterListener( final NpcAdapterListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * @param position the tile position to set
    */
   public void setPosition( final Coordinate position )
   {
      if( position != null )
      {
         this.position = position;
         fireNpcChanged();
      }
   }

   /**
    * @return the tile position
    */
   public Coordinate getPosition()
   {
      return this.position;
   }

   /**
    * Sets the direction in which the NPC faces.
    * <p>
    * Constrained to be UP, DOWN, LEFT or RIGHT. The diagonal directions are not valid for NPCs.
    *
    * @param direction the direction to set
    */
   public void setDirection( final Direction direction )
   {
      if( ( direction == Direction.UP ) || ( direction == Direction.DOWN ) || ( direction == Direction.LEFT ) ||
          ( direction == Direction.RIGHT ) )
      {
         this.direction = direction;
         fireNpcChanged();
      }
   }

   /**
    * @return the direction in which the NPC initially faces.
    */
   public Direction getDirection()
   {
      return this.direction;
   }

   /**
    * Sets the index of the map layer on which the NPC exists.
    *
    * @param layerIndex the index of the map layer on which the NPC is to exist.
    */
   public void setLayerIndex( final int layerIndex )
   {
      this.layerIndex = layerIndex;
      fireNpcChanged();
   }

   /**
    * @return the map layer index of the NPC.
    */
   public int getLayerIndex()
   {
      return this.layerIndex;
   }

   /**
    * Sets the NPC type.
    *
    * @param type the type to set
    */
   public void setType( final int type )
   {
      this.type = type;
      fireNpcChanged();
   }

   /**
    * @return the NPC's type
    */
   public int getType()
   {
      return this.type;
   }

   /**
    * @return whether the NPC is stationary.
    */
   public boolean isFixed()
   {
      return this.fixed;
   }

   /**
    * @param fixed whether the NPC is stationary.
    */
   public void setFixed( final boolean fixed )
   {
      if( fixed != this.fixed )
      {
         this.fixed = fixed;
         fireNpcChanged();
      }
   }

   /**
    * @return the number of things this NPC says.
    */
   public int getSpeechCount()
   {
      return this.speeches.size();
   }

   /**
    * @param index the index of the speech.
    * @return the speech at the given index.
    */
   public String getSpeech( final int index )
   {
      return this.speeches.get( index );
   }

   /**
    * @param index the index of the speech.
    * @return the game event that triggers the speech at the given index.
    */
   public int getSpeechTriggerEvent( final int index )
   {
      return this.speechEventTriggers.get( index );
   }

   /**
    * Removes one of the NPC's speeches.
    *
    * @param index the index of the speech to remove.
    */
   public void removeSpeech( final int index )
   {
      this.speeches.remove( index );
      this.speechEventTriggers.remove( index );
   }

   /**
    * Adds a speech that the NPC can say.
    *
    * @param speech the thing to say.
    * @param eventTrigger the game event that will trigger the speech.
    */
   public void addSpeech( final String speech, final int eventTrigger )
   {
      this.speeches.add( speech );
      this.speechEventTriggers.add( eventTrigger );
   }

   /**
    * Deletes this NPC.
    */
   public void delete()
   {
      this.parentData.removeNpc( this );
      fireNpcChanged();
   }

   /**
    * Adds the NPC back to the NPC data.
    */
   public void undelete()
   {
      if( !this.parentData.getNpcs().contains( this ) )
      {
         this.parentData.addNpc( this );
         fireNpcChanged();
      }
   }

   /**
    * @return the NPC from this adapter.
    */
   public NPC createNpc()
   {
      final Animation anim = AnimationFactory.getAnimation( this.type );
      final NPC npc = new NPC( anim, getPosition().x, getPosition().y, getType(), isFixed(), getSpeechCount() );
      for( int s = 0; s < getSpeechCount(); s++ )
      {
         npc.setSpeech( s, getSpeech( s ), getSpeechTriggerEvent( s ) );
      }
      npc.setLayerIndex( getLayerIndex() );
      return npc;
   }

   /**
    * Notify all listeners that this NPC was updated.
    */
   protected void fireNpcChanged()
   {
      for( final NpcAdapterListener listener : this.listeners )
      {
         listener.npcChanged( this );
      }
   }

   /**
    * Implementors are notified when an {@link NpcAdapter} is updated.
    */
   public static interface NpcAdapterListener
   {
      /**
       * Notification that NPC properties were updated.
       *
       * @param npc the NPC that was updated.
       */
      void npcChanged( NpcAdapter npc );
   }
}
