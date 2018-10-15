package uk.co.eduardo.abaddon.ald.data.adapter;

import uk.co.eduardo.abaddon.ald.data.mapmodel.Property;
import uk.co.eduardo.abaddon.ald.data.mapmodel.PropertyModel;

abstract class AbstractActionAdapterType implements ActionAdapterType
{
   protected < T > void checkAndSet( final PropertyModel model, final Property< T > property, final T value )
   {
      if( model.contains( property ) )
      {
         model.set( property, value );
      }
      else
      {
         model.add( property, value );
      }
   }
}
