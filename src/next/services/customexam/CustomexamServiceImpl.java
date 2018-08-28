package next.services.customexam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.zk.ui.Executions;

import exam.service.Exam;
import next.services.bindclass.Bindclass;
public class CustomexamServiceImpl implements CustomexamService {

	//搜尋客製考卷
	@Override
	public List<Exam> searchexam(String exam_keyin) {
		List<Exam> customlist = new ArrayList<Exam>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	String is_explain = null ;
        	String exam_code = null;//裝題目的字串
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT EB_NO,EB_NAME,EB_MAIN,is_explain FROM next_generation.exam_base"
        			+ " where EB_NO = ? ;");
        	stmt.setNString(1, exam_keyin);//用輸入的考卷名稱當搜尋條件
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
			while (resultSet.next()) {//如果有撈到東西
				exam_code = resultSet.getString("EB_MAIN");//把EB_MAIN丟進exam_code
				is_explain = resultSet.getString("is_explain");//是否給看詳解 1給看 0不給看
			}
			String exam_code_2 = exam_code.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除exam_code裡的[]跟所有空白
			String[] split_code = exam_code_2.split(",");//用逗號切開
			String split_code_2 =  Arrays.toString(split_code);
			String[] explain_code =  new String[split_code.length];//裝詳解的陣列
			for(int i = 0; i < split_code.length; i++){
				stmt = conn.prepareStatement("select QB_EXPLAIN"
	        			+ " from next_generation.quiz_base where QB_CODE = ?");
	        	stmt.setNString(1, split_code[i]);//用split_code[i]當搜尋條件(此為唯一值)
	        	ResultSet resultSet3 = stmt.executeQuery();//執行查詢並將結果塞進resultSet3
	        	while (resultSet3.next()) {//如果有撈到東西
	        		explain_code[i] = resultSet3.getString("QB_EXPLAIN");
	        	}
			}
			String explain_code_2 =  Arrays.toString(explain_code);
			
