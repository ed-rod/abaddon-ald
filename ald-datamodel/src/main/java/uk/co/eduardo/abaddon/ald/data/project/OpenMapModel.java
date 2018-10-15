package uk.co.eduardo.abaddon.ald.data.project;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * This model tracks all the currently open maps.
 * <p>
 * A map that is being edited is encapsulated in a {@link PropertyModel}.
 *
 * @author Ed
 */
public class OpenMapModel
{
   private final List< PropertyModel > maps = new CopyOnWriteArrayList<>();

   private final List< PropertyModel > readOnlyMaps = Collections.unmodifiableList( this.maps );

   private final List< OpenMapListener > listeners = new CopyOnWriteArrayList<>();

   private PropertyModel activeMap;

   /**
    * Adds a listener that will be notified whenever a map is opened/closed or when the active map changes.
    *
    * @param listener the listener to add.
    */
   public void addMapModelListener( final OpenMapListener listener )
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
   public void removeMapModelListener( final OpenMapListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * Gets a read-only list of the maps that are currently open.
    *
    * @return the list of open maps.
    */
   public List< PropertyModel > getOpenMaps()
   {
      return this.readOnlyMaps;
   }

   /**
    * Gets a map model with the specified name.
    * <p>
    * If no map model with the given name has been opened then this method returns <code>null</code>.
    *
    * @param mapName the name of the map./
    * @return the map model with the specified name or <code>null</code> if no model with that name could be found.
    */
   public PropertyModel getMap( final String mapName )
   {
      for( final PropertyModel map : this.maps )
      {
         if( map.get( Properties.MapName ).equals( mapName ) )
         {
            return map;
         }
      }
      return null;
   }

   /**
    * @param mapModel the map model to check.
    * @return whether the map model has been previously opened but not yet closed.
    */
   public synchronized boolean isOpen( final PropertyModel mapModel )
   {
      return this.maps.contains( mapModel );
   }

   /**
    * Gets the currently active map.
    * <p>
    * If the size of {@link #getOpenMaps()} is greater than zero then this method is guaranteed to return a non-<code>null</code>
    * value.
    * <p>
    * If the size of {@link #getOpenMaps()} is zero then this method will return <code>null</code>.
    *
    * @return the currently active map.
    */
   public synchronized PropertyModel getActiveMap()
   {
      return this.activeMap;
   }

   /**
    * Sets a map as active.
    *
    * @param mapModel the model to set as active. Cannot be <code>null</code>.
    */
   public synchronized void setActiveMap( final PropertyModel mapModel )
   {
      if( ( mapModel != null ) && ( mapModel != this.activeMap ) )
      {
         lockMap( mapModel );
      }
   }

   /**
    * Adds a map to the list of open maps being managed.
    * <p>
    * This variant of <code>openMap</code> method will not set the added map as active <em>UNLESS</em> the internal list of maps is
    * empty (i.e. the size of {@link #getActiveMap()} is zero). This is to guarantee that, while there are maps in this model,
    * exactly one of them will be active.
    * <p>
    * If the model has previously been opened (but not closed) then calling this method has no effect.
    *
    * @param mapModel the map model to open. Cannot be <code>null</code>.
    */
   public synchronized void openMap( final PropertyModel mapModel )
   {
      openMap( mapModel, false );
   }

   /**
    * Adds a map to the list of open maps being managed.
    * <p>
    * The opened map model can, optionally, be set as the active map. if <code>setActive</code> is <code>false</code> but the list
    * of internal maps is empty, then it will be made active regardless. This is to ensure that, while there are maps in this model,
    * exactly one of them will be active.
    * <p>
    * If the model has previously been added then calling this method has no effect.
    *
    * @param mapModel the map model to open. Cannot be <code>null</code>.
    * @param setActive whether the added map model should be set as the active model.
    */
   public synchronized void openMap( final PropertyModel mapModel, final boolean setActive )
   {
      if( ( mapModel != null ) && !this.maps.contains( mapModel ) )
      {
         final boolean noMaps = this.maps.size() == 0;
         this.maps.add( mapModel );
         fireMapAdded( mapModel );

         if( setActive || noMaps )
         {
            setActiveMap( mapModel );
         }
      }
   }

   /**
    * Removes a map model from the list of open maps being managed.
    * <p>
    * If the mnap model passed in is <code>null</code> or has not previously been opened then this method has no effect.
    * <p>
    * If the map model being closed is the current active map then the following happens:
    * <ul>
    * <li>The map is removed from the list</li>
    * <li>If the removed map was the last item in the list then that map is unlocked and no other map is registered as active</li>
    * <li>If it wasn't the last item then the map at the same index as the closed map will become active (or the previous one in the
    * list if the closed map happens to be at the end of the list).</li>
    * </ul>
    * 
    * @param mapModel the map model to close.
    */
   public synchronized void closeMap( final PropertyModel mapModel )
   {
      if( ( mapModel != null ) && this.maps.contains( mapModel ) )
      {
         final int index = this.maps.indexOf( mapModel );
         this.maps.remove( mapModel );

         if( mapModel == this.activeMap )
         {
            // Attempt to set another map as active.
            if( this.maps.size() == 0 )
            {
               unlockMap();
            }
            else
            {
               setActiveMap( this.maps.get( Math.min( this.maps.size() - 1, index ) ) );
            }
         }
         fireMapRemoved( mapModel );
      }
   }

   private synchronized void unlockMap()
   {
      if( this.activeMap != null )
      {
         final PropertyModel oldMap = this.activeMap;
         this.activeMap = null;

         // Notify listeners.
         fireMapUnlocked( oldMap );
      }
   }

   private synchronized void lockMap( final PropertyModel map )
   {
      unlockMap();

      if( map != null )
      {
         this.activeMap = map;

         fireMapLocked( map );
      }
   }

   private void fireMapAdded( final PropertyModel model )
   {
      // Lots of UI work is done when a model is added/removed/locked/unlocked so we should ensure
      // that notification is done on the Swing thread/
      // TODO: Should this check be removed? If we're already on the event thread then
      // notification is going to occur while we're holding onto the lock. It might be better
      // to invokeLater even if we're already on the event thread just to avoid notifying
      // listeners while holding the lock.
      if( !SwingUtilities.isEventDispatchThread() )
      {
         SwingUtilities.invokeLater( new Runnable()
         {
            @Override
            public void run()
            {
               fireMapAdded( model );
            }
         } );
         return;
      }
      for( final OpenMapListener listener : this.listeners )
      {
         listener.mapOpened( model );
      }
   }

   private void fireMapRemoved( final PropertyModel model )
   {
      if( !SwingUtilities.isEventDispatchThread() )
      {
         SwingUtilities.invokeLater( new Runnable()
         {
            @Override
            public void run()
            {
               fireMapRemoved( model );
            }
         } );
         return;
      }
      for( final OpenMapListener listener : this.listeners )
      {
         listener.mapClosed( model );
      }
   }

   private void fireMapUnlocked( final PropertyModel oldModel )
   {
      if( !SwingUtilities.isEventDispatchThread() )
      {
         SwingUtilities.invokeLater( new Runnable()
         {
            @Override
            public void run()
            {
               fireMapUnlocked( oldModel );
            }
         } );
         return;
      }
      for( final OpenMapListener listener : this.listeners )
      {
         listener.mapInactivated( oldModel );
      }
   }

   private void fireMapLocked( final PropertyModel model )
   {
      if( !SwingUtilities.isEventDispatchThread() )
      {
         SwingUtilities.invokeLater( new Runnable()
         {
            @Override
            public void run()
            {
               fireMapLocked( model );
            }
         } );
         return;
      }
      for( final OpenMapListener listener : this.listeners )
      {
         listener.mapActivated( model );
      }
   }
}
