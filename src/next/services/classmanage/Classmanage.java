package next.services.classmanage;

public class Classmanage {
	private String CD_NO;//班級代號(table:CLASS_DATA裡)
	private String CD_NAME;//班級名稱(table:CLASS_DATA裡)
	private String UR_ACCOUNT;//使用者帳號(table:USER_ROLE裡)
	private String UR_NAME;//使用者姓名(table:USER_ROLE裡)
	private String Last_login;//最後登入時間(table:sys_data裡)
	private String Last_duration;//最後使用時間(將上面時間與登出時間相減)
	private String Last_QUIZNAME;//最後考試的考卷名稱(table:SCORE_BASE裡)
	private String Last_point;//最後考試的分數(table:SCORE_BASE裡)
	private String Point_month;		//每月分數(table:score_base裡面的sum(SB_BONUS)+sum(SB_POINT)並給予對應日期條件)
	private String Point_semester;	//學期分數(table:score_base裡面的sum(SB_BONUS)+sum(SB_POINT)並給予對應日期條件)
	private String Doexam_month;	//每月考試(table:score_base裡面的count(sum(SB_BONUS)+sum(SB_POINT))並給予對應日期條件)
	private String Doexam_semester;	//學期考試(table:score_base裡面的count(sum(SB_BONUS)+sum(SB_POINT))並給予對應日期條件)
	private int Cls_Pointmonth;	//班級每月分數
	private int Cls_Pointsemester;//班級學期分數
	private int Cls_examtimesmonth;	//班級每月考試次數--2018/05/21 新增
	private int Cls_examtimessemester;	//班級學期考試次數--2018/05/21 新增
	private int Cls_loginmonth;	//班級每月登入次數--2018/05/21 新增
	private int Cls_loginsemester;	//班級學期登入次數--2018/05/21 新增	
	
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
