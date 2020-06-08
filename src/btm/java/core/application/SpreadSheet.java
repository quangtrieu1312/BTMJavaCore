package btm.java.core.application;

import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import btm.java.core.domain.employee.*;

public class SpreadSheet extends JFrame {
	public SpreadSheet(Vector<IEmployee> employees) {
		// headers for the table
		String[] columns = new String[] { "Employee Name", "Type", "Start Date", "Base Salary", "Working Days",
				"Salary" };

		// actual data for the table in a 2d array
		Object[][] data = new Object[employees.size()][columns.length];

		for (int i = 0; i < employees.size(); i++) {
			data[i][0] = (Object) employees.get(i).getEmployeeName();
			data[i][1] = (Object) employees.get(i).getType();
			data[i][2] = (Object) new SimpleDateFormat("mm/dd/yyyy").format(employees.get(i).getStartDate());
			data[i][3] = (Object) employees.get(i).getBaseSalary();
			data[i][4] = (Object) employees.get(i).getWorkingDays();
			data[i][5] = (Object) employees.get(i).getSalary();
		}

		final Class[] columnClass = new Class[] { String.class, Integer.class, String.class, Double.class,
				Integer.class, Double.class };
		// create table model with data
		DefaultTableModel model = new DefaultTableModel(data, columns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return columnClass[columnIndex];
			}
		};

		JTable table = new JTable(model);

		// add the table to the frame
		this.add(new JScrollPane(table));

		this.setTitle("Employee spreadsheet");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
}
