package uk.co.eduardo.abaddon.ald.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;

/**
 * Editor component for project settings.
 *
 * @author Ed
 */
public class ProjectSettingsEditor extends JScrollPane
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String CONFIRM_MESSAGE = resources.getString( "uk.co.eduardo.abaddon.confirm.project.reload.message" ); //$NON-NLS-1$

   private static final String CONFIRM_TITLE = resources.getString( "uk.co.eduardo.abaddon.confirm.project.reload.title" ); //$NON-NLS-1$

   private final Map< String, Integer > settings = new HashMap<>();

   private final Project project;

   /**
    * Initializes a component to edit the project.
    *
    * @param project the project to edit.
    */
   public ProjectSettingsEditor( final Project project )
   {
      this.project = project;
      if( project == null )
      {
         throw new NullPointerException( "project cannot be null" ); //$NON-NLS-1$
      }

      // Take a copy.
      for( final String key : project.getSettings().getKeys() )
      {
         this.settings.put( key, project.getSettings().get( key ) );
      }
      final JTable view = new JTable( new EditorTableModel() );
      view.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
      view.setPreferredScrollableViewportSize( view.getPreferredSize() );
      setViewportView( view );
   }

   /**
    * Apply the changes to the project.
    */
   public void applyChanges()
   {
      // First confirm the changes.
      if( JOptionPane.showConfirmDialog( null,
                                         CONFIRM_MESSAGE,
                                         CONFIRM_TITLE,
                                         JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION )
      {
         for( final Entry< String, Integer > entry : this.settings.entrySet() )
         {
            this.project.getSettings().set( entry.getKey(), entry.getValue() );
         }
         this.project.getSettings().save();

         final File projectRoot = this.project.getRootDirectory();

         // Close the current project.
         ProjectManager.getInstance().unlockProject();

         final Project reopened = new Project( projectRoot );
         ProjectManager.getInstance().lockProject( reopened );
      }
   }

   // private void setColumnWidths( final JTable table )
   // {
   // // set each column to be 20% bigger than the longest item in the column.
   // for( int col = 0; col < table.getColumnModel().getColumnCount(); col++ )
   // {
   // final List< String > items = getColumnItems( table, col );
   // final int maxWidth = getMaxWidth( table, items );
   // final int colWidth = (int) ( maxWidth * 1.2f );
   //
   // table.getColumnModel().getColumn( col ).setWidth( colWidth );
   // }
   // }
   //
   // private List< String > getColumnItems( final JTable table, final int col )
   // {
   // final List< String > items = new ArrayList< String >();
   // for( int row = 0; row < table.getModel().getRowCount(); row++ )
   // {
   // items.add( table.getModel().getValueAt( row, col ).toString() );
   // }
   // return items;
   // }
   //
   // private int getMaxWidth( final JTable table, final List< String > items )
   // {
   // int width = 0;
   // for( final String item : items )
   // {
   // width = Math.max( width, table.getFontMetrics( table.getFont() ).stringWidth( item ) );
   // }
   // return width;
   // }

   private final class EditorTableModel extends AbstractTableModel
   {
      private static final int KEY_COL = 0;

      private static final int VALUE_COL = 1;

      private static final int COLUMNS_COUNT = 2;

      private final List< String > keys;

      private EditorTableModel()
      {
         this.keys = new ArrayList<>( ProjectSettingsEditor.this.settings.keySet() );
         Collections.sort( this.keys );
      }

      @Override
      public int getRowCount()
      {
         return this.keys.size();
      }

      @Override
      public int getColumnCount()
      {
         return COLUMNS_COUNT;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getColumnName( final int column )
      {
         switch( column )
         {
            case KEY_COL:
               return resources.getString( "uk.co.eduardo.abaddon.project.editor.key.title" ); //$NON-NLS-1$

            case VALUE_COL:
               return resources.getString( "uk.co.eduardo.abaddon.project.editor.value.title" ); //$NON-NLS-1$

            default:
               throw new IllegalStateException( "Unknown column" ); //$NON-NLS-1$
         }
      }

      @Override
      public boolean isCellEditable( final int rowIndex, final int columnIndex )
      {
         return columnIndex == VALUE_COL;
      }

      @Override
      public Class< ? > getColumnClass( final int columnIndex )
      {
         switch( columnIndex )
         {
            case KEY_COL:
               return String.class;

            case VALUE_COL:
               return Integer.class;

            default:
               throw new IllegalStateException( "Unknown column" ); //$NON-NLS-1$
         }
      }

      @Override
      public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex )
      {
         switch( columnIndex )
         {
            case VALUE_COL:
               if( ( aValue != null ) && ( aValue instanceof Integer ) )
               {
                  ProjectSettingsEditor.this.settings.put( this.keys.get( rowIndex ), (Integer) aValue );
               }
               break;

            default:
               throw new IllegalStateException( "Unmodifiable column!" ); //$NON-NLS-1$
         }
      }

      @Override
      public Object getValueAt( final int rowIndex, final int columnIndex )
      {
         switch( columnIndex )
         {
            case KEY_COL:
               return resources.getString( this.keys.get( rowIndex ) );

            case VALUE_COL:
               return ProjectSettingsEditor.this.settings.get( this.keys.get( rowIndex ) );

            default:
               throw new IllegalStateException( "Unmodifiable column!" ); //$NON-NLS-1$
         }
      }
   }
}
