package uk.co.eduardo.abaddon.ald.data.project;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

/**
 * Manages the currently locked project.
 * <p>
 * I really don't like this as it essentially makes everything global.
 *
 * @author Ed
 */
public class ProjectManager
{
   private static final ProjectManager INSTANCE = new ProjectManager();

   private final List< ProjectListener > listeners = new CopyOnWriteArrayList<>();

   private Project lockedProject = null;

   private ProjectManager()
   {
      // Hide constructor for utility class.
   }

   /**
    * @return the singleton instance of the project manager.
    */
   public static ProjectManager getInstance()
   {
      return INSTANCE;
   }

   /**
    * Adds a listener to to the manager that will be notified when a {@link Project} becomes locked and unlocked.
    *
    * @param listener the listener to add.
    */
   public void addProjectListener( final ProjectListener listener )
   {
      if( ( listener != null ) && !this.listeners.contains( listener ) )
      {
         this.listeners.add( listener );

         // If we already have a project the synchronously notify.
         if( this.lockedProject != null )
         {
            listener.projectOpened( this.lockedProject );
         }
      }
   }

   /**
    * Removes a listener.
    *
    * @param listener the listener to remove.
    */
   public void removeProjectListener( final ProjectListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * @param project the project to lock. If a project is already locked then that project will be unlocked first.
    */
   public synchronized void lockProject( final Project project )
   {
      unlockProject();

      if( project != null )
      {
         this.lockedProject = project;
         fireProjectLocked( project );
      }
   }

   /**
    * Unlocks the currently locked project if one exists.
    */
   public synchronized void unlockProject()
   {
      if( this.lockedProject != null )
      {
         final Project oldModel = this.lockedProject;
         this.lockedProject = null;
         fireModelUnlocked( oldModel );
      }
   }

   private void fireProjectLocked( final Project project )
   {
      // Lots of UI work is done when a project is locked/unlocked so we should ensure
      // that notification is done on the Swing thread
      if( !SwingUtilities.isEventDispatchThread() )
      {
         SwingUtilities.invokeLater( new Runnable()
         {
            @Override
            public void run()
            {
               fireProjectLocked( project );
            }
         } );
         return;
      }
      for( final ProjectListener listener : this.listeners )
      {
         listener.projectOpened( project );
      }
   }

   private void fireModelUnlocked( final Project project )
   {
      if( !SwingUtilities.isEventDispatchThread() )
      {
         SwingUtilities.invokeLater( new Runnable()
         {
            @Override
            public void run()
            {
               fireModelUnlocked( project );
            }
         } );
         return;
      }
      for( final ProjectListener listener : this.listeners )
      {
         listener.projectClosed( project );
      }
   }

   /**
    * @return the currently locked project or <code>null</code> if no project is locked.
    */
   public synchronized Project getLockedProject()
   {
      return this.lockedProject;
   }
}
