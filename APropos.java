import java.awt.*;
import java.awt.event.* ;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.text.*;

@SuppressWarnings("serial")
public class APropos extends JDialog {

	
	//Créer la boîte de dialogue "A propos"
	public APropos(PostIt p) {
		initComponents();
		this.postit = p;
		//Définir l'apparence et l'emplacement
		this.setTitle("À propos de Memento");
		this.setIconImage(new ImageIcon(getClass().getResource("img/icone.png")).getImage());
		this.setLocationRelativeTo(p);
	}

	//Créer les composants
	private void initComponents() {
		
		text 			= new JTextArea();
		button_licence 	= new JButton();
		button_close 	= new JButton();
		logo			= new LogoComponent();
		
		//Ajout des actions listeners
		button_licence.addActionListener(new ButtonLicenceActionListener());
		button_close.addActionListener(new ButtonCloseActionListener(this));
		
        
		logo.setPreferredSize(new Dimension(128,128));
		
		text.setText("Version : 1.0b \n" +
				"\n" +
				"Copyright © 2008-2011 Sipieter Clément \n" +
				"Copyright © 2011 Sellem Lev-Arcady"
				);
		text.setBackground(this.getBackground());
		
		button_licence.setText("Licence");
		button_close.setText("Fermer");
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;

			
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		this.add(logo, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth 	= 3;
		gbc.insets = new Insets(20,20,20,20);
		this.add(text, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(5,5,20,5);

		this.add(button_licence, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 2;
		this.add(button_close, gbc);
		this.pack();
		 

	}
                    
	private JTextArea text;
	private JButton button_licence, button_close;
	private LogoComponent logo;
	private PostIt postit;

	
	/* **********************************************************************************
	 * 		PRIVATES CLASS
	 * **********************************************************************************/
	 
	
	private class LogoComponent extends JPanel{
		
		Image logo;
		
		public LogoComponent() {
			logo= new ImageIcon(getClass().getResource("img/logo.png")).getImage();
		}
	
		public void paint(Graphics g){
			g.drawImage(logo, 0, 0, this);
		}
	}
	
	private class ButtonLicenceActionListener implements ActionListener{
		
		private String licence_text;
		

		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			licence_text = new String( 
					"This program is free software: you can redistribute it and/or modify\n"+
					"it under the terms of the GNU General Public License as published by\n"+
					"the Free Software Foundation, either version 3 of the License, or\n"+
					"(at your option) any later version.\n"+
					"\n"+
					"This program is distributed in the hope that it will be useful,\n"+
					"but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
					"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
					"GNU General Public License for more details.\n"+
					"\n"+
					"You should have received a copy of the GNU General Public License\n"+
					"along with this program.  If not, see <http://www.gnu.org/licenses/>.\n"+
					"\n\n\n"
			);
									
			try{
				InputStream ips = new FileInputStream(getClass().getResource("COPYING").getPath()); 
				InputStreamReader ipsr = new InputStreamReader(ips);
				BufferedReader br = new BufferedReader(ipsr);
				
				String line;
				while ((line=br.readLine())!=null){
					licence_text+=line+'\n';
				}
				br.close(); 
			}		
			catch (Exception e1){
			}

			Dimension dimension = new Dimension(600, 400);
			
			JDialog licence = new JDialog(postit, "Licence");

			JTextPane text_pane = new JTextPane();
			text_pane.setEditable(false);
			text_pane.setPreferredSize(dimension);
			text_pane.setSize(dimension);
			
		    StyledDocument doc = text_pane.getStyledDocument();
			
		    Style justified = doc.addStyle("justified", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
			StyleConstants.setAlignment(justified, StyleConstants.ALIGN_JUSTIFIED);
		   				    
		    try{
				doc.insertString(0,licence_text,justified);
			} catch (BadLocationException ble) {
	            System.err.println("Couldn't insert initial text into text pane.");
	        }
			Style logicalStyle = doc.getLogicalStyle(0);  
			doc.setParagraphAttributes(0, licence_text.length(), justified, false);  
			doc.setLogicalStyle(0, logicalStyle);
			

	        JScrollPane paneScrollPane = new JScrollPane(text_pane);
			paneScrollPane.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			paneScrollPane.setPreferredSize(dimension);
			paneScrollPane.setMinimumSize(dimension);

			JPanel pan = new JPanel();
			LayoutManager layout = new BorderLayout();
			pan.setLayout(layout);
			
			pan.add(new JScrollPane(paneScrollPane), BorderLayout.CENTER);
			JButton close = new JButton("Fermer");
			close.addActionListener(new ButtonCloseActionListener(licence));
			JPanel button_panel = new JPanel();
			FlowLayout button_panel_layout = new FlowLayout(FlowLayout.RIGHT,20,20);
			button_panel.setLayout(button_panel_layout);
			
			button_panel.add(close);
			pan.add(button_panel, BorderLayout.SOUTH);
			
			licence.add(pan);
			
			licence.pack();
			licence.setLocationRelativeTo(postit);
			licence.setVisible(true);
			
			
		}
	}
	
	private class ButtonCloseActionListener implements ActionListener{

		private JDialog window;
		
		public ButtonCloseActionListener(JDialog window){
			this.window = window;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			window.dispose();
		}
		
		
	}
}