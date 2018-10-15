package uk.co.eduardo.abaddon.ald.utils;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.Sizes;
import com.jgoodies.forms.util.LayoutStyle;

import uk.co.eduardo.abaddon.ald.data.project.Project;
import uk.co.eduardo.abaddon.ald.data.project.ProjectManager;
import uk.co.eduardo.abaddon.ald.ui.TilesetAnimatedEditor;
import uk.co.eduardo.abaddon.ald.ui.TilesetWalkDirectionEditor;
import uk.co.eduardo.abaddon.ald.ui.action.AbstractItemAction;
import uk.co.eduardo.abaddon.tileset.TileDescription;
import uk.co.eduardo.abaddon.tileset.TileDescriptionReader;

/**
 * Utility methods for opening the tileset editor
 *
 * @author Ed
 */
public final class TilesetUtilities
{
   private TilesetUtilities()
   {
      // hide constructor for utility class.
   }

   /**
    * Opens an editor dialog for the specified tileset file.
    *
    * @param tileset the tileset file to edit.
    * @param parent the parent window for the editor dialog.
    */
   public static void openEditor( final File tileset, final Window parent )
   {
      final TilesetDialog dialog = new TilesetDialog( parent, tileset );
      dialog.setVisible( true );
   }

   private static class TilesetDialog extends JDialog
   {
      private static final ResourceBundle resources = ResourceBundle.getBundle( "ALD" ); //$NON-NLS-1$

      private static final String WALK_EDITOR = resources.getString( "uk.co.eduardo.abaddon.tileset.editor.dialog.walk" ); //$NON-NLS-1$

      private static final String ANIMATED_EDITOR = resources.getString( "uk.co.eduardo.abaddon.tileset.editor.dialog.animated" ); //$NON-NLS-1$

      private final File tilesetFile;

      private final TilesetWalkDirectionEditor walkEditor;

      private final TilesetAnimatedEditor animatedEditor;

      private TilesetDialog( final Window parent, final File tileset )
      {
         super( parent );
         this.tilesetFile = tileset;

         final Project project = ProjectManager.getInstance().getLockedProject();
         final File tilesetDirectory = project.getAvailableTilesetsModel().getDirectory();
         this.walkEditor = new TilesetWalkDirectionEditor( tilesetDirectory, this.tilesetFile.getName(), project.getSettings() );

         this.animatedEditor = new TilesetAnimatedEditor( tilesetDirectory, this.tilesetFile.getName(), project.getSettings() );

         setTitle( resources.getString( "uk.co.eduardo.abaddon.tileset.editor.dialog.title" ) ); //$NON-NLS-1$
         setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
         createUI();
         setModal( true );
         pack();
         setLocationRelativeTo( parent );
      }

      private void createUI()
      {
         final JButton okButton = new JButton( new OkAction() );
         final JButton cancelButton = new JButton( new CancelAction() );
         final JPanel buttonBar = ButtonBarFactory.buildRightAlignedBar( okButton, cancelButton );

         // create the card panel to show either the animated, or the walk editors
         final JPanel cardPanel = new JPanel();
         final CardLayout cardLayout = new CardLayout();
         cardPanel.setLayout( cardLayout );
         cardPanel.add( this.walkEditor, WALK_EDITOR );
         cardPanel.add( this.animatedEditor, ANIMATED_EDITOR );

         final JComboBox< String > comboBox = new JComboBox<>( new String[]
         {
            WALK_EDITOR,
            ANIMATED_EDITOR
         } );
         comboBox.addItemListener( new ItemListener()
         {
            @Override
            public void itemStateChanged( final ItemEvent e )
            {
               cardLayout.show( cardPanel, (String) comboBox.getSelectedItem() );
            }
         } );
         final JPanel comboPanel = new JPanel( new FlowLayout( FlowLayout.LEADING ) );
         comboPanel.setBorder( Borders.createEmptyBorder( LayoutStyle.getCurrent().getDialogMarginY(),
                                                          LayoutStyle.getCurrent().getDialogMarginX(),
                                                          Sizes.ZERO,
                                                          Sizes.ZERO ) );
         comboPanel.add( comboBox );

         final FormLayout layout = new FormLayout( "p:grow", //$NON-NLS-1$
                                                   "p:grow, $ug, p, p" ); //$NON-NLS-1$
         final DefaultFormBuilder builder = new DefaultFormBuilder( layout );
         builder.setDefaultDialogBorder();
         builder.append( cardPanel );
         builder.appendSeparator();
         builder.append( buttonBar );

         getContentPane().add( builder.getPanel(), BorderLayout.CENTER );
         getContentPane().add( comboPanel, BorderLayout.PAGE_START );

         getRootPane().setDefaultButton( okButton );
      }

      private final class OkAction extends AbstractItemAction
      {
         private OkAction()
         {
            super( resources, "uk.co.eduardo.abaddon.action.generic.ok" ); //$NON-NLS-1$
         }

         @Override
         public void actionPerformed( final ActionEvent e )
         {
            // Save changes to the tileset and dismiss.
            OutputStream stream = null;
            try
            {
               stream = new BufferedOutputStream( new FileOutputStream( TilesetDialog.this.tilesetFile ) );
               final TileDescription walkDesc = TilesetDialog.this.walkEditor.getTileDescription();
               final TileDescription animDesc = TilesetDialog.this.animatedEditor.getTileDescription();

               TileDescriptionReader.writeStream( walkDesc.walkable, animDesc.animated, stream );
            }
            catch( final IOException exception )
            {
               // TODO: warning
               exception.printStackTrace();
            }
            finally
            {
               if( stream != null )
               {
                  try
                  {
                     stream.close();
                  }
                  catch( final IOException e1 )
                  {
                     // Ignore.
                  }
               }
            }
            dispose();
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
            dispose();
         }
      }
   }
}
