import java.awt.* ;
import javax.swing.*;

@SuppressWarnings("serial")
public class APropos extends JDialog {

	//Créer la boîte de dialogue "A propos"
	public APropos(PostIt p) {
		initComponents();
		//Définir l'apparence et l'emplacement
		setTitle("À propos de Post-it");
		setIconImage(new ImageIcon(getClass().getResource("postit.png")).getImage());
		setLocationRelativeTo(p);
	}

	//Créer les composants
	private void initComponents() {
		jLabel1 = new JLabel();
		jLabel2 = new JLabel();
		jLabel3 = new JLabel();
		jLabel4 = new JLabel();

		//*******************************************************************************
		//======================Informations à mettre à jour=============================
		//*******************************************************************************
		jLabel1.setText("Post-it est développé par 6pi.");

		jLabel2.setText("Pour toutes informations, suggestions ou rapport de beug,");

		jLabel3.setText("contactez-moi par mail à 6pi.prog@free.fr,");

		jLabel4.setText("merci d'indiquer [Post-it] dans l'objet de votre mail.");
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		 
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(jLabel1, gbc);
		 
		gbc.gridy = 1;
		add(jLabel2, gbc);
		 
		gbc.gridy = 2;
		add(jLabel3, gbc);
		 
		gbc.gridy = 3;
		add(jLabel4, gbc);
		
		//Optimiser les dimensions
		pack();		
	}
                    
	private JLabel jLabel1, jLabel2, jLabel3, jLabel4;
}