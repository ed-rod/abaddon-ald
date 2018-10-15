package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.HeaderData;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.ald.sprite.SpriteUtilities;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Interactive element for the hero.
 *
 * @author Ed
 */
public class HeroInteractiveElement implements InteractiveElement
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String NAME = resources.getString( "uk.co.eduardo.abaddon.interactive.hero.name" ); //$NON-NLS-1$

   private final BufferedImage image;

   private final HeaderData header;

   private final ProjectSettings settings;

   /**
    * @param header the HeaderSection for the current map.
    * @param settings the current project settings
    */
   public HeroInteractiveElement( final HeaderData header, final ProjectSettings settings )
   {
      this.header = header;
      this.settings = settings;
      this.image = SpriteUtilities.getGlowOverlay( SpriteUtilities.getPCImage( settings, header.getDirection() ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public BufferedImage getInteractingImage()
   {
      return this.image;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Coordinate getPosition()
   {
      return new Coordinate( this.header.getStartPosition().x, this.header.getStartPosition().y );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPosition( final Coordinate newPosition )
   {
      this.header.setStartPosition( new Coordinate( newPosition.x, newPosition.y ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void populateContextMenu( final JComponent contextArea )
   {
      contextArea.add( new HeroContextEditor( this.header, this.settings ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals( final Object obj )
   {
      if( obj instanceof HeroInteractiveElement )
      {
         return ( (HeroInteractiveElement) obj ).header == this.header;
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return this.header.hashCode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return NAME;
   }
}
