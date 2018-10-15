package uk.co.eduardo.abaddon.ald.ui;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Text field that performs validation.
 *
 * @author Ed
 */
public final class FileTextField extends JTextField
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   // At least on NTFS!!
   private static final char[] DISALLOWED =
   {
      '\\',
      '/',
      ':',
      '*',
      '?',
      '"',
      '<',
      '>',
      '|'
   };

   private static final String EMPTY = resources.getString( "uk.co.eduardo.abaddon.file.text.validate.empty" ); //$NON-NLS-1$

   private static final String START_SPACE = resources.getString( "uk.co.eduardo.abaddon.file.text.validate.start.space" ); //$NON-NLS-1$

   private static final String END_SPACE = resources.getString( "uk.co.eduardo.abaddon.file.text.validate.end.space" ); //$NON-NLS-1$

   private static final String INVALID_CHAR_FORMAT = resources.getString( "uk.co.eduardo.abaddon.file.text.validate.invalid.char" ); //$NON-NLS-1$

   private static final String ALREADY_EXISTS = resources.getString( "uk.co.eduardo.abaddon.file.text.validate.already.exists" ); //$NON-NLS-1$

   private final List< FileTextFieldListener > listeners = new CopyOnWriteArrayList<>();

   private final DocumentListener documentListener = new DocumentListener()
   {
      @Override
      public void insertUpdate( final DocumentEvent e )
      {
         validateName();
      }

      @Override
      public void removeUpdate( final DocumentEvent e )
      {
         validateName();
      }

      @Override
      public void changedUpdate( final DocumentEvent e )
      {
         validateName();
      }
   };

   private final List< String > disallowed;

   private final String extension;

   /**
    * @param initial the initial file name
    * @param disallowed list of file names that are disallowed
    * @param extension the extension of the file to be created.
    */
   public FileTextField( final String initial, final List< String > disallowed, final String extension )
   {
      super( initial );
      this.disallowed = disallowed;
      this.extension = extension;
      validateName();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addNotify()
   {
      super.addNotify();
      getDocument().addDocumentListener( this.documentListener );
      validateName();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeNotify()
   {
      getDocument().removeDocumentListener( this.documentListener );
      super.removeNotify();
   }

   /**
    * Adds a listener that will be notified of any changes to the validity of the name entered.
    *
    * @param listener the listener to add.
    */
   public void addFileTextFieldListener( final FileTextFieldListener listener )
   {
      if( ( listener != null ) && !this.listeners.contains( listener ) )
      {
         this.listeners.add( listener );
      }
   }

   /**
    * Removes a listener.
    *
    * @param listener the listener to remove.
    */
   public void removeFileTextFieldListener( final FileTextFieldListener listener )
   {
      this.listeners.remove( listener );
   }

   private void validateName()
   {
      final String name = getText();
      if( ( name == null ) || name.isEmpty() )
      {
         fireNameInvalid( EMPTY );
         return;
      }
      if( name.startsWith( " " ) ) //$NON-NLS-1$
      {
         fireNameInvalid( START_SPACE );
         return;
      }
      if( name.endsWith( " " ) ) //$NON-NLS-1$
      {
         fireNameInvalid( END_SPACE );
         return;
      }
      for( final char invalid : DISALLOWED )
      {
         if( name.indexOf( invalid ) >= 0 )
         {
            final String formatted = MessageFormat.format( INVALID_CHAR_FORMAT, new Object[]
            {
               invalid
            } );
            fireNameInvalid( formatted );
            return;
         }
      }
      for( final String existing : this.disallowed )
      {
         if( existing.toLowerCase().equals( name.toLowerCase() ) )
         {
            fireNameInvalid( ALREADY_EXISTS );
            return;
         }
         // Also check that for the extension
         if( existing.toLowerCase().equals( ( name + this.extension ).toLowerCase() ) )
         {
            fireNameInvalid( ALREADY_EXISTS );
            return;
         }
      }
      fireNameValid();
   }

   private void fireNameInvalid( final String message )
   {
      for( final FileTextFieldListener listener : this.listeners )
      {
         listener.nameChanged( false, message );
      }
   }

   private void fireNameValid()
   {
      for( final FileTextFieldListener listener : this.listeners )
      {
         listener.nameChanged( true, "" ); //$NON-NLS-1$
      }
   }

   /**
    * Listener interface for notification of changes to the {@link FileTextField}.
    */
   public interface FileTextFieldListener
   {
      /**
       * @param isValid whether the new name is valid.
       * @param message if the name is not valid then this contains a user-readable description of why the name is invalid.
       */
      void nameChanged( boolean isValid, String message );
   }
}