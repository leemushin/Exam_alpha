package next.services.classmanage;

public class Classmanage {
	private String CD_NO;//�Z�ťN��(table:CLASS_DATA��)
	private String CD_NAME;//�Z�ŦW��(table:CLASS_DATA��)
	private String UR_ACCOUNT;//�ϥΪ̱b��(table:USER_ROLE��)
	private String UR_NAME;//�ϥΪ̩m�W(table:USER_ROLE��)
	private String Last_login;//�̫�n�J�ɶ�(table:sys_data��)
	private String Last_duration;//�̫�ϥήɶ�(�N�W���ɶ��P�n�X�ɶ��۴�)
	private String Last_QUIZNAME;//�̫�Ҹժ��Ҩ��W��(table:SCORE_BASE��)
	private String Last_point;//�̫�Ҹժ�����(table:SCORE_BASE��)
	private String Point_month;		//�C�����(table:score_base�̭���sum(SB_BONUS)+sum(SB_POINT)�õ��������������)
	private String Point_semester;	//�Ǵ�����(table:score_base�̭���sum(SB_BONUS)+sum(SB_POINT)�õ��������������)
	private String Doexam_month;	//�C��Ҹ�(table:score_base�̭���count(sum(SB_BONUS)+sum(SB_POINT))�õ��������������)
	private String Doexam_semester;	//�Ǵ��Ҹ�(table:score_base�̭���count(sum(SB_BONUS)+sum(SB_POINT))�õ��������������)
	private int Cls_Pointmonth;	//�Z�ŨC�����
	private int Cls_Pointsemester;//�Z�žǴ�����
	private int Cls_examtimesmonth;	//�Z�ŨC��Ҹզ���--2018/05/21 �s�W
	private int Cls_examtimessemester;	//�Z�žǴ��Ҹզ���--2018/05/21 �s�W
	private int Cls_loginmonth;	//�Z�ŨC��n�J����--2018/05/21 �s�W
	private int Cls_loginsemester;	//�Z�žǴ��n�J����--2018/05/21 �s�W	
	
	public Classmanage(){		
	}
	/*public Classmanage(String CD_NO,String CD_NAME,String UR_ACCOUNT,String UR_NAME,String Last_login,String Last_duration,String Last_QUIZNAME,String Last_point,String Point_month,String Point_semester,String Doexam_month,String Doexam_semester){
		this.CD_NO = CD_NO;
	}*/
	
	
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
	public String getUR_ACCOUNT() {
		return UR_ACCOUNT;
	}
	public void setUR_ACCOUNT(String UR_ACCOUNT) {
		this.UR_ACCOUNT = UR_ACCOUNT;
	}	
	public String getUR_NAME() {
		return UR_NAME;
	}
	public void setUR_NAME(String UR_NAME) {
		this.UR_NAME = UR_NAME;
	}	
	public String getLast_login() {
		return Last_login;
	}
	public void setLast_login(String Last_login) {
		this.Last_login = Last_login;
	}	
	public String getLast_duration() {
		return Last_duration;
	}
	public void setLast_duration(String Last_duration) {
		this.Last_duration = Last_duration;
	}
	public String getLast_QUIZNAME() {
		return Last_QUIZNAME;
	}
	public void setLast_QUIZNAME(String Last_QUIZNAME) {
		this.Last_QUIZNAME = Last_QUIZNAME;
	}	
	public String getLast_point() {
		return Last_point;
	}
	public void setLast_point(String Last_point) {
		this.Last_point = Last_point;
	}	
	public String getPoint_month() {
		return Point_month;
	}
	public void setPoint_month(String Point_month) {
		this.Point_month = Point_month;
	}	
	public String getPoint_semester() {
		return Point_semester;
	}
	public void setPoint_semester(String Point_semester) {
		this.Point_semester = Point_semester;
	}	
	public String getDoexam_month() {
		return Doexam_month;
	}
	public void setDoexam_month(String Doexam_month) {
		this.Doexam_month = Doexam_month;
	}	
	public String getDoexam_semester() {
		return Doexam_semester;
	}
	public void setDoexam_semester(String Doexam_semester) {
		this.Doexam_semester = Doexam_semester;
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
	public int getCls_examtimesmonth() {
		return Cls_examtimesmonth;
	}
	public void setCls_examtimesmonth(int Cls_examtimesmonth) {
		this.Cls_examtimesmonth = Cls_examtimesmonth;
	}
	public int getCls_examtimessemester() {
		return Cls_examtimessemester;
	}
	public void setCls_examtimessemester(int Cls_examtimessemester) {
		this.Cls_examtimessemester = Cls_examtimessemester;
	}	
	public int getCls_loginmonth() {
		return Cls_loginmonth;
	}
	public void setCls_loginmonth(int Cls_loginmonth) {
		this.Cls_loginmonth = Cls_loginmonth;
	}	
	public int getCls_loginsemester() {
		return Cls_loginsemester;
	}
	public void setCls_loginsemester(int Cls_loginsemester) {
		this.Cls_loginsemester = Cls_loginsemester;
	}
}
