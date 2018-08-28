package exam.service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;//切時間用
import java.util.Date;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.Messagebox;
import java.sql.*;//sql套件
import javax.sql.DataSource;
import javax.naming.InitialContext;

import exam.service.Exam;
import exam.service.ExamService;
import exam.service.ExamServiceImpl;
import login.services.Login;
import login.services.LoginService;
import login.services.LoginServiceImpl;
//例題功能主檔
public class ExamViewModel {

	private ExamService Service = new ExamServiceImpl();	//Impl檔案	
	private List<Exam> examlist; 							//list跟Exam.java設定的一樣
	private ListModel<Exam> examModel;						//exam.zul要用的ListModel
	private ListModel<Exam> Ary_toexp;						//explain.zul要用的ListModel
	private int countpage;									//計算ListModel裡面有幾題
    String[] Ary_right;										//zul傳來的解答陣列
    String[] Ary_choice;									//zul傳來的選答陣列
    String[] Ary_explain;									//zul傳來的詳解陣列
    String[] Ary_quizNo;									//zul傳來的題號陣列
    String[] Ary_isright;									//是否答對陣列
    String[] Ary_qcode;										//考題代碼陣列
    String qcode;											//考題代碼字串(存入資料庫用)
    //String Ary_quizcode;									//考題代碼字串
    int quiz_right = 0;										//答對題數     
    int ActivePage;											//zul傳來的使用中頁面
    String quizname;										//試卷名稱
    //String Correct_rate;									//正確率
    String point;											//分數
    String User_Acc;										//使用者帳號
    String exam_start;										//開始考試時間
    String exam_exit;										//離開考試時間
    String spendtime;										//花費時間
    long diff;												//花費時間(判斷給分用)
    String REMARK = "○";									//是否給分的標記,預設為Ｏ
    String UID;												//詳解頁面update用
    String book,ver,ch,se,lv,ifteacher = "false" ;//這是題目搜尋條件的變數,預設不是老師
    Session s = Sessions.getCurrent(); 
    
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();//取得登入時的資訊
	
	public String getquizname() {//試卷名稱
		return quizname;
	}
	public void setquizname(String quizname) {//試卷名稱
		this.quizname = quizname;
	}	
	
	public ExamViewModel() {								//這邊實現了zul檔要的資料
			if(s.getAttribute("Ary_from") != null ){		//如果Ary_from有資料(代表考完試) 就設定Ary_toexp
				Ary_toexp = (ListModel<Exam>) s.getAttribute("Ary_from");
				countpage = Ary_toexp.getSize();
			}
	}	
	
