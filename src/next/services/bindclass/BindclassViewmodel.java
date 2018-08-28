package next.services.bindclass;
import org.zkoss.bind.annotation.BindingParam;
/*
Author:<Thomas> 
Create date: <2018/1/31>
Description: �Z�Ŭd��
*/
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

import next.services.bindclass.Bindclass;
import next.services.bindclass.BindclassService;
import next.services.bindclass.BindclassServiceImpl;

public class BindclassViewmodel {
	
	private BindclassService service = new BindclassServiceImpl();
	private ListModel<Bindclass> BindclassModel;
	private ListModel<Bindclass> LastclassModel;
	private boolean readonly = true;//�d�߼Ҧ��ϥ�
	private String class_keyin;//�j�M����
	private String upd_NO;//�Z�ťN��
	private String upd_NAME;//�Z�ŦW��
	private String upd_GRADE;//�Z�Ŧ~��
	private String upd_TEACHER;//�Z�žɮv
	private String upd_SCHOOL;//�Z�ũ��ݾǮ�
	private String upd_MEMBER;//�Z�Ŧ���
	int before_grade = 7;//�n�ɯŪ��Ǧ~(�w�]��7�~��)
	int after_grade = 8;//�N�n�ɯŨ쪺�Ǧ~(�w�]��8�~��)
	
	
	public boolean isReadonly() {//�P�_textbox��disabled
		return readonly;
	}
	public void setReadonly(boolean ro) {//�P�_textbox��disabled
		this.readonly=ro;
	}
	public void setclass_keyin(String class_keyin) {//��J���Z��
		this.class_keyin = class_keyin;
	}
	public String getclass_keyin() {//��J���Z��
		return class_keyin;
	}
	public String getupd_NO() {//�Z�ťN��
		return upd_NO;
	}
	public void setupd_NO(String upd_NO) {//�Z�ťN��
		this.upd_NO = upd_NO;
	}
	public String getupd_NAME() {//�Z�ŦW��
		return upd_NAME;
	}
	public void setupd_NAME(String upd_NAME) {//�Z�ŦW��
		this.upd_NAME = upd_NAME;
	}
	public String getupd_GRADE() {//�Z�Ŧ~��
		return upd_GRADE;
	}
	public void setupd_GRADE(String upd_GRADE) {//�Z�Ŧ~��
		this.upd_GRADE = upd_GRADE;
	}
	public String getupd_TEACHER() {//�Z�žɮv
		return upd_TEACHER;
	}
	public void setupd_TEACHER(String upd_TEACHER) {//�Z�žɮv
		this.upd_TEACHER = upd_TEACHER;
	}
	public String getupd_SCHOOL() {//�Z�ũ��ݾǮ�
		return upd_SCHOOL;
	}
	public void setupd_SCHOOL(String upd_SCHOOL) {//�Z�ũ��ݾǮ�
		this.upd_SCHOOL = upd_SCHOOL;
	}
	public String getupd_MEMBER() {//�Z�Ŧ���
		return upd_MEMBER;
	}
	public void setupd_MEMBER(String upd_MEMBER) {//�Z�Ŧ���
		this.upd_MEMBER = upd_MEMBER;
	}
	public ListModel<Bindclass> getlastModel(){
		LastclassModel =  new ListModelList<Bindclass>(service.searchlast());
		return LastclassModel;
	}
	public ListModel<Bindclass> getclassModel(){
		return BindclassModel;
	}
	//�j�M�Z��
	@Command
	@NotifyChange("*")//�q��binder���s���J
	public void search_class(){
		BindclassModel = new ListModelList<Bindclass>(service.searchcls(class_keyin));
	}
	//��s�Z�Ÿ��
	@Command
	@NotifyChange("*")//�q��binder���s���J
	public void go_update(){
		BindclassModel = new ListModelList<Bindclass>(service.doupdate(upd_NO,upd_NAME,upd_GRADE,upd_TEACHER,upd_SCHOOL,upd_MEMBER));
		setReadonly(true);
	}
	//�R���Z��
	@Command
	public void do_delete(){
		Messagebox.show("�Y�N�R���ӯZ�Ÿ��,�O�_�T�{����?", "�T�{", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION
				, new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event evt) throws InterruptedException {
		        if (evt.getName().equals("onOK")) {
		        	service.deletecls(class_keyin);}//���UOK�����R���ʧ@
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
	//�Z�Ŧ~�Ŵ���
	@Command
	public void cls_grade_upd(){
		if(before_grade == after_grade) {
			Messagebox.show("�~�Ť��i�۵�", "����", Messagebox.OK, Messagebox.EXCLAMATION);
			return;//���X
		}
		else {
			service.upd_cls_grade(before_grade, after_grade);
		}	
	}
	
	
}
