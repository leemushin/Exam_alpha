package exam.service;

import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Arrays;

import next.services.binduser.BinduserConnect;
import java.sql.*;


public class ExamServiceImpl implements ExamService {

	//新版的撈題目,用變數去搜尋
	@Override
	public List<Exam> find_chose(String book, String ver, String ch, String se, String lv,String ifteacher,String User_Acc) {
		 List<Exam> Examlist = new ArrayList<Exam>();
	        Connection conn = null;
	        PreparedStatement stmt = null;
	        PreparedStatement updatetimes = null;
	        java.util.Date loginday = new java.util.Date();//取得今天日期
			java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//設定格式yyyy-MM
			String loginm = getyymm.format(loginday);//將時間轉成yyyy-MM
			int examtimes = 0;
		 try {
	        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");//連結資料庫
	        	conn = ds.getConnection();
	        	//---下面是更新考試次數
	        	updatetimes = conn.prepareStatement("SELECT Monthly_exams FROM next_generation.login_data where LD_ACCOUNT = ? and LD_MONTH like ?");
	        	updatetimes.setNString(1, User_Acc);//第一個?為帳號
	        	updatetimes.setNString(2, loginm+"%");//第二個?為登入月份
	            ResultSet querytimes = updatetimes.executeQuery();//執行查詢並將結果塞進resultSet
	            while(querytimes.next()) {
	            	examtimes = 1 + querytimes.getInt("Monthly_exams");//將考試次數+1
	            }
	            updatetimes.close();
	            updatetimes = null;
	            updatetimes = conn.prepareStatement("update next_generation.login_data set Monthly_exams =? where LD_ACCOUNT = ? and DATE_FORMAT(LD_MONTH,\"%Y-%m\") like ?");
            	//將登入次數更改為+1後的數字
	            updatetimes.setInt(1, examtimes);//第一個?為登入次數
	            updatetimes.setNString(2, User_Acc);//第二個?為帳號
	            updatetimes.setNString(3, loginm+"%");//第三個?為登入月份
	            updatetimes.executeUpdate();
	            updatetimes.close();
	        	//---上面是更新考試次數
	        	if(ifteacher.equals("false")) {//如果不是老師,限制亂數10題
		        	stmt = conn.prepareStatement("select QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_CODE,QB_MAIN,QB_PIC,QB_ANSWER,QB_EXPLAIN,isfill,ispick "
		        			+ "from next_generation.quiz_base "
		        			+ " where QB_BOOK =? and QB_VERSION = ? and QB_CHAPTER = ? and QB_SECTION = ? and QB_LEVEL = ? "
		        			+ "ORDER BY RAND()  limit 10");
	        	}
	        	else {//如果是老師,依序將所有題目撈出
		        	stmt = conn.prepareStatement("select QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_CODE,QB_MAIN,QB_PIC,QB_ANSWER,QB_EXPLAIN,isfill,ispick "
		        			+ "from next_generation.quiz_base "
		        			+ " where QB_BOOK =? and QB_VERSION = ? and QB_CHAPTER = ? and QB_SECTION = ? and QB_LEVEL = ? "
		        			+ "order by QB_ID asc ");
	        	}
	        	stmt.setNString(1, book);//第一個?用冊別當搜尋條件
	        	stmt.setNString(2, ver);//第二個?用版本當搜尋條件
	        	stmt.setNString(3, ch);//第三個?用章別當搜尋條件
	        	stmt.setNString(4, se);//第四個?用節別當搜尋條件
	        	stmt.setNString(5, lv);//第五個?用難度當搜尋條件
	        	ResultSet rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
	        	
	        	/** 這一段是舊版程式需要從zul檔案讀取資料所設計,現已不使用
	        	rs.last();//移動到最後一筆
	        	int re_lenth = rs.getRow(); //-----------長度等於最後一行row
	        	//int re_lenth = 10; //長度由上面limit決定
	        	rs.beforeFirst();//將rs轉回最開頭
				String[] all_question = new String[re_lenth];//存放全部題目
					while (rs.next()) {//如果有撈到東西
						int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
						all_question[i] = rs.getString("QB_CODE");		
					}
					String all_question_2 =  Arrays.toString(all_question);
				rs.beforeFirst();//將rs跳轉到最開頭		
				
				String[] all_EXPLAIN = new String[re_lenth];//存放全部詳解
				while (rs.next()) {//如果有撈到東西
					int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
					all_EXPLAIN[i] = rs.getString("QB_EXPLAIN");//依序將詳解放入all_EXPLAIN字串
				}
				String all_EXPLAIN_2 =  Arrays.toString(all_EXPLAIN);//所有詳解的字串
				rs.beforeFirst();//將rs跳轉到最開頭
				**/
				Exam examsource;				
				while (rs.next()) {
					examsource = new Exam();
					examsource.setQB_BOOK(rs.getString("QB_BOOK"));
			        examsource.setQB_VERSION(rs.getString("QB_VERSION"));
					examsource.setQB_CHAPTER(rs.getString("QB_CHAPTER"));
					examsource.setQB_SECTION(rs.getString("QB_SECTION"));
					examsource.setQB_LEVEL(rs.getString("QB_LEVEL"));
					examsource.setQB_NO(rs.getString("QB_NO"));
					examsource.setQB_CODE(rs.getString("QB_CODE"));
					//examsource.setQB_CODE(all_question_2);
					examsource.setQB_MAIN(rs.getString("QB_MAIN"));
					examsource.setQB_PIC(rs.getString("QB_PIC"));
					examsource.setQB_ANSWER(rs.getString("QB_ANSWER"));
					examsource.setQB_EXPLAIN(rs.getString("QB_EXPLAIN"));
					//examsource.setQB_EXPLAIN(all_EXPLAIN_2);
					examsource.setis_fill(rs.getString("isfill"));
					examsource.setis_pick(rs.getString("ispick"));
					Examlist.add(examsource);
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
		return Examlist;
	}
	//搜尋題目--全章
	@Override
	public List<Exam> find_chose_A(String book, String ver, String ch, String lv,String ifteacher,String User_Acc) {
		 List<Exam> Examlist = new ArrayList<Exam>();
	        Connection conn = null;
	        PreparedStatement stmt = null;
	        PreparedStatement updatetimes = null;
	        java.util.Date loginday = new java.util.Date();//取得今天日期
			java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//設定格式yyyy-MM
			String loginm = getyymm.format(loginday);//將時間轉成yyyy-MM
			int examtimes = 0;
		 try {
	        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
	        	conn = ds.getConnection();
	        	//---下面是更新考試次數
	        	updatetimes = conn.prepareStatement("SELECT Monthly_exams FROM next_generation.login_data where LD_ACCOUNT = ? and LD_MONTH like ?");
	        	updatetimes.setNString(1, User_Acc);//第一個?為帳號
	        	updatetimes.setNString(2, loginm+"%");//第二個?為登入月份
	            ResultSet querytimes = updatetimes.executeQuery();//執行查詢並將結果塞進resultSet
	            while(querytimes.next()) {
	            	examtimes = 1 + querytimes.getInt("Monthly_exams");//將考試次數+1
	            }
	            updatetimes.close();
	            updatetimes = null;
	            updatetimes = conn.prepareStatement("update next_generation.login_data set Monthly_exams =? where LD_ACCOUNT = ? and DATE_FORMAT(LD_MONTH,\"%Y-%m\") like ?");
            	//將登入次數更改為+1後的數字
	            updatetimes.setInt(1, examtimes);//第一個?為登入次數
	            updatetimes.setNString(2, User_Acc);//第二個?為帳號
	            updatetimes.setNString(3, loginm+"%");//第三個?為登入月份
	            updatetimes.executeUpdate();
	            updatetimes.close();
	        	//---上面是更新考試次數
	        	if(ifteacher.equals("false")) {//如果不是老師,限制亂數10題
	        	stmt = conn.prepareStatement("select QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_CODE,QB_MAIN,QB_PIC,QB_ANSWER,QB_EXPLAIN,isfill,ispick "
	        			+ "from next_generation.quiz_base "
	        			+ " where QB_BOOK =? and QB_VERSION = ? and QB_CHAPTER = ? and QB_LEVEL = ? "
	        			+ "ORDER BY RAND()  limit 10");
	        	}
	        	else {//如果是老師,依序將所有題目撈出
		        	stmt = conn.prepareStatement("select QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_CODE,QB_MAIN,QB_PIC,QB_ANSWER,QB_EXPLAIN,isfill,ispick "
		        			+ "from next_generation.quiz_base "
		        			+ " where QB_BOOK =? and QB_VERSION = ? and QB_CHAPTER = ? and QB_LEVEL = ? "
		        			+ "order by QB_ID asc ");
	        	}
	        	stmt.setNString(1, book);//第一個?用冊別當搜尋條件
	        	stmt.setNString(2, ver);//第二個?用版本當搜尋條件
	        	stmt.setNString(3, ch);//第三個?用章別當搜尋條件
	        	stmt.setNString(4, lv);//第四個?用難度別當搜尋條件
	        	ResultSet rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
	        	
	        	/** 這一段是舊版程式需要從zul檔案讀取資料所設計,現已不使用
	        	rs.last();//移動到最後一筆
	        	int re_lenth = rs.getRow(); //-----------長度等於最後一行row
	        	//int re_lenth = 10; //長度由上面limit決定
	        	rs.beforeFirst();//將rs轉回最開頭
				String[] all_question = new String[re_lenth];//存放全部題目
					while (rs.next()) {//如果有撈到東西
						int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
						all_question[i] = rs.getString("QB_CODE");		
					}
					String all_question_2 =  Arrays.toString(all_question);
				rs.beforeFirst();//將rs跳轉到最開頭		
				String[] all_EXPLAIN = new String[re_lenth];//存放全部詳解
				while (rs.next()) {//如果有撈到東西
					int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
					all_EXPLAIN[i] = rs.getString("QB_EXPLAIN");		
				}
				String all_EXPLAIN_2 =  Arrays.toString(all_EXPLAIN);
				rs.beforeFirst();//將rs跳轉到最開頭
				**/
				Exam examsource;				
				while (rs.next()) {
					examsource = new Exam();
					examsource.setQB_BOOK(rs.getString("QB_BOOK"));
			        examsource.setQB_VERSION(rs.getString("QB_VERSION"));
					examsource.setQB_CHAPTER(rs.getString("QB_CHAPTER"));
					examsource.setQB_SECTION(rs.getString("QB_SECTION"));
					examsource.setQB_LEVEL(rs.getString("QB_LEVEL"));
					examsource.setQB_NO(rs.getString("QB_NO"));
					examsource.setQB_CODE(rs.getString("QB_CODE"));
					//examsource.setQB_CODE(all_question_2);
					examsource.setQB_MAIN(rs.getString("QB_MAIN"));
					examsource.setQB_PIC(rs.getString("QB_PIC"));
					examsource.setQB_ANSWER(rs.getString("QB_ANSWER"));
					examsource.setQB_EXPLAIN(rs.getString("QB_EXPLAIN"));
					//examsource.setQB_EXPLAIN(all_EXPLAIN_2);
					examsource.setis_fill(rs.getString("isfill"));
					examsource.setis_pick(rs.getString("ispick"));
					Examlist.add(examsource);
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
		return Examlist;
	}
}
