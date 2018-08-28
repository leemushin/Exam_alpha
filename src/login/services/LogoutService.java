/*
Author:<Thomas> 
Create date: <2018/02/09>
Description: �n�X
*/
package login.services;

import login.services.LoginServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

public class LogoutService extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	
	LoginService LoginService = new LoginServiceImpl();
	
	@Listen("onClick=#logout")
	public void doLogout(){
		Login usridentity= LoginService.getUserCredential();//���o�ϥΪ̪���T
		LoginService.updlastlogout(usridentity.getaccount());//��s�̫�n�X�ɶ�
		LoginService.logout();//�Nsession�M��
		Executions.sendRedirect("/LoginSystem/login.zul");//�o�䤧��g���}(http://www.xx.com/login)�μg���PzulŪ���Pfunction
	}
	/**@Listen("onClick=#logout_child")
	public void doLogout_exam(){//�o�O���l�ؿ��ϥΪ�
		Login usridentity= LoginService.getUserCredential();//���o�ϥΪ̪���T
		LoginService.updlastlogout(usridentity.getaccount());//��s�̫�n�X�ɶ�
		LoginService.logout();
		Executions.sendRedirect("/LoginSystem/login.zul");//�o�䤧��g���}(http://www.xx.com/login)�μg���PzulŪ���Pfunction
	}**/
	@Listen("onClick=#cancel")
	public void docancel(){//�Ҹծɨ����ϥ�
		Executions.sendRedirect("/ExaminationSystem/exam_main.zul");
	}
	
}
