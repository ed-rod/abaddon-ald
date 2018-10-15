package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.util.ArrayList;
import java.util.List;

import uk.co.eduardo.abaddon.ald.data.NpcData;
import uk.co.eduardo.abaddon.ald.data.adapter.NpcAdapter;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Provides NPC interactive elements.
 *
 * @author Ed
 */
public class NpcInteractiveElementProvider extends AbstractInteractiveElementProvider
{
   private NpcData npcData;

   private ProjectSettings settings;

   /**
    * {@inheritDoc}
    */
   @Override
   public InteractiveElement[] getElements( final Coordinate tile, final int width, final int height )
   {
      if( this.npcData == null )
      {
         return new InteractiveElement[ 0 ];
      }
      final List< InteractiveElement > elements = new ArrayList<>();
      final int x = tile.x;
      final int y = tile.y;
      for( final NpcAdapter npc : this.npcData.getNpcs() )
      {
         if( ( npc.getPosition().x >= x ) && ( npc.getPosition().x < ( x + width ) ) && ( npc.getPosition().y >= y ) &&
             ( npc.getPosition().y < ( y + height ) ) )
         {
            elements.add( new NpcInteractiveElement( npc, this.settings ) );
         }
      }
      return elements.toArray( new InteractiveElement[ 0 ] );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void initialize( final PropertyModel currentModel )
   {
      this.npcData = currentModel.get( Properties.NpcData );
      this.settings = currentModel.get( Properties.ProjectSettings );
   }
}
