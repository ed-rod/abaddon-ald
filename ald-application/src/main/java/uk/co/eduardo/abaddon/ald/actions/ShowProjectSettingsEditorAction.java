package uk.co.eduardo.abaddon.ald.actions;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.ui.ProjectSettingsEditor;
import uk.co.eduardo.abaddon.ald.ui.WindowAncestorUtilities;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractItemAction;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractProjectAction;

/**
 * Action to display the project settings editor dialog.
 *
 * @author Ed
 */
public class ShowProjectSettingsEditorAction extends AbstractProjectAction
{
   private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

   /**
    * Initializes an action to display the project settings editor.
    */
   public ShowProjectSettingsEditorAction()
   {
      super( resources, "" ); //$NON-NLS-1$
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void actionPerformed( final ActionEvent e )
   {
      new EditorDialog( WindowAncestorUtilities.getWindow( e ), getProject() ).setVisible( true );
   }

   private static class EditorDialog extends JDialog
   {
      private final ProjectSettingsEditor editor;

      private EditorDialog( final Window parent, final Project project )
      {
         super( parent );
         this.editor = new ProjectSettingsEditor( project );

         setTitle( resources.getString( "uk.co.eduardo.abaddon.project.editor.title" ) ); //$NON-NLS-1$
         setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
         setModal( true );
         createUI();
      }

      @Override
      public void addNotify()
      {
         super.addNotify();
         pack();
         setLocationRelativeTo( getParent() );
      }

      private void createUI()
      {
         final JLabel label = new JLabel( resources.getString( "uk.co.eduardo.abaddon.project.editor.hint" ) ); //$NON-NLS-1$
         final JButton okButton = new JButton( new OkAction() );
         final JButton cancelButton = new JButton( new CancelAction() );
         final JPanel buttonBar = ButtonBarFactory.buildRightAlignedBar( okButton, cancelButton );
         getRootPane().setDefaultButton( okButton );

         final FormLayout layout = new FormLayout( "p:grow" ); //$NON-NLS-1$
         final DefaultFormBuilder builder = new DefaultFormBuilder( layout );

         builder.setDefaultDialogBorder();
         builder.append( label );
         builder.append( Box.createVerticalStrut( 10 ) );
         builder.appendRow( "fill:p:grow" ); //$NON-NLS-1$
         builder.append( this.editor );
         builder.appendSeparator();
         builder.append( buttonBar );

         setContentPane( builder.getPanel() );
      }

      private class OkAction extends AbstractItemAction
      {
         private OkAction()
         {
            super( resources, "uk.co.eduardo.abaddon.action.generic.ok" ); //$NON-NLS-1$
         }

         @Override
         public void actionPerformed( final ActionEvent e )
         {
            EditorDialog.this.editor.applyChanges();
            dispose();
         }
      }

      private class CancelAction extends AbstractItemAction
      {
         private CancelAction()
         {
            super( resources, "uk.co.eduardo.abaddon.action.generic.cancel" ); //$NON-NLS-1$
         }

         @Override
         public void actionPerformed( final ActionEvent e )
         {
            dispose();
         }
      }
   }
}
