package uk.co.eduardo.map.sections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import uk.co.eduardo.abaddon.graphics.Animation;
import uk.co.eduardo.abaddon.graphics.AnimationFactory;
import uk.co.eduardo.abaddon.graphics.layer.NPC;
import uk.co.eduardo.abaddon.util.ResourceUtilities;

/**
 * Reads/writes an {@link NpcSection}
 *
 * @author Ed
 */
public class NpcSectionProducer extends AbstractFileSectionProducer< NpcSection >
{
   // ================| Constructors |====================================

   /**
    * Default Constructor.
    */
   public NpcSectionProducer()
   {
      super( NpcSection.class );
   }

   // ================| Public Methods |====================================

   /**
    * {@inheritDoc}
    */
   @Override
   public NpcSection readSection( final DataInputStream stream ) throws IOException
   {
      // Read the number of NPCs in the map.
      final int npcCount = stream.readShort();
      final NPC[] characters = new NPC[ npcCount ];

      for( int npc = 0; npc < npcCount; npc++ )
      {
         // Read the NPC type (different types represent different sprites)
         final int id = stream.readShort();

         // Read the NPC's starting tile position
         final int x = stream.readShort();
         final int y = stream.readShort();

         // The initial layer index of the NPC
         final int layerIndex = stream.read();

         // Does the NPC move or is it fixed?
         final boolean fixed = stream.read() == 1;

         // Read the number of different speeches the NPC has.
         final int speechCount = stream.readShort();

         final String key = "npc" + String.valueOf( id ); //$NON-NLS-1$
         final int resourceId = ResourceUtilities.getDrawableResourceId( key );
         final Animation anim = AnimationFactory.getAnimation( resourceId );

         characters[ npc ] = new NPC( anim, x, y, id, fixed, speechCount );
         characters[ npc ].setLayerIndex( layerIndex );

         for( int s = 0; s < speechCount; s++ )
         {
            final int event = stream.readShort();
            final String speech = readString( stream );
            characters[ npc ].setSpeech( s, speech, event );
         }
      }
      return new NpcSection( characters );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeSection( final DataOutputStream stream, final NpcSection section ) throws IOException
   {
      // Write the number of NPCs
      stream.writeShort( section.getNpcCount() );

      for( final NPC npc : section.getNpcs() )
      {
         // Write the NPC type
         stream.writeShort( npc.getType() );

         // Write the starting position
         stream.writeShort( npc.getTilePosition().x );
         stream.writeShort( npc.getTilePosition().y );

         // Write the layer index of the NPC
         stream.write( npc.getLayerIndex() );

         // Is it fixed?
         stream.write( npc.isFixed() ? 1 : 0 );

         // The number of different speeches it has.
         stream.writeShort( npc.getSpeechCount() );

         for( int s = 0; s < npc.getSpeechCount(); s++ )
         {
            // Write the event that triggers the speech.
            stream.writeShort( npc.getSpeechEvent( s ) );

            // Write the length of the speech in chars
            stream.writeShort( npc.getSpeech( s ).length() );

            for( int c = 0; c < npc.getSpeech( s ).length(); c++ )
            {
               // Write each character
               stream.write( npc.getSpeech( s ).charAt( c ) );
            }
         }
      }
   }
}
