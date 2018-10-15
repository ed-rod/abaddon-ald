package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.util.ServiceLoader;

import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Provides an interactive elements.
 *
 * @author Ed
 */
public interface InteractiveElementProvider
{
   /**
    * @param tile the tile coordinate
    * @return any interactive elements at that coordinate.
    */
   InteractiveElement[] getElements( Coordinate tile );

   /**
    * Get all the interactive elements within a bounding box.
    *
    * @param tile the tile coordinate for the top-left of a bounding box.
    * @param width the width of the bounding box in tiles
    * @param height the height of the bounding box in tiles.
    * @return all the elements within the bounding box.
    */
   InteractiveElement[] getElements( Coordinate tile, int width, int height );

   /**
    * Sets the current model. This should be called exactly once immediately after construction and before any call to
    * {@link #getElements(Coordinate)}.
    * <p>
    * Ideally it would be set via the constructor of the concrete implementation. However, constructors with arguments are not
    * supported by {@link ServiceLoader}.
    *
    * @param model the current map model.
    */
   void setModel( PropertyModel model );
}
