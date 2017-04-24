package softpass;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import jssc.SerialPortList;
import tfhka.PrinterException;
import tfhka.ve.S1PrinterData;
import tfhka.ve.Tfhka;

public class SoftPassView extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8608650998009515094L;
	private static final int BIXOLON = 1;
	private static final int BEMATECH = 2;
	private static final String RELAY_PORT = "COM2";
	private static final int RELAY_DELAY = 4000;
	
	final private Tfhka bixolonFiscalPrinter;
	BematechPrinter bematechFiscalPrinter;
	private boolean isPrinterConnected;
	
	RelayDriver relayDriver;
	
	private int summaryId = 0;
	private boolean summaryHasInvoice = false;
	private int invoiceCount = 0;
	
	private User user;
	private int userId;
	private int stationId;
	private ArrayList<Summary> summaries;
	private ArrayList<Station> stationsWithSummary;
	private ArrayList<Transaction> transactionsType;
	private ArrayList<Transaction> transactions;
	private ArrayList<PayType> payTypes;

	private JButton 
	buttonVehicle0,
	buttonVehicle1,
	buttonVehicle2,
	buttonVehicle3,
	buttonVehicle4,
	buttonVehicle5,
	buttonVehicle6,
//	buttonVehicle7,
//	buttonVehicle8,
//	buttonVehicle9,
//	buttonVehicle10,
	buttonAccept,
	buttonCancel;
	private Station stationInfo;
	private JLabel 
	labelVehicle0,
	labelVehicle1,
	labelVehicle2,
	labelVehicle3,
	labelVehicle4,
	labelVehicle5,
	labelVehicle6,
//	labelVehicle7,
//	labelVehicle8,
//	labelVehicle9,
//	labelVehicle10,
	labelVehicleNumber0,
	labelVehicleNumber1,
	labelVehicleNumber2,
	labelVehicleNumber3,
	labelVehicleNumber4,
	labelVehicleNumber5,
	labelVehicleNumber6,
//	labelVehicleNumber7,
//	labelVehicleNumber8,
//	labelVehicleNumber9,
//	labelVehicleNumber10,
	labelRateVehicle0,
	labelRateVehicle1,
	labelRateVehicle2,
	labelRateVehicle3,
	labelRateVehicle4,
	labelRateVehicle5,
	labelRateVehicle6,
//	labelRateVehicle7,
//	labelRateVehicle8,
//	labelRateVehicle9,
//	labelRateVehicle10,
	labelPrice;

	JMenuBar menuBar;
	JMenu mainMenu;
	JMenuItem menu;
	JMenuItem menuItem;

	private static JLabel labelStatus;
	
	private JPanel theStatusBarPanel;

	private static JMenuItem menuItemConnect;

	private static JMenuItem menuItemDisconnect;

	private String activePort;
	
	private int selectedVehicle;
	private boolean isVehicleSelected = false;
	
	private int selectedPrinter;
	private JMenuItem subMenuBixolonItem;
	private JMenuItem subMenuBematechItem;
	
	private JTree tree;
	private JButton buttonReloadReports;
	
	public SoftPassView(int stationId){
		
		bixolonFiscalPrinter = new tfhka.ve.Tfhka();
		
		bematechFiscalPrinter = new BematechPrinter();
		
		relayDriver = new RelayDriver();

		try {
			relayDriver.connect(RELAY_PORT);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error en tarjeta de relays", "Error en la conexion a la tarjeta de relays-" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		LoginDialog loginDialog = new LoginDialog(null);
		
		loginDialog.setVisible(true);
		
		if(!loginDialog.isSucceeded()) {
			System.exit(0);
		}
		
		userId = Db.getUserId(loginDialog.getUsername());
		
		Db db = new Db();
		
		user = db.loadUserInfo(userId);
		
		if(user == null) {
			JOptionPane.showMessageDialog(null, "Usuario invalido", "Usuario invalido", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		this.stationId = stationId;
		
		stationInfo = Station.getStationInfo(stationId);
		
		transactionsType = Db.loadTransactionTypes();
		
		transactions = new ArrayList<Transaction>();
		
		payTypes = Db.loadPayTypes();
		
		summaryId = Db.getSummaryId(userId,stationId);
		
		if(summaryId > 0) {
			invoiceCount = db.countSummaryInvoices(summaryId);
		}
		
		if(invoiceCount > 0) {
			summaryHasInvoice = true;
		}
		
		summaries = Db.loadSummaries();
		
		
		//this.setSize(new Dimension(1024,600));
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		//this.setResizable(false);
		//this.setSize(new Dimension(800,600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(800,600));
		this.setIconImage(new ImageIcon("resources/6ejes.png").getImage());
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		
		int x = (dim.width / 2) - (this.getWidth()/2);
		int y = (dim.height / 2) - (this.getHeight() / 2);
		
		this.setLocation(x, y);
		
		this.setTitle("SoftPass - (" + user.getLogin() + ") " + user.getName());
		
		this.setLayout(new BorderLayout(20,20));
		
		// Create the menu bar.
		this.setJMenuBar(createMenu());
		
		JPanel mainPanel = new JPanel();
		
		mainPanel.setLayout(new BorderLayout(50,50));
		
		JPanel centerPanel = new JPanel();
		
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.LINE_AXIS));
		
		centerPanel.add(createVehicles());
		
		centerPanel.add(createCashier());
		
		enableButtons();
		
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		
		mainPanel.add(createStatusBar(), BorderLayout.SOUTH);
		
		this.add(mainPanel);
		
		this.add(createReportTree(), BorderLayout.EAST);
		
		selectedPrinter = SoftPassView.BEMATECH;
		
		isPrinterConnected = true;
		//menuItemDisconnect.setEnabled(true);
		//menuItemConnect.setEnabled(false);
		//menu.setForeground(Color.GREEN);
		
		this.setVisible(true);
	}

	private JMenuBar createMenu() {
		// JPanel thePanel = new JPanel(new BorderLayout());

		menuBar = new JMenuBar();

		// Build the first menu.
		mainMenu = new JMenu("Archivo");
		mainMenu.setMnemonic(KeyEvent.VK_A);
		mainMenu.getAccessibleContext().setAccessibleDescription("Menu Archivo");
		menuBar.add(mainMenu);

		// a group of JMenuItems
		MenuItemListener lForMenuItem = new MenuItemListener();

		menuItem = new JMenuItem("Cerrar Sesion", new ImageIcon("resources/signout.png"));
		menuItem.setActionCommand("logout");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_C);
		mainMenu.add(menuItem);

		menuItem = new JMenuItem("Salir", new ImageIcon("resources/close-program.png"));
		menuItem.setActionCommand("close");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		mainMenu.add(menuItem);

		// Build second menu in the menu bar.
		mainMenu = new JMenu("Ver");
		mainMenu.setMnemonic(KeyEvent.VK_V);
		mainMenu.getAccessibleContext().setAccessibleDescription("Menu Ver");
		menuBar.add(mainMenu);

//		menuItem = new JMenuItem("Resumen del Cierre Actual", new ImageIcon("resources/actual-resume.png"));
//		menuItem.setActionCommand("actual_resume");
//		menuItem.addActionListener(lForMenuItem);
//		menuItem.setMnemonic(KeyEvent.VK_R);
//		mainMenu.add(menuItem);

//		mainMenu.addSeparator();
//		menuItem = new JMenuItem("Ver Log", new ImageIcon("resources/log.png"));
//		menuItem.setActionCommand("log");
//		menuItem.setMnemonic(KeyEvent.VK_L);
//		mainMenu.add(menuItem);

		// Build third menu in the menu bar.
		mainMenu = new JMenu("Sistema");
		mainMenu.setMnemonic(KeyEvent.VK_S);
		mainMenu.getAccessibleContext().setAccessibleDescription("Menu Sistema");
		menuBar.add(mainMenu);

//		menuItem = new JMenuItem("Personalizar Factura", new ImageIcon("resources/fixed-invoice.png"));
//		menuItem.setActionCommand("fixed_invoice");
//		menuItem.setMnemonic(KeyEvent.VK_P);
//		mainMenu.add(menuItem);

//		menuItem = new JMenuItem("Detalle de Ultima Transaccion", new ImageIcon("resources/last-transaction.png"));
//		menuItem.setActionCommand("last_transaction");
//		menuItem.setMnemonic(KeyEvent.VK_D);
//		mainMenu.add(menuItem);

		mainMenu.addSeparator();
		menuItem = new JMenuItem("Calculadora", new ImageIcon("resources/calculator.png"));
		menuItem.setActionCommand("calc");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_C);
		mainMenu.add(menuItem);

		// Build fourth menu in the menu bar.
		mainMenu = new JMenu("Reportes");
		mainMenu.setMnemonic(KeyEvent.VK_R);
		mainMenu.getAccessibleContext().setAccessibleDescription("Menu Reportes");
		menuBar.add(mainMenu);
		
