package uk.co.eduardo.abaddon.ald.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Properties;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapAdapter;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapListener;
import uk.co.eduardo.abaddon.ald.data.project.OpenMapModel;
import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.layer.MapPanel;

/**
 * Map editor where each map is edited in a separate tab.
 *
 * @author Ed
 */
public class MapEditor extends JTabbedPane
{
   private final OpenMapListener mapListener = new OpenMapAdapter()
   {
      @Override
      public void mapActivated( final PropertyModel activeMap )
      {
         switchTo( activeMap );
      }

      @Override
      public void mapOpened( final PropertyModel opened )
      {
         createTab( opened );
      }

      @Override
      public void mapClosed( final PropertyModel closed )
      {
         closeTab( closed );
      }
   };

   private final PropertyListener saveListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel mapModel )
      {
         updateSaveState( mapModel, mapModel.get( Properties.UncommittedChanges ) );
      }
   };

   private final Map< PropertyModel, Component > createdTabs = new HashMap<>();

   private final OpenMapModel openMapsModel;

   /**
    * Initializes a new tabbed editor for a project.
    *
    * @param project the project
    */
   public MapEditor( final Project project )
   {
      this.openMapsModel = project.getOpenMapsModel();
      addChangeListener( new ChangeListener()
      {
         @Override
         public void stateChanged( final ChangeEvent e )
         {
            // Lock the currently selected model
            final PropertyModel map = getMapFor( getSelectedComponent() );
            if( map != null )
            {
               MapEditor.this.openMapsModel.setActiveMap( map );
            }
         }
      } );
      updateState();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      super.addNotify();
      this.openMapsModel.addMapModelListener( this.mapListener );

      updateState();
   }

   private void updateState()
   {
      // For each open map check to see that a tab has been created.
      final List< PropertyModel > openMaps = new ArrayList<>( this.openMapsModel.getOpenMaps() );

      for( final PropertyModel openMap : openMaps )
      {
         final Component existingTab = getComponentFor( openMap );
         if( existingTab == null )
         {
            createTab( openMap );
         }
      }

      // Close any tabs that are no longer open
      final Set< PropertyModel > keySet = new HashSet<>( this.createdTabs.keySet() );
      for( final PropertyModel createdMap : keySet )
      {
         if( !openMaps.contains( createdMap ) )
         {
            closeTab( createdMap );
         }
      }
      setVisible( getTabCount() > 0 );

      // Switch to the active tab
      switchTo( this.openMapsModel.getActiveMap() );
   }

   private void switchTo( final PropertyModel map )
   {
      if( map == null )
      {
         return;
      }
      final Component mapComponent = getComponentFor( map );
      final Component currentComponent = getSelectedComponent();
      if( mapComponent != currentComponent )
      {
         setSelectedComponent( mapComponent );
      }
   }

   private void createTab( final PropertyModel map )
   {
      if( map == null )
      {
         return;
      }
      if( !this.createdTabs.containsKey( map ) )
      {
         // Add a listener for uncommitted changes
         map.addPropertyListener( Properties.UncommittedChanges, this.saveListener );

         final Component tabComponent = createComponentFor( map );
         addTab( map.get( Properties.MapName ), tabComponent );
         this.createdTabs.put( map, tabComponent );
         setVisible( true );
      }
   }

   private void closeTab( final PropertyModel map )
   {
      if( map == null )
      {
         return;
      }
      if( this.createdTabs.containsKey( map ) )
      {
         map.removePropertyListener( Properties.UncommittedChanges, this.saveListener );

         final int index = indexOfComponent( getComponentFor( map ) );
         if( index != -1 )
         {
            removeTabAt( index );
         }
         this.createdTabs.remove( map );
         if( getTabCount() == 0 )
         {
            setVisible( false );
         }
      }
   }

   private Component createComponentFor( final PropertyModel map )
   {
      final JPanel tab = new JPanel( new BorderLayout() );
      final MapPanel panel = new MapPanelAdapter( map ).initialize();
      final JScrollPane scrollPane = new JScrollPane( panel );
      tab.add( scrollPane );
      return tab;
   }

   private Component getComponentFor( final PropertyModel map )
   {
      return this.createdTabs.get( map );
   }

   private PropertyModel getMapFor( final Component component )
   {
      for( final Entry< PropertyModel, Component > entry : this.createdTabs.entrySet() )
      {
         if( entry.getValue() == component )
         {
            return entry.getKey();
         }
      }
      return null;
   }

   private void updateSaveState( final PropertyModel map, final boolean changed )
   {
      final String name = map.get( Properties.MapName );
      final String modifiedName = name + '*';

      final int index = indexOfComponent( getComponentFor( map ) );
      if( index != -1 )
      {
         setTitleAt( index, changed ? modifiedName : name );
      }
   }
}
