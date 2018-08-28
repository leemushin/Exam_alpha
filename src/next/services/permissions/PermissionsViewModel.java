package next.services.permissions;

import login.services.Login;
import login.services.LoginService;
import login.services.LoginServiceImpl;
//�v���P�_��
public class PermissionsViewModel {
	
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();//���o�n�J�ɩҼ����v��
	
	String admin_tmp = usridentity.getUR_ADMIN();//���o�޲z���v��
	String grade_tmp = usridentity.getUR_STUDENT();//���o�ǥͦ~��
	String Teacher_tmp = usridentity.getUR_TEACHER();//���o�Юv�v��
	String Principal_tmp = usridentity.getUR_PRINCIPAL();//���o�ժ��v��
	int Teacher_Permission = Integer.parseInt(Teacher_tmp);
	int Principal_Permission = Integer.parseInt(Principal_tmp);
	int Student_grade = Integer.parseInt(grade_tmp);
	int Admin_Permission = Integer.parseInt(admin_tmp);
	
	public boolean isAdmin() {//�P�_�޲z�̨ϥ��v��(disable�޿�,�O�޲z�̫h�^��false,disable:false �N�i�ϥ�) 
		if(Admin_Permission == 1) {
			return false;
		}
		else {
			return true;
		}
	}
	public boolean isAdmin_visible() {//�P�_�޲z�̨ϥ��v��(visible�޿�ۤ�,�O�޲z�̫h�^��true,visible:true �~�ݱo��)
		if(Admin_Permission == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isTeacher() {//�P�_�Ѯv�ϥ��v��(disable�޿�,�O�Ѯv�h�^��false,disable:false �N�i�ϥ�) 
		if(Teacher_Permission == 1 || Principal_Permission == 1) {
			return false;
		}
		else {
			return true;
		}
	}
	public boolean isTeacher_visible() {//�P�_�Ѯv�ϥ��v��(visible�޿�ۤ�,�O�Ѯv�h�^��true,visible:true �~�ݱo��)
		if(Teacher_Permission == 1 || Principal_Permission == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isPrincipal() {//�P�_�ժ��ϥ��v��(disable�޿�,�O�ժ��h�^��false,disable:false �N�i�ϥ�) 
		if(Principal_Permission == 1) {
			return false;	
		}
		else {
			return true;
		}
	}
	public boolean isPrincipal_visible() {//�P�_�ժ��ϥ��v��(visible�޿�ۤ�,�O�ժ��h�^��true,visible:true �~�ݱo��)
		if(Principal_Permission == 1) {
			return true;	
		}
		else {
			return false;
		}
	}
	public boolean isgrade_7() {//�P�_7�~�Ũϥ��v��,�j��C�~�ũξ֦��v���v���i�ϥ�
		if(Student_grade >= 7 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return false;
		}
		else {
			return true;
		}
	}
	public boolean isgrade_7_visible() {//�P�_7�~�Ũϥ��v��,�j��C�~�ũξ֦��v���v���i�ϥ�(visible�޿�)
		if(Student_grade >= 7 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isgrade_8() {//�P�_8�~�Ũϥ��v��,�j��K�~�ũξ֦��v���v���i�ϥ�
		if(Student_grade >= 8 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return false;
		}
		else {
			return true;
		}
	}
	public boolean isgrade_8_visible() {//�P�_8�~�Ũϥ��v��,�j��K�~�ũξ֦��v���v���i�ϥ�(visible�޿�)
		if(Student_grade >= 8 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isgrade_9() {//�P�_9�~�Ũϥ��v��,�j��E�~�ũξ֦��v���v���i�ϥ�
		if(Student_grade >= 9 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return false;
		}
		else {
			return true;
		}
	}
	public boolean isgrade_9_visible() {//�P�_9�~�Ũϥ��v��,�j��E�~�ũξ֦��v���v���i�ϥ�(visible�޿�)
		if(Student_grade >= 9 || Teacher_Permission == 1 || Principal_Permission == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	

}
