import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.* ;

@SuppressWarnings("serial")
public class PostIt extends JDialog implements ActionListener, DocumentListener{

	private JTextArea textPane; private JPanel north, east, west, south;
	private JButton boutonClose;
	private BufferedInputStream bis; private BufferedOutputStream bos;
	private DataInputStream dis; private DataOutputStream dos;
	private String s; private static String locateTexte, locateOption, locatePolice;
	private APropos apropos;
	private int x,y; public int hauteur, largeur;
	private MenuItem aProposItem, reglagesItem, closetItem;

	//Initialise les chemins utilisés par le programme
	public static void chemins(){
		//On utilise les variables systèmes afin de déterminer
		//les différents chemins vers les fichiers qui seront utilisés
		//tout au long du programme
		String fs = File.separator;
		String rep_backup = System.getProperty("user.home") + fs + ".memento" + fs;
		File rep_backup_ = new File(rep_backup);
		if(rep_backup_.exists()){}
		else{
			boolean création = rep_backup_.mkdir();
			if(création==false){System.out.println("erreur lors de la création du répertoire .memento");}
		}
		locateTexte = rep_backup + "text";
		locateOption = rep_backup + "option";
		locatePolice = rep_backup + "fonte";
	}
	//Interception des modifications du contenu du post-it
	public void insertUpdate(DocumentEvent e){
		enregistrerText();
	}
	public void removeUpdate(DocumentEvent e){
		enregistrerText();
	}
	/*Inutile mais obligatoire*/public void changedUpdate(DocumentEvent e){}

