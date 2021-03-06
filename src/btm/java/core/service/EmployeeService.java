package btm.java.core.service;

import java.util.Vector;

import btm.java.core.domain.employee.IEmployee;

public interface EmployeeService {

	public Vector<IEmployee> processInboundFiles(String inboundPath, String outboundPath);

	public IEmployee saveEmployeeToDB(IEmployee employee);

	public Vector<IEmployee> saveEmployeesToDB(Vector<IEmployee> employees);

	void saveOutboundFile(String filename, Vector<IEmployee> employees);
}
