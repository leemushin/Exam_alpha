package next.services.binduser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Messagebox;

import java.util.ArrayList;
import java.util.Date;

public class BinduserServiceImpl implements BinduserService {
	//查詢使用者
	@Override
	public List<Binduser> searchusr(String account_keyin) {
		List<Binduser> usrlist = new ArrayList<Binduser>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT SYS_ACCOUNT,SYS_USERNAME,PASSWORD,PWD_HINT,LAST_LOGIN,LAST_LOGOUT,UR_STUDENT,"
        			+ "UR_TEACHER,UR_PRINCIPAL,UR_ADMIN,UR_SCHOOL,REGISTERED,EXPIRYDATE,SYS_REMARK"
        			+ " FROM next_generation.sys_data sd"
        			+ " LEFT JOIN next_generation.user_role ur ON sd.SYS_ACCOUNT = ur.UR_ACCOUNT"
        			+ " where SYS_ACCOUNT = ? ;");
        	stmt.setNString(1, account_keyin);//用輸入的帳號當搜尋條件
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Binduser findusr;
        	while (resultSet.next()) {
        		findusr = new Binduser();
        		findusr.setSYS_ACCOUNT(resultSet.getString("SYS_ACCOUNT"));
        		findusr.setUSERNAME(resultSet.getString("SYS_USERNAME"));
        		findusr.setPASSWORD(resultSet.getString("PASSWORD"));
        		findusr.setPWD_HINT(resultSet.getString("PWD_HINT"));
        		findusr.setLast_login(resultSet.getString("LAST_LOGIN"));
        		findusr.setLast_logout(resultSet.getString("LAST_LOGOUT"));
        		findusr.setUR_STUDENT(resultSet.getString("UR_STUDENT"));
        		findusr.setUR_TEACHER(resultSet.getString("UR_TEACHER"));
        		findusr.setUR_PRINCIPAL(resultSet.getString("UR_PRINCIPAL"));
        		findusr.setUR_ADMIN(resultSet.getString("UR_ADMIN"));
        		findusr.setUR_SCHOOL(resultSet.getString("UR_SCHOOL"));
        		findusr.setREGISTERED(resultSet.getTimestamp("REGISTERED"));
        		findusr.setEXPIRYDATE(resultSet.getTimestamp("EXPIRYDATE"));
        		findusr.setREMARK(resultSet.getString("SYS_REMARK"));
        		usrlist.add(findusr);
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
		return usrlist;
	}
	//更新使用者資料
	@Override
	public List<Binduser> doupdate(String upd_USERNAME, String upd_ACCOUNT, String upd_PASSWORD, String upd_PWD_HINT,
			String upd_STUDENT, String upd_SCHOOL, Date upd_REGISTERED, Date upd_EXPIRYDATE, String upd_TEACHER,
			String upd_PRINCIPAL, String upd_ADMIN,String remark) {
		List<Binduser> usrlist = new ArrayList<Binduser>();
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement update_data = null;
        java.text.SimpleDateFormat cuttime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String REGISTERED = cuttime.format(upd_REGISTERED);
        String EXPIRYDATE = cuttime.format(upd_EXPIRYDATE);
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	conn.setAutoCommit(true);
        	update_data = conn.prepareStatement("UPDATE next_generation.sys_data sd"
        			+ " LEFT JOIN next_generation.user_role ur ON sd.SYS_ACCOUNT = ur.UR_ACCOUNT"
        			+ " SET SYS_USERNAME = ?,PASSWORD = ?,PWD_HINT = ?,UR_STUDENT = ?,UR_SCHOOL = ?,REGISTERED = ?,EXPIRYDATE = ?,"
        			+ "UR_TEACHER = ?,UR_PRINCIPAL = ?,UR_ADMIN = ?,SYS_REMARK = ?"
        			+ " where SYS_ACCOUNT = ? ;");
        	//update到table sys_data JOIN user_role 條件是畫面上的資料
        	update_data.setNString(1, upd_USERNAME);//第一個?是SYS_USERNAME
        	update_data.setNString(2, upd_PASSWORD);//第二個?是PASSWORD
        	update_data.setNString(3, upd_PWD_HINT);//第三個?是PWD_HINT
        	update_data.setNString(4, upd_STUDENT);//第四個?是UR_STUDENT
        	update_data.setNString(5, upd_SCHOOL);//第五個?是UR_SCHOOL
        	update_data.setNString(6, REGISTERED);//第六個?是REGISTERED
        	update_data.setNString(7, EXPIRYDATE);//第七個?是EXPIRYDATE
        	update_data.setNString(8, upd_TEACHER);//第八個?是UR_TEACHER
        	update_data.setNString(9, upd_PRINCIPAL);//第九個?是UR_PRINCIPAL
        	update_data.setNString(10, upd_ADMIN);//第十個?是UR_ADMIN
        	update_data.setNString(11, remark);//第十一個?是remark
        	update_data.setNString(12, upd_ACCOUNT);//第十二個?是SYS_ACCOUNT
        	update_data.executeUpdate();
        	//上面先將資料update
        	//下面再次撈取資料
        	stmt = conn.prepareStatement("SELECT SYS_ACCOUNT,SYS_USERNAME,PASSWORD,PWD_HINT,LAST_LOGIN,LAST_LOGOUT,UR_STUDENT,"
        			+ "UR_TEACHER,UR_PRINCIPAL,UR_ADMIN,UR_SCHOOL,REGISTERED,EXPIRYDATE,SYS_REMARK"
        			+ " FROM next_generation.sys_data sd"
        			+ " LEFT JOIN next_generation.user_role ur ON sd.SYS_ACCOUNT = ur.UR_ACCOUNT"
        			+ " where SYS_ACCOUNT = ? ;");
        	stmt.setNString(1, upd_ACCOUNT);//用輸入的帳號當搜尋條件
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	Binduser findusr;
        	while (resultSet.next()) {
        		findusr = new Binduser();
        		findusr.setSYS_ACCOUNT(resultSet.getString("SYS_ACCOUNT"));
        		findusr.setUSERNAME(resultSet.getString("SYS_USERNAME"));
        		findusr.setPASSWORD(resultSet.getString("PASSWORD"));
        		findusr.setPWD_HINT(resultSet.getString("PWD_HINT"));
        		findusr.setLast_login(resultSet.getString("LAST_LOGIN"));
        		findusr.setLast_logout(resultSet.getString("LAST_LOGOUT"));
        		findusr.setUR_STUDENT(resultSet.getString("UR_STUDENT"));
        		findusr.setUR_TEACHER(resultSet.getString("UR_TEACHER"));
        		findusr.setUR_PRINCIPAL(resultSet.getString("UR_PRINCIPAL"));
        		findusr.setUR_ADMIN(resultSet.getString("UR_ADMIN"));
        		findusr.setUR_SCHOOL(resultSet.getString("UR_SCHOOL"));
        		findusr.setREGISTERED(resultSet.getTimestamp("REGISTERED"));
        		findusr.setEXPIRYDATE(resultSet.getTimestamp("EXPIRYDATE"));
        		findusr.setREMARK(resultSet.getString("SYS_REMARK"));
        		usrlist.add(findusr);
        	}
        	stmt.close();
        	stmt = null;
    		Messagebox.show("修改完畢!", "提示", Messagebox.OK, Messagebox.EXCLAMATION);
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
		return usrlist;
	}
	//刪除使用者
	@Override
	public void deleteusr(String account_keyin) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("delete sd.*,sb.* ,ur.* ,ld.* from next_generation.sys_data sd "
        			+ "left join next_generation.user_role ur on sd.SYS_ACCOUNT = ur.UR_ACCOUNT "
        			+ "left join next_generation.score_base sb on sd.SYS_ACCOUNT = sb.SB_ACCOUNT "
        			+ "left join next_generation.login_data ld on sd.SYS_ACCOUNT = ld.LD_ACCOUNT "
        			+ "where sd.SYS_ACCOUNT = ?;");
        	stmt.setNString(1, account_keyin);//用輸入的帳號當刪除條件
        	stmt.executeUpdate();
        	stmt.close();
        	stmt = null;
			Messagebox.show("刪除完成!", "提醒", Messagebox.OK,Messagebox.INFORMATION
					, new org.zkoss.zk.ui.event.EventListener() {
					    public void onEvent(Event evt) throws InterruptedException {
					        if (evt.getName().equals("onOK")) {
					        	Executions.sendRedirect("User_manage.zul");}//按下OK後移動到頁面
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
	//升級使用者年級
	@Override
	public void upd_usr_grade(int before_grade, int after_grade) {
		 Connection conn = null;
	     PreparedStatement stmt = null;
	     try {
	    	 DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
	    	 conn = ds.getConnection();
	    	 stmt = conn.prepareStatement("UPDATE next_generation.user_role"
	    	 		+ " SET UR_STUDENT = ? where UR_STUDENT = ?");
	    	 stmt.setLong(1, after_grade);//第一個條件是升級成的年級 (ex:9)
	    	 stmt.setLong(2, before_grade);//第二個條件是將要升級的年級(ex:8)
	    	 stmt.executeUpdate();
	    	 stmt.close();
	    	 stmt = null;
				Messagebox.show("使用者年級升級完成!", "訊息", Messagebox.OK,Messagebox.INFORMATION
						, new org.zkoss.zk.ui.event.EventListener() {
						    public void onEvent(Event evt) throws InterruptedException {
						        if (evt.getName().equals("onOK")) {
						        	Executions.sendRedirect("User_manage.zul");}//按下OK後移動到頁面
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
