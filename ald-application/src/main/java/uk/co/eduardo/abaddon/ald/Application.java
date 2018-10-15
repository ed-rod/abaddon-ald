package uk.co.eduardo.abaddon.ald;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import android.content.res.Resources;
import uk.co.eduardo.abaddon.ald.actions.CopyAction;
import uk.co.eduardo.abaddon.ald.actions.CutAction;
import uk.co.eduardo.abaddon.ald.actions.ExitAction;
import uk.co.eduardo.abaddon.ald.actions.MapCloseAction;
import uk.co.eduardo.abaddon.ald.actions.MapNewAction;
import uk.co.eduardo.abaddon.ald.actions.MapSaveAction;
import uk.co.eduardo.abaddon.ald.actions.MapSaveAsAction;
import uk.co.eduardo.abaddon.ald.actions.PasteAction;
import uk.co.eduardo.abaddon.ald.actions.ProjectCloseAction;
import uk.co.eduardo.abaddon.ald.actions.ProjectExportAction;
import uk.co.eduardo.abaddon.ald.actions.ProjectOpenAction;
import uk.co.eduardo.abaddon.ald.actions.RedoAction;
import uk.co.eduardo.abaddon.ald.actions.UndoAction;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectListener;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;
import uk.co.eduardo.abaddon.ald.layer.ControlLayerProvider;
import uk.co.eduardo.abaddon.ald.ui.ApplicationUI;
import uk.co.eduardo.abaddon.ald.ui.MapEditor;
import uk.co.eduardo.abaddon.ald.ui.ProjectBrowser;
import uk.co.eduardo.abaddon.ald.ui.SwingHacks;
import uk.co.eduardo.abaddon.ald.ui.TilesetPaletteWindow;
import uk.co.eduardo.abaddon.ald.ui.ToolsPaletteWindow;
import uk.co.eduardo.abaddon.util.Res;

/**
 * Main entry point into the application
 *
 * @author Ed
 */
public class Application
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final ApplicationUI ui = new ApplicationUI();

   private Application()
   {
      // Do nothing.
   }

   /**
    * Main entry point into the Abaddon Level Designer application.
    * <p>
    * If a program argument is supplied then it is interpreted as a path to an existing project. That project will be opened.
    * <p>
    * If no arguments are supplied then the application will start with no project loaded.
    *
    * @param args program arguments. Only the first is read.
    */
   public static void main( final String[] args )
   {
      setLookAndFeel();
      Res.resources = new Resources();
      SaveHandler.install();

      final Application application = new Application();
      application.setupUI();

      if( args.length > 0 )
      {
         final String projectPath = args[ 0 ];
         try
         {
            final Project project = new Project( new File( projectPath ) );
            ProjectManager.getInstance().lockProject( project );
         }
         catch( final Exception exception )
         {
            // Start with no project.
         }
      }
   }

   private void setupUI()
   {
      setupMenuBar();
      setupToolsArea();
      setupStatusArea();
      setupWorkspaceArea();

      ui.getFrame().setTitle( resources.getString( "uk.co.eduardo.abaddon.title" ) ); //$NON-NLS-1$
      ui.getFrame().setExtendedState( Frame.MAXIMIZED_BOTH );
      ui.getFrame().setLocationRelativeTo( null );
      ui.getFrame().setVisible( true );
   }

   private void setupMenuBar()
   {
      final JMenuBar menuBar = ui.getMenuBar();

      // File menu
      final JMenu fileMenu = new JMenu( resources.getString( "uk.co.eduardo.abaddon.menu.file" ) ); //$NON-NLS-1$
      fileMenu.add( new ProjectOpenAction() );
      fileMenu.add( new ProjectCloseAction() );
      fileMenu.add( new ProjectExportAction() );
      fileMenu.addSeparator();
      fileMenu.add( new MapNewAction() );
      fileMenu.add( new MapCloseAction() );
      fileMenu.addSeparator();
      fileMenu.add( new MapSaveAction() );
      fileMenu.add( new MapSaveAsAction() );
      fileMenu.addSeparator();
      fileMenu.add( new ExitAction( ui.getFrame() ) );
      menuBar.add( fileMenu );

      // Edit menu
      final JMenu editMenu = new JMenu( resources.getString( "uk.co.eduardo.abaddon.menu.edit" ) ); //$NON-NLS-1$
      editMenu.add( new UndoAction() );
      editMenu.add( new RedoAction() );
      editMenu.addSeparator();
      editMenu.add( new CutAction() );
      editMenu.add( new CopyAction() );
      editMenu.add( new PasteAction() );
      menuBar.add( editMenu );

      // Tools menu
      final JMenu toolsMenu = new JMenu( resources.getString( "uk.co.eduardo.abaddon.menu.tools" ) ); //$NON-NLS-1$
      for( final ControlLayerProvider provider : ServiceLoader.load( ControlLayerProvider.class ) )
      {
         toolsMenu.add( provider.getAction().createMenuBarItem() );
      }
      menuBar.add( toolsMenu );
   }

   private void setupToolsArea()
   {
      final ProjectBrowser projectBrowser = new ProjectBrowser();
      final JScrollPane scrollPane = new JScrollPane( projectBrowser )
      {
         @Override
         public Dimension getPreferredSize()
         {
            final Dimension preferred = super.getPreferredSize();
            return new Dimension( 200, preferred.height );
         }
      };

      projectBrowser.getTree().addMouseListener( new ProjectBrowserMouseListener( projectBrowser ) );
      projectBrowser.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
      ui.getToolsArea().add( scrollPane );
   }

   private void setupStatusArea()
   {
      // Do nothing for now.
   }

   private void setupWorkspaceArea()
   {
      final JComponent workspaceArea = ui.getWorkspaceArea();

      // Create the tools palette window.
      final JInternalFrame toolsPalette = new ToolsPaletteWindow();
      workspaceArea.add( toolsPalette );
      toolsPalette.setLocation( 0, 50 );

      // Create the tileset palette window.
      final JInternalFrame tilesetPalette = new TilesetPaletteWindow();
      workspaceArea.add( tilesetPalette );
      tilesetPalette.setLocation( 0, 200 );

      workspaceArea.setLayout( new BorderLayout() );
      ProjectManager.getInstance().addProjectListener( new ProjectListener()
      {
         @Override
         public void projectOpened( final Project project )
         {
            final MapEditor editor = new MapEditor( project );
            workspaceArea.add( editor );
            SwingHacks.forceLayoutAndRepaint( workspaceArea );
         }

         @Override
         public void projectClosed( final Project project )
         {
            workspaceArea.removeAll();
            SwingHacks.forceLayoutAndRepaint( workspaceArea );
         }
      } );
   }

   private static void setLookAndFeel()
   {
      try
      {
         UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
      }
      catch( final Exception exception )
      {
         // Ignore and use default LaF
      }
   }
}
