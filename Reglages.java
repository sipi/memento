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
import javax.swing.event.* ;

/*====================================================================================
 *                    Création de la boîte de dialogue
 *====================================================================================*/

@SuppressWarnings("serial")
class Onglets extends JDialog{
	public Onglets(final Font f, int[] tail, JTextArea cible){
		//Créer la boîte
		super((JFrame)null, "Réglages", true);
		//Créer les onglets
		onglets = new JTabbedPane(SwingConstants.TOP);
		polices = new Polices(f, this);
		taille = new Taille(tail[0], tail[1], this);
		couleur = new Couleur(cible, this);
		scroll_police = new JScrollPane(polices);
		scroll_size = new JScrollPane(taille);
		scroll_couleur = new JScrollPane(couleur);
		onglets.addTab("Choix de la police", scroll_police);
		onglets.addTab("Dimensions", scroll_size);
		onglets.addTab("Couleurs", couleur);
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

	//Récupérer la taille demandée
	public int[] sizeGetter(){
		return taille.getter();
	}

	//Récupérer la fonte demandée
	public Font fontGetter(){
		return polices.getter();
	}
	
	//Récupérer les couleurs demandées
	public Color[] colorGetter(){
		return couleur.getter();
	}
	JTabbedPane onglets; Dimension dims; JScrollPane scroll_police, scroll_size, scroll_couleur; static Polices polices;
	Taille taille; Couleur couleur;

	/*====================================================================================
	 *                    Création du panneau gérant la police
	 *====================================================================================*/

	class Polices extends JPanel implements ActionListener{
		public Polices(Font f, JDialog appel){
			//Permet d'utiliser f et appel en dehors du constructeur
			arg0 = f;
			arg1 = appel;

			//Création du champ exemple et de tableaux contenant le choix proposé
			exemple = new JTextField("Voix ambiguë d'un coeur qui, au zéphyr, préfère les jattes de kiwis", 24);
			exemple.setEditable(false);

			tailles = new String[]{"6","8","10","11","12","14","16","18","20","22","24","26","28","30","32","34","36","40","44","48"};
			styles = new String[]{"Normal", "Gras", "Italique", "Gras et Italique"};
			polices = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

			//Création des box JTextField + JList permettant de choisir
			fonte = new Vertical(0, polices, false);
			style = new Vertical(0, styles, false);
			size = new Vertical(0, tailles, true);

			//Création des boutons pour finir
			annuler = new JButton("Annuler");
			annuler.addActionListener(this);
			ok = new JButton("Valider");
			ok.addActionListener(this);

			//Box contenant les boutons
			prov_valid = Box.createHorizontalBox();
			prov_valid.add(annuler);
			prov_valid.add(Box.createHorizontalStrut(5));
			prov_valid.add(ok);

			entier = Box.createVerticalBox();

			//Box gérant la mise en page des boutons
			valid = Box.createHorizontalBox();
			valid.add(Box.createGlue());
			valid.add(prov_valid);

			//Box gérant la mise en page des listes de choix
			settings = Box.createHorizontalBox();
			settings.add(fonte);
			settings.add(Box.createHorizontalStrut(5));
			settings.add(style);
			settings.add(Box.createHorizontalStrut(5));
			settings.add(size);

			//Box contenant l'intégralité des composants
			entier.add(settings);
			entier.add(Box.createVerticalStrut(5));
			entier.add(exemple);
			entier.add(Box.createVerticalStrut(5));
			entier.add(valid);

			//Remplissage des valeurs par défauts et affichage de l'exemple
			setter(f);
			exemple();

			//Écouter le champ de texte de saisie de la taille afin que les modifications
			//soient répercutées lorsque son contenu change
			size.resultat.addActionListener(this);

			//Ajout du résultat au panneau
			add(entier);
		}

		//Remplir les JTextField de résultat avec les valeurs par défaut transmises
		//sous forme d'objet 'Font'
		public void setter(Font f){
			fonte.resultat.setText(f.getName());
			size.resultat.setText(String.valueOf(f.getSize()));
			int prov = f.getStyle();
			String prov2 = "";
			if(prov==Font.PLAIN){prov2 = "Normal";}
			else if(prov==Font.BOLD){prov2 = "Gras";}
			else if(prov==Font.ITALIC){prov2 = "Italique";}
			else if(prov==Font.BOLD+Font.ITALIC){prov2 = "Gras et Italique";}
			style.resultat.setText(prov2);
		}

		//Retourner les valeurs saisies par l'utilisateur sous forme d'objet 'Font'
		public Font getter(){
			String prov = style.resultat.getText();
			if(prov.equals("Normal")){
				styl = Font.PLAIN;
			}
			else if(prov.equals("Gras")){
				styl = Font.BOLD;
			}
			else if(prov.equals("Italique")){
				styl = Font.ITALIC;
			}
			else if(prov.equals("Gras et Italique")){
				styl = Font.BOLD+Font.ITALIC;
			}
			font = new Font(fonte.resultat.getText(), styl, Integer.parseInt(size.resultat.getText()));
			return font;
		}

		//Afficher un exemple pour les valeurs sélectionnées
		public void exemple(){
			exemple.setFont(getter());
		}

		public void actionPerformed(ActionEvent e){
			if(e.getSource()==ok){
				//Faire disparaître la boîte de dialogue sans la détruire
				//pour pouvoir continuer à utiliser getter() de l'extérieur
				arg1.setVisible(false);
			}
			else if(e.getSource()==annuler){
				//Remettre les valeurs de départ puis
				//faire disparaître la boîte de dialogue sans la détruire
				//pour pouvoir continuer à utiliser getter() de l'extérieur
				setter(arg0);
				arg1.setVisible(false);
			}
			else if(e.getSource()==size.resultat){
				//Mettre à jour l'exemple quand l'utilisateur entre une taille
				exemple();
			}
		}

		String [] tailles, polices, styles; Box settings, entier, valid, prov_valid; Vertical fonte, style, size;
		Font font; int styl; JTextField exemple; JButton ok, annuler; Font arg0; JDialog arg1;
	}
}

/*=======================================================================================
 *        Type créant un ensemble JTextField + JList servant au choix
 *=======================================================================================*/
@SuppressWarnings("serial")
class Vertical extends Box implements ListSelectionListener{
	//Créer un Box vertical contenant le champ de texte et la liste
	public Vertical(int i, String[] contenu, boolean edition){
		super(1);
		resultat = new JTextField();
		resultat.setEditable(edition);
		choix = new JList<String>(contenu);
		choix.setSelectionMode(0);
		choix.setVisibleRowCount(10);
		choix.addListSelectionListener(this);
		defil = new JScrollPane(choix);

		//Ajouter les composants au Box
		add(resultat);
		add(Box.createVerticalStrut(3));
		add(defil);
	}

