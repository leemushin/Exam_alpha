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
	//�̷ӨϥΪ̿�J���r��ӷj�M�Z��
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
        	stmt.setNString(1, class_keyin);//�ο�J���Z�ŷ�j�M����
        	ResultSet resultSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
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
	//��Z�Ÿ�ư���s
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
        	update_data.setNString(1, upd_NAME);//�Ĥ@��?�Oupd_NAME
        	update_data.setNString(2, upd_GRADE);//�ĤG��?�Oupd_GRADE
        	update_data.setNString(3, upd_TEACHER);//�ĤT��?�Oupd_TEACHER
        	update_data.setNString(4, upd_SCHOOL);//�ĥ|��?�Oupd_SCHOOL
        	update_data.setNString(5, upd_MEMBER);//�Ĥ���?�Oupd_MEMBER
        	update_data.setNString(6, upd_NO);//�Ĥ���?�Oupd_MEMBER
        	update_data.executeUpdate();
        	//�W�����N���update
        	//�U���A���������
        	stmt = conn.prepareStatement("SELECT CD_NO,CD_NAME,CD_MEMBER,CD_GRADE,CD_SCHOOL,CD_TEACHER"
        			+ " FROM next_generation.class_data"
        			+ " where CD_NO = ? ;");
        	stmt.setNString(1, upd_NO);//�ίZ�ťN����j�M����
        	ResultSet resultSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
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
    		Messagebox.show("�ק粒��!", "����", Messagebox.OK, Messagebox.INFORMATION);
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
	//�M��̫�10���s�W���Z��
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
        	//���̪�Q�����Z��
        	ResultSet resultSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
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
	//�R���Z�Ÿ��
	@Override
	public void deletecls(String class_keyin) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("delete class_data.* from next_generation.class_data "
        			+ "where CD_NO = ?");
        	stmt.setNString(1, class_keyin);//�ο�J���Z�ťN����R������
        	stmt.executeUpdate();
        	stmt.close();
        	stmt = null;
			Messagebox.show("�R������!", "����", Messagebox.OK,Messagebox.INFORMATION
					, new org.zkoss.zk.ui.event.EventListener() {
					    public void onEvent(Event evt) throws InterruptedException {
					        if (evt.getName().equals("onOK")) {
					        	Executions.sendRedirect("Class_manage.zul");}//���UOK�Ჾ�ʨ쭶��
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
	//�ɯůZ�Ŧ~��
	@Override
	public void upd_cls_grade(int before_grade, int after_grade) {
		 Connection conn = null;
	     PreparedStatement stmt = null;
	     try {
	    	 DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
	    	 conn = ds.getConnection();
	    	 stmt = conn.prepareStatement("UPDATE next_generation.class_data"
	    	 		+ " SET CD_GRADE = ? where CD_GRADE = ?");
	    	 stmt.setLong(1, after_grade);//�Ĥ@�ӱ���O�ɯŦ����~�� (ex:9)
	    	 stmt.setLong(2, before_grade);//�ĤG�ӱ���O�N�n�ɯŪ��~��(ex:8)
	    	 stmt.executeUpdate();
	    	 stmt.close();
	    	 stmt = null;
				Messagebox.show("�Z�Ŧ~�Ťɯŧ���!", "�T��", Messagebox.OK,Messagebox.INFORMATION
						, new org.zkoss.zk.ui.event.EventListener() {
						    public void onEvent(Event evt) throws InterruptedException {
						        if (evt.getName().equals("onOK")) {
						        	Executions.sendRedirect("Class_manage.zul");}//���UOK�Ჾ�ʨ쭶��
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
