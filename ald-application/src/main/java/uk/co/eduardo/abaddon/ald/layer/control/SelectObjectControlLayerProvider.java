package uk.co.eduardo.abaddon.ald.layer.control;

import java.util.ResourceBundle;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.actions.AbstractControlAction;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.layer.ControlLayerProvider;
import uk.co.eduardo.abaddon.ald.layer.InteractiveElementsEditorLayer;
import uk.co.eduardo.abaddon.ald.layer.MapLayer;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractItemAction;

/**
 * Select object provider
 *
 * @author Ed
 */
public class SelectObjectControlLayerProvider implements ControlLayerProvider
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String KEY = "SelectControl"; //$NON-NLS-1$

   private static final AbstractItemAction ACTION = new AbstractControlAction( resources,
                                                                               "uk.co.eduardo.abaddon.action.control.select", //$NON-NLS-1$
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
      return new InteractiveElementsEditorLayer( model, host, Properties.Tileset, Properties.MouseTile, Properties.UndoManager );
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
