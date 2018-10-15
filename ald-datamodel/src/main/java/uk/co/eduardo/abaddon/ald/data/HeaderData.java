package uk.co.eduardo.abaddon.ald.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import uk.co.eduardo.abaddon.graphics.layer.Direction;
import uk.co.eduardo.abaddon.util.Coordinate;
import uk.co.eduardo.map.sections.HeaderSection;

/**
 * Wrapper for the header data being currently edited. the {@link HeaderSection} is immutable and this is a mutable version from
 * which a {@link HeaderSection} can be created.
 *
 * @author Ed
 */
public class HeaderData implements FileSectionAdaptor
{
   private static final String DefaultMapName = "newMap"; //$NON-NLS-1$

   private static final String DefaultTileset = "newTiles"; //$NON-NLS-1$

   private static final Coordinate DefaultPosition = new Coordinate( 0, 0 );

   private static final int DefaultLayerIndex = 0;

   private static final Direction DefaultDirection = Direction.DOWN;

   private final List< HeaderDataListener > listeners = new CopyOnWriteArrayList<>();

   private String mapName;

   private String tilesetName;

   private Coordinate startPosition;

   private int layerIndex;

   private Direction direction;

   /**
    * Creates a new Header data.
    * <p>
    * if the <code>section</code> is <code>null</code> then a default data is initialized.
    *
    * @param section the section from which to initialize the data.
    */
   public HeaderData( final HeaderSection section )
   {
      this.mapName = section == null ? DefaultMapName : section.getMapName();
      this.tilesetName = section == null ? DefaultTileset : section.getTilesetName();
      this.startPosition = section == null ? DefaultPosition : section.getStartPos();
      this.layerIndex = section == null ? DefaultLayerIndex : section.getLayerIndex();
      this.direction = section == null ? DefaultDirection : section.getDirection();
   }

   /**
    * Adds a listener that will be notified when the header data is updated.
    *
    * @param listener the listener to add.
    */
   public void addHeaderDataListener( final HeaderDataListener listener )
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
   public void removeHeaderDataListener( final HeaderDataListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * @return the unique name for the map.
    */
   public String getMapName()
   {
      return this.mapName;
   }

   /**
    * @param mapName the name for the map.
    */
   public void setMapName( final String mapName )
   {
      this.mapName = mapName;
   }

   /**
    * @return the tilesetName
    */
   public String getTilesetName()
   {
      return this.tilesetName;
   }

   /**
    * @param tilesetName the tilesetName to set
    */
   public void setTilesetName( final String tilesetName )
   {
      if( ( tilesetName != null ) && !this.tilesetName.equals( tilesetName ) )
      {
         this.tilesetName = tilesetName;
         fireHeaderDataChanged();
      }
   }

   /**
    * @return the startPosition
    */
   public Coordinate getStartPosition()
   {
      return this.startPosition;
   }

   /**
    * @param startPosition the startPosition to set
    */
   public void setStartPosition( final Coordinate startPosition )
   {
      if( ( startPosition != null ) && !this.startPosition.equals( startPosition ) )
      {
         this.startPosition = startPosition;
         fireHeaderDataChanged();
      }
   }

   /**
    * @return the layer index of the player character.
    */
   public int getLayerIndex()
   {
      return this.layerIndex;
   }

   /**
    * @param layerIndex the layer index to set on the player character.
    */
   public void setLayerIndex( final int layerIndex )
   {
      if( this.layerIndex != layerIndex )
      {
         this.layerIndex = layerIndex;
         fireHeaderDataChanged();
      }
   }

   /**
    * @return the direction in which the character will initially face.
    */
   public Direction getDirection()
   {
      return this.direction;
   }

   /**
    * @param direction the direction in which the character will initially face.
    */
   public void setDirection( final Direction direction )
   {
      if( ( direction != null ) && !this.direction.equals( direction ) )
      {
         this.direction = direction;
         fireHeaderDataChanged();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public HeaderSection createFileSection()
   {
      return new HeaderSection( this.mapName, this.tilesetName, this.startPosition, this.layerIndex, this.direction );
   }

   private void fireHeaderDataChanged()
   {
      for( final HeaderDataListener listener : this.listeners )
      {
         listener.headerChanged();
      }
   }

   /**
    * Notification that the header data has changed.
    */
   public static interface HeaderDataListener
   {
      /**
       * Notification that the header data has changed.
       */
      void headerChanged();
   }
}
