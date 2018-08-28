package next.services.statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class StatisticsServiceImpl implements StatisticsService {
	int isrun = 0;//�O�_���]�L
	int totalscore_m = 0; //�����Z�ť[�`����(���)	
	int totalscore_se = 0; //�����Z�ť[�`����(�Ǵ�)
	int totalmember = 0;	//�����Z�ŤH�ƥ[�`
	int totallogin_m = 0;	//����n�J�H�ƥ[�`
	int totalexam_m = 0;	//����Ҹզ��ƥ[�`
	int totalexam_se = 0;	//�Ǵ��Ҹզ��ƥ[�`
	
	//�p�G���O���~�ťB���O����(���7~9,��@���)
	@Override
	public List<Statistics> getdetail_1(String selectgrade, Date single_m) {
		List<Statistics> getclass =  new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        String singlemonth;//�����
		String StartMonth;//�Ǵ��}�l���
		String EndMonth;//�Ǵ��������
		String classNO_tmp = null;//�Z�ťN���Ȧs�r��
		String classNO_tmp_2 = "";//�Z�ťN���Ȧs�r��
		String membertmp = null;
		int theMonth;
		int theYear;
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//�]�w�榡yyyy-MM
		java.text.SimpleDateFormat getmonth = new java.text.SimpleDateFormat("MM");//�]�w�榡MM
		java.text.SimpleDateFormat getyear = new java.text.SimpleDateFormat("yyyy");//�]�w�榡yyyy
		singlemonth =  getyymm.format(single_m);//�N�ǨӪ��ɶ�����yyyy-MM 
		theMonth = Integer.parseInt(getmonth.format(single_m));//���o�Ӥ���H�ΨӧP�_
		theYear = Integer.parseInt(getyear.format(single_m));//���o�Ӧ~���H�ΨӧP�_
		
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
		//-------------------------------------
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_NO FROM next_generation.class_data where CD_GRADE = ?");//���Z�ťN��
            stmt.setNString(1,selectgrade);//�]�w�Ĥ@��?����selectgrade
            ResultSet classnotSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
            while (classnotSet.next()) {
            	classNO_tmp = classnotSet.getString("CD_NO");
            	classNO_tmp_2 = classNO_tmp_2 + classNO_tmp + ",";
            }
            String classNO_tmp_3 = classNO_tmp_2.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//�h��classNO_tmp_2�̪�[]��Ҧ��ť�
            String[] classno = classNO_tmp_3.split(",");//���o�Z�ťN���}�C
            Statistics cls_Statistics;
            for(int i = 0; i < classno.length; i++) {
            	stmt = conn.prepareStatement("SELECT CD_MEMBER FROM next_generation.class_data where CD_NO = ?");//���Z�Ŧ���
            	stmt.setNString(1, classno[i]);//�]�w�Ĥ@��?����classno[i]
            	ResultSet member_rs = stmt.executeQuery();//����d�ߨñN���G��iresultSet
                int classmonth = 0;//�Z�Ť�n��
                int classsemester = 0;//�Z�žǴ��n��
                int islogin_m =0;//����ϥΤH��
                int exam_m = 0;//����Ҹզ���
                int exam_sem =0 ;//�Ǵ��Ҹզ���
                int marktmp = 0;
                while (member_rs.next()) {
                	membertmp = member_rs.getString("CD_MEMBER");//��CD_MEMBER��imembertmp
                	
                    String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//�h��membertmp�̪�[]��Ҧ��ť�
                    String[] classmember = membertmp_2.split(",");//���o�Z�Ŧ����}�C
                    for(int i2 = 0; i2 < classmember.length; i2++) {
                    	stmt = conn.prepareStatement("SELECT "
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as exam_month,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as Point_month,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as Point_semester,"
                    			+ "(SELECT (CASE when LD_FREQUENCY is not null then '1' ELSE '0' end ) as log_m from next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") = ? and LD_ACCOUNT = ?) as login_m,"
                    			//+ "(SELECT (CASE when LAST_LOGIN like ? then '1' ELSE '0' end ) as log_m from next_generation.sys_data where SYS_ACCOUNT = ?) as login_m,"
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? ) as exam_sem");
                    	//�Ĥ@�Ӽ����Ҹզ���,�ĤG�Ӽ�������,�ĤT�Ӽ��Ǵ�����,�ĥ|�Ӽ����O�_���ϥ�
                    	stmt.setNString(1, classmember[i2]);//�]�w�Ĥ@��?����classmember[i]
                    	stmt.setNString(2, singlemonth+"%");//�]�w�ĤG��?����singlemonth%
                    	stmt.setNString(3, classmember[i2]);//�]�w�ĤT��?����classmember[i]
                    	stmt.setNString(4, singlemonth+"%");//�]�w�ĥ|��?����singlemonth%
                    	stmt.setNString(5, StartMonth);//�]�w�Ĥ���?����StartMonth
                    	stmt.setNString(6, EndMonth);//�]�w�Ĥ���?����EndMonth
                    	stmt.setNString(7, classmember[i2]);//�]�w�ĤC��?����classmember[i]
                    	stmt.setNString(8, singlemonth);//�]�w�ĤK��?����singlemonth%
                    	stmt.setNString(9, classmember[i2]);//�]�w�ĤE��?����classmember[i]
                    	stmt.setNString(10, classmember[i2]);//�]�w�ĤQ��?����classmember[i]
                    	stmt.setNString(11, StartMonth);//�]�w�ĤQ�@��?����StartMonth
                    	stmt.setNString(12, EndMonth);//�]�w�ĤQ�G��?����EndMonth
                    	ResultSet member_datail = stmt.executeQuery();//����d�ߨñN���G��imember_datail
                    	while (member_datail.next()) {
                    		String PM_tmp = member_datail.getString("Point_month");//�ӥͤ�n���s��PM_tmp
                    		String PS_tmp = member_datail.getString("Point_semester");//�ӥ;Ǵ��n���s��PS_tmp
                    		String lm_tmp = member_datail.getString("login_m");
                    		String em_tmp = member_datail.getString("exam_month");
                    		String es_tmp = member_datail.getString("exam_sem");
                    		if(PM_tmp == null ||PM_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			PM_tmp = "0";
                    		}
                    		if(PS_tmp == null ||PS_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			PS_tmp = "0";
                    		}
                    		if(lm_tmp == null ||lm_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			lm_tmp = "0";
                    		}
                    		if(em_tmp == null ||em_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			em_tmp = "0";
                    		}
                    		if(es_tmp == null ||es_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			es_tmp = "0";
                    		}
                    		classmonth = classmonth + Integer.parseInt(PM_tmp);//�[�`��n��
                    		classsemester = classsemester + Integer.parseInt(PS_tmp);//�[�`�Ǵ��n��
                    		islogin_m = islogin_m + Integer.parseInt(lm_tmp);//����O�_���n�J(�ϥΤH��)
                    		exam_m = exam_m + Integer.parseInt(em_tmp);//�[�`����Ҹ�
                    		exam_sem = exam_sem + Integer.parseInt(es_tmp);//�[�`�Ǵ��Ҹ�
                    		marktmp = marktmp +i2;//�P�_���S���]���q���Ʀr

                    	}
                    }
                    if(marktmp > 0) {//�N���]�L�W����	
                    	cls_Statistics = new Statistics();
                    	totalscore_m = totalscore_m + classmonth;//������ƥ[�`
                		totalscore_se = totalscore_se + classsemester;//�Ǵ����ƥ[�`
                		totalmember = totalmember + classmember.length;//�Ҧ��H�ƥ[�`
                		totallogin_m = totallogin_m + islogin_m;//����n�J�H�ƥ[�`
                		totalexam_m = totalexam_m + exam_m;//����Ҹզ��ƥ[�`
                		totalexam_se = totalexam_se + exam_sem;//�Ǵ��Ҹզ��ƥ[�`
                		isrun = marktmp;
                    	double clsme = (double)islogin_m/(double)classmember.length * 100;
                    	String Utilization = clsme +"%";//�ϥβv
                    	cls_Statistics.setCD_NO(classno[i]);
                    	cls_Statistics.setmembers(classmember.length);;
                    	cls_Statistics.setCls_Pointmonth(classmonth);
                    	cls_Statistics.setCls_Pointsemester(classsemester);
                    	cls_Statistics.setUtilization(Utilization);
                    	cls_Statistics.setmonth_login(islogin_m);
                    	cls_Statistics.setmonth_exams(exam_m);
                    	cls_Statistics.setsemester_exams(exam_sem);
                    	getclass.add(cls_Statistics);
                    }
                }
            }
            stmt.close();
            stmt = null;
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
		// TODO Auto-generated method stub
		return getclass;
	}
	//�p�G���O���~�ťB���O��@���(���7~9,����)
	@Override
	public List<Statistics> getdetail_2(String selectgrade, Date start_m, Date end_m) {
		List<Statistics> getclass =  new ArrayList<>();
		String StartDate;//�Ǵ��}�l���
		String EndDate;//�Ǵ��������
		String classNO_tmp = null;//�Z�ťN���Ȧs�r��
		String classNO_tmp_2 = "";//�Z�ťN���Ȧs�r��
		String membertmp = null;		
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//�]�w�榡yyyy-MM
		StartDate =  getyymm.format(start_m);//�N�ǨӪ��ɶ�����yyyy-MM 
		EndDate =  getyymm.format(end_m);//�N�ǨӪ��ɶ�����yyyy-MM
		long daydiff = (end_m.getTime()-start_m.getTime())/((24*60*60*1000));//�ۮt���(����-�}�l)/24�p��*60��*60��*1000(��)
		long mondiff = daydiff /30;//�ۮt��� (�ۮt���)/30��
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_NO FROM next_generation.class_data where CD_GRADE = ?");//���Z�ťN��
            stmt.setNString(1,selectgrade);//�]�w�Ĥ@��?����selectgrade
            ResultSet classnotSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
            while (classnotSet.next()) {
            	classNO_tmp = classnotSet.getString("CD_NO");
            	classNO_tmp_2 = classNO_tmp_2 + classNO_tmp + ",";
            }
            String classNO_tmp_3 = classNO_tmp_2.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//�h��classNO_tmp_2�̪�[]��Ҧ��ť�
            String[] classno = classNO_tmp_3.split(",");//���o�Z�ťN���}�C
            Statistics cls_Statistics;
            for(int i = 0; i < classno.length; i++) {
            	stmt = conn.prepareStatement("SELECT CD_MEMBER FROM next_generation.class_data where CD_NO = ?");//���Z�Ŧ���
            	stmt.setNString(1, classno[i]);//�]�w�Ĥ@��?����classno[i]
            	ResultSet member_rs = stmt.executeQuery();//����d�ߨñN���G��iresultSet
                int classsemester = 0;//�Z�žǴ��n��
                int exam_sem =0 ;//�Ǵ��Ҹզ���
                int clslogintimes = 0;//�Z�ŵn�J����
                int marktmp = 0;
                while (member_rs.next()) {
                	membertmp = member_rs.getString("CD_MEMBER");//��CD_MEMBER��imembertmp
                    String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//�h��membertmp�̪�[]��Ҧ��ť�
                    String[] classmember = membertmp_2.split(",");//���o�Z�Ŧ����}�C
                    for(int i2 = 0; i2 < classmember.length; i2++) {
                    	stmt = conn.prepareStatement("SELECT "
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? ) as exam_sem,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as Point_semester,"
                    			+ "(SELECT sum(LD_FREQUENCY) FROM next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") between ? and ? and LD_ACCOUNT = ?)as login_times");
                    	stmt.setNString(1, classmember[i2]);//�]�w��1��?����classmember[i]
                    	stmt.setNString(2, StartDate);//�]�w��2��?����StartDate
                    	stmt.setNString(3, EndDate);//�]�w��3��?����EndDate
                    	stmt.setNString(4, StartDate);//�]�w��4��?����StartDate
                    	stmt.setNString(5, EndDate);//�]�w��5��?����EndDate
                    	stmt.setNString(6, classmember[i2]);//�]�w��6��?����classmember[i]
                    	stmt.setNString(7, StartDate);//�]�w��7��?����StartDate
                    	stmt.setNString(8, EndDate);//�]�w��8��?����EndDate
                    	stmt.setNString(9, classmember[i2]);//�]�w��9��?����classmember[i]
                    	
                    	ResultSet member_datail = stmt.executeQuery();//����d�ߨñN���G��imember_datail
                    	while (member_datail.next()) {
                    		String PS_tmp = member_datail.getString("Point_semester");//�ӥ;Ǵ��n���s��PS_tmp
                    		String es_tmp = member_datail.getString("exam_sem");//�����Ҹզ���
                    		String lt_tmp = member_datail.getString("login_times");//�����n�J����
                    		if(PS_tmp == null ||PS_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			PS_tmp = "0";
                    		}
                    		if(es_tmp == null ||es_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			es_tmp = "0";
                    		}
                    		if(lt_tmp == null ||lt_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			lt_tmp = "0";
                    		}
                    		classsemester = classsemester + Integer.parseInt(PS_tmp);//�[�`�Ǵ��n��
                    		exam_sem = exam_sem + Integer.parseInt(es_tmp);//�[�`�Ǵ��Ҹ�
                    		clslogintimes = clslogintimes +  Integer.parseInt(lt_tmp);//�[�`�n�J����
                    		marktmp = marktmp +i2;//�P�_���S���]���q���Ʀr
                    	}
                    }
                    if(marktmp > 0) {//�N���]�L�W����	
                    	cls_Statistics = new Statistics();
                    	if(mondiff == 0) {//�p�G�O0,�N��P���,����@�Ӥ�
                    		mondiff = 1;
                    	}
                    	long avg_exam = exam_sem/mondiff;//�����Ҹ�
                    	long avg_point = classsemester/mondiff;//��������
                    	cls_Statistics.setCD_NO(classno[i]);
                    	cls_Statistics.setCls_Pointsemester(classsemester);
                    	cls_Statistics.setCls_Pointmonth((int)avg_point);
                    	cls_Statistics.setsemester_exams(exam_sem);
                    	cls_Statistics.setmonth_exams((int)avg_exam);
                    	cls_Statistics.setmonth_login(clslogintimes);
                    	getclass.add(cls_Statistics);
                    }
                }
            }
            stmt.close();
            stmt = null;
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
		return getclass;
	}
	//�p�G�O���~�ťB���O����(��ܥ��~��,��@���)
	@Override
	public List<Statistics> getdetail_3(String selectgrade, Date single_m) {
		List<Statistics> getclass =  new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        String singlemonth;//�����
		String StartMonth;//�Ǵ��}�l���
		String EndMonth;//�Ǵ��������
		String classNO_tmp = null;//�Z�ťN���Ȧs�r��
		String classNO_tmp_2 = "";//�Z�ťN���Ȧs�r��
		String membertmp = null;
		int theMonth;
		int theYear;
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//�]�w�榡yyyy-MM
		java.text.SimpleDateFormat getmonth = new java.text.SimpleDateFormat("MM");//�]�w�榡MM
		java.text.SimpleDateFormat getyear = new java.text.SimpleDateFormat("yyyy");//�]�w�榡yyyy
		singlemonth =  getyymm.format(single_m);//�N�ǨӪ��ɶ�����yyyy-MM 
		theMonth = Integer.parseInt(getmonth.format(single_m));//���o�Ӥ���H�ΨӧP�_
		theYear = Integer.parseInt(getyear.format(single_m));//���o�Ӧ~���H�ΨӧP�_
		
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
		//-------------------------------------
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_NO FROM next_generation.class_data");//���Z�ťN��(���h���Ǯծ�,�[�JCD_SCHOOL����)
            ResultSet classnotSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
            while (classnotSet.next()) {
            	classNO_tmp = classnotSet.getString("CD_NO");
            	classNO_tmp_2 = classNO_tmp_2 + classNO_tmp + ",";
            }
            String classNO_tmp_3 = classNO_tmp_2.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//�h��classNO_tmp_2�̪�[]��Ҧ��ť�
            String[] classno = classNO_tmp_3.split(",");//���o�Z�ťN���}�C
            Statistics cls_Statistics;
            for(int i = 0; i < classno.length; i++) {
            	stmt = conn.prepareStatement("SELECT CD_MEMBER FROM next_generation.class_data where CD_NO = ?");//���Z�Ŧ���
            	stmt.setNString(1, classno[i]);//�]�w�Ĥ@��?����classno[i]
            	ResultSet member_rs = stmt.executeQuery();//����d�ߨñN���G��iresultSet
                int classmonth = 0;//�Z�Ť�n��
                int classsemester = 0;//�Z�žǴ��n��
                int islogin_m =0;//����ϥΤH��
                int exam_m = 0;//����Ҹզ���
                int exam_sem =0 ;//�Ǵ��Ҹզ���
                int marktmp = 0;
                while (member_rs.next()) {
                	membertmp = member_rs.getString("CD_MEMBER");//��CD_MEMBER��imembertmp
                	
                    String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//�h��membertmp�̪�[]��Ҧ��ť�
                    String[] classmember = membertmp_2.split(",");//���o�Z�Ŧ����}�C
                    for(int i2 = 0; i2 < classmember.length; i2++) {
                    	stmt = conn.prepareStatement("SELECT "
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as exam_month,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as Point_month,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as Point_semester,"
                    			+ "(SELECT (CASE when LD_FREQUENCY is not null then '1' ELSE '0' end ) as log_m from next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") = ? and LD_ACCOUNT = ?) as login_m,"
                    			//+ "(SELECT (CASE when LAST_LOGIN like ? then '1' ELSE '0' end ) as log_m from next_generation.sys_data where SYS_ACCOUNT = ?) as login_m,"
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? ) as exam_sem");
                    	//�Ĥ@�Ӽ����Ҹզ���,�ĤG�Ӽ�������,�ĤT�Ӽ��Ǵ�����,�ĥ|�Ӽ����O�_���ϥ�,�Ĥ��Ӽ��Ǵ��Ҹզ���
                    	stmt.setNString(1, classmember[i2]);//�]�w�Ĥ@��?����classmember[i]
                    	stmt.setNString(2, singlemonth+"%");//�]�w�ĤG��?����singlemonth%
                    	stmt.setNString(3, classmember[i2]);//�]�w�ĤT��?����classmember[i]
                    	stmt.setNString(4, singlemonth+"%");//�]�w�ĥ|��?����singlemonth%
                    	stmt.setNString(5, StartMonth);//�]�w�Ĥ���?����StartMonth
                    	stmt.setNString(6, EndMonth);//�]�w�Ĥ���?����EndMonth
                    	stmt.setNString(7, classmember[i2]);//�]�w�ĤC��?����classmember[i]
                    	stmt.setNString(8, singlemonth);//�]�w�ĤK��?����singlemonth%
                    	stmt.setNString(9, classmember[i2]);//�]�w�ĤE��?����classmember[i]
                    	stmt.setNString(10, classmember[i2]);//�]�w�ĤQ��?����classmember[i]
                    	stmt.setNString(11, StartMonth);//�]�w�ĤQ�@��?����StartMonth
                    	stmt.setNString(12, EndMonth);//�]�w�ĤQ�G��?����EndMonth
                    	ResultSet member_datail = stmt.executeQuery();//����d�ߨñN���G��imember_datail
                    	while (member_datail.next()) {
                    		String PM_tmp = member_datail.getString("Point_month");//�ӥͤ�n���s��PM_tmp
                    		String PS_tmp = member_datail.getString("Point_semester");//�ӥ;Ǵ��n���s��PS_tmp
                    		String lm_tmp = member_datail.getString("login_m");
                    		String em_tmp = member_datail.getString("exam_month");
                    		String es_tmp = member_datail.getString("exam_sem");
                    		if(PM_tmp == null ||PM_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			PM_tmp = "0";
                    		}
                    		if(PS_tmp == null ||PS_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			PS_tmp = "0";
                    		}
                    		if(lm_tmp == null ||lm_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			lm_tmp = "0";
                    		}
                    		if(em_tmp == null ||em_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			em_tmp = "0";
                    		}
                    		if(es_tmp == null ||es_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			es_tmp = "0";
                    		}
                    		classmonth = classmonth + Integer.parseInt(PM_tmp);//�[�`��n��
                    		classsemester = classsemester + Integer.parseInt(PS_tmp);//�[�`�Ǵ��n��
                    		islogin_m = islogin_m + Integer.parseInt(lm_tmp);//����O�_���n�J(�ϥΤH��)
                    		exam_m = exam_m + Integer.parseInt(em_tmp);//�[�`����Ҹ�
                    		exam_sem = exam_sem + Integer.parseInt(es_tmp);//�[�`�Ǵ��Ҹ�
                    		marktmp = marktmp +i2;//�P�_���S���]���q���Ʀr
                    	}
                    }
                    if(marktmp > 0) {//�N���]�L�W����	
                    	cls_Statistics = new Statistics();
                    	totalscore_m = totalscore_m + classmonth;//������ƥ[�`
                		totalscore_se = totalscore_se + classsemester;//�Ǵ����ƥ[�`
                		totalmember = totalmember + classmember.length;//�Ҧ��H�ƥ[�`
                		totallogin_m = totallogin_m + islogin_m;//����n�J�H�ƥ[�`
                		totalexam_m = totalexam_m + exam_m;//����Ҹզ��ƥ[�`
                		totalexam_se = totalexam_se + exam_sem;//�Ǵ��Ҹզ��ƥ[�`
                		isrun = marktmp;
                    	double clsme = (double)islogin_m/(double)classmember.length * 100;
                    	String Utilization = clsme +"%";//�ϥβv
                    	cls_Statistics.setCD_NO(classno[i]);
                    	cls_Statistics.setmembers(classmember.length);;
                    	cls_Statistics.setCls_Pointmonth(classmonth);
                    	cls_Statistics.setCls_Pointsemester(classsemester);
                    	cls_Statistics.setUtilization(Utilization);
                    	cls_Statistics.setmonth_login(islogin_m);
                    	cls_Statistics.setmonth_exams(exam_m);
                    	cls_Statistics.setsemester_exams(exam_sem);
                    	getclass.add(cls_Statistics);
                    }
                }
            }
            stmt.close();
            stmt = null;
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
		// TODO Auto-generated method stub
		return getclass;
	}
	//�p�G�O���~�ťB�O����(��ܥ��~��,����)
	@Override
	public List<Statistics> getdetail_4(String selectgrade, Date start_m, Date end_m) {
		List<Statistics> getclass =  new ArrayList<>();
		String StartDate;//�Ǵ��}�l���
		String EndDate;//�Ǵ��������
		String classNO_tmp = null;//�Z�ťN���Ȧs�r��
		String classNO_tmp_2 = "";//�Z�ťN���Ȧs�r��
		String membertmp = null;		
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//�]�w�榡yyyy-MM
		StartDate =  getyymm.format(start_m);//�N�ǨӪ��ɶ�����yyyy-MM 
		EndDate =  getyymm.format(end_m);//�N�ǨӪ��ɶ�����yyyy-MM
		long daydiff = (end_m.getTime()-start_m.getTime())/((24*60*60*1000));//�ۮt���(����-�}�l)/24�p��*60��*60��*1000(��)
		long mondiff = daydiff /30;//�ۮt��� = �ۮt���/30��
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_NO FROM next_generation.class_data");//���Z�ťN��(���h���Ǯծ�,�[�JCD_SCHOOL����)
            ResultSet classnotSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
            while (classnotSet.next()) {
            	classNO_tmp = classnotSet.getString("CD_NO");
            	classNO_tmp_2 = classNO_tmp_2 + classNO_tmp + ",";
            }
            String classNO_tmp_3 = classNO_tmp_2.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//�h��classNO_tmp_2�̪�[]��Ҧ��ť�
            String[] classno = classNO_tmp_3.split(",");//���o�Z�ťN���}�C
            Statistics cls_Statistics;
            for(int i = 0; i < classno.length; i++) {
            	stmt = conn.prepareStatement("SELECT CD_MEMBER FROM next_generation.class_data where CD_NO = ?");//���Z�Ŧ���
            	stmt.setNString(1, classno[i]);//�]�w�Ĥ@��?����classno[i]
            	ResultSet member_rs = stmt.executeQuery();//����d�ߨñN���G��iresultSet
                int classsemester = 0;//�Z�žǴ��n��
                int clslogintimes = 0;//�Z�ŵn�J����
                int exam_sem =0 ;//�Ǵ��Ҹզ���
                int marktmp = 0;
                while (member_rs.next()) {
                	membertmp = member_rs.getString("CD_MEMBER");//��CD_MEMBER��imembertmp
                	
                    String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//�h��membertmp�̪�[]��Ҧ��ť�
                    String[] classmember = membertmp_2.split(",");//���o�Z�Ŧ����}�C
                    for(int i2 = 0; i2 < classmember.length; i2++) {
                    	stmt = conn.prepareStatement("SELECT "
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? ) as exam_sem,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as Point_semester,"
                    			+ "(SELECT sum(LD_FREQUENCY) FROM next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") between ? and ? and LD_ACCOUNT = ?)as login_times");
                    	stmt.setNString(1, classmember[i2]);//�]�w��1��?����classmember[i]
                    	stmt.setNString(2, StartDate);//�]�w��2��?����StartDate
                    	stmt.setNString(3, EndDate);//�]�w��3��?����EndDate
                    	stmt.setNString(4, StartDate);//�]�w��4��?����StartDate
                    	stmt.setNString(5, EndDate);//�]�w��5��?����EndDate
                    	stmt.setNString(6, classmember[i2]);//�]�w��6��?����classmember[i]
                    	stmt.setNString(7, StartDate);//�]�w��7��?����StartDate
                    	stmt.setNString(8, EndDate);//�]�w��8��?����EndDate
                    	stmt.setNString(9, classmember[i2]);//�]�w��9��?����classmember[i]
                    	ResultSet member_datail = stmt.executeQuery();//����d�ߨñN���G��imember_datail
                    	while (member_datail.next()) {
                    		String PS_tmp = member_datail.getString("Point_semester");//�ӥ;Ǵ��n���s��PS_tmp
                    		String es_tmp = member_datail.getString("exam_sem");//�����Ҹզ���
                    		String lt_tmp = member_datail.getString("login_times");//�����n�J����
                    		if(PS_tmp == null ||PS_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			PS_tmp = "0";
                    		}
                    		if(es_tmp == null ||es_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			es_tmp = "0";
                    		}
                    		if(lt_tmp == null ||lt_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
                    			lt_tmp = "0";
                    		}
                    		classsemester = classsemester + Integer.parseInt(PS_tmp);//�[�`�Ǵ��n��
                    		exam_sem = exam_sem + Integer.parseInt(es_tmp);//�[�`�Ǵ��Ҹ�
                    		clslogintimes = clslogintimes +  Integer.parseInt(lt_tmp);//�[�`�n�J����
                    		marktmp = marktmp +i2;//�P�_���S���]���q���Ʀr
                    	}
                    }
                    if(marktmp > 0) {//�N���]�L�W����	
                    	cls_Statistics = new Statistics();
                    	if(mondiff == 0) {//�p�G�O0,�N��P���,����@�Ӥ�
                    		mondiff = 1;
                    	}
                    	long avg_exam = exam_sem/mondiff;//�����Ҹ�
                    	long avg_point = classsemester/mondiff;//��������
                    	cls_Statistics.setCD_NO(classno[i]);
                    	cls_Statistics.setCls_Pointsemester(classsemester);
                    	cls_Statistics.setCls_Pointmonth((int)avg_point);
                    	cls_Statistics.setsemester_exams(exam_sem);
                    	cls_Statistics.setmonth_exams((int)avg_exam);
                    	cls_Statistics.setmonth_login(clslogintimes);
                    	getclass.add(cls_Statistics);
                    }
                }
            }
            stmt.close();
            stmt = null;
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
		return getclass;
	}
	
	//���o��@�������
	@Override
	public List<Statistics> getScoretotal_1() {
		List<Statistics> getscore =  new ArrayList<>();
		Statistics scoretotal;
		if (isrun>0) {
			scoretotal = new Statistics();
			double clsme = (double)totallogin_m/(double)totalmember * 100;
			String Utilization = clsme +"%";//�ϥβv
			scoretotal.setCls_Pointmonth(totalscore_m);
			scoretotal.setCls_Pointsemester(totalscore_se);
			scoretotal.setUtilization(Utilization);
			scoretotal.setmonth_login(totallogin_m);
			scoretotal.setmembers(totalmember);
			scoretotal.setmonth_exams(totalexam_m);
			scoretotal.setsemester_exams(totalexam_se);
			getscore.add(scoretotal);
		}
		return getscore;
	}

}
