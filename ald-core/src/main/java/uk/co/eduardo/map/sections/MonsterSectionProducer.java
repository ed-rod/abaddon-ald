package uk.co.eduardo.map.sections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.graphics.Rect;
import uk.co.eduardo.abaddon.monsters.Monster;
import uk.co.eduardo.abaddon.monsters.MonsterZone;

/**
 * Reads/writes a {@link MonsterSection}
 *
 * @author Ed
 */
public class MonsterSectionProducer extends AbstractFileSectionProducer< MonsterSection >
{
   // ================| Constructors |====================================

   /**
    * Default Constructor.
    */
   public MonsterSectionProducer()
   {
      super( MonsterSection.class );
   }

   // ================| Public Methods |====================================

   /**
    * {@inheritDoc}
    */
   @Override
   public MonsterSection readSection( final DataInputStream stream ) throws IOException
   {
      final int zoneCount = stream.read();
      final MonsterZone[] zones = new MonsterZone[ zoneCount ];

      // Read each of the zones
      for( int z = 0; z < zoneCount; z++ )
      {
         final int tlx = stream.readShort();
         final int tly = stream.readShort();
         final int brx = stream.readShort();
         final int bry = stream.readShort();
         final Rect rect = new Rect( tlx, tly, brx, bry );

         final int monsterCount = stream.read() & 0xFF;
         final Monster[] monsters = new Monster[ monsterCount ];
         for( int m = 0; m < monsterCount; m++ )
         {
            monsters[ m ] = Monster.getMonster( stream.read() & 0xFF );
         }
         zones[ z ] = new MonsterZone( rect, monsters );
      }
      return new MonsterSection( zones );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeSection( final DataOutputStream stream, final MonsterSection section ) throws IOException
   {
      // Number of zones
      stream.write( section.getZoneCount() );

      for( int z = 0; z < section.getZoneCount(); z++ )
      {
         final MonsterZone zone = section.getZone( z );
         // Zone bounds
         final Rect bounds = zone.getBounds();
         stream.writeShort( bounds.left );
         stream.writeShort( bounds.top );
         stream.writeShort( bounds.right );
         stream.writeShort( bounds.bottom );

         // Number of monsters in zone
         stream.write( zone.getMonsterCount() );

         for( int m = 0; m < zone.getMonsterCount(); m++ )
         {
            // Monster id.
            stream.write( Monster.getId( zone.getMonster( m ) ) );
         }
      }
   }
}
