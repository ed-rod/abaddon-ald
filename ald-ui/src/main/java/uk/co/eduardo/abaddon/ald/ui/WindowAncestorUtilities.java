package uk.co.eduardo.abaddon.ald.ui;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * Utility method for getting the source window of an {@link ActionEvent}.
 *
 * @author Ed
 */
public final class WindowAncestorUtilities
{
   private WindowAncestorUtilities()
   {
      // prevent instantiation.
   }

   /**
    * Attempts to find the window ancestor for the action event.
    * <p>
    * The action event may have originated for a popup menu (or sub menu). This is checked for so that the invoker window can be
    * found.
    *
    * @param event the action event.
    * @return the source <code>Window</code> or <code>null</code> if none could be found.
    */
   public static Window getWindow( final ActionEvent event )
   {
      if( event == null )
      {
         return null;
      }
      if( event.getSource() instanceof Component )
      {
         Component comp = (Component) event.getSource();
         Window window = SwingUtilities.getWindowAncestor( comp );
         do
         {
            if( window == null )
            {
               // Maybe the source was a menu item housed in a JPopupMenu that has no window ancestor
               final JPopupMenu popup = (JPopupMenu) SwingUtilities.getAncestorOfClass( JPopupMenu.class,
                                                                                        ( (Component) event.getSource() ) );
               if( popup != null )
               {
                  comp = popup.getInvoker();
                  window = SwingUtilities.getWindowAncestor( comp );
               }
               else
               {
                  return null;
               }
            }
            else
            {
               return window;
            }
         }
         while( true );
      }
      return null;
   }
}
