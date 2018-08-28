package next.services.statistics;

public class Statistics {
	private String CD_NO;//班級代號(table:CLASS_DATA裡)
	private String CD_NAME;//班級名稱(table:CLASS_DATA裡)
	private int members;//班級人數(table:CLASS_DATA count MEMBER裡面的人數)
	private int month_login;//本月班級登入人數(用last_login count跟本月份相同的)
	
	private String Utilization;//使用率(members/month_login * 100%)
	private int month_exams;//每月考試次數
	private int semester_exams;//每學期考試次數
	private int Cls_Pointmonth;	//班級每月分數
	private int Cls_Pointsemester;//班級學期分數
	
	public Statistics() {
	}
	
	public String getCD_NO() {
		return CD_NO;
	}
	public void setCD_NO(String CD_NO) {
		this.CD_NO = CD_NO;
	}	
	public String getCD_NAME() {
		return CD_NAME;
	}
	public void setCD_NAME(String CD_NAME) {
		this.CD_NAME = CD_NAME;
	}
	public int getmembers() {
		return members;
	}
	public void setmembers(int length) {
		this.members = length;
	}
	public int getmonth_login() {
		return month_login;
	}
	public void setmonth_login(int login_m) {
		this.month_login = login_m;
	}
	public String getUtilization() {
		return Utilization;
	}
	public void setUtilization(String Utilization) {
		this.Utilization = Utilization;
	}
	public int getmonth_exams() {
		return month_exams;
	}
	public void setmonth_exams(int month_exams) {
		this.month_exams = month_exams;
	}
	public int getsemester_exams() {
		return semester_exams;
	}
	public void setsemester_exams(int exam_sem) {
		this.semester_exams = exam_sem;
	}
	public int getCls_Pointmonth() {
		return Cls_Pointmonth;
	}
	public void setCls_Pointmonth(int Cls_Pointmonth) {
		this.Cls_Pointmonth = Cls_Pointmonth;
	}
	public int getCls_Pointsemester() {
		return Cls_Pointsemester;
	}
	public void setCls_Pointsemester(int Cls_Pointsemester) {
		this.Cls_Pointsemester = Cls_Pointsemester;
	}

}
