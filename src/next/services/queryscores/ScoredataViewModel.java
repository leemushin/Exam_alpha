package next.services.queryscores;
/*
Author:<Thomas> 
Create date: <2018/1/11>
Description: ���Ƭd��
*/
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import exam.service.Exam;
import login.services.Login;
import login.services.LoginService;
import login.services.LoginServiceImpl;
import next.services.queryscores.Scoredata;
import next.services.queryscores.ScoredataService;
import next.services.queryscores.ScoredataServiceImpl;


public class ScoredataViewModel {
	
	private ScoredataService Service = new ScoredataServiceImpl();
	private ListModel<Scoredata> ScoredataModel;						//zul�n�Ϊ�ListModel
	private ListModel<Scoredata> PointModel;							//���ƭn�Ϊ�ListModel
	private ListModel<Exam> recorddataModel;							//record.zul�n�Ϊ�ListModel
	String User_Acc;													//�ϥΪ̱b��
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();

	public ScoredataViewModel() {										//�o���{�Fzul�ɭn�����
		ScoredataService Service = new ScoredataServiceImpl();
		ScoredataModel = new ListModelList<Scoredata>(Service.findAll());
		Session s = Sessions.getCurrent(); 
		if(s.getAttribute("Rec_data") != null ){		//�p�GRec_data�����(�N����) �N�]�wrecorddataModel
			recorddataModel = (ListModel<Exam>) s.getAttribute("Rec_data");
		}
	}
	public ListModel<Scoredata> getScoredataModel() {					//querypoint.zul�OŪ�o��
		return ScoredataModel;
	}
	public ListModel<Exam> getrecordataModel() {						//record.zul�OŪ�o��
		return recorddataModel;
	}
	public ListModel<Scoredata> getPointModel() {						//�n���OŪ�o��
		return PointModel;
	}
	
    @Command
    //���o������
	public void get_record(@BindingParam("paramx") String id){
		//ScoredataService Service = new ScoredataServiceImpl();
		recorddataModel = new ListModelList<Exam>(Service.findRecord(id));
		Session s = Sessions.getCurrent();//�]�wsession
		s.setAttribute("Rec_data", recorddataModel);//�NrecorddataModel�]�w���W��Rec_data��session
		Executions.sendRedirect("exam_review.zul");//�����Ըѭ���
	}
    
	@Init
	//Init=Ū���@��,�b���J�o��ViewModel��,���osession����usr_account�ö�JUser_Acc	
	public void init(@ContextParam(ContextType.SESSION) Session session){
		User_Acc = usridentity.getaccount();
		//User_Acc = (String) session.getAttribute("usr_account");
		PointModel =  new ListModelList<Scoredata>(Service.getpoint(User_Acc));
	}
}
