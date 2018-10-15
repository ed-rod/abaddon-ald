package uk.co.eduardo.abaddon.ald.ui;

import java.awt.Component;
import java.awt.Container;

/**
 * Swing hacks
 *
 * @author Ed
 */
public final class SwingHacks
{
   private SwingHacks()
   {
      // Hide constructor for utility class.
   }

   /**
    * Forces a component to re-layout and redraw.
    * <p>
    * The way it does it is a bit extreme but I've often had the situation where adding/removing components from a hierarchy does
    * not trigger the container to layout. Even after ensuring that all hierarchy changes (and component creation) were done on the
    * Swing thread I still had this problem. I'm sure it's something to do with changing the hierarchy while the tree is displayable
    * but I didn't have time to get to the bottom of it. This hack was quicker at the time...
    *
    * @param component the component to repaint.
    */
   public static void forceLayoutAndRepaint( final Component component )
   {
      // There my be more invadate roots lower down the hierarchy
      recursiveInvalidate( component );
      component.validate();
      component.repaint();
   }

   /**
    * Invalidates the whole tree! Guarantees that everything will have to be laid out again and repainted.
    *
    * @param component root component of tree to invalidate.
    */
   private static void recursiveInvalidate( final Component component )
   {
      if( component == null )
      {
         return;
      }
      if( component instanceof Container )
      {
         for( final Component comp : ( (Container) component ).getComponents() )
         {
            recursiveInvalidate( comp );
         }
      }
      component.invalidate();
   }
}
