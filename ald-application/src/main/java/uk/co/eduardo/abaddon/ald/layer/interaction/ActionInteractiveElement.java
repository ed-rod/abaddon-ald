package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter;
import uk.co.eduardo.abaddon.ald.layer.ActionDisplayLayer;
import uk.co.eduardo.abaddon.ald.sprite.SpriteUtilities;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Action interactive element.
 *
 * @author Ed
 */
public class ActionInteractiveElement implements InteractiveElement
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String NAME_FORMAT = resources.getString( "uk.co.eduardo.abaddon.interactive.action.name" ); //$NON-NLS-1$

   private static final Map< Dimension, BufferedImage > IMAGE_CACHE = new HashMap<>();

   private static final List< MapActionUI > ActionUIs = new ArrayList<>();

   static
   {
      ActionUIs.add( new TeleportMapActionUI() );
      ActionUIs.add( new ChangeLayerMapActionUI() );
   }

   private final ActionAdapter action;

   private final BufferedImage image;

   /**
    * @param action the action that will be interacted with.
    * @param tilesetData the current tileset data.
    */
   public ActionInteractiveElement( final ActionAdapter action, final TilesetData tilesetData )
   {
      this.action = action;
      this.image = getCachedImage( new Dimension( tilesetData.getTileWidth(), tilesetData.getTileHeight() ) );
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
      return this.action.getPosition();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setPosition( final Coordinate newPosition )
   {
      this.action.setPosition( newPosition );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void populateContextMenu( final JComponent contextArea )
   {
      for( final MapActionUI actionUI : ActionUIs )
      {
         if( actionUI.getSupportedType() == this.action.getType() )
         {
            actionUI.populateContextMenu( contextArea, this.action );
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals( final Object obj )
   {
      if( obj instanceof ActionInteractiveElement )
      {
         return ( (ActionInteractiveElement) obj ).action == this.action;
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return this.action.hashCode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return MessageFormat.format( NAME_FORMAT, new Object[]
      {
         getActionTypeName()
      } );
   }

   private String getActionTypeName()
   {
      for( final MapActionUI actionUI : ActionUIs )
      {
         if( actionUI.getSupportedType() == this.action.getType() )
         {
            return actionUI.getName();
         }
      }
      throw new IllegalStateException( "Unknown action type" ); //$NON-NLS-1$
   }

   private static BufferedImage getCachedImage( final Dimension dimension )
   {
      BufferedImage image = IMAGE_CACHE.get( dimension );
      if( image == null )
      {
         image = new BufferedImage( dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB );
         final Graphics2D g = image.createGraphics();
         g.setColor( ActionDisplayLayer.ACTION_COLOR );
         g.fillRect( 0, 0, dimension.width, dimension.height );

         image = SpriteUtilities.getGlowOverlay( image );
         IMAGE_CACHE.put( dimension, image );
      }
      return image;
   }
}
