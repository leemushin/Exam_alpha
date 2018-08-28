package next.services.insertclass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.naming.InitialContext;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
//�s�W�Z�ťD��
public class insertclass extends SelectorComposer<Window>  {
	private static final long serialVersionUID = 1L;
    @Wire
    private Textbox CD_NO;//�Z�ťN��
    @Wire
    private Textbox CD_NAME;//�Z�ŦW��
    @Wire
    private Textbox CD_GRADE;//�Z�Ŧ~��
    @Wire
    private Textbox CD_TEACHER;//�Z�žɮv
    @Wire
    private Textbox CD_SCHOOL;//�Z�ũ��ݾǮ�
    @Wire
    private Textbox CD_MEMBER;//�Z�Ŧ���
    
    @Listen("onClick=#btn_clsinsert")
    public void Insertcls() 
    { 
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("INSERT INTO next_generation.class_data (CD_NO, CD_NAME, CD_GRADE ,CD_TEACHER,CD_SCHOOL,CD_MEMBER) values(?,?,?,?,?,?)");
            //�N�U���]�w���ȷs�W��class_data
            stmt.setString(1, CD_NO.getValue());//�]�w�Ĥ@��?���Z�ťN��
            stmt.setNString(2, CD_NAME.getValue());//�]�w�ĤG��?���Z�ŦW��
            stmt.setString(3, CD_GRADE.getValue());//�]�w�ĤT��?���Z�Ŧ~��
            stmt.setNString(4, CD_TEACHER.getValue());//�]�w�ĥ|��?���Z�žɮv
            stmt.setNString(5, CD_SCHOOL.getValue());//�]�w�Ĥ���?���Z�ũ��ݾǮ�
            stmt.setNString(6, CD_MEMBER.getValue());//�]�w�Ĥ���?���Z�Ŧ���
            stmt.executeUpdate();
            stmt.close();
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
            Executions.sendRedirect("Class_manage.zul");
        }
    } 
    
}
