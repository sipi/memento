/*  
 *  Copyright © 2008-2012 Sipieter Clément <c.sipieter@gmail.com>
 *  Copyright © 2011-2012 Sellem Lev-Arcady
 *
 *  This file is part of Memento.
 *
 *  Memento is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Memento is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Memento.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

/*====================================================================================
 *                    Création de la boîte de dialogue
 *====================================================================================*/

/**
 * Boîte de dialogue proposant de définir la taille du mémo et la police de caractères utilisée
 */
@SuppressWarnings("serial")
class Onglets extends JDialog{
	/**
	 * Constructeur de la classe Onglets
	 * @param f La police de caractères courante
	 * @param tail La taille courante
	 * @param cible L'aire de texte du mémo appelant
	 */
	public Onglets(final Font f, int[] tail, JTextArea cible){
		//Créer la boîte
		super((JFrame)null, "Réglages", true);
		//Créer les onglets
		onglets = new JTabbedPane(SwingConstants.TOP);
		polices = new FontChooser(f, this);
		taille = new Taille(tail[0], tail[1], this);
		couleur = new Couleur(cible, this);
		scroll_police = new JScrollPane(polices);
		scroll_size = new JScrollPane(taille);
		scroll_couleur = new JScrollPane(couleur);
		onglets.addTab("Choix de la police", scroll_police);
		onglets.addTab("Dimensions", scroll_size);
		onglets.addTab("Couleurs", scroll_couleur);
		add(onglets);
		//Modifier le comportement pour que la fermeture de la boîte aie le même résultat
		//qu'un clic sur 'Annuler'
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				polices.setter(f);
			}
		});
		//Dimensionner et afficher la boîte
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Récupération des dimensions spécifiées par l'utilisateur
	 * @return Un tableau contenant la taille du mémo
	 */
	public int[] sizeGetter(){
		return taille.getter();
	}

	/**
	 * Récupération de la fonte spécifiée par l'utilisateur
	 * @return Un objet Font contenant les choix de l'utilisateur
	 */
	public Font fontGetter(){
		return polices.getFont();
	}
	
	/**
	 * Récupération des couleurs spécifiées par l'utilisateur
	 * @return Un tableau contenant les choix de l'utilisateur
	 */
	public Color[] colorGetter(){
		return couleur.getter();
	}
	private JTabbedPane onglets; private JScrollPane scroll_police, scroll_size, scroll_couleur;
	private FontChooser polices; private Taille taille; private Couleur couleur;
	
}


/**
 * Panneau permettant le choix de la taille
 */
@SuppressWarnings("serial")
class Taille extends JPanel implements ActionListener{
	/**
	 * Gestion des clics sur les boutons "Valider" et "Annuler"
	 */
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==ok){
			//Faire disparaître la boîte de dialogue sans la détruire
			//pour pouvoir continuer à utiliser getter() de l'extérieur
			appel.setVisible(false);

		}
		else if(e.getSource()==annuler){
			//Remettre les valeurs de départ puis
			//faire disparaître la boîte de dialogue sans la détruire
			//pour pouvoir continuer à utiliser getter() de l'extérieur
			hauteur.setText(String.valueOf(haut));
			largeur.setText(String.valueOf(larg));
			appel.setVisible(false);
		}
	}
	
	/**
	 * Constructeur de la classe Taille
	 * @param haut La hauteur par défaut
	 * @param larg La largeur par défaut
	 * @param appel La boîte de dialogue appelante
	 */
	public Taille(int haut, int larg, JDialog appel){
		//Création et initialisation des composants et variables
		this.haut = haut;
		this.larg = larg;
		this.appel = appel;
		haut_label = new JLabel("Hauteur : ");
		larg_label = new JLabel("Largeur : ");
		hauteur = new JTextField(10);
		hauteur.setText(String.valueOf(haut));
		largeur = new JTextField(10);
		largeur.setText(String.valueOf(larg));
		ok = new JButton("Valider");
		ok.addActionListener(this);
		annuler = new JButton("Annuler");
		annuler.addActionListener(this);

		//Création et remplissage des box

		//Choix de la hauteur
		hor1 = Box.createHorizontalBox();
		hor1.add(haut_label);
		hor1.add(Box.createHorizontalStrut(5));
		hor1.add(hauteur);

		//Choix de la largeur
		hor2 = Box.createHorizontalBox();
		hor2.add(larg_label);
		hor2.add(Box.createHorizontalStrut(5));
		hor2.add(largeur);

		//Boutons pour finir
		hor3 = Box.createHorizontalBox();
		hor3.add(ok);
		hor3.add(Box.createHorizontalStrut(15));
		hor3.add(annuler);

		//Box servant à la mise en page des boutons
		hor4 = Box.createHorizontalBox();
		hor4.add(Box.createGlue());
		hor4.add(hor3);

		//Ajout des box au conteneur global puis ajout de celui-ci au panneau
		global = Box.createVerticalBox();
		global.add(hor1);
		global.add(Box.createVerticalStrut(5));
		global.add(hor2);
		global.add(Box.createVerticalStrut(5));
		global.add(hor4);

		setLayout(new GridBagLayout());

		add(global, new GridBagConstraints (0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets (0,0,0,0), 0, 0));
	}

	/**
	 * Récupération des dimensions choisies par l'utilisateur  
	 * @return Un tableau contenant les choix de l'utilisateur
	 */
	public int[] getter(){
		int[] resultat = new int[2];
		try{
			resultat[0] = Integer.parseInt(hauteur.getText());
			resultat[1] = Integer.parseInt(largeur.getText());
		}
		catch(NumberFormatException e){
			//Si la valeur entrée n'est pas un nombre, remettre les valeurs précédentes
			//et afficher un message d'erreur
			hauteur.setText(String.valueOf(haut));
			largeur.setText(String.valueOf(larg));
			resultat[0] = Integer.parseInt(hauteur.getText());
			resultat[1] = Integer.parseInt(largeur.getText());
			JOptionPane.showMessageDialog(this, "Erreur, veuillez saisir des entiers décimaux", "Mauvais format", JOptionPane.ERROR_MESSAGE);
		}
		return resultat;
	}
	private JLabel haut_label, larg_label; private JTextField hauteur, largeur; private JButton ok, annuler;
	private JDialog appel; private int haut, larg; private Box global, hor1, hor2, hor3, hor4;
}

