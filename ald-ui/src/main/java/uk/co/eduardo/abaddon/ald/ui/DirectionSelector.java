package uk.co.eduardo.abaddon.ald.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.ald.sprite.SpriteUtilities;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractItemAction;
import uk.co.eduardo.abaddon.graphics.layer.Direction;

/**
 * Component for selecting the initial direction an NPC faces.
 *
 * @author Ed
 */
public class DirectionSelector extends JComponent
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final List< DirectionSelectorListener > listeners = new CopyOnWriteArrayList<>();

   private final ProjectSettings settings;

   private final boolean allowDiagonal;

   private final JLabel directionDisplay = new JLabel();

   private final ButtonGroup buttonGroup = new ButtonGroup();

   private Direction direction;

   /**
    * @param initialDirection the initial direction to display.
    * @param settings the current project settings
    */
   public DirectionSelector( final Direction initialDirection, final ProjectSettings settings )
   {
      this( initialDirection, settings, false );
   }

   /**
    * @param initialDirection the initial direction to display.
    * @param settings the current project settings
    * @param allowDiagonal whether selecting diagonal directions is valid.
    */
   public DirectionSelector( final Direction initialDirection, final ProjectSettings settings, final boolean allowDiagonal )
   {
      this.settings = settings;
      this.allowDiagonal = allowDiagonal;
      if( initialDirection == null )
      {
         throw new IllegalArgumentException( "The initial direction cannot be null" ); //$NON-NLS-1$
      }
      setDirection( initialDirection );
      setLayout( new BorderLayout() );
      initUI();
   }

   /**
    * Sets the direction for the control.
    *
    * @param direction the direction to set.
    */
   public final void setDirection( final Direction direction )
   {
      if( ( direction != null ) && ( this.direction != direction ) )
      {
         final Direction oldDirection = this.direction;

         this.direction = direction;
         final BufferedImage image = SpriteUtilities.getSpriteImage( "outline.png", null, direction, this.settings ); //$NON-NLS-1$

         this.directionDisplay.setIcon( new ImageIcon( image ) );

         fireDirectionChanged( oldDirection, direction );
      }
   }

   /**
    * @return the currently selected direction.
    */
   public final Direction getDirection()
   {
      return this.direction;
   }

   /**
    * Adds a listener that will be notified when the direction changes.
    *
    * @param listener the listener to add.
    */
   public void addDirectionSelectorListener( final DirectionSelectorListener listener )
   {
      if( ( listener != null ) && !this.listeners.contains( listener ) )
      {
         this.listeners.add( listener );
      }
   }

   /**
    * Removes a listener.
    *
    * @param listener the listener to remove.
    */
   public void removeDirectionSelectorListener( final DirectionSelectorListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * Notifies all listeners that a new direction has been selected.
    *
    * @param oldDirection the previously selected direction.
    * @param newDirection the newly selected direction.
    */
   protected void fireDirectionChanged( final Direction oldDirection, final Direction newDirection )
   {
      for( final DirectionSelectorListener listener : this.listeners )
      {
         listener.directionChanged( oldDirection, newDirection );
      }
   }

   private void initUI()
   {
      // Create the UI. This is a 3 x 3 grid with an icon in the middle to indicate the direction
      final FormLayout layout = new FormLayout( "c:p, $rg, c:p, $rg, c:p", //$NON-NLS-1$
                                                "c:p, $rg, c:p, $rg, c:p" ); //$NON-NLS-1$
      final PanelBuilder builder = new PanelBuilder( layout );
      final CellConstraints cc = new CellConstraints();

      builder.add( getUiFor( Direction.UP_LEFT ), cc.xy( 1, 1 ) );
      builder.add( getUiFor( Direction.UP ), cc.xy( 3, 1 ) );
      builder.add( getUiFor( Direction.UP_RIGHT ), cc.xy( 5, 1 ) );
      builder.add( getUiFor( Direction.LEFT ), cc.xy( 1, 3 ) );
      builder.add( this.directionDisplay, cc.xy( 3, 3 ) );
      builder.add( getUiFor( Direction.RIGHT ), cc.xy( 5, 3 ) );
      builder.add( getUiFor( Direction.DOWN_LEFT ), cc.xy( 1, 5 ) );
      builder.add( getUiFor( Direction.DOWN ), cc.xy( 3, 5 ) );
      builder.add( getUiFor( Direction.DOWN_RIGHT ), cc.xy( 5, 5 ) );

      add( builder.getPanel() );
   }

   private JComponent getUiFor( final Direction dir )
   {
      // Stick them in toolbars because the buttons look nicer.
      final JToolBar toolbar = new JToolBar();
      toolbar.setFloatable( false );
      if( directionAllowed( dir ) )
      {
         final AbstractButton button = new DirectionAction( dir ).createToolBarItem();
         this.buttonGroup.add( button );
         if( dir == this.direction )
         {
            button.setSelected( true );
         }
         toolbar.add( button );
      }
      return toolbar;
   }

   private boolean directionAllowed( final Direction dir )
   {
      if( ( dir == Direction.LEFT ) || ( dir == Direction.RIGHT ) || ( dir == Direction.DOWN ) || ( dir == Direction.UP ) )
      {
         return true;
      }
      return this.allowDiagonal;
   }

   private class DirectionAction extends AbstractItemAction
   {
      private final Direction directionToSet;

      private DirectionAction( final Direction direction )
      {
         super( resources, "uk.co.eduardo.abaddon.ui.direction.selector." + direction.toString(), true ); //$NON-NLS-1$
         this.directionToSet = direction;
      }

      @Override
      public void actionPerformed( final ActionEvent e )
      {
         setDirection( this.directionToSet );
      }
   }
}
