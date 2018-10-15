package uk.co.eduardo.map.sections;

/**
 * Contains the 2D tile grid information from a map file
 *
 * @author Ed
 */
public class MapSection implements FileSection
{
   // ================| Fields |====================================

   /** The maximum number of sparse maps a map file can specify. */
   static final int MAX_SPARSE_MAPS = 2;

   /** The number of sparse layers. */
   private int sparseMapCount;

   /** all the map layers */
   private final int[][][] maps;

   /** The number of tiles wide the maps are. */
   private final int width;

   /** The number of tiles high the maps are. */
   private final int height;

   // ================| Constructors |====================================

   /**
    * @param width width of the map in tiles
    * @param height height of the map in tiles
    * @param maps the tile arrays.
    */
   public MapSection( final int width, final int height, final int[][][] maps )
   {
      this.maps = maps;

      this.height = height;
      this.width = width;
      if( maps != null )
      {
         this.sparseMapCount = maps.length - 1;
      }
      else
      {
         this.sparseMapCount = 0;
      }
   }

   // ================| Public Methods |====================================

   /**
    * @return the map layers defined in this map file. e.g. returning an array with dimensions int[3][20][30] would represent a map
    *         file with 3 map layers each with 20 rows and 30 columns.
    */
   public int[][][] getMaps()
   {
      return this.maps;
   }

   /**
    * @return width of the map in tiles
    */
   public int getWidth()
   {
      return this.width;
   }

   /**
    * @return height of the map in tiles
    */
   public int getHeight()
   {
      return this.height;
   }

   // ================| Default Methods |====================================

   int getSparseMapCount()
   {
      return this.sparseMapCount;
   }
}
