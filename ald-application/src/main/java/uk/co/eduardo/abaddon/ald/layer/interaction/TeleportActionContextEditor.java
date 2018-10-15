package uk.co.eduardo.abaddon.ald.layer.interaction;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import uk.co.eduardo.abaddon.ald.data.adapter.ActionAdapter;
import uk.co.eduardo.abaddon.ald.data.adapter.TeleportActionAdapterType;
import uk.co.eduardo.abaddon.ald.data.project.AvailableMapsModel;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;
import uk.co.eduardo.abaddon.ald.ui.IntegerTextField;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractUndoableAction;
import uk.co.eduardo.abaddon.util.Coordinate;

final class TeleportActionContextEditor extends JPanel
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private final ActionAdapter action;

   TeleportActionContextEditor( final ActionAdapter action )
   {
      super( new BorderLayout() );
      this.action = action;
      createUI();
   }

   private String getDestinationMapName()
   {
      return this.action.getSpecializedProperty( TeleportActionAdapterType.TA_DESTINATION_MAP );
   }

   private Coordinate getDestination()
   {
      return this.action.getSpecializedProperty( TeleportActionAdapterType.TA_DESTINATION );
   }

   private void createUI()
   {
      final FormLayout layout = new FormLayout( "p, $rg, fill:p", "fill:p" ); //$NON-NLS-1$ //$NON-NLS-2$
      final DefaultFormBuilder builder = new DefaultFormBuilder( layout );

      // Add a combo for selecting the map.
      addMapSelector( builder );

      // Add a field for selecting the target X tile coordinate on that map.
      addDestinationX( builder );

      // Add a field for selecting the target Y tile coordinate on that map.
      addDestinationY( builder );

      this.add( builder.getPanel() );
   }

   private void addMapSelector( final DefaultFormBuilder builder )
   {
      final Project project = ProjectManager.getInstance().getLockedProject();
      final AvailableMapsModel model = project.getAvailableMapsModel();

      final JLabel mapLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.interactive.action.teleport.map.label" ) ); //$NON-NLS-1$
      final JComboBox< File > mapCombo = new JComboBox<>( model.getAvailableMapFiles().toArray( new File[ 0 ] ) );
      mapCombo.setRenderer( new FileComboRenderer() );
      mapCombo.setMaximumRowCount( 6 );
      mapCombo.setSelectedItem( model.getMapFile( getDestinationMapName() ) );
      mapCombo.addActionListener( new ChangeDestinationMapAction( this.action, mapCombo ) );
      builder.append( mapLabel );
      builder.append( mapCombo );
   }

   private void addDestinationX( final DefaultFormBuilder builder )
   {
      final JLabel destXLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.interactive.action.teleport.map.x" ) ); //$NON-NLS-1$
      final IntegerTextField textField = new IntegerTextField( false );
      final ChangeDestinationCoordinateAction commitAction = new ChangeDestinationCoordinateAction( this.action,
                                                                                                    textField,
                                                                                                    Type.X );

      textField.setValue( getDestination().x );
      textField.addFocusListener( new FocusAdapter()
      {
         @Override
         public void focusLost( final FocusEvent e )
         {
            // Commit the change
            commitAction.actionPerformed( null );
         }
      } );
      builder.append( destXLabel );
      builder.append( textField );
   }

   private void addDestinationY( final DefaultFormBuilder builder )
   {
      final JLabel destYLabel = new JLabel( resources.getString( "uk.co.eduardo.abaddon.interactive.action.teleport.map.y" ) ); //$NON-NLS-1$
      final IntegerTextField textField = new IntegerTextField( false );
      textField.setValue( getDestination().y );
      final ChangeDestinationCoordinateAction commitAction = new ChangeDestinationCoordinateAction( this.action,
                                                                                                    textField,
                                                                                                    Type.Y );

      textField.addFocusListener( new FocusAdapter()
      {
         @Override
         public void focusLost( final FocusEvent e )
         {
            // Commit the change
            commitAction.actionPerformed( null );
         }
      } );
      builder.append( destYLabel );
      builder.append( textField );
   }

   private static final class FileComboRenderer implements ListCellRenderer< File >
   {
      private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

      @Override
      public Component getListCellRendererComponent( final JList< ? extends File > list,
                                                     final File value,
                                                     final int index,
                                                     final boolean isSelected,
                                                     final boolean cellHasFocus )
      {
         final JLabel comp = (JLabel) this.renderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
         comp.setText( value.getName() );
         return comp;
      }
   }

   private static final class ChangeDestinationMapAction extends AbstractUndoableAction
   {
      private final JComboBox< File > combo;

      private final ActionAdapter action;

      private ChangeDestinationMapAction( final ActionAdapter action, final JComboBox< File > combo )
      {
         super( resources, "uk.co.eduardo.abaddon.interactive.action.teleport.map" ); //$NON-NLS-1$
         this.action = action;
         this.combo = combo;
      }

      @Override
      protected UndoableEdit performAction( final ActionEvent event )
      {
         final File selectedMapFile = (File) this.combo.getSelectedItem();
         final String oldDestinationMap = getMapName();
         final String newDestinationMap = AvailableMapsModel.getMapName( selectedMapFile );
         setMapName( newDestinationMap );

         return new ChangeDestinationMapUndoableEdit( this.action, oldDestinationMap, newDestinationMap );
      }

      private String getMapName()
      {
         return this.action.getSpecializedProperty( TeleportActionAdapterType.TA_DESTINATION_MAP );
      }

      private void setMapName( final String mapName )
      {
         this.action.setSpecializedProperty( TeleportActionAdapterType.TA_DESTINATION_MAP, mapName );
      }
   }

   private static final class ChangeDestinationMapUndoableEdit extends AbstractUndoableEdit
   {
      private final ActionAdapter action;

      private final String oldDestinationMap;

      private final String newDestinationMap;

      private ChangeDestinationMapUndoableEdit( final ActionAdapter action,
                                                final String oldDestinationMap,
                                                final String newDestinationMap )
      {
         this.action = action;
         this.oldDestinationMap = oldDestinationMap;
         this.newDestinationMap = newDestinationMap;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         setMapName( this.oldDestinationMap );
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         setMapName( this.newDestinationMap );
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.action.teleport.map.name" ); //$NON-NLS-1$
      }

      private void setMapName( final String mapName )
      {
         this.action.setSpecializedProperty( TeleportActionAdapterType.TA_DESTINATION_MAP, mapName );
      }
   }

   private static enum Type
   {
      X,
      Y;
   }

   private static final class ChangeDestinationCoordinateAction extends AbstractUndoableAction
   {

      private final Type type;

      private final ActionAdapter action;

      private final IntegerTextField field;

      private ChangeDestinationCoordinateAction( final ActionAdapter action, final IntegerTextField field, final Type type )
      {
         super( resources, "uk.co.eduardo.abaddon.interactive.action.teleport.destination" ); //$NON-NLS-1$
         this.action = action;
         this.field = field;
         this.type = type;
      }

      @Override
      protected UndoableEdit performAction( final ActionEvent event )
      {
         final Integer value = this.field.getValue();
         if( value != null )
         {
            final Coordinate oldDestination = getDestination();
            final Coordinate newDestination;
            switch( this.type )
            {
               case X:
                  newDestination = new Coordinate( value, oldDestination.y );
                  break;

               default: // Y:
                  newDestination = new Coordinate( oldDestination.x, value );
            }
            if( !oldDestination.equals( newDestination ) )
            {
               setDestination( newDestination );
               return new ChangeDestinationCoordinateUndoableEdit( this.action, oldDestination, newDestination );
            }
         }
         return null;
      }

      private Coordinate getDestination()
      {
         return this.action.getSpecializedProperty( TeleportActionAdapterType.TA_DESTINATION );
      }

      private void setDestination( final Coordinate destination )
      {
         this.action.setSpecializedProperty( TeleportActionAdapterType.TA_DESTINATION, destination );
      }
   }

   private static final class ChangeDestinationCoordinateUndoableEdit extends AbstractUndoableEdit
   {
      private final ActionAdapter action;

      private final Coordinate oldDestination;

      private final Coordinate newDestination;

      private ChangeDestinationCoordinateUndoableEdit( final ActionAdapter action,
                                                       final Coordinate oldDestination,
                                                       final Coordinate newDestination )
      {
         this.action = action;
         this.oldDestination = oldDestination;
         this.newDestination = newDestination;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();
         setDestination( this.oldDestination );
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();
         setDestination( this.newDestination );
      }

      @Override
      public String getPresentationName()
      {
         return resources.getString( "uk.co.eduardo.abaddon.undoable.action.teleport.coord.name" ); //$NON-NLS-1$
      }

      private void setDestination( final Coordinate coordinate )
      {
         this.action.setSpecializedProperty( TeleportActionAdapterType.TA_DESTINATION, coordinate );
      }
   }
}