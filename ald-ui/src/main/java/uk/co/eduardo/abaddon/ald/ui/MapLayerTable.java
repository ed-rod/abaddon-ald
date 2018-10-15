package uk.co.eduardo.abaddon.ald.ui;

import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * Table specialization for displaying map layers.
 *
 * @author Ed
 */
public class MapLayerTable extends JTable
{
   private final PropertyListener selectedLayerListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel s )
      {
         final Integer selected = s.get( MapLayerTable.this.selectedLayerProperty );
         MapLayerTable.this.selectionModel.setSelectionInterval( 0, selected );
      }
   };

   private final ListSelectionListener listListener = new ListSelectionListener()
   {
      @Override
      public void valueChanged( final ListSelectionEvent e )
      {
         if( !e.getValueIsAdjusting() )
         {
            final int selected = getSelectedRow();
            if( selected == -1 )
            {
               // Oh no you don't! stay selected.
               MapLayerTable.this.selectionModel.setSelectionInterval( 0, e.getLastIndex() );
            }
            else
            {
               MapLayerTable.this.model.set( MapLayerTable.this.selectedLayerProperty, selected );
            }
         }
      }
   };

   private final PropertyModel model;

   private final Property< Integer > selectedLayerProperty;

   /**
    * @param model the current model.
    * @param layerCountProperty the property for the number of map layers.
    * @param visibleProperty the property for the layer visibility.
    * @param selectedLayerProperty property for the currently selected layer.
    */
   public MapLayerTable( final PropertyModel model,
                         final Property< Integer > layerCountProperty,
                         final Property< Boolean > visibleProperty,
                         final Property< Integer > selectedLayerProperty )
   {
      super( new MapLayerTableModel( model, layerCountProperty, visibleProperty ) );
      this.model = model;
      this.selectedLayerProperty = selectedLayerProperty;

      this.selectionModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
      this.selectionModel.setSelectionInterval( 0, model.get( selectedLayerProperty ) );
      setBorder( BorderFactory.createEtchedBorder() );
      setShowGrid( false );
      initColumns();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      super.addNotify();
      this.selectionModel.addListSelectionListener( this.listListener );
      this.model.addPropertyListener( this.selectedLayerProperty, this.selectedLayerListener );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      this.model.removePropertyListener( this.selectedLayerProperty, this.selectedLayerListener );
      this.selectionModel.removeListSelectionListener( this.listListener );
      super.removeNotify();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setModel( final TableModel dataModel )
   {
      if( dataModel instanceof MapLayerTableModel )
      {
         super.setModel( dataModel );
      }
      else
      {
         throw new IllegalArgumentException( "Can only use MapLayerTableModel" ); //$NON-NLS-1$
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MapLayerTableModel getModel()
   {
      return (MapLayerTableModel) super.getModel();
   }

   private void initColumns()
   {
      final int size = (int) ( getRowHeight() * 1.2f );
      setRowHeight( size );
      for( int col = 0; col < this.columnModel.getColumnCount(); col++ )
      {
         final TableColumn column = this.columnModel.getColumn( col );
         if( getModel().getColumnClass( col ) == Boolean.class )
         {
            column.setPreferredWidth( size );
         }
         else if( getModel().getColumnClass( col ) == String.class )
         {
            column.setPreferredWidth( getPreferredWidth( col ) );
         }
      }
   }

   private int getPreferredWidth( final int col )
   {
      final FontMetrics fontMetrics = getFontMetrics( getFont() );
      double maxWidth = 0;

      for( int row = 0; row < getModel().getRowCount(); row++ )
      {
         final String string = (String) getModel().getValueAt( row, col );
         final double width = fontMetrics.getStringBounds( string, getGraphics() ).getWidth();
         maxWidth = Math.max( maxWidth, width );
      }
      // Add a few pixels for good measure.
      return (int) ( maxWidth + 10 );
   }
}
