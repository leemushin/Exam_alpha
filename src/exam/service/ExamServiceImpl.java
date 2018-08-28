package exam.service;

import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Arrays;

import next.services.binduser.BinduserConnect;
import java.sql.*;


public class ExamServiceImpl implements ExamService {

	//�s�������D��,���ܼƥh�j�M
	@Override
	public List<Exam> find_chose(String book, String ver, String ch, String se, String lv,String ifteacher,String User_Acc) {
		 List<Exam> Examlist = new ArrayList<Exam>();
	        Connection conn = null;
	        PreparedStatement stmt = null;
	        PreparedStatement updatetimes = null;
	        java.util.Date loginday = new java.util.Date();//���o���Ѥ��
			java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//�]�w�榡yyyy-MM
			String loginm = getyymm.format(loginday);//�N�ɶ��নyyyy-MM
			int examtimes = 0;
		 try {
	        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");//�s����Ʈw
	        	conn = ds.getConnection();
	        	//---�U���O��s�Ҹզ���
	        	updatetimes = conn.prepareStatement("SELECT Monthly_exams FROM next_generation.login_data where LD_ACCOUNT = ? and LD_MONTH like ?");
	        	updatetimes.setNString(1, User_Acc);//�Ĥ@��?���b��
	        	updatetimes.setNString(2, loginm+"%");//�ĤG��?���n�J���
	            ResultSet querytimes = updatetimes.executeQuery();//����d�ߨñN���G��iresultSet
	            while(querytimes.next()) {
	            	examtimes = 1 + querytimes.getInt("Monthly_exams");//�N�Ҹզ���+1
	            }
	            updatetimes.close();
	            updatetimes = null;
	            updatetimes = conn.prepareStatement("update next_generation.login_data set Monthly_exams =? where LD_ACCOUNT = ? and DATE_FORMAT(LD_MONTH,\"%Y-%m\") like ?");
            	//�N�n�J���Ƨ�אּ+1�᪺�Ʀr
	            updatetimes.setInt(1, examtimes);//�Ĥ@��?���n�J����
	            updatetimes.setNString(2, User_Acc);//�ĤG��?���b��
	            updatetimes.setNString(3, loginm+"%");//�ĤT��?���n�J���
	            updatetimes.executeUpdate();
	            updatetimes.close();
	        	//---�W���O��s�Ҹզ���
	        	if(ifteacher.equals("false")) {//�p�G���O�Ѯv,����ü�10�D
		        	stmt = conn.prepareStatement("select QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_CODE,QB_MAIN,QB_PIC,QB_ANSWER,QB_EXPLAIN,isfill,ispick "
		        			+ "from next_generation.quiz_base "
		        			+ " where QB_BOOK =? and QB_VERSION = ? and QB_CHAPTER = ? and QB_SECTION = ? and QB_LEVEL = ? "
		        			+ "ORDER BY RAND()  limit 10");
	        	}
	        	else {//�p�G�O�Ѯv,�̧ǱN�Ҧ��D�ؼ��X
		        	stmt = conn.prepareStatement("select QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_CODE,QB_MAIN,QB_PIC,QB_ANSWER,QB_EXPLAIN,isfill,ispick "
		        			+ "from next_generation.quiz_base "
		        			+ " where QB_BOOK =? and QB_VERSION = ? and QB_CHAPTER = ? and QB_SECTION = ? and QB_LEVEL = ? "
		        			+ "order by QB_ID asc ");
	        	}
	        	stmt.setNString(1, book);//�Ĥ@��?�ΥU�O��j�M����
	        	stmt.setNString(2, ver);//�ĤG��?�Ϊ�����j�M����
	        	stmt.setNString(3, ch);//�ĤT��?�γ��O��j�M����
	        	stmt.setNString(4, se);//�ĥ|��?�θ`�O��j�M����
	        	stmt.setNString(5, lv);//�Ĥ���?�����׷�j�M����
	        	ResultSet rs = stmt.executeQuery();//����d�ߨñN���G��iresultSet
	        	
	        	/** �o�@�q�O�ª��{���ݭn�qzul�ɮ�Ū����Ʃҳ]�p,�{�w���ϥ�
	        	rs.last();//���ʨ�̫�@��
	        	int re_lenth = rs.getRow(); //-----------���׵���̫�@��row
	        	//int re_lenth = 10; //���ץѤW��limit�M�w
	        	rs.beforeFirst();//�Nrs��^�̶}�Y
				String[] all_question = new String[re_lenth];//�s������D��
					while (rs.next()) {//�p�G������F��
						int i = rs.getRow()-1;//getrow�q1�}�l,array�q0�}�l,�G-1
						all_question[i] = rs.getString("QB_CODE");		
					}
					String all_question_2 =  Arrays.toString(all_question);
				rs.beforeFirst();//�Nrs�����̶}�Y		
				
				String[] all_EXPLAIN = new String[re_lenth];//�s������Ը�
				while (rs.next()) {//�p�G������F��
					int i = rs.getRow()-1;//getrow�q1�}�l,array�q0�}�l,�G-1
					all_EXPLAIN[i] = rs.getString("QB_EXPLAIN");//�̧ǱN�Ըѩ�Jall_EXPLAIN�r��
				}
				String all_EXPLAIN_2 =  Arrays.toString(all_EXPLAIN);//�Ҧ��ԸѪ��r��
				rs.beforeFirst();//�Nrs�����̶}�Y
				**/
				Exam examsource;				
				while (rs.next()) {
					examsource = new Exam();
					examsource.setQB_BOOK(rs.getString("QB_BOOK"));
			        examsource.setQB_VERSION(rs.getString("QB_VERSION"));
					examsource.setQB_CHAPTER(rs.getString("QB_CHAPTER"));
					examsource.setQB_SECTION(rs.getString("QB_SECTION"));
					examsource.setQB_LEVEL(rs.getString("QB_LEVEL"));
					examsource.setQB_NO(rs.getString("QB_NO"));
					examsource.setQB_CODE(rs.getString("QB_CODE"));
					//examsource.setQB_CODE(all_question_2);
					examsource.setQB_MAIN(rs.getString("QB_MAIN"));
					examsource.setQB_PIC(rs.getString("QB_PIC"));
					examsource.setQB_ANSWER(rs.getString("QB_ANSWER"));
					examsource.setQB_EXPLAIN(rs.getString("QB_EXPLAIN"));
					//examsource.setQB_EXPLAIN(all_EXPLAIN_2);
					examsource.setis_fill(rs.getString("isfill"));
					examsource.setis_pick(rs.getString("ispick"));
					Examlist.add(examsource);
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
		return Examlist;
	}
	//�j�M�D��--����
	@Override
	public List<Exam> find_chose_A(String book, String ver, String ch, String lv,String ifteacher,String User_Acc) {
		 List<Exam> Examlist = new ArrayList<Exam>();
	        Connection conn = null;
	        PreparedStatement stmt = null;
	        PreparedStatement updatetimes = null;
	        java.util.Date loginday = new java.util.Date();//���o���Ѥ��
			java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//�]�w�榡yyyy-MM
			String loginm = getyymm.format(loginday);//�N�ɶ��নyyyy-MM
			int examtimes = 0;
		 try {
	        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
	        	conn = ds.getConnection();
	        	//---�U���O��s�Ҹզ���
	        	updatetimes = conn.prepareStatement("SELECT Monthly_exams FROM next_generation.login_data where LD_ACCOUNT = ? and LD_MONTH like ?");
	        	updatetimes.setNString(1, User_Acc);//�Ĥ@��?���b��
	        	updatetimes.setNString(2, loginm+"%");//�ĤG��?���n�J���
	            ResultSet querytimes = updatetimes.executeQuery();//����d�ߨñN���G��iresultSet
	            while(querytimes.next()) {
	            	examtimes = 1 + querytimes.getInt("Monthly_exams");//�N�Ҹզ���+1
	            }
	            updatetimes.close();
	            updatetimes = null;
	            updatetimes = conn.prepareStatement("update next_generation.login_data set Monthly_exams =? where LD_ACCOUNT = ? and DATE_FORMAT(LD_MONTH,\"%Y-%m\") like ?");
            	//�N�n�J���Ƨ�אּ+1�᪺�Ʀr
	            updatetimes.setInt(1, examtimes);//�Ĥ@��?���n�J����
	            updatetimes.setNString(2, User_Acc);//�ĤG��?���b��
	            updatetimes.setNString(3, loginm+"%");//�ĤT��?���n�J���
	            updatetimes.executeUpdate();
	            updatetimes.close();
	        	//---�W���O��s�Ҹզ���
	        	if(ifteacher.equals("false")) {//�p�G���O�Ѯv,����ü�10�D
	        	stmt = conn.prepareStatement("select QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_CODE,QB_MAIN,QB_PIC,QB_ANSWER,QB_EXPLAIN,isfill,ispick "
	        			+ "from next_generation.quiz_base "
	        			+ " where QB_BOOK =? and QB_VERSION = ? and QB_CHAPTER = ? and QB_LEVEL = ? "
	        			+ "ORDER BY RAND()  limit 10");
	        	}
	        	else {//�p�G�O�Ѯv,�̧ǱN�Ҧ��D�ؼ��X
		        	stmt = conn.prepareStatement("select QB_BOOK,QB_VERSION,QB_CHAPTER,QB_SECTION,QB_LEVEL,QB_NO,QB_CODE,QB_MAIN,QB_PIC,QB_ANSWER,QB_EXPLAIN,isfill,ispick "
		        			+ "from next_generation.quiz_base "
		        			+ " where QB_BOOK =? and QB_VERSION = ? and QB_CHAPTER = ? and QB_LEVEL = ? "
		        			+ "order by QB_ID asc ");
	        	}
	        	stmt.setNString(1, book);//�Ĥ@��?�ΥU�O��j�M����
	        	stmt.setNString(2, ver);//�ĤG��?�Ϊ�����j�M����
	        	stmt.setNString(3, ch);//�ĤT��?�γ��O��j�M����
	        	stmt.setNString(4, lv);//�ĥ|��?�����קO��j�M����
	        	ResultSet rs = stmt.executeQuery();//����d�ߨñN���G��iresultSet
	        	
	        	/** �o�@�q�O�ª��{���ݭn�qzul�ɮ�Ū����Ʃҳ]�p,�{�w���ϥ�
	        	rs.last();//���ʨ�̫�@��
	        	int re_lenth = rs.getRow(); //-----------���׵���̫�@��row
	        	//int re_lenth = 10; //���ץѤW��limit�M�w
	        	rs.beforeFirst();//�Nrs��^�̶}�Y
				String[] all_question = new String[re_lenth];//�s������D��
					while (rs.next()) {//�p�G������F��
						int i = rs.getRow()-1;//getrow�q1�}�l,array�q0�}�l,�G-1
						all_question[i] = rs.getString("QB_CODE");		
					}
					String all_question_2 =  Arrays.toString(all_question);
				rs.beforeFirst();//�Nrs�����̶}�Y		
				String[] all_EXPLAIN = new String[re_lenth];//�s������Ը�
				while (rs.next()) {//�p�G������F��
					int i = rs.getRow()-1;//getrow�q1�}�l,array�q0�}�l,�G-1
					all_EXPLAIN[i] = rs.getString("QB_EXPLAIN");		
				}
				String all_EXPLAIN_2 =  Arrays.toString(all_EXPLAIN);
				rs.beforeFirst();//�Nrs�����̶}�Y
				**/
				Exam examsource;				
				while (rs.next()) {
					examsource = new Exam();
					examsource.setQB_BOOK(rs.getString("QB_BOOK"));
			        examsource.setQB_VERSION(rs.getString("QB_VERSION"));
					examsource.setQB_CHAPTER(rs.getString("QB_CHAPTER"));
					examsource.setQB_SECTION(rs.getString("QB_SECTION"));
					examsource.setQB_LEVEL(rs.getString("QB_LEVEL"));
					examsource.setQB_NO(rs.getString("QB_NO"));
					examsource.setQB_CODE(rs.getString("QB_CODE"));
					//examsource.setQB_CODE(all_question_2);
					examsource.setQB_MAIN(rs.getString("QB_MAIN"));
					examsource.setQB_PIC(rs.getString("QB_PIC"));
					examsource.setQB_ANSWER(rs.getString("QB_ANSWER"));
					examsource.setQB_EXPLAIN(rs.getString("QB_EXPLAIN"));
					//examsource.setQB_EXPLAIN(all_EXPLAIN_2);
					examsource.setis_fill(rs.getString("isfill"));
					examsource.setis_pick(rs.getString("ispick"));
					Examlist.add(examsource);
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
		return Examlist;
	}
}
