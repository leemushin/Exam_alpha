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
	//�d�ߨϥΪ�
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
        	stmt.setNString(1, account_keyin);//�ο�J���b����j�M����
        	ResultSet resultSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
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
	//��s�ϥΪ̸��
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
        	//update��table sys_data JOIN user_role ����O�e���W�����
        	update_data.setNString(1, upd_USERNAME);//�Ĥ@��?�OSYS_USERNAME
        	update_data.setNString(2, upd_PASSWORD);//�ĤG��?�OPASSWORD
        	update_data.setNString(3, upd_PWD_HINT);//�ĤT��?�OPWD_HINT
        	update_data.setNString(4, upd_STUDENT);//�ĥ|��?�OUR_STUDENT
        	update_data.setNString(5, upd_SCHOOL);//�Ĥ���?�OUR_SCHOOL
        	update_data.setNString(6, REGISTERED);//�Ĥ���?�OREGISTERED
        	update_data.setNString(7, EXPIRYDATE);//�ĤC��?�OEXPIRYDATE
        	update_data.setNString(8, upd_TEACHER);//�ĤK��?�OUR_TEACHER
        	update_data.setNString(9, upd_PRINCIPAL);//�ĤE��?�OUR_PRINCIPAL
        	update_data.setNString(10, upd_ADMIN);//�ĤQ��?�OUR_ADMIN
        	update_data.setNString(11, remark);//�ĤQ�@��?�Oremark
        	update_data.setNString(12, upd_ACCOUNT);//�ĤQ�G��?�OSYS_ACCOUNT
        	update_data.executeUpdate();
        	//�W�����N���update
        	//�U���A���������
        	stmt = conn.prepareStatement("SELECT SYS_ACCOUNT,SYS_USERNAME,PASSWORD,PWD_HINT,LAST_LOGIN,LAST_LOGOUT,UR_STUDENT,"
        			+ "UR_TEACHER,UR_PRINCIPAL,UR_ADMIN,UR_SCHOOL,REGISTERED,EXPIRYDATE,SYS_REMARK"
        			+ " FROM next_generation.sys_data sd"
        			+ " LEFT JOIN next_generation.user_role ur ON sd.SYS_ACCOUNT = ur.UR_ACCOUNT"
        			+ " where SYS_ACCOUNT = ? ;");
        	stmt.setNString(1, upd_ACCOUNT);//�ο�J���b����j�M����
        	ResultSet resultSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
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
    		Messagebox.show("�ק粒��!", "����", Messagebox.OK, Messagebox.EXCLAMATION);
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
	//�R���ϥΪ�
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
        	stmt.setNString(1, account_keyin);//�ο�J���b����R������
        	stmt.executeUpdate();
        	stmt.close();
        	stmt = null;
			Messagebox.show("�R������!", "����", Messagebox.OK,Messagebox.INFORMATION
					, new org.zkoss.zk.ui.event.EventListener() {
					    public void onEvent(Event evt) throws InterruptedException {
					        if (evt.getName().equals("onOK")) {
					        	Executions.sendRedirect("User_manage.zul");}//���UOK�Ჾ�ʨ쭶��
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
	//�ɯŨϥΪ̦~��
	@Override
	public void upd_usr_grade(int before_grade, int after_grade) {
		 Connection conn = null;
	     PreparedStatement stmt = null;
	     try {
	    	 DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
	    	 conn = ds.getConnection();
	    	 stmt = conn.prepareStatement("UPDATE next_generation.user_role"
	    	 		+ " SET UR_STUDENT = ? where UR_STUDENT = ?");
	    	 stmt.setLong(1, after_grade);//�Ĥ@�ӱ���O�ɯŦ����~�� (ex:9)
	    	 stmt.setLong(2, before_grade);//�ĤG�ӱ���O�N�n�ɯŪ��~��(ex:8)
	    	 stmt.executeUpdate();
	    	 stmt.close();
	    	 stmt = null;
				Messagebox.show("�ϥΪ̦~�Ťɯŧ���!", "�T��", Messagebox.OK,Messagebox.INFORMATION
						, new org.zkoss.zk.ui.event.EventListener() {
						    public void onEvent(Event evt) throws InterruptedException {
						        if (evt.getName().equals("onOK")) {
						        	Executions.sendRedirect("User_manage.zul");}//���UOK�Ჾ�ʨ쭶��
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
