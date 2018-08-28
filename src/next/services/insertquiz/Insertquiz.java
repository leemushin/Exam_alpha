package next.services.insertquiz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Window;
//新增題目主檔
public class Insertquiz extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;
    private String QB_CODE;//題目代碼
    private String QB_VERSION ="";//題目版本
    private String QB_BOOK ="";//題目冊別
    private String QB_CHAPTER="";//題目章別
    private String QB_SECTION="";//題目節別
    private String QB_LEVEL="";//題目難度
    private String QB_NO="";//題目編號
    private String QB_ANSWER;//題目答案
    private String QB_PIC;//題目圖片位置
    private String QB_EXPLAIN;//題目詳解位置
    private String isfill = "false";//填充,預設為false
    private String ispick = "true";//選擇,預設為true

	public void setQB_VERSION(String QB_VERSION) {
		this.QB_VERSION = QB_VERSION;
	}
	public String getQB_VERSION() {
		return QB_VERSION;
	}
	public void setQB_BOOK(String QB_BOOK) {
		this.QB_BOOK = QB_BOOK;
	}
	public String getQB_BOOK() {
		return QB_BOOK;
	}
	public void setQB_CHAPTER(String QB_CHAPTER) {
		this.QB_CHAPTER = QB_CHAPTER;
	}
	public String getQB_CHAPTER() {
		return QB_CHAPTER;
	}
	public void setQB_SECTION(String QB_SECTION) {
		this.QB_SECTION = QB_SECTION;
	}
	public String getQB_SECTION() {
		return QB_SECTION;
	}
	public void setQB_LEVEL(String QB_LEVEL) {
		this.QB_LEVEL = QB_LEVEL;
	}
	public String getQB_LEVEL() {
		return QB_LEVEL;
	}
	public void setQB_NO(String QB_NO) {
		this.QB_NO = QB_NO;
	}
	public String getQB_NO() {
		return QB_NO;
	}
	public void setQB_ANSWER(String QB_ANSWER) {
		this.QB_ANSWER = QB_ANSWER;
	}
	public String getQB_ANSWER() {
		return QB_ANSWER;
	}
	public void setQB_PIC(String QB_PIC) {
		this.QB_PIC = QB_PIC;
	}
	public String getQB_PIC() {
		return QB_PIC;
	}
	public void setQB_EXPLAIN(String QB_EXPLAIN) {
		this.QB_EXPLAIN = QB_EXPLAIN;
	}
	public String getQB_EXPLAIN() {
		return QB_EXPLAIN;
	}
	public void setQB_CODE(String QB_CODE) {
		this.QB_CODE = QB_CODE;
	}
	@DependsOn({ "QB_VERSION", "QB_BOOK","QB_CHAPTER","QB_SECTION","QB_LEVEL","QB_NO"})
	public String getQB_CODE() {
		return getQB_VERSION()+getQB_BOOK()+getQB_CHAPTER()+getQB_SECTION()+getQB_LEVEL()+getQB_NO();
	}
	@Command
	public void set_type(@BindingParam("paramx") int type) {
		if (type == 1) {//如果選填充isfill 為 true ispick 為false 
			isfill = "true";
			ispick = "false";
		}
		else {
			isfill = "false";
			ispick = "true";
		}
	}
    
	@Command
    public void Insert_quiz() 
    { 
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("INSERT INTO next_generation.quiz_base (QB_CODE,QB_VERSION,QB_BOOK,QB_CHAPTER,QB_SECTION,"
            		+ "QB_LEVEL,QB_NO,QB_ANSWER,QB_PIC,QB_EXPLAIN,isfill,ispick) values(?,?,?,?,?,?,?,?,?,?,?,?)");
            //將下面設定的值新增到quiz_base
            stmt.setString(1, QB_CODE);//設定第一個?為題目代碼
            stmt.setString(2, QB_VERSION);//設定第二個?為題目版本
            stmt.setString(3, QB_BOOK);//設定第三個?為題目冊別
            stmt.setString(4, QB_CHAPTER);//設定第四個?為題目章別
            stmt.setString(5, QB_SECTION);//設定第五個?為題目節別
            stmt.setString(6, QB_LEVEL);//設定第六個?為題目難易度
            stmt.setString(7, QB_NO);//設定第七個?為題目編號
            stmt.setString(8, QB_ANSWER);//設定第八個?為題目答案
            stmt.setString(9, QB_PIC);//設定第九個?為題目圖片位置
            stmt.setString(10, QB_EXPLAIN);//設定第十個?為題目詳解位置
            stmt.setString(11, isfill);//設定第十一個?為題目是否為填充
            stmt.setString(12, ispick);//設定第十二個?為題目是否為選擇
            stmt.executeUpdate();//執行sql
            stmt.close();
            stmt = null;
            Executions.sendRedirect("Quiz_manage.zul");//導回題目管理頁面
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
