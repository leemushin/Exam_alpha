package next.services.bindclass;

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


public class BindclassServiceImpl implements BindclassService {
	//依照使用者輸入的字串來搜尋班級
	@Override
	public List<Bindclass> searchcls(String class_keyin) {
		List<Bindclass> clslist  = new ArrayList<Bindclass>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT CD_NO,CD_NAME,CD_MEMBER,CD_GRADE,CD_SCHOOL,CD_TEACHER"
        			+ " FROM next_generation.class_data"
        			+ " where CD_NO = ? ;");
        	stmt.setNString(1, class_keyin);//用輸入的班級當搜尋條件
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Bindclass findcls;
        	while (resultSet.next()) {
        		findcls = new Bindclass();
        		findcls.setCD_NO(resultSet.getString("CD_NO"));
        		findcls.setCD_NAME(resultSet.getString("CD_NAME"));
        		findcls.setCD_MEMBER(resultSet.getString("CD_MEMBER"));
        		findcls.setCD_GRADE(resultSet.getString("CD_GRADE"));
        		findcls.setCD_SCHOOL(resultSet.getString("CD_SCHOOL"));
        		findcls.setCD_TEACHER(resultSet.getString("CD_TEACHER"));
        		clslist.add(findcls);
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
		return clslist;
	}
	//對班級資料做更新
	@Override
	public List<Bindclass> doupdate(String upd_NO, String upd_NAME, String upd_GRADE, String upd_TEACHER,
			String upd_SCHOOL, String upd_MEMBER) {
		List<Bindclass> clslist  = new ArrayList<Bindclass>();
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement update_data = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	update_data = conn.prepareStatement("UPDATE next_generation.class_data"
        			+ " SET CD_NAME = ?,CD_GRADE = ?,CD_TEACHER = ?,CD_SCHOOL = ?,CD_MEMBER = ?"
        			+ " where CD_NO = ? ;");
        	update_data.setNString(1, upd_NAME);//第一個?是upd_NAME
        	update_data.setNString(2, upd_GRADE);//第二個?是upd_GRADE
        	update_data.setNString(3, upd_TEACHER);//第三個?是upd_TEACHER
        	update_data.setNString(4, upd_SCHOOL);//第四個?是upd_SCHOOL
        	update_data.setNString(5, upd_MEMBER);//第五個?是upd_MEMBER
        	update_data.setNString(6, upd_NO);//第五個?是upd_MEMBER
        	update_data.executeUpdate();
        	//上面先將資料update
        	//下面再次撈取資料
        	stmt = conn.prepareStatement("SELECT CD_NO,CD_NAME,CD_MEMBER,CD_GRADE,CD_SCHOOL,CD_TEACHER"
        			+ " FROM next_generation.class_data"
        			+ " where CD_NO = ? ;");
        	stmt.setNString(1, upd_NO);//用班級代號當搜尋條件
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Bindclass findcls;
        	while (resultSet.next()) {
        		findcls = new Bindclass();
        		findcls.setCD_NO(resultSet.getString("CD_NO"));
        		findcls.setCD_NAME(resultSet.getString("CD_NAME"));
        		findcls.setCD_MEMBER(resultSet.getString("CD_MEMBER"));
        		findcls.setCD_GRADE(resultSet.getString("CD_GRADE"));
        		findcls.setCD_SCHOOL(resultSet.getString("CD_SCHOOL"));
        		findcls.setCD_TEACHER(resultSet.getString("CD_TEACHER"));
        		clslist.add(findcls);
        	}
        	stmt.close();
        	stmt = null;
    		Messagebox.show("修改完畢!", "提示", Messagebox.OK, Messagebox.INFORMATION);
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
		return clslist;
	}
	//尋找最後10筆新增的班級
	@Override
	public List<Bindclass> searchlast() {
		List<Bindclass> lastcls  = new ArrayList<Bindclass>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT CD_NO,CD_NAME,CD_TEACHER FROM next_generation.class_data"
        			+ " ORDER BY CD_ID DESC limit 10 ;");
        	//撈最近十筆的班級
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Bindclass findcls;
        	while (resultSet.next()) {
        		findcls = new Bindclass();
        		findcls.setCD_NO(resultSet.getString("CD_NO"));
        		findcls.setCD_NAME(resultSet.getString("CD_NAME"));
        		findcls.setCD_TEACHER(resultSet.getString("CD_TEACHER"));
        		lastcls.add(findcls);
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
		return lastcls;
	}
	//刪除班級資料
	@Override
	public void deletecls(String class_keyin) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("delete class_data.* from next_generation.class_data "
        			+ "where CD_NO = ?");
        	stmt.setNString(1, class_keyin);//用輸入的班級代號當刪除條件
        	stmt.executeUpdate();
        	stmt.close();
        	stmt = null;
			Messagebox.show("刪除完成!", "提醒", Messagebox.OK,Messagebox.INFORMATION
					, new org.zkoss.zk.ui.event.EventListener() {
					    public void onEvent(Event evt) throws InterruptedException {
					        if (evt.getName().equals("onOK")) {
					        	Executions.sendRedirect("Class_manage.zul");}//按下OK後移動到頁面
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
	//升級班級年級
	@Override
	public void upd_cls_grade(int before_grade, int after_grade) {
		 Connection conn = null;
	     PreparedStatement stmt = null;
	     try {
	    	 DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
	    	 conn = ds.getConnection();
	    	 stmt = conn.prepareStatement("UPDATE next_generation.class_data"
	    	 		+ " SET CD_GRADE = ? where CD_GRADE = ?");
	    	 stmt.setLong(1, after_grade);//第一個條件是升級成的年級 (ex:9)
	    	 stmt.setLong(2, before_grade);//第二個條件是將要升級的年級(ex:8)
	    	 stmt.executeUpdate();
	    	 stmt.close();
	    	 stmt = null;
				Messagebox.show("班級年級升級完成!", "訊息", Messagebox.OK,Messagebox.INFORMATION
						, new org.zkoss.zk.ui.event.EventListener() {
						    public void onEvent(Event evt) throws InterruptedException {
						        if (evt.getName().equals("onOK")) {
						        	Executions.sendRedirect("Class_manage.zul");}//按下OK後移動到頁面
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
