package btm.java.core.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import btm.java.core.domain.Developer;
import btm.java.core.domain.Manager;
import btm.java.core.domain.Tester;
import btm.java.core.domain.employee.IEmployee;
import btm.java.core.repository.EmployeeRepository;

public class InboundSolver implements Runnable {
	private static Logger LOG = Logger.getLogger(InboundSolver.class);

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

		private IEmployee calcSalaryFromBasicInfo(IEmployee employee) {
			employee.calcSalary();
			return employee;
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

	private Thread thread;
	private String name;
	private File inboundFile;
	private Vector<IEmployee> employees;
	private Vector<String> lines;
	private Vector<String> errorLines;
	private Vector<String> successLines;
	private EmployeeRepository employeeRepository;

	public InboundSolver(String name, File inboundFile, Vector<IEmployee> employees, Vector<String> lines,
			Vector<String> errorLines, Vector<String> successLines, EmployeeRepository employeeRepository) {
		this.setName(name);
		this.setInboundFile(inboundFile);
		this.setEmployees(employees);
		this.setLines(successLines);
		this.setErrorLines(errorLines);
		this.setSuccessLines(successLines);
		this.setEmployeeRepository(employeeRepository);

		this.setThread(new Thread(this, name));
		LOG.info("..... STARTING THREAD " + name);
		thread.start();
	}

	@Override
	public void run() {

		LOG.info("-----> START PROCESSING INBOUND LINES <-----");
		try {
			this.setLines(employeeRepository.getLinesFromFile(inboundFile));
		} catch (IOException e1) {
			LOG.error("[run]: " + e1);
		}
		if (lines == null) {
			return;
		}
		LineSolver t1 = new LineSolver(this.getName() + "_ONE", lines, 0, (lines.size() - 1) / 2, employees, errorLines,
				successLines);
		LineSolver t2 = new LineSolver(this.getName() + "_TWO", lines, (lines.size() - 1) / 2 + 1, lines.size() - 1,
				employees, errorLines, successLines);
		try {
			t1.getThread().join();
			t2.getThread().join();
		} catch (InterruptedException e) {
			LOG.error("[run]: " + e);
		}

		employeeRepository.saveArchiveFile(inboundFile.getName(), successLines);
		employeeRepository.saveErrorFile(inboundFile.getName(), errorLines);

		LOG.info("----->    DONE WITH INBOUND LINES    <-----");

	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public File getInboundFile() {
		return inboundFile;
	}

	public void setInboundFile(File inboundFile) {
		this.inboundFile = inboundFile;
	}

	public Vector<IEmployee> getEmployees() {
		return employees;
	}

	public void setEmployees(Vector<IEmployee> employees) {
		this.employees = employees;
	}

	public Vector<String> getLines() {
		return lines;
	}

	public void setLines(Vector<String> lines) {
		this.lines = lines;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EmployeeRepository getEmployeeRepository() {
		return employeeRepository;
	}

	public void setEmployeeRepository(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}
}
