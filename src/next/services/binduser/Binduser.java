package next.services.binduser;

import java.sql.Timestamp;

public class Binduser {
	private String USERNAME;//使用者姓名
	private String SYS_ACCOUNT;//使用者帳號
	private String PASSWORD;//使用者密碼
	private String Last_login;//最後登入
	private String Last_logout;//最後登出
	private String PWD_HINT;//使用者密碼提示
	private String UR_STUDENT;//所屬年級
	private String UR_TEACHER;//老師註記
	private String UR_PRINCIPAL;//校長註記
	private String UR_ADMIN;//管理者註記
	private String UR_SCHOOL;//所屬學校
	private Timestamp REGISTERED;//註冊日
	private Timestamp EXPIRYDATE;//到期日
	private String REMARK;//使用者資料備註--2018/05/22新增
	
	public Binduser(String USERNAME, String SYS_ACCOUNT, String PASSWORD,String Last_login,String Last_logout,String PWD_HINT,String UR_STUDENT,String UR_TEACHER,String UR_PRINCIPAL,String UR_ADMIN,String UR_SCHOOL) {
		this.USERNAME = USERNAME;
		this.SYS_ACCOUNT = SYS_ACCOUNT;
		this.PASSWORD = PASSWORD;
		this.Last_login = Last_login;
		this.Last_logout = Last_logout;
		this.PWD_HINT = PWD_HINT;
		this.UR_STUDENT = UR_STUDENT;
		this.UR_TEACHER = UR_TEACHER;
		this.UR_PRINCIPAL = UR_PRINCIPAL;
		this.UR_ADMIN = UR_ADMIN;
		this.UR_SCHOOL = UR_SCHOOL;
	}	
	public Binduser() {
	}
	public String getUSERNAME() {
		return USERNAME;
	}
	public void setUSERNAME(String USERNAME) {
		this.USERNAME = USERNAME;
	}
	public String getSYS_ACCOUNT() {
		return SYS_ACCOUNT;
	}
	public void setSYS_ACCOUNT(String SYS_ACCOUNT) {
		this.SYS_ACCOUNT = SYS_ACCOUNT;
	}
	public String getPASSWORD() {
		return PASSWORD;
	}
	public void setPASSWORD(String PASSWORD) {
		this.PASSWORD = PASSWORD;
	}
	public String getLast_login() {
		return Last_login;
	}
	public void setLast_login(String Last_login) {
		this.Last_login = Last_login;
	}
	public String getLast_logout() {
		return Last_logout;
	}
	public void setLast_logout(String Last_logout) {
		this.Last_logout = Last_logout;
	}
	public String getPWD_HINT() {
		return PWD_HINT;
	}
	public void setPWD_HINT(String PWD_HINT) {
		this.PWD_HINT = PWD_HINT;
	}
	public String getUR_STUDENT() {
		return UR_STUDENT;
	}
	public void setUR_STUDENT(String UR_STUDENT) {
		this.UR_STUDENT = UR_STUDENT;
	}
	public String getUR_TEACHER() {
		return UR_TEACHER;
	}
	public void setUR_TEACHER(String UR_TEACHER) {
		this.UR_TEACHER = UR_TEACHER;
	}
	public String getUR_PRINCIPAL() {
		return UR_PRINCIPAL;
	}
	public void setUR_PRINCIPAL(String UR_PRINCIPAL) {
		this.UR_PRINCIPAL = UR_PRINCIPAL;
	}
	public String getUR_ADMIN() {
		return UR_ADMIN;
	}
	public void setUR_ADMIN(String UR_ADMIN) {
		this.UR_ADMIN = UR_ADMIN;
	}
	public String getUR_SCHOOL() {
		return UR_SCHOOL;
	}
	public void setUR_SCHOOL(String UR_SCHOOL) {
		this.UR_SCHOOL = UR_SCHOOL;
	}
	public Timestamp getREGISTERED() {
		return REGISTERED;
	}
	public void setREGISTERED(Timestamp REGISTERED) {
		this.REGISTERED = REGISTERED;
	}
	public Timestamp getEXPIRYDATE() {
		return EXPIRYDATE;
	}
	public void setEXPIRYDATE(Timestamp EXPIRYDATE) {
		this.EXPIRYDATE = EXPIRYDATE;
	}
	public String getREMARK() {
		return REMARK;
	}
	public void setREMARK(String REMARK) {
		this.REMARK = REMARK;
	}
	
}
