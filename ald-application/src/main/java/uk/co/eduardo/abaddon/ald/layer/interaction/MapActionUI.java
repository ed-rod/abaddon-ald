package uk.co.eduardo.abaddon.ald.layer.interaction;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter;

/**
 * Provides the UI for modifying an action.
 *
 * @author Ed
 */
public interface MapActionUI
{
   /**
    * @return the ID of the action which this UI supports.
    */
   int getSupportedType();

   /**
    * Adds the necessary controls to the context menu component.
    *
    * @param component the component to which the action controls are to be added.
    * @param action the action.
    */
   void populateContextMenu( JComponent component, ActionAdapter action );

   /**
    * @return the display name of the map action.
    */
   String getName();
}
