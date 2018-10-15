package uk.co.eduardo.abaddon.ald.utils;

import java.io.File;

import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.ui.MapModelAdapter;

/**
 * Utility methods for opening maps.
 *
 * @author Ed
 */
public class OpenUtilities
{
   private OpenUtilities()
   {
      // Hide constructor for utility class.
   }

   /**
    * @param project the current project
    * @param mapFile the file to open.
    */
   public static void openMap( final Project project, final File mapFile )
   {
      final PropertyModel existing = project.getOpenMapsModel().getMap( mapFile.getName() );
      if( existing != null )
      {
         project.getOpenMapsModel().setActiveMap( existing );
         return;
      }
      final PropertyModel mapModel = new MapModelAdapter( project, mapFile ).initialize();
      project.getOpenMapsModel().openMap( mapModel );
      project.getOpenMapsModel().setActiveMap( mapModel );
   }
}
