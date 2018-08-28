package next.services.setexam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import next.services.bindquiz.Bindquiz;

public class SetexamServiceImpl implements SetexamService{
	
	//public static final Map<Integer, Bindquiz> DUMMY_PERSON_TABLE = new LinkedHashMap<Integer, Bindquiz>();
	//private static volatile int uuid = 0;
	/**public static List<Bindquiz> getAll() {
		//List<Bindquiz> quizlist  = new ArrayList<Bindquiz>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT QB_CODE,QB_PIC FROM next_generation.quiz_base"
        			+ " ORDER BY QB_ID DESC;");
        	//撈一些預設顯現的題目出來
        	ResultSet resultSet = stmt.executeQuery();
        	Bindquiz findquiz;
        	while (resultSet.next()) {
        		findquiz = new Bindquiz();
        		findquiz.setexam_id(++uuid);
        		findquiz.setQB_CODE(resultSet.getString("QB_CODE"));
        		findquiz.setQB_PIC(resultSet.getString("QB_PIC"));
        		DUMMY_PERSON_TABLE.put(findquiz.getexam_id(),findquiz);
        		//quizlist.add(findquiz);
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
        return new ArrayList<Bindquiz>(DUMMY_PERSON_TABLE.values());
		//return quizlist;
	}**/
	
	@Override//自選題目章節
	public  List<Bindquiz> searchquiz(String QB_VERSION, String QB_LEVEL, String QB_BOOK, int QB_CHAPTER,
			int QB_SECTION) {
		List<Bindquiz> quizlist  = new ArrayList<Bindquiz>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT QB_CODE,QB_PIC,QB_NO FROM next_generation.quiz_base"
        			+ " where QB_VERSION = ? and QB_LEVEL = ? and QB_BOOK = ? and QB_CHAPTER = ? and QB_SECTION = ?");
        	stmt.setNString(1, QB_VERSION);//用選擇的版本當搜尋條件
        	stmt.setNString(2, QB_LEVEL);//用選擇的難度當搜尋條件
        	stmt.setNString(3, QB_BOOK);//用選擇的冊別當搜尋條件
        	stmt.setLong(4, QB_CHAPTER);//用選擇的章別當搜尋條件
        	stmt.setLong(5, QB_SECTION);//用選擇的節別當搜尋條件
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Bindquiz findquiz;
        	while (resultSet.next()) {
        		findquiz = new Bindquiz();
        		findquiz.setQB_CODE(resultSet.getString("QB_CODE"));
        		findquiz.setQB_PIC(resultSet.getString("QB_PIC"));
        		findquiz.setQB_NO(resultSet.getString("QB_NO"));
        		quizlist.add(findquiz);
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
		return quizlist;
	}
	//隨機-新
	@Override
	public List<Bindquiz> searchquiz_random(String QB_VERSION, String QB_LEVEL, String QB_BOOK, int QB_CHAPTER,
			int QB_SECTION, int random_num) {
		List<Bindquiz> quizlist  = new ArrayList<Bindquiz>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT QB_CODE,QB_PIC,QB_NO FROM next_generation.quiz_base"
        			+ " where QB_VERSION = ? and QB_LEVEL = ? and QB_BOOK = ? and QB_CHAPTER = ? and QB_SECTION = ?"
        			+ " ORDER BY RAND()  limit ?");
        	stmt.setNString(1, QB_VERSION);//用選擇的版本當搜尋條件
        	stmt.setNString(2, QB_LEVEL);//用選擇的難度當搜尋條件
        	stmt.setNString(3, QB_BOOK);//用選擇的冊別當搜尋條件
        	stmt.setLong(4, QB_CHAPTER);//用選擇的章別當搜尋條件
        	stmt.setLong(5, QB_SECTION);//用選擇的節別當搜尋條件
        	stmt.setLong(6, random_num);//隨機出題的數量
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Bindquiz findquiz;
        	while (resultSet.next()) {
        		findquiz = new Bindquiz();
        		findquiz.setQB_CODE(resultSet.getString("QB_CODE"));
        		findquiz.setQB_PIC(resultSet.getString("QB_PIC"));
        		findquiz.setQB_NO(resultSet.getString("QB_NO"));
        		quizlist.add(findquiz);
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
		return quizlist;
	}