	//Interception des clics
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==aProposItem){
			apropos.setVisible(true);
		}
		else if(e.getSource()==reglagesItem){
			//On crée la boîte de dialogue gérant les réglages
			Onglets ongl = new Onglets(textPane.getFont(), new int[]{hauteur, largeur});
			//On utilise les différents getter pour obtenir les valeurs
			//sélectionnées par l'utilisateur
			Font f = ongl.fontGetter();
			int[] i = ongl.sizeGetter();
			hauteur = i[0];
			largeur = i[1];
			//On applique les changements
			textPane.setFont(f);
			setSize(largeur, hauteur);
			System.out.println("Fonte sélectionnée :\tPolice : " + f.getName() + " \tStyle : " + f.getStyle() + "\tPoints : " + f.getSize());
			editOptionFile();
			enregistrerPolice();
			//Libérer les ressources car la boîte de dialogue a juste été cachée, 
			//pas détruite
			ongl.dispose();
		}
		else if(e.getSource()==boutonClose){
			setVisible(false);
		}
		else if(e.getSource()==closetItem){
			//On enregistre à la fermeture du programme
			try {
				enregistrer();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
	}

	public PostIt(int couleur){
		chemins();

		//définition de la fenêtre
		this.setTitle("Memento");
		this.setResizable(false);
		this.setUndecorated(true);

		apropos = new APropos(this);

		//Création de l'icône de la SystemTray ainsi que des évènements associés
		final TrayIcon trayIcon;
		Image image = new ImageIcon(getClass().getResource("img/icone.png")).getImage();

		if (SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();

			//définition des actions lors d'un clique sur la SysTrayIcon
			MouseAdapter mouseListener = new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(isShowing()){
						editOptionFile();
						setVisible(false);
					}else{
						readOptionFile();
						setVisible(true);
					}
				}
			};

			PopupMenu popup = new PopupMenu();
			aProposItem = new MenuItem("A propos");
			reglagesItem = new MenuItem("Réglages");
			closetItem = new MenuItem("Quitter");

			aProposItem.addActionListener(this);
			reglagesItem.addActionListener(this);
			closetItem.addActionListener(this);
			popup.add(aProposItem);
			popup.add(reglagesItem);
			popup.add(closetItem);

			trayIcon = new TrayIcon(image, null , popup);

			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					trayIcon.displayMessage("Action Event",
							"An Action Event Has Been Performed!",
							TrayIcon.MessageType.INFO);
				}
			};

			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(actionListener);
			trayIcon.addMouseListener(mouseListener);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println("TrayIcon could not be added.");
			}

		} else {
			//  System Tray is not supported
			System.out.println("ERREUR : pas de systemTray supporté.");
			System.exit(-1);

		}

		//création du bouton de fermeture
		boutonClose = new JButton("X");
		boutonClose.setBorderPainted(false);
		boutonClose.setContentAreaFilled(false);
		boutonClose.addActionListener(this);


		//création du JTextArea, avec retour à la ligne automatique et coupure des mots
		//inactive
		textPane = new JTextArea();
		textPane.getDocument().addDocumentListener(this);
		textPane.setLineWrap(true);
		textPane.setWrapStyleWord(true);
		//On récupère la police du post-it
		Font f = lirePolice();
		if(f!=null){
			textPane.setFont(f);
		}
		else{
			try{
				InputStream inputFont = this.getClass().getResourceAsStream("NoteThis.ttf");
				Font font = Font.createFont(Font.TRUETYPE_FONT, inputFont);
				font = font.deriveFont(20f);
				textPane.setFont(font) ;
			}
			catch(Exception e){System.out.println("error");}
		}


		//on déclare les differents JPanel;
		north = new JPanel();
		east = new JPanel();
		west = new JPanel();
		south = new JPanel();
		Color jaune_texte = new Color(couleur);
		Color jaune_haut = new Color(couleur + 0x60);
		north.setBackground(jaune_haut);
		east.setBackground(jaune_texte);
		west.setBackground(jaune_texte);
		south.setBackground(jaune_texte);
		textPane.setBackground(jaune_texte);

		Handle handle = new Handle();
		handle.setBackground(new Color(couleur + 0x60));
		
		//ajout du bouton dans le JPanel du nord
		north.setLayout(new BorderLayout());
		north.add(handle, BorderLayout.CENTER);
		north.add(boutonClose, BorderLayout.EAST);


		//ajout des JPanel dans le conteneur principal
		Container c = getContentPane();
		c.add(north, BorderLayout.NORTH);
		c.add(east, BorderLayout.EAST);
		c.add(west, BorderLayout.WEST);
		c.add(south, BorderLayout.SOUTH);
		c.add(textPane, BorderLayout.CENTER);

		readOptionFile();
		this.textPane.setText(this.lireText());
		this.setVisible(true);
	}

	/********************************************
	 *                  METHOD                  
	 * @throws IOException *
	 * ******************************************/

	private void enregistrer() throws IOException{
		enregistrerText();
		editOptionFile();
		enregistrerPolice();
	}

	//Enregistrer la police courante
	private void enregistrerPolice(){
		//Initialiser les valeurs à enregistrer et créer le Writer
		Font f = textPane.getFont();
		String nom = f.getName();
		int style = f.getStyle();
		int taille = f.getSize();
		PrintWriter police_register;
		System.out.println("Enregistrement de la fonte :\tPolice : " + f.getName() + " \tStyle : " + f.getStyle() + "\tPoints : " + f.getSize());
		try {
			//Ouvrir le fichier en écriture et enregistrer la police courante
			police_register = new PrintWriter(new FileWriter(locatePolice));
			police_register.println(nom);
			police_register.println(style);
			police_register.println(taille);
			//Forcer l'envoi puis fermer le fichier
			police_register.flush();
			police_register.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	//Lire la police enregistrée
	private Font lirePolice(){
		//Créer la fonte futur-résultat et le lecteur
		Font resultat;
		BufferedReader lecteur;
		try {
			//Essayer d'ouvrir le fichier en lecture
			//et de lire les différentes informations de police stockées
			lecteur = new BufferedReader(new FileReader(locatePolice));
			String nom = lecteur.readLine();
			int style = Integer.parseInt(lecteur.readLine());
			int taille = Integer.parseInt(lecteur.readLine());
			//Créer la fonte à partir de ces informations puis fermer le fichier
			resultat = new Font(nom, style, taille);
			lecteur.close();
			System.out.println("Restauration de la fonte :\tPolice : " + resultat.getName() + " \tStyle : " + resultat.getStyle() + "\tPoints : " + resultat.getSize());
		} 
		catch (Exception e) {
			//En cas de problème retourner une valeur nulle afin
			//que le post-it utilise la fonte "Note This" embarquée
			return null;
		}
		//Sinon retourner la fonte créée à partir des informations lues
		return resultat;
	}

	/**
	 * Enregistrement du contenu du post-it dans le fichier de sauvegarde du texte
	 */
	private void enregistrerText(){

		try{
			bos = new BufferedOutputStream(new FileOutputStream(new File(locateTexte)));
			s=textPane.getText();

			byte[] byteString = s.getBytes();
			bos.write(byteString, 0, byteString.length);

			bos.close();

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Lecture du fichier de sauvegarde du texte
	 * @return String - a string representing the contents of the backup file
	 */
	private String lireText(){
		String s = new String();
		int val;
		try{
			bis = new BufferedInputStream(new FileInputStream(new File(locateTexte)));
			byte[] buf = new byte[64];

			while(-1 != (val= bis.read(buf,0,64)) ){
				s += new String(buf, 0, val);
			}
			bis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	//Lecture du fichier de configuration et application de ses paramètres
	private void readOptionFile(){
		try{

			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(locateOption))));
			x = dis.readInt();
			y = dis.readInt();
			hauteur = dis.readInt();
			largeur = dis.readInt();
			dis.close();

		} catch (IOException e) {
			System.out.println("erreur");
			x=y=0;
			hauteur=largeur=250;
		}
		this.setSize(largeur,hauteur);
		this.setLocation(x, y);
	}

	//Détection des paramètres courants et écriture de ceux-ci
	//dans le fichier de configuration
	public void editOptionFile(){
		try{
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(locateOption))));
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(hauteur);
			dos.writeInt(largeur);
			dos.close();

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	//****************************************************************
	//****************************************************************



	public void setLocationFrame(int x, int y){
		this.setLocation(x,y);
	}

	public Point getLocationFrame(){
		return this.getLocation();
	}

	//Création du type 'Panneau' enregistrant sa positon lorsqu'elle change
	private class Handle extends JPanel{
		Point p;    
		MouseEvent sauvE;
		public Handle(){
			this.addMouseMotionListener(new MouseMotionAdapter(){
				public void mouseDragged(MouseEvent e){
					p = getLocationFrame();
					x=(int)(p.getX()+(e.getX()-sauvE.getX()));
					y=(int)(p.getY()+(e.getY()-sauvE.getY()));
					setLocationFrame(x,y);
				}
			});

			this.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					p=getLocationFrame();
					sauvE = e;
				}
				public void mouseReleased(MouseEvent e){
					x=(int)(p.getX()+(e.getX()-sauvE.getX()));
					y=(int)(p.getY()+(e.getY()-sauvE.getY()));
					setLocationFrame(x,y);
					editOptionFile();
				}
			});

		}
	}

	public static void main(String[] args){
		new PostIt(0xffff00);
	}
}