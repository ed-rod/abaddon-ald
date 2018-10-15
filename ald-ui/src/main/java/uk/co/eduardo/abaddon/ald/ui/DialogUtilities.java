package uk.co.eduardo.abaddon.ald.ui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 * Helper methods for dialogs
 */
public class DialogUtilities
{
   /**
    * Closes the specified dialog box, as if the Close button had been pressed.
    *
    * @param dialog the dialog box to close.
    */
   public static void close( final JDialog dialog )
   {
      Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent( new WindowEvent( dialog, WindowEvent.WINDOW_CLOSING ) );
   }

   /**
    * Modifies the specified dialog box to close when Escape is pressed.
    *
    * @param dialog the dialog to modify.
    */
   public static void setCloseOnEscape( final JDialog dialog )
   {
      dialog.getRootPane().registerKeyboardAction( new ActionListener()
      {
         @Override
         public void actionPerformed( final ActionEvent e )
         {
            close( dialog );
         }
      }, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), JComponent.WHEN_IN_FOCUSED_WINDOW );
   }
}
