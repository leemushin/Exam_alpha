/*
Author:<Thomas> 
Create date: <2017/11/7>
Description: �n�J�e��������
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
	//�s���ե�
	@Wire
	Textbox account;
	@Wire
	Textbox password;
	@Wire
	Label message;
	//�s��model
	LoginService LoginService = new LoginServiceImpl();
	//���U�n�J���s
	@Listen("onClick=#login; onOK=#loginWin")
	public void doLogin(){
		String acc = account.getValue();//���o��J���b��
		String pd = password.getValue();//���o��J���K�X
		if(!LoginService.User(acc,pd)){//�H�W�����b�K�h���j�M,�Y�P��Ʈw���ŦX�h���X���~�ä���
			message.setValue("�b���αK�X���~!!");
			return;
		}
		Login usridentity= LoginService.getUserCredential();//���o�ϥΪ̪���T
		Date EXPIRY = usridentity.getEXPIRYDATE();//���o�ӱb����������
		java.util.Date lastlogin = usridentity.getLAST_LOGIN();//���o�ӱb�����̫�n�J�ɶ�
		java.util.Date loginday = new java.util.Date();//���o���Ѥ��
		long intervaltime = (loginday.getTime()-lastlogin.getTime())/(60*1000);//�n�J���j�ɶ�(����ᬰ����)
		long daydiff = (EXPIRY.getTime()-loginday.getTime())/((24*60*60*1000));//�ۮt���/24�p��*60��*60��*1000(��)
    	//�P�_�O�_���Ƶn�J���{���X,�Y���ŦX�h���X�n�J�{��
		String islogin = LoginService.iflogin(usridentity.getaccount());//�d�ߨϥΪ̪��n�J���A
		if(islogin.equals("1")&&intervaltime<10) {//�p�G�w�g�n�J,�ӥB�n�J���j�p��10����
			Messagebox.show("�T��Ƶn�J", "ĵ�i", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		//�p�G�q�L�W�����ˮ�,�P�_�b���O�_�٦���
		if (daydiff >= 0) {//�p�G�٦b���Ĥ����
			Messagebox.show(usridentity.getfullName()+",�z�����Ĥ���٦�"+daydiff+"��", "�w��ϥ�,���I��T�{�n�J!", Messagebox.OK,Messagebox.INFORMATION
					, new org.zkoss.zk.ui.event.EventListener() {
					    public void onEvent(Event evt) throws InterruptedException {
					        if (evt.getName().equals("onOK")) {
					        	LoginService.updlastlogin(usridentity.getaccount(),loginday);//�ק�̫�n�J�ɶ�
					        	/**Executions.sendRedirect("../Main/main2.zul");**/
					        	Executions.sendRedirect("../Main/mainpage.zul");}
					    }
					});
		}
		else {//�W�L���Ĥ��,�i���w�g�L��,�������n�J
			Session sess = Sessions.getCurrent();
			sess.removeAttribute("userCredential");
			Messagebox.show("�z����������"+EXPIRY+"�w�g�L��", "����", Messagebox.OK, Messagebox.INFORMATION);
		}
	}
}
