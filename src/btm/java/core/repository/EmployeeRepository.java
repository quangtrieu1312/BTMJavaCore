package btm.java.core.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import btm.java.core.domain.employee.IEmployee;

public interface EmployeeRepository {

	File getErrorDir();

	File getArchiveDir();

	File getOutboundDir() throws Exception;

	File getInboundDir() throws Exception;

	void saveOutboundFile(String filename, List<IEmployee> employees);

	void saveArchiveFile(String filename, List<String> content);

	void saveErrorFile(String filename, List<String> content);

	void updatePaths(String inboundPath, String outboundPath) throws Exception;
	
	void saveEmployeeToDB(IEmployee employee);

	Vector<String> getLinesFromFile(File input) throws IOException;
}
