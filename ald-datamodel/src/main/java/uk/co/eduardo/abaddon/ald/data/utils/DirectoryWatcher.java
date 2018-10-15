package uk.co.eduardo.abaddon.ald.data.utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Listens for changes to directories.
 *
 * @author Ed
 */
public class DirectoryWatcher
{
   private static final DirectoryWatcher INSTANCE = new DirectoryWatcher();

   private final Map< File, List< ChangeListener > > listeners = new HashMap<>();

   private final Map< File, Long > lastModified = new HashMap<>();

   private final Timer timer = new Timer( "DirectoryWatcher Poller", true ); //$NON-NLS-1$

   private final TimerTask task = new TimerTask()
   {
      @Override
      public void run()
      {
         checkForChanges();
      }
   };

   private DirectoryWatcher()
   {
      // Hide constructor for singleton.

      this.timer.scheduleAtFixedRate( this.task, 0, 1000 );
   }

   /**
    * @return the singleton instance.
    */
   public static DirectoryWatcher getInstance()
   {
      return INSTANCE;
   }

   /**
    * Registers a listener that will be notified when the last modified date of a directory is changed.
    *
    * @param directory the directory to watch. Cannot be <code>null</code> and must be a directory.
    * @param listener the listener to add.
    */
   public void addListener( final File directory, final ChangeListener listener )
   {
      if( ( directory != null ) && directory.isDirectory() )
      {
         List< ChangeListener > directoryListeners = this.listeners.get( directory );
         if( directoryListeners == null )
         {
            directoryListeners = new CopyOnWriteArrayList<>();
            this.listeners.put( directory, directoryListeners );
         }
         if( ( listener != null ) && !directoryListeners.contains( listener ) )
         {
            directoryListeners.add( listener );
            if( !this.lastModified.containsKey( directory ) )
            {
               this.lastModified.put( directory, directory.lastModified() );
            }
         }
      }
   }

   /**
    * Detaches a listener.
    *
    * @param directory the directory to stop watching.
    * @param listener the listener to remove.
    */
   public void removeListener( final File directory, final ChangeListener listener )
   {
      final List< ChangeListener > directoryListeners = this.listeners.get( directory );
      if( directoryListeners != null )
      {
         directoryListeners.remove( listener );
         if( directoryListeners.size() == 0 )
         {
            this.listeners.remove( directory );
            this.lastModified.remove( directory );
         }
      }
   }

   private void checkForChanges()
   {
      // Check all directories for their last modified date
      for( final Entry< File, Long > entry : this.lastModified.entrySet() )
      {
         final File directory = entry.getKey();
         final Long currentModified = directory.lastModified();

         if( !currentModified.equals( entry.getValue() ) )
         {
            // First update our time.
            entry.setValue( currentModified );

            final ChangeEvent event = new ChangeEvent( directory );
            final List< ChangeListener > directoryListeners = this.listeners.get( directory );
            for( final ChangeListener listener : directoryListeners )
            {
               listener.stateChanged( event );
            }
         }
      }
   }
}
