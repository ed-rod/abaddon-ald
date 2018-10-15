package uk.co.eduardo.abaddon.ald.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapAdapter;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapListener;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectListener;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;
import uk.co.eduardo.abaddon.ald.layer.ControlLayerProvider;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractItemAction;

/**
 * A palette window for the tools.
 *
 * @author Ed
 */
public class ToolsPaletteWindow extends AldPaletteWindow
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String TOOLS = resources.getString( "uk.co.eduardo.abaddon.palette.tools" ); //$NON-NLS-1$

   private final ProjectListener projectListener = new ProjectListener()
   {
      @Override
      public void projectOpened( final Project project )
      {
         if( project != null )
         {
            project.getOpenMapsModel().addMapModelListener( ToolsPaletteWindow.this.mapListener );
            if( project.getOpenMapsModel().getActiveMap() != null )
            {
               recreateUI( project.getOpenMapsModel().getActiveMap() );
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
            project.getOpenMapsModel().removeMapModelListener( ToolsPaletteWindow.this.mapListener );
         }
         setVisible( false );
      }
   };

   private final OpenMapListener mapListener = new OpenMapAdapter()
   {
      @Override
      public void mapActivated( final PropertyModel activeMap )
      {
         recreateUI( activeMap );
      }

      @Override
      public void mapInactivated( final PropertyModel inactivated )
      {
         removeElements();
      }
   };

   private final JComponent content = new JPanel( new BorderLayout() );

   private final Box contextPanel = Box.createVerticalBox();

   /**
    * Create a floating palette window for the tools.
    */
   public ToolsPaletteWindow()
   {
      super( TOOLS, false, false );
      setContentPane( this.content );
      createUI();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      ProjectManager.getInstance().addProjectListener( this.projectListener );
      super.addNotify();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      detachListeners();
      super.removeNotify();
   }

   private void createUI()
   {
      final Box verticalBox = Box.createVerticalBox();

      final ButtonGroup group = new ButtonGroup();

      JToolBar toolbar = new JToolBar();
      toolbar.setFloatable( false );
      toolbar.setLayout( new FlowLayout( FlowLayout.CENTER ) );
      verticalBox.add( toolbar );
      int counter = 0;
      for( final ControlLayerProvider provider : ServiceLoader.load( ControlLayerProvider.class ) )
      {
         final AbstractItemAction action = provider.getAction();
         final AbstractButton button = action.createToolBarItem();
         if( provider.isDefault() )
         {
            button.setSelected( true );
         }
         toolbar.add( button );
         group.add( button );
         if( ( ++counter % 2 ) == 0 )
         {
            toolbar = new JToolBar();
            toolbar.setFloatable( false );
            toolbar.setLayout( new FlowLayout( FlowLayout.CENTER ) );
            verticalBox.add( toolbar );
         }
      }
      verticalBox.add( this.contextPanel );
      this.content.add( verticalBox );
   }

   private void recreateUI( final PropertyModel map )
   {
      final FormLayout formLayout = new FormLayout( "center:p" ); //$NON-NLS-1$
      final DefaultFormBuilder builder = new DefaultFormBuilder( formLayout );
      builder.setDefaultDialogBorder();

      builder.append( new TilePositionDisplay( map, Properties.MouseTile ) );

      builder.append( new HoverTileDisplay( map,
                                            Properties.MapData,
                                            Properties.Tileset,
                                            Properties.MouseTile,
                                            Properties.ActiveLayer ) );

      builder.append( new MapLayerTable( map, Properties.LayerCount, Properties.LayerVisible, Properties.ActiveLayer ) );

      this.contextPanel.add( builder.getPanel() );
      pack();
   }

   private void removeElements()
   {
      this.contextPanel.removeAll();
      pack();
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
}
