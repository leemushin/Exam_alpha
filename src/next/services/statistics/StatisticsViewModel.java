package next.services.statistics;
/*
Author:<Thomas> 
Create date: <2018/1/29>
Description: �έp��s
*/
import java.util.Date;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import javax.naming.InitialContext;

import next.services.statistics.Statistics;
import next.services.statistics.StatisticsService;
import next.services.statistics.StatisticsServiceImpl;

public class StatisticsViewModel {
	
	private StatisticsService Serivce = new StatisticsServiceImpl();
	private ListModel<Statistics> StatisticsModel;//���o��s�έpmodel
	private ListModel<Statistics> StatdetailModel;
	private ListModel<Statistics> Scoretotal_1Model;
	int selectdate = 1;//�����ܤ覡;1�O��@,2�O����
	String selectgrade = "all";//��~�� �w�]���~��
	Date single_m = new Date();//��@���
	Date start_m;//�}�l���
	Date end_m;//�������
	Session s = Sessions.getCurrent();//�]�wsession
	public StatisticsViewModel() {
	}
	public ListModel<Statistics> getStatisticsModel(){
		return StatisticsModel;
	}
	
	public ListModel<Statistics> getStatdetailModel(){
		if(s.getAttribute("Stat_detail") != null ){//�p�GStat_detail�����  �N�]�wStatdetailModel
			StatdetailModel= (ListModel<Statistics>) s.getAttribute("Stat_detail");
		}
		return StatdetailModel;
	}
	
	public ListModel<Statistics> getScoretotal_1Model(){
		if(s.getAttribute("Scoretotal_1") != null ){//�p�GStat_detail�����  �N�]�wStatdetailModel
			Scoretotal_1Model= (ListModel<Statistics>) s.getAttribute("Scoretotal_1");
		}
		return Scoretotal_1Model;
	}
	
	
	public Date getsingle_m(){
		return single_m;
	}
	public void setsingle_m(Date single_m) {
		this.single_m = single_m;
	}
	public Date getstart_m(){
		return start_m;
	}
	public void setstart_m(Date start_m) {
		this.start_m = start_m;
	}
	public Date getend_m(){
		return end_m;
	}
	public void setend_m(Date end_m) {
		this.end_m = end_m;
	}
	
	
	@Command
	public void get_selectdate(@BindingParam("paramx") int choice){
		selectdate = choice;
	}
	@Command
	public void get_selectgrade(@BindingParam("paramx") String grade){
		selectgrade = grade;
	}
	@Command
	public void btn_confirm(){
		
		if(selectgrade != "all" && selectdate == 1) {//�p�G���O���~�ťB���O����(���7~9,��@���)
			StatisticsModel = new ListModelList<Statistics>(Serivce.getdetail_1(selectgrade,single_m));
			Scoretotal_1Model = new ListModelList<Statistics>(Serivce.getScoretotal_1());
			s.setAttribute("Scoretotal_1", Scoretotal_1Model);//�NScoretotal_1Model�]�w���W��Scoretotal_1��session
			s.setAttribute("Stat_detail", StatisticsModel);//�NStatisticsModel�]�w���W��Stat_detail��session
			Executions.sendRedirect("manage_statdetail.zul");//�����ԲӸ�T
		}
		else if(selectgrade != "all" && selectdate == 2){//�p�G���O���~�ťB���O��@���(���7~9,����)
			if(end_m.before(start_m)) {//����ˮ�
				Messagebox.show("�}�l������p�󵲧����", "����", Messagebox.OK, Messagebox.EXCLAMATION);
				return;//���X
			}
			StatisticsModel = new ListModelList<Statistics>(Serivce.getdetail_2(selectgrade,start_m,end_m));
			s.setAttribute("Stat_detail", StatisticsModel);//�NStatisticsModel�]�w���W��Stat_detail��session
			Executions.sendRedirect("manage_statperiod.zul");//�����ԲӸ�T(����)
		}
		else if(selectgrade == "all" && selectdate == 1){//�p�G�O���~�ťB���O����(��ܥ��~��,��@���)
			StatisticsModel = new ListModelList<Statistics>(Serivce.getdetail_3(selectgrade,single_m));
			Scoretotal_1Model = new ListModelList<Statistics>(Serivce.getScoretotal_1());
			s.setAttribute("Scoretotal_1", Scoretotal_1Model);//�NScoretotal_1Model�]�w���W��Scoretotal_1��session
			s.setAttribute("Stat_detail", StatisticsModel);//�NStatisticsModel�]�w���W��Stat_detail��session
			Executions.sendRedirect("manage_statdetail.zul");//�����ԲӸ�T
		}
		else {//�p�G�O���~�ťB�O����(��ܥ��~��,����)
			if(end_m.before(start_m)) {//����ˮ�
				Messagebox.show("�}�l������p�󵲧����", "����", Messagebox.OK, Messagebox.EXCLAMATION);
				return;//���X
			}
			StatisticsModel = new ListModelList<Statistics>(Serivce.getdetail_4(selectgrade,start_m,end_m));
			s.setAttribute("Stat_detail", StatisticsModel);//�NStatisticsModel�]�w���W��Stat_detail��session
			Executions.sendRedirect("manage_statperiod.zul");//�����ԲӸ�T(����)
		}
		
	}
	
	
	
}
