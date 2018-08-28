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
//�s�W�D�إD��
public class Insertquiz extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;
    private String QB_CODE;//�D�إN�X
    private String QB_VERSION ="";//�D�ت���
    private String QB_BOOK ="";//�D�إU�O
    private String QB_CHAPTER="";//�D�س��O
    private String QB_SECTION="";//�D�ظ`�O
    private String QB_LEVEL="";//�D������
    private String QB_NO="";//�D�ؽs��
    private String QB_ANSWER;//�D�ص���
    private String QB_PIC;//�D�عϤ���m
    private String QB_EXPLAIN;//�D�ظԸѦ�m
    private String isfill = "false";//��R,�w�]��false
    private String ispick = "true";//���,�w�]��true

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
		if (type == 1) {//�p�G���Risfill �� true ispick ��false 
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
            //�N�U���]�w���ȷs�W��quiz_base
            stmt.setString(1, QB_CODE);//�]�w�Ĥ@��?���D�إN�X
            stmt.setString(2, QB_VERSION);//�]�w�ĤG��?���D�ت���
            stmt.setString(3, QB_BOOK);//�]�w�ĤT��?���D�إU�O
            stmt.setString(4, QB_CHAPTER);//�]�w�ĥ|��?���D�س��O
            stmt.setString(5, QB_SECTION);//�]�w�Ĥ���?���D�ظ`�O
            stmt.setString(6, QB_LEVEL);//�]�w�Ĥ���?���D��������
            stmt.setString(7, QB_NO);//�]�w�ĤC��?���D�ؽs��
            stmt.setString(8, QB_ANSWER);//�]�w�ĤK��?���D�ص���
            stmt.setString(9, QB_PIC);//�]�w�ĤE��?���D�عϤ���m
            stmt.setString(10, QB_EXPLAIN);//�]�w�ĤQ��?���D�ظԸѦ�m
            stmt.setString(11, isfill);//�]�w�ĤQ�@��?���D�جO�_����R
            stmt.setString(12, ispick);//�]�w�ĤQ�G��?���D�جO�_�����
            stmt.executeUpdate();//����sql
            stmt.close();
            stmt = null;
            Executions.sendRedirect("Quiz_manage.zul");//�ɦ^�D�غ޲z����
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
