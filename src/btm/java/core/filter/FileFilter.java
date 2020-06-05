package btm.java.core.filter;

import java.io.File;
import java.io.FilenameFilter;

import btm.java.core.util.Validator;

public class FileFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return Validator.isValidFilename(name);
	}
}
