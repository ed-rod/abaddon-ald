package uk.co.eduardo.abaddon.ald.ui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 * Floating palette window.
 *
 * @author Ed
 */
public class AldPaletteWindow extends JInternalFrame
{
   /**
    * Creates a non-resizable, non-closable, non-maximizable, non-iconifiable <code>AldPaletteWindow</code> with no title.
    */
   public AldPaletteWindow()
   {
      this( "", false, false, false, false ); //$NON-NLS-1$
   }

   /**
    * Creates a non-resizable, non-closable, non-maximizable, non-iconifiable <code>AldPaletteWindow</code> with the specified
    * title.
    *
    * @param title the non-<code>null</code> <code>String</code> to display in the title bar
    */
   public AldPaletteWindow( final String title )
   {
      this( title, false, false, false, false );
   }

   /**
    * Creates a non-closable, non-maximizable, non-iconifiable <code>AldPaletteWindow</code> with the specified title and
    * resizability.
    *
    * @param title the non-<code>null</code> <code>String</code> to display in the title bar
    * @param resizable if <code>true</code>, the internal frame can be resized
    */
   public AldPaletteWindow( final String title, final boolean resizable )
   {
      this( title, resizable, false, false, false );
   }

   /**
    * Creates a non-maximizable, non-iconifiable <code>AldPaletteWindow</code> with the specified title, resizability, and
    * closability.
    *
    * @param title the non-<code>null</code> <code>String</code> to display in the title bar
    * @param resizable if <code>true</code>, the internal frame can be resized
    * @param closable if <code>true</code>, the internal frame can be closed
    */
   public AldPaletteWindow( final String title, final boolean resizable, final boolean closable )
   {
      this( title, resizable, closable, false, false );
   }

   /**
    * Creates a non-iconifiable <code>AldPaletteWindow</code> with the specified title, resizability, closability, and
    * maximizability.
    *
    * @param title the non-<code>null</code> <code>String</code> to display in the title bar
    * @param resizable if <code>true</code>, the internal frame can be resized
    * @param closable if <code>true</code>, the internal frame can be closed
    * @param maximizable if <code>true</code>, the internal frame can be maximized
    */
   public AldPaletteWindow( final String title, final boolean resizable, final boolean closable, final boolean maximizable )
   {
      this( title, resizable, closable, maximizable, false );
   }

   /**
    * Creates a <code>AldPaletteWindow</code> with the specified title, resizability, closability, maximizability, and
    * iconifiability.
    *
    * @param title the non-<code>null</code> <code>String</code> to display in the title bar
    * @param resizable if <code>true</code>, the internal frame can be resized
    * @param closable if <code>true</code>, the internal frame can be closed
    * @param maximizable if <code>true</code>, the internal frame can be maximized
    * @param iconifiable if <code>true</code>, the internal frame can be iconified
    */
   public AldPaletteWindow( final String title,
                            final boolean resizable,
                            final boolean closable,
                            final boolean maximizable,
                            final boolean iconifiable )
   {
      super( title, resizable, closable, maximizable, iconifiable );
      putClientProperty( "JInternalFrame.isPalette", Boolean.TRUE ); //$NON-NLS-1$
      // remove the menu.
      for( final Component comp : getComponents() )
      {
         if( comp instanceof BasicInternalFrameTitlePane )
         {
            // Remove the menu bar
            for( final Component c : ( (Container) comp ).getComponents() )
            {
               if( ( c instanceof JMenuBar ) || ( c instanceof JLabel ) )
               {
                  ( (Container) comp ).remove( c );
               }
            }
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void setUI( final ComponentUI newUI )
   {
      super.setUI( new AldPaletteWindowUI( this ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      pack();
      super.addNotify();
   }

   private static class AldPaletteWindowUI extends BasicInternalFrameUI
   {
      public AldPaletteWindowUI( final JInternalFrame frame )
      {
         super( frame );
      }

      @Override
      protected JComponent createNorthPane( final JInternalFrame f )
      {
         return new AldPaletteWindowFrameTitle( f );
      }
   }

   private static class AldPaletteWindowFrameTitle extends BasicInternalFrameTitlePane
   {
      public AldPaletteWindowFrameTitle( final JInternalFrame frame )
      {
         super( frame );
      }

      @Override
      protected void assembleSystemMenu()
      {
         this.menuBar = new JMenuBar();
      }

      @Override
      protected void addSystemMenuItems( final JMenu systemMenu )
      {
         // Do not have a system menu.
      }

      @Override
      protected void addSubComponents()
      {
         add( this.iconButton );
         add( this.maxButton );
         add( this.closeButton );
      }
   }
}
