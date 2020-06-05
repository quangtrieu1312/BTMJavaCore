package btm.java.core.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import btm.java.core.config.HibernateConfig;
import btm.java.core.constant.FileDir;
import btm.java.core.domain.Developer;
import btm.java.core.domain.Manager;
import btm.java.core.domain.Tester;
import btm.java.core.domain.employee.IEmployee;
import btm.java.core.filter.FileFilter;
import btm.java.core.util.Validator;

public class Application {

	class LineSolver implements Runnable {
		private String name;
		private List<String> lines;
		private Integer startAtIndex;
		private Integer endAtIndex;

		public LineSolver(String name, List<String> lines, Integer startAtIndex, Integer endAtIndex) {
			this.setName(name);
			this.setLines(lines);
			this.setStartAtIndex(startAtIndex);
			this.setEndAtIndex(endAtIndex);
		}

		@Override
		public void run() {
			
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getLines() {
			return lines;
		}

		public void setLines(List<String> lines) {
			this.lines = lines;
		}

		public Integer getStartAtIndex() {
			return startAtIndex;
		}

		public void setStartAtIndex(Integer startAtIndex) {
			this.startAtIndex = startAtIndex;
		}

		public Integer getEndAtIndex() {
			return endAtIndex;
		}

		public void setEndAtIndex(Integer endAtIndex) {
			this.endAtIndex = endAtIndex;
		}

	}

	private static Logger LOG = Logger.getLogger(Application.class);

	private static File getInboundDir() { // raw input folder
		File input = new File(FileDir.INBOUND);
		if (!input.exists()) {
			input.mkdirs();
		}
		return input;
	}

	private static File getOutboundDir() {// output folder
		File output = new File(FileDir.OUTBOUND);
		if (!output.exists()) {
			output.mkdirs();
		}
		return output;
	}

	private static File getArchiveDir() { // successfully processed input folder
		File archive = new File(FileDir.ARCHIVE);
		if (!archive.exists()) {
			archive.mkdirs();
		}
		return archive;
	}

	private static File getErrorDir() {// failed processed input folder
		File error = new File(FileDir.ERROR);
		if (!error.exists()) {
			error.mkdirs();
		}
		return error;
	}

	private static IEmployee getEmployeeFromLine(String line) {
		LOG.info("..... Start converting 1 line to 1 employee");
		try {
			IEmployee employee = null;
			if (line.isEmpty()) {
				throw new Exception("Cannot convert empty line to employee");
			}
			String[] toks = line.split("\\|");
			if (toks.length != 5) {
				throw new Exception("Employee must have exactly 5 fields");
			}
			Integer type = Integer.parseInt(toks[0]);
			String employeeName = toks[1];
			String startDate = toks[2];
			Double baseSalary = Double.parseDouble(toks[3]);
			Integer workingDays = Integer.parseInt(toks[4]);
			if (!Validator.isDDMMYYYY(startDate)) {
				throw new Exception("Invalid date format");
			}
			switch (type) {
			case 1:
				employee = new Developer(employeeName, startDate, baseSalary, workingDays);
				break;
			case 2:
				employee = new Tester(employeeName, startDate, baseSalary, workingDays);
				break;
			case 3:
				employee = new Manager(employeeName, startDate, baseSalary, workingDays);
				break;
			default:
				throw new Exception("Invalid employee type");
			}
			return employee;
		} catch (Exception e) {
			LOG.error("[getEmployeeFromLine]: " + e);
			return null;
		} finally {
			LOG.info("***** Done converting");
		}
	}

	private static Vector<String> getLinesFromFile(File input) throws IOException {
		Vector<String> lines = new Vector<String>();
		int data;
		char temp;
		String line = "";
		try {
			BufferedInputStream iStream = new BufferedInputStream(new FileInputStream(input));
			while ((data = iStream.read()) != -1) {
				temp = (char) data;
				if (temp == '\r' || temp == '\n') {

					if (line.isEmpty()) {
						continue;
					}
					lines.add(line);
					line = "";
				} else {
					line += temp;
				}
			}
			iStream.close();
			return lines;
		} catch (Exception e) {
			LOG.error("[getLinesFromFile]: " + e);
			return null;
		}
	}

