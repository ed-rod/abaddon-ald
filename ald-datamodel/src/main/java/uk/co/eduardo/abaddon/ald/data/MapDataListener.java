package uk.co.eduardo.abaddon.ald.data;

/**
 * Implementors are notified when the data in the map is updated.
 *
 * @author Ed
 */
public interface MapDataListener
{
   /**
    * Notification that the map has been updated. The updates to the map were made between the bounding box specified (in tile
    * coordinates).
    *
    * @param startTileX the top-left X position where the update started
    * @param startTileY the top-right Y position where the update started.
    * @param width the number of tiles wide that were affected.
    * @param height the number of tiles wide that were affected.
    */
   void mapUpdated( int startTileX, int startTileY, int width, int height );
}
