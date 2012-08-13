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


/**
 * Panneau permettant le choix d'une police de caractères
 */
@SuppressWarnings("serial")
class FontChooser extends JPanel implements ActionListener{
	/**
	 * Constructeur de la classe ChoicePanel
	 * @param f La police par défaut
	 * @param parent_dialog La boîte de dialogue appelante
	 */
	public FontChooser(Font f, JDialog parent_dialog){
		this.f = f;
		this.parent_dialog = parent_dialog;

		//Création du champ exemple et de tableaux contenant le choix proposé
		example = new JTextField("Voix ambigu\u00eb d'un coeur qui, au z\u00e9phyr, pr\u00e9f\u00e8re les jattes de kiwis", 24);
		example.setEditable(false);

		sizes = new String[]{"6","8","10","11","12","14","16","18","20","22","24","26","28","30","32","34","36","40","44","48"};
		styles = new String[]{"Normal", "Gras", "Italique", "Gras et Italique"};
		fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		//Création des box JTextField + JList permettant de choisir
		font_choice = new VerticalBox(fonts, false, this);
		style_choice = new VerticalBox(styles, false, this);
		size_choice = new VerticalBox(sizes, true, this);

		//Création des boutons de fin de dialogue
		cancel = new JButton("Annuler");
		cancel.addActionListener(this);
		ok = new JButton("OK");
		ok.addActionListener(this);

		//Box contenant les boutons
		prov_valid = Box.createHorizontalBox();
		prov_valid.add(cancel);
		prov_valid.add(Box.createHorizontalStrut(5));
		prov_valid.add(ok);

		global = Box.createVerticalBox();

		//Box gérant la mise en page des boutons
		valid = Box.createHorizontalBox();
		valid.add(Box.createGlue());
		valid.add(prov_valid);

		//Box gérant la mise en page des listes de choix
		settings = Box.createHorizontalBox();
		settings.add(font_choice);
		settings.add(Box.createHorizontalStrut(5));
		settings.add(style_choice);
		settings.add(Box.createHorizontalStrut(5));
		settings.add(size_choice);

		//Box contenant l'intégralité des composants
		global.add(settings);
		global.add(Box.createVerticalStrut(5));
		global.add(example);
		global.add(Box.createVerticalStrut(5));
		global.add(valid);

		//Remplissage des valeurs par défauts et affichage de l'exemple
		setter(f);
		example();

		//Ecouter le champ de texte de saisie de la taille afin que les modifications
		//soient repercutées lorsque l'utilisateur change son contenu
		size_choice.result.addActionListener(this);

		//Ajout du résultat au panneau
		add(global);
	}

	/**
	 * Mise à jour des champs de texte avec la police de caractères choisie
	 * @param f La police de caractères choisie
	 */
	public void setter(Font f){
		font_choice.result.setText(f.getName());
		size_choice.result.setText(String.valueOf(f.getSize()));
		int prov = f.getStyle();
		String prov2 = "";
		if(prov==Font.PLAIN){prov2 = "Normal";}
		else if(prov==Font.BOLD){prov2 = "Gras";}
		else if(prov==Font.ITALIC){prov2 = "Italique";}
		else if(prov==Font.BOLD+Font.ITALIC){prov2 = "Gras et Italique";}
		style_choice.result.setText(prov2);
	}

	/**
	 * Récupération de la police de caractères choisie par l'utilisateur
	 * @return L'objet Font contenant les choix de l'utilisateur
	 */
	public Font getter(){
		String prov = style_choice.result.getText();
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
		if(size_choice.result.getText().equals("")==false){
			font = new Font(font_choice.result.getText(), styl, Integer.parseInt(size_choice.result.getText()));
			return font;
		}
		else{
			JOptionPane.showMessageDialog(null, "Vous n'avez pas sp\u00e9cifi\u00e9 de taille !", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	/**
	 * Formatage de la phrase d'exemple avec la police de caractères selectionnée
	 */
	public void example(){
		Font prov_font = getter();
		if(prov_font != null){
			example.setFont(prov_font);
		}
		else{System.out.println("Erreur : Impossible d'afficher l'exemple, l'appel de getter() a retourn\u00e9 une valeur nulle");}
	}

	/**
	 * Gestion des clics sur les boutons "Valider" et "Annuler" ainsi que le changement de taille de la police de caractères
	 */
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==ok){
			//Faire disparaître la boîte de dialogue sans la éetruire
			//pour pouvoir continuer à utiliser getter() de l'extérieur
			parent_dialog.setVisible(false);
		}
		else if(e.getSource()==cancel){
			//Remettre les valeurs de départ puis
			//faire disparaître la boite de dialogue sans la détruire
			//pour pouvoir continuer à utiliser getter() de l'extérieur
			setter(f);
			parent_dialog.setVisible(false);
		}
		else if(e.getSource()==size_choice.result){
			example();
		}
	}

	private String [] sizes, fonts, styles; private Box settings, global, valid, prov_valid; private VerticalBox font_choice, style_choice, size_choice;
	private Font font, f; private int styl; private JTextField example; private JButton ok, cancel;
	private JDialog parent_dialog;
}

/**
 * Ensemble champ de texte et liste permettant la selection
 */
@SuppressWarnings("serial")
class VerticalBox extends Box implements ListSelectionListener{
	/**
	 * Constructeur de la classe VerticalBox
	 * @param content La liste de choix disponibles
	 * @param is_editable Le caractère éditable de la liste
	 * @param parent Le panneau appelant
	 */
	public VerticalBox(String[] content, boolean is_editable, FontChooser parent){
		super(1);
		this.parent = parent;
		result = new JTextField();
		result.setEditable(is_editable);
		choix = new JList<String>(content);
		choix.setSelectionMode(0);
		choix.setVisibleRowCount(10);
		choix.addListSelectionListener(this);
		defil = new JScrollPane(choix);

		add(result);
		add(Box.createVerticalStrut(3));
		add(defil);
	}

	/**
	 * Gestion de la sélection des éléments de la liste
	 */
	public void valueChanged(ListSelectionEvent e){
		if(!e.getValueIsAdjusting()){
			result.setText((String)choix.getSelectedValue());
			parent.example();
		}
	}
	JTextField result; private JList<String> choix; private JScrollPane defil; private FontChooser parent;
}