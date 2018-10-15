package uk.co.eduardo.abaddon.ald.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import uk.co.eduardo.abaddon.ald.data.HeaderData;
import uk.co.eduardo.abaddon.ald.data.TilesetData;
import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapAdapter;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapListener;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.undo.UndoManager;

/**
 * Tileset selector.
 *
 * @author Ed
 */
public class TilesetSelector extends JComboBox< String >
{
   private final OpenMapListener mapListener = new OpenMapAdapter()
   {
      @Override
      public void mapInactivated( final PropertyModel unlockedModel )
      {
         setMapModel( null );
      }

      @Override
      public void mapActivated( final PropertyModel lockedModel )
      {
         setMapModel( lockedModel );
      }
   };

   private final PropertyListener propertyListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel s )
      {
         // The tileset has been changed. Update our selected index.
         updateIndexFromModel();
      }
   };

   private final Project project;

   private PropertyModel mapModel;

   /**
    * Initializes a component that allows selection of tileset.
    *
    * @param project the current project
    */
   public TilesetSelector( final Project project )
   {
      super( new DefaultComboBoxModel<>( project.getAvailableTilesetsModel().getAvailableTilesetNames().toArray( new String[ 0 ] ) ) );
      this.project = project;

      setFocusable( false );
      addItemListener( new ItemListener()
      {
         @Override
         public void itemStateChanged( final ItemEvent e )
         {
            updateModelFromIndex();
         }
      } );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      super.addNotify();
      this.project.getOpenMapsModel().addMapModelListener( this.mapListener );
      setMapModel( this.project.getOpenMapsModel().getActiveMap() );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      this.project.getOpenMapsModel().removeMapModelListener( this.mapListener );
      super.removeNotify();
   }

   private void setMapModel( final PropertyModel mapModel )
   {
      if( mapModel == this.mapModel )
      {
         return;
      }

      if( this.mapModel != null )
      {
         this.mapModel.removePropertyListener( Properties.Tileset, this.propertyListener );
      }
      this.mapModel = mapModel;
      if( this.mapModel != null )
      {
         this.mapModel.addPropertyListener( Properties.Tileset, this.propertyListener );
         updateIndexFromModel();
      }
      updateEnabled();
   }

   private void updateIndexFromModel()
   {
      if( this.mapModel != null )
      {
         final TilesetData tilesetData = this.mapModel.get( Properties.Tileset );
         if( tilesetData != null )
         {
            setSelectedItem( tilesetData.getTilesetName().toLowerCase() );
         }
         else
         {
            setSelectedIndex( -1 );
         }
      }
   }

   private void updateModelFromIndex()
   {
      if( this.mapModel != null )
      {
         final String newName = (String) getSelectedItem();
         if( newName != null )
         {
            final TilesetData newTileset = new TilesetData( this.project.getAvailableTilesetsModel().getDirectory(),
                                                            newName,
                                                            this.project.getSettings() );

            // See if a current tileset exists
            final TilesetData oldTileset = this.mapModel.get( Properties.Tileset );
            final HeaderData headerData = this.mapModel.get( Properties.HeaderData );
            if( ( oldTileset != null ) && ( headerData != null ) )
            {
               final String oldName = oldTileset.getTilesetName().toLowerCase();
               if( !oldName.equals( newName ) )
               {
                  // Only change if necessary
                  this.mapModel.set( Properties.Tileset, newTileset );
                  headerData.setTilesetName( newName );

                  // Add an undoable to the undo manager
                  final UndoManager undoManager = this.mapModel.get( Properties.UndoManager );
                  undoManager.addEdit( new ChangeTilesetUndoableEdit( this.mapModel,
                                                                      headerData,
                                                                      oldName,
                                                                      newName,
                                                                      oldTileset,
                                                                      newTileset ) );
               }
            }
            else
            {
               throw new IllegalStateException( "Cannot find tileset + " + newName ); //$NON-NLS-1$
            }
         }
      }
   }

   private void updateEnabled()
   {
      setEnabled( this.mapModel != null );
   }

   private static final class ChangeTilesetUndoableEdit extends AbstractUndoableEdit
   {
      private final PropertyModel model;

      private final HeaderData header;

      private final String oldName;

      private final String newName;

      private final TilesetData oldTileset;

      private final TilesetData newTileset;

      private ChangeTilesetUndoableEdit( final PropertyModel model,
                                         final HeaderData header,
                                         final String oldName,
                                         final String newName,
                                         final TilesetData oldTileset,
                                         final TilesetData newTileset )
      {
         this.model = model;
         this.header = header;
         this.oldName = oldName;
         this.newName = newName;
         this.oldTileset = oldTileset;
         this.newTileset = newTileset;
      }

      @Override
      public void undo() throws CannotUndoException
      {
         super.undo();

         this.header.setTilesetName( this.oldName );
         this.model.set( Properties.Tileset, this.oldTileset );
      }

      @Override
      public void redo() throws CannotRedoException
      {
         super.redo();

         this.header.setTilesetName( this.newName );
         this.model.set( Properties.Tileset, this.newTileset );
      }
   }
}
