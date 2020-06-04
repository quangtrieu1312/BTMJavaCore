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
@DiscriminatorValue(value = "Manager")
public class Manager extends AbsEmployee implements IEmployee {
	public Manager(String employeeName, String startDate, Double baseSalary, Integer workingDays) {
		this.setEmployeeName(employeeName);
		this.setStartDate(startDate);
		this.setBaseSalary(baseSalary);
		this.setWorkingDays(workingDays);
	}

	@Override
	public void calcSalary() {
		Double salary = this.getBaseSalary();
		this.setSalary(salary);
	}

}
