package next.services.customexam;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

import exam.service.Exam;
import login.services.Login;
import login.services.LoginService;
import login.services.LoginServiceImpl;
import next.services.customexam.Customexam;
import next.services.customexam.CustomexamService;
import next.services.customexam.CustomexamServiceImpl;
//歷屆試題&客製考卷&模擬考主檔
public class CustomexamViewModel {
	private CustomexamService service = new CustomexamServiceImpl();
	private ListModel<Exam> CustomexamModel;
	private ListModel<Exam> SimulationexamModel;
	private ListModel<Exam> PastexamModel;
	private String exam_keyin;//搜尋條件
	private int countpage;									//計算ListModel裡面有幾題
    String[] Ary_right;										//zul傳來的解答陣列
    String[] Ary_choice;									//zul傳來的選答陣列
    String[] Ary_explain;									//zul傳來的詳解陣列
    String[] Ary_quizNo;									//zul傳來的題號陣列
    String[] Ary_quizPic;									//題目圖片陣列(客製考卷不給看解答用)
    String[] Ary_isright;									//是否答對陣列
    String[] Ary_qcode;										//考題代碼陣列
    String qcode;											//考題代碼字串(存入資料庫用)
	String quizname;										//試題名稱
    int ActivePage;											//zul傳來的使用中頁面
    String Ary_quizcode;									//考題代碼字串
    String Ary_explainsert;
    String is_explain = "1";										//是否給學生看解答(預設為1,給學生看)
    int quiz_right = 0;										//答對敵數              
    String Correct_rate;									//正確率
    String point;											//分數
    String User_Acc;										//使用者帳號
    String exam_start;										//開始考試時間
    String exam_exit;										//離開考試時間
    String spendtime;										//花費時間
    long diff;												//花費時間(判斷給分用)
    String REMARK = "○";
    String UID;												//詳解頁面update用
    String Simulation_book;//模擬考的冊別	
    String Simulation_ver;//模擬考的版本
    String sim_ver,sim_book;
	private ListModel<Exam> Ary_toexp;						//explain.zul要用的ListModel
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();
	Session s = Sessions.getCurrent(); 
	public void setexam_keyin(String exam_keyin) {//輸入的考卷名稱
		this.exam_keyin = exam_keyin;
	}
	public String getexam_keyin() {//輸入的考卷名稱
		return exam_keyin;
	}
	public ListModel<Exam>  getCustomexamModel() {
		if(s.getAttribute("custom_data") != null ){
			CustomexamModel = (ListModel<Exam>) s.getAttribute("custom_data");
			countpage = CustomexamModel.getSize();
			quizname = (String) s.getAttribute("custom_quizname");//取得考卷名稱(由Custom_main.zul傳過來)
		}
		return CustomexamModel;
	}
	//會考單一年份
	public ListModel<Exam>  getPastexamModel() {
		//如果歷屆試題seeeion不是null
		if(s.getAttribute("Past_year") != null ){	
			String booktmp = (String) s.getAttribute("Past_year");
			String[] searchtmp = booktmp.split(",");//歷屆考題年份,名稱暫存字串
			sim_book = searchtmp[0];
			quizname = searchtmp[1];
		}
		PastexamModel = new ListModelList<Exam>(service.pastexam(sim_book));
		countpage = PastexamModel.getSize();
		return PastexamModel;
	}
	
