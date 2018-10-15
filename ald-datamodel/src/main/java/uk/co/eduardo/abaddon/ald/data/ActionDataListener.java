package uk.co.eduardo.abaddon.ald.data;

import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter;

/**
 * Listeners, when registered with {@link ActionData}, are notified when changes are made to the actions.
 *
 * @author Ed
 */
public interface ActionDataListener
{
   /**
    * Notification that an action was removed.
    *
    * @param action the action that was removed.
    */
   void actionRemoved( ActionAdapter action );

   /**
    * Notification that an action was added.
    *
    * @param action the action that was added.
    */
   void actionAdded( ActionAdapter action );

   /**
    * Notification that an action was updated.
    *
    * @param action the action that was updated.
    */
   void actionUpdated( ActionAdapter action );
}
