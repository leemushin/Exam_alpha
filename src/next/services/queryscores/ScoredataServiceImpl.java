package next.services.queryscores;

import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import java.util.ArrayList;
import next.services.binduser.BinduserConnect;
import java.sql.*;

import exam.service.Exam;
import login.services.Login;
import login.services.LoginService;
import login.services.LoginServiceImpl;

public class ScoredataServiceImpl implements ScoredataService{
	
	String User_Acc;//使用者帳號
	//Session s = Sessions.getCurrent();
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();
	private final BinduserConnect ds = BinduserConnect.INSTANCE;
	//---------------------------------------------------
	//使用者詳細資訊查詢(學生適用)
	public List<Scoredata> findAll() {
		List<Scoredata> Scorelist = new ArrayList<Scoredata>();
		try {
			User_Acc = usridentity.getaccount();//取得登入後的使用者帳號session
			//User_Acc = (String) s.getAttribute("usr_account");//取得登入後的使用者帳號session
		    Statement stmt = ds.getStatement();// 與資料庫連結
			ResultSet rs = stmt.executeQuery("select SB_ACCOUNT,SB_QUIZTOTAL,SB_QUIZCORRECT,SB_POINT,SB_QUIZNAME,SB_QUIZIN,SB_QUIZOUT,SB_ANSWERTIME,SB_REMARK,SB_ID,SB_BONUS,SB_QUIZEXPLAIN from next_generation.score_base where SB_ACCOUNT ="+"'"+User_Acc+"'"+"ORDER BY SB_ID DESC");
			// 從database中撈出分數的資訊,條件是User_Acc ORDER BY SB_ID DESC(從最新列到最舊,可再加limit限制查詢幾筆)
			Scoredata scoresource;
			while (rs.next()) {
				scoresource = new Scoredata();
				scoresource.setSB_ACCOUNT(rs.getString("SB_ACCOUNT"));
				scoresource.setSB_QUIZTOTAL(rs.getString("SB_QUIZTOTAL"));
				scoresource.setSB_QUIZCORRECT(rs.getString("SB_QUIZCORRECT"));
				scoresource.setSB_POINT(rs.getString("SB_POINT"));	
				scoresource.setSB_QUIZNAME(rs.getString("SB_QUIZNAME"));
				scoresource.setSB_QUIZIN(rs.getTimestamp("SB_QUIZIN"));
				scoresource.setSB_QUIZOUT(rs.getTimestamp("SB_QUIZOUT"));
				scoresource.setSB_ANSWERTIME(rs.getString("SB_ANSWERTIME"));
				scoresource.setREMARK(rs.getString("SB_REMARK"));
				scoresource.setSB_ID(rs.getString("SB_ID"));
				scoresource.setSB_QUIZEXPLAIN(rs.getString("SB_QUIZEXPLAIN"));
				scoresource.setSB_BONUS(rs.getString("SB_BONUS"));
				Scorelist.add(scoresource);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ds.close();
		}	
		return Scorelist;
	}	
	//---------------------------------------------------
	//查詢詳解回顧
	@Override
	public List<Exam> findRecord(String id) {
		List<Exam> recordlist = new ArrayList<Exam>();
		try {
			String is_explain = null;//是否可看詳解
			String explain_code = null;//裝詳解的字串
		    Statement stmt = ds.getStatement();// 與資料庫連結
			ResultSet rs = stmt.executeQuery("select SB_Aryexplain,is_explain from next_generation.score_base where SB_ID ="+id);//去撈該筆考試記錄ID所存的SB_Aryexplain
			while (rs.next()) {//如果有撈到東西
				explain_code = rs.getString("SB_Aryexplain");//把SB_Aryexplain丟進explain_code
				is_explain = rs.getString("is_explain");//把is_explain丟進is_explain
			}
			Exam examsource;
			if(is_explain.equals("1")) {//如果可以看解答,就撈出原考試答案
				String explain_code_2 = explain_code.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除explain_code裡的[]跟所有空白
				String[] split_code = explain_code_2.split(",");//用逗號切開
				for(int i = 0; i < split_code.length; i++){
					ResultSet rs2 = stmt.executeQuery("select QB_EXPLAIN,QB_ANSWER,QB_NO from next_generation.quiz_base where QB_CODE ="+"'"+split_code[i]+"'");
					//選取QB_EXPLAIN,QB_ANSWER.條件是split_code[i](此為唯一值)
						while (rs2.next()) {//如果有撈到東西
							examsource = new Exam();
							examsource.setQB_EXPLAIN(rs2.getString("QB_EXPLAIN"));
							examsource.setQB_ANSWER(rs2.getString("QB_ANSWER"));
							examsource.setQB_NO(rs2.getString("QB_NO"));
							recordlist.add(examsource);
					}	
				}	
			}
			else {//如果不能看解答,顯示預設圖片
				examsource = new Exam();
				examsource.setQB_EXPLAIN("/Qbase/common/donotexplain.jpg");//將詳解替換成預設圖片
				examsource.setQB_NO("None");//將題目代碼替換成none
				recordlist.add(examsource);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ds.close();
		}	
		return recordlist;
	}
	//---------------------------------------------------
	//查詢積分(教師管理/學生皆適用)
	@Override
	public List<Scoredata> getpoint(String user_Acc){ 
		String loginmonth;//登入月份
		String StartMonth;//學期開始月份
		String EndMonth;//學期結束月份
		String semester_point;//學期積分
		String PM_tmp;//月積分
		String usr_name;//使用者姓名
		int theMonth;
		int theYear;
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//設定格式yyyy-MM
		java.text.SimpleDateFormat getmonth = new java.text.SimpleDateFormat("MM");//設定格式MM
		java.text.SimpleDateFormat getyear = new java.text.SimpleDateFormat("yyyy");//設定格式yyyy
		java.util.Date dt = new java.util.Date();//取得時間
		loginmonth = getyymm.format(dt);//將時間切成yyyy-MM 
		theMonth = Integer.parseInt(getmonth.format(dt));//取得該月份以用來判斷
		theYear = Integer.parseInt(getyear.format(dt));//取得該年份以用來判斷
		
		if(theMonth>=2 && theMonth<8) {							//如果在2~7月登入
			StartMonth =  (Integer.toString(theYear))+"-02";	//開始月份為20XX-02
			EndMonth = 	 (Integer.toString(theYear))+"-07";		//結束月份為20XX-07
		}
		else if(theMonth>=8 && theMonth<=12) {					//如果在8~12月登入
			StartMonth =  (Integer.toString(theYear))+"-08";	//開始月份為20XX-08
			EndMonth = 	 (Integer.toString(theYear+1))+"-01";	//結束月份為(20XX+1)-01 跨年度
		}
		else {													//如果在1月份登入
			StartMonth =  (Integer.toString(theYear-1))+"-08";	//開始月份為(20XX-1)-08 跨年度
			EndMonth = 	 (Integer.toString(theYear))+"-01";		//結束月份為20XX-01
		}		
		List<Scoredata> pointlist = new ArrayList<Scoredata>();
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt_semester = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            /**
            //下面是學期積分
            stmt_semester = conn.prepareStatement("SELECT sum(SB_BONUS)+sum(SB_POINT) as s_total  FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?;");
            //選取SB_BONUS的加總與SB_POINT的加總相加為s_total,條件是SB_QUIZIN(進入試題時間)在StartMonth與EndMonth之間且使用者帳號為user_Acc
            stmt_semester.setNString(1, StartMonth);//設定第一個?條件為StartMonth
            stmt_semester.setNString(2, EndMonth);//設定第二個?條件為EndMonth
            stmt_semester.setNString(3, user_Acc);//設定第三個?條件為user_Acc
            ResultSet semester  = stmt_semester.executeQuery();//執行查詢並將結果塞進semester
            while (semester.next()) {//如果有東西
            	semester_point = semester.getString("s_total");//semester_point學期積分=s_total
            }**/
            //下面是月積分
            stmt = conn.prepareStatement("SELECT (select sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as pointmonth,"
            		+ "(select sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as pointsemester,"
            		+ "(select SYS_USERNAME FROM next_generation.sys_data where SYS_ACCOUNT = ?) as usrname");
            //選取SB_BONUS的加總與SB_POINT的加總相加為total,條件是使用者帳號為user_Acc且SB_QUIZIN(進入試題時間)like loginmonth%
            stmt.setNString(1, user_Acc);//設定第一個?條件為user_Acc
            stmt.setNString(2, loginmonth+"%");//設定第二個?條件為loginmonth%
            stmt.setNString(3, StartMonth);//設定第三個?條件為StartMonth
            stmt.setNString(4, EndMonth);//設定第四個?條件為EndMonth
            stmt.setNString(5, user_Acc);//設定第五個?條件為user_Acc
            stmt.setNString(6, user_Acc);//設定第六個?條件為user_Acc
            ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
            Scoredata pointsource;	
            while (resultSet.next()) {
            	pointsource = new Scoredata();
            	PM_tmp = resultSet.getString("pointmonth");
            	semester_point = resultSet.getString("pointsemester");
            	usr_name = resultSet.getString("usrname");
        		if(PM_tmp == null ||PM_tmp == "" ) {//如果是null或""指定為0
        			PM_tmp = "0";
        		}
        		if(semester_point == null ||semester_point == "" ) {//如果是null或""指定為0
        			semester_point = "0";
        		}
        		pointsource.setSB_ACCOUNT(usr_name);//使用者姓名
            	pointsource.setPoint_month(PM_tmp);//月積分
            	pointsource.setPoint_semester(semester_point);//學期積分
            	pointlist.add(pointsource);
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
		return pointlist;
    }
	//---------------------------------------------------
	//教師管理查詳細訊息
	@Override
	public List<Scoredata> getdetail(String Memb_Acc) {
		List<Scoredata> Scorelist = new ArrayList<Scoredata>();
		try {
		    Statement stmt = ds.getStatement();// 與資料庫連結
			ResultSet rs = stmt.executeQuery("select SB_ACCOUNT,SB_QUIZTOTAL,SB_QUIZCORRECT,SB_POINT,SB_QUIZNAME,SB_QUIZIN,SB_QUIZOUT,SB_ANSWERTIME,SB_REMARK,SB_ID,SB_BONUS,SB_QUIZEXPLAIN from next_generation.score_base where SB_ACCOUNT ="+"'"+Memb_Acc+"'"+"ORDER BY SB_ID DESC");
			// 從database中撈出分數的資訊,條件是User_Acc ORDER BY SB_ID DESC(從最新列到最舊,可再加limit限制查詢幾筆)
			Scoredata scoresource;
			while (rs.next()) {
				scoresource = new Scoredata();
				scoresource.setSB_ACCOUNT(rs.getString("SB_ACCOUNT"));
				scoresource.setSB_QUIZTOTAL(rs.getString("SB_QUIZTOTAL"));
				scoresource.setSB_QUIZCORRECT(rs.getString("SB_QUIZCORRECT"));
				scoresource.setSB_POINT(rs.getString("SB_POINT"));	
				scoresource.setSB_QUIZNAME(rs.getString("SB_QUIZNAME"));
				scoresource.setSB_QUIZIN(rs.getTimestamp("SB_QUIZIN"));
				scoresource.setSB_QUIZOUT(rs.getTimestamp("SB_QUIZOUT"));
				scoresource.setSB_ANSWERTIME(rs.getString("SB_ANSWERTIME"));
				scoresource.setREMARK(rs.getString("SB_REMARK"));
				scoresource.setSB_ID(rs.getString("SB_ID"));
				scoresource.setSB_QUIZEXPLAIN(rs.getString("SB_QUIZEXPLAIN"));
				scoresource.setSB_BONUS(rs.getString("SB_BONUS"));
				Scorelist.add(scoresource);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ds.close();
		}	
		return Scorelist;
	}	

}


