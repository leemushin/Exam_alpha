package next.services.queryscores;

import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import java.util.ArrayList;
import next.services.binduser.BinduserConnect;
import java.sql.*;

import exam.service.Exam;
import login.services.Login;
import login.services.LoginService;
import login.services.LoginServiceImpl;

public class ScoredataServiceImpl implements ScoredataService{
	
	String User_Acc;//�ϥΪ̱b��
	//Session s = Sessions.getCurrent();
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();
	private final BinduserConnect ds = BinduserConnect.INSTANCE;
	//---------------------------------------------------
	//�ϥΪ̸ԲӸ�T�d��(�ǥ;A��)
	public List<Scoredata> findAll() {
		List<Scoredata> Scorelist = new ArrayList<Scoredata>();
		try {
			User_Acc = usridentity.getaccount();//���o�n�J�᪺�ϥΪ̱b��session
			//User_Acc = (String) s.getAttribute("usr_account");//���o�n�J�᪺�ϥΪ̱b��session
		    Statement stmt = ds.getStatement();// �P��Ʈw�s��
			ResultSet rs = stmt.executeQuery("select SB_ACCOUNT,SB_QUIZTOTAL,SB_QUIZCORRECT,SB_POINT,SB_QUIZNAME,SB_QUIZIN,SB_QUIZOUT,SB_ANSWERTIME,SB_REMARK,SB_ID,SB_BONUS,SB_QUIZEXPLAIN from next_generation.score_base where SB_ACCOUNT ="+"'"+User_Acc+"'"+"ORDER BY SB_ID DESC");
			// �qdatabase�����X���ƪ���T,����OUser_Acc ORDER BY SB_ID DESC(�q�̷s�C�����,�i�A�[limit����d�ߴX��)
			Scoredata scoresource;
			while (rs.next()) {
				scoresource = new Scoredata();
				scoresource.setSB_ACCOUNT(rs.getString("SB_ACCOUNT"));
				scoresource.setSB_QUIZTOTAL(rs.getString("SB_QUIZTOTAL"));
				scoresource.setSB_QUIZCORRECT(rs.getString("SB_QUIZCORRECT"));
				scoresource.setSB_POINT(rs.getString("SB_POINT"));	
				scoresource.setSB_QUIZNAME(rs.getString("SB_QUIZNAME"));
				scoresource.setSB_QUIZIN(rs.getTimestamp("SB_QUIZIN"));
				scoresource.setSB_QUIZOUT(rs.getTimestamp("SB_QUIZOUT"));
				scoresource.setSB_ANSWERTIME(rs.getString("SB_ANSWERTIME"));
				scoresource.setREMARK(rs.getString("SB_REMARK"));
				scoresource.setSB_ID(rs.getString("SB_ID"));
				scoresource.setSB_QUIZEXPLAIN(rs.getString("SB_QUIZEXPLAIN"));
				scoresource.setSB_BONUS(rs.getString("SB_BONUS"));
				Scorelist.add(scoresource);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ds.close();
		}	
		return Scorelist;
	}	
	//---------------------------------------------------
	//�d�߸ԸѦ^�U
	@Override
	public List<Exam> findRecord(String id) {
		List<Exam> recordlist = new ArrayList<Exam>();
		try {
			String is_explain = null;//�O�_�i�ݸԸ�
			String explain_code = null;//�˸ԸѪ��r��
		    Statement stmt = ds.getStatement();// �P��Ʈw�s��
			ResultSet rs = stmt.executeQuery("select SB_Aryexplain,is_explain from next_generation.score_base where SB_ID ="+id);//�h���ӵ��ҸհO��ID�Ҧs��SB_Aryexplain
			while (rs.next()) {//�p�G������F��
				explain_code = rs.getString("SB_Aryexplain");//��SB_Aryexplain��iexplain_code
				is_explain = rs.getString("is_explain");//��is_explain��iis_explain
			}
			Exam examsource;
			if(is_explain.equals("1")) {//�p�G�i�H�ݸѵ�,�N���X��Ҹյ���
				String explain_code_2 = explain_code.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//�h��explain_code�̪�[]��Ҧ��ť�
				String[] split_code = explain_code_2.split(",");//�γr�����}
				for(int i = 0; i < split_code.length; i++){
					ResultSet rs2 = stmt.executeQuery("select QB_EXPLAIN,QB_ANSWER,QB_NO from next_generation.quiz_base where QB_CODE ="+"'"+split_code[i]+"'");
					//���QB_EXPLAIN,QB_ANSWER.����Osplit_code[i](�����ߤ@��)
						while (rs2.next()) {//�p�G������F��
							examsource = new Exam();
							examsource.setQB_EXPLAIN(rs2.getString("QB_EXPLAIN"));
							examsource.setQB_ANSWER(rs2.getString("QB_ANSWER"));
							examsource.setQB_NO(rs2.getString("QB_NO"));
							recordlist.add(examsource);
					}	
				}	
			}
			else {//�p�G����ݸѵ�,��ܹw�]�Ϥ�
				examsource = new Exam();
				examsource.setQB_EXPLAIN("/Qbase/common/donotexplain.jpg");//�N�ԸѴ������w�]�Ϥ�
				examsource.setQB_NO("None");//�N�D�إN�X������none
				recordlist.add(examsource);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ds.close();
		}	
		return recordlist;
	}
	//---------------------------------------------------
	//�d�߿n��(�Юv�޲z/�ǥͬҾA��)
	@Override
	public List<Scoredata> getpoint(String user_Acc){ 
		String loginmonth;//�n�J���
		String StartMonth;//�Ǵ��}�l���
		String EndMonth;//�Ǵ��������
		String semester_point;//�Ǵ��n��
		String PM_tmp;//��n��
		String usr_name;//�ϥΪ̩m�W
		int theMonth;
		int theYear;
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//�]�w�榡yyyy-MM
		java.text.SimpleDateFormat getmonth = new java.text.SimpleDateFormat("MM");//�]�w�榡MM
		java.text.SimpleDateFormat getyear = new java.text.SimpleDateFormat("yyyy");//�]�w�榡yyyy
		java.util.Date dt = new java.util.Date();//���o�ɶ�
		loginmonth = getyymm.format(dt);//�N�ɶ�����yyyy-MM 
		theMonth = Integer.parseInt(getmonth.format(dt));//���o�Ӥ���H�ΨӧP�_
		theYear = Integer.parseInt(getyear.format(dt));//���o�Ӧ~���H�ΨӧP�_
		
		if(theMonth>=2 && theMonth<8) {							//�p�G�b2~7��n�J
			StartMonth =  (Integer.toString(theYear))+"-02";	//�}�l�����20XX-02
			EndMonth = 	 (Integer.toString(theYear))+"-07";		//���������20XX-07
		}
		else if(theMonth>=8 && theMonth<=12) {					//�p�G�b8~12��n�J
			StartMonth =  (Integer.toString(theYear))+"-08";	//�}�l�����20XX-08
			EndMonth = 	 (Integer.toString(theYear+1))+"-01";	//���������(20XX+1)-01 ��~��
		}
		else {													//�p�G�b1����n�J
			StartMonth =  (Integer.toString(theYear-1))+"-08";	//�}�l�����(20XX-1)-08 ��~��
			EndMonth = 	 (Integer.toString(theYear))+"-01";		//���������20XX-01
		}		
		List<Scoredata> pointlist = new ArrayList<Scoredata>();
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt_semester = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            /**
            //�U���O�Ǵ��n��
            stmt_semester = conn.prepareStatement("SELECT sum(SB_BONUS)+sum(SB_POINT) as s_total  FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?;");
            //���SB_BONUS���[�`�PSB_POINT���[�`�ۥ[��s_total,����OSB_QUIZIN(�i�J���D�ɶ�)�bStartMonth�PEndMonth�����B�ϥΪ̱b����user_Acc
            stmt_semester.setNString(1, StartMonth);//�]�w�Ĥ@��?����StartMonth
            stmt_semester.setNString(2, EndMonth);//�]�w�ĤG��?����EndMonth
            stmt_semester.setNString(3, user_Acc);//�]�w�ĤT��?����user_Acc
            ResultSet semester  = stmt_semester.executeQuery();//����d�ߨñN���G��isemester
            while (semester.next()) {//�p�G���F��
            	semester_point = semester.getString("s_total");//semester_point�Ǵ��n��=s_total
            }**/
            //�U���O��n��
            stmt = conn.prepareStatement("SELECT (select sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as pointmonth,"
            		+ "(select sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as pointsemester,"
            		+ "(select SYS_USERNAME FROM next_generation.sys_data where SYS_ACCOUNT = ?) as usrname");
            //���SB_BONUS���[�`�PSB_POINT���[�`�ۥ[��total,����O�ϥΪ̱b����user_Acc�BSB_QUIZIN(�i�J���D�ɶ�)like loginmonth%
            stmt.setNString(1, user_Acc);//�]�w�Ĥ@��?����user_Acc
            stmt.setNString(2, loginmonth+"%");//�]�w�ĤG��?����loginmonth%
            stmt.setNString(3, StartMonth);//�]�w�ĤT��?����StartMonth
            stmt.setNString(4, EndMonth);//�]�w�ĥ|��?����EndMonth
            stmt.setNString(5, user_Acc);//�]�w�Ĥ���?����user_Acc
            stmt.setNString(6, user_Acc);//�]�w�Ĥ���?����user_Acc
            ResultSet resultSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
            Scoredata pointsource;	
            while (resultSet.next()) {
            	pointsource = new Scoredata();
            	PM_tmp = resultSet.getString("pointmonth");
            	semester_point = resultSet.getString("pointsemester");
            	usr_name = resultSet.getString("usrname");
        		if(PM_tmp == null ||PM_tmp == "" ) {//�p�G�Onull��""���w��0
        			PM_tmp = "0";
        		}
        		if(semester_point == null ||semester_point == "" ) {//�p�G�Onull��""���w��0
        			semester_point = "0";
        		}
        		pointsource.setSB_ACCOUNT(usr_name);//�ϥΪ̩m�W
            	pointsource.setPoint_month(PM_tmp);//��n��
            	pointsource.setPoint_semester(semester_point);//�Ǵ��n��
            	pointlist.add(pointsource);
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
		return pointlist;
    }
	//---------------------------------------------------
	//�Юv�޲z�d�ԲӰT��
	@Override
	public List<Scoredata> getdetail(String Memb_Acc) {
		List<Scoredata> Scorelist = new ArrayList<Scoredata>();
		try {
		    Statement stmt = ds.getStatement();// �P��Ʈw�s��
			ResultSet rs = stmt.executeQuery("select SB_ACCOUNT,SB_QUIZTOTAL,SB_QUIZCORRECT,SB_POINT,SB_QUIZNAME,SB_QUIZIN,SB_QUIZOUT,SB_ANSWERTIME,SB_REMARK,SB_ID,SB_BONUS,SB_QUIZEXPLAIN from next_generation.score_base where SB_ACCOUNT ="+"'"+Memb_Acc+"'"+"ORDER BY SB_ID DESC");
			// �qdatabase�����X���ƪ���T,����OUser_Acc ORDER BY SB_ID DESC(�q�̷s�C�����,�i�A�[limit����d�ߴX��)
			Scoredata scoresource;
			while (rs.next()) {
				scoresource = new Scoredata();
				scoresource.setSB_ACCOUNT(rs.getString("SB_ACCOUNT"));
				scoresource.setSB_QUIZTOTAL(rs.getString("SB_QUIZTOTAL"));
				scoresource.setSB_QUIZCORRECT(rs.getString("SB_QUIZCORRECT"));
				scoresource.setSB_POINT(rs.getString("SB_POINT"));	
				scoresource.setSB_QUIZNAME(rs.getString("SB_QUIZNAME"));
				scoresource.setSB_QUIZIN(rs.getTimestamp("SB_QUIZIN"));
				scoresource.setSB_QUIZOUT(rs.getTimestamp("SB_QUIZOUT"));
				scoresource.setSB_ANSWERTIME(rs.getString("SB_ANSWERTIME"));
				scoresource.setREMARK(rs.getString("SB_REMARK"));
				scoresource.setSB_ID(rs.getString("SB_ID"));
				scoresource.setSB_QUIZEXPLAIN(rs.getString("SB_QUIZEXPLAIN"));
				scoresource.setSB_BONUS(rs.getString("SB_BONUS"));
				Scorelist.add(scoresource);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ds.close();
		}	
		return Scorelist;
	}	

}


