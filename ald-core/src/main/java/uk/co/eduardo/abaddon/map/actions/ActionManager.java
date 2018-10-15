package uk.co.eduardo.abaddon.map.actions;

/**
 * Manages the action tiles within the map
 *
 * @author Ed
 */
public final class ActionManager
{
   // ================| Fields |====================================

   /** The current actions */
   private static MapAction[] actions;

   // ================| Constructors |====================================

   /**
    * Private constructor to prevent instantiation
    */
   private ActionManager()
   {
      // Private constructor
   }

   // ================| Public Methods |====================================

   /**
    * Sets the game actions
    *
    * @param actions the actions to manage
    */
   public static void setActions( final MapAction[] actions )
   {
      ActionManager.actions = actions;
   }

   /**
    * @return the number of actions in the array or <code>null</code> if no array has been set
    */
   public static int getActionCount()
   {
      if( actions != null )
      {
         return actions.length;
      }
      return 0;
   }

   /**
    * @param index the action to retrieve
    * @return the action at that index or <code>null</code> if no action array has been set
    */
   public static MapAction getAction( final int index )
   {
      if( actions != null )
      {
         return actions[ index ];
      }
      return null;
   }
}
