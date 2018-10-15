package uk.co.eduardo.abaddon.ald.sprite;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import uk.co.eduardo.abaddon.ald.data.adapter.NpcAdapter;
import uk.co.eduardo.abaddon.ald.data.project.AvailableNpcsModel;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.graphics.layer.Direction;

/**
 * Utility methods for sprites
 *
 * @author Ed
 */
public class SpriteUtilities
{
   private static final String PC_PREFIX = "pc"; //$NON-NLS-1$

   private static final Map< String, BufferedImage > spriteMap = new HashMap<>();

   private SpriteUtilities()
   {
      // hide constructor for utility class.
   }

   /**
    * @param npc the NPC
    * @param settings the current project settings
    * @return the image for the sprite.
    */
   public static BufferedImage getSpriteImage( final NpcAdapter npc, final ProjectSettings settings )
   {
      if( npc == null )
      {
         return null;
      }
      return getNpcImage( npc.getType(), npc.getDirection(), settings );
   }

   /**
    * @param settings the current project settings
    * @param direction the direction in which to face.
    * @return the image for the player character
    */
   public static BufferedImage getPCImage( final ProjectSettings settings, final Direction direction )
   {
      final String spriteName = String.format( "%s%d.png", PC_PREFIX, 3, direction ); //$NON-NLS-1$
      final String spriteKey = String.format( "%s_%d", spriteName, direction.offset ); //$NON-NLS-1$

      BufferedImage image = spriteMap.get( spriteKey );
      if( image == null )
      {
         image = getSpriteImage( spriteName,
                                 ProjectManager.getInstance().getLockedProject().getAvailablePcsModel().getDirectory(),
                                 direction,
                                 settings );

         spriteMap.put( spriteKey, image );
      }
      return image;
   }

   /**
    * @param type the NPC type index.
    * @param direction the direction in which the NPC is facing
    * @param settings the current project settings
    * @return the NPC image for the specified sprite facing the given direction.
    */
   public static BufferedImage getNpcImage( final int type, final Direction direction, final ProjectSettings settings )
   {
      final AvailableNpcsModel model = ProjectManager.getInstance().getLockedProject().getAvailableNpcsModel();

      final File npcFile = model.getNpcFile( type );
      final String spriteName = npcFile.getName();
      final String spriteKey = String.format( "%s_%d", spriteName, direction.offset ); //$NON-NLS-1$

      BufferedImage image = spriteMap.get( spriteKey );
      if( image == null )
      {
         image = getSpriteImage( spriteName, model.getDirectory(), direction, settings );
         spriteMap.put( spriteKey, image );
      }
      return image;
   }

   /**
    * @param name the (suffix-less) filename of the file from which the sprite is to be extracted
    * @param directory the directory in which the image is. If <code>null</code> then it will be attempted to be read with the
    *           {@link SpriteUtilities}'s <code>class</code> <code>getResourceAsStream</code> method.
    * @param direction the direction the sprite is facing.
    * @param settings the current project settings
    * @return the image extracted from the file for the sprite facing the specified direction.
    */
   public static BufferedImage getSpriteImage( final String name,
                                               final File directory,
                                               final Direction direction,
                                               final ProjectSettings settings )
   {
      BufferedImage image = null;
      try
      {
         final InputStream stream = directory == null ? SpriteUtilities.class.getResourceAsStream( name ) : new BufferedInputStream( new FileInputStream( new File( directory,
                                                                                                                                                                    name ) ) );
         if( stream != null )
         {
            final BufferedImage fullImage = ImageIO.read( stream );
            image = cropImage( fullImage, direction, settings );
         }
      }
      catch( final IOException e )
      {
         // no image.
      }
      return image;
   }

   private static BufferedImage cropImage( final BufferedImage fullImage,
                                           final Direction direction,
                                           final ProjectSettings settings )
   {
      final int spriteWidth = settings.get( ProjectSettings.TILE_WIDTH );
      final int spriteHeight = settings.get( ProjectSettings.SPRITE_HEIGHT );

      final int cols = fullImage.getWidth() / spriteWidth;
      final int rows = fullImage.getHeight() / spriteHeight;

      final BufferedImage[] images = new BufferedImage[ cols * rows ];
      int count = 0;
      for( int row = 0; row < rows; row++ )
      {
         for( int col = 0; col < cols; col++ )
         {
            images[ count++ ] = fullImage.getSubimage( col * spriteWidth, row * spriteHeight, spriteWidth, spriteHeight );
         }
      }
      return images[ direction.offset ];
   }

   /**
    * @param input the input image.
    * @return an image with alpha that, when overlayed on the source image, will make it look like it's glowing.
    */
   public static BufferedImage getGlowOverlay( final BufferedImage input )
   {
      // In case the input image is an indexed one we'll paste it into a new image with the
      // correct type necessary for the filters.
      BufferedImage output = new BufferedImage( input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB );
      final Graphics2D g = output.createGraphics();
      g.drawImage( input, 0, 0, null );
      g.dispose();

      output = ImageFiltering.ghost( output );
      output = ImageFiltering.blur( output );
      return output;
   }
}
