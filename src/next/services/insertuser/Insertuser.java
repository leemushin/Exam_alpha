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
//新增使用者主檔
public class Insertuser extends SelectorComposer<Window> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Wire
    private Textbox SYS_USERNAME;//使用者姓名
    @Wire
    private Textbox SYS_ACCOUNT;//使用者帳號
    @Wire
    private Textbox PASSWORD;//密碼
    @Wire
    private Textbox PWD_HINT;//密碼提示
    @Wire
    private Datebox REGISTERED;//註冊日
    @Wire
    private Datebox EXPIRYDATE;//到期日
    @Wire
    private Textbox UR_STUDENT;//所屬年級
    @Wire
    private Textbox UR_SCHOOL;//所屬學校
    @Wire
    private Textbox UR_TEACHER;//老師註記
    @Wire
    private Textbox UR_PRINCIPAL;//校長註記
    @Wire
    private Textbox UR_ADMIN;//管理者註記
    @Wire
    private Textbox remark;//使用者備註
    @Wire
    private Textbox SYS_USERNAME_M;//多重使用者姓名_Multiple
    @Wire
    private Textbox SYS_ACCOUNT_M;//多重使用者帳號_Multiple
    @Wire
    private Textbox PASSWORD_M;//多重密碼_Multiple
    
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
            //將下面設定的值新增到sys_data
            stmt.setNString(1, SYS_USERNAME.getValue());//設定第一個?為使用者姓名
            stmt.setNString(2, SYS_ACCOUNT.getValue());//設定第二個?為使用者帳號
            stmt.setNString(3, PASSWORD.getValue());//設定第三個?為密碼
            stmt.setNString(4, PWD_HINT.getValue());//設定第四個?為密碼提示
            stmt.setString(5, REGISTERED.getText());//設定第五個?為註冊日
            stmt.setString(6, EXPIRYDATE.getText());   //設定第六個?為到期日
            stmt.setString(7, REGISTERED.getText());   //設定第七個?為註冊日(最近一次登入預設為註冊日)
            stmt.setNString(8, remark.getValue());//設定第八個?為備註
            stmt.executeUpdate();
            stmt.close();
            stmt = conn.prepareStatement("INSERT INTO next_generation.user_role (UR_NAME, UR_ACCOUNT, UR_SCHOOL ,UR_STUDENT,UR_TEACHER,UR_PRINCIPAL,UR_ADMIN) "
            		+ "values(?,?,?,?,?,?,?)");
            //將下面設定的值新增到user_role
            stmt.setNString(1, SYS_USERNAME.getValue());//設定第一個?為使用者姓名
            stmt.setNString(2, SYS_ACCOUNT.getValue());//設定第二個?為使用者帳號
            stmt.setNString(3, UR_SCHOOL.getValue());//設定第三個?為所屬學校
            stmt.setNString(4, UR_STUDENT.getValue());//設定第四個?為所屬年級
            stmt.setString(5, UR_TEACHER.getValue());//設定第五個?為老師註記
            stmt.setString(6, UR_PRINCIPAL.getValue());//設定第六個?為校長註記
            stmt.setString(7, UR_ADMIN.getValue());//設定第七個?為管理者註記
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
            Executions.sendRedirect("User_manage.zul");//導回使用者管理頁面
        }
    }
    //大量新增試作
    @Listen("onClick=#btn_insertm")
    public void InsertUsr_M() 
    { 
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	String[] usr_array = SYS_USERNAME_M.getValue().split(",");//姓名陣列
        	String[] acc_array = SYS_ACCOUNT_M.getValue().split(",");//帳號陣列
        	String[] pwd_array = PASSWORD_M.getValue().split(",");//密碼陣列
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            for(int i = 0; i < usr_array.length; i++) {
            stmt = conn.prepareStatement("INSERT INTO next_generation.sys_data (SYS_USERNAME, SYS_ACCOUNT, PASSWORD ,PWD_HINT,REGISTERED,EXPIRYDATE,LAST_LOGIN,SYS_REMARK) values(?,?,?,?,?,?,?,?)");
            //將下面設定的值新增到sys_data
            stmt.setNString(1, usr_array[i]);//設定第一個?為使用者姓名
            stmt.setNString(2, acc_array[i]);//設定第二個?為使用者帳號
            stmt.setNString(3, pwd_array[i]);//設定第三個?為密碼
            stmt.setNString(4, pwd_array[i]);//設定第四個?為密碼提示
            stmt.setString(5, REGISTERED.getText());//設定第五個?為註冊日
            stmt.setString(6, EXPIRYDATE.getText());   //設定第六個?為到期日
            stmt.setString(7, REGISTERED.getText());   //設定第七個?為註冊日(最近一次登入預設為註冊日)
            stmt.setNString(8, remark.getValue());//設定第八個?為備註
            stmt.executeUpdate();
            stmt.close();
            stmt = null;
            stmt = conn.prepareStatement("INSERT INTO next_generation.user_role (UR_NAME, UR_ACCOUNT, UR_SCHOOL ,UR_STUDENT,UR_TEACHER,UR_PRINCIPAL,UR_ADMIN) "
            		+ "values(?,?,?,?,?,?,?)");
            //將下面設定的值新增到user_role
            stmt.setNString(1, usr_array[i]);//設定第一個?為使用者姓名
            stmt.setNString(2, acc_array[i]);//設定第二個?為使用者帳號
            stmt.setNString(3, UR_SCHOOL.getValue());//設定第三個?為所屬學校
            stmt.setNString(4, UR_STUDENT.getValue());//設定第四個?為所屬年級
            stmt.setString(5, UR_TEACHER.getValue());//設定第五個?為老師註記
            stmt.setString(6, UR_PRINCIPAL.getValue());//設定第六個?為校長註記
            stmt.setString(7, UR_ADMIN.getValue());//設定第七個?為管理者註記
            stmt.executeUpdate();
            stmt.close();
            stmt = null;
            }
            Executions.sendRedirect("User_manage.zul");//導回使用者管理頁面
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