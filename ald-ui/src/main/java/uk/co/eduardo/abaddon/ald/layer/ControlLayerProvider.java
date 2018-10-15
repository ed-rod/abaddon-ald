package uk.co.eduardo.abaddon.ald.layer;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractItemAction;

/**
 * Provides a control layer.
 *
 * @author Ed
 */
public interface ControlLayerProvider
{
   /**
    * @return the key that will activate this control layer.
    */
   String getKey();

   /**
    * @param host the host to which layers are to be added.
    * @param model the current map model.
    * @return a new control layer
    */
   MapLayer getControlLayer( final PropertyModel model, final JComponent host );

   /**
    * @return the action that activates the control layer.
    */
   AbstractItemAction getAction();

   /**
    * @return whether this tool should be the initially selected too.
    */
   boolean isDefault();
}
