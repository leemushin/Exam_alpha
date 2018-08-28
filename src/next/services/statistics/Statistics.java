package next.services.statistics;

public class Statistics {
	private String CD_NO;//�Z�ťN��(table:CLASS_DATA��)
	private String CD_NAME;//�Z�ŦW��(table:CLASS_DATA��)
	private int members;//�Z�ŤH��(table:CLASS_DATA count MEMBER�̭����H��)
	private int month_login;//����Z�ŵn�J�H��(��last_login count�򥻤���ۦP��)
	
	private String Utilization;//�ϥβv(members/month_login * 100%)
	private int month_exams;//�C��Ҹզ���
	private int semester_exams;//�C�Ǵ��Ҹզ���
	private int Cls_Pointmonth;	//�Z�ŨC�����
	private int Cls_Pointsemester;//�Z�žǴ�����
	
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
