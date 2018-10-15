package uk.co.eduardo.abaddon.ald.ui;

/**
 * Value text fields for Doubles
 *
 * @author Ed
 */
public class DoubleTextField extends ValueTextField< Double >
{
   /**
    * {@inheritDoc}
    */
   @Override
   protected Double stringToValue( final String string ) throws NumberFormatException
   {
      if( string != null )
      {
         Double.parseDouble( string );
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected String valueToString( final Double value )
   {
      if( value != null )
      {
         Double.toString( value );
      }
      return null;
   }
}