	//會考模擬考
	public ListModel<Exam>  getPastexam_RModel() {
		//如果歷屆試題seeeion不是null
		if(s.getAttribute("Past_year") != null){	
			quizname = (String) s.getAttribute("Past_year");
		}
		PastexamModel = new ListModelList<Exam>(service.pastexam_r());
		countpage = PastexamModel.getSize();
		return PastexamModel;
	}
	//單冊模擬考
	public ListModel<Exam>  getSimulationexamModel() {
		//如果模擬考版本與模擬考冊別的session不是null
		if(s.getAttribute("Simulation_book") != null && s.getAttribute("Simulation_ver") != null ){	
			String booktmp = (String) s.getAttribute("Simulation_book");
			String[] searchtmp = booktmp.split(",");//模擬考冊別暫存字串
			sim_book = searchtmp[0];
			quizname = searchtmp[1];
			sim_ver = (String) s.getAttribute("Simulation_ver");
		}
		SimulationexamModel = new ListModelList<Exam>(service.simulationexam(sim_ver, sim_book));
		countpage = SimulationexamModel.getSize();
		return SimulationexamModel;
	}
	//多冊模擬考
	public ListModel<Exam>  getSimulationexam_RModel() {
		//如果模擬考版本與模擬考冊別的session不是null
		if(s.getAttribute("Simulation_book") != null && s.getAttribute("Simulation_ver") != null ){	
			String booktmp = (String) s.getAttribute("Simulation_book");
			String[] searchtmp = booktmp.split(",");//模擬考冊別暫存字串
			sim_book = searchtmp[0];
			quizname = searchtmp[1];
			sim_ver = (String) s.getAttribute("Simulation_ver");
		}
		SimulationexamModel = new ListModelList<Exam>(service.simulationexam_r(sim_ver, sim_book));
		countpage = SimulationexamModel.getSize();
		return SimulationexamModel;
	}
	//搜尋試卷,有符合內容就塞進session並跳到考試頁面
	@Command
	public void search_Customexam(){
		int isexist = service.checkexam(exam_keyin);//先檢查是否有該試卷
		if(isexist == 0) {//如果沒有就中斷
			Messagebox.show("查無該試卷!", "提醒", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		CustomexamModel =  new ListModelList<Exam>(service.searchexam(exam_keyin));//如果有就將題目塞到CustomexamModel
		s.setAttribute("custom_quizname", exam_keyin);//將exam_keyin設定成名為custom_quizname的session
		s.setAttribute("custom_data", CustomexamModel);//將CustomexamModel設定成名為custom_data的session
		Executions.sendRedirect("Custom/Custom_exam.zul");//跳轉到考試頁面
	}
	//-------------------------以下是從examviewmodel抓過來的-----------------------------------
	public ListModel<Exam> getAry_toexp() {					//custom_explain.zul是讀這個
		
		if(s.getAttribute("Ary_from") != null ){		//如果Ary_from有資料(代表考完試) 就設定Ary_toexp
			Ary_toexp = (ListModel<Exam>) s.getAttribute("Ary_from");
		}
		return Ary_toexp;
	}
    //取得詳解離開時間且一併更新到資料庫
    @Command
	public void get_exittime_exp() {
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat outtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_exit = outtime.format(dt);//切好離開時間
		Date in = null;
		Date out = null;
		try {
			in = outtime.parse(exam_start);
			out = outtime.parse(exam_exit);
		} catch (ParseException e) {
		    e.printStackTrace();
		}   
		diff = out.getTime() - in.getTime();//將離開時間與進入時間做運算
		long seconds = diff / 1000 % 60;//取得秒
		long minutes = TimeUnit.MILLISECONDS.toMinutes(diff); //取得分
		long hours = TimeUnit.MILLISECONDS.toHours(diff);//取得小時	
		spendtime = hours+"時"+minutes+"分"+seconds+"秒";
		Bonus_time();//呼叫獎勵時間function
		updatetime();//將時間更新到資料庫
		Executions.sendRedirect("../exam_main.zul");
	}
    //計算獎勵時間
	public void Bonus_time() {
		double bouns = Double.parseDouble(point);
		if(diff>20000) {
			point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
		}
		else if(diff>10000) {
			point = String.valueOf(Math.round(bouns*0.1));//超過10秒得到原分數*0.1的獎勵
		}
		else {
			point = "0";
		}
	}
    //計算獎勵時間---歷屆試題用
	public void Bonus_time_forpast() {
		double bouns = Double.parseDouble(point);
		if(diff>20000) {
			point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
		}
		else if(diff>10000) {
			point = String.valueOf(Math.round(bouns*0.1));//超過10秒得到原分數*0.1的獎勵
		}
		else {
			point = "0";
		}
	}
	//將看解答時間更新到資料庫--------
    public void updatetime() 
    { 
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("update next_generation.score_base set SB_QUIZEXPLAIN = ? ,SB_BONUS =? where SB_ID = ?");
            stmt.setNString(1, spendtime);//看解答時間
            stmt.setString(2, point);//獎勵分
            stmt.setNString(3, UID);//對應的題目編號
            stmt.executeUpdate();
            stmt.close();
            stmt = null;
        } catch (SQLException e) {
            try{
                conn.rollback();
            }catch(SQLException ex){
            }
        } catch (Exception e) {
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
        }
    }
    
    //取得最後一筆(也就是剛考完的)考試記錄SB_ID(主鍵、唯一)與分數------
    @Command
    public void getUid() 
    { 
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT SB_ID,SB_POINT FROM next_generation.score_base where SB_ACCOUNT =? order by SB_ID DESC limit 1;");
            stmt.setNString(1, User_Acc);//中文字用Nstring比較不會有問題
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
            	UID = resultSet.getString("SB_ID");
            	point = resultSet.getString("SB_POINT");
		}	
            stmt.close();
            stmt = null;
        } catch (SQLException e) {
            try{
                conn.rollback();
            }catch(SQLException ex){
            }
        } catch (Exception e) {
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
        }
    }
	/**public static String accuracy(double num, double total, int scale){	//計算正確率
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();	//可以設置精確小數
		df.setMaximumFractionDigits(scale);								//模式 例如四捨五入
		df.setRoundingMode(RoundingMode.HALF_UP);
		double accuracy_num = num / total * 100;						//計算正確率
		return df.format(accuracy_num);
	}**/
	public static String Calculate(double num, double total, int scale){	//計算正確率
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();		//可以設置精確小數
		df.setMaximumFractionDigits(scale);									//模式 例如四捨五入
		df.setRoundingMode(RoundingMode.HALF_UP);
		double accuracy_num = num / total * 100;
		double point = Math.round(accuracy_num*1.1);						//計算分數(依照規則模擬考與歷屆試題分數*1.1)
		return df.format(point);
	}
	
	public static String Calculate_forcustom(double num, double total, int scale){	//計算正確率
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();		//可以設置精確小數
		df.setMaximumFractionDigits(scale);									//模式 例如四捨五入
		df.setRoundingMode(RoundingMode.HALF_UP);
		double accuracy_num = num / total * 100;
		double point = Math.round(accuracy_num);						//計算分數(自訂考卷沒有分數加成)
		return df.format(point);
	}
	//將成績新增到資料庫--------
    public void InsertScore() 
    { 
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("INSERT INTO next_generation.score_base "
            		+ "(SB_ACCOUNT, SB_POINT, SB_QUIZTOTAL ,SB_QUIZCORRECT,SB_QUIZNAME,SB_QUIZIN,SB_QUIZOUT,SB_ANSWERTIME,SB_REMARK,SB_Aryexplain,is_explain)"
            		+ " values(?,?,?,?,?,?,?,?,?,?,?)");
            stmt.setString(1, User_Acc);
            stmt.setString(2, point);
            stmt.setLong(3, countpage);
            stmt.setLong(4, quiz_right);
            stmt.setNString(5, quizname);//中文字用Nstring比較不會有問題
            stmt.setString(6, exam_start);
            stmt.setString(7, exam_exit);   
            stmt.setNString(8, spendtime);//中文字用Nstring比較不會有問題
            stmt.setNString(9, REMARK);//中文字用Nstring比較不會有問題     
            stmt.setString(10, Ary_quizcode);
            stmt.setString(11, is_explain);        
            stmt.executeUpdate();
            stmt.close();
            stmt = null;
        } catch (SQLException e) {
            try{
                conn.rollback();
            }catch(SQLException ex){
            }
        } catch (Exception e) {
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
        }
    }
	//--------------------------------
	@Init
	//Init=讀取一次,在載入這個ViewModel時,取得session中的usr_account並塞入User_Acc	,且取得進入時間exam_start
	public void init(@ContextParam(ContextType.SESSION) Session session){
		User_Acc = usridentity.getaccount();
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat intime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_start = intime.format(dt);
	}
    //取得考試離開時間並且一併計算花了多少時間作答案
	public void get_exittime() {
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat outtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_exit = outtime.format(dt);//切好離開時間
		Date in = null;
		Date out = null;
		try {
			in = outtime.parse(exam_start);
			out = outtime.parse(exam_exit);
		} catch (ParseException e) {
		    e.printStackTrace();
		}   
		diff = out.getTime() - in.getTime();//將離開時間與進入時間做運算
		long seconds = diff / 1000 % 60;//取得秒
		long minutes = TimeUnit.MILLISECONDS.toMinutes(diff); //取得分
		long hours = TimeUnit.MILLISECONDS.toHours(diff);//取得小時	
		spendtime = hours+"時"+minutes+"分"+seconds+"秒";
	}
	//是否符合給分標準,1000為1秒
	public void is_correspond() {
		String booktmp = (String) s.getAttribute("Simulation_book");
		String[] searchtmp = booktmp.split(",");//模擬考冊別暫存字串
		sim_book = searchtmp[0];
		//如果是一冊、二冊、三冊、一~二冊、一~三冊且作答時間小於五分鐘
		if((sim_book.equals("jr1_1")||sim_book.equals("jr1_2")||sim_book.equals("jr2_1")||sim_book.equals("range_1_2")||sim_book.equals("range_1_3"))&&diff<300000) {
			REMARK = "╳";
			point = "0";
		}
		else if(diff<600000) {//如果小於10 分鐘 不給分
			REMARK = "╳";
			point = "0";
		}
	}
	//是否符合給分標準,1000為1秒(歷屆試題用)
	public void is_correspond_forpast() {
		if(diff<600000) {//如果小於10 分鐘 不給分
			REMARK = "╳";
			point = "0";
		}
	}
	//是否符合給分標準,1000為1秒(客製考卷用)
	public void is_correspond_forcustom() {
		if(diff<45000) {
			REMARK = "╳";
			point = "0";
		}
	}
	//---下面是接受zul檔傳來參數的function
	//陣列長度,將陣列長度設為與總題數相等
    @Command
    public void lenth(){
    	Ary_choice = new String[countpage];
    	Ary_right = new String[countpage];
    	Ary_explain = new String[countpage];
    	Ary_isright = new String[countpage];
    	Ary_quizNo = new String[countpage];
    	Ary_qcode = new String[countpage];
    	Ary_quizPic = new String[countpage];;
    	/**Ary_quizcode = new String[countpage];**/
    }	
	@Command
    //取得zul檔傳來的考試名稱
	public void set_quizname(@BindingParam("paramx") String name){
		quizname = name;
    }
	@Command
    //取得詳解位置
	public void set_explain(@BindingParam("paramx") String exp){
		String exp_buff = exp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");
		String[] exp_array = exp_buff.split(",");
		for(int i = 0; i < Ary_explain.length; i++) {
			Ary_explain[i] = exp_array[i];
		    }
	}
    @Command
    //取得正確解答
    public void set_rightans(@BindingParam("paramx") String right){
		for(int i = 0; i < Ary_right.length; i++) {
			Ary_right[ActivePage] = right;
		    }
	}
	@Command
    //取得zul檔傳來的考題代碼
	public void set_quizcode(@BindingParam("paramx") String code){
		Ary_quizcode = code;
    }
	@Command
    //取得zul檔傳來的圖片代碼--客製考卷不給看答案的詳解替代
	public void set_quizPic(@BindingParam("paramx") String pic){
		Ary_quizPic[ActivePage] = pic;
    }
	/**
	//取得題目號碼---2018/05/11新增
	public void getQuiz_No() {
		for(int i = 0; i < Ary_quizNo.length; i++) {
			Ary_quizNo[i] =CustomexamModel.getElementAt(i).getQB_NO();
		    }
	}
	//取得詳解位置---2018/05/11改寫
	public void getQuiz_explain() {
		for(int i = 0; i < Ary_explain.length; i++) {
			Ary_explain[i] =CustomexamModel.getElementAt(i).getQB_EXPLAIN();
		    }
	}
	//取得題目代碼---2018/05/11改寫
	public void getQuiz_code() {
		for(int i = 0; i < Ary_qcode.length; i++) {
			Ary_qcode[i] =CustomexamModel.getElementAt(i).getQB_CODE();
		    }
		qcode = Arrays.toString(Ary_qcode);
	}
	//取得正確答案---2018/05/11改寫
	public void getrightans() {
		for(int i = 0; i < Ary_right.length; i++) {
			Ary_right[i] =CustomexamModel.getElementAt(i).getQB_ANSWER();
		    }
	}**/
    @Command
    //取得使用者選擇
	public void set_choiceans(@BindingParam("paramx") String Ans){
		for(int i = 0; i < Ary_choice.length; i++) {
			Ary_choice[ActivePage] = Ans;
		    }
	}
	@Command
    //取得zul檔傳來的目前頁數 
	public void set_Actpage(@BindingParam("paramx") int page){
    	ActivePage = page;
    }
	//---上面是接受zul檔傳來參數的function
	
	@Command
    //交卷
	public void submit_quiz(){
		 List<Exam> Examlist = new ArrayList<Exam>();
    		//檢查題目裡面有沒有null
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				if(Ary_choice[i] == null){ //如果有沒作答的就不計算
					Messagebox.show("需全部作答完畢才可交卷!", "提醒", Messagebox.OK, Messagebox.EXCLAMATION);
					return;//有的話跳出不檢查答案
				}
			}
			//開始檢查答案
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
			
					if(Ary_choice[i].equals(Ary_right[i])){//字串相等
						Ary_isright[i] = "正確";
						quiz_right++;//答對就正確題目+1
					}
					else {
						Ary_isright[i] = "錯誤";
					}
					
			}	
			//將陣列塞進llist裡面
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				Exam examsource = new Exam();
				examsource.setQB_EXPLAIN(Ary_explain[i]);
				examsource.setQB_ANSWER(Ary_right[i]);
				examsource.setusr_choice(Ary_choice[i]);
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}
			
			Ary_toexp = new ListModelList<Exam>(Examlist);//將陣列塞進modellist裡面
			//Correct_rate = accuracy(quiz_right,countpage,1);//計算正確率
			point = Calculate(quiz_right,countpage,1);//得分
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//呼叫離開時間的function
			is_correspond();//檢查是否符合給分標準
			InsertScore();//呼叫新增資料進資料庫的function
			s.setAttribute("Ary_from", Ary_toexp);//將Ary_toexp設定成名為Ary_from的session
			s.setAttribute("point", point);//將point設定成名為point的session
			s.setAttribute("total", countpage);//將countpage設定成名為total的session
			s.setAttribute("right", quiz_right);//將quiz_right設定成名為right的session
			
			Executions.sendRedirect("Custom_explain.zul");//跳轉到詳解頁面
	}	
	@Command
    //超時,基本上跟交卷一樣,差在會將null補成未作答後繼續
	public void timeover(){
		 List<Exam> Examlist = new ArrayList<Exam>();
    		//檢查題目裡面有沒有null
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				if(Ary_choice[i] == null){ //如果有沒作答的就不計算
					Ary_choice[i] = "未作答";
				}
			}
			//開始檢查答案
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
					if(Ary_choice[i].equals(Ary_right[i])){//字串相等
						Ary_isright[i] = "正確";
						quiz_right++;//答對就正確題目+1
					}
					else {
						Ary_isright[i] = "錯誤";
					}
			}	
			//將陣列塞進llist裡面
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				Exam examsource = new Exam();
				examsource.setQB_EXPLAIN(Ary_explain[i]);
				examsource.setQB_ANSWER(Ary_right[i]);
				examsource.setusr_choice(Ary_choice[i]);
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}
			
			Ary_toexp = new ListModelList<Exam>(Examlist);//將陣列塞進modellist裡面
			//Correct_rate = accuracy(quiz_right,countpage,1);//計算正確率
			point = Calculate(quiz_right,countpage,1);//得分
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//呼叫離開時間的function
			is_correspond();//檢查是否符合給分標準
			InsertScore();//呼叫新增資料進資料庫的function
			s.setAttribute("Ary_from", Ary_toexp);//將Ary_toexp設定成名為Ary_from的session
			s.setAttribute("point", point);//將point設定成名為point的session
			s.setAttribute("total", countpage);//將countpage設定成名為total的session
			s.setAttribute("right", quiz_right);//將quiz_right設定成名為right的session
			Executions.sendRedirect("Custom_explain.zul");//跳轉到詳解頁面
	}
	//----------------------以下為歷屆試題使用,呼叫的function與給予分數會略為不同
	@Command
    //交卷
	public void submit_quiz_forpast(){
		 List<Exam> Examlist = new ArrayList<Exam>();
    		//檢查題目裡面有沒有null
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				if(Ary_choice[i] == null){ //如果有沒作答的就不計算
					Messagebox.show("需全部作答完畢才可交卷!", "提醒", Messagebox.OK, Messagebox.EXCLAMATION);
					return;//有的話跳出不檢查答案
				}
			}
			//開始檢查答案
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
			
					if(Ary_choice[i].equals(Ary_right[i])){//字串相等
						Ary_isright[i] = "正確";
						quiz_right++;//答對就正確題目+1
					}
					else {
						Ary_isright[i] = "錯誤";
					}
					
			}	
			//將陣列塞進llist裡面
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				Exam examsource = new Exam();
				examsource.setQB_EXPLAIN(Ary_explain[i]);
				examsource.setQB_ANSWER(Ary_right[i]);
				examsource.setusr_choice(Ary_choice[i]);
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}
			
			Ary_toexp = new ListModelList<Exam>(Examlist);//將陣列塞進modellist裡面
			//Correct_rate = accuracy(quiz_right,countpage,1);//計算正確率
			point = Calculate(quiz_right,countpage,1);//得分
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//呼叫離開時間的function
			is_correspond_forpast();//檢查是否符合給分標準(歷屆試題標準)
			InsertScore();//呼叫新增資料進資料庫的function
			s.setAttribute("Ary_from", Ary_toexp);//將Ary_toexp設定成名為Ary_from的session
			s.setAttribute("point", point);//將point設定成名為point的session
			s.setAttribute("total", countpage);//將countpage設定成名為total的session
			s.setAttribute("right", quiz_right);//將quiz_right設定成名為right的session
			
			Executions.sendRedirect("Custom_explain.zul");//跳轉到詳解頁面
	}	
	@Command
    //超時,基本上跟交卷一樣,差在會將null補成未作答後繼續
	public void timeover_forpast(){
		 List<Exam> Examlist = new ArrayList<Exam>();
    		//檢查題目裡面有沒有null
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				if(Ary_choice[i] == null){ //如果有沒作答的就不計算
					Ary_choice[i] = "未作答";
				}
			}
			//開始檢查答案
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
					if(Ary_choice[i].equals(Ary_right[i])){//字串相等
						Ary_isright[i] = "正確";
						quiz_right++;//答對就正確題目+1
					}
					else {
						Ary_isright[i] = "錯誤";
					}
			}	
			//將陣列塞進llist裡面
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				Exam examsource = new Exam();
				examsource.setQB_EXPLAIN(Ary_explain[i]);
				examsource.setQB_ANSWER(Ary_right[i]);
				examsource.setusr_choice(Ary_choice[i]);
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}
			
			Ary_toexp = new ListModelList<Exam>(Examlist);//將陣列塞進modellist裡面
			//Correct_rate = accuracy(quiz_right,countpage,1);//計算正確率
			point = Calculate(quiz_right,countpage,1);//得分
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//呼叫離開時間的function
			is_correspond_forpast();//檢查是否符合給分標準(歷屆試題標準)
			InsertScore();//呼叫新增資料進資料庫的function
			s.setAttribute("Ary_from", Ary_toexp);//將Ary_toexp設定成名為Ary_from的session
			s.setAttribute("point", point);//將point設定成名為point的session
			s.setAttribute("total", countpage);//將countpage設定成名為total的session
			s.setAttribute("right", quiz_right);//將quiz_right設定成名為right的session
			Executions.sendRedirect("Custom_explain.zul");//跳轉到詳解頁面
	}
	//----------------------以下為客製考試使用,呼叫的function與給予分數會略為不同
	@Command
    //交卷
	public void submit_quiz_forcustom(){
		 List<Exam> Examlist = new ArrayList<Exam>();
    		//檢查題目裡面有沒有null
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				if(Ary_choice[i] == null){ //如果有沒作答的就不計算
					Messagebox.show("需全部作答完畢才可交卷!", "提醒", Messagebox.OK, Messagebox.EXCLAMATION);
					return;//有的話跳出不檢查答案
				}
			}
			//開始檢查答案
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
					if(Ary_choice[i].equals(Ary_right[i])){//字串相等
						Ary_isright[i] = "正確";
						quiz_right++;//答對就正確題目+1
					}
					else {
						Ary_isright[i] = "錯誤";
					}	
			}	
			//將陣列塞進llist裡面
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				Exam examsource = new Exam();
				examsource.setQB_EXPLAIN(Ary_explain[i]);
				examsource.setQB_ANSWER(Ary_right[i]);
				examsource.setusr_choice(Ary_choice[i]);
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}	
			Ary_toexp = new ListModelList<Exam>(Examlist);//將陣列塞進modellist裡面
			//Correct_rate = accuracy(quiz_right,countpage,1);//計算正確率
			point = Calculate_forcustom(quiz_right,countpage,1);//得分
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//呼叫離開時間的function
			is_correspond_forcustom();//檢查是否符合給分標準(客製考卷標準)
			InsertScore();//呼叫新增資料進資料庫的function
			s.setAttribute("Ary_from", Ary_toexp);//將Ary_toexp設定成名為Ary_from的session
			s.setAttribute("point", point);//將point設定成名為point的session
			s.setAttribute("total", countpage);//將countpage設定成名為total的session
			s.setAttribute("right", quiz_right);//將quiz_right設定成名為right的session
			
			Executions.sendRedirect("Custom_explain.zul");//跳轉到詳解頁面
	}	
	@Command
    //超時,基本上跟交卷一樣,差在會將null補成未作答後繼續
	public void timeover_forcustom(){
		 List<Exam> Examlist = new ArrayList<Exam>();
    		//檢查題目裡面有沒有null
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				if(Ary_choice[i] == null){ //如果有沒作答的就不計算
					Ary_choice[i] = "未作答";
				}
			}
			//開始檢查答案
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
					if(Ary_choice[i].equals(Ary_right[i])){//字串相等
						Ary_isright[i] = "正確";
						quiz_right++;//答對就正確題目+1
					}
					else {
						Ary_isright[i] = "錯誤";
					}
			}	
			//將陣列塞進llist裡面
			for(int i=0;i< Ary_choice.length;i++)//i若小於題目總數 i++
			{
				Exam examsource = new Exam();
				examsource.setQB_PIC(Ary_quizPic[i]);
				examsource.setQB_EXPLAIN(Ary_explain[i]);
				examsource.setQB_ANSWER(Ary_right[i]);
				examsource.setusr_choice(Ary_choice[i]);
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}
			is_explain = CustomexamModel.getElementAt(0).getQB_MAIN();//此張考卷是否能給學生看詳解,與老師設定相同
			Ary_toexp = new ListModelList<Exam>(Examlist);//將陣列塞進modellist裡面
			//Correct_rate = accuracy(quiz_right,countpage,1);//計算正確率
			point = Calculate_forcustom(quiz_right,countpage,1);//得分
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//呼叫離開時間的function
			is_correspond_forcustom();//檢查是否符合給分標準(客製考卷標準)
			InsertScore();//呼叫新增資料進資料庫的function
			s.setAttribute("Ary_from", Ary_toexp);//將Ary_toexp設定成名為Ary_from的session
			s.setAttribute("point", point);//將point設定成名為point的session
			s.setAttribute("total", countpage);//將countpage設定成名為total的session
			s.setAttribute("right", quiz_right);//將quiz_right設定成名為right的session
			if(CustomexamModel.getElementAt(0).getQB_MAIN().equals("1")) {//如果這張考卷能給學生看詳解
				Executions.sendRedirect("Custom_explain.zul");//跳轉到詳解頁面
			}
			else {
				Executions.sendRedirect("Custom_explain_none.zul");//跳轉到不給看詳解的頁面
			}
			
	}
	
}
