package uk.co.eduardo.abaddon.ald.ui;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyListener;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;
import uk.co.eduardo.abaddon.util.Coordinate;

/**
 * Displays the current tile coordinate.
 *
 * @author Ed
 */
public class TilePositionDisplay extends JTextField
{
   private final PropertyListener positionListener = new PropertyListener()
   {
      @Override
      public void propertyChanged( final PropertyModel s )
      {
         updateText();
      }
   };

   private final PropertyModel model;

   private final Property< Coordinate > locationProperty;

   /**
    * @param model the current model.
    * @param locationProperty the property for the current mouse tile position.
    */
   public TilePositionDisplay( final PropertyModel model, final Property< Coordinate > locationProperty )
   {
      super( 8 );
      setEditable( false );
      setHorizontalAlignment( SwingConstants.CENTER );
      this.model = model;
      this.locationProperty = locationProperty;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      super.addNotify();
      this.model.addPropertyListener( this.locationProperty, this.positionListener );
      updateText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final void setEditable( final boolean b )
   {
      super.setEditable( false );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final boolean isEditable()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      this.model.removePropertyListener( this.locationProperty, this.positionListener );
      super.removeNotify();
   }

   private void updateText()
   {
      final Coordinate point = this.model.get( this.locationProperty );
      if( point == null )
      {
         setText( "" ); //$NON-NLS-1$
      }
      else
      {
         setText( String.format( "(%d, %d)", point.x, point.y ) ); //$NON-NLS-1$
      }
   }
}
