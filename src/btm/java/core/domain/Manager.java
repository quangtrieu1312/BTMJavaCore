package btm.java.core.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import btm.java.core.constant.EmployeeType;
import btm.java.core.domain.employee.AbsEmployee;
import btm.java.core.domain.employee.IEmployee;

@Entity
@Table(name = "EMPLOYEE")
@Immutable
public class Manager extends AbsEmployee implements IEmployee {

	public Manager(String employeeName, Date startDate, Double baseSalary, Integer workingDays) {
		this.setEmployeeName(employeeName);
		this.setType(EmployeeType.MANAGER);
		this.setStartDate(startDate);
		this.setBaseSalary(baseSalary);
		this.setWorkingDays(workingDays);
	}

	@Override
	public void calcSalary() {
		Double salary = this.getBaseSalary();
		this.setSalary(salary);
	}

	@Override
	public String toString() {
		return this.getType() + "|" + this.getEmployeeName() + "|"
				+ (new SimpleDateFormat("MM/dd/yyyy").format(this.getStartDate())) + "|" + this.getBaseSalary() + "|"
				+ this.getWorkingDays() + "|" + this.getSalary();

	}
}
