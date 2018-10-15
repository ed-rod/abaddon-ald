package uk.co.eduardo.abaddon.ald.layer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.ald.data.NpcData;
import uk.co.eduardo.abaddon.ald.data.NpcDataListener;
import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.adapter.NpcAdapter;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.ProjectSettings;
import uk.co.eduardo.abaddon.ald.sprite.SpriteUtilities;

/**
 * A layer to draw NPCs on screen.
 *
 * @author Ed
 */
public class NpcDisplayLayer extends SpriteDisplaylayer
{
   private final NpcData npcData;

   private final NpcDataListener npcListener = new NpcDataListener()
   {
      @Override
      public void npcUpdated( final NpcAdapter npc )
      {
         updateMapArea( npc );
      }

      @Override
      public void npcRemoved( final NpcAdapter npc )
      {
         updateMapArea( npc );
      }

      @Override
      public void npcAdded( final NpcAdapter npc )
      {
         updateMapArea( npc );
      }
   };

   private final Property< ProjectSettings > settingsProperty;

   /**
    * Initializes a layer that displays all NPCs that exist on the map layer <code>layerIndex</code>.
    *
    * @param model the current model.
    * @param host the host for the layer.
    * @param layerIndex the index of the map layer.
    * @param tilesetProperty property for the currently selected tileset.
    * @param visibleProperty property that determines whether to draw the NPCs or not.
    * @param npcDataProperty property for the NPC data.
    * @param settingsProperty property for the current project settings.
    */
   public NpcDisplayLayer( final PropertyModel model,
                           final JComponent host,
                           final int layerIndex,
                           final Property< TilesetData > tilesetProperty,
                           final Property< Boolean > visibleProperty,
                           final Property< NpcData > npcDataProperty,
                           final Property< ProjectSettings > settingsProperty )
   {
      super( model, host, layerIndex, tilesetProperty, visibleProperty );
      this.settingsProperty = settingsProperty;
      this.npcData = model.get( npcDataProperty );
      this.npcData.addNpcDataListener( this.npcListener );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void paint( final Graphics2D g2d )
   {
      if( !isVisible() )
      {
         return;
      }
      if( ( getTilesetData() == null ) || ( getNpcData() == null ) )
      {
         return;
      }
      for( final NpcAdapter npc : getNpcData().getNpcs() )
      {
         if( npc.getLayerIndex() == getLayerIndex() )
         {
            final ProjectSettings settings = getModel().get( this.settingsProperty );
            final BufferedImage image = SpriteUtilities.getSpriteImage( npc, settings );
            drawSprite( g2d, image, npc.getPosition() );
         }
      }
   }

   private NpcData getNpcData()
   {
      return this.npcData;
   }

   private void updateMapArea( final NpcAdapter npc )
   {
      final ProjectSettings settings = getModel().get( this.settingsProperty );
      final Rectangle rect = getDisplayPixelBounds( SpriteUtilities.getSpriteImage( npc, settings ), npc.getPosition() );
      getHost().repaint( rect );
   }
}
