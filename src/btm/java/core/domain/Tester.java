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
public class Tester extends AbsEmployee implements IEmployee {

	public Tester(String employeeName, Date startDate, Double baseSalary, Integer workingDays) {
		this.setEmployeeName(employeeName);
		this.setType(EmployeeType.TESTER);
		this.setStartDate(startDate);
		this.setBaseSalary(baseSalary);
		this.setWorkingDays(workingDays);
	}

	private Long calcWorkingYear() {
		long now = (new Date()).getTime();
		long startDate = this.getStartDate().getTime();
		return (now - startDate) / 1000 / 60 / 60 / 25 / 365;
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

	@Override
	public String toString() {
		return this.getType() + "|" + this.getEmployeeName() + "|"
				+ (new SimpleDateFormat("MM/dd/yyyy").format(this.getStartDate())) + "|" + this.getBaseSalary() + "|"
				+ this.getWorkingDays() + "|" + this.getSalary();

	}

}
