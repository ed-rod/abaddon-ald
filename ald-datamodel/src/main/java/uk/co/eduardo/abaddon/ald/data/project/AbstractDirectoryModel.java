package uk.co.eduardo.abaddon.ald.data.project;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.eduardo.abaddon.ald.data.utils.DirectoryWatcher;

/**
 * Manages the list of files that are currently available within a directory.
 *
 * @author Ed
 */
abstract class AbstractDirectoryModel
{
   private final File rootDir;

   private final FileFilter filter;

   private final List< File > files = new ArrayList<>();

   private final List< File > readOnlyFiles = Collections.unmodifiableList( this.files );

   private final List< AvailableFileListener > listeners = new CopyOnWriteArrayList<>();

   private final ChangeListener changeListener = new ChangeListener()
   {
      @Override
      public void stateChanged( final ChangeEvent e )
      {
         rescan();
      }
   };

   /**
    * Initializes a model for the available files.
    *
    * @param rootDir the project root directory.
    * @param dirName the name of the subdirectory within the project directory.
    * @param fileSuffixes the file suffixes to check within that directory.
    */
   protected AbstractDirectoryModel( final File rootDir, final String dirName, final String... fileSuffixes )
   {
      if( ( rootDir == null ) || !rootDir.exists() || !rootDir.isDirectory() )
      {
         throw new IllegalArgumentException( "rootDir must be a directory" ); //$NON-NLS-1$
      }
      final File subDir = new File( rootDir, dirName );
      if( subDir.exists() && !subDir.isDirectory() )
      {
         throw new IllegalArgumentException( "Subdirectory already exists but is not a directory!" ); //$NON-NLS-1$
      }
      if( !subDir.exists() )
      {
         if( !subDir.mkdirs() )
         {
            throw new IllegalStateException( "Failed to create the subdirectory" ); //$NON-NLS-1$
         }
      }

      this.rootDir = subDir;
      this.filter = new FileFilter()
      {
         @Override
         public boolean accept( final File pathname )
         {
            for( final String suffix : fileSuffixes )
            {
               if( pathname.isFile() && pathname.getName().toLowerCase().endsWith( suffix ) )
               {
                  return true;
               }
            }
            return false;
         }
      };

      // TODO: Memory leak. This listener is added but never removed.
      DirectoryWatcher.getInstance().addListener( this.rootDir, this.changeListener );
   }

   /**
    * Adds a listener that will be notified whenever a file is added or removed from the file system.
    *
    * @param listener the listener to add.
    */
   public void addAvailableFileListener( final AvailableFileListener listener )
   {
      if( ( listener != null ) && !this.listeners.contains( listener ) )
      {
         this.listeners.add( listener );
      }
   }

   /**
    * Removes a listener.
    *
    * @param listener the listener to remove.
    */
   public void removeAvailableFileListener( final AvailableFileListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * Gets a read-only list of available files.
    *
    * @return the list of available files.
    */
   protected List< File > getAvailableFiles()
   {
      return this.readOnlyFiles;
   }

   /**
    * @return the directory in which all the files are stored.
    */
   public File getDirectory()
   {
      return this.rootDir;
   }

   protected void rescan()
   {
      final Set< File > addedFiles = new HashSet<>();
      final Set< File > removedFiles = new HashSet<>( this.files );

      for( final File file : this.rootDir.listFiles( this.filter ) )
      {
         if( accept( file ) )
         {
            if( !this.files.contains( file ) )
            {
               addedFiles.add( file );
            }
            else
            {
               removedFiles.remove( file );
            }
         }
      }

      if( addedFiles.size() > 0 )
      {
         for( final File file : addedFiles )
         {
            this.files.add( file );
            fireFileAdded( file );
         }
      }
      if( removedFiles.size() > 0 )
      {
         for( final File file : removedFiles )
         {
            this.files.remove( file );
            fireFileRemoved( file );
         }
      }
   }

   protected boolean accept( @SuppressWarnings( "unused" ) final File file )
   {
      return true;
   }

   protected void fireFileAdded( final File file )
   {
      for( final AvailableFileListener listener : this.listeners )
      {
         listener.fileAdded( file );
      }
   }

   protected void fireFileRemoved( final File file )
   {
      for( final AvailableFileListener listener : this.listeners )
      {
         listener.fileRemoved( file );
      }
   }
}
