package btm.java.core.service;

import btm.java.core.domain.employee.IEmployee;

public interface EmployeeService {

	public void processInboundFiles(String inboundPath, String outboundPath);
	
	public void saveEmployeeToDB(IEmployee employee);
}
