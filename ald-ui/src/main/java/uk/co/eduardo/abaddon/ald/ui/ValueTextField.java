package uk.co.eduardo.abaddon.ald.ui;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * Text field that is restricted to take a numeric type.
 *
 * @author Ed
 * @param <T> the type of the text field.
 */
public abstract class ValueTextField< T extends Number > extends JTextField
{
   private final List< ChangeListener > listeners = new CopyOnWriteArrayList<>();

   private final DocumentListener documentListener;

   /**
    * Initializes a new ValueTextField
    */
   public ValueTextField()
   {
      this.documentListener = createDocumentListener();
      final AbstractDocument document = (AbstractDocument) getDocument();
      document.setDocumentFilter( createDocumentFilter() );
      document.addDocumentListener( this.documentListener );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDocument( final Document doc )
   {
      if( !( doc instanceof AbstractDocument ) )
      {
         throw new IllegalArgumentException( "doc must be an instance of AbstractDocument" ); //$NON-NLS-1$
      }

      // Detach the old document filter.
      final Document old = getDocument();
      if( old != null )
      {
         if( old instanceof AbstractDocument )
         {
            ( (AbstractDocument) old ).setDocumentFilter( null );
         }
         old.removeDocumentListener( this.documentListener );
      }

      // Set the new document.
      super.setDocument( doc );

      // Ensure the new document also has the same document filter
      ( (AbstractDocument) doc ).setDocumentFilter( createDocumentFilter() );
      doc.addDocumentListener( this.documentListener );
   }

   /**
    * @return the number currently in the text field.
    */
   public final T getValue()
   {
      return stringToValue( getText() );
   }

   /**
    * @param value the value to set.
    */
   public final void setValue( final T value )
   {
      setText( valueToString( value ) );
   }

   /**
    * Adds a listener that will be notified whenever the value changes.
    *
    * @param listener the listener to add.
    */
   public void addChangeListener( final ChangeListener listener )
   {
      if( ( listener != null ) && !this.listeners.contains( listener ) )
      {
         this.listeners.add( listener );
      }
   }

   /**
    * @param listener the listener to remove.
    */
   public void removeChangeListener( final ChangeListener listener )
   {
      this.listeners.remove( listener );
   }

   /**
    * @param string the string to convert into a number
    * @return the number for the string.
    * @throws NumberFormatException if the string is not a valid number.
    */
   protected abstract T stringToValue( final String string ) throws NumberFormatException;

   /**
    * @param value the value to convert
    * @return the string representation of the number.
    */
   protected abstract String valueToString( final T value );

   /**
    * Notifies all listeners that the value in the text field has changed.
    */
   protected void fireChangeEvent()
   {
      final ChangeEvent event = new ChangeEvent( this );
      for( final ChangeListener listener : this.listeners )
      {
         listener.stateChanged( event );
      }
   }

   private DocumentFilter createDocumentFilter()
   {
      return new DocumentFilter()
      {
         @Override
         public void insertString( final FilterBypass fb, final int offset, final String string, final AttributeSet attr )
            throws BadLocationException
         {
            String text = fb.getDocument().getText( 0, fb.getDocument().getLength() );
            text = text.substring( 0, offset ) + string + text.substring( offset );
            try
            {
               stringToValue( text );
               super.insertString( fb, offset, string, attr );
            }
            catch( final NumberFormatException exception )
            {
               // ignore edit.
            }
         }

         @Override
         public void remove( final FilterBypass fb, final int offset, final int length ) throws BadLocationException
         {
            String text = fb.getDocument().getText( 0, fb.getDocument().getLength() );
            text = text.substring( 0, offset ) + text.substring( offset + length );
            try
            {
               stringToValue( text );
               super.remove( fb, offset, length );
            }
            catch( final NumberFormatException exception )
            {
               // ignore edit.
            }
         }

         @Override
         public void replace( final FilterBypass fb,
                              final int offset,
                              final int length,
                              final String text,
                              final AttributeSet attrs )
            throws BadLocationException
         {
            String newText = fb.getDocument().getText( 0, fb.getDocument().getLength() );
            newText = newText.substring( 0, offset ) + text + newText.substring( offset + length );
            try
            {
               stringToValue( newText );
               super.replace( fb, offset, length, text, attrs );
            }
            catch( final NumberFormatException exception )
            {
               // ignore edit.
            }
         }
      };
   }

   private DocumentListener createDocumentListener()
   {
      return new DocumentListener()
      {

         @Override
         public void removeUpdate( final DocumentEvent e )
         {
            fireChangeEvent();
         }

         @Override
         public void insertUpdate( final DocumentEvent e )
         {
            fireChangeEvent();
         }

         @Override
         public void changedUpdate( final DocumentEvent e )
         {
            fireChangeEvent();
         }
      };
   }
}
