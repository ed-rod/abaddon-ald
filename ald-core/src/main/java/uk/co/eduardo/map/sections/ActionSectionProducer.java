package uk.co.eduardo.map.sections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import uk.co.eduardo.abaddon.map.actions.ChangeLayerAction;
import uk.co.eduardo.abaddon.map.actions.MapAction;
import uk.co.eduardo.abaddon.map.actions.TeleportAction;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Reads/writes an {@link ActionSection}.
 *
 * @author Ed
 */
public class ActionSectionProducer extends AbstractFileSectionProducer< ActionSection >
{
   // ================| Constructors |====================================

   /**
    * Default constructor.
    */
   public ActionSectionProducer()
   {
      super( ActionSection.class );
   }

   // ================| Public Methods |====================================

   /**
    * {@inheritDoc}
    */
   @Override
   public ActionSection readSection( final DataInputStream stream ) throws IOException
   {
      final int actionCount = stream.readShort();

      final MapAction[] readActions = new MapAction[ actionCount ];

      for( int action = 0; action < actionCount; action++ )
      {
         // Read the type of action this specifies
         final int actionType = stream.readShort();

         // Read the X and Y tile coordinates of the action trigger
         final Coordinate source = new Coordinate( stream.readShort(), stream.readShort() );

         if( actionType == TeleportAction.TELEPORT_ACTION_TYPE )
         {
            // Read the name of the map that is the teleport destination.
            final String name = readString( stream );

            // Read the X and Y tile coordinates to where to teleport in the new map.
            final Coordinate target = new Coordinate( stream.readShort(), stream.readShort() );

            readActions[ action ] = new TeleportAction( source, target, name );
         }
         else if( actionType == ChangeLayerAction.CHANGE_LAYER_TYPE )
         {
            // Read the target layer index of the player character should the action be performed.
            final int layerIndex = stream.readShort();

            readActions[ action ] = new ChangeLayerAction( source, layerIndex );
         }
      }
      return new ActionSection( readActions );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeSection( final DataOutputStream stream, final ActionSection section ) throws IOException
   {
      stream.writeShort( section.getActionCount() );

      for( int actionIndex = 0; actionIndex < section.getActionCount(); actionIndex++ )
      {
         final MapAction action = section.getAction( actionIndex );
         // Write the type of action
         stream.writeShort( action.getActionType() );

         // Write out the position of the action trigger
         stream.writeShort( action.getSource().x );
         stream.writeShort( action.getSource().y );

         if( action.getActionType() == TeleportAction.TELEPORT_ACTION_TYPE )
         {
            final TeleportAction ta = (TeleportAction) action;

            // Write out the name of the teleport destination map.
            writeString( stream, ta.getDestinationMapName() );

            // Write out the destination coordinate in the teleport destination map.
            stream.writeShort( ta.getDestination().x );
            stream.writeShort( ta.getDestination().y );
         }
         else if( action.getActionType() == ChangeLayerAction.CHANGE_LAYER_TYPE )
         {
            final ChangeLayerAction cla = (ChangeLayerAction) action;

            // Write the layer index of the the player character after triggering the action
            stream.writeShort( cla.getLayerIndex() );
         }
      }
   }
}
