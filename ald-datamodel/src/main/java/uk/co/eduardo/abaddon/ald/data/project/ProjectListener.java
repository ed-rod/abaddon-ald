package uk.co.eduardo.abaddon.ald.data.project;

/**
 * Listeners are notified when project is opened and closed.
 *
 * @author Ed
 */
public interface ProjectListener
{
   /**
    * Notification that a project has just been locked.
    *
    * @param project the project that has been locked.
    */
   void projectOpened( Project project );

   /**
    * Notification that a project has been unlocked.
    *
    * @param project the project that has been unlocked.
    */
   void projectClosed( Project project );
}
