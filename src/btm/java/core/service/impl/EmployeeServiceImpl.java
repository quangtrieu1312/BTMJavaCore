package btm.java.core.service.impl;

import org.apache.log4j.Logger;

import btm.java.core.service.EmployeeService;

public class EmployeeServiceImpl implements EmployeeService {
	
	private static EmployeeService instance;
	private static Logger LOG = Logger.getLogger(EmployeeServiceImpl.class);

	public static EmployeeService getInstance() {
		if (instance == null) {
			instance = new EmployeeServiceImpl();
		}
		return instance;
	}
}
