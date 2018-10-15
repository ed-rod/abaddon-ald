package uk.co.eduardo.abaddon.ald.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter;
import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter.ActionAdapterListener;
import uk.co.eduardo.abaddon.map.actions.MapAction;
import uk.co.eduardo.map.sections.ActionSection;

/**
 * Wrapper for the action data being currently edited. the {@link ActionSection} is immutable and this is a mutable version from
 * which a {@link ActionSection} can be created.
 *
 * @author Ed
 */
public class ActionData implements FileSectionAdaptor, Iterable< ActionAdapter >
{
   private final List< ActionAdapter > actions = new ArrayList<>();

   private final List< ActionDataListener > listeners = new CopyOnWriteArrayList<>();

   private final ActionAdapterListener individualActionListener = new ActionAdapterListener()
   {
      @Override
      public void actionChanged( final ActionAdapter action )
      {
         fireActionUpdated( action );
      }
   };

   /**
    * Initializes a new action data.
    * <p>
    * if the <code>section</code> is <code>null</code> then a default data is initialized.
    *
    * @param section the section from which to initialize the section.
    */
   public ActionData( final ActionSection section )
   {
      if( section != null )
      {
         for( final MapAction action : section.getActions() )
         {
            final ActionAdapter newAction = new ActionAdapter( action );
            newAction.addActionAdapterListener( this.individualActionListener );
            this.actions.add( newAction );
         }
      }
   }

   /**
    * Adds an action to the list of action.
    *
    * @param action the action to add.
    */
   public void addAction( final ActionAdapter action )
   {
      if( action != null )
      {
         this.actions.add( action );
      }
   }

   /**
    * Removes an action from the list of current actions.
    *
    * @param action the action to remove
    */
   public void removeAction( final ActionAdapter action )
   {
      this.actions.remove( action );
   }

   /**
    * @return all the actions.
    */
   public List< ActionAdapter > getActions()
   {
      return Collections.unmodifiableList( this.actions );
   }

   /**
    * @return the number of actions
    */
   public int getCount()
   {
      return this.actions.size();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterator< ActionAdapter > iterator()
   {
      return getActions().iterator();
   }

   /**
    * Adds a listener that will be notified about changes to the actions.
    *
    * @param listener the listener to add.
    */
   public void addActionDataListener( final ActionDataListener listener )
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
   public void removeActionDataListener( final ActionDataListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * Notifies all listener that an action was added.
    *
    * @param action the action that was added.
    */
   protected void fireActionAdded( final ActionAdapter action )
   {
      for( final ActionDataListener listener : this.listeners )
      {
         listener.actionAdded( action );
      }
   }

   /**
    * Notifies all listeners that an action was removed.
    *
    * @param action the action that was removed.
    */
   protected void fireActionRemoved( final ActionAdapter action )
   {
      for( final ActionDataListener listener : this.listeners )
      {
         listener.actionRemoved( action );
      }
   }

   /**
    * Notifies all listeners that an action was updated.
    *
    * @param action the action that was updated.
    */
   protected void fireActionUpdated( final ActionAdapter action )
   {
      for( final ActionDataListener listener : this.listeners )
      {
         listener.actionUpdated( action );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ActionSection createFileSection()
   {
      final List< MapAction > adapted = new ArrayList<>();
      for( final ActionAdapter action : this.actions )
      {
         adapted.add( action.createAction() );
      }
      return new ActionSection( adapted.toArray( new MapAction[ 0 ] ) );
   }
}
