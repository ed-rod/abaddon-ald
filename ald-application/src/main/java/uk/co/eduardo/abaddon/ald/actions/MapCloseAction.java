package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractMapActiveAction;

/**
 * Action to close a map.
 * <p>
 * If constructed with the default constructor then this action will close the currently active map. Otherwise it will attempt to
 * close a specific map.
 *
 * @author Ed
 */
public class MapCloseAction extends AbstractMapActiveAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final String mapName;

   /**
    * Initializes an action that closes the current map.
    */
   public MapCloseAction()
   {
      this( null );
   }

   /**
    * Initializes an action that closes the current map with the specified name.
    *
    * @param mapName the name of the map to close. If <code>null</code> then this action will attempt to close the active map.
    */
   public MapCloseAction( final String mapName )
   {
      super( resources, "uk.co.eduardo.abaddon.action.map.close" ); //$NON-NLS-1$
      this.mapName = mapName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent event )
   {
      PropertyModel toClose = getProject().getOpenMapsModel().getActiveMap();
      if( this.mapName != null )
      {
         toClose = getProject().getOpenMapsModel().getMap( this.mapName );
      }
      if( toClose != null )
      {
         getProject().getOpenMapsModel().closeMap( toClose );
      }
   }
}
