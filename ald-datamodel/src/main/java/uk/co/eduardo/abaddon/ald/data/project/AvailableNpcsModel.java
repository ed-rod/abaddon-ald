package uk.co.eduardo.abaddon.ald.data.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model for the available Non-Player Character files.
 *
 * @author Ed
 */
public class AvailableNpcsModel extends AbstractDirectoryModel
{
   private static final String NPCS_DIR_NAME = "NPCs"; //$NON-NLS-1$

   private static final String NPC_SUFFIX = ".png"; //$NON-NLS-1$

   private static final String NPC_NAME_FORMAT = "npc%d.png"; //$NON-NLS-1$

   private static final Pattern PATTERN = Pattern.compile( "\\d+" ); //$NON-NLS-1$

   private static final Comparator< File > NPC_FILE_COMPARATOR = new Comparator< File >()
   {
      @Override
      public int compare( final File o1, final File o2 )
      {
         final Integer num1 = getType( o1.getName() );
         final Integer num2 = getType( o2.getName() );
         return num1.compareTo( num2 );
      }
   };

   /**
    * Initializes a model for the available Non-Player Character files.
    *
    * @param rootDir the project directory
    */
   public AvailableNpcsModel( final File rootDir )
   {
      super( rootDir, NPCS_DIR_NAME, NPC_SUFFIX );
      rescan();
   }

   /**
    * Gets a (sorted) read-only list of available Non-Player Character files.
    *
    * @return the list of available Non-Player Character files.
    */
   public List< File > getAvailableNpcFiles()
   {
      final List< File > sortedList = new ArrayList<>( getAvailableFiles() );
      Collections.sort( sortedList, NPC_FILE_COMPARATOR );
      return Collections.unmodifiableList( sortedList );
   }

   /**
    * @param type the NPC type identifier.
    * @return whether an NPC with that type exists.
    */
   public boolean exists( final int type )
   {
      return getNpcFile( type ) != null;
   }

   /**
    * Given a name of the form: npc&lt;i&gt;.png, the integer portion, &lt;i&gt;, is extracted.
    *
    * @param name the NPC file name to parse
    * @return the type of the NPC.
    */
   public static int getType( final String name )
   {
      if( name == null )
      {
         throw new IllegalArgumentException( "file cannot be null" ); //$NON-NLS-1$
      }
      final Matcher m = PATTERN.matcher( name );
      if( !m.find() )
      {
         throw new IllegalArgumentException( "name is not a proper NPC formatted name" ); //$NON-NLS-1$
      }
      try
      {
         return Integer.parseInt( m.group() );
      }
      catch( final NumberFormatException exception )
      {
         throw new IllegalArgumentException( "name is not a proper NPC formatted name" ); //$NON-NLS-1$
      }
   }

   /**
    * @param type the NPC id
    * @return the File for the NPC or <code>null</code> if no NPC with that ID exists.
    */
   public File getNpcFile( final int type )
   {
      final String toCheck = String.format( NPC_NAME_FORMAT, type );
      for( final File file : getAvailableNpcFiles() )
      {
         if( file.getName().equalsIgnoreCase( toCheck ) )
         {
            return file;
         }
      }
      return null;
   }
}
