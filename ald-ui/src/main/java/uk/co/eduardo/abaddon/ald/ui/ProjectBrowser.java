package uk.co.eduardo.abaddon.ald.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import uk.co.eduardo.abaddon.ald.data.project.AvailableFileListener;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectListener;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;

/**
 * The project browser offers a tree view of the files currently in the project.
 * <p>
 * This comoponent is only enabled while there is project active.
 *
 * @author Ed
 */
public class ProjectBrowser extends JComponent
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String PROJECT = resources.getString( "uk.co.eduardo.abaddon.browser.project" ); //$NON-NLS-1$

   private static final String MAPS = resources.getString( "uk.co.eduardo.abaddon.browser.maps" ); //$NON-NLS-1$

   private static final String TILESETS = resources.getString( "uk.co.eduardo.abaddon.browser.tilesets" ); //$NON-NLS-1$

   private static final String PCS = resources.getString( "uk.co.eduardo.abaddon.browser.pcs" ); //$NON-NLS-1$

   private static final String NPCS = resources.getString( "uk.co.eduardo.abaddon.browser.npcs" ); //$NON-NLS-1$

   private static final String MUSIC = resources.getString( "uk.co.eduardo.abaddon.browser.music" ); //$NON-NLS-1$

   private enum Operation
   {
      ADDED,
      REMOVED,
      UPDATE_ALL;
   }

   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode( PROJECT );

   private final DefaultMutableTreeNode mapsNode = new DefaultMutableTreeNode( MAPS );

   private final DefaultMutableTreeNode tilesetsNode = new DefaultMutableTreeNode( TILESETS );

   private final DefaultMutableTreeNode pcsNode = new DefaultMutableTreeNode( PCS );

   private final DefaultMutableTreeNode npcsNode = new DefaultMutableTreeNode( NPCS );

   private final DefaultMutableTreeNode musicNode = new DefaultMutableTreeNode( MUSIC );

   private final ProjectListener listener = new ProjectListener()
   {
      @Override
      public void projectOpened( final Project project )
      {
         updateFromProject( project );
      }

      @Override
      public void projectClosed( final Project project )
      {
         clearAll( project );
      }
   };

   private final NodeFileListener mapListener = new NodeFileListener( this.mapsNode )
   {
      @Override
      protected List< File > getFiles()
      {
         return ProjectManager.getInstance().getLockedProject().getAvailableMapsModel().getAvailableMapFiles();
      }
   };

   private final NodeFileListener tilesetsListener = new NodeFileListener( this.tilesetsNode )
   {
      @Override
      protected List< File > getFiles()
      {
         return ProjectManager.getInstance().getLockedProject().getAvailableTilesetsModel().getAvailableDscTilesetFiles();
      }
   };

   private final NodeFileListener npcsListener = new NodeFileListener( this.npcsNode )
   {
      @Override
      protected List< File > getFiles()
      {
         return ProjectManager.getInstance().getLockedProject().getAvailableNpcsModel().getAvailableNpcFiles();
      }
   };

   private final NodeFileListener pcsListener = new NodeFileListener( this.pcsNode )
   {
      @Override
      protected List< File > getFiles()
      {
         return ProjectManager.getInstance().getLockedProject().getAvailablePcsModel().getAvailablePcFiles();
      }
   };

   private final NodeFileListener musicListener = new NodeFileListener( this.musicNode )
   {
      @Override
      protected List< File > getFiles()
      {
         return ProjectManager.getInstance().getLockedProject().getAvailableMusicModel().getAvailableMusicFiles();
      }
   };

   private final DefaultTreeModel treeModel;

   private final JTree tree;

   /**
    * Initialzes a new Project Browser component.
    */
   public ProjectBrowser()
   {
      this.treeModel = new DefaultTreeModel( this.rootNode );
      this.tree = new JTree( this.treeModel );
      this.tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
      this.tree.setCellRenderer( new IconCellRenderer() );
      // this.tree.setRowHeight( this.tree.getRowHeight() + 2 );
      setLayout( new BorderLayout() );
      add( this.tree );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      super.addNotify();
      ProjectManager.getInstance().addProjectListener( this.listener );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      ProjectManager.getInstance().removeProjectListener( this.listener );
      super.removeNotify();
   }

   /**
    * @return the underlying tree component
    */
   public JTree getTree()
   {
      return this.tree;
   }

   /**
    * @param node the node in the tree.
    * @return whether the node is a map node.
    */
   public boolean isMap( final FileTreeNode node )
   {
      return node.getParent() == this.mapsNode;
   }

   /**
    * @param node the node in the tree.
    * @return whether the node is a tileset node.
    */
   public boolean isTileset( final FileTreeNode node )
   {
      return node.getParent() == this.tilesetsNode;
   }

   /**
    * @param node the node in the tree.
    * @return whether the node is an NPC node.
    */
   public boolean isNpc( final FileTreeNode node )
   {
      return node.getParent() == this.npcsNode;
   }

   /**
    * @param node the node in the tree.
    * @return whether the node is a PC node.
    */
   public boolean isPc( final FileTreeNode node )
   {
      return node.getParent() == this.pcsNode;
   }

   /**
    * @param node the node in the tree.
    * @return whether the node is a music node.
    */
   public boolean isMusic( final FileTreeNode node )
   {
      return node.getParent() == this.musicNode;
   }

   /**
    * @param node the node in the tree.
    * @return whether the node is the node for the project settings.
    */
   public boolean isSettings( final FileTreeNode node )
   {
      // Crap test.
      return node.getParent() == this.rootNode;
   }

   private void updateFromProject( final Project project )
   {
      project.getAvailableMapsModel().addAvailableFileListener( this.mapListener );
      project.getAvailableTilesetsModel().addAvailableFileListener( this.tilesetsListener );
      project.getAvailableNpcsModel().addAvailableFileListener( this.npcsListener );
      project.getAvailablePcsModel().addAvailableFileListener( this.pcsListener );
      project.getAvailableMusicModel().addAvailableFileListener( this.musicListener );

      this.mapListener.updateAll();
      this.tilesetsListener.updateAll();
      this.npcsListener.updateAll();
      this.pcsListener.updateAll();
      this.musicListener.updateAll();

      this.rootNode.add( this.mapsNode );
      this.rootNode.add( this.tilesetsNode );
      this.rootNode.add( this.pcsNode );
      this.rootNode.add( this.npcsNode );
      this.rootNode.add( this.musicNode );
      this.rootNode.add( new FileTreeNode( project.getSettings().getSource() ) );
      this.treeModel.nodeStructureChanged( this.rootNode );
      setEnabled( true );
   }

   private void clearAll( final Project project )
   {
      project.getAvailableMapsModel().removeAvailableFileListener( this.mapListener );
      project.getAvailableTilesetsModel().removeAvailableFileListener( this.tilesetsListener );
      project.getAvailableNpcsModel().removeAvailableFileListener( this.npcsListener );
      project.getAvailablePcsModel().removeAvailableFileListener( this.pcsListener );
      project.getAvailableMusicModel().removeAvailableFileListener( this.musicListener );

      // Clear the table.
      this.rootNode.removeAllChildren();
      this.treeModel.nodeStructureChanged( this.rootNode );
      setEnabled( false );
   }

   private void updateNode( final DefaultMutableTreeNode parentNode,
                            final File tilesetFile,
                            final List< File > allFiles,
                            final Operation operation )
   {
      final FileTreeNode child = new FileTreeNode( tilesetFile );
      switch( operation )
      {
         case ADDED:
            parentNode.add( child );
            break;

         case REMOVED:
            child.setParent( parentNode );
            parentNode.remove( parentNode.getIndex( child ) );
            child.setParent( null );
            break;

         case UPDATE_ALL:
         {
            parentNode.removeAllChildren();
            for( final File file : allFiles )
            {
               parentNode.add( new FileTreeNode( file ) );
            }
         }
            break;

         default:
            throw new IllegalStateException( "Unkown operation" ); //$NON-NLS-1$
      }
      this.treeModel.nodeStructureChanged( parentNode );
   }

   private static class IconCellRenderer extends DefaultTreeCellRenderer
   {
      @Override
      public Component getTreeCellRendererComponent( final JTree tree,
                                                     final Object value,
                                                     final boolean sel,
                                                     final boolean expanded,
                                                     final boolean leaf,
                                                     final int row,
                                                     final boolean focused )
      {
         final Component component = super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, focused );

         if( value instanceof FileTreeNode )
         {
            ( (JLabel) component ).setIcon( ( (FileTreeNode) value ).getIcon() );
         }
         return component;
      }
   }

   private abstract class NodeFileListener implements AvailableFileListener
   {
      private final DefaultMutableTreeNode node;

      private NodeFileListener( final DefaultMutableTreeNode node )
      {
         this.node = node;
      }

      @Override
      public void fileAdded( final File file )
      {
         updateNode( this.node, file, getFiles(), Operation.ADDED );
      }

      @Override
      public void fileRemoved( final File file )
      {
         updateNode( this.node, file, getFiles(), Operation.REMOVED );
      }

      public void updateAll()
      {
         updateNode( this.node, null, getFiles(), Operation.UPDATE_ALL );
      }

      protected abstract List< File > getFiles();
   }

   /**
    * All leaves in the {@link ProjectBrowser} tree are {@link FileTreeNode}s.
    *
    * @author Ed
    */
   public static class FileTreeNode extends DefaultMutableTreeNode
   {
      private final Icon icon;

      private FileTreeNode( final File file )
      {
         super( file, false );
         this.icon = FileSystemView.getFileSystemView().getSystemIcon( file );
      }

      /**
       * Specialization of the method that returns the file that this node is wrapping.
       *
       * {@inheritDoc}
       */
      @Override
      public File getUserObject()
      {
         return (File) super.getUserObject();
      }

      /**
       * throws {@link UnsupportedOperationException}
       */
      @Override
      public void setUserObject( final Object userObject )
      {
         throw new UnsupportedOperationException( "Cannot change user object" ); //$NON-NLS-1$
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString()
      {
         if( getUserObject() == null )
         {
            return null;
         }
         return getUserObject().getName();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode()
      {
         return getUserObject().hashCode();
      }

      /**
       * Two {@link FileTreeNode}s are equal if the files retrieved from {@link #getUserObject()} are equal.
       *
       * {@inheritDoc}
       */
      @Override
      public boolean equals( final Object obj )
      {
         if( obj instanceof FileTreeNode )
         {
            final FileTreeNode node = (FileTreeNode) obj;
            return node.getUserObject().equals( getUserObject() );
         }
         return false;
      }

      /**
       * @return the small icon associated with the file.
       */
      public Icon getIcon()
      {
         return this.icon;
      }
   }
}
