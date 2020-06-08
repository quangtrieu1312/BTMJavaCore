package btm.java.core.domain.employee;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbsEmployee {
	@Id
	@Column(name = "Employee_name")
	private String employeeName;
	@Column(name = "Type")
	private Integer type;
	@Column(name = "Start_date")
	private String startDate;
	@Column(name = "Base_salary")
	private Double baseSalary;
	@Column(name = "Working_days")
	private Integer workingDays;
	@Column(name = "Salary")
	private Double salary;

	public AbsEmployee() {

	}

	public AbsEmployee(String employeeName, Integer type, String startDate, Double baseSalary, Integer workingDays,
			Double salary) {
		this.setEmployeeName(employeeName);
		this.setType(type);
		this.setStartDate(startDate);
		this.setBaseSalary(baseSalary);
		this.setWorkingDays(workingDays);
		this.setSalary(salary);
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public Double getBaseSalary() {
		return baseSalary;
	}

	public void setBaseSalary(Double baseSalary) {
		this.baseSalary = baseSalary;
	}

	public Integer getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(Integer workingDays) {
		this.workingDays = workingDays;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}
}
