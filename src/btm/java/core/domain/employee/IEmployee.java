package btm.java.core.domain.employee;

public interface IEmployee {
	public String getEmployeeName();

	public void setEmployeeName(String employeeName);

	public String getStartDate();

	public void setStartDate(String startDate);

	public Double getBaseSalary();

	public void setBaseSalary(Double baseSalary);

	public Integer getWorkingDays();

	public void setWorkingDays(Integer workingDays);

	public Double getSalary();

	public void setSalary(Double salary);

	public void calcSalary();
}
