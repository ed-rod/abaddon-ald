package uk.co.eduardo.abaddon.monsters;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Monster, monster monster.
 *
 * @author Ed
 */
@SuppressWarnings( "nls" )
public class Monster
{
   // ================| Fields |====================================

   /** Name of the bad guy. */
   public final String name;

   /** Determines the order in which people attack. */
   public final int agility;

   /** Attack power. */
   public final int attack;

   /** Defence strength. */
   public final int defence;

   /** Experience earned from defeating. */
   public final int xp;

   /** Money earned from picking pocket. */
   public final int gold;

   /** Is the enemy still alive? */
   private boolean alive;

   /** How many hit points it has left. */
   private int hp;

   /** screen position in battle. */
   public int xPos;

   /** Screen position in battle. */
   public int yPos;

   /** The default list of monsters. */
   private static final ArrayList< Monster > LIST;

   /** Map of a monster back to it's ID. */
   private static final HashMap< Monster, Integer > MAP;

   static
   {
      LIST = new ArrayList<>();
      // Add the default monsters to the list.
      // | Name | AG | AT | DF | XP | $$ | HP |
      LIST.add( new Monster( "Goober", 5, 5, 7, 2, 3, 6 ) );
      LIST.add( new Monster( "Rad Goober", 10, 9, 10, 6, 11, 10 ) );
      LIST.add( new Monster( "Mad Goober", 20, 12, 13, 12, 20, 18 ) );

      MAP = new HashMap<>();
      for( int m = 0; m < LIST.size(); m++ )
      {
         MAP.put( LIST.get( m ), m );
      }
   }

   // ================| Constructors |====================================

   /**
    * Constructs a new Monster
    *
    * @param name the monster name.
    * @param agility its agility.
    * @param attack its attack strength.
    * @param defence its defence strength.
    * @param xp experience earned from defeating.
    * @param gold money earned from picking pocket.
    * @param hp how many hit points the monster has.
    */
   private Monster( final String name,
                    final int agility,
                    final int attack,
                    final int defence,
                    final int xp,
                    final int gold,
                    final int hp )
   {
      this.name = name;
      this.agility = agility;
      this.attack = attack;
      this.defence = defence;
      this.xp = xp;
      this.gold = gold;
      this.hp = hp;

      this.alive = hp > 0;
   }

   /**
    * Copy constructor.
    *
    * @param copy the monster from which to clopy.
    */
   private Monster( final Monster copy )
   {
      this.name = copy.name;
      this.agility = copy.agility;
      this.attack = copy.attack;
      this.defence = copy.defence;
      this.xp = copy.xp;
      this.gold = copy.gold;
      this.hp = copy.hp;
      this.alive = copy.alive;
      this.xPos = copy.xPos;
      this.yPos = copy.yPos;
   }

   // ================| Public Methods |====================================

   /**
    * @return the total number of defined monsters.
    */
   public static int getAvailableMonsterCount()
   {
      return LIST.size();
   }

   /**
    * @param index the index of the monster to get.
    * @return A copy of the monster with the given index.
    */
   public static Monster getMonster( final int index )
   {
      return new Monster( LIST.get( index ) );
   }

   /**
    * @param monster the monster.
    * @return the ID of the monster.
    */
   public static int getId( final Monster monster )
   {
      final Integer id = MAP.get( monster );
      if( id != null )
      {
         return id;
      }
      throw new IllegalArgumentException( "That monster does not exist!" ); //$NON-NLS-1$
   }

   /**
    * Gets the number of hit points.
    *
    * @return the number of hit points the monster has.
    */
   public int getHP()
   {
      return this.hp;
   }

   /**
    * Decrements the monster's hit points by the specified amount.
    *
    * @param delta the amount by which the monster's HP is to be decremented.
    */
   public void decrementHP( final int delta )
   {
      this.hp = Math.max( 0, this.hp - delta );

      if( this.hp == 0 )
      {
         this.alive = false;
      }
   }

   /**
    * Increments the monster's hit points by the specified amount.
    *
    * @param delta the amount by which the monster's HP is to be incremented.
    */
   public void incrementHP( final int delta )
   {
      this.hp += delta;

      // TODO: Can a monster be revived??
      if( delta > 0 )
      {
         this.alive = true;
      }
   }

   /**
    * @return true if the monster is still alive (i.e. HP > 0 )
    */
   public boolean isAlive()
   {
      return this.alive;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      // Hash is based on name only
      return this.name.hashCode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals( final Object other )
   {
      if( !( other instanceof Monster ) )
      {
         return false;
      }
      final Monster compare = (Monster) other;

      boolean same = true;
      same &= ( this.name.equals( compare.name ) );
      same &= ( this.agility == compare.agility );
      same &= ( this.attack == compare.attack );
      same &= ( this.defence == compare.defence );
      same &= ( this.xp == compare.xp );
      same &= ( this.gold == compare.gold );

      return same;
   }
}
