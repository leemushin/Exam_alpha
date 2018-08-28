package next.services.bindclass;
import org.zkoss.bind.annotation.BindingParam;
/*
Author:<Thomas> 
Create date: <2018/1/31>
Description: 班級查詢
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
	private boolean readonly = true;//查詢模式使用
	private String class_keyin;//搜尋條件
	private String upd_NO;//班級代號
	private String upd_NAME;//班級名稱
	private String upd_GRADE;//班級年級
	private String upd_TEACHER;//班級導師
	private String upd_SCHOOL;//班級所屬學校
	private String upd_MEMBER;//班級成員
	int before_grade = 7;//要升級的學年(預設為7年級)
	int after_grade = 8;//將要升級到的學年(預設為8年級)
	
	
	public boolean isReadonly() {//判斷textbox的disabled
		return readonly;
	}
	public void setReadonly(boolean ro) {//判斷textbox的disabled
		this.readonly=ro;
	}
	public void setclass_keyin(String class_keyin) {//輸入的班級
		this.class_keyin = class_keyin;
	}
	public String getclass_keyin() {//輸入的班級
		return class_keyin;
	}
	public String getupd_NO() {//班級代號
		return upd_NO;
	}
	public void setupd_NO(String upd_NO) {//班級代號
		this.upd_NO = upd_NO;
	}
	public String getupd_NAME() {//班級名稱
		return upd_NAME;
	}
	public void setupd_NAME(String upd_NAME) {//班級名稱
		this.upd_NAME = upd_NAME;
	}
	public String getupd_GRADE() {//班級年級
		return upd_GRADE;
	}
	public void setupd_GRADE(String upd_GRADE) {//班級年級
		this.upd_GRADE = upd_GRADE;
	}
	public String getupd_TEACHER() {//班級導師
		return upd_TEACHER;
	}
	public void setupd_TEACHER(String upd_TEACHER) {//班級導師
		this.upd_TEACHER = upd_TEACHER;
	}
	public String getupd_SCHOOL() {//班級所屬學校
		return upd_SCHOOL;
	}
	public void setupd_SCHOOL(String upd_SCHOOL) {//班級所屬學校
		this.upd_SCHOOL = upd_SCHOOL;
	}
	public String getupd_MEMBER() {//班級成員
		return upd_MEMBER;
	}
	public void setupd_MEMBER(String upd_MEMBER) {//班級成員
		this.upd_MEMBER = upd_MEMBER;
	}
	public ListModel<Bindclass> getlastModel(){
		LastclassModel =  new ListModelList<Bindclass>(service.searchlast());
		return LastclassModel;
	}
	public ListModel<Bindclass> getclassModel(){
		return BindclassModel;
	}
	//搜尋班級
	@Command
	@NotifyChange("*")//通知binder重新載入
	public void search_class(){
		BindclassModel = new ListModelList<Bindclass>(service.searchcls(class_keyin));
	}
	//更新班級資料
	@Command
	@NotifyChange("*")//通知binder重新載入
	public void go_update(){
		BindclassModel = new ListModelList<Bindclass>(service.doupdate(upd_NO,upd_NAME,upd_GRADE,upd_TEACHER,upd_SCHOOL,upd_MEMBER));
		setReadonly(true);
	}
	//刪除班級
	@Command
	public void do_delete(){
		Messagebox.show("即將刪除該班級資料,是否確認執行?", "確認", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION
				, new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event evt) throws InterruptedException {
		        if (evt.getName().equals("onOK")) {
		        	service.deletecls(class_keyin);}//按下OK後執行刪除動作
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
	//班級年級提升
	@Command
	public void cls_grade_upd(){
		if(before_grade == after_grade) {
			Messagebox.show("年級不可相等", "提醒", Messagebox.OK, Messagebox.EXCLAMATION);
			return;//跳出
		}
		else {
			service.upd_cls_grade(before_grade, after_grade);
		}	
	}
	
	
}
