package next.services.classmanage;
/*
Author:<Thomas> 
Create date: <2018/1/19>
Description: �Юv�޲z-�Z�ź޲z
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
	private ListModel<Classmanage> ClassmanageModel;	//manage_class��
	private ListModel<Classmanage> ClassPointModel;		//manage_clsdetail��(�n��)
	private ListModel<Classmanage> ClassdetailModel;	//manage_clsdetail��
	private ListModel<Scoredata> PointModel;			//manage_mbrdetail��(�n��)
	private ListModel<Scoredata> ScoredataModel;		//manage_mbrdetail��(�ԲӰT��)
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();
	String User_Acc;//�ϥΪ̱b��
	Session s = Sessions.getCurrent(); 
	public ClassmanageViewModel() {
	}
	public ListModel<Classmanage> getClassModel() {
		return ClassmanageModel;
	}
	public ListModel<Classmanage> getDetailModel() {
		if(s.getAttribute("Cla_detail") != null ){		//�p�GCla_detail�����  �N�]�wClassdetailModel
			ClassdetailModel = (ListModel<Classmanage>) s.getAttribute("Cla_detail");
		}
		return ClassdetailModel;
	}
	public ListModel<Classmanage> getClsPointModel() {	//�Z�ſn��model
		if(s.getAttribute("Cla_point") != null ){		//�p�GCla_point�����  �N�]�wClassPointModel
			ClassPointModel = (ListModel<Classmanage>) s.getAttribute("Cla_point");
		}
		return ClassPointModel;
	}
	public ListModel<Scoredata> getPointModel() {			//�n���OŪ�o��
		if(s.getAttribute("member_point") != null ){		//�p�Gmember_point�����  �N�]�wPointModel
			PointModel = (ListModel<Scoredata>) s.getAttribute("member_point");
		}
		return PointModel;
	}
	public ListModel<Scoredata> getScoredataModel() {		//manage_mbrdetail.zul�OŪ�o��
		if(s.getAttribute("member_detail") != null ){		//�p�Gmember_detail�����  �N�]�wScoredataModel
			ScoredataModel = (ListModel<Scoredata>) s.getAttribute("member_detail");
		}
		return ScoredataModel;
	}
	
	
	@Init
	//Init=Ū���@��,�b���J�o��ViewModel��,���osession����usr_account�ö�JUser_Acc	
	public void init(@ContextParam(ContextType.SESSION) Session session){
		User_Acc = usridentity.getaccount();
		//User_Acc = (String) session.getAttribute("usr_account");
		ClassmanageModel = new ListModelList<Classmanage>(Service.class_teach(User_Acc));//�ΨϥΪ̱b����j�M����
	}
	
    @Command
    //���o�Z�ŸԲӸ�T
	public void get_classdeatail(@BindingParam("paramx") String NO){
    	ClassmanageService Service = new ClassmanageServiceImpl();
    	ClassdetailModel = new ListModelList<Classmanage>(Service.class_deatail(NO));//�Φ^�Ǧ^�Ӫ��Z�ťN���h�d��
    	ClassPointModel = new ListModelList<Classmanage>(Service.class_point(NO));//�Φ^�Ǧ^�Ӫ��Z�ťN���h�d�߯Z�ſn��
		Session s = Sessions.getCurrent();//�]�wsession
		s.setAttribute("Cla_detail", ClassdetailModel);//�NClassdetailModel�]�w���W��Cla_detail��session
		s.setAttribute("Cla_point", ClassPointModel);//�NClassPointModel�]�w���W��Cla_point��session
		Executions.sendRedirect("manage_clsdetail.zul");//�����Z�ŸԲӸ�T����
	}
    
    @Command
    //���o�ǥ��ɮ�
	public void get_memberdeatail(@BindingParam("paramx") String account){
		ScoredataService Service = new ScoredataServiceImpl();
		PointModel =  new ListModelList<Scoredata>(Service.getpoint(account));//�ξǥͪ��b���h�d�L���n��
		ScoredataModel =  new ListModelList<Scoredata>(Service.getdetail(account));//�ξǥͪ��b���h�d�L���ԲӸ�T
		Session s = Sessions.getCurrent();//�]�wsession
		s.setAttribute("member_point", PointModel);//�NPointModel�]�w���W��member_point��session
		s.setAttribute("member_detail", ScoredataModel);//�NScoredataModel�]�w���W��member_detail��session
		Executions.sendRedirect("manage_mbrdetail.zul");//�����ǥ͸ԲӸ�T
	}

}
