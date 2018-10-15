package uk.co.eduardo.abaddon.ald.ui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import uk.co.eduardo.abaddon.ald.ui.FileTextField.FileTextFieldListener;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractItemAction;

/**
 *
 * @author Ed
 */
public class InputDialog extends JDialog
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   private static final String DEFAULT_MESSAGE = resources.getString( "uk.co.eduardo.abaddon.file.input.message" ); //$NON-NLS-1$

   private static final String DEFAULT_PREFIX = resources.getString( "uk.co.eduardo.abaddon.file.input.prefix" ); //$NON-NLS-1$

   private boolean accepted = false;

   private FileTextField textField;

   private InputDialog( final Window parent,
                        final String message,
                        final String prefix,
                        final List< String > disallowed,
                        final String extension )
   {
      super( parent );

      createUI( message, prefix, disallowed, extension );
      setLocationRelativeTo( parent );
   }

   /**
    * @param parent the parent for the dialog. May be <code>null</code>.
    * @param message the message to display.
    * @param prefix the text to display next to the input field.
    * @param disallowed a list of disallowed file names.
    * @param extension the extension of the file to be created.
    * @return the selected file name or <code>null</code> if the dialog was cancelled.
    */
   public static String showFileInputDialog( final Window parent,
                                             final String message,
                                             final String prefix,
                                             final List< String > disallowed,
                                             final String extension )
   {
      final InputDialog dialog = new InputDialog( parent, message, prefix, disallowed, extension );
      dialog.setModal( true );
      dialog.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
      dialog.setVisible( true );
      if( dialog.accepted )
      {
         return dialog.textField.getText();
      }
      return null;
   }

   private void createUI( final String message, final String prefix, final List< String > disallowed, final String extension )
   {
      final JLabel messageLabel = new JLabel( message == null ? DEFAULT_MESSAGE : message );
      final JLabel prefixLabel = new JLabel( prefix == null ? DEFAULT_PREFIX : prefix );
      this.textField = new FileTextField( "", disallowed, extension ); //$NON-NLS-1$
      final JLabel warningLabel = new JLabel();
      final JButton okButton = new JButton( new OkAction( this.textField ) );
      final JButton cancelButton = new JButton( new CancelAction() );
      final JPanel buttonBar = ButtonBarFactory.buildRightAlignedBar( okButton, cancelButton );

      this.textField.addFileTextFieldListener( new FileTextFieldListener()
      {
         @Override
         public void nameChanged( final boolean isValid, final String validMessage )
         {
            warningLabel.setText( validMessage );
         }
      } );
      final FormLayout layout = new FormLayout( "p, $rg, 150dlu:grow", //$NON-NLS-1$
                                                "p, $ug, p, $rg, p, $ug:grow, p, $rg, p" ); //$NON-NLS-1$
      final DefaultFormBuilder builder = new DefaultFormBuilder( layout );
      final CellConstraints cc = new CellConstraints();

      builder.setDefaultDialogBorder();
      builder.add( messageLabel, cc.xyw( 1, 1, 3 ) );
      builder.add( prefixLabel, cc.xy( 1, 3 ) );
      builder.add( this.textField, cc.xy( 3, 3 ) );
      builder.add( warningLabel, cc.xyw( 1, 5, 3 ) );
      builder.addSeparator( "", cc.xyw( 1, 7, 3 ) ); //$NON-NLS-1$
      builder.add( buttonBar, cc.xyw( 1, 9, 3 ) );
      setContentPane( builder.getPanel() );

      getRootPane().setDefaultButton( okButton );
      pack();
   }

   private final class OkAction extends AbstractItemAction implements FileTextFieldListener
   {
      private OkAction( final FileTextField textField )
      {
         super( resources, "uk.co.eduardo.abaddon.action.generic.ok" ); //$NON-NLS-1$
         textField.addFileTextFieldListener( this );
      }

      @Override
      public void actionPerformed( final ActionEvent e )
      {
         InputDialog.this.accepted = true;
         dispose();
      }

      @Override
      public void nameChanged( final boolean isValid, final String message )
      {
         setEnabled( isValid );
      }
   }

   private final class CancelAction extends AbstractItemAction
   {
      private CancelAction()
      {
         super( resources, "uk.co.eduardo.abaddon.action.generic.cancel" ); //$NON-NLS-1$
      }

      @Override
      public void actionPerformed( final ActionEvent e )
      {
         InputDialog.this.accepted = false;
         dispose();
      }
   }
}
