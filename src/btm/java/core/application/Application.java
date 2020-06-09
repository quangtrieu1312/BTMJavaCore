package btm.java.core.application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import btm.java.core.constant.FileDir;
import btm.java.core.service.EmployeeService;
import btm.java.core.service.impl.EmployeeServiceImpl;

import btm.java.core.domain.employee.IEmployee;

public class Application {

	private static EmployeeService employeeService = EmployeeServiceImpl.getInstance();

	private static Logger LOG = Logger.getLogger(Application.class);

	private static String inboundPath = "";
	private static String outboundPath = "";

	private static void displayUI(Vector<IEmployee> employees) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new SpreadSheet(employees);
			}
		});
	}

	private static Boolean initParams(String args[]) {
		if (args.length == 0) {
			inboundPath = FileDir.DEFAULT_INBOUND;
			outboundPath = FileDir.DEFAULT_OUTBOUND;
			System.out.println("Use default inbound path: " + inboundPath);
			System.out.println("Use default outbound path: " + outboundPath);
		} else if (args.length != 2) {
			System.out.println("Error: File needs 2 input for inbound and outbound paths.");
			System.out.println("If no input given, default path will be used.");
			return false;
		} else {
			inboundPath = args[0];
			outboundPath = args[1];
		}
		return true;
	}

	public static void main(String args[]) {
		Boolean initSuccess = initParams(args);
		if (initSuccess) {
			Vector<IEmployee> employees = employeeService.processInboundFiles(inboundPath, outboundPath);
			Vector<IEmployee> savedEmployees = employeeService.saveEmployeesToDB(employees);
			employeeService.saveOutboundFile(
					"employees_" + (new SimpleDateFormat("MMddyyyy").format(new Date())) + ".txt", savedEmployees);
			displayUI(savedEmployees);
		}
	}
}
