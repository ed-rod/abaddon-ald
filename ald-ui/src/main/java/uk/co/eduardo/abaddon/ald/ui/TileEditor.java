package uk.co.eduardo.abaddon.ald.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import uk.co.eduardo.abaddon.tileset.TileDescription;

/**
 * Editor for a specific tile.
 * <p>
 * User interacts with this component to specify which parts of the tile are blocked.
 *
 * @author Ed
 */
public class TileEditor extends JComponent
{
   /**
    * Property for the type of tile.
    */
   public static final String TILE_EDITOR_TYPE = "TileEditor.type"; //$NON-NLS-1$

   private static final Stroke HOVER_STROKE = new BasicStroke( 2f );

   private static final int W = 4;

   private static final Cursor NORMAL_CURSOR = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );

   private static final Cursor HOVER_CURSOR = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );

   private static final Dimension MIN_DIMENSION = new Dimension( 50, 50 );

   private static final Dimension PREF_DIMENSION = new Dimension( 80, 80 );

   private ThickLine top, bottom, right, left, tlToBrDiag, trToBlDiag;

   private final List< ThickLine > lines = new ArrayList<>();

   private Shape lbTriangle, brTriangle, rtTriangle, tlTriangle;

   private final List< Shape > filledTriangle = new ArrayList<>();

   private int type;

   /**
    * Creates an editor for a tile.
    *
    * @param type the type of tile. This is a composition of bitmasks specifying which directions are blocked for entry for the
    *           tile.
    */
   public TileEditor( final int type )
   {
      this.type = type;

      final MouseAdapter mouseListener = new MouseAdapter()
      {
         @Override
         public void mouseClicked( final MouseEvent e )
         {
            checkClick( e.getPoint() );
         }

         @Override
         public void mouseMoved( final MouseEvent e )
         {
            checkMove( e.getPoint() );
         }

         @Override
         public void mouseExited( final MouseEvent e )
         {
            checkMove( new Point( -10, -10 ) );
         }

         @Override
         public void mouseEntered( final MouseEvent e )
         {
            checkMove( e.getPoint() );
         }
      };
      addMouseListener( mouseListener );
      addMouseMotionListener( mouseListener );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setBounds( final int x, final int y, final int width, final int height )
   {
      super.setBounds( x, y, width, height );
      updateAll();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void paintComponent( final Graphics g )
   {
      super.paintComponent( g );

      final Graphics2D g2d = (Graphics2D) g;
      // save state.
      final Color oldColor = g2d.getColor();
      final Stroke oldStroke = g2d.getStroke();
      final Object oldHints = g2d.getRenderingHint( RenderingHints.KEY_ANTIALIASING );

      g2d.setColor( Color.white );
      g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      g2d.fillRect( 0, 0, getWidth(), getHeight() );

      // Draw filled background in black
      g2d.setColor( Color.black );
      for( final Shape triangle : this.filledTriangle )
      {
         g2d.fill( triangle );
      }

      // Draw lines
      for( final ThickLine line : this.lines )
      {
         g2d.setColor( line.getColor( this.type ) );
         if( line.isHover() )
         {
            g2d.setStroke( HOVER_STROKE );
            g2d.draw( line );
         }
         g2d.fill( line );
      }

      // restore state
      g2d.setColor( oldColor );
      g2d.setStroke( oldStroke );
      g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, oldHints );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Dimension getMinimumSize()
   {
      return MIN_DIMENSION;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Dimension getPreferredSize()
   {
      return PREF_DIMENSION;
   }

   /**
    * @param type the type to set
    */
   public void setType( final int type )
   {
      if( type != this.type )
      {
         final int oldType = this.type;
         this.type = type;
         firePropertyChange( TILE_EDITOR_TYPE, oldType, this.type );
         updateAll();
         repaint();
      }
   }

   /**
    * @return the type
    */
   public int getType()
   {
      return this.type;
   }

   private void checkClick( final Point point )
   {
      for( final ThickLine line : this.lines )
      {
         if( line.contains( point ) )
         {
            toggleState( line );
         }
      }
   }

   private void checkMove( final Point point )
   {
      Cursor current = NORMAL_CURSOR;
      for( final ThickLine line : this.lines )
      {
         final boolean oldValue = line.isHover();
         final boolean newValue = line.contains( point );
         if( oldValue != newValue )
         {
            line.setHover( newValue );
            repaint( line.getBounds() );
         }
         if( line.isHover() )
         {
            current = HOVER_CURSOR;
         }
      }
      if( getCursor() != current )
      {
         setCursor( current );
      }
   }

   private void toggleState( final ThickLine clicked )
   {
      final int oldType = this.type;
      this.type = this.type ^ clicked.getMask();
      firePropertyChange( TILE_EDITOR_TYPE, oldType, this.type );

      updateFilledTriangles();
      repaint();
   }

   private void updateAll()
   {
      final int width = getWidth();
      final int height = getHeight();

      // Update the lines.
      this.top = new ThickLine( new Point( 0, W ), new Point( width, W ), TileDescription.TOP );
      this.bottom = new ThickLine( new Point( 0, height - W ), new Point( width, height - W ), TileDescription.BOTTOM );
      this.left = new ThickLine( new Point( W, 0 ), new Point( W, height ), TileDescription.LEFT );
      this.right = new ThickLine( new Point( width - W, 0 ), new Point( width - W, height ), TileDescription.RIGHT );
      this.tlToBrDiag = new ThickLine( new Point( W, W ), new Point( width - W, height - W ), TileDescription.TL_BR_DIAG );
      this.trToBlDiag = new ThickLine( new Point( W, height - W ), new Point( width - W, W ), TileDescription.TR_BL_DIAG );

      this.lines.clear();
      this.lines.add( this.tlToBrDiag );
      this.lines.add( this.trToBlDiag );
      this.lines.add( this.top );
      this.lines.add( this.bottom );
      this.lines.add( this.left );
      this.lines.add( this.right );

      // Update the triangles.
      this.lbTriangle = new Polygon( new int[]
      {
         0,
         0,
         width
      }, new int[]
      {
         0,
         height,
         height
      }, 3 );
      this.brTriangle = new Polygon( new int[]
      {
         0,
         width,
         width
      }, new int[]
      {
         height,
         height,
         0
      }, 3 );
      this.rtTriangle = new Polygon( new int[]
      {
         width,
         width,
         0
      }, new int[]
      {
         height,
         0,
         0
      }, 3 );
      this.tlTriangle = new Polygon( new int[]
      {
         width,
         0,
         0
      }, new int[]
      {
         0,
         0,
         height
      }, 3 );

      updateFilledTriangles();
   }

   private void updateFilledTriangles()
   {
      this.filledTriangle.clear();

      final int mask = this.type;
      if( this.left.isSet( mask ) && this.bottom.isSet( mask ) && this.tlToBrDiag.isSet( mask ) )
      {
         this.filledTriangle.add( this.lbTriangle );
      }
      if( this.bottom.isSet( mask ) && this.right.isSet( mask ) && this.trToBlDiag.isSet( mask ) )
      {
         this.filledTriangle.add( this.brTriangle );
      }
      if( this.right.isSet( mask ) && this.top.isSet( mask ) && this.tlToBrDiag.isSet( mask ) )
      {
         this.filledTriangle.add( this.rtTriangle );
      }
      if( this.top.isSet( mask ) && this.left.isSet( mask ) && this.trToBlDiag.isSet( mask ) )
      {
         this.filledTriangle.add( this.tlTriangle );
      }
   }

   private static class ThickLine extends Polygon
   {
      private static final Color NORMAL = new Color( 128, 128, 128, 64 );

      private static final Color SET = new Color( 0, 0, 0 );

      private static final Color NORMAL_HOVER = new Color( 128, 128, 128, 128 );

      private static final Color SET_HOVER = new Color( 32, 32, 32 );

      private final int mask;

      private boolean hover;

      private ThickLine( final Point start, final Point end, final int mask )
      {
         this.mask = mask;
         final Vector vStart = new Vector( start.x, start.y );
         final Vector vEnd = new Vector( end.x, end.y );

         // Direction of the line.
         final Vector dir = vEnd.sub( vStart ).normalize();

         // Get the perpendicular to the line:
         final Vector perpendicular = new Vector( -dir.y, dir.x );

         // Extend the endpoints along the perpendicular by STROKE_RADIUS;
         final Vector p1 = vStart.add( perpendicular.mul( W ) );
         final Vector p2 = vStart.add( perpendicular.mul( -W ) );

         final Vector p3 = vEnd.add( perpendicular.mul( -W ) );
         final Vector p4 = vEnd.add( perpendicular.mul( W ) );

         addPoint( (int) p1.x, (int) p1.y );
         addPoint( (int) p2.x, (int) p2.y );
         addPoint( (int) p3.x, (int) p3.y );
         addPoint( (int) p4.x, (int) p4.y );
      }

      private int getMask()
      {
         return this.mask;
      }

      private boolean isSet( final int type )
      {
         return ( type & this.mask ) != 0;
      }

      private boolean isHover()
      {
         return this.hover;
      }

      private void setHover( final boolean hover )
      {
         this.hover = hover;
      }

      private Color getColor( final int type )
      {
         if( isHover() )
         {
            return isSet( type ) ? SET_HOVER : NORMAL_HOVER;
         }
         return isSet( type ) ? SET : NORMAL;
      }
   }

   /**
    * Very basic 2D vector.
    */
   private static class Vector
   {
      private final double x;

      private final double y;

      private Vector( final double x, final double y )
      {
         this.x = x;
         this.y = y;
      }

      private double dot( final Vector other )
      {
         return ( this.x * other.x ) + ( this.y * other.y );
      }

      private Vector div( final double value )
      {
         return new Vector( this.x / value, this.y / value );
      }

      private Vector mul( final double value )
      {
         return new Vector( this.x * value, this.y * value );
      }

      private Vector add( final Vector other )
      {
         return new Vector( this.x + other.x, this.y + other.y );
      }

      private Vector sub( final Vector other )
      {
         return new Vector( this.x - other.x, this.y - other.y );
      }

      private double length()
      {
         return Math.sqrt( dot( this ) );
      }

      private Vector normalize()
      {
         return div( length() );
      }
   }
}