//		menuItem = new JMenuItem("Cierre Corte de Turno (X)", new ImageIcon("resources/x-report.png"));
//		menuItem.setActionCommand("cierre_x");
//		menuItem.addActionListener(lForMenuItem);
//		menuItem.setMnemonic(KeyEvent.VK_R);
//		mainMenu.add(menuItem);

		menuItem = new JMenuItem("Reporte Fiscal Diario (Z)", new ImageIcon("resources/z-report.png"));
		menuItem.setActionCommand("reporte_z");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_R);
		mainMenu.add(menuItem);

		// Build fifth menu in the menu bar.
		mainMenu = new JMenu("Ayuda");
		mainMenu.setMnemonic(KeyEvent.VK_Y);
		mainMenu.getAccessibleContext().setAccessibleDescription("Menu Ayuda");
		menuBar.add(mainMenu);

		menuItem = new JMenuItem("Acerca de", new ImageIcon("resources/about.png"));
		menuItem.setActionCommand("aboutus");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_A);
		mainMenu.add(menuItem);

		menuBar.add(mainMenu);
		// thePanel.add(menuBar, BorderLayout.PAGE_START);

		return menuBar;
	}

	private JPanel createCashier() {
		
		JPanel containerPanel = new JPanel();
		
		containerPanel.setLayout(new GridBagLayout());
		
		JPanel thePanel = new JPanel();
		
		thePanel.setLayout(new GridBagLayout());
		
		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		
		TitledBorder title;
		
		title = BorderFactory.createTitledBorder(loweredEtched, "Bs. F.");
		
		title.setTitleFont(new Font(null, Font.BOLD, 24));
		
		thePanel.setBorder(title);
		
		labelPrice = new JLabel("0,00");
		
		labelPrice.setFont(new Font(null, Font.BOLD, 48));
		
		labelPrice.setForeground(Color.RED);
		
		ButtonListener lForButton = new ButtonListener();
		
		GridBagConstraints c = new GridBagConstraints();
		
		thePanel.add(labelPrice);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 10;
		c.ipady = 10;
		c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		
		containerPanel.add(thePanel, c);
		
		buttonAccept = new JButton("Aceptar");
		buttonAccept.addActionListener(lForButton);
		buttonAccept.addKeyListener(lForButton);
		buttonAccept.setActionCommand("accept");
		buttonAccept.setMnemonic('A');
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 10;
		c.ipady = 10;
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.insets = new Insets(5,5,5,5);
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		containerPanel.add(buttonAccept, c);
		
		buttonCancel = new JButton("Cancelar");
		buttonCancel.addActionListener(lForButton);
		buttonCancel.addKeyListener(lForButton);
		buttonCancel.setActionCommand("cancel");
		buttonCancel.setMnemonic('C');
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 10;
		c.ipady = 10;
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.insets = new Insets(5,5,5,5);
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		containerPanel.add(buttonCancel, c);
		
		buttonAccept.setVisible(false);
		buttonCancel.setVisible(false);
		
		return containerPanel;
	}

	private JPanel createStatusBar() {
		
		//JMenu subMenu, subMenu2;
		//JMenuItem menuItem, subMenuItem;

		//String[] serialPorts = null;

		theStatusBarPanel = new JPanel(new BorderLayout());
		
		theStatusBarPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		JMenuBar menuBar = new JMenuBar();

		MenuItemListener lForMenuItem = new MenuItemListener();

		menu = new JMenu("•");
		menu.setForeground(Color.BLACK);
		menu.setIcon(new ImageIcon("resources/printer.png"));
		menu.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		/*menuItemConnect = new JMenuItem("Conectar");
		menuItemConnect.setActionCommand("connect");
		menuItemConnect.addActionListener(lForMenuItem);
		menu.add(menuItemConnect);
		menuItemDisconnect = new JMenuItem("Desconectar");
		menuItemDisconnect.setActionCommand("disconnect");
		menuItemDisconnect.addActionListener(lForMenuItem);
		menuItemDisconnect.setEnabled(false);
		menu.add(menuItemDisconnect);*/
		menuItem = new JMenuItem("Prueba");
		menuItem.setActionCommand("test");
		menuItem.addActionListener(lForMenuItem);
		menu.add(menuItem);
		
		/*subMenuPrinters = new JMenu("Impresora");
		subMenuBixolonItem = new JMenuItem("Bixolon SRP-350");
		subMenuBixolonItem.setActionCommand("bixolon");
		subMenuBixolonItem.addActionListener(lForMenuItem);
		subMenuPrinters.add(subMenuBixolonItem);
		menu.add(subMenuPrinters);
		subMenuBematechItem = new JMenuItem("Bematech MP-4000");
		subMenuBematechItem.setActionCommand("bematech");
		subMenuBematechItem.addActionListener(lForMenuItem);
		subMenuPrinters.add(subMenuBematechItem);
		menu.add(subMenuPrinters);
		
		subMenu = new JMenu("Puertos");

		// Create the COM Ports
		subMenu2 = new JMenu("COM");
		try {
			
			serialPorts = CommPortUtils.getSerialPorts();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (serialPorts.length == 0) {
			subMenuItem = new JMenuItem("No hay puertos COM");
			subMenu2.add(subMenuItem);
		} else {
			for (String port : serialPorts) {
				subMenuItem = new JMenuItem(port);
				subMenuItem.setActionCommand(port);
				subMenuItem.addActionListener(lForMenuItem);
				subMenu2.add(subMenuItem);
			}
		}

		subMenu.add(subMenu2);
		subMenuItem = new JMenuItem("USB");
		subMenu.add(subMenuItem);*/

		//menu.add(subMenu);
		menuBar.add(menu);

		labelStatus = new JLabel();
		// theFrame.add(BorderLayout.SOUTH, menuBar);
		theStatusBarPanel.add(menuBar, BorderLayout.WEST);

		theStatusBarPanel.add(labelStatus, BorderLayout.EAST);
		;

		return theStatusBarPanel;
	}

	private JPanel createVehicles() {
		JPanel thePanel = new JPanel();
		
		JPanel 
		labelPanel0,
		labelPanel1,
		labelPanel2,
		labelPanel3,
		labelPanel4,
		labelPanel5,
		labelPanel6
//		labelPanel7,
//		labelPanel8,
//		labelPanel9,
//		labelPanel10
		;
		
		GroupLayout layout = new GroupLayout(thePanel);
		thePanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		ButtonListener lForButton = new ButtonListener();
		
		buttonVehicle1 = new JButton(new ImageIcon("resources/automovil.png"));
		thePanel.add(initButton(buttonVehicle1, "vehicle1", lForButton));
		labelVehicleNumber1 = initVehicleLabelNumber(1,'1');
		labelVehicle1 = initVehicleLabel(1,transactionsType);
		labelRateVehicle1 = initVehicleRateLabel(1,transactionsType);
		labelPanel1 = new JPanel();
		thePanel.add(implementGroupLayout(labelPanel1, labelVehicleNumber1, labelVehicle1, labelRateVehicle1));
		
		buttonVehicle2 = new JButton(new ImageIcon("resources/pickup.png"));
		thePanel.add(initButton(buttonVehicle2, "vehicle2", lForButton));
		labelVehicleNumber2 = initVehicleLabelNumber(2,'2');
		labelVehicle2 = initVehicleLabel(2,transactionsType);
		labelRateVehicle2 = initVehicleRateLabel(2,transactionsType);
		labelPanel2 = new JPanel();
		thePanel.add(implementGroupLayout(labelPanel2, labelVehicleNumber2, labelVehicle2, labelRateVehicle2));
		
		buttonVehicle3 = new JButton(new ImageIcon("resources/camion350.png"));
		thePanel.add(initButton(buttonVehicle3, "vehicle3", lForButton));
		labelVehicleNumber3 = initVehicleLabelNumber(3,'3');
		labelVehicle3 = initVehicleLabel(3,transactionsType);
		labelRateVehicle3 = initVehicleRateLabel(3,transactionsType);
		labelPanel3 = new JPanel();
		thePanel.add(implementGroupLayout(labelPanel3, labelVehicleNumber3, labelVehicle3, labelRateVehicle3));
		
		buttonVehicle4 = new JButton(new ImageIcon("resources/camion750.png"));
		thePanel.add(initButton(buttonVehicle4, "vehicle4", lForButton));
		labelVehicleNumber4 = initVehicleLabelNumber(4,'4');
		labelVehicle4 = initVehicleLabel(4,transactionsType);
		labelRateVehicle4 = initVehicleRateLabel(4,transactionsType);
		labelPanel4 = new JPanel();
		thePanel.add(implementGroupLayout(labelPanel4, labelVehicleNumber4, labelVehicle4, labelRateVehicle4));
		
		buttonVehicle5 = new JButton(new ImageIcon("resources/5ejes.png"));
		thePanel.add(initButton(buttonVehicle5, "vehicle5", lForButton));
		labelVehicleNumber5 = initVehicleLabelNumber(5,'5');
		labelVehicle5 = initVehicleLabel(5,transactionsType);
		labelRateVehicle5 = initVehicleRateLabel(5,transactionsType);
		labelPanel5 = new JPanel();
		thePanel.add(implementGroupLayout(labelPanel5, labelVehicleNumber5, labelVehicle5, labelRateVehicle5));
		
		buttonVehicle6 = new JButton(new ImageIcon("resources/camion-playa.png"));
		thePanel.add(initButton(buttonVehicle6, "vehicle6", lForButton));
		labelVehicleNumber6 = initVehicleLabelNumber(6,'6');
		labelVehicle6 = initVehicleLabel(6,transactionsType);
		labelRateVehicle6 = initVehicleRateLabel(6,transactionsType);
		labelPanel6 = new JPanel();
		thePanel.add(implementGroupLayout(labelPanel6, labelVehicleNumber6, labelVehicle6, labelRateVehicle6));
		
		buttonVehicle0 = new JButton("EXONERADO");
		buttonVehicle0.setFont(new Font(null, Font.BOLD, 24));
		thePanel.add(initButton(buttonVehicle0, "vehicle0", lForButton));
		labelVehicleNumber0 = initVehicleLabelNumber(0,'0');
		labelVehicle0 = initVehicleLabel(0,transactionsType);
		labelRateVehicle0 = initVehicleRateLabel(0,transactionsType);
		labelPanel0 = new JPanel();
		thePanel.add(implementGroupLayout(labelPanel0, labelVehicleNumber0, labelVehicle0, labelRateVehicle0));
		
//		buttonVehicle7 = new JButton(new ImageIcon("resources/camion-playa.png"));
//		thePanel.add(initButton(buttonVehicle7, "vehicle7", lForButton));
//		labelVehicleNumber7 = initVehicleLabelNumber(7,'7');
//		labelVehicle7 = initVehicleLabel(7,transactionsType);
//		labelRateVehicle7 = initVehicleRateLabel(7,transactionsType);
//		labelPanel7 = new JPanel();
//		thePanel.add(implementGroupLayout(labelPanel7, labelVehicleNumber7, labelVehicle7, labelRateVehicle7));
//		
//		buttonVehicle8 = new JButton(new ImageIcon("resources/5ejes.png"));
//		thePanel.add(initButton(buttonVehicle8, "vehicle8", lForButton));
//		labelVehicleNumber8 = initVehicleLabelNumber(8,'8');
//		labelVehicle8 = initVehicleLabel(8,transactionsType);
//		labelRateVehicle8 = initVehicleRateLabel(8,transactionsType);
//		labelPanel8 = new JPanel();
//		thePanel.add(implementGroupLayout(labelPanel8, labelVehicleNumber8, labelVehicle8, labelRateVehicle8));
//		
//		buttonVehicle9 = new JButton(new ImageIcon("resources/6ejes.png"));
//		thePanel.add(initButton(buttonVehicle9, "vehicle9", lForButton));
//		labelVehicleNumber9 = initVehicleLabelNumber(9,'9');
//		labelVehicle9 = initVehicleLabel(9,transactionsType);
//		labelRateVehicle9 = initVehicleRateLabel(9,transactionsType);
//		labelPanel9 = new JPanel();
//		thePanel.add(implementGroupLayout(labelPanel9, labelVehicleNumber9, labelVehicle9, labelRateVehicle9));
//		
//		buttonVehicle10 = new JButton(new ImageIcon("resources/camion-playa.png"));
//		thePanel.add(initButton(buttonVehicle10, "vehicle10", lForButton));
//		labelVehicleNumber10 = initVehicleLabelNumber(10,'p');
//		labelVehicle10 = initVehicleLabel(10,transactionsType);
//		labelRateVehicle10 = initVehicleRateLabel(10,transactionsType);
//		labelPanel10 = new JPanel();
//		thePanel.add(implementGroupLayout(labelPanel10, labelVehicleNumber10, labelVehicle10, labelRateVehicle10));
		
		layout.setHorizontalGroup(
				
				layout.createSequentialGroup()
				
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(buttonVehicle1)
						.addComponent(buttonVehicle2)
						.addComponent(buttonVehicle3)
						.addComponent(buttonVehicle4)
						.addComponent(buttonVehicle5))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(labelPanel1)
						.addComponent(labelPanel2)
						.addComponent(labelPanel3)
						.addComponent(labelPanel4)
						.addComponent(labelPanel5))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(buttonVehicle6)
						.addComponent(buttonVehicle0)
