package btm.java.core.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import btm.java.core.domain.Developer;
import btm.java.core.domain.Manager;
import btm.java.core.domain.Tester;
import btm.java.core.domain.employee.IEmployee;
import btm.java.core.filter.FileFilter;
import btm.java.core.repository.EmployeeRepository;
import btm.java.core.repository.impl.EmployeeRepositoryImpl;
import btm.java.core.service.EmployeeService;

public class EmployeeServiceImpl implements EmployeeService {

	private class LineSolver implements Runnable {
		private Thread thread;

		private String name;
		private Vector<String> lines;
		private Integer startAtIndex;
		private Integer endAtIndex;
		private Vector<IEmployee> employees;
		private Vector<String> errorLines;
		private Vector<String> successLines;

		public LineSolver(String name, Vector<String> lines, Integer startAtIndex, Integer endAtIndex,
				Vector<IEmployee> employees, Vector<String> errorLines, Vector<String> successLines) {
			this.setName(name);
			this.setLines(lines);
			this.setStartAtIndex(startAtIndex);
			this.setEndAtIndex(endAtIndex);
			this.setEmployees(employees);
			this.setErrorLines(errorLines);
			this.setSuccessLines(successLines);

			this.setThread(new Thread(this, name));
			LOG.info("..... STARTING THREAD " + name);
			thread.start();
		}

		public void setThread(Thread thread) {
			this.thread = thread;
		}

		public Thread getThread() {
			return this.thread;
		}

		private IEmployee getEmployeeFromLine(String line) {
			LOG.info("..... THREAD " + name + " Start converting 1 line to 1 employee");
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
				Date startDate = new SimpleDateFormat("mm/dd/yyyy").parse(toks[2]);
				Double baseSalary = Double.parseDouble(toks[3]);
				Integer workingDays = Integer.parseInt(toks[4]);
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
				LOG.error("THREAD " + name + "[getEmployeeFromLine]: " + e);
				return null;
			} finally {
				LOG.info("***** THREAD " + name + " Done converting");
			}
		}

		@Override
		public void run() {
			IEmployee employee = null;
			for (int i = this.getStartAtIndex(); i <= this.getEndAtIndex(); i++) {
				employee = getEmployeeFromLine(lines.elementAt(i));
				if (employee != null) {
					employee = calcSalaryFromBasicInfo(employee);
					this.addEmployee(employee);
					this.addSuccessLine(lines.elementAt(i));
				} else {
					this.addErrorLine(lines.elementAt(i));
				}
			}
		}

		public void addEmployee(IEmployee employee) {
			this.employees.add(employee);
		}

		public void addSuccessLine(String line) {
			this.successLines.add(line);
		}

		public void addErrorLine(String line) {
			this.errorLines.add(line);
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Vector<String> getLines() {
			return lines;
		}

		public void setLines(Vector<String> lines) {
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

		public Vector<IEmployee> getEmployees() {
			return employees;
		}

		public void setEmployees(Vector<IEmployee> employees) {
			this.employees = employees;
		}

		public Vector<String> getErrorLines() {
			return errorLines;
		}

		public void setErrorLines(Vector<String> errorLines) {
			this.errorLines = errorLines;
		}

		public Vector<String> getSuccessLines() {
			return successLines;
		}

		public void setSuccessLines(Vector<String> successLines) {
			this.successLines = successLines;
		}
	}

	private static EmployeeService instance;
	private static Logger LOG = Logger.getLogger(EmployeeServiceImpl.class);

	private String inboundPath;
	private String outboundPath;

	private EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();

	public static EmployeeService getInstance() {
		if (instance == null) {
			instance = new EmployeeServiceImpl();
		}
		return instance;
	}

	private IEmployee calcSalaryFromBasicInfo(IEmployee employee) {
		employee.calcSalary();
		return employee;
	}

	private void processLinesInParallel(Vector<String> lines, Vector<IEmployee> employees, Vector<String> errorLines,
			Vector<String> successLines) {
		try {
			LOG.info("-----> START PROCESSING INBOUND LINES <-----");
			if (lines == null) {
				return;
			}
			LineSolver t1 = new LineSolver("ONE", lines, 0, (lines.size() - 1) / 2, employees, errorLines,
					successLines);
			LineSolver t2 = new LineSolver("TWO", lines, (lines.size() - 1) / 2 + 1, lines.size() - 1, employees,
					errorLines, successLines);
			t1.getThread().join();
			t2.getThread().join();
		} catch (InterruptedException e) {
			LOG.error("[processLinesInParallel]: " + e);
		} finally {
			LOG.info("----->    DONE WITH INBOUND LINES    <-----");
		}
	}

	private void processInboundFile(File inboundFile, Vector<IEmployee> employees, Vector<String> lines,
			Vector<String> errorLines, Vector<String> successLines) {

		this.processLinesInParallel(lines, employees, errorLines, successLines);

		employeeRepository.saveArchiveFile(inboundFile.getName(), successLines);
		employeeRepository.saveErrorFile(inboundFile.getName(), errorLines);
	}

	@Override
	public void saveOutboundFile(String filename, Vector<IEmployee> employees) {
		employeeRepository.saveOutboundFile(filename, employees);
	}

	@Override
	public Vector<IEmployee> processInboundFiles(String inboundPath, String outboundPath) {

		Vector<IEmployee> fullList = new Vector<IEmployee>();
		Vector<IEmployee> employees = null;
		Vector<String> lines = null;
		Vector<String> errorLines = null;
		Vector<String> successLines = null;
		File[] inboundFiles = null;
		Boolean updateSuccess = null;
		try {
			LOG.info("___________ START PROCESSING FILES ___________");
			updateSuccess = employeeRepository.updatePaths(inboundPath, outboundPath);
			if (!updateSuccess) {
				return null;
			}
			inboundFiles = employeeRepository.getInboundDir().listFiles(new FileFilter());
			for (File inboundFile : inboundFiles) {

				employees = new Vector<IEmployee>();
				lines = employeeRepository.getLinesFromFile(inboundFile);
				errorLines = new Vector<String>();
				successLines = new Vector<String>();

				processInboundFile(inboundFile, employees, lines, errorLines, successLines);
				if (employees != null) {
					fullList.addAll(employees);
				}

			}
			return fullList;
		} catch (Exception e) {
			LOG.error("[main]: " + e);
			System.out.println("[main]: " + e);
			return null;
		} finally {
			LOG.info("___________  DONE PROCESSING FILES ___________");
		}
	}

	@Override
	public IEmployee saveEmployeeToDB(IEmployee employee) {

		return employeeRepository.saveEmployeeToDB(employee);

	}

	@Override
	public Vector<IEmployee> saveEmployeesToDB(Vector<IEmployee> employees) {
		Vector<IEmployee> savedEmployees = new Vector<IEmployee>();
		IEmployee temp = null;
		for (IEmployee employee : employees) {
			try {
				temp = employeeRepository.saveEmployeeToDB(employee);
				if (temp != null) {
					savedEmployees.add(temp);
				}
			} catch (Exception e) {
				LOG.error("[processInboundFiles]: " + e);
			}
		}
		return savedEmployees;
	}
}
