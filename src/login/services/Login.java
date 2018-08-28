package login.services;
/*
Author:<Thomas> 
Create date: <2017/11/7>
Description: 登入畫面的控制
*/

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Login implements Serializable,Cloneable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String account;
	private String fullName;
	private String password;
	private String UR_STUDENT;
	private String UR_TEACHER;
	private String UR_PRINCIPAL;
	private String UR_ADMIN;
	private Date EXPIRYDATE;
	private Timestamp LAST_LOGIN;
	
	/**public Login(String account, String fullName, String password) {
		this.account = account;				//使用者帳號
		this.fullName = fullName;			//使用者名字
		this.password = password;			//使用者密碼
	}**/
	
	public Login(String account, String fullName, String password,String UR_STUDENT,String UR_TEACHER,String UR_PRINCIPAL,String UR_ADMIN,Date EXPIRYDATE, Timestamp LAST_LOGIN) {
		this.account = account;				//使用者帳號
		this.fullName = fullName;			//使用者名字
		this.password = password;			//使用者密碼
		this.UR_STUDENT = UR_STUDENT;		//學生權限
		this.UR_TEACHER = UR_TEACHER;		//老師權限
		this.UR_PRINCIPAL = UR_PRINCIPAL;	//校長權限
		this.UR_ADMIN = UR_ADMIN;			//管理者權限
		this.EXPIRYDATE = EXPIRYDATE;		//註冊日期
		this.LAST_LOGIN = LAST_LOGIN;		//最後登入日期,拿來判斷登入間隔用
	}
	
	public Login() {
	}

	public String getaccount() {
		return account;
	}
	public void setaccount(String account) {
		this.account = account;
	}	
	public String getfullName() {
		return fullName;
	}
	public void setfullName(String fullName) {
		this.fullName = fullName;
	}	
	public String getpassword() {
		return password;
	}
	public void setpassword(String password) {
		this.password = password;
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
	public Date getEXPIRYDATE() {
		return EXPIRYDATE;
	}
	public void setEXPIRYDATE(Date EXPIRYDATE) {
		this.EXPIRYDATE = EXPIRYDATE;
	}
	public Timestamp getLAST_LOGIN() {
		return LAST_LOGIN;
	}
	public void setLAST_LOGIN(Timestamp LAST_LOGIN) {
		this.LAST_LOGIN = LAST_LOGIN;
	}

	public static Login clone(Login log) {
		try {
			return	(Login)log.clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}	
}
