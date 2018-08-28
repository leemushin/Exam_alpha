package next.services.bindquiz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Messagebox;

public class BindquizServiceImpl implements BindquizService {
	//搜尋題目
	@Override
	public List<Bindquiz> searchquiz(String quiz_keyin) {
		List<Bindquiz> quizlist  = new ArrayList<Bindquiz>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT QB_CODE,QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_PIC,QB_ANSWER,QB_EXPLAIN"
        			+ " FROM next_generation.quiz_base"
        			+ " where QB_CODE = ?;");
        	stmt.setNString(1, quiz_keyin);//用輸入的題目代碼當搜尋條件
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Bindquiz findquiz;
        	while (resultSet.next()) {
        		findquiz = new Bindquiz();
        		findquiz.setQB_CODE(resultSet.getString("QB_CODE"));
        		findquiz.setQB_BOOK(resultSet.getString("QB_BOOK"));
        		findquiz.setQB_VERSION(resultSet.getString("QB_VERSION"));
        		findquiz.setQB_CHAPTER(resultSet.getString("QB_CHAPTER"));
        		findquiz.setQB_SECTION(resultSet.getString("QB_SECTION"));
        		findquiz.setQB_LEVEL(resultSet.getString("QB_LEVEL"));
        		findquiz.setQB_NO(resultSet.getString("QB_NO"));
        		findquiz.setQB_PIC(resultSet.getString("QB_PIC"));
        		findquiz.setQB_ANSWER(resultSet.getString("QB_ANSWER"));
        		findquiz.setQB_EXPLAIN(resultSet.getString("QB_EXPLAIN"));
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
	//更新題目資料
	@Override
	public List<Bindquiz> doupdate( String upd_CODE,String upd_VERSION, String upd_BOOK, String upd_CHAPTER, String upd_SECTION,
			String upd_LEVEL, String upd_NO, String upd_ANSWER, String upd_PIC, String upd_EXPLAIN) {
		List<Bindquiz> quizlist  = new ArrayList<Bindquiz>();
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement update_data = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	update_data = conn.prepareStatement("UPDATE next_generation.quiz_base"
        			+ " SET QB_VERSION = ?,QB_BOOK = ?,QB_CHAPTER = ?,QB_SECTION = ?,QB_LEVEL = ?,QB_NO = ?,QB_ANSWER = ?,QB_PIC = ?,QB_EXPLAIN = ?"
        			+ " where QB_CODE = ? ;");
        	update_data.setNString(1, upd_VERSION);//第一個?是upd_VERSION
        	update_data.setNString(2, upd_BOOK);//第二個?是upd_BOOK
        	update_data.setNString(3, upd_CHAPTER);//第三個?是upd_CHAPTER
        	update_data.setNString(4, upd_SECTION);//第四個?是upd_SECTION
        	update_data.setNString(5, upd_LEVEL);//第五個?是upd_LEVEL
        	update_data.setNString(6, upd_NO);//第六個?是upd_NO
        	update_data.setNString(7, upd_ANSWER);//第七個?是upd_ANSWER
        	update_data.setNString(8, upd_PIC);//第八個?是upd_PIC
        	update_data.setNString(9, upd_EXPLAIN);//第九個?是upd_EXPLAIN
        	update_data.setNString(10, upd_CODE);//第十個?是upd_CODE
        	update_data.executeUpdate();
        	//上面先將資料update
        	//下面再次撈取資料
        	stmt = conn.prepareStatement("SELECT QB_CODE,QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_PIC,QB_ANSWER,QB_EXPLAIN"
        			+ " FROM next_generation.quiz_base"
        			+ " where QB_CODE = ?;");
        	stmt.setNString(1, upd_CODE);//用題目代碼當搜尋條件
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Bindquiz findquiz;
        	while (resultSet.next()) {
        		findquiz = new Bindquiz();
        		findquiz.setQB_CODE(resultSet.getString("QB_CODE"));
        		findquiz.setQB_BOOK(resultSet.getString("QB_BOOK"));
        		findquiz.setQB_VERSION(resultSet.getString("QB_VERSION"));
        		findquiz.setQB_CHAPTER(resultSet.getString("QB_CHAPTER"));
        		findquiz.setQB_SECTION(resultSet.getString("QB_SECTION"));
        		findquiz.setQB_LEVEL(resultSet.getString("QB_LEVEL"));
        		findquiz.setQB_NO(resultSet.getString("QB_NO"));
        		findquiz.setQB_PIC(resultSet.getString("QB_PIC"));
        		findquiz.setQB_ANSWER(resultSet.getString("QB_ANSWER"));
        		findquiz.setQB_EXPLAIN(resultSet.getString("QB_EXPLAIN"));
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
	//搜尋最後20筆題目
	@Override
	public List<Bindquiz> searchlastquiz() {
		List<Bindquiz> quizlist  = new ArrayList<Bindquiz>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT QB_CODE,QB_EXPLAIN FROM next_generation.quiz_base"
        			+ " ORDER BY QB_ID DESC limit 20 ;");
        	//撈最近二十筆的題目
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Bindquiz findquiz;
        	while (resultSet.next()) {
        		findquiz = new Bindquiz();
        		findquiz.setQB_CODE(resultSet.getString("QB_CODE"));
        		findquiz.setQB_EXPLAIN(resultSet.getString("QB_EXPLAIN"));
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
	//刪除題目
	@Override
	public void deletequiz(String quiz_keyin) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("delete quiz_base.* from next_generation.quiz_base "
        			+ "where QB_CODE = ?;");
        	stmt.setNString(1, quiz_keyin);//用輸入的題目代碼當刪除條件
        	stmt.executeUpdate();
        	stmt.close();
        	stmt = null;
			Messagebox.show("刪除完成!", "提醒", Messagebox.OK,Messagebox.INFORMATION
					, new org.zkoss.zk.ui.event.EventListener() {
					    public void onEvent(Event evt) throws InterruptedException {
					        if (evt.getName().equals("onOK")) {
					        	Executions.sendRedirect("Quiz_manage.zul");}//按下OK後移動到頁面
					    }
					});
        }catch (SQLException e) {
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
}
