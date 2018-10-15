package uk.co.eduardo.abaddon.ald.ui.action;

import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapListener;
import uk.co.eduardo.abaddon.ald.data.project.Project;

/**
 * An action that is only enabled when there is an active map being edited.
 *
 * @author Ed
 */
public abstract class AbstractMapActiveAction extends AbstractProjectAction
{
   private PropertyModel model;

   private final OpenMapListener listener = new OpenMapListener()
   {
      @Override
      public void mapActivated( final PropertyModel activatedMap )
      {
         setModel( activatedMap );
      }

      @Override
      public void mapInactivated( final PropertyModel inactivated )
      {
         setModel( null );
      }

      @Override
      public void mapOpened( final PropertyModel opened )
      {
         // Not interested.
      }

      @Override
      public void mapClosed( final PropertyModel closed )
      {
         // Not interested.
      }
   };

   /**
    * @param bundle Resource bundle
    * @param key the base key for the action.
    */
   public AbstractMapActiveAction( final ResourceBundle bundle, final String key )
   {
      this( bundle, key, false );
   }

   /**
    * @param bundle Resource bundle
    * @param key the base key for the action.
    * @param toggle whether the action is a toggle action or not.
    */
   public AbstractMapActiveAction( final ResourceBundle bundle, final String key, final boolean toggle )
   {
      super( bundle, key, toggle );

      final Project project = getProject();
      if( project != null )
      {
         project.getOpenMapsModel().addMapModelListener( this.listener );
         this.model = project.getOpenMapsModel().getActiveMap();
      }
      setEnabled( updateEnabled() );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void projectChanged( final Project oldProject, final Project newProject )
   {
      if( oldProject != null )
      {
         oldProject.getOpenMapsModel().removeMapModelListener( this.listener );
         setModel( null );
      }
      if( newProject != null )
      {
         newProject.getOpenMapsModel().addMapModelListener( this.listener );
         setModel( newProject.getOpenMapsModel().getActiveMap() );
      }
      super.projectChanged( oldProject, newProject );
   }

   /**
    * @return the current model or <code>null</code> if there is no map currently being edited.
    */
   protected final PropertyModel getModel()
   {
      return this.model;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected boolean updateEnabled()
   {
      if( !super.updateEnabled() )
      {
         return false;
      }
      return this.model != null;
   }

   /**
    * Called when the model has been updated.
    *
    * @param oldModel the previous map model that was locked for editing or <code>null</code> if there was no previous map being
    *           edited..
    * @param newModel the new map model being edited or <code>null</code> if there is no current map being edited.
    */
   protected void mapModelChanged( final PropertyModel oldModel, final PropertyModel newModel )
   {
      // Override if necessary.
   }

   private void setModel( final PropertyModel newModel )
   {
      final PropertyModel oldModel = this.model;

      this.model = newModel;
      setEnabled( updateEnabled() );
      mapModelChanged( oldModel, newModel );
   }
}
