package uk.co.eduardo.abaddon.ald;

import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import uk.co.eduardo.abaddon.ald.actions.MapCloseAction;
import uk.co.eduardo.abaddon.ald.actions.MapDeleteAction;
import uk.co.eduardo.abaddon.ald.actions.MapOpenAction;
import uk.co.eduardo.abaddon.ald.actions.ShowProjectSettingsEditorAction;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;
import uk.co.eduardo.abaddon.ald.ui.ProjectBrowser;
import uk.co.eduardo.abaddon.ald.ui.ProjectBrowser.FileTreeNode;
import uk.co.eduardo.abaddon.ald.utils.OpenUtilities;
import uk.co.eduardo.abaddon.ald.utils.TilesetUtilities;

/**
 * Mouse listener for a {@link ProjectBrowser} tree component.
 *
 * @author Ed
 */
class ProjectBrowserMouseListener extends MouseAdapter
{
   private final ProjectBrowser browser;

   ProjectBrowserMouseListener( final ProjectBrowser browser )
   {
      this.browser = browser;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void mouseClicked( final MouseEvent e )
   {
      final JTree tree = this.browser.getTree();
      final int row = tree.getRowForLocation( e.getX(), e.getY() );
      final TreePath path = tree.getPathForLocation( e.getX(), e.getY() );
      if( ( row == -1 ) || ( path == null ) )
      {
         return;
      }
      final TreeNode leaf = (TreeNode) path.getLastPathComponent();
      if( leaf instanceof FileTreeNode )
      {
         if( SwingUtilities.isLeftMouseButton( e ) )
         {
            handleLeftClick( e, (FileTreeNode) leaf );
         }
         else if( SwingUtilities.isRightMouseButton( e ) )
         {
            handleRightClick( e, (FileTreeNode) leaf );
         }
      }
   }

   private void handleLeftClick( final MouseEvent event, final FileTreeNode fileNode )
   {
      if( event.getClickCount() == 2 )
      {
         if( this.browser.isMap( fileNode ) )
         {
            // Double-clicked on a Map node
            OpenUtilities.openMap( ProjectManager.getInstance().getLockedProject(), fileNode.getUserObject() );
         }
         else if( this.browser.isTileset( fileNode ) )
         {
            // Double-clicked on Tileset node.
            TilesetUtilities.openEditor( fileNode.getUserObject(), getWindow() );
         }
         else if( this.browser.isNpc( fileNode ) || this.browser.isPc( fileNode ) || this.browser.isMusic( fileNode ) )
         {
            // Open them with the default system editor.
            try
            {
               Desktop.getDesktop().open( fileNode.getUserObject() );
            }
            catch( final IOException e )
            {
               // Do nothing.
            }
         }
         else if( this.browser.isSettings( fileNode ) )
         {
            new ShowProjectSettingsEditorAction().actionPerformed( new ActionEvent( event.getSource(),
                                                                                    ActionEvent.ACTION_PERFORMED,
                                                                                    null ) );
         }
      }
   }

   private void handleRightClick( final MouseEvent event, final FileTreeNode fileNode )
   {
      if( event.getClickCount() != 1 )
      {
         return;
      }
      // First select the node on right-click
      this.browser.getTree().setSelectionPath( new TreePath( fileNode.getPath() ) );
      if( this.browser.isMap( fileNode ) )
      {
         final JPopupMenu popup = buildMapPopup( fileNode );
         popup.show( this.browser, event.getX(), event.getY() );
      }
      else if( this.browser.isTileset( fileNode ) )
      {
         final JPopupMenu popup = buildTilesetPopup( fileNode );
         popup.show( this.browser, event.getX(), event.getY() );
      }
   }

   private JPopupMenu buildMapPopup( final FileTreeNode fileNode )
   {
      final JPopupMenu popup = new JPopupMenu();
      final Project project = ProjectManager.getInstance().getLockedProject();

      // Add the open action
      final File mapFile = fileNode.getUserObject();
      popup.add( new MapOpenAction( mapFile ) );

      // Add the close action only if already open
      if( project.getOpenMapsModel().getMap( mapFile.getName() ) != null )
      {
         popup.add( new MapCloseAction( mapFile.getName() ) );
      }

      // Add the delete action
      popup.add( new MapDeleteAction( mapFile.getName() ) );

      return popup;
   }

   private Window getWindow()
   {
      return SwingUtilities.getWindowAncestor( this.browser );
   }

   private JPopupMenu buildTilesetPopup( @SuppressWarnings( "unused" ) final FileTreeNode fileNode )
   {
      return new JPopupMenu();
   }
}
