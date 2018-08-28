package next.services.queryscores;
/*
Author:<Thomas> 
Create date: <2018/1/11>
Description: 分數查詢
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
	private ListModel<Scoredata> ScoredataModel;						//zul要用的ListModel
	private ListModel<Scoredata> PointModel;							//分數要用的ListModel
	private ListModel<Exam> recorddataModel;							//record.zul要用的ListModel
	String User_Acc;													//使用者帳號
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();

	public ScoredataViewModel() {										//這邊實現了zul檔要的資料
		ScoredataService Service = new ScoredataServiceImpl();
		ScoredataModel = new ListModelList<Scoredata>(Service.findAll());
		Session s = Sessions.getCurrent(); 
		if(s.getAttribute("Rec_data") != null ){		//如果Rec_data有資料(代表選擇) 就設定recorddataModel
			recorddataModel = (ListModel<Exam>) s.getAttribute("Rec_data");
		}
	}
	public ListModel<Scoredata> getScoredataModel() {					//querypoint.zul是讀這個
		return ScoredataModel;
	}
	public ListModel<Exam> getrecordataModel() {						//record.zul是讀這個
		return recorddataModel;
	}
	public ListModel<Scoredata> getPointModel() {						//積分是讀這個
		return PointModel;
	}
	
    @Command
    //取得紀錄檔
	public void get_record(@BindingParam("paramx") String id){
		//ScoredataService Service = new ScoredataServiceImpl();
		recorddataModel = new ListModelList<Exam>(Service.findRecord(id));
		Session s = Sessions.getCurrent();//設定session
		s.setAttribute("Rec_data", recorddataModel);//將recorddataModel設定成名為Rec_data的session
		Executions.sendRedirect("exam_review.zul");//跳轉到詳解頁面
	}
    
	@Init
	//Init=讀取一次,在載入這個ViewModel時,取得session中的usr_account並塞入User_Acc	
	public void init(@ContextParam(ContextType.SESSION) Session session){
		User_Acc = usridentity.getaccount();
		//User_Acc = (String) session.getAttribute("usr_account");
		PointModel =  new ListModelList<Scoredata>(Service.getpoint(User_Acc));
	}
}
