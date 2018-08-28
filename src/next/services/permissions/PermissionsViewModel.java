package next.services.permissions;

import login.services.Login;
import login.services.LoginService;
import login.services.LoginServiceImpl;
//權限判斷用
public class PermissionsViewModel {
	
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();//取得登入時所撈的權限
	
	String admin_tmp = usridentity.getUR_ADMIN();//取得管理者權限
	String grade_tmp = usridentity.getUR_STUDENT();//取得學生年級
	String Teacher_tmp = usridentity.getUR_TEACHER();//取得教師權限
	String Principal_tmp = usridentity.getUR_PRINCIPAL();//取得校長權限
	int Teacher_Permission = Integer.parseInt(Teacher_tmp);
	int Principal_Permission = Integer.parseInt(Principal_tmp);
	int Student_grade = Integer.parseInt(grade_tmp);
	int Admin_Permission = Integer.parseInt(admin_tmp);
	
	public boolean isAdmin() {//判斷管理者使用權限(disable邏輯,是管理者則回傳false,disable:false 就可使用) 
		if(Admin_Permission == 1) {
			return false;
		}
		else {
			return true;
		}
	}
	public boolean isAdmin_visible() {//判斷管理者使用權限(visible邏輯相反,是管理者則回傳true,visible:true 才看得到)
		if(Admin_Permission == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isTeacher() {//判斷老師使用權限(disable邏輯,是老師則回傳false,disable:false 就可使用) 
		if(Teacher_Permission == 1 || Principal_Permission == 1) {
			return false;
		}
		else {
			return true;
		}
	}
	public boolean isTeacher_visible() {//判斷老師使用權限(visible邏輯相反,是老師則回傳true,visible:true 才看得到)
		if(Teacher_Permission == 1 || Principal_Permission == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isPrincipal() {//判斷校長使用權限(disable邏輯,是校長則回傳false,disable:false 就可使用) 
		if(Principal_Permission == 1) {
			return false;	
		}
		else {
			return true;
		}
	}
	public boolean isPrincipal_visible() {//判斷校長使用權限(visible邏輯相反,是校長則回傳true,visible:true 才看得到)
		if(Principal_Permission == 1) {
			return true;	
		}
		else {
			return false;
		}
	}
	public boolean isgrade_7() {//判斷7年級使用權限,大於七年級或擁有師長權限可使用
		if(Student_grade >= 7 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return false;
		}
		else {
			return true;
		}
	}
	public boolean isgrade_7_visible() {//判斷7年級使用權限,大於七年級或擁有師長權限可使用(visible邏輯)
		if(Student_grade >= 7 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isgrade_8() {//判斷8年級使用權限,大於八年級或擁有師長權限可使用
		if(Student_grade >= 8 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return false;
		}
		else {
			return true;
		}
	}
	public boolean isgrade_8_visible() {//判斷8年級使用權限,大於八年級或擁有師長權限可使用(visible邏輯)
		if(Student_grade >= 8 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isgrade_9() {//判斷9年級使用權限,大於九年級或擁有師長權限可使用
		if(Student_grade >= 9 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return false;
		}
		else {
			return true;
		}
	}
	public boolean isgrade_9_visible() {//判斷9年級使用權限,大於九年級或擁有師長權限可使用(visible邏輯)
		if(Student_grade >= 9 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	

}
