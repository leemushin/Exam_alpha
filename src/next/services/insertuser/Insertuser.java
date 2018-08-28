package next.services.insertuser;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import java.sql.*;
import javax.naming.InitialContext;
import javax.sql.DataSource;
//�s�W�ϥΪ̥D��
public class Insertuser extends SelectorComposer<Window> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Wire
    private Textbox SYS_USERNAME;//�ϥΪ̩m�W
    @Wire
    private Textbox SYS_ACCOUNT;//�ϥΪ̱b��
    @Wire
    private Textbox PASSWORD;//�K�X
    @Wire
    private Textbox PWD_HINT;//�K�X����
    @Wire
    private Datebox REGISTERED;//���U��
    @Wire
    private Datebox EXPIRYDATE;//�����
    @Wire
    private Textbox UR_STUDENT;//���ݦ~��
    @Wire
    private Textbox UR_SCHOOL;//���ݾǮ�
    @Wire
    private Textbox UR_TEACHER;//�Ѯv���O
    @Wire
    private Textbox UR_PRINCIPAL;//�ժ����O
    @Wire
    private Textbox UR_ADMIN;//�޲z�̵��O
    @Wire
    private Textbox remark;//�ϥΪ̳Ƶ�
    @Wire
    private Textbox SYS_USERNAME_M;//�h���ϥΪ̩m�W_Multiple
    @Wire
    private Textbox SYS_ACCOUNT_M;//�h���ϥΪ̱b��_Multiple
    @Wire
    private Textbox PASSWORD_M;//�h���K�X_Multiple
    
    @Listen("onClick=#btn_insert")
    public void InsertUsr() 
    { 
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            //remember that we specify autocommit as false in the context.xml 
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("INSERT INTO next_generation.sys_data (SYS_USERNAME, SYS_ACCOUNT, PASSWORD ,PWD_HINT,REGISTERED,EXPIRYDATE,LAST_LOGIN,SYS_REMARK) values(?,?,?,?,?,?,?,?)");
            //�N�U���]�w���ȷs�W��sys_data
            stmt.setNString(1, SYS_USERNAME.getValue());//�]�w�Ĥ@��?���ϥΪ̩m�W
            stmt.setNString(2, SYS_ACCOUNT.getValue());//�]�w�ĤG��?���ϥΪ̱b��
            stmt.setNString(3, PASSWORD.getValue());//�]�w�ĤT��?���K�X
            stmt.setNString(4, PWD_HINT.getValue());//�]�w�ĥ|��?���K�X����
            stmt.setString(5, REGISTERED.getText());//�]�w�Ĥ���?�����U��
            stmt.setString(6, EXPIRYDATE.getText());   //�]�w�Ĥ���?�������
            stmt.setString(7, REGISTERED.getText());   //�]�w�ĤC��?�����U��(�̪�@���n�J�w�]�����U��)
            stmt.setNString(8, remark.getValue());//�]�w�ĤK��?���Ƶ�
            stmt.executeUpdate();
            stmt.close();
            stmt = conn.prepareStatement("INSERT INTO next_generation.user_role (UR_NAME, UR_ACCOUNT, UR_SCHOOL ,UR_STUDENT,UR_TEACHER,UR_PRINCIPAL,UR_ADMIN) "
            		+ "values(?,?,?,?,?,?,?)");
            //�N�U���]�w���ȷs�W��user_role
            stmt.setNString(1, SYS_USERNAME.getValue());//�]�w�Ĥ@��?���ϥΪ̩m�W
            stmt.setNString(2, SYS_ACCOUNT.getValue());//�]�w�ĤG��?���ϥΪ̱b��
            stmt.setNString(3, UR_SCHOOL.getValue());//�]�w�ĤT��?�����ݾǮ�
            stmt.setNString(4, UR_STUDENT.getValue());//�]�w�ĥ|��?�����ݦ~��
            stmt.setString(5, UR_TEACHER.getValue());//�]�w�Ĥ���?���Ѯv���O
            stmt.setString(6, UR_PRINCIPAL.getValue());//�]�w�Ĥ���?���ժ����O
            stmt.setString(7, UR_ADMIN.getValue());//�]�w�ĤC��?���޲z�̵��O
            stmt.executeUpdate();
            stmt.close();
            stmt = null;
        } catch (SQLException e) {
            try{
                conn.rollback();
            }catch(SQLException ex){
                //log
            }
            //(optional log and) ignore
        } catch (Exception e) {
            //log
        } finally { //cleanup
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    //(optional log and) ignore
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    //(optional log and) ignore
                }
            }
            Executions.sendRedirect("User_manage.zul");//�ɦ^�ϥΪ̺޲z����
        }
    }
    //�j�q�s�W�է@
    @Listen("onClick=#btn_insertm")
    public void InsertUsr_M() 
    { 
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	String[] usr_array = SYS_USERNAME_M.getValue().split(",");//�m�W�}�C
        	String[] acc_array = SYS_ACCOUNT_M.getValue().split(",");//�b���}�C
        	String[] pwd_array = PASSWORD_M.getValue().split(",");//�K�X�}�C
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            for(int i = 0; i < usr_array.length; i++) {
            stmt = conn.prepareStatement("INSERT INTO next_generation.sys_data (SYS_USERNAME, SYS_ACCOUNT, PASSWORD ,PWD_HINT,REGISTERED,EXPIRYDATE,LAST_LOGIN,SYS_REMARK) values(?,?,?,?,?,?,?,?)");
            //�N�U���]�w���ȷs�W��sys_data
            stmt.setNString(1, usr_array[i]);//�]�w�Ĥ@��?���ϥΪ̩m�W
            stmt.setNString(2, acc_array[i]);//�]�w�ĤG��?���ϥΪ̱b��
            stmt.setNString(3, pwd_array[i]);//�]�w�ĤT��?���K�X
            stmt.setNString(4, pwd_array[i]);//�]�w�ĥ|��?���K�X����
            stmt.setString(5, REGISTERED.getText());//�]�w�Ĥ���?�����U��
            stmt.setString(6, EXPIRYDATE.getText());   //�]�w�Ĥ���?�������
            stmt.setString(7, REGISTERED.getText());   //�]�w�ĤC��?�����U��(�̪�@���n�J�w�]�����U��)
            stmt.setNString(8, remark.getValue());//�]�w�ĤK��?���Ƶ�
            stmt.executeUpdate();
            stmt.close();
            stmt = null;
            stmt = conn.prepareStatement("INSERT INTO next_generation.user_role (UR_NAME, UR_ACCOUNT, UR_SCHOOL ,UR_STUDENT,UR_TEACHER,UR_PRINCIPAL,UR_ADMIN) "
            		+ "values(?,?,?,?,?,?,?)");
            //�N�U���]�w���ȷs�W��user_role
            stmt.setNString(1, usr_array[i]);//�]�w�Ĥ@��?���ϥΪ̩m�W
            stmt.setNString(2, acc_array[i]);//�]�w�ĤG��?���ϥΪ̱b��
            stmt.setNString(3, UR_SCHOOL.getValue());//�]�w�ĤT��?�����ݾǮ�
            stmt.setNString(4, UR_STUDENT.getValue());//�]�w�ĥ|��?�����ݦ~��
            stmt.setString(5, UR_TEACHER.getValue());//�]�w�Ĥ���?���Ѯv���O
            stmt.setString(6, UR_PRINCIPAL.getValue());//�]�w�Ĥ���?���ժ����O
            stmt.setString(7, UR_ADMIN.getValue());//�]�w�ĤC��?���޲z�̵��O
            stmt.executeUpdate();
            stmt.close();
            stmt = null;
            }
            Executions.sendRedirect("User_manage.zul");//�ɦ^�ϥΪ̺޲z����
        } catch (SQLException e) {
            try{
                conn.rollback();
            }catch(SQLException ex){
                //log
            }
            //(optional log and) ignore
        } catch (Exception e) {
            //log
        } finally { //cleanup
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    //(optional log and) ignore
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    //(optional log and) ignore
                }
            }
        }
    }  
}