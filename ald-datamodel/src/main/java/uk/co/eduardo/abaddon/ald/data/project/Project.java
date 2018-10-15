package uk.co.eduardo.abaddon.ald.data.project;

import java.io.File;

/**
 * A project is a collection of available services.
 *
 * @author Ed
 */
public class Project
{
   private static final String PROJECT_FILE_NAME = "project.cfg"; //$NON-NLS-1$

   private final File rootDir;

   private final ProjectSettings settings;

   private final AvailableMapsModel availableMapsModel;

   private final OpenMapModel mapEditorModel = new OpenMapModel();

   private final AvailableTilesetsModel availableTilesetsModel;

   private final AvailableNpcsModel availableNpcsModel;

   private final AvailablePcsModel availablePcsModel;

   private final AvailableMusicModel availableMusicModel;

   /**
    * Opens the project at the specified directory.
    * <p>
    * If the project directory does not exist then it will be created.
    *
    * @param rootDir the project directory.
    */
   public Project( final File rootDir )
   {
      if( rootDir == null )
      {
         throw new IllegalArgumentException( "Project directory cannot be null" ); //$NON-NLS-1$
      }
      if( rootDir.exists() && !rootDir.isDirectory() )
      {
         throw new IllegalArgumentException( "Project must be a directory" ); //$NON-NLS-1$
      }
      if( !rootDir.exists() )
      {
         // Attempt to create it.
         if( !rootDir.mkdirs() )
         {
            throw new IllegalArgumentException( "Failed to create project directory. Is the path valid?" ); //$NON-NLS-1$
         }
      }
      this.rootDir = rootDir;
      this.settings = getProjectSettings( rootDir );
      final int tileSize = this.settings.get( ProjectSettings.TILE_HEIGHT );

      this.availableMapsModel = new AvailableMapsModel( rootDir );
      this.availableTilesetsModel = new AvailableTilesetsModel( rootDir, tileSize );
      this.availableNpcsModel = new AvailableNpcsModel( rootDir );
      this.availablePcsModel = new AvailablePcsModel( rootDir );
      this.availableMusicModel = new AvailableMusicModel( rootDir );
   }

   /**
    * @return the project root directory.
    */
   public File getRootDirectory()
   {
      return this.rootDir;
   }

   /**
    * @return the settings
    */
   public ProjectSettings getSettings()
   {
      return this.settings;
   }

   /**
    * @return the model for the map files that are present in the project.
    */
   public AvailableMapsModel getAvailableMapsModel()
   {
      return this.availableMapsModel;
   }

   /**
    * @return the model for all the maps currently being edited.
    */
   public OpenMapModel getOpenMapsModel()
   {
      return this.mapEditorModel;
   }

   /**
    * @return the model for the tileset files that are present in the project
    */
   public AvailableTilesetsModel getAvailableTilesetsModel()
   {
      return this.availableTilesetsModel;
   }

   /**
    * @return the model for the available NPC files that are present in the project.
    */
   public AvailableNpcsModel getAvailableNpcsModel()
   {
      return this.availableNpcsModel;
   }

   /**
    * @return the model for the available PC files that are present in the project.
    */
   public AvailablePcsModel getAvailablePcsModel()
   {
      return this.availablePcsModel;
   }

   /**
    * @return the model for the available music files that are present in the project.
    */
   public AvailableMusicModel getAvailableMusicModel()
   {
      return this.availableMusicModel;
   }

   private ProjectSettings getProjectSettings( final File root )
   {
      return new ProjectSettings( new File( root, PROJECT_FILE_NAME ) );
   }
}
