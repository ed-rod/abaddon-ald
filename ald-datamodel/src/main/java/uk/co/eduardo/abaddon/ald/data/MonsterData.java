package uk.co.eduardo.abaddon.ald.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import uk.co.eduardo.abaddon.monsters.MonsterZone;
import uk.co.eduardo.map.sections.MonsterSection;

/**
 * Wrapper for the monster data being currently edited. the {@link MonsterSection} is immutable and this is a mutable version from
 * which a {@link MonsterSection} can be created.
 *
 * @author Ed
 */
public class MonsterData implements FileSectionAdaptor, Iterable< MonsterZone >
{
   private final List< MonsterZone > zones = new ArrayList<>();

   /**
    * Initializes a new monster data.
    * <p>
    * if the <code>section</code> is <code>null</code> then a default data is initialized.
    *
    * @param section the section from which to initialize the section.
    */
   public MonsterData( final MonsterSection section )
   {
      if( section != null )
      {
         this.zones.addAll( Arrays.asList( section.getZones() ) );
      }
   }

   /**
    * Adds a zone to the list of zones.
    *
    * @param zone the zone to add.
    */
   public void addZone( final MonsterZone zone )
   {
      if( zone != null )
      {
         this.zones.add( zone );
      }
   }

   /**
    * Removes a zone from the list of current zones.
    *
    * @param zone the zone to remove
    */
   public void removeZone( final MonsterZone zone )
   {
      this.zones.remove( zone );
   }

   /**
    * @return all the actions.
    */
   public List< MonsterZone > getZones()
   {
      return Collections.unmodifiableList( this.zones );
   }

   /**
    * @return the number of actions
    */
   public int getCount()
   {
      return this.zones.size();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterator< MonsterZone > iterator()
   {
      return getZones().iterator();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MonsterSection createFileSection()
   {
      return new MonsterSection( this.zones.toArray( new MonsterZone[ 0 ] ) );
   }
}
