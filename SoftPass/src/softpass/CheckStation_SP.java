package softpass;
import java.sql.ResultSet;
//import org.joda.time.LocalDate;

import javax.swing.JOptionPane;

public class CheckStation_SP{
	
	public static void main(String[] args){
		
		new CheckStation_SP();
		
	} //END OF main
	
	public CheckStation_SP(){
		//LocalDate nowDate = new LocalDate();
		try{
			Db db = new Db();
			String macAddress = GetNetworkAddress.GetAddress("mac");
			//String macAddress = "00-30-67-DF-AA-07";
			//Playa String macAddress = "00-30-67-DF-A6-B7";
			//Salida1 String macAddress = "00-30-67-DF-B2-90";
			//Salida2 String macAddress = "00-30-67-DF-AA-07";
			//Salida3 String macAddress = "00-30-67-DF-A4-61";
			//String macAddress = "08-00-27-00-7C-35";
			if(!(macAddress == null)){
				ResultSet rowsMac = db.select("SELECT Id, Until FROM Stations WHERE"
						+ " MacAddress = '" + macAddress + "'");
				if(rowsMac.next()){
					//LocalDate untilDate = new LocalDate(rowsMac.getDate("Until"));
					//if(untilDate.isAfter(nowDate)){
						new SoftPassView(rowsMac.getInt("Id"));
					//}else{
						//JOptionPane.showMessageDialog(null, "El programa se ha vencido,\n\r debe contactar a Soporte Tecnico para actualizar su licencia", "Licencia vencida", JOptionPane.INFORMATION_MESSAGE);
					//}
				}else{
					new SelectStationView();
				}
			}else{
				JOptionPane.showMessageDialog(null, "No esta conectado a la red", "Conectese a la red", JOptionPane.ERROR_MESSAGE);
			}
		} //END OF try
		catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Ha ocurrido un error", JOptionPane.ERROR_MESSAGE);
		} //END OF catch
	} //END OF public CheckStation()
}//END OF class CheckStation