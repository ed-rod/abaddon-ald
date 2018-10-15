package uk.co.eduardo.abaddon.ald.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapAdapter;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapListener;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectListener;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;

/**
 * A palette window for the tools.
 *
 * @author Ed
 */
public class TilesetPaletteWindow extends AldPaletteWindow
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String TILESET = resources.getString( "uk.co.eduardo.abaddon.palette.tileset" ); //$NON-NLS-1$

   private static final int SCROLL_WIDTH = UIManager.getInt( "ScrollBar.width" ); //$NON-NLS-1$

   private final ProjectListener projectListener = new ProjectListener()
   {
      @Override
      public void projectOpened( final Project project )
      {
         if( project != null )
         {
            project.getOpenMapsModel().addMapModelListener( TilesetPaletteWindow.this.mapListener );
            if( project.getOpenMapsModel().getActiveMap() != null )
            {
               recreateUI( project, project.getOpenMapsModel().getActiveMap() );
            }
            setVisible( true );
         }
         else
         {
            setVisible( false );
         }
      }

      @Override
      public void projectClosed( final Project project )
      {
         if( project != null )
         {
            project.getOpenMapsModel().removeMapModelListener( TilesetPaletteWindow.this.mapListener );
         }
         setVisible( false );
      }
   };

   private final OpenMapListener mapListener = new OpenMapAdapter()
   {
      @Override
      public void mapActivated( final PropertyModel activeMap )
      {
         activeMap.addPropertyListener( Properties.Tileset, TilesetPaletteWindow.this.tilesetListener );
         recreateUI( ProjectManager.getInstance().getLockedProject(), activeMap );
      }

      @Override
      public void mapInactivated( final PropertyModel inactivated )
      {
         inactivated.removePropertyListener( Properties.Tileset, TilesetPaletteWindow.this.tilesetListener );
         removeElements();
      }
   };

   private final PropertyListener tilesetListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel s )
      {
         recreateUI( ProjectManager.getInstance().getLockedProject(), s );
      }
   };

   private final ComponentListener componentListener = new ComponentAdapter()
   {
      @Override
      public void componentResized( final ComponentEvent e )
      {
         updateLocation();
      }
   };

   private final JComponent content = new JPanel( new BorderLayout() );

   private final Box contextPanel = Box.createVerticalBox();

   /**
    * Create a floating palette window for the tools.
    */
   public TilesetPaletteWindow()
   {
      super( TILESET, false, false );
      setContentPane( this.content );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      ProjectManager.getInstance().addProjectListener( this.projectListener );
      super.addNotify();
      getParent().addComponentListener( this.componentListener );
      updateLocation();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      getParent().removeComponentListener( this.componentListener );
      detachListeners();
      super.removeNotify();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doLayout()
   {
      super.doLayout();
      SwingUtilities.invokeLater( new Runnable()
      {
         @Override
         public void run()
         {
            updateLocation();
         }
      } );
   }

   private void recreateUI( final Project project, final PropertyModel map )
   {
      this.content.removeAll();
      this.contextPanel.removeAll();
      final JPanel northBox = new JPanel( new FlowLayout( FlowLayout.LEADING ) );

      if( project != null )
      {
         final TilesetSelector selector = new TilesetSelector( project );
         final TilesetDisplay display = new TilesetDisplay( map, Properties.Tileset, Properties.SelectedTiles, true, false, false );
         this.contextPanel.add( display );
         northBox.add( selector );
         this.content.add( northBox, BorderLayout.NORTH );
         this.content.add( this.contextPanel, BorderLayout.CENTER );
      }
      pack();
      updateLocation();
   }

   private void removeElements()
   {
      this.contextPanel.removeAll();
      pack();
      updateLocation();
   }

   private void detachListeners()
   {
      final Project lockedProject = ProjectManager.getInstance().getLockedProject();
      if( lockedProject != null )
      {
         // Remove the map listener
         lockedProject.getOpenMapsModel().removeMapModelListener( this.mapListener );
      }
      ProjectManager.getInstance().removeProjectListener( this.projectListener );
   }

   private void updateLocation()
   {
      final Dimension parentSize = getParent().getSize();
      final Dimension size = getSize();
      setLocation( new Point( parentSize.width - size.width - SCROLL_WIDTH, parentSize.height - size.height - SCROLL_WIDTH ) );
   }
}