	private static IEmployee calcSalaryFromBasicInfo(IEmployee employee) {
		employee.calcSalary();
		return employee;
	}

	private static void saveOutboundFile(String filename, List<IEmployee> employees) {
		LOG.info("--->>> START SAVING OUTBOUND FILE <<<---");
		try {
			File outboundFile = new File(getOutboundDir() + File.separator + filename);
			if (outboundFile.exists()) {
				outboundFile.delete();
			}
			outboundFile.createNewFile();
			BufferedOutputStream oStream = new BufferedOutputStream(new FileOutputStream(outboundFile));
			String column = "Type|Employee Name|Start Date|Base Salary|Working Days\r\n";
			oStream.write(column.getBytes());
			for (IEmployee employee : employees) {
				oStream.write((employee.toString() + "\r\n").getBytes());
			}
			oStream.close();
		} catch (IOException e) {
			LOG.error("[saveArchiveFile]: " + e);
		} finally {
			LOG.info("------  DONE SAVING OUTBOUND FILE ------");
		}
	}

	private static void saveArchiveFile(String filename, List<String> content) {
		LOG.info("---->> START SAVING ARCHIVE FILE <<----");
		try {
			File archiveFile = new File(getArchiveDir() + File.separator + filename);
			if (archiveFile.exists()) {
				archiveFile.delete();
			}
			archiveFile.createNewFile();
			BufferedOutputStream oStream = new BufferedOutputStream(new FileOutputStream(archiveFile));
			for (String line : content) {
				oStream.write((line + "\r\n").getBytes());
			}
			oStream.close();
		} catch (IOException e) {
			LOG.error("[saveArchiveFile]: " + e);
		} finally {
			LOG.info("---->>  DONE SAVING ARCHIVE FILE <<----");
		}
	}

	private static void saveErrorFile(String filename, List<String> content) {
		LOG.info("------ START SAVING ERROR FILE ------");
		try {
			File errorFile = new File(getErrorDir() + File.separator + "Error_" + filename);
			if (errorFile.exists()) {
				errorFile.delete();
			}
			errorFile.createNewFile();
			BufferedOutputStream oStream = new BufferedOutputStream(new FileOutputStream(errorFile));
			for (String line : content) {
				oStream.write((line + "\r\n").getBytes());
			}
			oStream.close();
		} catch (IOException e) {
			LOG.error("[saveArchiveFile]: " + e);
		} finally {
			LOG.info("------  DONE SAVING ERROR FILE ------");
		}
	}

	public static void main(String args[]) {

		try {
			File[] inboundFiles = getInboundDir().listFiles(new FileFilter());
			LOG.info("___________ START PROCESSING FILES ___________");
			for (File inboundFile : inboundFiles) {

				IEmployee employee = null;
				Vector<IEmployee> employees = new Vector<IEmployee>();
				Vector<String> lines = null;
				Vector<String> errorLines = new Vector<String>();
				Vector<String> successLines = new Vector<String>();

				LOG.info("-----> START PROCESSING INBOUND FILE <-----");
				lines = getLinesFromFile(inboundFile);
				if (lines == null) {
					continue;
				}
				for (String line : lines) {
					employee = getEmployeeFromLine(line);
					if (employee != null) {
						employee = calcSalaryFromBasicInfo(employee);
						employees.add(employee);
						successLines.add(line);
					} else {
						errorLines.add(line);
					}
				}
				LOG.info("----->    DONE WITH INBOUND FILE    <-----");

				if (!successLines.isEmpty()) {
					saveArchiveFile(inboundFile.getName(), successLines);
					saveOutboundFile(inboundFile.getName(), employees);
				}
				if (!errorLines.isEmpty()) {
					saveErrorFile(inboundFile.getName(), errorLines);
				}

			}

		} catch (Exception e) {
			LOG.error("[main]: " + e);
		} finally {

			LOG.info("___________  DONE PROCESSING FILES ___________");
		}
	}
}