	//隨機(以章為範圍)
	@Override
	public List<Bindquiz> searchbych(String qiz_ch1, String qiz_ch2, String qiz_ch3, int qiz_ch4, String qiz_ch5,
			String qiz_ch6, String qiz_ch7, int qiz_ch8,String QB_VERSION) {
		List<Bindquiz> quizlist  = new ArrayList<Bindquiz>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("(SELECT QB_CODE,QB_PIC FROM next_generation.quiz_base where QB_LEVEL= ? and QB_BOOK = ? "
        			+ "and QB_CHAPTER = ? and QB_VERSION = ? ORDER BY RAND()  limit ?)"
        			+ "UNION ALL"
        			+ "(SELECT QB_CODE,QB_PIC FROM next_generation.quiz_base where QB_LEVEL= ? and QB_BOOK = ? and QB_CHAPTER = ? "
        			+ "and QB_VERSION = ? ORDER BY RAND()  limit ?)"
        			+ "ORDER BY RAND()");
        	stmt.setNString(1, qiz_ch1);
        	stmt.setNString(2, qiz_ch2);
        	stmt.setNString(3, qiz_ch3);
        	stmt.setNString(4, QB_VERSION);
        	stmt.setLong(5, qiz_ch4);
        	stmt.setNString(6, qiz_ch5);
        	stmt.setNString(7, qiz_ch6);
        	stmt.setNString(8, qiz_ch7);
        	stmt.setNString(9, QB_VERSION);
        	stmt.setLong(10, qiz_ch8);

        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Bindquiz findquiz;
        	while (resultSet.next()) {
        		findquiz = new Bindquiz();
        		findquiz.setQB_CODE(resultSet.getString("QB_CODE"));
        		findquiz.setQB_PIC(resultSet.getString("QB_PIC"));
        		quizlist.add(findquiz);
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
		return quizlist;
	}
	
	//隨機(以節為範圍)
	@Override
	public List<Bindquiz> searchbyse(String qiz_se1, String qiz_se2, String qiz_se3, String qiz_se4, int qiz_se5,
			String qiz_se6, String qiz_se7, String qiz_se8, String qiz_se9, int qiz_se10,String QB_VERSION) {
		List<Bindquiz> quizlist  = new ArrayList<Bindquiz>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("(SELECT QB_CODE,QB_PIC FROM next_generation.quiz_base where QB_LEVEL= ? and QB_BOOK = ? "
        			+ "and QB_CHAPTER = ? and QB_SECTION = ? and QB_VERSION = ? ORDER BY RAND()  limit ?)"
        			+ "UNION ALL"
        			+ "(SELECT QB_CODE,QB_PIC FROM next_generation.quiz_base where QB_LEVEL= ? and QB_BOOK = ? and QB_CHAPTER = ? "
        			+ "and QB_SECTION = ? and QB_VERSION = ? ORDER BY RAND()  limit ?)"
        			+ "ORDER BY RAND()");
        	stmt.setNString(1, qiz_se1);//第一個問號是
        	stmt.setNString(2, qiz_se2);//第二個問號是
        	stmt.setNString(3, qiz_se3);//第三個問號是
        	stmt.setNString(4, qiz_se4);//第四個問號是
        	stmt.setNString(5, QB_VERSION);//第五個問號是
        	stmt.setLong(6, qiz_se5);//第六個問號是
        	stmt.setNString(7, qiz_se6);//第七個問號是
        	stmt.setNString(8, qiz_se7);//第八個問號是
        	stmt.setNString(9, qiz_se8);//第九個問號是
        	stmt.setNString(10, qiz_se9);//第十個問號是
        	stmt.setNString(11, QB_VERSION);//第十一個問號是
        	stmt.setLong(12, qiz_se10);//第十二個問號是

        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Bindquiz findquiz;
        	while (resultSet.next()) {
        		findquiz = new Bindquiz();
        		findquiz.setQB_CODE(resultSet.getString("QB_CODE"));
        		findquiz.setQB_PIC(resultSet.getString("QB_PIC"));
        		quizlist.add(findquiz);
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
		return quizlist;
	}
	//搜尋自訂考卷列表
	@Override
	public List<Setexam> customlist() {
		List<Setexam> customlist  = new ArrayList<Setexam>();
		Connection conn = null;
	    PreparedStatement stmt = null;		
	    try {	
	    	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
	    	conn = ds.getConnection();
	    	stmt = conn.prepareStatement("SELECT EB_NO,EB_MAIN,EB_NAME FROM next_generation.exam_base order by EB_ID desc");
	    	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
	    	Setexam examlist;
	    	 while (resultSet.next()) {
	    		 examlist = new Setexam();
	    		 examlist.setEB_NO(resultSet.getString("EB_NO"));
	    		 examlist.setEB_MAIN(resultSet.getString("EB_MAIN"));
	    		 examlist.setEB_NAME(resultSet.getString("EB_NAME"));
	    		 customlist.add(examlist);
	    	 }
	        stmt.close();
	        stmt = null;
	    }
	    catch (SQLException e) {
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
		return customlist;
	}
	//查詢已建立的考卷內容
	@Override
	public List<Bindquiz> searchdetail(String EB_NO) {
		List<Bindquiz> quizlist  = new ArrayList<Bindquiz>();
        Connection conn = null;
        PreparedStatement stmt = null;
        Session s = Sessions.getCurrent(); 
        try {
        	String exam_code = null;//裝題目的字串
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT EB_MAIN,EB_NAME FROM next_generation.exam_base where EB_NO = ? ;");
        	stmt.setNString(1, EB_NO);//用考卷代號當搜尋條件
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
			while (resultSet.next()) {//如果有撈到東西
				exam_code = resultSet.getString("EB_MAIN");//把EB_MAIN丟進exam_code
				s.setAttribute("custom_EB_NAME", resultSet.getString("EB_NAME"));//在這邊加入給下面ZUL撈EB_NAME SESSION
			}
			String exam_code_2 = exam_code.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除exam_code裡的[]跟所有空白
			String[] split_code = exam_code_2.split(",");//用逗號切開
			Bindquiz detailmodel;
			for(int i = 0; i < split_code.length; i++){
	        	stmt = conn.prepareStatement("select QB_CODE,QB_PIC,QB_NO from next_generation.quiz_base where QB_CODE = ?");
	        	stmt.setNString(1, split_code[i]);//用split_code[i]當搜尋條件(此為唯一值)
	        	ResultSet resultSet2 = stmt.executeQuery();//執行查詢並將結果塞進resultSet2
	        	while (resultSet2.next()) {//如果有撈到東西
	        		detailmodel = new Bindquiz();
	        		detailmodel.setQB_NO(resultSet2.getString("QB_NO"));
	        		detailmodel.setQB_CODE(resultSet2.getString("QB_CODE"));
	        		detailmodel.setQB_PIC(resultSet2.getString("QB_PIC"));
	        		detailmodel.setQB_EXPLAIN(EB_NO);//使用此功能沒用到的EXPLAIN,以免去新增欄位的麻煩
	        		quizlist.add(detailmodel);
	        	}
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
		return quizlist;
	}

}