	public ListModel<Exam> getexamModel_N() {					//exam_n.zul是讀這個
		if(s.getAttribute("search_condition") != null ){
			String alltmp = (String) s.getAttribute("search_condition");//從example.zul傳來的,如jr1_1,ni,1,1,e,數與數線(易)
			String[] searchtmp = alltmp.split(",");//搜尋題目暫存字串
			book = searchtmp[0];//ex:jr1_1
			ver = searchtmp[1];//ex:ni
			ch = searchtmp[2];//ex:1
			se = searchtmp[3];//ex:1
			lv = searchtmp[4];//ex:e
			quizname =  searchtmp[5];//ex:數與數線(易)
		}
		if(se.equals("0")) {//如果節別是0(代表選擇全章) 
			examModel = new ListModelList<Exam>(Service.find_chose_A(book,ver,ch,lv,ifteacher,User_Acc));//用上述條件去搜尋,沒特別設定ifteacher預設false
		}
		else {//依照搜尋條件去資料庫撈資料
			examModel = new ListModelList<Exam>(Service.find_chose(book,ver,ch,se,lv,ifteacher,User_Acc));//用上述條件去搜尋,沒特別設定ifteacher預設false
		}
		countpage = examModel.getSize();
		return examModel;
	}
	public ListModel<Exam> getexamModel_T() {					//exam_t.zul是讀這個(老師版)		
		if(s.getAttribute("search_condition") != null ){
			String alltmp = (String) s.getAttribute("search_condition");//從example.zul傳來的,如jr1_1,ni,1,1,e,數與數線(易)
			String[] searchtmp = alltmp.split(",");//搜尋題目暫存字串
			book = searchtmp[0];
			ver = searchtmp[1];
			ch = searchtmp[2];
			se = searchtmp[3];
			lv = searchtmp[4];
			quizname =  searchtmp[5];
			ifteacher = "true";
		}
		if(se.equals("0")) {//如果節別是0(代表選擇全章) 
			examModel = new ListModelList<Exam>(Service.find_chose_A(book,ver,ch,lv,ifteacher,User_Acc));//用上述條件去搜尋,ifteacher被設定為true
		}
		else {//依照搜尋條件去資料庫撈資料
			examModel = new ListModelList<Exam>(Service.find_chose(book,ver,ch,se,lv,ifteacher,User_Acc));//用上述條件去搜尋,ifteacher被設定為true
		}
		countpage = examModel.getSize();
		return examModel;
	}
	public ListModel<Exam> getAry_toexp() {					//explain.zul是讀這個
		return Ary_toexp;
	}
	public List<Exam> getexamlist(){
		return examlist;
	}
	/**public static String accuracy(double num, double total, int scale){	//計算正確率---暫時沒用
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();	//可以設置精確小數
		df.setMaximumFractionDigits(scale);								//模式 例如四捨五入
		df.setRoundingMode(RoundingMode.HALF_UP);
		double accuracy_num = num / total * 100;						//計算正確率
		return df.format(accuracy_num);
	}**/
	public static String Calculate(double num, double total, int scale){	//計算分數
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();		//可以設置精確小數
		df.setMaximumFractionDigits(scale);									//模式 例如四捨五入
		df.setRoundingMode(RoundingMode.HALF_UP);
		double accuracy_num = num / total * 100;
		double point = Math.round(accuracy_num);
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
            		+ "(SB_ACCOUNT, SB_POINT, SB_QUIZTOTAL ,SB_QUIZCORRECT,SB_QUIZNAME,SB_QUIZIN,SB_QUIZOUT,SB_ANSWERTIME,SB_REMARK,SB_Aryexplain) "
            		+ "values(?,?,?,?,?,?,?,?,?,?)");
            stmt.setString(1, User_Acc);
            stmt.setString(2, point);
            stmt.setLong(3, countpage);
            stmt.setLong(4, quiz_right);
            stmt.setNString(5, quizname);//中文字用Nstring比較不會有問題
            stmt.setString(6, exam_start);
            stmt.setString(7, exam_exit);   
            stmt.setNString(8, spendtime);//中文字用Nstring比較不會有問題
            stmt.setNString(9, REMARK);//中文字用Nstring比較不會有問題     
            stmt.setString(10, qcode); 
            //stmt.setString(10, Ary_quizcode);   
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
            stmt.executeUpdate();//執行sql update語法
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
    //------
    
