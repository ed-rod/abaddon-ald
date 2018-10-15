package uk.co.eduardo.abaddon.ald.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;

/**
 * Custom window.
 *
 * @author Ed
 */
public final class PopupWindow extends JDialog
{
   // Number of pixels of drop shadow on the right and bottom sides.
   private static final int DROP_SHADOW = 5;

   // The number of pixels of padding between the edges of the popup window and the contents.
   private static final int PADDING = 6;

   // The size of the arc in pixels for the
   private static final int ARC = PADDING * 4;

   private BufferedImage background;

   private int dropShadow;

   private int padding;

   private int arc;

   /**
    * Initializes a {@link PopupWindow} without an owning peer.
    */
   public PopupWindow()
   {
      this( null );
   }

   /**
    * @param parent the owning window for this popup.
    */
   public PopupWindow( final Window parent )
   {
      super( parent );
      setUndecorated( true );
      setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
      DialogUtilities.setCloseOnEscape( this );
      final BoxLayout verticalLayout = new BoxLayout( this.getContentPane(), BoxLayout.Y_AXIS );
      getContentPane().setLayout( verticalLayout );
      initDefaults();

      // Close popup when it loses focus
      addWindowFocusListener( new WindowAdapter()
      {
         @Override
         public void windowLostFocus( final WindowEvent e )
         {
            closeWindow();
         }
      } );
   }

   /**
    * Cannot be made modal.
    */
   @Override
   public final void setModal( final boolean modal )
   {
      super.setModal( false );
   }

   /**
    * {@link PopupWindow}s are never modal.
    */
   @Override
   public final boolean isModal()
   {
      return false;
   }

   /**
    * Adds an action directly to the {@link PopupWindow}. Much like the {@link JPopupMenu#add(Action)} method.
    *
    * @param action the action to add
    */
   public void add( final Action action )
   {
      if( action != null )
      {
         add( new JMenuItem( action ) );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void paint( final Graphics g )
   {
      final Graphics2D g2d = (Graphics2D) g;

      // Save context
      final Color oldColor = g2d.getColor();
      final Stroke oldStroke = g2d.getStroke();
      final Shape oldClip = g2d.getClip();

      // This is a heavyweight component and we don't have any control over the Graphics that is
      // created for rendering (i.e. we cannot really override createGraphics). Therefore, we
      // cannot guarantee that it supports alpha which is necessary for displaying the drop shadow.
      // To get around that, just before we show the component we take a screen-grab of what's
      // behind it and use that as the (opaque) background for the component. This only really works
      // for popups because if you move the window behind the popup you'd see weird effects. However,
      // If you do move the window then the popup loses focus and it should be dismissed.
      if( this.background != null )
      {
         g2d.drawImage( this.background, 0, 0, null );
      }
      g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

      // First draw the drop shadow
      final float alphaDelta = 128f / DROP_SHADOW;
      float currentAlpha = alphaDelta;
      for( int shadow = 0; shadow < DROP_SHADOW; shadow++ )
      {
         g.setColor( new Color( 0, 0, 0, (int) currentAlpha ) );
         g2d.drawRoundRect( DROP_SHADOW - shadow,
                            DROP_SHADOW - shadow,
                            getWidth() - DROP_SHADOW - 1,
                            getHeight() - DROP_SHADOW - 1,
                            4 * PADDING,
                            4 * PADDING );
         currentAlpha += alphaDelta;
      }
      // Fill in the background
      g2d.setColor( getBackground() );
      g2d.fillRoundRect( 0, 0, getWidth() - this.dropShadow, getHeight() - this.dropShadow, this.arc, this.arc );

      // Draw a slightly darker border to give it a slight edge.
      g2d.setColor( getBackground().darker() );
      g2d.drawRoundRect( 0, 0, getWidth() - this.dropShadow, getHeight() - this.dropShadow, this.arc, this.arc );

      final int contentWidth = getWidth() - ( 2 * this.padding ) - this.dropShadow;
      final int contentHeight = getHeight() - ( 2 * this.padding ) - this.dropShadow;

      // Set the clip so that our borders and shadows aren't painted over.
      g2d.setClip( new Rectangle( this.padding, this.padding, contentWidth, contentHeight ) );

      // Restore context.
      g2d.setColor( oldColor );
      g2d.setStroke( oldStroke );

      // Now that we have set up the borders and drop shadow we can paint the content
      super.paint( g2d );
      g2d.setClip( oldClip );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Insets getInsets()
   {
      return new Insets( this.padding, this.padding, this.padding + this.dropShadow, this.padding + this.dropShadow );
   }

   /**
    * Displays this popup window relative to another component.
    *
    * @param invoker the popup window will be displayed relative to this component.
    * @param x the x coordinate of the popup location in the invoker's coordinate space.
    * @param y the y coordinate of the popup location in the invoker's coordinate space.
    */
   public void show( final Component invoker, final int x, final int y )
   {
      if( invoker != null )
      {
         final Point invokerOrigin = invoker.getLocationOnScreen();
         setLocation( invokerOrigin.x + x, invokerOrigin.y + y );
      }
      else
      {
         setLocation( x, y );
      }
      pack();
      captureBackground();
      setVisible( true );
   }

   private void closeWindow()
   {
      setVisible( false );
      this.background = null;
      dispose();
   }

   private void initDefaults()
   {
      this.padding = PADDING;
      this.arc = ARC;
      this.dropShadow = DROP_SHADOW;
   }

   private void captureBackground()
   {
      try
      {
         final Robot robot = new Robot();
         final Dimension dim = getPreferredSize();
         this.background = robot.createScreenCapture( new Rectangle( getLocation().x, getLocation().y, dim.width, dim.height ) );
      }
      catch( final Exception e )
      {
         // Return null.
      }
   }
}
