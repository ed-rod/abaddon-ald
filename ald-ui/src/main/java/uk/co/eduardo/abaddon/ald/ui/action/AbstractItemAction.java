package uk.co.eduardo.abaddon.ald.ui.action;

import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 * Abstract base class for creating actions from a resource key.
 * <p>
 * Also has methods for creating toolbar and menu items.
 *
 * @author Ed
 */
public abstract class AbstractItemAction extends AbstractAction
{
   private final boolean toggle;

   /**
    * Create an action from a resource bundle and a key.
    * <p>
    * The key will be used as the base for searching the bundle for the following entries:<br/>
    * <i>key</i><b>.name</b><br/>
    * <i>key</i><b>.icon</b><br/>
    * <i>key</i><b>.icon.small</b><br/>
    * <i>key</i><b>.tooltip</b><br/>
    * <i>key</i><b>.accelerator</b><br/>
    * <i>key</i><b>.mnemonic</b><br/>
    *
    * Not all of the options need to be defined in the resource bundle.
    *
    * @param bundle Resource bundle
    * @param key the base key for the action.
    */
   public AbstractItemAction( final ResourceBundle bundle, final String key )
   {
      this( bundle, key, false );
   }

   /**
    * Create an action from a resource bundle and a key.
    * <p>
    * The key will be used as the base for searching the bundle for the following entries:<br/>
    * <i>key</i><b>.name</b><br/>
    * <i>key</i><b>.icon</b><br/>
    * <i>key</i><b>.icon.small</b><br/>
    * <i>key</i><b>.tooltip</b><br/>
    * <i>key</i><b>.accelerator</b><br/>
    * <i>key</i><b>.mnemonic</b><br/>
    *
    * Not all of the options need to be defined in the resource bundle.
    *
    * @param bundle Resource bundle
    * @param key the base key for the action.
    * @param toggle whether the action is a toggle action.
    */
   public AbstractItemAction( final ResourceBundle bundle, final String key, final boolean toggle )
   {
      this.toggle = toggle;
      extractIcon( Action.LARGE_ICON_KEY, bundle, key + ".icon" ); //$NON-NLS-1$
      extractIcon( Action.SMALL_ICON, bundle, key + ".icon.small" ); //$NON-NLS-1$
      extractString( Action.NAME, bundle, key + ".name" ); //$NON-NLS-1$
      extractString( Action.SHORT_DESCRIPTION, bundle, key + ".tooltip" ); //$NON-NLS-1$
      extractKeyStroke( Action.ACCELERATOR_KEY, bundle, key + ".accelerator" ); //$NON-NLS-1$
      extractChar( Action.MNEMONIC_KEY, bundle, key + ".mnemonic" ); //$NON-NLS-1$

      putValue( Action.ACTION_COMMAND_KEY, key );
   }

   /**
    * Creates a component suitable for placement in a {@link JMenuBar}.
    *
    * @return a menu component for this action.
    */
   public JComponent createMenuBarItem()
   {
      if( this.toggle )
      {
         return new JRadioButtonMenuItem( this );
      }
      return new JMenuItem( this );
   }

   /**
    * Creates a component suitable for placement in a {@link JToolBar}.
    *
    * @return a tool bar component for this action.
    */
   public AbstractButton createToolBarItem()
   {
      final AbstractButton button = this.toggle ? new JToggleButton( this ) : new JButton( this );
      if( !this.toggle )
      {
         ( (JButton) button ).setDefaultCapable( false );
      }
      button.setHideActionText( true );
      button.setFocusable( false ); // Just because I think it looks better
      button.setHorizontalTextPosition( SwingConstants.CENTER );
      button.setVerticalTextPosition( SwingConstants.BOTTOM );
      return button;
   }

   /**
    * Sets an ImageIcon value from a resource.
    *
    * @param actionKey the key.
    * @param bundle the resource bundle.
    * @param key the resource key containing the value.
    */
   protected void extractIcon( final String actionKey, final ResourceBundle bundle, final String key )
   {
      putValue( actionKey, Resources.getImageIcon( bundle, key ) );
   }

   /**
    * Sets a single character value from a resource.
    *
    * @param actionKey the key.
    * @param bundle the resource bundle.
    * @param key the resource key containing the value.
    */
   protected void extractChar( final String actionKey, final ResourceBundle bundle, final String key )
   {
      final String value = Resources.getString( bundle, key );
      if( value != null )
      {
         putValue( actionKey, (int) value.charAt( 0 ) );
      }
   }

   /**
    * Sets a KeyStroke value from a resource.
    *
    * @param actionKey the key.
    * @param bundle the resource bundle.
    * @param key the resource key containing the value.
    */
   protected void extractKeyStroke( final String actionKey, final ResourceBundle bundle, final String key )
   {
      KeyStroke keyStroke = null;

      final String value = Resources.getString( bundle, key );
      if( value != null )
      {
         keyStroke = KeyStroke.getKeyStroke( value );
      }

      if( keyStroke != null )
      {
         putValue( actionKey, keyStroke );
      }
   }

   /**
    * Sets a string value from a resource.
    *
    * @param actionKey the key.
    * @param bundle the resource bundle.
    * @param key the resource key containing the value.
    */
   protected void extractString( final String actionKey, final ResourceBundle bundle, final String key )
   {
      putValue( actionKey, Resources.getString( bundle, key ) );
   }
}
