package uk.co.eduardo.abaddon.ald.data.adapter;

import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.map.actions.MapAction;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Plugin types for the action adapters.
 *
 * @author Ed
 */
public interface ActionAdapterType
{
   /**
    * @return the type of action this adapter type can support.
    */
   int getSupportedType();

   /**
    * This will get called to save the action's specialization parameters. It is assumed that this method will only be called if a
    * call to <code>
    * {@link MapAction#getActionType()} == {@link #getSupportedType()}</code>
    *
    * @param action the action.
    * @param properties the property model to populate with the action's specialization parameters.
    */
   void saveSpecializationParameters( MapAction action, PropertyModel properties );

   /**
    * @param source the position of the action to create.
    * @param properties the property model containing all the action's specialization parameters.
    * @return the create action.
    */
   MapAction createAction( final Coordinate source, final PropertyModel properties );
}