    //取得詳解離開時間且一併更新到資料庫
    @Command
	public void get_exittime_exp() {
		java.util.Date dt = new java.util.Date();//取得離開詳解的時間
		java.text.SimpleDateFormat outtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_exit = outtime.format(dt);//切好離開時間
		Date in = null;
		Date out = null;
		try {
			in = outtime.parse(exam_start);//Init的時後取得的
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
    //計算獎勵時間-先判斷冊別再判斷時間是否符合
	public void Bonus_time() {
		String alltmp = (String) s.getAttribute("search_condition");
		String[] searchtmp = alltmp.split(",");//搜尋題目暫存字串
		book = searchtmp[0];
		ch = searchtmp[2];
		String decide = book+ch;//冊別+章別
		double bouns = Double.parseDouble(point);
		quiz_right = (int) s.getAttribute("right");//答對題數與right session中的相等
		/**countpage = (int) s.getAttribute("total");**/
		int Wrong = countpage-quiz_right;//全部題數 - 答對數 = 答錯題數
		
		if(decide.equals("jr1_11")) {//如果是第一冊第一章
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";//獎勵分數為30
				}
				else {
					point = "0";//獎勵分數為0
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*3.5*1000)) {//看解答超過時間
					point = "20";//獎勵分數為20
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*3.5*1000)) {//看解答時間>=答錯題數×(最少7秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_12")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*8*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*8*1000)) {//看解答時間>=答錯題數×(最少16秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_13")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*7*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7*1000)) {//看解答時間>=答錯題數×(最少14秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_21")) {//如果是第二冊第一章
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*7.5*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7.5*1000)) {//看解答時間>=答錯題數×(最少15秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_22")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*5.5*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*5.5*1000)) {//看解答時間>=答錯題數×(最少11秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_23")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*11*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*11*1000)) {//看解答時間>=答錯題數×(最少22秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_24")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*5*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*5*1000)) {//看解答時間>=答錯題數×(最少10秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_25")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*6.5*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*6.5*1000)) {//看解答時間>=答錯題數×(最少13秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_11")) {//如果是第三冊第一章
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*7*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7*1000)) {//看解答時間>=答錯題數×(最少14秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_12")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*6.5*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*6.5*1000)) {//看解答時間>=答錯題數×(最少13秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_13")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*7.5*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7.5*1000)) {//看解答時間>=答錯題數×(最少15秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_14")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*6*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*6*1000)) {//看解答時間>=答錯題數×(最少12秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過秒數得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_21")) {//如果是第四冊第一章
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*7*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7*1000)) {//看解答時間>=答錯題數×(最少14秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_22")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*7*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7*1000)) {//看解答時間>=答錯題數×(最少14秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_23")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*11.5*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*11.5*1000)) {//看解答時間>=答錯題數×(最少23秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_24")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*16*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*16*1000)) {//看解答時間>=答錯題數×(最少32秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_11")) {//如果是第五冊第一章
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*15*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*15*1000)) {//看解答時間>=答錯題數×(最少30秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_12")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*12.5*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*12.5*1000)) {//看解答時間>=答錯題數×(最少25秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_13")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*14*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*14*1000)) {//看解答時間>=答錯題數×(最少28秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_21")) {//如果是第六冊第一章
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*10.5*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*10.5*1000)) {//看解答時間>=答錯題數×(最少21秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_22")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*8*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*8*1000)) {//看解答時間>=答錯題數×(最少16秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_23")) {
			if(Wrong==0) {//全部答對
				if(diff>120000) {//看解答超過2 分鐘
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//零分
				if(diff>(10*6*1000)) {//看解答超過時間
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*6*1000)) {//看解答時間>=答錯題數×(最少12秒÷2)
				point = String.valueOf(Math.round(bouns *0.3)); //超過20秒得到原分數*0.3的獎勵 //Math.round四捨五入
			}
			else {
				point = "0";
			}
		}
	}
    //取得考試離開時間並且一併計算花了多少時間作答案
	public void get_exittime() {
		java.util.Date dt = new java.util.Date();//取得離開當下的時間
		java.text.SimpleDateFormat outtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_exit = outtime.format(dt);//切好離開時間
		Date in = null;//進入時間
		Date out = null;//離開時間
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
	//依照冊別判斷是否符合給分標準,1000為1秒
	public void is_correspond() {
		String decide = book+ch;//冊別+章別
		if(decide.equals("jr1_11")) {//如果是第一冊第一章
			if(diff<70000) {//依照計分規則 每題最少7秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr1_12")) {
			if(diff<160000) {//依照計分規則 每題最少16秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr1_13")) {
			if(diff<140000) {//依照計分規則 每題最少14秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr1_21")) {//如果是第二冊第一章
			if(diff<150000) {//依照計分規則 每題最少15秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr1_22")) {
			if(diff<110000) {//依照計分規則 每題最少11秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr1_23")) {
			if(diff<220000) {//依照計分規則 每題最少22秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr1_24")) {
			if(diff<100000) {//依照計分規則 每題最少10秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr1_25")) {
			if(diff<130000) {//依照計分規則 每題最少13秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr2_11")) {//如果是第三冊第一章
			if(diff<140000) {//依照計分規則 每題最少14秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr2_12")) {
			if(diff<130000) {//依照計分規則 每題最少13秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr2_13")) {
			if(diff<150000) {//依照計分規則 每題最少15秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr2_14")) {
			if(diff<120000) {//依照計分規則 每題最少12秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr2_21")) {//如果是第四冊第一章
			if(diff<140000) {//依照計分規則 每題最少14秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr2_22")) {
			if(diff<140000) {//依照計分規則 每題最少14秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr2_23")) {
			if(diff<230000) {//依照計分規則 每題最少23秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr2_24")) {
			if(diff<320000) {//依照計分規則 每題最少32秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr3_11")) {//如果是第五冊第一章
			if(diff<300000) {//依照計分規則 每題最少30秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr3_12")) {
			if(diff<250000) {//依照計分規則 每題最少25秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr3_13")) {
			if(diff<280000) {//依照計分規則 每題最少28秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr3_21")) {//如果是第六冊第一章
			if(diff<210000) {//依照計分規則 每題最少21秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr3_22")) {
			if(diff<160000) {//依照計分規則 每題最少16秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
		else if(decide.equals("jr3_23")) {
			if(diff<120000) {//依照計分規則 每題最少12秒 * 10題
				REMARK = "╳";
				point = "0";
			}
		}
	}
	@Init
	//Init=讀取一次,在載入這個ViewModel時,取得session中的usr_account並塞入User_Acc	,且取得進入時間exam_start
	public void init(@ContextParam(ContextType.SESSION) Session session){
		User_Acc = usridentity.getaccount();
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat intime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_start = intime.format(dt);
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
    }
	/**以下為舊版程式碼,須由zul檔讀取資料後回傳
	(須在zul檔新增看不見的label
	<!-- <label value="@bind(each.QB_EXPLAIN)" onCreate="@command('set_explain', paramx=self.getValue())" visible="false"/><h:br/>-->	
	<!-- <label value="@bind(each.QB_ANSWER)"  onCreate="@command('set_rightans', paramx=self.getValue())"/><h:br/>	 -->
	<!-- <label value="@bind(each.QB_CODE)"  onCreate="@command('set_quizcode', paramx=self.getValue())" visible="false"/><h:br/>--> 	
	@Command
    //取得正確解答
    public void set_rightans(@BindingParam("paramx") String right){
		for(int i = 0; i < Ary_right.length; i++) {
			Ary_right[ActivePage] = right;
		    }
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
    //取得zul檔傳來的考題名稱
	public void set_quizcode(@BindingParam("paramx") String code){
		Ary_quizcode = code;
    }
    @Command
    //取得zul檔傳來的考試名稱
	public void set_quizname(@BindingParam("paramx") String name){
		quizname = name;
    }
    **/
	//取得題目號碼---2018/05/11新增
	public void getQuiz_No() {
		for(int i = 0; i < Ary_quizNo.length; i++) {
			Ary_quizNo[i] =examModel.getElementAt(i).getQB_NO();
		    }
	}
	//取得詳解位置---2018/05/11改寫
	public void getQuiz_explain() {
		for(int i = 0; i < Ary_explain.length; i++) {
			Ary_explain[i] =examModel.getElementAt(i).getQB_EXPLAIN();
		    }
	}
	//取得題目代碼---2018/05/11改寫
	public void getQuiz_code() {
		for(int i = 0; i < Ary_qcode.length; i++) {
			Ary_qcode[i] =examModel.getElementAt(i).getQB_CODE();
		    }
		qcode = Arrays.toString(Ary_qcode);
	}
	//取得正確答案---2018/05/11改寫
	public void getrightans() {
		for(int i = 0; i < Ary_right.length; i++) {
			Ary_right[i] =examModel.getElementAt(i).getQB_ANSWER();
		    }
	}
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
	
	/** 
	@Command
    //交卷-目前沒使用,會檢核是否作答完畢,如果後續要使用記得要更新到與超時function相同
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
			get_exittime();//呼叫離開時間的function
			is_correspond();//檢查是否符合給分標準
			InsertScore();//呼叫新增資料進資料庫的function
			Session s = Sessions.getCurrent();//設定session
			s.setAttribute("Ary_from", Ary_toexp);//將Ary_toexp設定成名為Ary_from的session
			s.setAttribute("point", point);//將point設定成名為point的session
			s.setAttribute("total", countpage);//將countpage設定成名為total的session
			s.setAttribute("right", quiz_right);//將quiz_right設定成名為right的session
			Executions.sendRedirect("explain.zul");//跳轉到詳解頁面
	}**/
	@Command
    //超時,基本上跟交卷一樣,差在會將null補成未作答後繼續
	public void timeover(){
		 List<Exam> Examlist = new ArrayList<Exam>();
			getQuiz_No();//取得題號
			getQuiz_explain();//取得詳解
			getQuiz_code();//取得題目代碼
			getrightans();//取得正確解答
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
				examsource.setQB_NO(Ary_quizNo[i]);
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}
			Ary_toexp = new ListModelList<Exam>(Examlist);//將陣列塞進modellist裡面
			//Correct_rate = accuracy(quiz_right,countpage,1);//計算正確率
			point = Calculate(quiz_right,countpage,1);//得分
			get_exittime();//呼叫離開時間的function
			is_correspond();//檢查是否符合給分標準
			InsertScore();//呼叫新增資料進資料庫的function
			Session s = Sessions.getCurrent();//設定session
			s.setAttribute("Ary_from", Ary_toexp);//將Ary_toexp設定成名為Ary_from的session
			s.setAttribute("point", point);//將point設定成名為point的session
			s.setAttribute("total", countpage);//將countpage設定成名為total的session
			s.setAttribute("right", quiz_right);//將quiz_right設定成名為right的session
			Executions.sendRedirect("explain.zul");//跳轉到詳解頁面
	}	
	//------------------------
}
