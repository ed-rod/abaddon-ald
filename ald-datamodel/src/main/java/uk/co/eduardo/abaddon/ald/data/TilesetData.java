package uk.co.eduardo.abaddon.ald.data;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import uk.co.eduardo.abaddon.ald.data.project.AvailableTilesetsModel;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.tileset.TileDescription;
import uk.co.eduardo.abaddon.tileset.TileDescriptionReader;

/**
 * maintains the loaded tileset information.
 *
 * @author Ed
 */
public class TilesetData
{
   private final String name;

   private final int tileWidth;

   private final int tileHeight;

   private final TileDescription tileDescription;

   private final BufferedImage tileset;

   private final BufferedImage[] tiles;

   private final int tileCount;

   /**
    * Initializes the current tileset data. Uses the default tileset size of 16 x 16 pixels.
    *
    * @param tilesetDir the directory in which the tileset descriptor and image are stored.
    * @param tilesetName the name of the tileset.
    * @param settings the project settings.
    */
   public TilesetData( final File tilesetDir, final String tilesetName, final ProjectSettings settings )
   {
      this.name = tilesetName;
      this.tileWidth = settings.get( ProjectSettings.TILE_WIDTH );
      this.tileHeight = settings.get( ProjectSettings.TILE_HEIGHT );

      this.tileDescription = readTileDescription( tilesetDir, this.name );
      this.tileset = readTileset( tilesetDir, this.name );
      this.tiles = splitTiles( this.tileset, this.tileWidth, this.tileHeight );
      this.tileCount = this.tiles.length;

   }

   /**
    * @return the name of the tileset.
    */
   public String getTilesetName()
   {
      return this.name;
   }

   /**
    * @return the width of the tiles in pixels.
    */
   public int getTileWidth()
   {
      return this.tileWidth;
   }

   /**
    * @return the height of the tiles in pixels.
    */
   public int getTileHeight()
   {
      return this.tileHeight;
   }

   /**
    * @return the tile description
    */
   public TileDescription getTileDescription()
   {
      return this.tileDescription;
   }

   /**
    * @return the tiles
    */
   public BufferedImage[] getTiles()
   {
      return this.tiles;
   }

   /**
    * @return the number of tiles in this tileset.
    */
   public int getTileCount()
   {
      return this.tileCount;
   }

   /**
    * Get a tile.
    * <p>
    * The returned tile will have dimensions: ({@link #getTileWidth()} x {@link #getTileHeight()})
    *
    * @param index the index of the tile to get
    * @return the tile.
    */
   public BufferedImage getTile( final int index )
   {
      return this.tiles[ index ];
   }

   /**
    * @return the tileset image.
    */
   public BufferedImage getTileset()
   {
      return this.tileset;
   }

   private static TileDescription readTileDescription( final File tilesetDir, final String tilesetName )
   {
      final String tilesetDscName = AvailableTilesetsModel.getDscFileNameFor( tilesetName );
      final File tilesetFile = new File( tilesetDir, tilesetDscName );

      InputStream dscInput = null;
      try
      {
         dscInput = new BufferedInputStream( new FileInputStream( tilesetFile ) );
         return TileDescriptionReader.readStream( dscInput );
      }
      catch( final IOException exception )
      {
         // return null
      }
      finally
      {
         if( dscInput != null )
         {
            try
            {
               dscInput.close();
            }
            catch( final IOException exception )
            {
               // Do nothing.
            }
         }
      }
      return null;
   }

   private static BufferedImage readTileset( final File tilesetDir, final String tilesetName )
   {
      final String tilesetPngName = AvailableTilesetsModel.getPngFileNameFor( tilesetName );
      final File tilesetImageFile = new File( tilesetDir, tilesetPngName );
      InputStream stream = null;
      try
      {
         stream = new BufferedInputStream( new FileInputStream( tilesetImageFile ) );
         return ImageIO.read( stream );
      }
      catch( final IOException exception )
      {
         // return null;
      }
      finally
      {
         if( stream != null )
         {
            try
            {
               stream.close();
            }
            catch( final IOException e )
            {
               // Do nothing.
            }
         }
      }
      return null;
   }

   private static BufferedImage[] splitTiles( final BufferedImage source, final int tileWidth, final int tileHeight )
   {
      if( source == null )
      {
         return new BufferedImage[ 0 ];
      }
      final int width = source.getWidth() / tileWidth;
      final int height = source.getHeight() / tileHeight;
      final int tileCount = width * height;
      final BufferedImage[] tiles = new BufferedImage[ tileCount ];

      int counter = 0;
      for( int y = 0; y < source.getHeight(); y += tileHeight )
      {
         for( int x = 0; x < source.getWidth(); x += tileWidth )
         {
            tiles[ counter++ ] = source.getSubimage( x, y, tileWidth, tileHeight );
         }
      }
      return tiles;
   }
}
