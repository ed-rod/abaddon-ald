package uk.co.eduardo.abaddon.util;

/**
 * Controls the day-night cycle
 *
 * @author Ed
 */
public final class DayNight
{
   // ================| Fields |====================================

   /** Alpha limit */
   private static final int LIMIT = 120;

   /** Length of the day in game frames */
   private static final int DAY_LENGTH = 200;

   /** Length of the night in game frames */
   private static final int NIGHT_LENGTH = 200;

   /** Constant definition for daytime */
   private static final int DAY = 0;

   /** Constant definition for dusk */
   private static final int DUSK = 1;

   /** Constant definition for night-time */
   private static final int NIGHT = 2;

   /** Constant definition for dawn */
   private static final int DAWN = 3;

   /** The time of day */
   private static int period = DAY;

   /** The counter for the period of the day */
   private static int timer = 0;

   /** The red component overlay */
   public static final int RED = 0;

   /** The green component overlay */
   public static final int GREEN = 0;

   /** The blue component overlay */
   public static final int BLUE = 96;

   // ================| Public Methods |====================================

   /**
    * Increases the time of day
    */
   public static void tick()
   {
      if( period == DAY )
      {
         timer++;
         if( timer > DAY_LENGTH )
         {
            timer = 0;
            period = DUSK;
         }
      }
      else if( period == DUSK )
      {
         timer++;
         if( timer == LIMIT )
         {
            timer = 0;
            period = NIGHT;
         }
      }
      else if( period == NIGHT )
      {
         timer++;
         if( timer == NIGHT_LENGTH )
         {
            timer = LIMIT;
            period = DAWN;
         }
      }
      else // period = DAWN
      {
         timer--;
         if( timer == 0 )
         {
            period = DAY;
         }
      }
   }

   /**
    * @return the alpha overlay for the time of day
    */
   public static int getTimeAlpha()
   {
      if( period == DAY )
      {
         return 0;
      }
      if( period == NIGHT )
      {
         return LIMIT;
      }
      return timer;
   }
}
