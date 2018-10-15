package uk.co.eduardo.abaddon.ald.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ResourceBundle;

import uk.co.eduardo.abaddon.ald.ui.action.AbstractProjectAction;
import uk.co.eduardo.abaddon.ald.utils.OpenUtilities;

/**
 * Action to open a map.
 *
 * @author Ed
 */
public class MapOpenAction extends AbstractProjectAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final File mapFile;

   /**
    * Initializes an action that allows the user to open a map.
    *
    * @param mapFile the map file to open.
    */
   public MapOpenAction( final File mapFile )
   {
      super( resources, "uk.co.eduardo.abaddon.action.map.open" ); //$NON-NLS-1$
      this.mapFile = mapFile;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      OpenUtilities.openMap( getProject(), this.mapFile );
   }
}
