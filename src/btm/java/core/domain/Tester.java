package btm.java.core.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import btm.java.core.constant.EmployeeType;
import btm.java.core.domain.employee.AbsEmployee;
import btm.java.core.domain.employee.IEmployee;

@Entity
@DiscriminatorValue(value = "Developer")
public class Tester extends AbsEmployee implements IEmployee {
	public Tester(String employeeName, String startDate, Double baseSalary, Integer workingDays) {
		this.setEmployeeName(employeeName);
		this.setStartDate(startDate);
		this.setBaseSalary(baseSalary);
		this.setWorkingDays(workingDays);
	}

	private Integer calcWorkingYear() {
		return this.getWorkingDays() / 365;
	}

	@Override
	public void calcSalary() {
		Double salary = this.getBaseSalary() / 26.0 * this.getWorkingDays();
		if (this.calcWorkingYear() < 1) {
			salary += 100;
		} else if (this.calcWorkingYear() < 2) {
			salary += 200;
		} else if (this.calcWorkingYear() < 3) {
			salary += 300;
		} else {
			salary += 1000;
		}
		this.setSalary(salary);
	}

}
