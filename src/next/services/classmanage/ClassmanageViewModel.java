package next.services.classmanage;
/*
Author:<Thomas> 
Create date: <2018/1/19>
Description: 教師管理-班級管理
*/
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import login.services.Login;
import login.services.LoginService;
import login.services.LoginServiceImpl;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;

import next.services.classmanage.Classmanage;
import next.services.classmanage.ClassmanageService;
import next.services.classmanage.ClassmanageServiceImpl;
import next.services.queryscores.Scoredata;
import next.services.queryscores.ScoredataService;
import next.services.queryscores.ScoredataServiceImpl;

public class ClassmanageViewModel {
	
	private ClassmanageService Service = new ClassmanageServiceImpl();
	private ListModel<Classmanage> ClassmanageModel;	//manage_class用
	private ListModel<Classmanage> ClassPointModel;		//manage_clsdetail用(積分)
	private ListModel<Classmanage> ClassdetailModel;	//manage_clsdetail用
	private ListModel<Scoredata> PointModel;			//manage_mbrdetail用(積分)
	private ListModel<Scoredata> ScoredataModel;		//manage_mbrdetail用(詳細訊息)
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();
	String User_Acc;//使用者帳號
	Session s = Sessions.getCurrent(); 
	public ClassmanageViewModel() {
	}
	public ListModel<Classmanage> getClassModel() {
		return ClassmanageModel;
	}
	public ListModel<Classmanage> getDetailModel() {
		if(s.getAttribute("Cla_detail") != null ){		//如果Cla_detail有資料  就設定ClassdetailModel
			ClassdetailModel = (ListModel<Classmanage>) s.getAttribute("Cla_detail");
		}
		return ClassdetailModel;
	}
	public ListModel<Classmanage> getClsPointModel() {	//班級積分model
		if(s.getAttribute("Cla_point") != null ){		//如果Cla_point有資料  就設定ClassPointModel
			ClassPointModel = (ListModel<Classmanage>) s.getAttribute("Cla_point");
		}
		return ClassPointModel;
	}
	public ListModel<Scoredata> getPointModel() {			//積分是讀這個
		if(s.getAttribute("member_point") != null ){		//如果member_point有資料  就設定PointModel
			PointModel = (ListModel<Scoredata>) s.getAttribute("member_point");
		}
		return PointModel;
	}
	public ListModel<Scoredata> getScoredataModel() {		//manage_mbrdetail.zul是讀這個
		if(s.getAttribute("member_detail") != null ){		//如果member_detail有資料  就設定ScoredataModel
			ScoredataModel = (ListModel<Scoredata>) s.getAttribute("member_detail");
		}
		return ScoredataModel;
	}
	
	
	@Init
	//Init=讀取一次,在載入這個ViewModel時,取得session中的usr_account並塞入User_Acc	
	public void init(@ContextParam(ContextType.SESSION) Session session){
		User_Acc = usridentity.getaccount();
		//User_Acc = (String) session.getAttribute("usr_account");
		ClassmanageModel = new ListModelList<Classmanage>(Service.class_teach(User_Acc));//用使用者帳號當搜尋條件
	}
	
    @Command
    //取得班級詳細資訊
	public void get_classdeatail(@BindingParam("paramx") String NO){
    	ClassmanageService Service = new ClassmanageServiceImpl();
    	ClassdetailModel = new ListModelList<Classmanage>(Service.class_deatail(NO));//用回傳回來的班級代號去查詢
    	ClassPointModel = new ListModelList<Classmanage>(Service.class_point(NO));//用回傳回來的班級代號去查詢班級積分
		Session s = Sessions.getCurrent();//設定session
		s.setAttribute("Cla_detail", ClassdetailModel);//將ClassdetailModel設定成名為Cla_detail的session
		s.setAttribute("Cla_point", ClassPointModel);//將ClassPointModel設定成名為Cla_point的session
		Executions.sendRedirect("manage_clsdetail.zul");//跳轉到班級詳細資訊頁面
	}
    
    @Command
    //取得學生檔案
	public void get_memberdeatail(@BindingParam("paramx") String account){
		ScoredataService Service = new ScoredataServiceImpl();
		PointModel =  new ListModelList<Scoredata>(Service.getpoint(account));//用學生的帳號去查他的積分
		ScoredataModel =  new ListModelList<Scoredata>(Service.getdetail(account));//用學生的帳號去查他的詳細資訊
		Session s = Sessions.getCurrent();//設定session
		s.setAttribute("member_point", PointModel);//將PointModel設定成名為member_point的session
		s.setAttribute("member_detail", ScoredataModel);//將ScoredataModel設定成名為member_detail的session
		Executions.sendRedirect("manage_mbrdetail.zul");//跳轉到學生詳細資訊
	}

}
