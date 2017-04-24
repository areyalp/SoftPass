package softpass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.JOptionPane;

public class BematechPrinter {
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;

	public static void main(String[] args) {
//		BematechPrinter fiscalPrinter = new BematechPrinter();
//		fiscalPrinter.printInvoice(2,"Camion 350",4,1,12,0.01);
		
//		printDnf("IMPRIMIENDO ALGO PARA PROBAR");
//		printXReport();
	}

	protected boolean printTest() {
		BematechPrinter bematechPrinter = new BematechPrinter();
		boolean printed = bematechPrinter.printDnf("PRUEBA");
		return printed;
	}
	
	protected boolean printInvoice(int invoiceId, String itemDescription, int itemCode, double itemQuantity, double itemTax, double itemPrice) {
		boolean fileExists = false;
		
		File file = new File("C:\\FACTURAS\\Softpass.001");
		try {
			if(file.exists()) {
				throw new BematechPrinterException("Impresion en curso o spooler no activo");
			}else{
				file.getParentFile().mkdirs();
				file.createNewFile();
				fileExists = true;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BematechPrinterException e) {
			JOptionPane.showMessageDialog(null, "Error en Impresora", "Error de Impresora", JOptionPane.ERROR_MESSAGE);
		}
		DecimalFormat df = new DecimalFormat("#0.00");
		double total = (itemQuantity * itemPrice) * (1 + (itemTax / 100));
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file,true));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			out.append("FACTURA   :  " + fillWithZeros(invoiceId, 8));
			out.newLine();
			out.append("NOMBRE    :  " + "NO CONTRIBUYENTE");
			out.newLine();
			out.append("DIRECCION1:  ");
			out.newLine();
			out.append("DIRECCION2:  ");
			out.newLine();
			out.append("RIF       :  " + "V-00000000-0");
			out.newLine();
			out.append("TELEFONO  :  ");
			out.newLine();
			out.append("DESCRIPCION                                 |CODIGO ARTICULO    |  CANTIDAD| %IVA|PREC/UNI/SIN IVA");
			out.newLine();
			out.append(setItem(itemDescription, itemCode, itemQuantity, itemTax, itemPrice));
			out.newLine();
			out.append("DESCUENTO : 0%");
			out.newLine();
			out.append("TOTAL     :  " + fillWithSpaces(df.format(total), 13, BematechPrinter.RIGHT));
			out.newLine();
			out.append("EFECTIVO  :  " + fillWithSpaces(df.format(total), 13, BematechPrinter.RIGHT));
			out.newLine();
			out.append("CHEQUE    :  " + fillWithSpaces(df.format(0), 13, BematechPrinter.RIGHT));
			out.newLine();
			out.append("TARJETA   :  " + fillWithSpaces(df.format(0), 13, BematechPrinter.RIGHT));
			out.newLine();
			out.append("CREDITO   :  " + fillWithSpaces(df.format(0), 13, BematechPrinter.RIGHT));
			out.newLine();
			out.append("COMentario1: ");
			out.newLine();
			out.append("COMentario2: ");
			out.newLine();
			out.append("COMentario3: ");
			out.newLine();
			out.append("COMentario4: ");
			out.newLine();
			out.append("NUMFACTAFECTA:  ");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error al imprimir factura");
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int i = 0;
		while(fileExists && i < 50) {
			if(!file.exists()) {
				fileExists = false;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
		}
		if(file.exists()) {
			return false;
		}else{
			return true;
		}
	}
	
	protected boolean printDnf(String text) {
		boolean fileExists = false;
		
		File file = new File("C:\\FACTURAS\\Softpass.003");
		try {
			if(file.exists()) {
				throw new BematechPrinterException("Impresion en curso o spooler no activo");
			}else{
				file.getParentFile().mkdirs();
				file.createNewFile();
				fileExists = true;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BematechPrinterException e) {
			e.printStackTrace();
		}
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file,true));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			out.append(fillWithSpaces("NO FISCAL", 47, BematechPrinter.LEFT));
			out.newLine();
			out.append(text);
			out.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int i = 0;
		while(fileExists && i < 50) {
			if(!file.exists()) {
				fileExists = false;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
		}
		if(file.exists()) {
			return false;
		}else{
			return true;
		}
	}
	
	protected boolean printXReport() {
		boolean fileExists = false;
		
		File file = new File("C:\\FACTURAS\\Softpass.001");
		try {
			if(file.exists()) {
				throw new BematechPrinterException("Impresion en curso o spooler no activo");
			}else{
				file.getParentFile().mkdirs();
				file.createNewFile();
				fileExists = true;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BematechPrinterException e) {
			e.printStackTrace();
		}
		
		BufferedWriter out = null;
		
		try {
			out = new BufferedWriter(new FileWriter(file,true));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			out.append("TIPO>X</TIPO");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int i = 0;
		while(fileExists && i < 50) {
			if(!file.exists()) {
				fileExists = false;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
		}
		if(file.exists()) {
			return false;
		}else{
			return true;
		}

	}
	
	protected boolean printZReport() {
		boolean fileExists = false;
		
		File file = new File("C:\\FACTURAS\\Softpass.001");
		try {
			if(file.exists()) {
				throw new BematechPrinterException("Impresion en curso o spooler no activo");
			}else{
				file.getParentFile().mkdirs();
				file.createNewFile();
				fileExists = true;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BematechPrinterException e) {
			e.printStackTrace();
		}
		
		BufferedWriter out = null;
		
		try {
			out = new BufferedWriter(new FileWriter(file,true));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			out.append("TIPO>Z</TIPO");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int i = 0;
		while(fileExists && i < 50) {
			if(!file.exists()) {
				fileExists = false;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
		}
		if(file.exists()) {
			return false;
		}else{
			return true;
		}
	}
	
	private String setItem(String itemDescription, int itemCode, double itemQuantity, double itemTax,
			double itemPrice) {
		String item;
		DecimalFormat formatQuantity = new DecimalFormat("#.000");
		DecimalFormat formatAmount = new DecimalFormat("#.00");
		item = fillWithSpaces(itemDescription, 44, BematechPrinter.LEFT) + " "
				+ fillWithSpaces(String.valueOf(itemCode), 19, BematechPrinter.LEFT) + " "
				+ fillWithSpaces(formatQuantity.format(itemQuantity), 10, BematechPrinter.RIGHT) + " "
				+ fillWithSpaces(formatAmount.format(itemTax), 5, BematechPrinter.RIGHT) + " "
				+ fillWithSpaces(formatAmount.format(itemPrice), 16, BematechPrinter.RIGHT);
		return item;
	}

	private static String fillWithZeros(int number, int digits){
		String stringNumber = String.valueOf(number);
		
		int numberLength = stringNumber.length();
		
		if(stringNumber.length() < digits){
			for(int i = 0; i < digits - numberLength; i++){
				stringNumber = "0" + stringNumber;
			}
		}
		
		return stringNumber;
	}
	
	private static String fillWithSpaces(String printStream, int characters, int justify) {
		int streamLength = printStream.length();
		
		if(printStream.length() < characters) {
			for(int i = 0; i < characters - streamLength; i++) {
				if(justify == BematechPrinter.LEFT) {
					printStream += " ";
				}else{
					printStream = " " + printStream;
				}
			}
		}
		
		return printStream;
	}
	
}
