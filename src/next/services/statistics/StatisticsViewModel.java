package next.services.statistics;
/*
Author:<Thomas> 
Create date: <2018/1/29>
Description: 統計研究
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
	private ListModel<Statistics> StatisticsModel;//取得研究統計model
	private ListModel<Statistics> StatdetailModel;
	private ListModel<Statistics> Scoretotal_1Model;
	int selectdate = 1;//日期選擇方式;1是單一,2是期間
	String selectgrade = "all";//選年級 預設全年級
	Date single_m = new Date();//單一日期
	Date start_m;//開始日期
	Date end_m;//結束日期
	Session s = Sessions.getCurrent();//設定session
	public StatisticsViewModel() {
	}
	public ListModel<Statistics> getStatisticsModel(){
		return StatisticsModel;
	}
	
	public ListModel<Statistics> getStatdetailModel(){
		if(s.getAttribute("Stat_detail") != null ){//如果Stat_detail有資料  就設定StatdetailModel
			StatdetailModel= (ListModel<Statistics>) s.getAttribute("Stat_detail");
		}
		return StatdetailModel;
	}
	
	public ListModel<Statistics> getScoretotal_1Model(){
		if(s.getAttribute("Scoretotal_1") != null ){//如果Stat_detail有資料  就設定StatdetailModel
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
		
		if(selectgrade != "all" && selectdate == 1) {//如果不是全年級且不是期間(選擇7~9,單一日期)
			StatisticsModel = new ListModelList<Statistics>(Serivce.getdetail_1(selectgrade,single_m));
			Scoretotal_1Model = new ListModelList<Statistics>(Serivce.getScoretotal_1());
			s.setAttribute("Scoretotal_1", Scoretotal_1Model);//將Scoretotal_1Model設定成名為Scoretotal_1的session
			s.setAttribute("Stat_detail", StatisticsModel);//將StatisticsModel設定成名為Stat_detail的session
			Executions.sendRedirect("manage_statdetail.zul");//跳轉到詳細資訊
		}
		else if(selectgrade != "all" && selectdate == 2){//如果不是全年級且不是單一日期(選擇7~9,期間)
			if(end_m.before(start_m)) {//日期檢核
				Messagebox.show("開始日期須小於結束日期", "提醒", Messagebox.OK, Messagebox.EXCLAMATION);
				return;//跳出
			}
			StatisticsModel = new ListModelList<Statistics>(Serivce.getdetail_2(selectgrade,start_m,end_m));
			s.setAttribute("Stat_detail", StatisticsModel);//將StatisticsModel設定成名為Stat_detail的session
			Executions.sendRedirect("manage_statperiod.zul");//跳轉到詳細資訊(期間)
		}
		else if(selectgrade == "all" && selectdate == 1){//如果是全年級且不是期間(選擇全年級,單一日期)
			StatisticsModel = new ListModelList<Statistics>(Serivce.getdetail_3(selectgrade,single_m));
			Scoretotal_1Model = new ListModelList<Statistics>(Serivce.getScoretotal_1());
			s.setAttribute("Scoretotal_1", Scoretotal_1Model);//將Scoretotal_1Model設定成名為Scoretotal_1的session
			s.setAttribute("Stat_detail", StatisticsModel);//將StatisticsModel設定成名為Stat_detail的session
			Executions.sendRedirect("manage_statdetail.zul");//跳轉到詳細資訊
		}
		else {//如果是全年級且是期間(選擇全年級,期間)
			if(end_m.before(start_m)) {//日期檢核
				Messagebox.show("開始日期須小於結束日期", "提醒", Messagebox.OK, Messagebox.EXCLAMATION);
				return;//跳出
			}
			StatisticsModel = new ListModelList<Statistics>(Serivce.getdetail_4(selectgrade,start_m,end_m));
			s.setAttribute("Stat_detail", StatisticsModel);//將StatisticsModel設定成名為Stat_detail的session
			Executions.sendRedirect("manage_statperiod.zul");//跳轉到詳細資訊(期間)
		}
		
	}
	
	
	
}
