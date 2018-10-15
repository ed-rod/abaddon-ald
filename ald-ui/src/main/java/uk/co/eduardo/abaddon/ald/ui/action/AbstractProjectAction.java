package uk.co.eduardo.abaddon.ald.ui.action;

import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectListener;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;

/**
 * An action that is only enabled when there is an open project.
 *
 * @author Ed
 */
public abstract class AbstractProjectAction extends AbstractItemAction
{
   private Project project;

   /**
    * @param bundle Resource bundle
    * @param key the base key for the action.
    */
   public AbstractProjectAction( final ResourceBundle bundle, final String key )
   {
      this( bundle, key, false );
   }

   /**
    * @param bundle Resource bundle
    * @param key the base key for the action.
    * @param toggle whether the action is a toggle action or not.
    */
   public AbstractProjectAction( final ResourceBundle bundle, final String key, final boolean toggle )
   {
      super( bundle, key, toggle );

      ProjectManager.getInstance().addProjectListener( new ProjectListener()
      {
         @Override
         public void projectOpened( final Project newProject )
         {
            setProject( newProject );
         }

         @Override
         public void projectClosed( final Project closedProject )
         {
            setProject( null );
         }
      } );
   }

   /**
    * @return the currently active project or <code>null</code> if there isn't one.
    */
   protected final Project getProject()
   {
      return this.project;
   }

   /**
    * Subclasses should override this method to refine the enabled state.
    * <p>
    * Always be sure to call super.updateEnabled() and perform any logic only if it returns <code>true</code>. If a call to
    * super.updateEnabled() returns <code>false</code> subclasses should always return <code>false</code>, too.
    *
    * @return whether this action should be enabled or not.
    */
   protected boolean updateEnabled()
   {
      return this.project != null;
   }

   /**
    * Subclasses should override this method if they need to perform any action when a project changes.
    *
    * @param oldProject the old project (may be <code>null</code> if no previous project was open).
    * @param newProject the new project (may be <code>null</code> if no new project is open).
    */
   protected void projectChanged( final Project oldProject, final Project newProject )
   {
      // Do nothing.
   }

   private void setProject( final Project newProject )
   {
      final Project oldProject = this.project;

      if( newProject != this.project )
      {
         this.project = newProject;
         setEnabled( updateEnabled() );
         projectChanged( oldProject, newProject );
      }
   }
}
