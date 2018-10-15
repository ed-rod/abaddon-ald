package uk.co.eduardo.abaddon.ald.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Defines a UI that has the following UI elements available:
 * <table border="1" width="300">
 * <tr>
 * <td colspan="2" align="center">1</td>
 * </tr>
 * <tr>
 * <td width="20%" rowspan="2" align="center">2</td>
 * <td height="100" width="80%" align="center">3</td>
 * </tr>
 * <tr>
 * <td align="center">4</td>
 * </tr>
 * </table>
 * <ol>
 * <li>Menu bar</li>
 * <li>Tools area</li>
 * <li>Main workspace area</li>
 * <li>Status area.</li>
 * </ol>
 *
 * @author Ed
 */
public class ApplicationUI
{
   private final JMenuBar menuBar;

   private final JComponent toolsArea;

   private final JComponent workspaceArea;

   private final JComponent statusArea;

   private final JFrame frame;

   private final JDesktopPane desktop;

   /**
    * Creates an application UI.
    */
   public ApplicationUI()
   {
      this.frame = new JFrame();
      this.menuBar = new JMenuBar();
      this.toolsArea = new JPanel( new BorderLayout() );
      this.statusArea = new JPanel( new BorderLayout() );
      this.workspaceArea = new JPanel( new BorderLayout() );
      this.desktop = new JDesktopPane();
      this.desktop.setOpaque( false );

      final FormLayout layout = new FormLayout( "p, fill:p:grow", "fill:p:grow, p" ); //$NON-NLS-1$ //$NON-NLS-2$
      final CellConstraints cc = new CellConstraints();
      final DefaultFormBuilder builder = new DefaultFormBuilder( layout );

      builder.add( this.toolsArea, cc.xywh( 1, 1, 1, 2 ) );
      builder.add( this.workspaceArea, cc.xy( 2, 1 ) );
      builder.add( this.statusArea, cc.xy( 2, 2 ) );

      this.workspaceArea.add( this.desktop );
      this.frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
      this.frame.setContentPane( builder.getPanel() );
      this.frame.setJMenuBar( this.menuBar );
      this.frame.setSize( new Dimension( 800, 600 ) );
   }

   /**
    * @return the application frame.
    */
   public JFrame getFrame()
   {
      return this.frame;
   }

   /**
    * @return the menu bar
    */
   public JMenuBar getMenuBar()
   {
      return this.menuBar;
   }

   /**
    * @return the tools area
    */
   public JComponent getToolsArea()
   {
      return this.toolsArea;
   }

   /**
    * @return the status area
    */
   public JComponent getStatusArea()
   {
      return this.statusArea;
   }

   /**
    * @return the workspace area
    */
   public JComponent getWorkspaceArea()
   {
      return this.desktop;
   }
}
