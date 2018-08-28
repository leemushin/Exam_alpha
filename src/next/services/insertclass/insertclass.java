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
//新增班級主檔
public class insertclass extends SelectorComposer<Window>  {
	private static final long serialVersionUID = 1L;
    @Wire
    private Textbox CD_NO;//班級代號
    @Wire
    private Textbox CD_NAME;//班級名稱
    @Wire
    private Textbox CD_GRADE;//班級年級
    @Wire
    private Textbox CD_TEACHER;//班級導師
    @Wire
    private Textbox CD_SCHOOL;//班級所屬學校
    @Wire
    private Textbox CD_MEMBER;//班級成員
    
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
            //將下面設定的值新增到class_data
            stmt.setString(1, CD_NO.getValue());//設定第一個?為班級代號
            stmt.setNString(2, CD_NAME.getValue());//設定第二個?為班級名稱
            stmt.setString(3, CD_GRADE.getValue());//設定第三個?為班級年級
            stmt.setNString(4, CD_TEACHER.getValue());//設定第四個?為班級導師
            stmt.setNString(5, CD_SCHOOL.getValue());//設定第五個?為班級所屬學校
            stmt.setNString(6, CD_MEMBER.getValue());//設定第六個?為班級成員
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
