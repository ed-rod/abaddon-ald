package uk.co.eduardo.abaddon.ald.data.project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Project settings.
 *
 * @author Ed
 */
public class ProjectSettings
{
   /** Key for the width of the tiles in pixels. */
   public static final String TILE_WIDTH = "uk.co.eduardo.abaddon.project.tile.size"; //$NON-NLS-1$

   /** Key for the height of the tiles in pixels. */
   public static final String TILE_HEIGHT = "uk.co.eduardo.abaddon.project.tile.size"; //$NON-NLS-1$

   /** Key for the height of the sprites in pixels. */
   public static final String SPRITE_HEIGHT = "uk.co.eduardo.abaddon.project.sprite.height"; //$NON-NLS-1$

   private static final int DEFAULT_TILE_SIZE = 16;

   private static final int DEFAULT_SPRITE_HEIGHT = 24;

   private final Map< String, Integer > settings = new HashMap<>();

   private final File source;

   /**
    * Initializes project settings with the default values.
    * 
    * @param source the source file from which the properties are to be read.
    */
   public ProjectSettings( final File source )
   {
      this.source = source;
      initDefaults();
      if( ( source != null ) && source.exists() && source.isFile() )
      {
         InputStream stream = null;
         try
         {
            // read from file.
            final Properties properties = new Properties();
            stream = new BufferedInputStream( new FileInputStream( source ) );
            properties.load( stream );
            initFromProperties( properties );
         }
         catch( final IOException e )
         {
            // Ignore and use defaults.
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
                  // Ignore.
               }
            }
         }
      }
   }

   /**
    * @return the file
    */
   public File getSource()
   {
      return this.source;
   }

   /**
    * Gets a value from the project settings.
    *
    * @param key the key for the value.
    * @return the value associated with the key.
    */
   public int get( final String key )
   {
      if( !this.settings.containsKey( key ) )
      {
         throw new IllegalArgumentException( "Key doesn't exist" ); //$NON-NLS-1$
      }
      return this.settings.get( key );
   }

   /**
    * Sets a value for one of the project settings.
    *
    * @param key a valid settings key.
    * @param value the value to set.
    */
   public void set( final String key, final int value )
   {
      if( !this.settings.containsKey( key ) )
      {
         throw new IllegalArgumentException( "Key doesn't exist" ); //$NON-NLS-1$
      }
      this.settings.put( key, value );
   }

   /**
    * @return the keys in the project settings map.
    */
   public Collection< String > getKeys()
   {
      return this.settings.keySet();
   }

   /**
    * Saves the project properties.
    */
   public void save()
   {
      final Properties properties = new Properties();
      for( final Entry< String, Integer > entry : this.settings.entrySet() )
      {
         properties.setProperty( entry.getKey(), Integer.toString( entry.getValue() ) );
      }
      OutputStream stream = null;
      try
      {
         stream = new BufferedOutputStream( new FileOutputStream( this.source ) );
         properties.store( stream, null );
      }
      catch( final IOException exception )
      {
         // TODO: failed to write.
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
               // Nothing we can do at this point.
            }
         }
      }
   }

   private void initDefaults()
   {
      // Currently only square tiles are supported.
      this.settings.put( TILE_HEIGHT, DEFAULT_TILE_SIZE );
      this.settings.put( SPRITE_HEIGHT, DEFAULT_SPRITE_HEIGHT );
   }

   private void initFromProperties( final Properties properties )
   {
      getAndSet( properties, TILE_WIDTH );
      getAndSet( properties, SPRITE_HEIGHT );
   }

   /**
    * @param properties properties from which to extract the value.
    * @param key the key for the value.
    */
   private void getAndSet( final Properties properties, final String key )
   {
      final Object value = properties.get( key );
      if( ( value != null ) && ( value instanceof String ) )
      {
         try
         {
            final int parsed = Integer.parseInt( (String) value );
            this.settings.put( key, parsed );
         }
         catch( final NumberFormatException exception )
         {
            // ignore and use default
         }
      }
   }
}
