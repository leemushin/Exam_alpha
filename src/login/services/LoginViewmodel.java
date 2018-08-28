/*
Author:<Thomas> 
Create date: <2017/11/7>
Description: 登入畫面的控制
*/
package login.services;
import java.util.Date;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import login.services.Login;
import login.services.LoginService;
import login.services.LoginServiceImpl;

public class LoginViewmodel extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	//連結組件
	@Wire
	Textbox account;
	@Wire
	Textbox password;
	@Wire
	Label message;
	//連結model
	LoginService LoginService = new LoginServiceImpl();
	//按下登入按鈕
	@Listen("onClick=#login; onOK=#loginWin")
	public void doLogin(){
		String acc = account.getValue();//取得輸入的帳號
		String pd = password.getValue();//取得輸入的密碼
		if(!LoginService.User(acc,pd)){//以上面的帳密去做搜尋,若與資料庫不符合則跳出錯誤並中止
			message.setValue("帳號或密碼錯誤!!");
			return;
		}
		Login usridentity= LoginService.getUserCredential();//取得使用者的資訊
		Date EXPIRY = usridentity.getEXPIRYDATE();//取得該帳號的到期日期
		java.util.Date lastlogin = usridentity.getLAST_LOGIN();//取得該帳號的最後登入時間
		java.util.Date loginday = new java.util.Date();//取得今天日期
		long intervaltime = (loginday.getTime()-lastlogin.getTime())/(60*1000);//登入間隔時間(換算後為分鐘)
		long daydiff = (EXPIRY.getTime()-loginday.getTime())/((24*60*60*1000));//相差日期/24小時*60分*60秒*1000(秒)
    	//判斷是否重複登入的程式碼,若不符合則跳出登入程序
		String islogin = LoginService.iflogin(usridentity.getaccount());//查詢使用者的登入狀態
		if(islogin.equals("1")&&intervaltime<10) {//如果已經登入,而且登入間隔小於10分鐘
			Messagebox.show("禁止重複登入", "警告", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		//如果通過上面的檢核,判斷帳號是否還有效
		if (daydiff >= 0) {//如果還在有效日期內
			Messagebox.show(usridentity.getfullName()+",您的有效日期還有"+daydiff+"日", "歡迎使用,請點選確認登入!", Messagebox.OK,Messagebox.INFORMATION
					, new org.zkoss.zk.ui.event.EventListener() {
					    public void onEvent(Event evt) throws InterruptedException {
					        if (evt.getName().equals("onOK")) {
					        	LoginService.updlastlogin(usridentity.getaccount(),loginday);//修改最後登入時間
					        	/**Executions.sendRedirect("../Main/main2.zul");**/
					        	Executions.sendRedirect("../Main/mainpage.zul");}
					    }
					});
		}
		else {//超過有效日期,告知已經過期,不給予登入
			Session sess = Sessions.getCurrent();
			sess.removeAttribute("userCredential");
			Messagebox.show("您的到期日期為"+EXPIRY+"已經過期", "提醒", Messagebox.OK, Messagebox.INFORMATION);
		}
	}
}
