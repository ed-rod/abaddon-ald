package uk.co.eduardo.abaddon.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import uk.co.eduardo.map.sections.ActionSectionProducer;
import uk.co.eduardo.map.sections.HeaderSectionProducer;
import uk.co.eduardo.map.sections.MapSectionProducer;
import uk.co.eduardo.map.sections.MonsterSectionProducer;
import uk.co.eduardo.map.sections.NpcSectionProducer;

/**
 * Support for version 0 map files.
 *
 * @author Ed
 */
public class Version0Map implements MapVersion
{
   /**
    * {@inheritDoc}
    */
   @Override
   public int getVersion()
   {
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MapDefinition readMap( final DataInputStream stream ) throws IOException
   {
      return new MapDefinition( new HeaderSectionProducer().readSection( stream ),
                                new NpcSectionProducer().readSection( stream ),
                                new ActionSectionProducer().readSection( stream ),
                                new MapSectionProducer().readSection( stream ),
                                new MonsterSectionProducer().readSection( stream ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeMap( final DataOutputStream stream, final MapDefinition map ) throws IOException
   {
      new HeaderSectionProducer().writeSection( stream, map.headerSection );
      new NpcSectionProducer().writeSection( stream, map.npcsSection );
      new ActionSectionProducer().writeSection( stream, map.actionsSection );
      new MapSectionProducer().writeSection( stream, map.mapsSection );
      new MonsterSectionProducer().writeSection( stream, map.monstersSection );
   }
}
