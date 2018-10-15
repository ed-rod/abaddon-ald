package uk.co.eduardo.map.sections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import uk.co.eduardo.abaddon.graphics.layer.Direction;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Reads/writes a {@link HeaderSection}
 *
 * @author Ed
 */
public class HeaderSectionProducer extends AbstractFileSectionProducer< HeaderSection >
{
   // ================| Constructors |====================================

   /**
    * Default Constructor.
    */
   public HeaderSectionProducer()
   {
      super( HeaderSection.class );
   }

   // ================| Public Methods |====================================

   /**
    * {@inheritDoc}
    */
   @Override
   public HeaderSection readSection( final DataInputStream stream ) throws IOException
   {
      final String mapName = readString( stream );
      final String tilesetName = readString( stream );
      final Coordinate startPos = new Coordinate( stream.readShort(), stream.readShort() );
      final int layerIndex = stream.readShort();
      final Direction startingDirection = Direction.fromOffset( stream.readShort() );
      return new HeaderSection( mapName, tilesetName, startPos, layerIndex, startingDirection );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeSection( final DataOutputStream stream, final HeaderSection section ) throws IOException
   {
      writeString( stream, section.getMapName() );
      writeString( stream, section.getTilesetName() );
      stream.writeShort( section.getStartPos().x );
      stream.writeShort( section.getStartPos().y );
      stream.writeShort( section.getLayerIndex() );
      stream.writeShort( section.getDirection().offset );
   }
}
