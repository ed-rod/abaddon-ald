package uk.co.eduardo.abaddon.util;

/**
 * A Simple coordinate class
 *
 * @author Ed
 */
public class Coordinate
{
   // ================| Fields |====================================

   /** X position */
   public final int x;

   /** Y position */
   public final int y;

   // ================| Constructors |====================================

   /**
    * @param x the x component of the coordinate.
    * @param y the y component of the coordinate.
    */
   public Coordinate( final int x, final int y )
   {
      this.x = x;
      this.y = y;
   }

   /**
    * Copy constructor.
    *
    * @param copy the coordinate to copy.
    */
   public Coordinate( final Coordinate copy )
   {
      this.x = copy.x;
      this.y = copy.y;
   }

   // ================| Public Methods |====================================

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals( final Object other )
   {
      if( other instanceof Coordinate )
      {
         final Coordinate check = (Coordinate) other;
         return ( this.x == check.x ) && ( this.y == check.y );
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      // Assuming that these tiles coordinates will be in the short range this gives a better hash.
      return ( this.x << 16 ) | this.y;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return "(" + this.x + ", " + this.y + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
   }
}
