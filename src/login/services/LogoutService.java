/*
Author:<Thomas> 
Create date: <2018/02/09>
Description: 登出
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
		Login usridentity= LoginService.getUserCredential();//取得使用者的資訊
		LoginService.updlastlogout(usridentity.getaccount());//更新最後登出時間
		LoginService.logout();//將session清除
		Executions.sendRedirect("/LoginSystem/login.zul");//這邊之後寫網址(http://www.xx.com/login)或寫不同zul讀不同function
	}
	/**@Listen("onClick=#logout_child")
	public void doLogout_exam(){//這是給子目錄使用的
		Login usridentity= LoginService.getUserCredential();//取得使用者的資訊
		LoginService.updlastlogout(usridentity.getaccount());//更新最後登出時間
		LoginService.logout();
		Executions.sendRedirect("/LoginSystem/login.zul");//這邊之後寫網址(http://www.xx.com/login)或寫不同zul讀不同function
	}**/
	@Listen("onClick=#cancel")
	public void docancel(){//考試時取消使用
		Executions.sendRedirect("/ExaminationSystem/exam_main.zul");
	}
	
}