//						.addComponent(buttonVehicle8)
//						.addComponent(buttonVehicle9)
//						.addComponent(buttonVehicle10)
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(labelPanel6)
						.addComponent(labelPanel0)
//						.addComponent(labelPanel8)
//						.addComponent(labelPanel9)
//						.addComponent(labelPanel10)
						)
				);
		
		layout.setVerticalGroup(
				
				layout.createSequentialGroup()
				
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(buttonVehicle1)
						.addComponent(labelPanel1)
						.addComponent(buttonVehicle6)
						.addComponent(labelPanel6))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(buttonVehicle2)
						.addComponent(labelPanel2)
						.addComponent(buttonVehicle0)
						.addComponent(labelPanel0)
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(buttonVehicle3)
						.addComponent(labelPanel3)
//						.addComponent(buttonVehicle8)
//						.addComponent(labelPanel8)
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(buttonVehicle4)
						.addComponent(labelPanel4)
//						.addComponent(buttonVehicle9)
//						.addComponent(labelPanel9)
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(buttonVehicle5)
						.addComponent(labelPanel5)
//						.addComponent(buttonVehicle10)
//						.addComponent(labelPanel10)
						)
				);
		
		layout.linkSize(buttonVehicle6, buttonVehicle0);
		
		return thePanel;
	}
	
	private JPanel createReportTree() {
		JPanel thePanel = new JPanel();
		thePanel.setLayout(new BorderLayout());
		thePanel.setMinimumSize(new Dimension(200, 200));
		thePanel.setPreferredSize(new Dimension(200, 200));
		
		ButtonListener lForButton = new ButtonListener();
		
		buttonReloadReports = new JButton("Recargar Cierres");
		buttonReloadReports.addActionListener(lForButton);
		buttonReloadReports.setActionCommand("reload-reports");
		thePanel.add(buttonReloadReports, BorderLayout.NORTH);
		
		stationsWithSummary = Db.getStationsWithSummary();
		summaries = Db.loadSummaries();
		
		tree = new JTree(new TreeDataModel(stationsWithSummary, summaries));
		
		MouseClickListener lForMouseClick = new MouseClickListener();
		
		tree.addMouseListener(lForMouseClick);
		
		JScrollPane treeView = new JScrollPane(tree);
		
		thePanel.add(treeView, BorderLayout.CENTER);
		return thePanel;
	}
	
	private static JButton initButton(JButton button, String actionCommand, ButtonListener buttonListener){
		
		button.setActionCommand(actionCommand);
		button.addActionListener(buttonListener);
		button.addKeyListener(buttonListener);
		
		return button;
	}
	
	private static JLabel initVehicleLabelNumber(int vehicleNumber, char mnemonic){
		
		JLabel labelVehicleNumber = new JLabel(String.valueOf(vehicleNumber));
		labelVehicleNumber.setDisplayedMnemonic(mnemonic);
		
		return labelVehicleNumber;
	}
	
	private static JLabel initVehicleLabel(int vehicleNumber, ArrayList<Transaction> transactionsType){
		
		JLabel labelVehicle = new JLabel(transactionsType.get(vehicleNumber).getName());
		
		return labelVehicle;
	}
	
	private static JLabel initVehicleRateLabel(int vehicleNumber, ArrayList<Transaction> transactionsType){
		
		DecimalFormat df = new DecimalFormat("0.00");
		
		JLabel labelRateVehicle = new JLabel(df.format(transactionsType.get(vehicleNumber).getAmount()*(1+transactionsType.get(vehicleNumber).getTax()/100)) + " BsF");
		labelRateVehicle.setFont(new Font(null, Font.BOLD, 18));
		
		return labelRateVehicle;
	}
	
	private static JPanel implementGroupLayout(JPanel thePanel, JLabel labelVehicleNumber, JLabel labelVehicle, JLabel labelRateVehicle){
		
		GroupLayout labelLayout = new GroupLayout(thePanel);
		thePanel.setLayout(labelLayout);
		labelLayout.setAutoCreateGaps(true);
		labelLayout.setAutoCreateContainerGaps(true);
		thePanel.add(labelVehicle);
		thePanel.add(labelRateVehicle);
		labelLayout.setHorizontalGroup(
				labelLayout.createSequentialGroup()
					.addGroup(labelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(labelVehicleNumber)
							.addComponent(labelVehicle)
							.addComponent(labelRateVehicle))
				);
		labelLayout.setVerticalGroup(
				labelLayout.createSequentialGroup()
					.addComponent(labelVehicleNumber)
					.addComponent(labelVehicle)
					.addComponent(labelRateVehicle)
				);
		
		return thePanel;
	}
	
	private class PopUpMenu extends JPopupMenu {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2007991130622202164L;
		JMenuItem closeReportMenu;
		
		public PopUpMenu() {
			PopUpMenuListener lForPopUpMenu = new PopUpMenuListener();
			closeReportMenu = new JMenuItem("Cerrar Reporte (Cierre X)");
			closeReportMenu.addActionListener(lForPopUpMenu);
			this.add(closeReportMenu);
		}
	}
	
	private class PopUpMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			PrintXReport x = new PrintXReport();
			new Thread(x).start();
		}
		
	}
	
	private class MouseClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			DefaultMutableTreeNode reportNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			//JOptionPane.showMessageDialog(null, "parent node user object: " + stationNode.getUserObject());
			if(SwingUtilities.isRightMouseButton(e) && reportNode.getUserObject() != null && reportNode.getLevel() == 3) {
				DefaultMutableTreeNode stationNode = new DefaultMutableTreeNode(reportNode.getParent().getParent());
				if(stationNode.getUserObject().toString().equalsIgnoreCase(stationInfo.getName())) {
					PopUpMenu popUpMenu = new PopUpMenu();
					popUpMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}
		
	}
	
	private class MenuItemListener implements ActionListener{

		@SuppressWarnings("unused")
		boolean sentCmd;
		
		@Override
		public void actionPerformed(ActionEvent ev) {
			
			LoginDialog loginDialog;
			Db db;
			User tempUser;
			
			if (ev.getActionCommand().startsWith("COM")) {
				activePort = ev.getActionCommand();
				labelStatus.setText(activePort + " seleccionado");
				OpenCommPortRun o = new OpenCommPortRun(activePort);
				if (!activePort.isEmpty() && !activePort.equals(null)) {
					new Thread(o).start();
				} else {
					labelStatus.setText("No hay puerto COM activo");
				}
			} else if (ev.getActionCommand().startsWith("USB")) {
				activePort = ev.getActionCommand();
				labelStatus.setText(activePort + " seleccionado");
			}

			switch (ev.getActionCommand()) {
				case "connect":
					if (activePort.isEmpty()) {
						activePort = SerialPortList.getPortNames()[0];
					}
					OpenCommPortRun o = new OpenCommPortRun(activePort);
					if (!activePort.isEmpty() && !activePort.equals(null)) {
						new Thread(o).start();
					} else {
						labelStatus.setText("No hay puerto COM activo");
					}
					break;
				case "disconnect":
					CloseCommPortRun c = new CloseCommPortRun(bixolonFiscalPrinter);
					new Thread(c).start();
					break;
				case "test":
					if(selectedPrinter == SoftPassView.BIXOLON) {
						SendCommandRun t = new SendCommandRun(PrinterCommand.printTest());
						new Thread(t).start();
					}else if(selectedPrinter == SoftPassView.BEMATECH){
						bematechFiscalPrinter.printTest();
					}
					break;
				case "bixolon":
					selectedPrinter = SoftPassView.BIXOLON;
					subMenuBixolonItem.setEnabled(false);
					subMenuBematechItem.setEnabled(true);
					break;
				case "bematech":
					selectedPrinter = SoftPassView.BEMATECH;
					subMenuBematechItem.setEnabled(false);
					subMenuBixolonItem.setEnabled(true);
					break;
			}
			
			switch(ev.getActionCommand()){
			case "logout":
				dispose();
				new CheckStation_SP();
				break;
			case "close":
				System.exit(0);
				break;
			case "actual_resume":
				break;
			case "log":
				break;
			case "fixed_invoice":
				break;
			case "last_transaction":
				break;
			case "calc":
				try {
					Runtime.getRuntime().exec("calc");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				break;
//			case "cierre_x":
//				db = new Db();
//				loginDialog = new LoginDialog(null);
//				
//				loginDialog.setVisible(true);
//				
//				tempUser = db.loadUserInfo(Db.getUserId(loginDialog.getUsername()));
//				
//				if(loginDialog.isSucceeded() && tempUser.isCanPrintReportX()) {
//					PrintXReport x = new PrintXReport();
//					new Thread(x).start();
//				}else{
//					JOptionPane.showMessageDialog(null, "No tiene permisos para realizar esta accion", "No tiene permisos", JOptionPane.ERROR_MESSAGE);
//				}
//				break;
			case "reporte_z":
				db = new Db();
				loginDialog = new LoginDialog(null);
				
				loginDialog.setVisible(true);
				
				tempUser = db.loadUserInfo(Db.getUserId(loginDialog.getUsername()));
				
				if(loginDialog.isSucceeded() && tempUser.isCanPrintReportZ()) {
					PrintZReport z = new PrintZReport();
					new Thread(z).start();
				}else{
					JOptionPane.showMessageDialog(null, "No tiene permisos para realizar esta accion", "No tiene permisos", JOptionPane.ERROR_MESSAGE);
				}
				break;
			case "aboutus":
				break;
			}
			
		}
		
	}
	
	private class ButtonListener implements ActionListener, KeyListener{

		@Override
		public void actionPerformed(ActionEvent ev) {
			SelectVehicleRun v = new SelectVehicleRun(ev.getActionCommand());
			new Thread(v).start();
		}

		@Override
		public void keyPressed(KeyEvent ev) {
			labelStatus.setText(KeyEvent.getKeyText(ev.getKeyChar()));
			SelectVehicleRun v = new SelectVehicleRun(KeyEvent.getKeyText(ev.getKeyChar()));
			new Thread(v).start();
		}

		@Override
		public void keyTyped(KeyEvent ev) {
			
		}

		@Override
		public void keyReleased(KeyEvent ev) {
			
		}
		
	}

	private class VehicleCheckOut implements Runnable{

		ArrayList<Transaction> transactions;
		S1PrinterData statusS1;
		@SuppressWarnings("unused")
		boolean sentCmd = false;
		
		VehicleCheckOut(ArrayList<Transaction> transactions){
			this.transactions = transactions;
		}
		
		@Override
		public void run() {
			Db db = new Db();
			int ticketNumber = 0;
			int insertedSummaryId = 0;
			
			buttonAccept.setEnabled(false);
			buttonCancel.setEnabled(false);
			if(db.testConnection()){
				if(selectedPrinter == BIXOLON) {
					printerChecker();
					if(isPrinterConnected){
						try {
							sentCmd = bixolonFiscalPrinter.SendCmd(PrinterCommand.setClientInfo(0, "Ticket #: " + ticketNumber));
						} catch (PrinterException ce) {
							ce.printStackTrace();
						}
						for(Transaction t: transactions) {
							try {
								sentCmd = bixolonFiscalPrinter.SendCmd(PrinterCommand.setItem(
										PrinterCommand.TAX1, 
										t.getAmount(), 
										1, 
										t.getName()));
							} catch (PrinterException ce) {
								ce.printStackTrace();
							}
						}
						
						try {
							sentCmd = bixolonFiscalPrinter.SendCmd(PrinterCommand.checkOut(PrinterCommand.PAYMENT_TYPE_EFECTIVO_01));
						} catch (PrinterException ce) {
							ce.printStackTrace();
						}
						db = new Db();
						
						if(summaryHasInvoice) {
							for(Transaction t: transactions) {
								db.insertTransaction(stationId, summaryId, ticketNumber, t.getAmount(), 12, 
									t.getId(), payTypes.get(0).getId());
							}
						}else{
							if(summaryId > 0) {
								summaryHasInvoice = true;
								for(Transaction t: transactions) {
									db.insertTransaction(stationId, summaryId, ticketNumber, t.getAmount(), 12, 
											t.getId(), payTypes.get(0).getId());
								}
							}else{
								try{
									statusS1 = bixolonFiscalPrinter.getS1PrinterData();
								} catch(PrinterException se) {
									se.printStackTrace();
								}
								int firstInvoiceNumber = statusS1.getLastInvoiceNumber();
								db = new Db();
								insertedSummaryId = db.insertSummary(stationId, user.getId(), firstInvoiceNumber);
								
								if(insertedSummaryId > 0) {
									summaryId = insertedSummaryId;
									summaryHasInvoice = true;
									for(Transaction t: transactions) {
										db.insertTransaction(stationId, summaryId, ticketNumber, t.getAmount(), 12, 
												t.getId(), payTypes.get(0).getId());
									}
									stationsWithSummary = Db.getStationsWithSummary();
									summaries = Db.loadSummaries();
									tree.setModel(new TreeDataModel(stationsWithSummary, summaries));
								}else{
									summaryId = 0;
									summaryHasInvoice = false;
									JOptionPane.showMessageDialog(null, "Error al crear el reporte", "Error de Reporte", JOptionPane.ERROR_MESSAGE);
								}
							}
						}
						transactions.clear();
						labelPrice.setText("0,00");
					}else{
						JOptionPane.showMessageDialog(null, "La impresora esta desconectada", "Impresora desconectada", JOptionPane.ERROR_MESSAGE);
					}
				}else if(selectedPrinter == BEMATECH){
					db = new Db();
					boolean printed = false;
					boolean exonerated = false;
					
					if(summaryHasInvoice) {
						int preInsertedTransaction = db.preInsertTransaction(stationId, summaryId, transactions.get(0).getAmount(), transactions.get(0).getTax(), 
								transactions.get(0).getId(), payTypes.get(0).getId());
						if(transactions.get(0).getAmount() > 0){
							printed = bematechFiscalPrinter.printInvoice(preInsertedTransaction, 
								transactions.get(0).getName(), transactions.get(0).getId(), 1, transactions.get(0).getTax(), transactions.get(0).getAmount());
						}else{
							exonerated = true;
						}
					}else{
						if(summaryId > 0) {
							summaryHasInvoice = true;
							int preInsertedTransaction = db.preInsertTransaction(stationId, summaryId, transactions.get(0).getAmount(), transactions.get(0).getTax(), 
									transactions.get(0).getId(), payTypes.get(0).getId());
							if(transactions.get(0).getAmount() > 0){
								printed = bematechFiscalPrinter.printInvoice(preInsertedTransaction, 
									transactions.get(0).getName(), transactions.get(0).getId(), 1, transactions.get(0).getTax(), transactions.get(0).getAmount());
							}else{
								exonerated = true;
							}
						}else{
							insertedSummaryId = db.insertSummary(stationId, user.getId(), 0);
							summaryId = insertedSummaryId;
							if(summaryId > 0) {
								summaryHasInvoice = true;
								
								int preInsertedTransaction = db.preInsertTransaction(stationId, summaryId, transactions.get(0).getAmount(), transactions.get(0).getTax(), 
										transactions.get(0).getId(), payTypes.get(0).getId());
								if(transactions.get(0).getAmount() > 0){
									printed = bematechFiscalPrinter.printInvoice(preInsertedTransaction, 
										transactions.get(0).getName(), transactions.get(0).getId(), 1, transactions.get(0).getTax(), transactions.get(0).getAmount());
								}else{
									exonerated = true;
								}
								stationsWithSummary = Db.getStationsWithSummary();
								summaries = Db.loadSummaries();
								tree.setModel(new TreeDataModel(stationsWithSummary, summaries));
							}else{
								summaryId = 0;
								summaryHasInvoice = false;
								JOptionPane.showMessageDialog(null, "Error al crear el reporte", "Error de Reporte", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					transactions.clear();
					labelPrice.setText("0,00");
					if(!printed && !exonerated) {
						JOptionPane.showMessageDialog(null, "Error al imprimir con impresora Bematech, por favor verifique","Error al imprimir", JOptionPane.ERROR_MESSAGE);
					}else{
						
						relayDriver.switchRelay(1,RelayDriver.ACTIVE_STATE);
						try {
							Thread.sleep(RELAY_DELAY);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						relayDriver.switchRelay(1,RelayDriver.INACTIVE_STATE);
						
					}
				}else{
					JOptionPane.showMessageDialog(null, "No ha seleccionado impresora", "Impresora no seleccionada", JOptionPane.INFORMATION_MESSAGE);
				}
			}else{
				JOptionPane.showMessageDialog(null, "La red esta desconectada, conectela de nuevo", "Red desconectada", JOptionPane.ERROR_MESSAGE);
			}
			transactions.clear();
			isVehicleSelected = false;
			enableButtons();
		}
		
	}

	private class OpenCommPortRun implements Runnable{
		
		String activePort;
		
		OpenCommPortRun(String activePort){
			this.activePort = activePort;
		}
		
		@Override
		public void run() {
			
			if(selectedPrinter == SoftPassView.BIXOLON) {
				if (bixolonFiscalPrinter.OpenFpctrl(activePort)) {
					isPrinterConnected = true;
					menuItemDisconnect.setEnabled(true);
					menuItemConnect.setEnabled(false);
					menu.setForeground(Color.GREEN);
					labelStatus.setText("Conectado al puerto " + activePort);
				} else {
					isPrinterConnected = false;
					labelStatus.setText("Error al conectarse a la impresora");
				}
			}else{
				isPrinterConnected = true;
				menuItemDisconnect.setEnabled(true);
				menuItemConnect.setEnabled(false);
				menu.setForeground(Color.GREEN);
				labelStatus.setText("Conectado al puerto " + activePort);
			}
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private class SendCommandRun implements Runnable{
		
		String command;
		boolean sentCmd;

		SendCommandRun(String command){
			this.command = command;
			this.sentCmd = false;
		}
		
		@Override
		public void run() {
			try {
				this.sentCmd = bixolonFiscalPrinter.SendCmd(this.command);
			} catch (PrinterException ex) {
				ex.printStackTrace();
			}
			
		}
		
	}

	private class SelectVehicleRun implements Runnable{

		String actionCommand;
		
		SelectVehicleRun(String actionCommand){
			this.actionCommand = actionCommand;
		}
		
		@Override
		public synchronized void run() {
			labelStatus.setText(actionCommand);
			if(!isVehicleSelected){
				switch(actionCommand){
				case "vehicle0":
				case "0":
					//if(stationInfo.getType()==1){
						transactions.add(transactionsType.get(0));
						selectVehicle(0);
					//}
					break;
				case "vehicle1":
				case "1":
					if(stationInfo.getType()==1){
						transactions.add(transactionsType.get(1));
						selectVehicle(1);
					}
					break;
				case "vehicle2":
				case "2":
					if(stationInfo.getType()==1){
						transactions.add(transactionsType.get(2));
						selectVehicle(2);
					}
					break;
				case "vehicle3":
				case "3":
					if(stationInfo.getType()==1){
						transactions.add(transactionsType.get(3));
						selectVehicle(3);
					}
					break;
				case "vehicle4":
				case "4":
					if(stationInfo.getType()==1){
						transactions.add(transactionsType.get(4));
						selectVehicle(4);
					}
					break;
				case "vehicle5":
				case "5":
					if(stationInfo.getType()==1){
						transactions.add(transactionsType.get(5));
						selectVehicle(5);
					}
					break;
				case "vehicle6":
				case "6":
					if(stationInfo.getType()==2){
						transactions.add(transactionsType.get(6));
						selectVehicle(6);
					}
					break;
//				case "vehicle7":
//				case "7":
//					transactions.add(transactionsType.get(6));
//					selectVehicle(7);
//					break;
//				case "vehicle8":
//				case "8":
//					transactions.add(transactionsType.get(7));
//					selectVehicle(8);
//					break;
//				case "vehicle9":
//				case "9":
//					transactions.add(transactionsType.get(8));
//					selectVehicle(9);
//					break;
//				case "vehicle10":
//				case "P":
//					transactions.add(transactionsType.get(9));
//					selectVehicle(10);
//					break;
				}//END OF switch()
			}else if(isVehicleSelected && (actionCommand.endsWith(String.valueOf(selectedVehicle)) || actionCommand.equalsIgnoreCase("p") 
					|| actionCommand.equalsIgnoreCase("c") || actionCommand.equalsIgnoreCase("cancel") || actionCommand.equalsIgnoreCase("escape"))){
				isVehicleSelected = false;
				transactions.clear();
				enableButtons();
			}else if(isVehicleSelected && (actionCommand.equalsIgnoreCase("a") || actionCommand.equalsIgnoreCase("accept") || actionCommand.equalsIgnoreCase("Intro"))){
				VehicleCheckOut v = new VehicleCheckOut(transactions);
				new Thread(v).start();
			}// END OF if(!isVehicleSelected)
		}// END OF public synchronized void run
	}// END OF class SelectVehicleRun
	
	private void selectVehicle(int vehicleNumber) {
		DecimalFormat df = new DecimalFormat("0.00");
		
		isVehicleSelected = true;
		labelPrice.setText(String.valueOf(df.format(transactions.get(0).getAmount()*(1+transactionsType.get(vehicleNumber).getTax()/100))));
		disableButtons(vehicleNumber);
		buttonAccept.requestFocus();
	}

	private void enableButtons() {
		if(stationInfo.getType() == 1){
			buttonVehicle1.setEnabled(true);
			buttonVehicle2.setEnabled(true);
			buttonVehicle3.setEnabled(true);
			buttonVehicle4.setEnabled(true);
			buttonVehicle5.setEnabled(true);
			buttonVehicle6.setEnabled(false);
//			buttonVehicle7.setEnabled(true);
//			buttonVehicle8.setEnabled(true);
//			buttonVehicle9.setEnabled(true);
//			buttonVehicle10.setEnabled(false);
		}else if(stationInfo.getType() == 2){
			buttonVehicle1.setEnabled(false);
			buttonVehicle2.setEnabled(false);
			buttonVehicle3.setEnabled(false);
			buttonVehicle4.setEnabled(false);
			buttonVehicle5.setEnabled(false);
			buttonVehicle6.setEnabled(true);
//			buttonVehicle7.setEnabled(false);
//			buttonVehicle8.setEnabled(false);
//			buttonVehicle9.setEnabled(false);
//			buttonVehicle10.setEnabled(true);
		}
		
		buttonAccept.setEnabled(false);
		buttonCancel.setEnabled(false);
		buttonAccept.setVisible(false);
		buttonCancel.setVisible(false);
		labelPrice.setText("0,00");
	}

	private void disableButtons(int button) {
		if(!(button == 1)){
			buttonVehicle1.setEnabled(false);
		}
		if(!(button == 2)){
			buttonVehicle2.setEnabled(false);
		}
		if(!(button == 3)){
			buttonVehicle3.setEnabled(false);
		}
		if(!(button == 4)){
			buttonVehicle4.setEnabled(false);
		}
		if(!(button == 5)){
			buttonVehicle5.setEnabled(false);
		}
		if(!(button == 6)){
			buttonVehicle6.setEnabled(false);
		}
//		if(!(button == 7)){
//			buttonVehicle7.setEnabled(false);
//		}
//		if(!(button == 8)){
//			buttonVehicle8.setEnabled(false);
//		}
//		if(!(button == 9)){
//			buttonVehicle9.setEnabled(false);
//		}
//		if(!(button == 10)){
//			buttonVehicle10.setEnabled(false);
//		}
		buttonAccept.setVisible(true);
		buttonCancel.setVisible(true);
		buttonAccept.setEnabled(true);
		buttonCancel.setEnabled(true);
	}
	
	private class PrintZReport implements Runnable{
		
		@Override
		public void run() {
			if(selectedPrinter == BIXOLON){
				try {
					bixolonFiscalPrinter.printZReport();
				} catch (PrinterException e) {
					e.printStackTrace();
				}
			}else if(selectedPrinter == BEMATECH){
				bematechFiscalPrinter.printZReport();
			}else{
				JOptionPane.showMessageDialog(null, "Impresora no seleccionada");
			}
		}
		
	}
	
	private class PrintXReport implements Runnable{

		@Override
		public void run() {
//			if(selectedPrinter == BIXOLON){
//				try {
//					bixolonFiscalPrinter.printXReport();
//				} catch (PrinterException e) {
//					e.printStackTrace();
//				}
//			}else if(selectedPrinter == BEMATECH){
//				bematechFiscalPrinter.printXReport();
//			}else{
//				JOptionPane.showMessageDialog(null, "Impresora no seleccionada");
//			}
			///////
			User supervisor;
			Db db = new Db();
			int supervisorId = 0;
			boolean sentCmd = true;
			
			tree.setEnabled(false);
			
			LoginDialog loginDialog = new LoginDialog(null);
			
			loginDialog.setVisible(true);
			
			supervisorId = Db.getUserId(loginDialog.getUsername());
			
			supervisor = db.loadUserInfo(supervisorId);
			
			if(selectedPrinter == BIXOLON){
				printerChecker();
			}
			
			if(isPrinterConnected) {
				
				if(loginDialog.isSucceeded() && supervisor.canPrintReportX) {
					if(selectedPrinter == BIXOLON){
						try {
							bixolonFiscalPrinter.printXReport();
						} catch (PrinterException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, "Error al imprimir con Bixolon");
							sentCmd = false;
						}
					}else if(selectedPrinter == BEMATECH){
						sentCmd = bematechFiscalPrinter.printXReport();
					}
					if(sentCmd) {
						DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
						boolean closed = Db.closeSummary((int) treeNode.getUserObject(), supervisorId);
						if(closed) {
							treeNode.removeFromParent();
							stationsWithSummary = Db.getStationsWithSummary();
							summaries = Db.loadSummaries();
							tree.setModel(new TreeDataModel(stationsWithSummary, summaries));
							summaryId = 0;
							summaryHasInvoice = false;
						}
					}else{
						JOptionPane.showMessageDialog(null, "Error al imprimir el reporte X","Error al imprimir", JOptionPane.ERROR_MESSAGE);
					}
				}else{
					JOptionPane.showMessageDialog(null, "Acceso no autorizado");
				}
			}else{
				JOptionPane.showMessageDialog(null, "Impresora desconectada", "Impresora desconectada", JOptionPane.ERROR_MESSAGE);
			}
			tree.setEnabled(true);
			
		}
		
	}
	
	private class CloseCommPortRun implements Runnable{

		Tfhka fiscalPrinter;
		
		CloseCommPortRun(Tfhka fiscalPrinter){
			this.fiscalPrinter = fiscalPrinter;
		}
		
		@Override
		public void run() {
			if (fiscalPrinter.CheckFprinter()) {
				fiscalPrinter.CloseFpctrl();
				isPrinterConnected = false;
				menuItemConnect.setEnabled(true);
				menuItemDisconnect.setEnabled(false);
				menu.setForeground(Color.BLACK);
				labelStatus.setText("Se desconecto la impresora");
			}
		}
		
	}
	
	private void printerChecker(){
			
		if(bixolonFiscalPrinter.CheckFprinter()){
			isPrinterConnected = true;
			menuItemConnect.setEnabled(false);
			menuItemDisconnect.setEnabled(true);
			menu.setForeground(Color.GREEN);
		}else{
			isPrinterConnected = false;
			menuItemConnect.setEnabled(true);
			menuItemDisconnect.setEnabled(false);
			menu.setForeground(Color.BLACK);
		}
	}
	
}