/**
 * Panneau proposant le choix des couleurs utilisées
 */
@SuppressWarnings("serial")
class Couleur extends JPanel implements ActionListener{
	public Couleur(JTextArea cible, JDialog appel){
		this.appel = appel;
		this.cible = cible;
		
		fondCourant = cible.getBackground();
		policeCourant = cible.getForeground();
		
		if(fondCourant==null){
			fondCourant = new Color(0xffff00);
		}
		if(policeCourant==null){
			policeCourant = Color.black;
		}
		
		JLabel fond = new JLabel("Couleur de fond :");
		JLabel texte = new JLabel("Couleur de police :");
		couleurFond = new JButton("                      ");
		couleurTexte = new JButton("                         ");
		ok = new JButton("Valider");
		annuler = new JButton("Annuler");
		exemple = new JTextArea("Aperçu du résultat avec ces couleurs");
		exemple.setFont(cible.getFont());
		
		couleurFond.addActionListener(this);
		couleurTexte.addActionListener(this);
		ok.addActionListener(this);
		annuler.addActionListener(this);
		
		setter();
		
		Box back = Box.createVerticalBox();
		Box police = Box.createVerticalBox();
		Box choix = Box.createHorizontalBox();
		Box boutons = Box.createHorizontalBox();
		Box align_boutons = Box.createHorizontalBox();
		Box global = Box.createVerticalBox();
		back.add(fond);
		back.add(Box.createVerticalStrut(5));
		back.add(couleurFond);
		police.add(texte);
		police.add(Box.createVerticalStrut(5));
		police.add(couleurTexte);
		choix.add(back);
		choix.add(Box.createHorizontalStrut(50));
		choix.add(police);
		boutons.add(ok);
		boutons.add(Box.createHorizontalStrut(10));
		boutons.add(annuler);
		align_boutons.add(Box.createHorizontalGlue());
		align_boutons.add(boutons);
		global.add(choix);
		global.add(Box.createVerticalStrut(10));
		global.add(exemple);
		global.add(Box.createVerticalStrut(30));
		global.add(align_boutons);
		
		add(global);
	}

	/**
	 * Formatage les composants avec les couleurs courantes
	 */
	public void setter(){
		couleurFond.setBackground(fondCourant);
		couleurTexte.setBackground(policeCourant);
		exemple.setBackground(fondCourant);
		exemple.setForeground(policeCourant);
	}

	/**
	 * Récupération des couleurs choisies par l'utilisateur
	 * @return Un tableau contenant les choix de l'utilisateur
	 */
	public Color[] getter(){
		return new Color[]{fondCourant, policeCourant};
	}

	/**
	 * Affichage d'une boîte de dialogue gérant le choix de couleur
	 * @param base Couleur courante
	 * @return Le choix de l'utilisateur
	 */
	public Color lancerChoix(Color base){
		Color resultat;
		JColorChooser color_chooser = new JColorChooser();
		color_chooser.setColor(base);
		JDialog dialog = JColorChooser.createDialog(null, "Sélectionnez une couleur", true, color_chooser, null, null);
		dialog.setVisible(true);
		resultat = color_chooser.getColor();
		return resultat;
	}

	/**
	 * Gestion des clics sur les différents boutons
	 */
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==couleurFond){
			fondCourant = lancerChoix(fondCourant);
			setter();
		}
		else if(e.getSource()==couleurTexte){
			policeCourant = lancerChoix(policeCourant);
			setter();
		}
		else if(e.getSource()==ok){
			//Masquer la boîte
			appel.setVisible(false);
		}
		else if(e.getSource()==annuler){
			//Remettre les valeurs de départ puis masquer la boîte
			fondCourant = cible.getBackground();
			policeCourant = cible.getForeground();
			appel.setVisible(false);
		}
	}

	private JButton couleurFond, couleurTexte, ok, annuler; private JTextArea exemple, cible;
	@SuppressWarnings("unused")
	private JLabel fond, texte;
	private Color fondCourant, policeCourant; private JDialog appel;
}