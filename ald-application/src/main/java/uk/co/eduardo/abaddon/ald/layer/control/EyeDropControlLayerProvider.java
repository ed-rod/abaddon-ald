package uk.co.eduardo.abaddon.ald.layer.control;

import java.util.ResourceBundle;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.actions.AbstractControlAction;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.layer.ControlLayerProvider;
import uk.co.eduardo.abaddon.ald.layer.MapLayer;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractItemAction;

/**
 * Provider for the eye drop tool
 *
 * @author Ed
 */
public class EyeDropControlLayerProvider implements ControlLayerProvider
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String KEY = "EyeDropControl"; //$NON-NLS-1$

   private static final AbstractItemAction ACTION = new AbstractControlAction( resources,
                                                                               "uk.co.eduardo.abaddon.action.control.eyedrop", //$NON-NLS-1$
                                                                               true,
                                                                               KEY );

   /**
    * {@inheritDoc}
    */
   @Override
   public String getKey()
   {
      return KEY;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MapLayer getControlLayer( final PropertyModel model, final JComponent host )
   {
      return new EyeDropControlLayer( model, host, Properties.ActiveLayer, Properties.SelectedTiles );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AbstractItemAction getAction()
   {
      return ACTION;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isDefault()
   {
      return false;
   }
}
