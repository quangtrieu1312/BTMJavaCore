package btm.java.core.domain.employee;

import java.util.Date;

public interface IEmployee {
	public String getEmployeeName();

	public void setEmployeeName(String employeeName);
	public Integer getType();

	public void setType(Integer type);
	
	public Long getEmployeeId();
	
	public void setEmployeeId(Long employeeId);

	public Date getStartDate();

	public void setStartDate(Date startDate);

	public Double getBaseSalary();

	public void setBaseSalary(Double baseSalary);

	public Integer getWorkingDays();

	public void setWorkingDays(Integer workingDays);

	public Double getSalary();

	public void setSalary(Double salary);

	public void calcSalary();

	
}
