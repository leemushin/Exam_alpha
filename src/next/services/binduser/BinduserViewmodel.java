package next.services.binduser;
/*
Author:<Thomas> 
Create date: <2018/1/25>
Description: �ϥΪ̬d��
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
	private boolean readonly = true;//�d�߼Ҧ��ϥ�
	private String account_keyin;//�j�M����
	private String upd_USERNAME;//�ϥΪ̩m�W
	private String upd_ACCOUNT;//�ϥΪ̱b��
	private String upd_PASSWORD;//�ϥΪ̱K�X
	private String upd_PWD_HINT;//�ϥΪ̱K�X����
	private String upd_STUDENT;//���ݦ~��
	private String upd_SCHOOL;//���ݾǮ�
	private Date upd_REGISTERED;//���U��
	private Date upd_EXPIRYDATE;//�����
	private String upd_TEACHER;//�Ѯv���O
	private String upd_PRINCIPAL;//�ժ����O
	private String upd_ADMIN;//�޲z�̵��O
	private String remark;//�ϥΪ̳Ƶ�
	int before_grade = 7;//�n�ɯŪ��Ǧ~(�w�]��7�~��)
	int after_grade = 8;//�N�n�ɯŨ쪺�Ǧ~(�w�]��8�~��)
	
	private BinduserService Service = new BinduserServiceImpl();
	private ListModel<Binduser> BinduserModel;
	
	public boolean isReadonly() {//�P�_textbox��disabled
		return readonly;
	}
	public void setReadonly(boolean ro) {//�P�_textbox��disabled
		this.readonly=ro;
	}
	
	public void setaccount_keyin(String setaccount_keyin) {//��J���b��
		this.account_keyin = setaccount_keyin;
	}
	public String getaccount_keyin() {//��J���b��
		return account_keyin;
	}
	public void setupd_USERNAME(String upd_USERNAME) {//�ϥΪ̩m�W
		this.upd_USERNAME = upd_USERNAME;
	}
	public String getupd_USERNAME() {//�ϥΪ̩m�W
		return upd_USERNAME;
	}
	public void setupd_ACCOUNT(String upd_ACCOUNT) {//�ϥΪ̱b��
		this.upd_ACCOUNT = upd_ACCOUNT;
	}
	public String getupd_ACCOUNT() {//�ϥΪ̱b��
		return upd_ACCOUNT;
	}
	public void setupd_PASSWORD(String upd_PASSWORD) {//�ϥΪ̱K�X
		this.upd_PASSWORD = upd_PASSWORD;
	}
	public String getupd_PASSWORD() {//�ϥΪ̱K�X
		return upd_PASSWORD;
	}
	public void setupd_PWD_HINT(String upd_PWD_HINT) {//�ϥΪ̱K�X����
		this.upd_PWD_HINT = upd_PWD_HINT;
	}
	public String getupd_PWD_HINT() {//�ϥΪ̱K�X����
		return upd_PWD_HINT;
	}
	public void setupd_STUDENT(String upd_STUDENT) {//���ݦ~��
		this.upd_STUDENT = upd_STUDENT;
	}
	public String getupd_STUDENT() {//���ݦ~��
		return upd_STUDENT;
	}
	public void setupd_SCHOOL(String upd_SCHOOL) {//���ݾǮ�
		this.upd_SCHOOL = upd_SCHOOL;
	}
	public String getupd_SCHOOL() {//���ݾǮ�
		return upd_SCHOOL;
	}
	public void setupd_REGISTERED(Date upd_REGISTERED) {//���U��
		this.upd_REGISTERED = upd_REGISTERED;
	}
	public Date getupd_REGISTERED() {//���U��
		return upd_REGISTERED;
	}
	public void setupd_EXPIRYDATE(Date upd_EXPIRYDATE) {//�����
		this.upd_EXPIRYDATE = upd_EXPIRYDATE;
	}
	public Date getupd_EXPIRYDATE() {//�����
		return upd_EXPIRYDATE;
	}
	public void setupd_TEACHER(String upd_TEACHER) {//�Ѯv���O
		this.upd_TEACHER = upd_TEACHER;
	}
	public String getupd_TEACHER() {//�Ѯv���O
		return upd_TEACHER;
	}
	public void setupd_PRINCIPAL(String upd_PRINCIPAL) {//�ժ����O
		this.upd_PRINCIPAL = upd_PRINCIPAL;
	}
	public String getupd_PRINCIPAL() {//�ժ����O
		return upd_PRINCIPAL;
	}
	public void setupd_ADMIN(String upd_ADMIN) {//�޲z�̵��O
		this.upd_ADMIN = upd_ADMIN;
	}
	public String getupd_ADMIN() {//�޲z�̵��O
		return upd_ADMIN;
	}
	public void setremark(String remark) {//�ϥΪ̳Ƶ�
		this.remark = remark;
	}
	public String getremark() {//�ϥΪ̳Ƶ�
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
	//�d�ߨϥΪ�
	@Command
	@NotifyChange("*")//�q��binder���s���J
	public void search_user(){
		BinduserModel = new ListModelList<Binduser>(Service.searchusr(account_keyin));
	}
	//��s�ϥΪ̸��
	@Command
	@NotifyChange("*")//�q��binder���s���J
	public void go_update(){
		BinduserModel = new ListModelList<Binduser>(Service.doupdate(upd_USERNAME,upd_ACCOUNT,upd_PASSWORD,
				upd_PWD_HINT,upd_STUDENT,upd_SCHOOL,upd_REGISTERED,upd_EXPIRYDATE,upd_TEACHER,upd_PRINCIPAL,upd_ADMIN,remark));
		setReadonly(true);
    }
	//�R���ϥΪ�
	@Command
	public void do_delete(){
		Messagebox.show("�Y�N�R���ӨϥΪ̩Ҧ����,�O�_�T�{����?", "�T�{", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION
				, new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event evt) throws InterruptedException {
		        if (evt.getName().equals("onOK")) {
		        	Service.deleteusr(account_keyin);}//���UOK�����R���ʧ@
		    }
		});
	}
	//���o���ɯŪ��~��
	@Command
	public void get_beforegrade(@BindingParam("paramx") int grade){
		before_grade = grade;
	}
	//���o�N�ɯŨ�~��
	@Command
	public void get_aftergrade(@BindingParam("paramx") int grade){
		after_grade = grade;
	}
	//�ϥΪ̦~�Ŵ���
	@Command
	public void usr_grade_upd(){
		if(before_grade == after_grade) {
			Messagebox.show("�~�Ť��i�۵�", "����", Messagebox.OK, Messagebox.EXCLAMATION);
			return;//���X
		}
		else {
			Service.upd_usr_grade(before_grade, after_grade);
		}
		
	}
}
