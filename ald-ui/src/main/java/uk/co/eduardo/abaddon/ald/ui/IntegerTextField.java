package uk.co.eduardo.abaddon.ald.ui;

/**
 * Value text field for Integers.
 *
 * @author Ed
 */
public class IntegerTextField extends ValueTextField< Integer >
{
   private final boolean allowNegativeValues;

   /**
    * Initializes a new {@link IntegerTextField} that allows negative numbers.
    */
   public IntegerTextField()
   {
      this( true );
   }

   /**
    * Initializes a new {@link IntegerTextField}.
    *
    * @param allowNegativeValues whether negative numbers are allowed.
    */
   public IntegerTextField( final boolean allowNegativeValues )
   {
      this.allowNegativeValues = allowNegativeValues;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Integer stringToValue( final String string ) throws NumberFormatException
   {
      if( string != null )
      {
         final int parsed = Integer.parseInt( string );
         if( !this.allowNegativeValues && ( parsed < 0 ) )
         {
            throw new NumberFormatException( "Negative values not allowed" ); //$NON-NLS-1$
         }
         return parsed;
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected String valueToString( final Integer value )
   {
      if( value != null )
      {
         return Integer.toString( value );
      }
      return null;
   }
}
