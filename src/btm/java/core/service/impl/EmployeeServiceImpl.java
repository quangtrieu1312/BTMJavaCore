package btm.java.core.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import btm.java.core.util.InboundSolver;

public class EmployeeServiceImpl implements EmployeeService {

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
		InboundSolver inboundSolver = null;
		List<InboundSolver> inboundSolvers = new ArrayList<InboundSolver>();
		try {
			LOG.info("___________ START PROCESSING FILES ___________");
			updateSuccess = employeeRepository.updatePaths(inboundPath, outboundPath);
			if (!updateSuccess) {
				return null;
			}

			inboundFiles = employeeRepository.getInboundDir().listFiles(new FileFilter());
			for (File inboundFile : inboundFiles) {

				employees = new Vector<IEmployee>();
				lines = new Vector<String>();
				errorLines = new Vector<String>();
				successLines = new Vector<String>();

				inboundSolver = new InboundSolver(inboundFile.getName(), inboundFile, employees, lines, errorLines,
						successLines, employeeRepository);
				inboundSolvers.add(inboundSolver);

			}
			for (InboundSolver s : inboundSolvers) {
				s.getThread().join();
			}
			for (InboundSolver s : inboundSolvers) {
				if (s != null && s.getEmployees() != null) {
					fullList.addAll(s.getEmployees());
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
		LOG.info(">>>>>> START SAVING EMPLOYEES TO DB <<<<<<<<");
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
		LOG.info(">>>>>> DONE SAVING EMPLOYEES TO DB  <<<<<<<<");
		return savedEmployees;
	}
}
