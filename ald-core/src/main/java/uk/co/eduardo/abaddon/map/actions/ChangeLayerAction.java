package uk.co.eduardo.abaddon.map.actions;

import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Changes the hero's layer position. Used to create the effect that the hero is moving above and below different layers.
 *
 * @author Ed
 */
public class ChangeLayerAction extends MapAction
{
   // ================| Fields |====================================

   /** Type ID for all ChangeLayerActions */
   public final static int CHANGE_LAYER_TYPE = 1;

   /** When activated, the hero will be moved to this layer index. */
   private final int layerIndex;

   // ================| Constructors |====================================

   /**
    * @param source the position of the action on the map.
    * @param layerIndex the target layer index.
    */
   public ChangeLayerAction( final Coordinate source, final int layerIndex )
   {
      super( source, CHANGE_LAYER_TYPE );
      this.layerIndex = layerIndex;
   }

   /**
    * @return the layer index to set on the hero.
    */
   public int getLayerIndex()
   {
      return this.layerIndex;
   }
}
