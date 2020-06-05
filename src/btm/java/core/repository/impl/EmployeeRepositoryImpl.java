package btm.java.core.repository.impl;

import org.apache.log4j.Logger;

import btm.java.core.repository.EmployeeRepository;

public class EmployeeRepositoryImpl implements EmployeeRepository {
	private static EmployeeRepository instance;
	private static Logger LOG = Logger.getLogger(EmployeeRepositoryImpl.class);

	public static EmployeeRepository getInstance() {
		if (instance == null) {
			instance = new EmployeeRepositoryImpl();
		}
		return instance;
	}
}
