package softpass;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class LoginView extends JFrame{
	
	private JLabel labelUsername, labelPassword, labelLogin;
	private JTextField textUsername;
	private JPasswordField textPassword;
	private JButton buttonLogin;
	
	private String username;
	
	private boolean userId;
	
	private Station stationInfo;
	private int stationId;
	private int retries = 0;
	
	public LoginView(int stationId){
		this.stationId = stationId;
		
		stationInfo = Station.getStationInfo(stationId);
		
		if(stationInfo == null) {
			JOptionPane.showMessageDialog(null, "Estacion invalida", "Estacion invalida, contacte al administrador", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		this.setSize(300, 150);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int x = (dim.width / 2) - (this.getWidth() / 2);
		int y = (dim.height / 2) - (this.getHeight() / 2);
		this.setLocation(x, y);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Softpass - (" + stationInfo.getName() + ")");
		JPanel thePanel = new JPanel(new SpringLayout());
		labelUsername = new JLabel("Usuario", SwingConstants.TRAILING);
		thePanel.add(labelUsername);
		textUsername = new JTextField(10);
		labelUsername.setLabelFor(textUsername);
		thePanel.add(textUsername);
		labelPassword = new JLabel("Contraseña", SwingConstants.TRAILING);
		thePanel.add(labelPassword);
		textPassword = new JPasswordField(10);
		textPassword.setEchoChar('*');
		labelPassword.setLabelFor(textPassword);
		thePanel.add(textPassword);
		labelLogin = new JLabel("");
		thePanel.add(labelLogin);
		labelLogin.setLabelFor(buttonLogin);
		buttonLogin = new JButton("Login");
		thePanel.add(buttonLogin);
		SpringUtilities.makeCompactGrid(thePanel,
										3, 2,
										6, 6,
										6, 6);
		ButtonListener lForButton = new ButtonListener();
		buttonLogin.addActionListener(lForButton);
		JRootPane rootPane = this.getRootPane();
		rootPane.setDefaultButton(buttonLogin);
		this.add(thePanel);
		this.setVisible(true);
		
	}

	private class ButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			if(ev.getSource()==buttonLogin){
				username = textUsername.getText();
				char[] input = textPassword.getPassword();
				String plainPassword = "";
				for(int i=0; i < input.length; i++){
					plainPassword += input[i];
				}
				
				userId = Login.authenticate(username, plainPassword);
				if(userId){
					dispose();
					new SoftPassView(stationId);
				}else{
					retries++;
					
					if(retries < 3) {
						JOptionPane.showMessageDialog(null,"Combinacion Usuario/Password invalida","Datos invalidos",JOptionPane.WARNING_MESSAGE);
					}else{
						JOptionPane.showMessageDialog(null,"Maximo de intentos alcanzado","Maximo de Intentos",JOptionPane.WARNING_MESSAGE);
						System.exit(0);
					}
					textUsername.requestFocusInWindow();
					textUsername.selectAll();
				};
			} //END OF if
		} //END OF actionPerformed
	} //END OF class ButtonListener
} //END OF class LoginView