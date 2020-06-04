package btm.java.core.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import btm.java.core.config.HibernateConfig;
import btm.java.core.constant.FileDir;
import btm.java.core.domain.Developer;
import btm.java.core.domain.Manager;
import btm.java.core.domain.Tester;
import btm.java.core.domain.employee.IEmployee;
import btm.java.core.util.Validator;

public class Application {
	private static File getInputFile() {
		File dir = new File(FileDir.TEST);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return new File(FileDir.TEST + "/input.txt");
	}

	private static IEmployee calcSalary(String[] toks) {
		IEmployee em = null;
		try {
			Integer type = Integer.parseInt(toks[0]);
			String emName = toks[1];
			String startDate = toks[2];
			Double baseSalary = Double.parseDouble(toks[3]);
			Integer workingDays = Integer.parseInt(toks[4]);
			if (!Validator.isDDMMYYYY(startDate)) {
				throw new Exception();
			}
			switch (type) {
			case 1:
				em = new Developer(emName, startDate, baseSalary, workingDays);
				break;
			case 2:
				em = new Tester(emName, startDate, baseSalary, workingDays);
				break;
			case 3:
				em = new Manager(emName, startDate, baseSalary, workingDays);
				break;
			default:
				throw new Exception();
			}
			em.calcSalary();
		} catch (Exception e) {
			System.out.println("Cannot calc salary");
			return null;
		}
		return em;
	}
	
	public static void main(String args[]) {
		IEmployee em = null;
		File input = getInputFile();
		try {
			Session session = HibernateConfig.openSession();
			Transaction transaction = session.beginTransaction();
			
			if (!input.exists()) {
				input.createNewFile();
			}
			BufferedInputStream iStream = new BufferedInputStream(new FileInputStream(input));
			int data;
			char temp;
			String word = "";
			String[] toks;
			while ((data = iStream.read()) != -1) {
				temp = (char) data;
				if (temp == '\r' || temp == '\n') {
					if (word.isEmpty()) {
						continue;
					}
					toks = word.split("\\|");

					em = calcSalary(toks);
					session.persist(em);

					word = "";
					continue;
				} else {
					word += temp;
				}
			}
			iStream.close();
			
			transaction.commit();
			session.close();
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
}
