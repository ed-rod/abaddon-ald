package uk.co.eduardo.abaddon.ald.ui;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

/**
 * Table model for the map layer visibility table.
 *
 * @author Ed
 */
public class MapLayerTableModel extends AbstractTableModel
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String LAYERS_FORMAT = resources.getString( "uk.co.eduardo.abaddon.layers.table.format" ); //$NON-NLS-1$

   private static final int COLUMNS = 2;

   private static final int VISIBLE_COL = 0;

   private static final int LAYER_COL = 1;

   private final Property< Integer > layerCountProperty;

   private final Property< Boolean > visibleProperty;

   private final PropertyModel model;

   /**
    * Initializes a new table model.
    *
    * @param model the current model.
    * @param layerCountProperty the property for the number of map layers.
    * @param visibleProperty the property for the layer visibility.
    */
   public MapLayerTableModel( final PropertyModel model,
                              final Property< Integer > layerCountProperty,
                              final Property< Boolean > visibleProperty )
   {
      this.model = model;
      this.layerCountProperty = layerCountProperty;
      this.visibleProperty = visibleProperty;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getRowCount()
   {
      return this.model.get( this.layerCountProperty );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getColumnCount()
   {
      return COLUMNS;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isCellEditable( final int rowIndex, final int columnIndex )
   {
      return columnIndex == VISIBLE_COL;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class< ? > getColumnClass( final int columnIndex )
   {
      switch( columnIndex )
      {
         case VISIBLE_COL:
            return Boolean.class;

         case LAYER_COL:
            return String.class;

         default:
            throw new IllegalStateException( "Invalid column" ); //$NON-NLS-1$
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object getValueAt( final int rowIndex, final int columnIndex )
   {
      switch( columnIndex )
      {
         case VISIBLE_COL:
            return this.model.get( Property.getLayerProperty( rowIndex, this.visibleProperty ) );

         case LAYER_COL:
            return MessageFormat.format( LAYERS_FORMAT, new Object[]
            {
               rowIndex +
              1
            } );

         default:
            throw new IllegalStateException( "Invalid column" ); //$NON-NLS-1$
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex )
   {
      switch( columnIndex )
      {
         case VISIBLE_COL:
            final Property< Boolean > property = Property.getLayerProperty( rowIndex, this.visibleProperty );
            this.model.set( property, !this.model.get( property ) );
            break;

         default:
            throw new IllegalStateException( "Column is not editable" ); //$NON-NLS-1$
      }
   }
}
