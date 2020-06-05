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
public class Developer extends AbsEmployee implements IEmployee {

	public Developer(String employeeName, String startDate, Double baseSalary, Integer workingDays) {
		this.setEmployeeName(employeeName);
		this.setStartDate(startDate);
		this.setBaseSalary(baseSalary);
		this.setWorkingDays(workingDays);
	}

	@Override
	public void calcSalary() {
		Double salary = this.getBaseSalary() / 26.0 * this.getWorkingDays();
		this.setSalary(salary);
	}

	@Override
	public String toString() {
		return EmployeeType.DEV + "|" + this.getEmployeeName() + "|" + this.getStartDate() + "|" + this.getBaseSalary()
				+ "|" + this.getWorkingDays() + "|" + this.getSalary();

	}

}
