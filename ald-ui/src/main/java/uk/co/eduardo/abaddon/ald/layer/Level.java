package uk.co.eduardo.abaddon.ald.layer;

/**
 * Level which the layer should be drawn.
 *
 * @author Ed
 */
public enum Level
{
   /**
    * Layers added at this level are rendered at the lowest level.
    */
   Display,

   /**
    * Layers added at this level are rendered above display layers.
    */
   Overlay,

   /**
    * Layers added at this level are drawn above all other layers.
    */
   Editor;
}