			Exam examsource;
			for(int i = 0; i < split_code.length; i++){
	        	stmt = conn.prepareStatement("select QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_CODE,QB_MAIN,QB_PIC,QB_ANSWER,QB_EXPLAIN,isfill,ispick"
	        			+ " from next_generation.quiz_base where QB_CODE = ?");
	        	stmt.setNString(1, split_code[i]);//用split_code[i]當搜尋條件(此為唯一值)
	        	ResultSet resultSet2 = stmt.executeQuery();//執行查詢並將結果塞進resultSet2
				while (resultSet2.next()) {//如果有撈到東西
					examsource = new Exam();
					examsource.setQB_BOOK(resultSet2.getString("QB_BOOK"));
			        examsource.setQB_VERSION(resultSet2.getString("QB_VERSION"));
					examsource.setQB_CHAPTER(resultSet2.getString("QB_CHAPTER"));
					examsource.setQB_SECTION(resultSet2.getString("QB_SECTION"));
					examsource.setQB_LEVEL(resultSet2.getString("QB_LEVEL"));
					examsource.setQB_NO(resultSet2.getString("QB_NO"));
					examsource.setQB_CODE(split_code_2);
					examsource.setQB_MAIN(resultSet2.getString("QB_MAIN"));
					examsource.setQB_PIC(resultSet2.getString("QB_PIC"));
					examsource.setQB_ANSWER(resultSet2.getString("QB_ANSWER"));
					examsource.setQB_EXPLAIN(explain_code_2);
					examsource.setis_fill(resultSet2.getString("isfill"));
					examsource.setis_pick(resultSet2.getString("ispick"));
					examsource.setQB_MAIN(is_explain);//放是否給看詳解
					customlist.add(examsource);
				}
			}
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
        }
		return customlist;
	}

	//檢查是否有輸入的客製考卷
	@Override
	public int checkexam(String exam_keyin) {
		int ifexist = 0;//預設為0(沒有該考卷)
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT EB_NO,EB_NAME,EB_MAIN FROM next_generation.exam_base"
        			+ " where EB_NO = ? ;");
        	stmt.setNString(1, exam_keyin);//用輸入的考卷名稱當搜尋條件
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
			while (resultSet.next()) {//如果有撈到東西
				ifexist = 1;//設定為1(有考卷)
			}
	       	stmt.close();
        	stmt = null;
        }catch (SQLException e) {
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
		return ifexist;
	}
	//模擬考的題目搜尋-單冊
	@Override
	public List<Exam> simulationexam(String sim_ver, String sim_book) {
		List<Exam> simulationlist = new ArrayList<Exam>();
		String condition_c1 = "",condition_c2= "",condition_c3= "",condition_c4= "",condition_c5= "",
				condition_c6= "",condition_c7= "",condition_c8= "",condition_c9= "",condition_c10= "",condition_c11= "";//章別條件
		String condition_s1= "",condition_s2= "",condition_s3= "",condition_s4= "",condition_s5= "",
				condition_s6= "",condition_s7= "",condition_s8= "",condition_s9= "",condition_s10= "",condition_s11= "";//節別條件
		//先判斷版本與冊別 來給予搜尋參數 南一與瀚林完全相同 康軒第六冊不同 部編版完全不同
		if(sim_book.equals("jr1_1")&&sim_ver.equals("ni")||sim_book.equals("jr1_1")&&sim_ver.equals("hanlin")||sim_book.equals("jr1_1")&&sim_ver.equals("knsh")) {//第一冊
			condition_c1 = "1";condition_s1="1";
			condition_c2 = "1";condition_s2="4";
			condition_c3 = "1";condition_s3="5";
			condition_c4 = "2";condition_s4="1";
			condition_c5 = "2";condition_s5="2";
			condition_c6 = "2";condition_s6="4";
			condition_c7 = "3";condition_s7="1";
			condition_c8 = "3";condition_s8="2";
			condition_c9 = "3";condition_s9="3";
		}
		else if(sim_book.equals("jr1_2")&&sim_ver.equals("ni")||sim_book.equals("jr1_2")&&sim_ver.equals("hanlin")||sim_book.equals("jr1_2")&&sim_ver.equals("knsh")) {//第二冊
			condition_c1 = "1";condition_s1="1";
			condition_c2 = "1";condition_s2="2";
			condition_c3 = "1";condition_s3="3";
			condition_c4 = "2";condition_s4="1";
			condition_c5 = "2";condition_s5="2";
			condition_c6 = "2";condition_s6="3";
			condition_c7 = "4";condition_s7="1";
			condition_c8 = "5";condition_s8="1";
			condition_c9 = "5";condition_s9="2";
		}
		else if(sim_book.equals("jr2_1")&&sim_ver.equals("ni")||sim_book.equals("jr2_1")&&sim_ver.equals("hanlin")||sim_book.equals("jr2_1")&&sim_ver.equals("knsh")) {//第三冊
			condition_c1 = "1";condition_s1="1";
			condition_c2 = "1";condition_s2="3";
			condition_c3 = "2";condition_s3="1";
			condition_c4 = "2";condition_s4="2";
			condition_c5 = "2";condition_s5="3";
			condition_c6 = "3";condition_s6="1";
			condition_c7 = "3";condition_s7="3";
			condition_c8 = "4";condition_s8="2";
		}
		else if(sim_book.equals("jr2_2")&&sim_ver.equals("ni")||sim_book.equals("jr2_2")&&sim_ver.equals("hanlin")||sim_book.equals("jr2_2")&&sim_ver.equals("knsh")) {//第四冊
			condition_c1 = "1";condition_s1="2";
			condition_c2 = "2";condition_s2="1";
			condition_c3 = "2";condition_s3="2";
			condition_c4 = "2";condition_s4="3";
			condition_c5 = "3";condition_s5="1";
			condition_c6 = "3";condition_s6="2";
			condition_c7 = "3";condition_s7="3";
			condition_c8 = "3";condition_s8="4";
			condition_c9 = "4";condition_s9="1";
			condition_c10 = "4";condition_s10="2"; 
			condition_c11 = "4";condition_s11="3";
		}
		else if(sim_book.equals("jr3_1")&&sim_ver.equals("ni")||sim_book.equals("jr3_1")&&sim_ver.equals("hanlin")||sim_book.equals("jr3_1")&&sim_ver.equals("knsh")) {//第五冊
			condition_c1 = "1";condition_s1="2";
			condition_c2 = "1";condition_s2="3";
			condition_c3 = "2";condition_s3="1";
			condition_c4 = "2";condition_s4="2";
			condition_c5 = "3";condition_s5="1";
			condition_c6 = "3";condition_s6="2";
		}
		else if(sim_book.equals("jr3_2")&&sim_ver.equals("ni")||sim_book.equals("jr3_2")&&sim_ver.equals("hanlin")) {//第六冊 南一或瀚林
			condition_c1 = "1";condition_s1="1";
			condition_c2 = "1";condition_s2="2";
			condition_c3 = "1";condition_s3="3";
			condition_c4 = "2";condition_s4="1";
			condition_c5 = "2";condition_s5="2";
			condition_c6 = "3";condition_s6="1";
			condition_c7 = "3";condition_s7="2";
			condition_c8 = "3";condition_s8="3";
		}
		else if(sim_book.equals("jr3_2")&&sim_ver.equals("knsh")) {//第六冊 康軒
			condition_c1 = "1";condition_s1="1";
			condition_c2 = "1";condition_s2="2";
			condition_c3 = "1";condition_s3="3";
			condition_c4 = "2";condition_s4="1";
			condition_c5 = "3";condition_s5="1";
			condition_c6 = "3";condition_s6="2";
			condition_c7 = "3";condition_s7="3";
		}
		else if(sim_book.equals("jr1_1")&&sim_ver.equals("naer")) {//第一冊 部編
			condition_c1 = "1";condition_s1="1";
			condition_c2 = "1";condition_s2="4";
			condition_c3 = "2";condition_s3="1";
			condition_c4 = "2";condition_s4="2";
			condition_c5 = "2";condition_s5="4";
			condition_c6 = "2";condition_s6="5";
			condition_c7 = "3";condition_s7="1";
			condition_c8 = "3";condition_s8="2";
			condition_c9 = "3";condition_s9="3";
		}
		else if(sim_book.equals("jr1_2")&&sim_ver.equals("naer")) {//第二冊 部編
			condition_c1 = "1";condition_s1="1";
			condition_c2 = "1";condition_s2="2";
			condition_c3 = "1";condition_s3="3";
			condition_c4 = "2";condition_s4="3";
			condition_c5 = "3";condition_s5="1";
			condition_c6 = "3";condition_s6="2";
			condition_c7 = "3";condition_s7="3";
			condition_c8 = "3";condition_s8="4";
			condition_c9 = "4";condition_s9="1";
			condition_c10 = "4";condition_s10="2"; 
		}
		else if(sim_book.equals("jr2_1")&&sim_ver.equals("naer")) {//第三冊 部編
			condition_c1 = "1";condition_s1="1";
			condition_c2 = "1";condition_s2="3";
			condition_c3 = "2";condition_s3="1";
			condition_c4 = "2";condition_s4="2";
			condition_c5 = "2";condition_s5="3";
			condition_c6 = "3";condition_s6="1";
			condition_c7 = "3";condition_s7="2";
			condition_c8 = "4";condition_s8="3";
		}
		else if(sim_book.equals("jr2_2")&&sim_ver.equals("naer")) {//第四冊 部編
			condition_c1 = "1";condition_s1="2";
			condition_c2 = "2";condition_s2="1";
			condition_c3 = "2";condition_s3="2";
			condition_c4 = "2";condition_s4="3";
			condition_c5 = "3";condition_s5="1";
			condition_c6 = "3";condition_s6="2";
			condition_c7 = "3";condition_s7="3";
			condition_c8 = "4";condition_s8="1";
			condition_c9 = "4";condition_s9="3";
			condition_c10 = "4";condition_s10="4"; 
		}
		else if(sim_book.equals("jr3_1")&&sim_ver.equals("naer")) {//第五冊 部編
			condition_c1 = "1";condition_s1="2";
			condition_c2 = "1";condition_s2="3";
			condition_c3 = "2";condition_s3="1";
			condition_c4 = "2";condition_s4="2";
			condition_c5 = "2";condition_s5="4";
			condition_c6 = "3";condition_s6="1";
			condition_c7 = "3";condition_s7="2";
		}
		else if(sim_book.equals("jr3_2")&&sim_ver.equals("naer")) {//第六冊 部編
			condition_c1 = "1";condition_s1="1";
			condition_c2 = "1";condition_s2="2";
			condition_c3 = "1";condition_s3="3";
		}
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	if(sim_book.equals("jr1_1")&&sim_ver.equals("ni")||sim_book.equals("jr1_1")&&sim_ver.equals("hanlin")||sim_book.equals("jr1_1")&&sim_ver.equals("knsh")) {
        		//南一或瀚林或康軒-第一冊
        		stmt = conn.prepareStatement(
        			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)");
        		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
            	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
            	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
        		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
            	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
            	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
            	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
            	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
            	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
            	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
            	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
            	stmt.setNString(25,sim_book);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
            	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
            	stmt.setNString(29,sim_book);/*第二九個?是模擬考的冊別*/stmt.setNString(30,sim_ver);//第三十個?是模擬考的版本
            	stmt.setNString(31,condition_c8);/*第三一個?是模擬考的章別*/stmt.setNString(32,condition_s8);//第三二個?是模擬考的節別
            	stmt.setNString(33,sim_book);/*第三三個?是模擬考的冊別*/stmt.setNString(34,sim_ver);//第三四個?是模擬考的版本
            	stmt.setNString(35,condition_c9);/*第三五個?是模擬考的章別*/stmt.setNString(36,condition_s9);//第三六個?是模擬考的節別
            	//南一的出題條件1~4:111(一冊一章一節) 5~7:114(一冊一章四節) 8~9:115 10~11:121 12~15:122 16~19:124 20~21:131 22~23:132 24~27:133
        	}
        	else if(sim_book.equals("jr1_2")&&sim_ver.equals("ni")||sim_book.equals("jr1_2")&&sim_ver.equals("hanlin")||sim_book.equals("jr1_2")&&sim_ver.equals("knsh")) {
        		//南一或瀚林或康軒-第二冊
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 5)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 6)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)");
            		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
                	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
            		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
                	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
            		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
                	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
                	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
                	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
                	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
                	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
                	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
                	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
                	stmt.setNString(25,sim_book);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
                	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
                	stmt.setNString(29,sim_book);/*第二九個?是模擬考的冊別*/stmt.setNString(30,sim_ver);//第三十個?是模擬考的版本
                	stmt.setNString(31,condition_c8);/*第三一個?是模擬考的章別*/stmt.setNString(32,condition_s8);//第三二個?是模擬考的節別
                	stmt.setNString(33,sim_book);/*第三三個?是模擬考的冊別*/stmt.setNString(34,sim_ver);//第三四個?是模擬考的版本
                	stmt.setNString(35,condition_c9);/*第三五個?是模擬考的章別*/stmt.setNString(36,condition_s9);//第三六個?是模擬考的節別
                	//南一的出題條件1~2:211(二冊一章一節) 3~6:212(二冊一章二節) 7~9:213 10~14:221 15~16:222 17~22:233 23:241 24~25:251 26~27:252
        	}
        	else if(sim_book.equals("jr2_1")&&sim_ver.equals("ni")||sim_book.equals("jr2_1")&&sim_ver.equals("hanlin")||sim_book.equals("jr2_1")&&sim_ver.equals("knsh")) {
        		//南一或瀚林或康軒-第三冊
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 5)");
            		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
                	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
            		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
                	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
            		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
                	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
                	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
                	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
                	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
                	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
                	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
                	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
                	stmt.setNString(25,sim_book);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
                	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
                	stmt.setNString(29,sim_book);/*第二九個?是模擬考的冊別*/stmt.setNString(30,sim_ver);//第三十個?是模擬考的版本
                	stmt.setNString(31,condition_c8);/*第三一個?是模擬考的章別*/stmt.setNString(32,condition_s8);//第三二個?是模擬考的節別
                	//南一的出題條件1~2:311 3~5:313 6~8:321 9~11:322 12~15:323 16~18:331 19~22:333 23~27:342
        	}
        	else if(sim_book.equals("jr2_2")&&sim_ver.equals("ni")||sim_book.equals("jr2_2")&&sim_ver.equals("hanlin")||sim_book.equals("jr2_2")&&sim_ver.equals("knsh")) {
        		//南一或瀚林或康軒-第四冊
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)");
            		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
                	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
            		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
                	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
            		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
                	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
                	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
                	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
                	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
                	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
                	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
                	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
                	stmt.setNString(25,sim_book);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
                	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
                	stmt.setNString(29,sim_book);/*第二九個?是模擬考的冊別*/stmt.setNString(30,sim_ver);//第三十個?是模擬考的版本
                	stmt.setNString(31,condition_c8);/*第三一個?是模擬考的章別*/stmt.setNString(32,condition_s8);//第三二個?是模擬考的節別
                	stmt.setNString(33,sim_book);/*第三三個?是模擬考的冊別*/stmt.setNString(34,sim_ver);//第三四個?是模擬考的版本
                	stmt.setNString(35,condition_c9);/*第三五個?是模擬考的章別*/stmt.setNString(36,condition_s9);//第三六個?是模擬考的節別
                	stmt.setNString(37,sim_book);/*第三七個?是模擬考的冊別*/stmt.setNString(38,sim_ver);//第三八個?是模擬考的版本
                	stmt.setNString(39,condition_c10);/*第三九個?是模擬考的章別*/stmt.setNString(40,condition_s10);//第四十個?是模擬考的節別
                	stmt.setNString(41,sim_book);/*第四一個?是模擬考的冊別*/stmt.setNString(42,sim_ver);//第四二個?是模擬考的版本
                	stmt.setNString(43,condition_c11);/*第四三個?是模擬考的章別*/stmt.setNString(44,condition_s11);//第四四個?是模擬考的節別
                	//南一的出題條件1~3:412 4~6:421 7~9:422 10~11:423 12~14:431 15~17:432 18:433 19~22:434 23~24:441 25~26:442 27:443
        	}
        	else if(sim_book.equals("jr3_1")&&sim_ver.equals("ni")||sim_book.equals("jr3_1")&&sim_ver.equals("hanlin")||sim_book.equals("jr3_1")&&sim_ver.equals("knsh")) {
        		//南一或瀚林或康軒-第五冊
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 5)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 12)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)");
            		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
                	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
            		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
                	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
            		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
                	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
                	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
                	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
                	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
                	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
                	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
                	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
                	//南一的出題條件1~2:512 3~5:513 6:521 7~11:522 12~23:531 24~27:532
        	}
        	else if(sim_book.equals("jr3_2")&&sim_ver.equals("knsh")) {
        		//康軒-第六冊
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 7)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 5)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 5)");
            		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
                	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
            		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
                	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
            		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
                	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
                	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
                	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
                	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
                	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
                	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
                	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
                	stmt.setNString(25,sim_book);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
                	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
                	//康軒的出題條件1~2:611 3~5:612 6~7:613 8~14:621 15~19:631 20~22:632 23~27:633
        	}
        	else if(sim_book.equals("jr3_2")&&sim_ver.equals("ni")||sim_book.equals("jr3_2")&&sim_ver.equals("hanlin")) {
        		//南一或瀚林-第六冊
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 5)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 5)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 5)");
            		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
                	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
            		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
                	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
            		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
                	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
                	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
                	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
                	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
                	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
                	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
                	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
                	stmt.setNString(25,sim_book);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
                	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
                	stmt.setNString(29,sim_book);/*第二九個?是模擬考的冊別*/stmt.setNString(30,sim_ver);//第三十個?是模擬考的版本
                	stmt.setNString(31,condition_c8);/*第三一個?是模擬考的章別*/stmt.setNString(32,condition_s8);//第三二個?是模擬考的節別
                	//南一的出題條件1~2:611 3~5:612 6~7:613 8~12:621 13~14:622 15~19:631 20~22:632 23~27:633
        	}
        	else if(sim_book.equals("jr1_1")&&sim_ver.equals("naer")) {
        		//部編-第一冊
        		stmt = conn.prepareStatement(
        			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)");
        		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
            	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
            	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
        		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
            	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
            	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
            	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
            	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
            	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
            	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
            	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
            	stmt.setNString(25,sim_book);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
            	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
            	stmt.setNString(29,sim_book);/*第二九個?是模擬考的冊別*/stmt.setNString(30,sim_ver);//第三十個?是模擬考的版本
            	stmt.setNString(31,condition_c8);/*第三一個?是模擬考的章別*/stmt.setNString(32,condition_s8);//第三二個?是模擬考的節別
            	stmt.setNString(33,sim_book);/*第三三個?是模擬考的冊別*/stmt.setNString(34,sim_ver);//第三四個?是模擬考的版本
            	stmt.setNString(35,condition_c9);/*第三五個?是模擬考的章別*/stmt.setNString(36,condition_s9);//第三六個?是模擬考的節別
            	//部編的出題條件1~4:111 5~7:114 8~9:121 10~13:122 14~17:124 18~19:125 20~21:131 22~23:132 24~27:133
        	}
        	else if(sim_book.equals("jr1_2")&&sim_ver.equals("naer")) {
        		//部編-第二冊
        		stmt = conn.prepareStatement(
        			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 6 )"
            		+ "UNION ALL"
            		+ " select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ?  ) ORDER BY RAND() limit 6) as group1 "
            		+ "UNION ALL "
            		+ "select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ) ORDER BY RAND() limit 2) as group2 "
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)");
        		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
            	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
            	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
        		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
            	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
            	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
            	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
            	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
            	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
            	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
            	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
            	stmt.setNString(25,sim_book);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
            	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
            	stmt.setNString(29,sim_book);/*第二九個?是模擬考的冊別*/stmt.setNString(30,sim_ver);//第三十個?是模擬考的版本
            	stmt.setNString(31,condition_c8);/*第三一個?是模擬考的章別*/stmt.setNString(32,condition_s8);//第三二個?是模擬考的節別
            	stmt.setNString(33,sim_book);/*第三三個?是模擬考的冊別*/stmt.setNString(34,sim_ver);//第三四個?是模擬考的版本
            	stmt.setNString(35,condition_c9);/*第三五個?是模擬考的章別*/stmt.setNString(36,condition_s9);//第三六個?是模擬考的節別
            	stmt.setNString(37,sim_book);/*第三七個?是模擬考的冊別*/stmt.setNString(38,sim_ver);//第三八個?是模擬考的版本
            	stmt.setNString(39,condition_c10);/*第三九個?是模擬考的章別*/stmt.setNString(40,condition_s10);//第四十個?是模擬考的節別
            	//部編的出題條件1~2:211 3~6:212 7~9:213 10~15:223 16~21:(231+232) 22~23:(233+234) 24~25:241 26~27:242
        	}
        	else if(sim_book.equals("jr2_1")&&sim_ver.equals("naer")) {
        		//部編-第三冊
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 5)");
            		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
                	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
            		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
                	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
            		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
                	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
                	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
                	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
                	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
                	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
                	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
                	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
                	stmt.setNString(25,sim_book);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
                	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
                	stmt.setNString(29,sim_book);/*第二九個?是模擬考的冊別*/stmt.setNString(30,sim_ver);//第三十個?是模擬考的版本
                	stmt.setNString(31,condition_c8);/*第三一個?是模擬考的章別*/stmt.setNString(32,condition_s8);//第三二個?是模擬考的節別
                	//部編的出題條件
        	}
        	else if(sim_book.equals("jr2_2")&&sim_ver.equals("naer")) {
        		//部編-第四冊
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ " select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ?  ) ORDER BY RAND() limit 1) as group1 ");
            		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
                	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
            		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
                	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
            		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
                	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
                	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
                	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
                	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
                	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
                	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
                	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
                	stmt.setNString(25,sim_book);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
                	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
                	stmt.setNString(29,sim_book);/*第二九個?是模擬考的冊別*/stmt.setNString(30,sim_ver);//第三十個?是模擬考的版本
                	stmt.setNString(31,condition_c8);/*第三一個?是模擬考的章別*/stmt.setNString(32,condition_s8);//第三二個?是模擬考的節別
                	stmt.setNString(33,sim_book);/*第三三個?是模擬考的冊別*/stmt.setNString(34,sim_ver);//第三四個?是模擬考的版本
                	stmt.setNString(35,condition_c9);/*第三五個?是模擬考的章別*/stmt.setNString(36,condition_s9);//第三六個?是模擬考的節別
                	stmt.setNString(37,sim_book);/*第三七個?是模擬考的冊別*/stmt.setNString(38,sim_ver);//第三八個?是模擬考的版本
                	stmt.setNString(39,condition_c10);/*第三九個?是模擬考的章別*/stmt.setNString(40,condition_s10);//第四十個?是模擬考的節別
                	//部編的出題條件
        	}
        	else if(sim_book.equals("jr3_1")&&sim_ver.equals("naer")) {
        		//部編-第五冊
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 7)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 5)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 5)");
            		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
                	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
            		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
                	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
            		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
                	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
                	stmt.setNString(13,sim_book);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
                	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
                	stmt.setNString(17,sim_book);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
                	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
                	stmt.setNString(21,sim_book);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
                	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
                	stmt.setNString(25,sim_book);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
                	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
                	//部編的出題條件
        	}
        	else if(sim_book.equals("jr3_2")&&sim_ver.equals("naer")) {
        		//部編-第六冊
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 9)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 9)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 9)");
            		stmt.setNString(1,sim_book);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
                	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
            		stmt.setNString(5,sim_book);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
                	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
            		stmt.setNString(9,sim_book);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
                	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
                	//部編的出題條件
        	}
        	/**stmt = conn.prepareStatement("select QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_CODE,"
        			+ "QB_MAIN,QB_PIC,QB_ANSWER,QB_EXPLAIN,isfill,ispick from next_generation.quiz_base "
        			+ "where QB_BOOK = ? and QB_VERSION = ? ORDER BY RAND()  limit 5");
        	//----------------限制5題為測試,正式版為27題
        	stmt.setNString(1,sim_book);//第一個?是模擬考的冊別
        	stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
        	int re_lenth = 5; //-----------長度由上面limit決定**/
        	ResultSet rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	
        	rs.last();//移動到最後一筆
        	int re_lenth = rs.getRow(); //-----------長度等於最後一行row
        	rs.beforeFirst();//將rs轉回最開頭
        	
			String[] all_question = new String[re_lenth];//存放全部題目
				while (rs.next()) {//如果有撈到東西
					int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
					all_question[i] = rs.getString("QB_CODE");		
				}
				String all_question_2 =  Arrays.toString(all_question);
			rs.beforeFirst();//將rs跳轉到最開頭		
			
			String[] all_EXPLAIN = new String[re_lenth];//存放全部詳解
			while (rs.next()) {//如果有撈到東西
				int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
				all_EXPLAIN[i] = rs.getString("QB_EXPLAIN");		
			}
			String all_EXPLAIN_2 =  Arrays.toString(all_EXPLAIN);
			rs.beforeFirst();//將rs跳轉到最開頭
			Exam examsource;				
			while (rs.next()) {
				examsource = new Exam();
				examsource.setQB_BOOK(rs.getString("QB_BOOK"));
		        examsource.setQB_VERSION(rs.getString("QB_VERSION"));
				examsource.setQB_CHAPTER(rs.getString("QB_CHAPTER"));
				examsource.setQB_SECTION(rs.getString("QB_SECTION"));
				examsource.setQB_LEVEL(rs.getString("QB_LEVEL"));
				examsource.setQB_NO(rs.getString("QB_NO"));
				examsource.setQB_CODE(all_question_2);
				examsource.setQB_MAIN(rs.getString("QB_MAIN"));
				examsource.setQB_PIC(rs.getString("QB_PIC"));
				examsource.setQB_ANSWER(rs.getString("QB_ANSWER"));
				examsource.setQB_EXPLAIN(all_EXPLAIN_2);
				examsource.setis_fill(rs.getString("isfill"));
				examsource.setis_pick(rs.getString("ispick"));
				simulationlist.add(examsource);
			}			 
	       	stmt.close();
        	stmt = null;
        	
        }
        catch (SQLException e) {
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
		return simulationlist;
	}
	//模擬考的題目搜尋-多冊 參數條件參考出題條件doc檔
	@Override
	public List<Exam> simulationexam_r(String sim_ver, String sim_book) {
		List<Exam> simulationlist = new ArrayList<Exam>();
		//章別條件
		String condition_b1 = "",condition_b2= "",condition_b3= "",condition_b4= "",condition_b5= "",condition_b6= "",condition_b7= "",condition_b8= "",condition_b9= "",condition_b10= "",
				condition_b11 = "",condition_b12= "",condition_b13= "",condition_b14= "",condition_b15= "",condition_b16= "",condition_b17= "",condition_b18= "",condition_b19= "",condition_b20= "",
				condition_b21 = "",condition_b22= "",condition_b23= "",condition_b24= "",condition_b25= "",condition_b26= "",condition_b27= "",condition_b28= "",condition_b29= "",condition_b30= "",
				condition_b31 = "",condition_b32= "",condition_b33= "",condition_b34= "",condition_b35= "",condition_b36= "",condition_b37= "",condition_b38= "",condition_b39= "",condition_b40= "",
				condition_b41 = "",condition_b42= "",condition_b43= "",condition_b44= "",condition_b45= "",condition_b46= "",condition_b47= "",condition_b48= "",condition_b49= "";
		//章別條件
		String condition_c1 = "",condition_c2= "",condition_c3= "",condition_c4= "",condition_c5= "",condition_c6= "",condition_c7= "",condition_c8= "",condition_c9= "",condition_c10= "",
				condition_c11 = "",condition_c12= "",condition_c13= "",condition_c14= "",condition_c15= "",condition_c16= "",condition_c17= "",condition_c18= "",condition_c19= "",condition_c20= "",
				condition_c21 = "",condition_c22= "",condition_c23= "",condition_c24= "",condition_c25= "",condition_c26= "",condition_c27= "",condition_c28= "",condition_c29= "",condition_c30= "",
				condition_c31 = "",condition_c32= "",condition_c33= "",condition_c34= "",condition_c35= "",condition_c36= "",condition_c37= "",condition_c38= "",condition_c39= "",condition_c40= "",
				condition_c41 = "",condition_c42= "",condition_c43= "",condition_c44= "",condition_c45= "",condition_c46= "",condition_c47= "",condition_c48= "",condition_c49= "";
		//節別條件
		String condition_s1= "",condition_s2= "",condition_s3= "",condition_s4= "",condition_s5= "",condition_s6= "",condition_s7= "",condition_s8= "",condition_s9= "",condition_s10= "",
				condition_s11 = "",condition_s12= "",condition_s13= "",condition_s14= "",condition_s15= "",condition_s16= "",condition_s17= "",condition_s18= "",condition_s19= "",condition_s20= "",
				condition_s21 = "",condition_s22= "",condition_s23= "",condition_s24= "",condition_s25= "",condition_s26= "",condition_s27= "",condition_s28= "",condition_s29= "",condition_s30= "",
				condition_s31 = "",condition_s32= "",condition_s33= "",condition_s34= "",condition_s35= "",condition_s36= "",condition_s37= "",condition_s38= "",condition_s39= "",condition_s40= "",
				condition_s41 = "",condition_s42= "",condition_s43= "",condition_s44= "",condition_s45= "",condition_s46= "",condition_s47= "",condition_s48= "",condition_s49= "";
		//先判斷選擇的章節與版本  來給予搜尋參數
		if(sim_book.equals("range_1_2")&&sim_ver.equals("ni")||sim_book.equals("range_1_2")&&sim_ver.equals("hanlin")||sim_book.equals("range_1_2")&&sim_ver.equals("knsh")) {
			condition_b1 = "jr1_1";condition_c1 = "1";condition_s1="1";
			condition_b2 = "jr1_2";condition_c2 = "2";condition_s2="1";
			condition_b3 = "jr1_2";condition_c3 = "3";condition_s3="3";
			condition_b4 = "jr1_1";condition_c4 = "1";condition_s4="4";
			condition_b5 = "jr1_1";condition_c5 = "1";condition_s5="5";
			condition_b6 = "jr1_1";condition_c6 = "2";condition_s6="1";
			condition_b7 = "jr1_1";condition_c7 = "2";condition_s7="2";
			condition_b8 = "jr1_1";condition_c8 = "2";condition_s8="4";
			condition_b9 = "jr1_1";condition_c9 = "3";condition_s9="1";
			condition_b10 = "jr1_1";condition_c10 = "3";condition_s10="2";
			condition_b11 = "jr1_1";condition_c11 = "3";condition_s11="3";
			condition_b12 = "jr1_2";condition_c12 = "1";condition_s12="1";
			condition_b13 = "jr1_2";condition_c13 = "1";condition_s13="2";
			condition_b14 = "jr1_2";condition_c14 = "1";condition_s14="3";
			condition_b15 = "jr1_2";condition_c15 = "5";condition_s15="1";
			condition_b16 = "jr1_2";condition_c16 = "5";condition_s16="2";
		}
		else if(sim_book.equals("range_3_4")&&sim_ver.equals("ni")||sim_book.equals("range_3_4")&&sim_ver.equals("hanlin")||sim_book.equals("range_3_4")&&sim_ver.equals("knsh")) {
			condition_b1 = "jr2_1";condition_c1 = "1";condition_s1="1";
			condition_b2 = "jr2_1";condition_c2 = "1";condition_s2="3";
			condition_b3 = "jr2_1";condition_c3 = "2";condition_s3="1";
			condition_b4 = "jr2_1";condition_c4 = "2";condition_s4="2";
			condition_b5 = "jr2_1";condition_c5 = "2";condition_s5="3";
			condition_b6 = "jr2_1";condition_c6 = "3";condition_s6="1";
			condition_b7 = "jr2_1";condition_c7 = "3";condition_s7="3";
			condition_b8 = "jr2_1";condition_c8 = "4";condition_s8="2";
			condition_b9 = "jr2_2";condition_c9 = "1";condition_s9="2";
			condition_b10 = "jr2_2";condition_c10 = "2";condition_s10="1";
			condition_b11 = "jr2_2";condition_c11 = "2";condition_s11="2";
			condition_b12 = "jr2_2";condition_c12 = "2";condition_s12="3";
			condition_b13 = "jr2_2";condition_c13 = "3";condition_s13="1";
			condition_b14 = "jr2_2";condition_c14 = "3";condition_s14="2";
			condition_b15 = "jr2_2";condition_c15 = "3";condition_s15="3";
			condition_b16 = "jr2_2";condition_c16 = "3";condition_s16="4";
			condition_b17 = "jr2_2";condition_c17 = "4";condition_s17="1";
			condition_b18 = "jr2_2";condition_c18 = "4";condition_s18="2";
			condition_b19 = "jr2_2";condition_c19 = "4";condition_s19="3";
		}
		else if(sim_book.equals("range_1_3")&&sim_ver.equals("ni")||sim_book.equals("range_1_3")&&sim_ver.equals("hanlin")||sim_book.equals("range_1_3")&&sim_ver.equals("knsh")) {
			condition_b1 = "jr1_1";condition_c1 = "1";condition_s1="1";
			condition_b2 = "jr1_2";condition_c2 = "2";condition_s2="1";
			condition_b3 = "jr1_2";condition_c3 = "3";condition_s3="3";
			condition_b4 = "jr1_1";condition_c4 = "1";condition_s4="4";
			condition_b5 = "jr1_1";condition_c5 = "1";condition_s5="5";
			condition_b6 = "jr1_1";condition_c6 = "2";condition_s6="1";
			condition_b7 = "jr1_1";condition_c7 = "2";condition_s7="2";
			condition_b8 = "jr1_1";condition_c8 = "2";condition_s8="4";
			condition_b9 = "jr1_1";condition_c9 = "3";condition_s9="1";
			condition_b10 = "jr1_1";condition_c10 = "3";condition_s10="2";
			condition_b11 = "jr1_1";condition_c11 = "3";condition_s11="3";
			condition_b12 = "jr1_2";condition_c12 = "1";condition_s12="1";
			condition_b13 = "jr1_2";condition_c13 = "1";condition_s13="2";
			condition_b14 = "jr1_2";condition_c14 = "1";condition_s14="3";
			condition_b15 = "jr1_2";condition_c15 = "5";condition_s15="1";
			condition_b16 = "jr1_2";condition_c16 = "5";condition_s16="2";
			condition_b17 = "jr2_1";condition_c17 = "1";condition_s17="1";
			condition_b18 = "jr2_1";condition_c18 = "1";condition_s18="3";
			condition_b19 = "jr2_1";condition_c19 = "2";condition_s19="1";
			condition_b20 = "jr2_1";condition_c20 = "2";condition_s20="2";
			condition_b21 = "jr2_1";condition_c21 = "2";condition_s21="3";
			condition_b22 = "jr2_1";condition_c22 = "3";condition_s22="3";
			condition_b23 = "jr2_1";condition_c23 = "4";condition_s23="2";
		}
		else if(sim_book.equals("range_1_4")&&sim_ver.equals("ni")||sim_book.equals("range_1_4")&&sim_ver.equals("hanlin")||sim_book.equals("range_1_4")&&sim_ver.equals("knsh")) {
			condition_b1 = "jr1_1";condition_c1 = "1";condition_s1="1";
			condition_b2 = "jr1_2";condition_c2 = "2";condition_s2="1";
			condition_b3 = "jr1_2";condition_c3 = "3";condition_s3="3";
			condition_b4 = "jr1_1";condition_c4 = "1";condition_s4="4";
			condition_b5 = "jr1_1";condition_c5 = "1";condition_s5="5";
			condition_b6 = "jr1_1";condition_c6 = "2";condition_s6="1";
			condition_b7 = "jr1_1";condition_c7 = "2";condition_s7="2";
			condition_b8 = "jr1_1";condition_c8 = "2";condition_s8="4";
			condition_b9 = "jr1_1";condition_c9 = "3";condition_s9="1";
			condition_b10 = "jr1_1";condition_c10 = "3";condition_s10="2";
			condition_b11 = "jr1_1";condition_c11 = "3";condition_s11="3";
			condition_b12 = "jr1_2";condition_c12 = "1";condition_s12="1";
			condition_b13 = "jr1_2";condition_c13 = "1";condition_s13="2";
			condition_b14 = "jr1_2";condition_c14 = "1";condition_s14="3";
			condition_b15 = "jr1_2";condition_c15 = "5";condition_s15="1";
			condition_b16 = "jr1_2";condition_c16 = "5";condition_s16="2";
			condition_b17 = "jr2_1";condition_c17 = "1";condition_s17="1";
			condition_b18 = "jr2_1";condition_c18 = "1";condition_s18="3";
			condition_b19 = "jr2_1";condition_c19 = "2";condition_s19="1";
			condition_b20 = "jr2_1";condition_c20 = "2";condition_s20="2";
			condition_b21 = "jr2_1";condition_c21 = "2";condition_s21="3";
			condition_b22 = "jr2_1";condition_c22 = "3";condition_s22="3";
			condition_b23 = "jr2_1";condition_c23 = "4";condition_s23="2";
			condition_b24 = "jr2_2";condition_c24 = "1";condition_s24="2";
			condition_b25 = "jr2_2";condition_c25 = "2";condition_s25="1";
			condition_b26 = "jr2_2";condition_c26 = "2";condition_s26="2";
			condition_b27 = "jr2_2";condition_c27 = "3";condition_s27="1";
			condition_b28 = "jr2_2";condition_c28 = "3";condition_s28="2";
			if(sim_ver.equals("hanlin")) {//翰林版的20~23由433出,另外兩版由434出
				condition_b29 = "jr2_2";condition_c29 = "3";condition_s29="3";
			}else {
				condition_b29 = "jr2_2";condition_c29 = "3";condition_s29="4";}
			condition_b30 = "jr2_2";condition_c30 = "4";condition_s30="2";
			condition_b31 = "jr2_2";condition_c31 = "2";condition_s31="3";
			condition_b32 = "jr2_2";condition_c32 = "4";condition_s32="1";
			condition_b33 = "jr2_2";condition_c33 = "4";condition_s33="3";
		}
		else if(sim_book.equals("range_1_5")&&sim_ver.equals("ni")||sim_book.equals("range_1_5")&&sim_ver.equals("hanlin")||sim_book.equals("range_1_5")&&sim_ver.equals("knsh")) {//第一冊
			condition_b1 = "jr1_1";condition_c1 = "1";condition_s1="1";
			condition_b2 = "jr1_2";condition_c2 = "2";condition_s2="1";
			condition_b3 = "jr1_2";condition_c3 = "3";condition_s3="3";
			condition_b4 = "jr1_1";condition_c4 = "1";condition_s4="4";
			condition_b5 = "jr1_1";condition_c5 = "1";condition_s5="5";
			condition_b6 = "jr1_1";condition_c6 = "2";condition_s6="1";
			condition_b7 = "jr1_1";condition_c7 = "2";condition_s7="2";
			condition_b8 = "jr1_1";condition_c8 = "2";condition_s8="4";
			condition_b9 = "jr1_1";condition_c9 = "3";condition_s9="1";
			condition_b10 = "jr1_1";condition_c10 = "3";condition_s10="2";
			condition_b11 = "jr1_1";condition_c11 = "3";condition_s11="3";
			condition_b12 = "jr1_2";condition_c12 = "1";condition_s12="1";
			condition_b13 = "jr1_2";condition_c13 = "1";condition_s13="2";
			condition_b14 = "jr1_2";condition_c14 = "1";condition_s14="3";
			condition_b15 = "jr1_2";condition_c15 = "5";condition_s15="1";
			condition_b16 = "jr1_2";condition_c16 = "5";condition_s16="2";
			condition_b17 = "jr2_1";condition_c17 = "1";condition_s17="1";
			condition_b18 = "jr2_1";condition_c18 = "1";condition_s18="3";
			condition_b19 = "jr2_1";condition_c19 = "2";condition_s19="1";
			condition_b20 = "jr2_1";condition_c20 = "2";condition_s20="2";
			condition_b21 = "jr2_1";condition_c21 = "2";condition_s21="3";
			condition_b22 = "jr2_1";condition_c22 = "3";condition_s22="3";
			condition_b23 = "jr2_1";condition_c23 = "4";condition_s23="2";
			condition_b24 = "jr2_2";condition_c24 = "1";condition_s24="2";
			condition_b25 = "jr2_2";condition_c25 = "2";condition_s25="1";
			condition_b26 = "jr2_2";condition_c26 = "2";condition_s26="2";
			condition_b27 = "jr2_2";condition_c27 = "3";condition_s27="1";
			condition_b28 = "jr2_2";condition_c28 = "3";condition_s28="2";
			if(sim_ver.equals("hanlin")) {//翰林版的15~16由433出,另外兩版由434出
				condition_b29 = "jr2_2";condition_c29 = "3";condition_s29="3";
			}else {
				condition_b29 = "jr2_2";condition_c29 = "3";condition_s29="4";
			}
			condition_b30 = "jr2_2";condition_c30 = "4";condition_s30="2";
			condition_b31 = "jr2_2";condition_c31 = "2";condition_s31="3";
			condition_b32 = "jr2_2";condition_c32 = "4";condition_s32="1";
			condition_b33 = "jr2_2";condition_c33 = "4";condition_s33="3";
			condition_b34 = "jr3_1";condition_c34 = "2";condition_s34="2";
			condition_b35 = "jr3_1";condition_c35 = "3";condition_s35="1";
			condition_b36 = "jr3_1";condition_c36 = "1";condition_s36="2";
			condition_b37 = "jr3_1";condition_c37 = "1";condition_s37="3";
			condition_b38 = "jr3_1";condition_c38 = "3";condition_s38="2";
		}
		else if(sim_book.equals("range_1_6")&&sim_ver.equals("ni")||sim_book.equals("range_1_6")&&sim_ver.equals("hanlin")||sim_book.equals("range_1_6")&&sim_ver.equals("knsh")) {//第一冊
			condition_b1 = "jr1_1";condition_c1 = "1";condition_s1="1";
			condition_b2 = "jr1_2";condition_c2 = "2";condition_s2="1";
			condition_b3 = "jr1_2";condition_c3 = "3";condition_s3="3";
			condition_b4 = "jr1_1";condition_c4 = "1";condition_s4="4";
			condition_b5 = "jr1_1";condition_c5 = "1";condition_s5="5";
			condition_b6 = "jr1_1";condition_c6 = "2";condition_s6="1";
			condition_b7 = "jr1_1";condition_c7 = "2";condition_s7="2";
			condition_b8 = "jr1_1";condition_c8 = "2";condition_s8="4";
			condition_b9 = "jr1_1";condition_c9 = "3";condition_s9="1";
			condition_b10 = "jr1_1";condition_c10 = "3";condition_s10="2";
			condition_b11 = "jr1_1";condition_c11 = "3";condition_s11="3";
			condition_b12 = "jr1_2";condition_c12 = "1";condition_s12="1";
			condition_b13 = "jr1_2";condition_c13 = "1";condition_s13="2";
			condition_b14 = "jr1_2";condition_c14 = "1";condition_s14="3";
			condition_b15 = "jr1_2";condition_c15 = "5";condition_s15="1";
			condition_b16 = "jr1_2";condition_c16 = "5";condition_s16="2";
			condition_b17 = "jr2_1";condition_c17 = "1";condition_s17="1";
			condition_b18 = "jr2_1";condition_c18 = "1";condition_s18="3";
			condition_b19 = "jr2_1";condition_c19 = "2";condition_s19="1";
			condition_b20 = "jr2_1";condition_c20 = "2";condition_s20="2";
			condition_b21 = "jr2_1";condition_c21 = "2";condition_s21="3";
			condition_b22 = "jr2_1";condition_c22 = "3";condition_s22="3";
			condition_b23 = "jr2_1";condition_c23 = "4";condition_s23="2";
			condition_b24 = "jr2_2";condition_c24 = "1";condition_s24="2";
			condition_b25 = "jr2_2";condition_c25 = "2";condition_s25="1";
			condition_b26 = "jr2_2";condition_c26 = "2";condition_s26="2";
			condition_b27 = "jr2_2";condition_c27 = "3";condition_s27="1";
			condition_b28 = "jr2_2";condition_c28 = "3";condition_s28="1";
			condition_b29 = "jr2_2";condition_c29 = "3";condition_s29="2";
			if(sim_ver.equals("hanlin")) {//翰林版的由433出,另外兩版由434出
				condition_b30 = "jr2_2";condition_c30 = "3";condition_s30="3";
				condition_b31 = "jr2_2";condition_c31 = "3";condition_s31="3";
			}else {
				condition_b30 = "jr2_2";condition_c30 = "3";condition_s30="4";
				condition_b31 = "jr2_2";condition_c31 = "3";condition_s31="4";
			}
			condition_b32 = "jr2_2";condition_c32 = "4";condition_s32="2";
			condition_b33 = "jr2_2";condition_c33 = "2";condition_s33="3";
			condition_b34 = "jr2_2";condition_c34 = "4";condition_s34="1";
			condition_b35 = "jr2_2";condition_c35 = "4";condition_s35="3";
			condition_b36 = "jr3_1";condition_c36 = "2";condition_s36="2";
			condition_b37 = "jr3_1";condition_c37 = "3";condition_s37="1";
			condition_b38 = "jr3_1";condition_c38 = "3";condition_s38="1";
			condition_b39 = "jr3_1";condition_c39 = "3";condition_s39="1";
			condition_b40 = "jr3_1";condition_c40 = "1";condition_s40="2";
			condition_b41 = "jr3_1";condition_c41 = "1";condition_s41="3";
			condition_b42 = "jr3_1";condition_c42 = "3";condition_s42="2";
			condition_b43 = "jr3_2";condition_c43 = "1";condition_s43="1";
			condition_b44 = "jr3_2";condition_c44 = "1";condition_s44="2";
			condition_b45 = "jr3_2";condition_c45 = "1";condition_s45="3";
			condition_b46 = "jr3_2";condition_c46 = "2";condition_s46="1";
			condition_b47 = "jr3_2";condition_c47 = "3";condition_s47="1";
			condition_b48 = "jr3_2";condition_c48 = "3";condition_s48="2";
			condition_b49 = "jr3_2";condition_c49 = "3";condition_s49="3";
		}
		else if(sim_book.equals("range_1_2")&&sim_ver.equals("naer")) {//部編版一~二冊
			condition_b1 = "jr1_1";condition_c1 = "1";condition_s1="1";
			condition_b2 = "jr1_1";condition_c2 = "1";condition_s2="4";
			condition_b3 = "jr1_1";condition_c3 = "2";condition_s3="1";
			condition_b4 = "jr1_1";condition_c4 = "2";condition_s4="2";
			condition_b5 = "jr1_1";condition_c5 = "2";condition_s5="4";
			condition_b6 = "jr1_1";condition_c6 = "2";condition_s6="5";
			condition_b7 = "jr1_1";condition_c7 = "3";condition_s7="1";
			condition_b8 = "jr1_1";condition_c8 = "3";condition_s8="2";
			condition_b9 = "jr1_1";condition_c9 = "3";condition_s9="3";
			condition_b10 = "jr1_2";condition_c10 = "1";condition_s10="1";
			condition_b11 = "jr1_2";condition_c11 = "1";condition_s11="2";
			condition_b12 = "jr1_2";condition_c12 = "1";condition_s12="3";
			condition_b13 = "jr1_2";condition_c13 = "3";condition_s13="1";
			condition_b14 = "jr1_2";condition_c14 = "3";condition_s14="2";
			condition_b15 = "jr1_2";condition_c15 = "4";condition_s15="1";
			condition_b16 = "jr1_2";condition_c16 = "4";condition_s16="2";
		}
		else if(sim_book.equals("range_3_4")&&sim_ver.equals("naer")) {
			condition_b1 = "jr2_1";condition_c1 = "1";condition_s1="1";
			condition_b2 = "jr2_1";condition_c2 = "1";condition_s2="3";
			condition_b3 = "jr2_1";condition_c3 = "2";condition_s3="1";
			condition_b4 = "jr2_1";condition_c4 = "2";condition_s4="2";
			condition_b5 = "jr2_1";condition_c5 = "2";condition_s5="3";
			condition_b6 = "jr2_1";condition_c6 = "3";condition_s6="1";
			condition_b7 = "jr2_1";condition_c7 = "3";condition_s7="2";
			condition_b8 = "jr2_1";condition_c8 = "4";condition_s8="3";
			condition_b9 = "jr2_2";condition_c9 = "1";condition_s9="2";
			condition_b10 = "jr2_2";condition_c10 = "2";condition_s10="1";
			condition_b11 = "jr2_2";condition_c11 = "2";condition_s11="2";
			condition_b12 = "jr2_2";condition_c12 = "2";condition_s12="3";
			condition_b13 = "jr2_2";condition_c13 = "3";condition_s13="1";
			condition_b14 = "jr2_2";condition_c14 = "3";condition_s14="2";
			condition_b15 = "jr2_2";condition_c15 = "3";condition_s15="3";
			condition_b16 = "jr2_2";condition_c16 = "4";condition_s16="1";
			condition_b17 = "jr2_2";condition_c17 = "4";condition_s17="3";
			condition_b18 = "jr2_2";condition_c18 = "4";condition_s18="4";
		}
		else if(sim_book.equals("range_1_3")&&sim_ver.equals("naer")) {
			condition_b1 = "jr1_1";condition_c1 = "1";condition_s1="2";
			condition_b2 = "jr1_2";condition_c2 = "2";condition_s2="3";
			condition_b3 = "jr1_2";condition_c3 = "3";condition_s3="4";
			condition_b4 = "jr1_1";condition_c4 = "1";condition_s4="4";
			condition_b5 = "jr1_1";condition_c5 = "2";condition_s5="1";
			condition_b6 = "jr1_1";condition_c6 = "2";condition_s6="2";
			condition_b7 = "jr1_1";condition_c7 = "2";condition_s7="4";
			condition_b8 = "jr1_1";condition_c8 = "2";condition_s8="5";
			condition_b9 = "jr1_1";condition_c9 = "3";condition_s9="1";
			condition_b10 = "jr1_1";condition_c10 = "3";condition_s10="2";
			condition_b11 = "jr1_1";condition_c11 = "3";condition_s11="3";
			condition_b12 = "jr1_2";condition_c12 = "1";condition_s12="1";
			condition_b13 = "jr1_2";condition_c13 = "1";condition_s13="2";
			condition_b14 = "jr1_2";condition_c14 = "1";condition_s14="3";
			condition_b15 = "jr1_2";condition_c15 = "4";condition_s15="1";
			condition_b16 = "jr1_2";condition_c16 = "4";condition_s16="2";
			condition_b17 = "jr2_1";condition_c17 = "1";condition_s17="1";
			condition_b18 = "jr2_1";condition_c18 = "1";condition_s18="3";
			condition_b19 = "jr2_1";condition_c19 = "2";condition_s19="1";
			condition_b20 = "jr2_1";condition_c20 = "2";condition_s20="2";
			condition_b21 = "jr2_1";condition_c21 = "2";condition_s21="3";
			condition_b22 = "jr2_1";condition_c22 = "3";condition_s22="2";
			condition_b23 = "jr2_1";condition_c23 = "4";condition_s23="3";
		}
		else if(sim_book.equals("range_1_4")&&sim_ver.equals("naer")) {
			condition_b1 = "jr1_1";condition_c1 = "1";condition_s1="1";
			condition_b2 = "jr1_2";condition_c2 = "2";condition_s2="3";
			condition_b3 = "jr1_2";condition_c3 = "3";condition_s3="4";
			condition_b4 = "jr1_1";condition_c4 = "1";condition_s4="4";
			condition_b5 = "jr1_1";condition_c5 = "2";condition_s5="1";
			condition_b6 = "jr1_1";condition_c6 = "2";condition_s6="2";
			condition_b7 = "jr1_1";condition_c7 = "2";condition_s7="4";
			condition_b8 = "jr1_1";condition_c8 = "2";condition_s8="5";
			condition_b9 = "jr1_1";condition_c9 = "3";condition_s9="1";
			condition_b10 = "jr1_1";condition_c10 = "3";condition_s10="2";
			condition_b11 = "jr1_1";condition_c11 = "3";condition_s11="3";
			condition_b12 = "jr1_2";condition_c12 = "1";condition_s12="1";
			condition_b13 = "jr1_2";condition_c13 = "1";condition_s13="2";
			condition_b14 = "jr1_2";condition_c14 = "1";condition_s14="3";
			condition_b15 = "jr1_2";condition_c15 = "4";condition_s15="1";
			condition_b16 = "jr1_2";condition_c16 = "4";condition_s16="2";
			condition_b17 = "jr2_1";condition_c17 = "1";condition_s17="1";
			condition_b18 = "jr2_1";condition_c18 = "1";condition_s18="3";
			condition_b19 = "jr2_1";condition_c19 = "2";condition_s19="1";
			condition_b20 = "jr2_1";condition_c20 = "2";condition_s20="2";
			condition_b21 = "jr2_1";condition_c21 = "2";condition_s21="3";
			condition_b22 = "jr2_1";condition_c22 = "3";condition_s22="2";
			condition_b23 = "jr2_1";condition_c23 = "4";condition_s23="3";
			condition_b24 = "jr2_2";condition_c24 = "1";condition_s24="2";
			condition_b25 = "jr2_2";condition_c25 = "2";condition_s25="1";
			condition_b26 = "jr2_2";condition_c26 = "2";condition_s26="2";
			condition_b27 = "jr2_2";condition_c27 = "3";condition_s27="1";
			condition_b28 = "jr2_2";condition_c28 = "3";condition_s28="2";
			condition_b29 = "jr2_2";condition_c29 = "3";condition_s29="3";
			condition_b30 = "jr2_2";condition_c30 = "4";condition_s30="2";
			condition_b31 = "jr2_2";condition_c31 = "2";condition_s31="3";
			condition_b32 = "jr2_2";condition_c32 = "4";condition_s32="1";
			condition_b33 = "jr2_2";condition_c33 = "4";condition_s33="3";
		}
		else if(sim_book.equals("range_1_5")&&sim_ver.equals("naer")) {
			condition_b1 = "jr1_1";condition_c1 = "1";condition_s1="1";
			condition_b2 = "jr1_2";condition_c2 = "2";condition_s2="3";
			condition_b3 = "jr1_2";condition_c3 = "3";condition_s3="4";
			condition_b4 = "jr1_1";condition_c4 = "1";condition_s4="4";
			condition_b5 = "jr1_1";condition_c5 = "2";condition_s5="1";
			condition_b6 = "jr1_1";condition_c6 = "2";condition_s6="2";
			condition_b7 = "jr1_1";condition_c7 = "2";condition_s7="4";
			condition_b8 = "jr1_1";condition_c8 = "2";condition_s8="5";
			condition_b9 = "jr1_1";condition_c9 = "3";condition_s9="1";
			condition_b10 = "jr1_1";condition_c10 = "3";condition_s10="2";
			condition_b11 = "jr1_1";condition_c11 = "3";condition_s11="3";
			condition_b12 = "jr1_2";condition_c12 = "1";condition_s12="1";
			condition_b13 = "jr1_2";condition_c13 = "1";condition_s13="2";
			condition_b14 = "jr1_2";condition_c14 = "1";condition_s14="3";
			condition_b15 = "jr1_2";condition_c15 = "4";condition_s15="1";
			condition_b16 = "jr1_2";condition_c16 = "4";condition_s16="2";
			condition_b17 = "jr2_1";condition_c17 = "1";condition_s17="1";
			condition_b18 = "jr2_1";condition_c18 = "1";condition_s18="3";
			condition_b19 = "jr2_1";condition_c19 = "2";condition_s19="1";
			condition_b20 = "jr2_1";condition_c20 = "2";condition_s20="2";
			condition_b21 = "jr2_1";condition_c21 = "2";condition_s21="3";
			condition_b22 = "jr2_1";condition_c22 = "3";condition_s22="2";
			condition_b23 = "jr2_1";condition_c23 = "4";condition_s23="3";
			condition_b24 = "jr2_2";condition_c24 = "1";condition_s24="2";
			condition_b25 = "jr2_2";condition_c25 = "2";condition_s25="1";
			condition_b26 = "jr2_2";condition_c26 = "2";condition_s26="2";
			condition_b27 = "jr2_2";condition_c27 = "3";condition_s27="1";
			condition_b28 = "jr2_2";condition_c28 = "3";condition_s28="2";
			condition_b29 = "jr2_2";condition_c29 = "3";condition_s29="3";
			condition_b30 = "jr2_2";condition_c30 = "4";condition_s30="1";
			condition_b31 = "jr2_2";condition_c31 = "2";condition_s31="3";
			condition_b32 = "jr2_2";condition_c32 = "4";condition_s32="1";
			condition_b33 = "jr2_2";condition_c33 = "4";condition_s33="4";
			condition_b34 = "jr3_1";condition_c34 = "2";condition_s34="2";
			condition_b35 = "jr3_1";condition_c35 = "3";condition_s35="1";
			condition_b36 = "jr3_1";condition_c36 = "1";condition_s36="2";
			condition_b37 = "jr3_1";condition_c37 = "1";condition_s37="3";
			condition_b38 = "jr3_1";condition_c38 = "3";condition_s38="2";
		}
		else if(sim_book.equals("range_1_6")&&sim_ver.equals("naer")) {
			condition_b1 = "jr1_1";condition_c1 = "1";condition_s1="1";
			condition_b2 = "jr1_2";condition_c2 = "2";condition_s2="3";
			condition_b3 = "jr1_2";condition_c3 = "3";condition_s3="4";
			condition_b4 = "jr1_1";condition_c4 = "1";condition_s4="4";
			condition_b5 = "jr1_1";condition_c5 = "2";condition_s5="1";
			condition_b6 = "jr1_1";condition_c6 = "2";condition_s6="2";
			condition_b7 = "jr1_1";condition_c7 = "2";condition_s7="4";
			condition_b8 = "jr1_1";condition_c8 = "2";condition_s8="5";
			condition_b9 = "jr1_1";condition_c9 = "3";condition_s9="1";
			condition_b10 = "jr1_1";condition_c10 = "3";condition_s10="2";
			condition_b11 = "jr1_1";condition_c11 = "3";condition_s11="3";
			condition_b12 = "jr1_2";condition_c12 = "1";condition_s12="1";
			condition_b13 = "jr1_2";condition_c13 = "1";condition_s13="2";
			condition_b14 = "jr1_2";condition_c14 = "1";condition_s14="3";
			condition_b15 = "jr1_2";condition_c15 = "4";condition_s15="1";
			condition_b16 = "jr1_2";condition_c16 = "4";condition_s16="2";
			condition_b17 = "jr2_1";condition_c17 = "1";condition_s17="1";
			condition_b18 = "jr2_1";condition_c18 = "1";condition_s18="3";
			condition_b19 = "jr2_1";condition_c19 = "2";condition_s19="1";
			condition_b20 = "jr2_1";condition_c20 = "2";condition_s20="2";
			condition_b21 = "jr2_1";condition_c21 = "2";condition_s21="3";
			condition_b22 = "jr2_1";condition_c22 = "3";condition_s22="2";
			condition_b23 = "jr2_1";condition_c23 = "4";condition_s23="3";
			condition_b24 = "jr2_2";condition_c24 = "1";condition_s24="2";
			condition_b25 = "jr2_2";condition_c25 = "2";condition_s25="1";
			condition_b26 = "jr2_2";condition_c26 = "2";condition_s26="2";
			condition_b27 = "jr2_2";condition_c27 = "2";condition_s27="3";
			condition_b28 = "jr2_2";condition_c28 = "3";condition_s28="1";
			condition_b29 = "jr2_2";condition_c29 = "3";condition_s29="2";
			condition_b30 = "jr2_2";condition_c30 = "3";condition_s30="3";
			condition_b31 = "jr2_2";condition_c31 = "4";condition_s31="1";
			condition_b32 = "jr2_2";condition_c32 = "4";condition_s32="3";
			condition_b33 = "jr2_2";condition_c33 = "4";condition_s33="4";
			condition_b34 = "jr3_1";condition_c34 = "2";condition_s34="3";
			condition_b35 = "jr3_1";condition_c35 = "2";condition_s35="4";
			condition_b36 = "jr3_1";condition_c36 = "3";condition_s36="1";
			condition_b37 = "jr3_1";condition_c37 = "1";condition_s37="2";
			condition_b38 = "jr3_1";condition_c38 = "1";condition_s38="3";
			condition_b39 = "jr3_1";condition_c39 = "3";condition_s39="2";
			condition_b40 = "jr3_2";condition_c40 = "1";condition_s40="1";
			condition_b41 = "jr3_2";condition_c41 = "1";condition_s41="2";
			condition_b42 = "jr3_2";condition_c42 = "1";condition_s42="3";
		}
		Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	if(sim_book.equals("range_1_2")&&sim_ver.equals("ni")||sim_book.equals("range_1_2")&&sim_ver.equals("hanlin")||sim_book.equals("range_1_2")&&sim_ver.equals("knsh")) {
        		//1~2南一或瀚林或康軒
        		stmt = conn.prepareStatement(
        			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 6)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 7)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 7)"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ) "
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ) ORDER BY RAND() limit 7) as group1 )");
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
            	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);/*第五個?是模擬考的冊別*/stmt.setNString(6,sim_ver);//第六個?是模擬考的版本
            	stmt.setNString(7,condition_c2);/*第七個?是模擬考的章別*/stmt.setNString(8,condition_s2);//第八個?是模擬考的節別
        		stmt.setNString(9,condition_b3);/*第九個?是模擬考的冊別*/stmt.setNString(10,sim_ver);//第十個?是模擬考的版本
            	stmt.setNString(11,condition_c3);/*第十一個?是模擬考的章別*/stmt.setNString(12,condition_s3);//第十二個?是模擬考的節別
            	stmt.setNString(13,condition_b4);/*第十三個?是模擬考的冊別*/stmt.setNString(14,sim_ver);//第十四個?是模擬考的版本
            	stmt.setNString(15,condition_c4);/*第十五個?是模擬考的章別*/stmt.setNString(16,condition_s4);//第十六個?是模擬考的節別
            	stmt.setNString(17,condition_b5);/*第十七個?是模擬考的冊別*/stmt.setNString(18,sim_ver);//第十八個?是模擬考的版本
            	stmt.setNString(19,condition_c5);/*第十九個?是模擬考的章別*/stmt.setNString(20,condition_s5);//第二十個?是模擬考的節別
            	stmt.setNString(21,condition_b6);/*第二一個?是模擬考的冊別*/stmt.setNString(22,sim_ver);//第二二個?是模擬考的版本
            	stmt.setNString(23,condition_c6);/*第二三個?是模擬考的章別*/stmt.setNString(24,condition_s6);//第二四個?是模擬考的節別
            	stmt.setNString(25,condition_b7);/*第二五個?是模擬考的冊別*/stmt.setNString(26,sim_ver);//第二六個?是模擬考的版本
            	stmt.setNString(27,condition_c7);/*第二七個?是模擬考的章別*/stmt.setNString(28,condition_s7);//第二八個?是模擬考的節別
            	stmt.setNString(29,condition_b8);/*第二九個?是模擬考的冊別*/stmt.setNString(30,sim_ver);//第三十個?是模擬考的版本
            	stmt.setNString(31,condition_c8);/*第三一個?是模擬考的章別*/stmt.setNString(32,condition_s8);//第三二個?是模擬考的節別
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
        	}
        	else if(sim_book.equals("range_3_4")&&sim_ver.equals("ni")||sim_book.equals("range_3_4")&&sim_ver.equals("hanlin")||sim_book.equals("range_3_4")&&sim_ver.equals("knsh")) {
        		//3~4南一或瀚林或康軒
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)");
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
        		stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);stmt.setNString(6,sim_ver);stmt.setNString(7,condition_c2);stmt.setNString(8,condition_s2);
        		stmt.setNString(9,condition_b3);stmt.setNString(10,sim_ver);stmt.setNString(11,condition_c3);stmt.setNString(12,condition_s3);
            	stmt.setNString(13,condition_b4);stmt.setNString(14,sim_ver);stmt.setNString(15,condition_c4);stmt.setNString(16,condition_s4);
            	stmt.setNString(17,condition_b5);stmt.setNString(18,sim_ver);stmt.setNString(19,condition_c5);stmt.setNString(20,condition_s5);
            	stmt.setNString(21,condition_b6);stmt.setNString(22,sim_ver);stmt.setNString(23,condition_c6);stmt.setNString(24,condition_s6);
            	stmt.setNString(25,condition_b7);stmt.setNString(26,sim_ver);stmt.setNString(27,condition_c7);stmt.setNString(28,condition_s7);
            	stmt.setNString(29,condition_b8);stmt.setNString(30,sim_ver);stmt.setNString(31,condition_c8);stmt.setNString(32,condition_s8);
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
            	stmt.setNString(65,condition_b17);stmt.setNString(66,sim_ver);stmt.setNString(67,condition_c17);stmt.setNString(68,condition_s17);
            	stmt.setNString(69,condition_b18);stmt.setNString(70,sim_ver);stmt.setNString(71,condition_c18);stmt.setNString(72,condition_s18);
            	stmt.setNString(73,condition_b19);stmt.setNString(74,sim_ver);stmt.setNString(75,condition_c19);stmt.setNString(76,condition_s19);
        	}
        	else if(sim_book.equals("range_1_3")&&sim_ver.equals("ni")||sim_book.equals("range_1_3")&&sim_ver.equals("hanlin")||sim_book.equals("range_1_3")&&sim_ver.equals("knsh")) {
        		//1~3南一或瀚林或康軒
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ) ORDER BY RAND() limit 3) as group1 )"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ) ORDER BY RAND() limit 3) as group2 )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)");
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
        		stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);stmt.setNString(6,sim_ver);stmt.setNString(7,condition_c2);stmt.setNString(8,condition_s2);
        		stmt.setNString(9,condition_b3);stmt.setNString(10,sim_ver);stmt.setNString(11,condition_c3);stmt.setNString(12,condition_s3);
            	stmt.setNString(13,condition_b4);stmt.setNString(14,sim_ver);stmt.setNString(15,condition_c4);stmt.setNString(16,condition_s4);
            	stmt.setNString(17,condition_b5);stmt.setNString(18,sim_ver);stmt.setNString(19,condition_c5);stmt.setNString(20,condition_s5);
            	stmt.setNString(21,condition_b6);stmt.setNString(22,sim_ver);stmt.setNString(23,condition_c6);stmt.setNString(24,condition_s6);
            	stmt.setNString(25,condition_b7);stmt.setNString(26,sim_ver);stmt.setNString(27,condition_c7);stmt.setNString(28,condition_s7);
            	stmt.setNString(29,condition_b8);stmt.setNString(30,sim_ver);stmt.setNString(31,condition_c8);stmt.setNString(32,condition_s8);
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
            	stmt.setNString(65,condition_b17);stmt.setNString(66,sim_ver);stmt.setNString(67,condition_c17);stmt.setNString(68,condition_s17);
            	stmt.setNString(69,condition_b18);stmt.setNString(70,sim_ver);stmt.setNString(71,condition_c18);stmt.setNString(72,condition_s18);
            	stmt.setNString(73,condition_b19);stmt.setNString(74,sim_ver);stmt.setNString(75,condition_c19);stmt.setNString(76,condition_s19);
            	stmt.setNString(77,condition_b20);stmt.setNString(78,sim_ver);stmt.setNString(79,condition_c20);stmt.setNString(80,condition_s20);
            	stmt.setNString(81,condition_b21);stmt.setNString(82,sim_ver);stmt.setNString(83,condition_c21);stmt.setNString(84,condition_s21);
            	stmt.setNString(85,condition_b22);stmt.setNString(86,sim_ver);stmt.setNString(87,condition_c22);stmt.setNString(88,condition_s22);
            	stmt.setNString(89,condition_b23);stmt.setNString(90,sim_ver);stmt.setNString(91,condition_c23);stmt.setNString(92,condition_s23);
        	}
        	else if(sim_book.equals("range_1_4")&&sim_ver.equals("ni")||sim_book.equals("range_1_4")&&sim_ver.equals("hanlin")||sim_book.equals("range_1_4")&&sim_ver.equals("knsh")) {
        		//1~4南一或瀚林或康軒
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ) ORDER BY RAND() limit 1) as group1 )"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ) ORDER BY RAND() limit 1) as group2 )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 2) as group3 )");
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
        		stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);stmt.setNString(6,sim_ver);stmt.setNString(7,condition_c2);stmt.setNString(8,condition_s2);
        		stmt.setNString(9,condition_b3);stmt.setNString(10,sim_ver);stmt.setNString(11,condition_c3);stmt.setNString(12,condition_s3);
            	stmt.setNString(13,condition_b4);stmt.setNString(14,sim_ver);stmt.setNString(15,condition_c4);stmt.setNString(16,condition_s4);
            	stmt.setNString(17,condition_b5);stmt.setNString(18,sim_ver);stmt.setNString(19,condition_c5);stmt.setNString(20,condition_s5);
            	stmt.setNString(21,condition_b6);stmt.setNString(22,sim_ver);stmt.setNString(23,condition_c6);stmt.setNString(24,condition_s6);
            	stmt.setNString(25,condition_b7);stmt.setNString(26,sim_ver);stmt.setNString(27,condition_c7);stmt.setNString(28,condition_s7);
            	stmt.setNString(29,condition_b8);stmt.setNString(30,sim_ver);stmt.setNString(31,condition_c8);stmt.setNString(32,condition_s8);
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
            	stmt.setNString(65,condition_b17);stmt.setNString(66,sim_ver);stmt.setNString(67,condition_c17);stmt.setNString(68,condition_s17);
            	stmt.setNString(69,condition_b18);stmt.setNString(70,sim_ver);stmt.setNString(71,condition_c18);stmt.setNString(72,condition_s18);
            	stmt.setNString(73,condition_b19);stmt.setNString(74,sim_ver);stmt.setNString(75,condition_c19);stmt.setNString(76,condition_s19);
            	stmt.setNString(77,condition_b20);stmt.setNString(78,sim_ver);stmt.setNString(79,condition_c20);stmt.setNString(80,condition_s20);
            	stmt.setNString(81,condition_b21);stmt.setNString(82,sim_ver);stmt.setNString(83,condition_c21);stmt.setNString(84,condition_s21);
            	stmt.setNString(85,condition_b22);stmt.setNString(86,sim_ver);stmt.setNString(87,condition_c22);stmt.setNString(88,condition_s22);
            	stmt.setNString(89,condition_b23);stmt.setNString(90,sim_ver);stmt.setNString(91,condition_c23);stmt.setNString(92,condition_s23);
            	stmt.setNString(93,condition_b24);stmt.setNString(94,sim_ver);stmt.setNString(95,condition_c24);stmt.setNString(96,condition_s24);
            	stmt.setNString(97,condition_b25);stmt.setNString(98,sim_ver);stmt.setNString(99,condition_c25);stmt.setNString(100,condition_s25);
            	stmt.setNString(101,condition_b26);stmt.setNString(102,sim_ver);stmt.setNString(103,condition_c26);stmt.setNString(104,condition_s26);
            	stmt.setNString(105,condition_b27);stmt.setNString(106,sim_ver);stmt.setNString(107,condition_c27);stmt.setNString(108,condition_s27);
            	stmt.setNString(109,condition_b28);stmt.setNString(110,sim_ver);stmt.setNString(111,condition_c28);stmt.setNString(112,condition_s28);
            	stmt.setNString(113,condition_b29);stmt.setNString(114,sim_ver);stmt.setNString(115,condition_c29);stmt.setNString(116,condition_s29);
            	stmt.setNString(117,condition_b30);stmt.setNString(118,sim_ver);stmt.setNString(119,condition_c30);stmt.setNString(120,condition_s30);
            	stmt.setNString(121,condition_b31);stmt.setNString(122,sim_ver);stmt.setNString(123,condition_c31);stmt.setNString(124,condition_s31);
            	stmt.setNString(125,condition_b32);stmt.setNString(126,sim_ver);stmt.setNString(127,condition_c32);stmt.setNString(128,condition_s32);
            	stmt.setNString(129,condition_b33);stmt.setNString(130,sim_ver);stmt.setNString(131,condition_c33);stmt.setNString(132,condition_s33);
        	}
        	else if(sim_book.equals("range_1_5")&&sim_ver.equals("ni")||sim_book.equals("range_1_5")&&sim_ver.equals("hanlin")||sim_book.equals("range_1_5")&&sim_ver.equals("knsh")) {
        		//1~5南一或瀚林或康軒
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ) ORDER BY RAND() limit 1) as group1 )"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ) ORDER BY RAND() limit 1) as group2 )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group3 )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 6)"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 2) as group4 )");
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
        		stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);stmt.setNString(6,sim_ver);stmt.setNString(7,condition_c2);stmt.setNString(8,condition_s2);
        		stmt.setNString(9,condition_b3);stmt.setNString(10,sim_ver);stmt.setNString(11,condition_c3);stmt.setNString(12,condition_s3);
            	stmt.setNString(13,condition_b4);stmt.setNString(14,sim_ver);stmt.setNString(15,condition_c4);stmt.setNString(16,condition_s4);
            	stmt.setNString(17,condition_b5);stmt.setNString(18,sim_ver);stmt.setNString(19,condition_c5);stmt.setNString(20,condition_s5);
            	stmt.setNString(21,condition_b6);stmt.setNString(22,sim_ver);stmt.setNString(23,condition_c6);stmt.setNString(24,condition_s6);
            	stmt.setNString(25,condition_b7);stmt.setNString(26,sim_ver);stmt.setNString(27,condition_c7);stmt.setNString(28,condition_s7);
            	stmt.setNString(29,condition_b8);stmt.setNString(30,sim_ver);stmt.setNString(31,condition_c8);stmt.setNString(32,condition_s8);
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
            	stmt.setNString(65,condition_b17);stmt.setNString(66,sim_ver);stmt.setNString(67,condition_c17);stmt.setNString(68,condition_s17);
            	stmt.setNString(69,condition_b18);stmt.setNString(70,sim_ver);stmt.setNString(71,condition_c18);stmt.setNString(72,condition_s18);
            	stmt.setNString(73,condition_b19);stmt.setNString(74,sim_ver);stmt.setNString(75,condition_c19);stmt.setNString(76,condition_s19);
            	stmt.setNString(77,condition_b20);stmt.setNString(78,sim_ver);stmt.setNString(79,condition_c20);stmt.setNString(80,condition_s20);
            	stmt.setNString(81,condition_b21);stmt.setNString(82,sim_ver);stmt.setNString(83,condition_c21);stmt.setNString(84,condition_s21);
            	stmt.setNString(85,condition_b22);stmt.setNString(86,sim_ver);stmt.setNString(87,condition_c22);stmt.setNString(88,condition_s22);
            	stmt.setNString(89,condition_b23);stmt.setNString(90,sim_ver);stmt.setNString(91,condition_c23);stmt.setNString(92,condition_s23);
            	stmt.setNString(93,condition_b24);stmt.setNString(94,sim_ver);stmt.setNString(95,condition_c24);stmt.setNString(96,condition_s24);
            	stmt.setNString(97,condition_b25);stmt.setNString(98,sim_ver);stmt.setNString(99,condition_c25);stmt.setNString(100,condition_s25);
            	stmt.setNString(101,condition_b26);stmt.setNString(102,sim_ver);stmt.setNString(103,condition_c26);stmt.setNString(104,condition_s26);
            	stmt.setNString(105,condition_b27);stmt.setNString(106,sim_ver);stmt.setNString(107,condition_c27);stmt.setNString(108,condition_s27);
            	stmt.setNString(109,condition_b28);stmt.setNString(110,sim_ver);stmt.setNString(111,condition_c28);stmt.setNString(112,condition_s28);
            	stmt.setNString(113,condition_b29);stmt.setNString(114,sim_ver);stmt.setNString(115,condition_c29);stmt.setNString(116,condition_s29);
            	stmt.setNString(117,condition_b30);stmt.setNString(118,sim_ver);stmt.setNString(119,condition_c30);stmt.setNString(120,condition_s30);
            	stmt.setNString(121,condition_b31);stmt.setNString(122,sim_ver);stmt.setNString(123,condition_c31);stmt.setNString(124,condition_s31);
            	stmt.setNString(125,condition_b32);stmt.setNString(126,sim_ver);stmt.setNString(127,condition_c32);stmt.setNString(128,condition_s32);
            	stmt.setNString(129,condition_b33);stmt.setNString(130,sim_ver);stmt.setNString(131,condition_c33);stmt.setNString(132,condition_s33);
            	stmt.setNString(133,condition_b34);stmt.setNString(134,sim_ver);stmt.setNString(135,condition_c34);stmt.setNString(136,condition_s34);
            	stmt.setNString(137,condition_b35);stmt.setNString(138,sim_ver);stmt.setNString(139,condition_c35);stmt.setNString(140,condition_s35);
            	stmt.setNString(141,condition_b36);stmt.setNString(142,sim_ver);stmt.setNString(143,condition_c36);stmt.setNString(144,condition_s36);
            	stmt.setNString(145,condition_b37);stmt.setNString(146,sim_ver);stmt.setNString(147,condition_c37);stmt.setNString(148,condition_s37);
            	stmt.setNString(149,condition_b38);stmt.setNString(150,sim_ver);stmt.setNString(151,condition_c38);stmt.setNString(152,condition_s38);
        	}
        	else if(sim_book.equals("range_1_6")&&sim_ver.equals("ni")||sim_book.equals("range_1_6")&&sim_ver.equals("hanlin")||sim_book.equals("range_1_6")&&sim_ver.equals("knsh")) {
        		//1~6南一或瀚林或康軒
        		stmt = conn.prepareStatement(
            			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ) ORDER BY RAND() limit 1) as group1 )"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ) ORDER BY RAND() limit 1) as group2 )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group3 )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group4 )"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group5 )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
                		+ "UNION ALL"
                		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group6 )"
                		+ "UNION ALL"
                		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)");
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
        		stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);stmt.setNString(6,sim_ver);stmt.setNString(7,condition_c2);stmt.setNString(8,condition_s2);
        		stmt.setNString(9,condition_b3);stmt.setNString(10,sim_ver);stmt.setNString(11,condition_c3);stmt.setNString(12,condition_s3);
            	stmt.setNString(13,condition_b4);stmt.setNString(14,sim_ver);stmt.setNString(15,condition_c4);stmt.setNString(16,condition_s4);
            	stmt.setNString(17,condition_b5);stmt.setNString(18,sim_ver);stmt.setNString(19,condition_c5);stmt.setNString(20,condition_s5);
            	stmt.setNString(21,condition_b6);stmt.setNString(22,sim_ver);stmt.setNString(23,condition_c6);stmt.setNString(24,condition_s6);
            	stmt.setNString(25,condition_b7);stmt.setNString(26,sim_ver);stmt.setNString(27,condition_c7);stmt.setNString(28,condition_s7);
            	stmt.setNString(29,condition_b8);stmt.setNString(30,sim_ver);stmt.setNString(31,condition_c8);stmt.setNString(32,condition_s8);
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
            	stmt.setNString(65,condition_b17);stmt.setNString(66,sim_ver);stmt.setNString(67,condition_c17);stmt.setNString(68,condition_s17);
            	stmt.setNString(69,condition_b18);stmt.setNString(70,sim_ver);stmt.setNString(71,condition_c18);stmt.setNString(72,condition_s18);
            	stmt.setNString(73,condition_b19);stmt.setNString(74,sim_ver);stmt.setNString(75,condition_c19);stmt.setNString(76,condition_s19);
            	stmt.setNString(77,condition_b20);stmt.setNString(78,sim_ver);stmt.setNString(79,condition_c20);stmt.setNString(80,condition_s20);
            	stmt.setNString(81,condition_b21);stmt.setNString(82,sim_ver);stmt.setNString(83,condition_c21);stmt.setNString(84,condition_s21);
            	stmt.setNString(85,condition_b22);stmt.setNString(86,sim_ver);stmt.setNString(87,condition_c22);stmt.setNString(88,condition_s22);
            	stmt.setNString(89,condition_b23);stmt.setNString(90,sim_ver);stmt.setNString(91,condition_c23);stmt.setNString(92,condition_s23);
            	stmt.setNString(93,condition_b24);stmt.setNString(94,sim_ver);stmt.setNString(95,condition_c24);stmt.setNString(96,condition_s24);
            	stmt.setNString(97,condition_b25);stmt.setNString(98,sim_ver);stmt.setNString(99,condition_c25);stmt.setNString(100,condition_s25);
            	stmt.setNString(101,condition_b26);stmt.setNString(102,sim_ver);stmt.setNString(103,condition_c26);stmt.setNString(104,condition_s26);
            	stmt.setNString(105,condition_b27);stmt.setNString(106,sim_ver);stmt.setNString(107,condition_c27);stmt.setNString(108,condition_s27);
            	stmt.setNString(109,condition_b28);stmt.setNString(110,sim_ver);stmt.setNString(111,condition_c28);stmt.setNString(112,condition_s28);
            	stmt.setNString(113,condition_b29);stmt.setNString(114,sim_ver);stmt.setNString(115,condition_c29);stmt.setNString(116,condition_s29);
            	stmt.setNString(117,condition_b30);stmt.setNString(118,sim_ver);stmt.setNString(119,condition_c30);stmt.setNString(120,condition_s30);
            	stmt.setNString(121,condition_b31);stmt.setNString(122,sim_ver);stmt.setNString(123,condition_c31);stmt.setNString(124,condition_s31);
            	stmt.setNString(125,condition_b32);stmt.setNString(126,sim_ver);stmt.setNString(127,condition_c32);stmt.setNString(128,condition_s32);
            	stmt.setNString(129,condition_b33);stmt.setNString(130,sim_ver);stmt.setNString(131,condition_c33);stmt.setNString(132,condition_s33);
            	stmt.setNString(133,condition_b34);stmt.setNString(134,sim_ver);stmt.setNString(135,condition_c34);stmt.setNString(136,condition_s34);
            	stmt.setNString(137,condition_b35);stmt.setNString(138,sim_ver);stmt.setNString(139,condition_c35);stmt.setNString(140,condition_s35);
            	stmt.setNString(141,condition_b36);stmt.setNString(142,sim_ver);stmt.setNString(143,condition_c36);stmt.setNString(144,condition_s36);
            	stmt.setNString(145,condition_b37);stmt.setNString(146,sim_ver);stmt.setNString(147,condition_c37);stmt.setNString(148,condition_s37);
            	stmt.setNString(149,condition_b38);stmt.setNString(150,sim_ver);stmt.setNString(151,condition_c38);stmt.setNString(152,condition_s38);
            	stmt.setNString(153,condition_b39);stmt.setNString(154,sim_ver);stmt.setNString(155,condition_c39);stmt.setNString(156,condition_s39);
            	stmt.setNString(157,condition_b40);stmt.setNString(158,sim_ver);stmt.setNString(159,condition_c40);stmt.setNString(160,condition_s40);
            	stmt.setNString(161,condition_b41);stmt.setNString(162,sim_ver);stmt.setNString(163,condition_c41);stmt.setNString(164,condition_s41);
            	stmt.setNString(165,condition_b42);stmt.setNString(166,sim_ver);stmt.setNString(167,condition_c42);stmt.setNString(168,condition_s42);
            	stmt.setNString(169,condition_b43);stmt.setNString(170,sim_ver);stmt.setNString(171,condition_c43);stmt.setNString(172,condition_s43);
            	stmt.setNString(173,condition_b44);stmt.setNString(174,sim_ver);stmt.setNString(175,condition_c44);stmt.setNString(176,condition_s44);
            	stmt.setNString(177,condition_b45);stmt.setNString(178,sim_ver);stmt.setNString(179,condition_c45);stmt.setNString(180,condition_s45);
            	stmt.setNString(181,condition_b46);stmt.setNString(182,sim_ver);stmt.setNString(183,condition_c46);stmt.setNString(184,condition_s46);
            	stmt.setNString(185,condition_b47);stmt.setNString(186,sim_ver);stmt.setNString(187,condition_c47);stmt.setNString(188,condition_s47);
            	stmt.setNString(189,condition_b48);stmt.setNString(190,sim_ver);stmt.setNString(191,condition_c48);stmt.setNString(192,condition_s48);
            	stmt.setNString(193,condition_b49);stmt.setNString(194,sim_ver);stmt.setNString(195,condition_c49);stmt.setNString(196,condition_s49);
        	}
        	else if(sim_book.equals("range_1_2")&&sim_ver.equals("naer")) {
        		//1~2部編版
        		stmt = conn.prepareStatement(
        			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2) "
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 3) as group1 )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)" );
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
            	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);stmt.setNString(6,sim_ver);stmt.setNString(7,condition_c2);stmt.setNString(8,condition_s2);
        		stmt.setNString(9,condition_b3);stmt.setNString(10,sim_ver);stmt.setNString(11,condition_c3);stmt.setNString(12,condition_s3);
            	stmt.setNString(13,condition_b4);stmt.setNString(14,sim_ver);stmt.setNString(15,condition_c4);stmt.setNString(16,condition_s4);
            	stmt.setNString(17,condition_b5);stmt.setNString(18,sim_ver);stmt.setNString(19,condition_c5);stmt.setNString(20,condition_s5);
            	stmt.setNString(21,condition_b6);stmt.setNString(22,sim_ver);stmt.setNString(23,condition_c6);stmt.setNString(24,condition_s6);
            	stmt.setNString(25,condition_b7);stmt.setNString(26,sim_ver);stmt.setNString(27,condition_c7);stmt.setNString(28,condition_s7);
            	stmt.setNString(29,condition_b8);stmt.setNString(30,sim_ver);stmt.setNString(31,condition_c8);stmt.setNString(32,condition_s8);
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
        	}
        	else if(sim_book.equals("range_3_4")&&sim_ver.equals("naer")) {
        		//3~4部編版
        		stmt = conn.prepareStatement(
        			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1) "
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group1 )");
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
            	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);stmt.setNString(6,sim_ver);stmt.setNString(7,condition_c2);stmt.setNString(8,condition_s2);
        		stmt.setNString(9,condition_b3);stmt.setNString(10,sim_ver);stmt.setNString(11,condition_c3);stmt.setNString(12,condition_s3);
            	stmt.setNString(13,condition_b4);stmt.setNString(14,sim_ver);stmt.setNString(15,condition_c4);stmt.setNString(16,condition_s4);
            	stmt.setNString(17,condition_b5);stmt.setNString(18,sim_ver);stmt.setNString(19,condition_c5);stmt.setNString(20,condition_s5);
            	stmt.setNString(21,condition_b6);stmt.setNString(22,sim_ver);stmt.setNString(23,condition_c6);stmt.setNString(24,condition_s6);
            	stmt.setNString(25,condition_b7);stmt.setNString(26,sim_ver);stmt.setNString(27,condition_c7);stmt.setNString(28,condition_s7);
            	stmt.setNString(29,condition_b8);stmt.setNString(30,sim_ver);stmt.setNString(31,condition_c8);stmt.setNString(32,condition_s8);
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
            	stmt.setNString(65,condition_b17);stmt.setNString(66,sim_ver);stmt.setNString(67,condition_c17);stmt.setNString(68,condition_s17);
            	stmt.setNString(69,condition_b18);stmt.setNString(70,sim_ver);stmt.setNString(71,condition_c18);stmt.setNString(72,condition_s18);
        	}
        	else if(sim_book.equals("range_1_3")&&sim_ver.equals("naer")) {
        		//1~3部編版
        		stmt = conn.prepareStatement(
        			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 3)"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 3) as group1 )"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 3) as group2 )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)");
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
            	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);stmt.setNString(6,sim_ver);stmt.setNString(7,condition_c2);stmt.setNString(8,condition_s2);
        		stmt.setNString(9,condition_b3);stmt.setNString(10,sim_ver);stmt.setNString(11,condition_c3);stmt.setNString(12,condition_s3);
            	stmt.setNString(13,condition_b4);stmt.setNString(14,sim_ver);stmt.setNString(15,condition_c4);stmt.setNString(16,condition_s4);
            	stmt.setNString(17,condition_b5);stmt.setNString(18,sim_ver);stmt.setNString(19,condition_c5);stmt.setNString(20,condition_s5);
            	stmt.setNString(21,condition_b6);stmt.setNString(22,sim_ver);stmt.setNString(23,condition_c6);stmt.setNString(24,condition_s6);
            	stmt.setNString(25,condition_b7);stmt.setNString(26,sim_ver);stmt.setNString(27,condition_c7);stmt.setNString(28,condition_s7);
            	stmt.setNString(29,condition_b8);stmt.setNString(30,sim_ver);stmt.setNString(31,condition_c8);stmt.setNString(32,condition_s8);
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
            	stmt.setNString(65,condition_b17);stmt.setNString(66,sim_ver);stmt.setNString(67,condition_c17);stmt.setNString(68,condition_s17);
            	stmt.setNString(69,condition_b18);stmt.setNString(70,sim_ver);stmt.setNString(71,condition_c18);stmt.setNString(72,condition_s18);
            	stmt.setNString(73,condition_b19);stmt.setNString(74,sim_ver);stmt.setNString(75,condition_c19);stmt.setNString(76,condition_s19);
            	stmt.setNString(77,condition_b20);stmt.setNString(78,sim_ver);stmt.setNString(79,condition_c20);stmt.setNString(80,condition_s20);
            	stmt.setNString(81,condition_b21);stmt.setNString(82,sim_ver);stmt.setNString(83,condition_c21);stmt.setNString(84,condition_s21);
            	stmt.setNString(85,condition_b22);stmt.setNString(86,sim_ver);stmt.setNString(87,condition_c22);stmt.setNString(88,condition_s22);
            	stmt.setNString(89,condition_b23);stmt.setNString(90,sim_ver);stmt.setNString(91,condition_c23);stmt.setNString(92,condition_s23);
        	}
        	else if(sim_book.equals("range_1_4")&&sim_ver.equals("naer")) {
        		//1~4部編版
        		stmt = conn.prepareStatement(
        			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group1 )"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group2 )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 2) as group3 )");
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
            	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);stmt.setNString(6,sim_ver);stmt.setNString(7,condition_c2);stmt.setNString(8,condition_s2);
        		stmt.setNString(9,condition_b3);stmt.setNString(10,sim_ver);stmt.setNString(11,condition_c3);stmt.setNString(12,condition_s3);
            	stmt.setNString(13,condition_b4);stmt.setNString(14,sim_ver);stmt.setNString(15,condition_c4);stmt.setNString(16,condition_s4);
            	stmt.setNString(17,condition_b5);stmt.setNString(18,sim_ver);stmt.setNString(19,condition_c5);stmt.setNString(20,condition_s5);
            	stmt.setNString(21,condition_b6);stmt.setNString(22,sim_ver);stmt.setNString(23,condition_c6);stmt.setNString(24,condition_s6);
            	stmt.setNString(25,condition_b7);stmt.setNString(26,sim_ver);stmt.setNString(27,condition_c7);stmt.setNString(28,condition_s7);
            	stmt.setNString(29,condition_b8);stmt.setNString(30,sim_ver);stmt.setNString(31,condition_c8);stmt.setNString(32,condition_s8);
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
            	stmt.setNString(65,condition_b17);stmt.setNString(66,sim_ver);stmt.setNString(67,condition_c17);stmt.setNString(68,condition_s17);
            	stmt.setNString(69,condition_b18);stmt.setNString(70,sim_ver);stmt.setNString(71,condition_c18);stmt.setNString(72,condition_s18);
            	stmt.setNString(73,condition_b19);stmt.setNString(74,sim_ver);stmt.setNString(75,condition_c19);stmt.setNString(76,condition_s19);
            	stmt.setNString(77,condition_b20);stmt.setNString(78,sim_ver);stmt.setNString(79,condition_c20);stmt.setNString(80,condition_s20);
            	stmt.setNString(81,condition_b21);stmt.setNString(82,sim_ver);stmt.setNString(83,condition_c21);stmt.setNString(84,condition_s21);
            	stmt.setNString(85,condition_b22);stmt.setNString(86,sim_ver);stmt.setNString(87,condition_c22);stmt.setNString(88,condition_s22);
            	stmt.setNString(89,condition_b23);stmt.setNString(90,sim_ver);stmt.setNString(91,condition_c23);stmt.setNString(92,condition_s23);
            	stmt.setNString(93,condition_b24);stmt.setNString(94,sim_ver);stmt.setNString(95,condition_c24);stmt.setNString(96,condition_s24);
            	stmt.setNString(97,condition_b25);stmt.setNString(98,sim_ver);stmt.setNString(99,condition_c25);stmt.setNString(100,condition_s25);
            	stmt.setNString(101,condition_b26);stmt.setNString(102,sim_ver);stmt.setNString(103,condition_c26);stmt.setNString(104,condition_s26);
            	stmt.setNString(105,condition_b27);stmt.setNString(106,sim_ver);stmt.setNString(107,condition_c27);stmt.setNString(108,condition_s27);
            	stmt.setNString(109,condition_b28);stmt.setNString(110,sim_ver);stmt.setNString(111,condition_c28);stmt.setNString(112,condition_s28);
            	stmt.setNString(113,condition_b29);stmt.setNString(114,sim_ver);stmt.setNString(115,condition_c29);stmt.setNString(116,condition_s29);
            	stmt.setNString(117,condition_b30);stmt.setNString(118,sim_ver);stmt.setNString(119,condition_c30);stmt.setNString(120,condition_s30);
            	stmt.setNString(121,condition_b31);stmt.setNString(122,sim_ver);stmt.setNString(123,condition_c31);stmt.setNString(124,condition_s31);
            	stmt.setNString(125,condition_b32);stmt.setNString(126,sim_ver);stmt.setNString(127,condition_c32);stmt.setNString(128,condition_s32);
            	stmt.setNString(129,condition_b33);stmt.setNString(130,sim_ver);stmt.setNString(131,condition_c33);stmt.setNString(132,condition_s33);
        	}
        	else if(sim_book.equals("range_1_5")&&sim_ver.equals("naer")) {
        		//1~5部編版
        		stmt = conn.prepareStatement(
        			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group1 )"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group2 )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 4)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 2) as group3 )");
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
            	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);stmt.setNString(6,sim_ver);stmt.setNString(7,condition_c2);stmt.setNString(8,condition_s2);
        		stmt.setNString(9,condition_b3);stmt.setNString(10,sim_ver);stmt.setNString(11,condition_c3);stmt.setNString(12,condition_s3);
            	stmt.setNString(13,condition_b4);stmt.setNString(14,sim_ver);stmt.setNString(15,condition_c4);stmt.setNString(16,condition_s4);
            	stmt.setNString(17,condition_b5);stmt.setNString(18,sim_ver);stmt.setNString(19,condition_c5);stmt.setNString(20,condition_s5);
            	stmt.setNString(21,condition_b6);stmt.setNString(22,sim_ver);stmt.setNString(23,condition_c6);stmt.setNString(24,condition_s6);
            	stmt.setNString(25,condition_b7);stmt.setNString(26,sim_ver);stmt.setNString(27,condition_c7);stmt.setNString(28,condition_s7);
            	stmt.setNString(29,condition_b8);stmt.setNString(30,sim_ver);stmt.setNString(31,condition_c8);stmt.setNString(32,condition_s8);
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
            	stmt.setNString(65,condition_b17);stmt.setNString(66,sim_ver);stmt.setNString(67,condition_c17);stmt.setNString(68,condition_s17);
            	stmt.setNString(69,condition_b18);stmt.setNString(70,sim_ver);stmt.setNString(71,condition_c18);stmt.setNString(72,condition_s18);
            	stmt.setNString(73,condition_b19);stmt.setNString(74,sim_ver);stmt.setNString(75,condition_c19);stmt.setNString(76,condition_s19);
            	stmt.setNString(77,condition_b20);stmt.setNString(78,sim_ver);stmt.setNString(79,condition_c20);stmt.setNString(80,condition_s20);
            	stmt.setNString(81,condition_b21);stmt.setNString(82,sim_ver);stmt.setNString(83,condition_c21);stmt.setNString(84,condition_s21);
            	stmt.setNString(85,condition_b22);stmt.setNString(86,sim_ver);stmt.setNString(87,condition_c22);stmt.setNString(88,condition_s22);
            	stmt.setNString(89,condition_b23);stmt.setNString(90,sim_ver);stmt.setNString(91,condition_c23);stmt.setNString(92,condition_s23);
            	stmt.setNString(93,condition_b24);stmt.setNString(94,sim_ver);stmt.setNString(95,condition_c24);stmt.setNString(96,condition_s24);
            	stmt.setNString(97,condition_b25);stmt.setNString(98,sim_ver);stmt.setNString(99,condition_c25);stmt.setNString(100,condition_s25);
            	stmt.setNString(101,condition_b26);stmt.setNString(102,sim_ver);stmt.setNString(103,condition_c26);stmt.setNString(104,condition_s26);
            	stmt.setNString(105,condition_b27);stmt.setNString(106,sim_ver);stmt.setNString(107,condition_c27);stmt.setNString(108,condition_s27);
            	stmt.setNString(109,condition_b28);stmt.setNString(110,sim_ver);stmt.setNString(111,condition_c28);stmt.setNString(112,condition_s28);
            	stmt.setNString(113,condition_b29);stmt.setNString(114,sim_ver);stmt.setNString(115,condition_c29);stmt.setNString(116,condition_s29);
            	stmt.setNString(117,condition_b30);stmt.setNString(118,sim_ver);stmt.setNString(119,condition_c30);stmt.setNString(120,condition_s30);
            	stmt.setNString(121,condition_b31);stmt.setNString(122,sim_ver);stmt.setNString(123,condition_c31);stmt.setNString(124,condition_s31);
            	stmt.setNString(125,condition_b32);stmt.setNString(126,sim_ver);stmt.setNString(127,condition_c32);stmt.setNString(128,condition_s32);
            	stmt.setNString(129,condition_b33);stmt.setNString(130,sim_ver);stmt.setNString(131,condition_c33);stmt.setNString(132,condition_s33);
            	stmt.setNString(133,condition_b34);stmt.setNString(134,sim_ver);stmt.setNString(135,condition_c34);stmt.setNString(136,condition_s34);
            	stmt.setNString(137,condition_b35);stmt.setNString(138,sim_ver);stmt.setNString(139,condition_c35);stmt.setNString(140,condition_s35);
            	stmt.setNString(141,condition_b36);stmt.setNString(142,sim_ver);stmt.setNString(143,condition_c36);stmt.setNString(144,condition_s36);
            	stmt.setNString(145,condition_b37);stmt.setNString(146,sim_ver);stmt.setNString(147,condition_c37);stmt.setNString(148,condition_s37);
            	stmt.setNString(149,condition_b38);stmt.setNString(150,sim_ver);stmt.setNString(151,condition_c38);stmt.setNString(152,condition_s38);
        	}
        	else if(sim_book.equals("range_1_6")&&sim_ver.equals("naer")) {
        		//1~6部編版
        		stmt = conn.prepareStatement(
        			"(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group1 )"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group2 )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group3 )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 2)"
            		+ "UNION ALL"
            		+ "( select * from ((select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? )  ORDER BY RAND() limit 1) as group4 )"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)"
            		+ "UNION ALL"
            		+ "(select * from next_generation.quiz_base where QB_BOOK = ? and QB_VERSION = ? and QB_CHAPTER =? and QB_SECTION = ? ORDER BY RAND() limit 1)");
        		stmt.setNString(1,condition_b1);/*第一個?是模擬考的冊別*/stmt.setNString(2,sim_ver);//第二個?是模擬考的版本
            	stmt.setNString(3,condition_c1);/*第三個?是模擬考的章別*/stmt.setNString(4,condition_s1);//第四個?是模擬考的節別
        		stmt.setNString(5,condition_b2);stmt.setNString(6,sim_ver);stmt.setNString(7,condition_c2);stmt.setNString(8,condition_s2);
        		stmt.setNString(9,condition_b3);stmt.setNString(10,sim_ver);stmt.setNString(11,condition_c3);stmt.setNString(12,condition_s3);
            	stmt.setNString(13,condition_b4);stmt.setNString(14,sim_ver);stmt.setNString(15,condition_c4);stmt.setNString(16,condition_s4);
            	stmt.setNString(17,condition_b5);stmt.setNString(18,sim_ver);stmt.setNString(19,condition_c5);stmt.setNString(20,condition_s5);
            	stmt.setNString(21,condition_b6);stmt.setNString(22,sim_ver);stmt.setNString(23,condition_c6);stmt.setNString(24,condition_s6);
            	stmt.setNString(25,condition_b7);stmt.setNString(26,sim_ver);stmt.setNString(27,condition_c7);stmt.setNString(28,condition_s7);
            	stmt.setNString(29,condition_b8);stmt.setNString(30,sim_ver);stmt.setNString(31,condition_c8);stmt.setNString(32,condition_s8);
            	stmt.setNString(33,condition_b9);stmt.setNString(34,sim_ver);stmt.setNString(35,condition_c9);stmt.setNString(36,condition_s9);
            	stmt.setNString(37,condition_b10);stmt.setNString(38,sim_ver);stmt.setNString(39,condition_c10);stmt.setNString(40,condition_s10);
            	stmt.setNString(41,condition_b11);stmt.setNString(42,sim_ver);stmt.setNString(43,condition_c11);stmt.setNString(44,condition_s11);
            	stmt.setNString(45,condition_b12);stmt.setNString(46,sim_ver);stmt.setNString(47,condition_c12);stmt.setNString(48,condition_s12);
            	stmt.setNString(49,condition_b13);stmt.setNString(50,sim_ver);stmt.setNString(51,condition_c13);stmt.setNString(52,condition_s13);
            	stmt.setNString(53,condition_b14);stmt.setNString(54,sim_ver);stmt.setNString(55,condition_c14);stmt.setNString(56,condition_s14);
            	stmt.setNString(57,condition_b15);stmt.setNString(58,sim_ver);stmt.setNString(59,condition_c15);stmt.setNString(60,condition_s15);
            	stmt.setNString(61,condition_b16);stmt.setNString(62,sim_ver);stmt.setNString(63,condition_c16);stmt.setNString(64,condition_s16);
            	stmt.setNString(65,condition_b17);stmt.setNString(66,sim_ver);stmt.setNString(67,condition_c17);stmt.setNString(68,condition_s17);
            	stmt.setNString(69,condition_b18);stmt.setNString(70,sim_ver);stmt.setNString(71,condition_c18);stmt.setNString(72,condition_s18);
            	stmt.setNString(73,condition_b19);stmt.setNString(74,sim_ver);stmt.setNString(75,condition_c19);stmt.setNString(76,condition_s19);
            	stmt.setNString(77,condition_b20);stmt.setNString(78,sim_ver);stmt.setNString(79,condition_c20);stmt.setNString(80,condition_s20);
            	stmt.setNString(81,condition_b21);stmt.setNString(82,sim_ver);stmt.setNString(83,condition_c21);stmt.setNString(84,condition_s21);
            	stmt.setNString(85,condition_b22);stmt.setNString(86,sim_ver);stmt.setNString(87,condition_c22);stmt.setNString(88,condition_s22);
            	stmt.setNString(89,condition_b23);stmt.setNString(90,sim_ver);stmt.setNString(91,condition_c23);stmt.setNString(92,condition_s23);
            	stmt.setNString(93,condition_b24);stmt.setNString(94,sim_ver);stmt.setNString(95,condition_c24);stmt.setNString(96,condition_s24);
            	stmt.setNString(97,condition_b25);stmt.setNString(98,sim_ver);stmt.setNString(99,condition_c25);stmt.setNString(100,condition_s25);
            	stmt.setNString(101,condition_b26);stmt.setNString(102,sim_ver);stmt.setNString(103,condition_c26);stmt.setNString(104,condition_s26);
            	stmt.setNString(105,condition_b27);stmt.setNString(106,sim_ver);stmt.setNString(107,condition_c27);stmt.setNString(108,condition_s27);
            	stmt.setNString(109,condition_b28);stmt.setNString(110,sim_ver);stmt.setNString(111,condition_c28);stmt.setNString(112,condition_s28);
            	stmt.setNString(113,condition_b29);stmt.setNString(114,sim_ver);stmt.setNString(115,condition_c29);stmt.setNString(116,condition_s29);
            	stmt.setNString(117,condition_b30);stmt.setNString(118,sim_ver);stmt.setNString(119,condition_c30);stmt.setNString(120,condition_s30);
            	stmt.setNString(121,condition_b31);stmt.setNString(122,sim_ver);stmt.setNString(123,condition_c31);stmt.setNString(124,condition_s31);
            	stmt.setNString(125,condition_b32);stmt.setNString(126,sim_ver);stmt.setNString(127,condition_c32);stmt.setNString(128,condition_s32);
            	stmt.setNString(129,condition_b33);stmt.setNString(130,sim_ver);stmt.setNString(131,condition_c33);stmt.setNString(132,condition_s33);
            	stmt.setNString(133,condition_b34);stmt.setNString(134,sim_ver);stmt.setNString(135,condition_c34);stmt.setNString(136,condition_s34);
            	stmt.setNString(137,condition_b35);stmt.setNString(138,sim_ver);stmt.setNString(139,condition_c35);stmt.setNString(140,condition_s35);
            	stmt.setNString(141,condition_b36);stmt.setNString(142,sim_ver);stmt.setNString(143,condition_c36);stmt.setNString(144,condition_s36);
            	stmt.setNString(145,condition_b37);stmt.setNString(146,sim_ver);stmt.setNString(147,condition_c37);stmt.setNString(148,condition_s37);
            	stmt.setNString(149,condition_b38);stmt.setNString(150,sim_ver);stmt.setNString(151,condition_c38);stmt.setNString(152,condition_s38);
            	stmt.setNString(153,condition_b39);stmt.setNString(154,sim_ver);stmt.setNString(155,condition_c39);stmt.setNString(156,condition_s39);
            	stmt.setNString(157,condition_b40);stmt.setNString(158,sim_ver);stmt.setNString(159,condition_c40);stmt.setNString(160,condition_s40);
            	stmt.setNString(161,condition_b41);stmt.setNString(162,sim_ver);stmt.setNString(163,condition_c41);stmt.setNString(164,condition_s41);
            	stmt.setNString(165,condition_b42);stmt.setNString(166,sim_ver);stmt.setNString(167,condition_c42);stmt.setNString(168,condition_s42);
        	}
        	
        	ResultSet rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	
        	rs.last();//移動到最後一筆
        	int re_lenth = rs.getRow(); //-----------長度等於最後一行row
        	rs.beforeFirst();//將rs轉回最開頭
        	
			String[] all_question = new String[re_lenth];//存放全部題目
				while (rs.next()) {//如果有撈到東西
					int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
					all_question[i] = rs.getString("QB_CODE");		
				}
				String all_question_2 =  Arrays.toString(all_question);
			rs.beforeFirst();//將rs跳轉到最開頭		
			
			String[] all_EXPLAIN = new String[re_lenth];//存放全部詳解
			while (rs.next()) {//如果有撈到東西
				int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
				all_EXPLAIN[i] = rs.getString("QB_EXPLAIN");		
			}
			String all_EXPLAIN_2 =  Arrays.toString(all_EXPLAIN);
			rs.beforeFirst();//將rs跳轉到最開頭
			Exam examsource;				
			while (rs.next()) {
				examsource = new Exam();
				examsource.setQB_BOOK(rs.getString("QB_BOOK"));
		        examsource.setQB_VERSION(rs.getString("QB_VERSION"));
				examsource.setQB_CHAPTER(rs.getString("QB_CHAPTER"));
				examsource.setQB_SECTION(rs.getString("QB_SECTION"));
				examsource.setQB_LEVEL(rs.getString("QB_LEVEL"));
				examsource.setQB_NO(rs.getString("QB_NO"));
				examsource.setQB_CODE(all_question_2);
				examsource.setQB_MAIN(rs.getString("QB_MAIN"));
				examsource.setQB_PIC(rs.getString("QB_PIC"));
				examsource.setQB_ANSWER(rs.getString("QB_ANSWER"));
				examsource.setQB_EXPLAIN(all_EXPLAIN_2);
				examsource.setis_fill(rs.getString("isfill"));
				examsource.setis_pick(rs.getString("ispick"));
				simulationlist.add(examsource);
			}			 
	       	stmt.close();
        	stmt = null;
        	
        }catch (SQLException e) {
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
		return simulationlist;
	}
	//歷屆試題考試
	@Override
	public List<Exam> pastexam(String past_year) {
		List<Exam> pastlist = new ArrayList<Exam>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("select * from next_generation.exam_past where EP_YEAR = ? ORDER BY EP_ORDER ASC");
        	stmt.setNString(1, past_year);//用選擇的年份作查詢條件
        	ResultSet rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	
        	rs.last();//移動到最後一筆
        	int re_lenth = rs.getRow(); //-----------長度等於最後一行row
        	rs.beforeFirst();//將rs轉回最開頭
			String[] all_question = new String[re_lenth];//存放全部題目
				while (rs.next()) {//如果有撈到東西
					int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
					all_question[i] = rs.getString("EP_CODE");
				}
				String all_question_2 =  Arrays.toString(all_question);
			rs.beforeFirst();//將rs跳轉到最開頭
			
			String[] all_EXPLAIN = new String[re_lenth];//存放全部詳解
			while (rs.next()) {//如果有撈到東西
				int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
				all_EXPLAIN[i] = rs.getString("EP_EXPLAIN");		
			}
			String all_EXPLAIN_2 =  Arrays.toString(all_EXPLAIN);
			rs.beforeFirst();//將rs跳轉到最開頭
			
			Exam examsource;				
			while (rs.next()) {
				examsource = new Exam();
				examsource.setQB_BOOK(rs.getString("EP_YEAR"));
				examsource.setQB_NO(rs.getString("EP_NO"));
				examsource.setQB_CODE(all_question_2);
				examsource.setQB_PIC(rs.getString("EP_PIC"));
				examsource.setQB_ANSWER(rs.getString("EP_ANSWER"));
				examsource.setQB_EXPLAIN(all_EXPLAIN_2);
				examsource.setis_fill(rs.getString("isfill"));
				examsource.setis_pick(rs.getString("ispick"));
				pastlist.add(examsource);
			}			 
	       	stmt.close();
        	stmt = null;
        }catch (SQLException e) {
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
		return pastlist;
	}
	//歷屆試題模擬考
	@Override
	public List<Exam> pastexam_r() {
		List<Exam> pastlist = new ArrayList<Exam>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement(
        			"(select * from next_generation.exam_past where EP_ORDER = '1' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '2' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '3' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '4' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '5' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '6' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '7' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '8' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '9' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '10' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '11' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '12' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '13' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '14' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '15' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '16' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '17' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '18' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '19' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '20' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '21' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '22' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '23' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '24' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '25' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '26' ORDER BY RAND() limit 1) UNION ALL "
        			+ "(select * from next_generation.exam_past where EP_ORDER = '27' ORDER BY RAND() limit 1)");
        	//從每個題號中撈一題來
        	ResultSet rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
        	
        	rs.last();//移動到最後一筆
        	int re_lenth = rs.getRow(); //-----------長度等於最後一行row
        	rs.beforeFirst();//將rs轉回最開頭
			String[] all_question = new String[re_lenth];//存放全部題目
				while (rs.next()) {//如果有撈到東西
					int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
					all_question[i] = rs.getString("EP_CODE");
				}
				String all_question_2 =  Arrays.toString(all_question);
			rs.beforeFirst();//將rs跳轉到最開頭		
			String[] all_EXPLAIN = new String[re_lenth];//存放全部詳解
			while (rs.next()) {//如果有撈到東西
				int i = rs.getRow()-1;//getrow從1開始,array從0開始,故-1
				all_EXPLAIN[i] = rs.getString("EP_EXPLAIN");		
			}
			String all_EXPLAIN_2 =  Arrays.toString(all_EXPLAIN);
			rs.beforeFirst();//將rs跳轉到最開頭
			
			Exam examsource;				
			while (rs.next()) {
				examsource = new Exam();
				examsource.setQB_BOOK(rs.getString("EP_YEAR"));
				examsource.setQB_NO(rs.getString("EP_NO"));
				examsource.setQB_CODE(all_question_2);
				examsource.setQB_PIC(rs.getString("EP_PIC"));
				examsource.setQB_ANSWER(rs.getString("EP_ANSWER"));
				examsource.setQB_EXPLAIN(all_EXPLAIN_2);
				examsource.setis_fill(rs.getString("isfill"));
				examsource.setis_pick(rs.getString("ispick"));
				pastlist.add(examsource);
			}			 
	       	stmt.close();
        	stmt = null;
        }catch (SQLException e) {
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
		return pastlist;
	}

}
