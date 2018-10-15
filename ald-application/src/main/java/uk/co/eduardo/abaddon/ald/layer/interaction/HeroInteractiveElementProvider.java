package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.util.ArrayList;
import java.util.List;

import uk.co.eduardo.abaddon.ald.data.HeaderData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Provides the hero interactive element.
 *
 * @author Ed
 */
public class HeroInteractiveElementProvider extends AbstractInteractiveElementProvider
{
   private HeaderData headerData;

   private ProjectSettings settings;

   /**
    * {@inheritDoc}
    */
   @Override
   public InteractiveElement[] getElements( final Coordinate tile, final int width, final int height )
   {
      final List< InteractiveElement > elements = new ArrayList<>();
      final int x = this.headerData.getStartPosition().x;
      final int y = this.headerData.getStartPosition().y;
      if( ( x >= tile.x ) && ( x < ( tile.x + width ) ) && ( y >= tile.y ) && ( y < ( tile.y + height ) ) )
      {
         elements.add( new HeroInteractiveElement( this.headerData, this.settings ) );
      }
      return elements.toArray( new InteractiveElement[ 0 ] );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void initialize( final PropertyModel currentModel )
   {
      this.headerData = currentModel.get( Properties.HeaderData );
      this.settings = currentModel.get( Properties.ProjectSettings );
   }
}
