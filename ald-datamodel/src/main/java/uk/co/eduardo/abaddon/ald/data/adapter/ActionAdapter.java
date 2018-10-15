package uk.co.eduardo.abaddon.ald.data.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.map.actions.MapAction;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Adapter for actions
 *
 * @author Ed
 */
public class ActionAdapter
{
   private static final List< ActionAdapterType > ACTION_ADAPTERS;

   static
   {
      ACTION_ADAPTERS = new ArrayList<>();
      ACTION_ADAPTERS.add( new TeleportActionAdapterType() );
      ACTION_ADAPTERS.add( new ChangeLayerActionAdapterType() );
   }

   private final List< ActionAdapterListener > listeners = new CopyOnWriteArrayList<>();

   private final PropertyModel specializedParameters = new PropertyModel();

   private Coordinate position;

   private int type;

   /**
    * Initializes an ActionAdapter from a {@link MapAction}.
    *
    * @param action the action to copy.
    */
   public ActionAdapter( final MapAction action )
   {
      this.position = action.getSource();
      this.type = action.getActionType();

      for( final ActionAdapterType adapterType : ACTION_ADAPTERS )
      {
         if( adapterType.getSupportedType() == action.getActionType() )
         {
            adapterType.saveSpecializationParameters( action, this.specializedParameters );
         }
      }
   }

   /**
    * Adds a listener that will be notified when this action changes.
    *
    * @param listener the listener to add.
    */
   public void addActionAdapterListener( final ActionAdapterListener listener )
   {
      if( ( listener != null ) && !this.listeners.contains( listener ) )
      {
         this.listeners.add( listener );
      }
   }

   /**
    * Removes a listener.
    *
    * @param listener the listener to remove.
    */
   public void removeActionAdapterListener( final ActionAdapterListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * @return the tile trigger position.
    */
   public Coordinate getPosition()
   {
      return this.position;
   }

   /**
    * @param position the action trigger position.
    */
   public void setPosition( final Coordinate position )
   {
      if( position != null )
      {
         this.position = position;
         fireActionChanged();
      }
   }

   /**
    * @return the action type
    */
   public int getType()
   {
      return this.type;
   }

   /**
    * Sets the code for the type of action.
    *
    * @param type the type of action
    */
   public void setType( final int type )
   {
      this.type = type;
      fireActionChanged();
   }

   /**
    * @param property the property to get.
    * @return the value of the specialized property.
    * @param <T> the type of property to get.
    */
   public < T > T getSpecializedProperty( final Property< T > property )
   {
      return this.specializedParameters.get( property );
   }

   /**
    * @param property the property to set.
    * @param value the value of the specialized property.
    * @param <T> the type of the property to set.
    */
   public < T > void setSpecializedProperty( final Property< T > property, final T value )
   {
      this.specializedParameters.set( property, value );
      fireActionChanged();
   }

   /**
    * @return adapt back to a {@link MapAction}.
    */
   public MapAction createAction()
   {
      for( final ActionAdapterType adapterType : ACTION_ADAPTERS )
      {
         if( adapterType.getSupportedType() == this.type )
         {
            return adapterType.createAction( this.position, this.specializedParameters );
         }
      }
      throw new IllegalStateException( String.format( "Action type %d not handled!", this.type ) ); //$NON-NLS-1$
   }

   /**
    * Notify all listeners that this action was updated.
    */
   protected void fireActionChanged()
   {
      for( final ActionAdapterListener listener : this.listeners )
      {
         listener.actionChanged( this );
      }
   }

   /**
    * Implementors are notified when this adapter is updated.
    */
   public static interface ActionAdapterListener
   {
      /**
       * Notification that the action was changed.
       *
       * @param action the action that was updated.
       */
      void actionChanged( final ActionAdapter action );
   }
}
