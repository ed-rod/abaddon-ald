package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Interactive elements on the map can be selected, dragged around and edited.
 *
 * @author Ed
 */
public interface InteractiveElement
{
   /**
    * @return an image to display over the item to indicate that it is interactive.
    */
   BufferedImage getInteractingImage();

   /**
    * @return the position of the interactive element in tile coordinates.
    */
   Coordinate getPosition();

   /**
    * Sets the position of the interactive element in tile coordinates.
    *
    * @param newPosition the new position in tile coordinates.
    */
   void setPosition( final Coordinate newPosition );

   /**
    * Add controls to this interactive element's context menu.
    *
    * @param contextArea the component that will host the context controls.
    */
   void populateContextMenu( final JComponent contextArea );
}