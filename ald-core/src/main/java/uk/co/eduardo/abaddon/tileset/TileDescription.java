package uk.co.eduardo.abaddon.tileset;

/**
 * Describes which tiles can be walked on, from which direction each tile can be entered from end exitited to and whcih tiles are
 * animated.
 *
 * @author Ed
 */
public class TileDescription
{
   // ================| Fields |====================================

   /** The tile is blocked for entry/exit on the right */
   public final static int RIGHT = 0x00000001;

   /** The tile is blocked for entry/exit on the left */
   public final static int LEFT = 0x00000002;

   /** The tile is blocked for entry/exit on the top */
   public final static int TOP = 0x00000004;

   /** The tile is blocked for entry/exit on the bottom */
   public final static int BOTTOM = 0x00000008;

   /** The tile is blocked along the top-left to bottom-right diagonal */
   public final static int TL_BR_DIAG = 0x00000010;

   /** The tile is blocked along the top-right to bottom-left diagonal */
   public final static int TR_BL_DIAG = 0x00000020;

   /** The list of walkable tiles. Composition of the previous masks */
   public final int[] walkable;

   /** The list of animated tile indices */
   public final int[] animated;

   // ================| Constructors |====================================

   /**
    * @param walkable the array of walkable tile information.
    * @param animated the array of animated tile information.
    */
   public TileDescription( final int[] walkable, final int[] animated )
   {
      this.walkable = walkable;
      this.animated = animated;
   }
}