	//Remplir le champ de texte avec la valeur sélectionnée dans la liste
	public void valueChanged(ListSelectionEvent e){
		if(!e.getValueIsAdjusting()){
			resultat.setText((String)choix.getSelectedValue());
			Onglets.polices.exemple();
		}
	}

	JTextField resultat; JList<String> choix; JScrollPane defil;
}

/*====================================================================================
 *                    Création du panneau gérant la taille
 *====================================================================================*/

@SuppressWarnings("serial")
class Taille extends JPanel implements ActionListener{
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==ok){
			//Faire disparaître la boîte de dialogue sans la détruire
			//pour pouvoir continuer à utiliser getter() de l'extérieur
			arg0.setVisible(false);

		}
		else if(e.getSource()==annuler){
			//Remettre les valeurs de départ puis
			//faire disparaître la boîte de dialogue sans la détruire
			//pour pouvoir continuer à utiliser getter() de l'extérieur
			hauteur.setText(String.valueOf(haut));
			largeur.setText(String.valueOf(larg));
			arg0.setVisible(false);
		}
	}
	public Taille(int haut, int larg, JDialog appel){
		//Création et initialisation des composants et variables
		this.haut = haut;
		this.larg = larg;
		arg0 = appel;
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

	//Renvoyer les valeurs saisies par l'utilisateur sous forme d'un tableau d'entiers  
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
	JLabel haut_label, larg_label; JTextField hauteur, largeur; JButton ok, annuler;
	JDialog arg0; int haut, larg; Box global, hor1, hor2, hor3, hor4;
}

/*====================================================================================
 *                    Création du panneau gérant la couleur
 *====================================================================================*/
@SuppressWarnings("serial")
class Couleur extends JPanel implements ActionListener{
	public Couleur(JTextArea cible, JDialog appel){
		arg0 = appel;
		texteCible = cible;
		
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

	public static void setter(){
		couleurFond.setBackground(fondCourant);
		couleurTexte.setBackground(policeCourant);
		exemple.setBackground(fondCourant);
		exemple.setForeground(policeCourant);
	}

	public Color[] getter(){
		return new Color[]{fondCourant, policeCourant};
	}

	public Color lancerChoix(Color base){
		Color resultat;
		JColorChooser color_chooser = new JColorChooser();
		color_chooser.setColor(base);
		JDialog dialog = JColorChooser.createDialog(null, "Sélectionnez une couleur", true, color_chooser, null, null);
		dialog.setVisible(true);
		resultat = color_chooser.getColor();
		return resultat;
	}

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
			arg0.setVisible(false);
		}
		else if(e.getSource()==annuler){
			//Remettre les valeurs de départ puis masquer la boîte
			fondCourant = texteCible.getBackground();
			policeCourant = texteCible.getForeground();
			arg0.setVisible(false);
		}
	}

	static JButton couleurFond, couleurTexte, ok, annuler; static JTextArea exemple, texteCible; 
	JLabel fond, texte;	static Color fondCourant, policeCourant; JDialog arg0;
}