package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.adapter.NpcAdapter;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.ald.sprite.SpriteUtilities;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Wrapper for interacting with an NPC.
 *
 * @author Ed
 */
public class NpcInteractiveElement implements InteractiveElement
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String NAME = resources.getString( "uk.co.eduardo.abaddon.interactive.npc.name" ); //$NON-NLS-1$

   private final NpcAdapter npc;

   private final BufferedImage hoverImage;

   private final ProjectSettings settings;

   /**
    * Initializes a new interactive element for a specified NPC.
    *
    * @param npc the NPC to wrap as an interactive element.
    * @param settings the current project settings
    */
   public NpcInteractiveElement( final NpcAdapter npc, final ProjectSettings settings )
   {
      this.npc = npc;
      this.settings = settings;
      final BufferedImage original = SpriteUtilities.getSpriteImage( npc, settings );
      this.hoverImage = SpriteUtilities.getGlowOverlay( original );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public BufferedImage getInteractingImage()
   {
      return this.hoverImage;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Coordinate getPosition()
   {
      return this.npc.getPosition();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPosition( final Coordinate newPosition )
   {
      this.npc.setPosition( newPosition );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void populateContextMenu( final JComponent contextArea )
   {
      contextArea.add( new NpcContextEditor( this.npc, this.settings ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals( final Object obj )
   {
      if( obj instanceof NpcInteractiveElement )
      {
         return ( (NpcInteractiveElement) obj ).npc == this.npc;
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return this.npc.hashCode();
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