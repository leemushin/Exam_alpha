package next.services.binduser;
/*
Author:<Thomas> 
Create date: <2018/1/25>
Description: 使用者查詢
*/
import java.util.Date;
import java.util.List;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;

import next.services.binduser.Binduser;
import next.services.binduser.BinduserService;
import next.services.binduser.BinduserServiceImpl;


public class BinduserViewmodel {
	private BinduserDAO BinduserDAO = new BinduserDAO();
	private Binduser selectedUser , newUser = new Binduser();
	private boolean readonly = true;//查詢模式使用
	private String account_keyin;//搜尋條件
	private String upd_USERNAME;//使用者姓名
	private String upd_ACCOUNT;//使用者帳號
	private String upd_PASSWORD;//使用者密碼
	private String upd_PWD_HINT;//使用者密碼提示
	private String upd_STUDENT;//所屬年級
	private String upd_SCHOOL;//所屬學校
	private Date upd_REGISTERED;//註冊日
	private Date upd_EXPIRYDATE;//到期日
	private String upd_TEACHER;//老師註記
	private String upd_PRINCIPAL;//校長註記
	private String upd_ADMIN;//管理者註記
	private String remark;//使用者備註
	int before_grade = 7;//要升級的學年(預設為7年級)
	int after_grade = 8;//將要升級到的學年(預設為8年級)
	
	private BinduserService Service = new BinduserServiceImpl();
	private ListModel<Binduser> BinduserModel;
	
	public boolean isReadonly() {//判斷textbox的disabled
		return readonly;
	}
	public void setReadonly(boolean ro) {//判斷textbox的disabled
		this.readonly=ro;
	}
	
	public void setaccount_keyin(String setaccount_keyin) {//輸入的帳號
		this.account_keyin = setaccount_keyin;
	}
	public String getaccount_keyin() {//輸入的帳號
		return account_keyin;
	}
	public void setupd_USERNAME(String upd_USERNAME) {//使用者姓名
		this.upd_USERNAME = upd_USERNAME;
	}
	public String getupd_USERNAME() {//使用者姓名
		return upd_USERNAME;
	}
	public void setupd_ACCOUNT(String upd_ACCOUNT) {//使用者帳號
		this.upd_ACCOUNT = upd_ACCOUNT;
	}
	public String getupd_ACCOUNT() {//使用者帳號
		return upd_ACCOUNT;
	}
	public void setupd_PASSWORD(String upd_PASSWORD) {//使用者密碼
		this.upd_PASSWORD = upd_PASSWORD;
	}
	public String getupd_PASSWORD() {//使用者密碼
		return upd_PASSWORD;
	}
	public void setupd_PWD_HINT(String upd_PWD_HINT) {//使用者密碼提示
		this.upd_PWD_HINT = upd_PWD_HINT;
	}
	public String getupd_PWD_HINT() {//使用者密碼提示
		return upd_PWD_HINT;
	}
	public void setupd_STUDENT(String upd_STUDENT) {//所屬年級
		this.upd_STUDENT = upd_STUDENT;
	}
	public String getupd_STUDENT() {//所屬年級
		return upd_STUDENT;
	}
	public void setupd_SCHOOL(String upd_SCHOOL) {//所屬學校
		this.upd_SCHOOL = upd_SCHOOL;
	}
	public String getupd_SCHOOL() {//所屬學校
		return upd_SCHOOL;
	}
	public void setupd_REGISTERED(Date upd_REGISTERED) {//註冊日
		this.upd_REGISTERED = upd_REGISTERED;
	}
	public Date getupd_REGISTERED() {//註冊日
		return upd_REGISTERED;
	}
	public void setupd_EXPIRYDATE(Date upd_EXPIRYDATE) {//到期日
		this.upd_EXPIRYDATE = upd_EXPIRYDATE;
	}
	public Date getupd_EXPIRYDATE() {//到期日
		return upd_EXPIRYDATE;
	}
	public void setupd_TEACHER(String upd_TEACHER) {//老師註記
		this.upd_TEACHER = upd_TEACHER;
	}
	public String getupd_TEACHER() {//老師註記
		return upd_TEACHER;
	}
	public void setupd_PRINCIPAL(String upd_PRINCIPAL) {//校長註記
		this.upd_PRINCIPAL = upd_PRINCIPAL;
	}
	public String getupd_PRINCIPAL() {//校長註記
		return upd_PRINCIPAL;
	}
	public void setupd_ADMIN(String upd_ADMIN) {//管理者註記
		this.upd_ADMIN = upd_ADMIN;
	}
	public String getupd_ADMIN() {//管理者註記
		return upd_ADMIN;
	}
	public void setremark(String remark) {//使用者備註
		this.remark = remark;
	}
	public String getremark() {//使用者備註
		return remark;
	}
	
	public Binduser getSelectedUser() {
		return selectedUser ;
	}
	public void setSelectedUser(Binduser selectedUser) {
		this.selectedUser = selectedUser ;
	}	
	
	public Binduser getnewUser() {
		return newUser ;
	}
	public void setnewUser(Binduser newUser) {
		this.newUser = newUser ;
	}	

	public List<Binduser> getUsers() {
		return BinduserDAO.findAll();
	}
	public ListModel<Binduser> getUserModel(){
		return BinduserModel;
	}
	//查詢使用者
	@Command
	@NotifyChange("*")//通知binder重新載入
	public void search_user(){
		BinduserModel = new ListModelList<Binduser>(Service.searchusr(account_keyin));
	}
	//更新使用者資料
	@Command
	@NotifyChange("*")//通知binder重新載入
	public void go_update(){
		BinduserModel = new ListModelList<Binduser>(Service.doupdate(upd_USERNAME,upd_ACCOUNT,upd_PASSWORD,
				upd_PWD_HINT,upd_STUDENT,upd_SCHOOL,upd_REGISTERED,upd_EXPIRYDATE,upd_TEACHER,upd_PRINCIPAL,upd_ADMIN,remark));
		setReadonly(true);
    }
	//刪除使用者
	@Command
	public void do_delete(){
		Messagebox.show("即將刪除該使用者所有資料,是否確認執行?", "確認", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION
				, new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event evt) throws InterruptedException {
		        if (evt.getName().equals("onOK")) {
		        	Service.deleteusr(account_keyin);}//按下OK後執行刪除動作
		    }
		});
	}
	//取得欲升級的年級
	@Command
	public void get_beforegrade(@BindingParam("paramx") int grade){
		before_grade = grade;
	}
	//取得將升級到年級
	@Command
	public void get_aftergrade(@BindingParam("paramx") int grade){
		after_grade = grade;
	}
	//使用者年級提升
	@Command
	public void usr_grade_upd(){
		if(before_grade == after_grade) {
			Messagebox.show("年級不可相等", "提醒", Messagebox.OK, Messagebox.EXCLAMATION);
			return;//跳出
		}
		else {
			Service.upd_usr_grade(before_grade, after_grade);
		}
		
	}
}
