package next.services.binduser;

import java.sql.Timestamp;

public class Binduser {
	private String USERNAME;//�ϥΪ̩m�W
	private String SYS_ACCOUNT;//�ϥΪ̱b��
	private String PASSWORD;//�ϥΪ̱K�X
	private String Last_login;//�̫�n�J
	private String Last_logout;//�̫�n�X
	private String PWD_HINT;//�ϥΪ̱K�X����
	private String UR_STUDENT;//���ݦ~��
	private String UR_TEACHER;//�Ѯv���O
	private String UR_PRINCIPAL;//�ժ����O
	private String UR_ADMIN;//�޲z�̵��O
	private String UR_SCHOOL;//���ݾǮ�
	private Timestamp REGISTERED;//���U��
	private Timestamp EXPIRYDATE;//�����
	private String REMARK;//�ϥΪ̸�ƳƵ�--2018/05/22�s�W
	
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
