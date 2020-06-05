package btm.java.core.repository.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import btm.java.core.domain.employee.IEmployee;
import btm.java.core.repository.EmployeeRepository;

public class EmployeeRepositoryImpl implements EmployeeRepository {
	private static EmployeeRepository instance;
	private static Logger LOG = Logger.getLogger(EmployeeRepositoryImpl.class);
	private String inboundPath = "";
	private String outboundPath = "";

	public static EmployeeRepository getInstance() {
		if (instance == null) {
			instance = new EmployeeRepositoryImpl();
		}
		return instance;
	}

	@Override
	public File getInboundDir() throws Exception { // raw input folder
		File input = new File(inboundPath);
		if (!input.exists()) {
			throw new Exception("Invalid inbound path");
		}
		return input;
	}

	@Override
	public File getOutboundDir() throws Exception { // output folder
		File output = new File(outboundPath);
		if (!output.exists()) {
			throw new Exception("Invalid outbound path");
		}
		return output;
	}

	@Override
	public File getArchiveDir() { // successfully processed input folder
		File archive = new File(inboundPath + File.separator + "archive");
		if (!archive.exists()) {
			archive.mkdirs();
		}
		return archive;
	}

	@Override
	public File getErrorDir() {// failed processed input folder
		File error = new File(inboundPath + File.separator + "error");
		if (!error.exists()) {
			error.mkdirs();
		}
		return error;
	}

	@Override
	public void saveOutboundFile(String filename, List<IEmployee> employees) {
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
		} catch (Exception e) {
			LOG.error("[saveArchiveFile]: " + e);
		} finally {
			LOG.info("------  DONE SAVING OUTBOUND FILE ------");
		}
	}

	@Override
	public void saveArchiveFile(String filename, List<String> content) {
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

	@Override
	public void saveErrorFile(String filename, List<String> content) {
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

	@Override
	public void updatePaths(String inboundPath, String outboundPath) throws Exception {
		this.inboundPath = inboundPath;
		this.outboundPath = outboundPath;
		this.getInboundDir();
		this.getOutboundDir();
	}
}