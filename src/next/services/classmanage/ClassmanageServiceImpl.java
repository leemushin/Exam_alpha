package next.services.classmanage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class ClassmanageServiceImpl implements ClassmanageService {

	//���o���ЯZ��
	@Override
	public List<Classmanage> class_teach(String user_Acc) {
		List<Classmanage> classlist = new ArrayList<Classmanage>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_NO,CD_NAME FROM next_generation.class_data where CD_TEACHER like ?");
            //���CD_NO,CD_NAME ����OCD_TEACHER = �ϥΪ̱b��
            stmt.setNString(1, "%"+user_Acc+"%");//�]�w�Ĥ@��?����%user_Acc%
            ResultSet resultSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
            Classmanage teach_class ;
            while (resultSet.next()) {
            	teach_class = new Classmanage();
            	teach_class.setCD_NO(resultSet.getString("CD_NO"));
            	teach_class.setCD_NAME(resultSet.getString("CD_NAME"));
            	classlist.add(teach_class);
            }
            stmt.close();
            stmt = null;
        }
        catch (SQLException e) {
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
		return classlist;
	}

	//���o�Z�ŸԲӰT��
	@Override
	public List<Classmanage> class_deatail(String NO) {
		//�P�_�W�U�Ǵ���---------------------
		String loginmonth;//�n�J���
		String StartMonth;//�Ǵ��}�l���
		String EndMonth;//�Ǵ��������
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
		//-------------------------------------
		
		List<Classmanage> classlist = new ArrayList<Classmanage>();
        Connection conn = null;
        PreparedStatement stmt = null;
        String membertmp = null;//�Z�Ŧ����Ȧs�r��
        String Sbidtmp =null;//�̫�@�D�Ȧs�r��
        int classmonth = 0;//�Z�Ť�n��
        int classsemester = 0;//�Z�žǴ��n��
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_MEMBER FROM next_generation.class_data where CD_NO = ?");
            //���CD_MEMBER ����OCD_NO = �Z�ťN��
            stmt.setNString(1, NO);//�]�w�Ĥ@��?����NO
            ResultSet member_rs = stmt.executeQuery();//����d�ߨñN���G��iresultSet
            while (member_rs.next()) {
            	membertmp = member_rs.getString("CD_MEMBER");//��CD_MEMBER��imembertmp
            }
            String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//�h��membertmp�̪�[]��Ҧ��ť�
            String[] classmember = membertmp_2.split(",");//���o�Z�Ŧ����}�C
            String[] Last_sbid =new String[classmember.length];//�̫�ϥ��D�ذ}�C,���׸�Z�Ŧ����@��
            
            for(int i = 0; i < classmember.length; i++){
            	stmt = conn.prepareStatement("SELECT SB_ID from next_generation.score_base where SB_ACCOUNT = ? ORDER BY SB_ID DESC limit 1 ");
            	stmt.setNString(1, classmember[i]);
            	ResultSet SBid_rs = stmt.executeQuery();//����d�ߨñN���G��iSBid_rs
            	while (SBid_rs.next()) {
            		Sbidtmp =  SBid_rs.getString("SB_ID");
            	}
            	Last_sbid[i] = Sbidtmp;
            	Sbidtmp = "";
            }
      
            Classmanage cls_deatail;
            for(int i = 0; i < classmember.length; i++){
            	stmt = conn.prepareStatement("SELECT ur.UR_ACCOUNT,ur.UR_NAME,sy.Last_login,sb.SB_QUIZNAME,"
            			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as Point_month,"
            			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as Point_semester"
            			+ " FROM next_generation.user_role ur"
            			+ " LEFT JOIN next_generation.sys_data sy ON sy.SYS_ACCOUNT = ur.UR_ACCOUNT"
            			+ " LEFT JOIN next_generation.score_base sb ON sb.SB_ID = ?"
            			+ " where ur.UR_ACCOUNT = ?");//���󪺪ťդ��i�çR,�i��ɭPsql�y�k�X��
            	stmt.setNString(1, classmember[i]);//�]�w�Ĥ@��?����classmember[i]
            	stmt.setNString(2, loginmonth+"%");//�]�w�ĤG��?����loginmonth%
            	stmt.setNString(3, StartMonth);//�]�w�ĤT��?����StartMonth
            	stmt.setNString(4, EndMonth);//�]�w�ĥ|��?����EndMonth
            	stmt.setNString(5, classmember[i]);//�]�w�Ĥ���?����classmember[i]
            	stmt.setNString(6, Last_sbid[i]);//�]�w�Ĥ���?���󬰳̫�@�Did
            	stmt.setNString(7, classmember[i]);//�]�w�ĤC��?����classmember[i]
            	ResultSet member_datail = stmt.executeQuery();//����d�ߨñN���G��imember_datail
            	while (member_datail.next()) {
            		cls_deatail = new Classmanage();
            		cls_deatail.setUR_ACCOUNT(member_datail.getNString("UR_ACCOUNT"));
            		cls_deatail.setUR_NAME(member_datail.getNString("UR_NAME"));
            		cls_deatail.setLast_login(member_datail.getString("Last_login"));
            		cls_deatail.setLast_QUIZNAME(member_datail.getString("SB_QUIZNAME"));
            		cls_deatail.setPoint_month(member_datail.getString("Point_month"));
            		cls_deatail.setPoint_semester(member_datail.getString("Point_semester"));
            		classlist.add(cls_deatail);
            	}
            }
            stmt.close();
            stmt = null;
        }
        catch (SQLException e) {
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
		return classlist;
	}
	
	//���o�Z�ſn��--�j�����o�ԲӮ����ۦP
	@Override
	public List<Classmanage> class_point(String NO) {
		//�P�_�W�U�Ǵ���---------------------
		String loginmonth;//�n�J���
		String StartMonth;//�Ǵ��}�l���
		String EndMonth;//�Ǵ��������
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
		//-------------------------------------
		
		List<Classmanage> classlist = new ArrayList<Classmanage>();
        Connection conn = null;
        PreparedStatement stmt = null;
        String classname = null;//�Z�ŦW�٦r��
        String membertmp = null;//�Z�Ŧ����Ȧs�r��
        int classmonth = 0;//�Z�Ť�n��
        int classsemester = 0;//�Z�žǴ��n��
        int examtimes_month = 0;//�Z�Ť�Ҹզ���
        int examtimes_semester = 0;//�Z�žǴ��Ҹզ���
        int login_month = 0;//�Z�Ť�n�J����
        int login_semester = 0;//�Z�žǴ��n�J����
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_MEMBER,CD_NAME FROM next_generation.class_data where CD_NO = ?");
            //���CD_MEMBER ����OCD_NO = �Z�ťN��
            stmt.setNString(1, NO);//�]�w�Ĥ@��?����NO
            ResultSet member_rs = stmt.executeQuery();//����d�ߨñN���G��iresultSet
            while (member_rs.next()) {
            	membertmp = member_rs.getString("CD_MEMBER");//��CD_MEMBER��imembertmp
            	classname = member_rs.getString("CD_NAME");//��CD_NAME��iclassname
            }
            String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//�h��membertmp�̪�[]��Ҧ��ť�
            String[] classmember = membertmp_2.split(",");//���o�Z�Ŧ����}�C        
            Classmanage cls_deatail;
            for(int i = 0; i < classmember.length; i++){
            	stmt = conn.prepareStatement("SELECT "
            			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as Point_month,"
            			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as Point_semester,"
            			+ "(SELECT Monthly_exams from next_generation.login_data where LD_ACCOUNT = ? and LD_MONTH like ?) as examtimes_month,"
            			+ "(SELECT sum(Monthly_exams) from next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") between ? and ? and LD_ACCOUNT =?) as examtimes_semester,"
            			+ "(SELECT LD_FREQUENCY from next_generation.login_data where LD_ACCOUNT = ? and LD_MONTH like ?) as login_month,"
            			+ "(SELECT sum(LD_FREQUENCY) from next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") between ? and ? and LD_ACCOUNT =?) as login_semester"
            			);//���󪺪ťդ��i�çR,�i��ɭPsql�y�k�X��
            	stmt.setNString(1, classmember[i]);//�]�w�Ĥ@��?����classmember[i]
            	stmt.setNString(2, loginmonth+"%");//�]�w�ĤG��?����loginmonth%
            	stmt.setNString(3, StartMonth);//�]�w�ĤT��?����StartMonth
            	stmt.setNString(4, EndMonth);//�]�w�ĥ|��?����EndMonth
            	stmt.setNString(5, classmember[i]);//�]�w�Ĥ���?����classmember[i]
            	stmt.setNString(6, classmember[i]);//�]�w�Ĥ���?����classmember[i]
            	stmt.setNString(7, loginmonth+"%");//�]�w�ĤC��?����loginmonth%
            	stmt.setNString(8, StartMonth);//�]�w�ĤK��?����StartMonth
            	stmt.setNString(9, EndMonth);//�]�w�ĤE��?����EndMonth
            	stmt.setNString(10, classmember[i]);//�]�w�ĤQ��?����classmember[i]
            	stmt.setNString(11, classmember[i]);//�]�w�ĤQ�@��?����classmember[i]
            	stmt.setNString(12, loginmonth+"%");//�]�w�ĤQ�G��?����loginmonth%
            	stmt.setNString(13, StartMonth);//�]�w�ĤQ�T��?����StartMonth
            	stmt.setNString(14, EndMonth);//�]�w�ĤQ�|��?����EndMonth
            	stmt.setNString(15, classmember[i]);//�]�w�ĤQ����?����classmember[i]
            	ResultSet member_datail = stmt.executeQuery();//����d�ߨñN���G��imember_datail
            	while (member_datail.next()) {	
            		String PM_tmp = member_datail.getString("Point_month");//�ӥͤ�n���s��PM_tmp
            		String PS_tmp = member_datail.getString("Point_semester");//�ӥ;Ǵ��n���s��PS_tmp
            		String EM_tmp = member_datail.getString("examtimes_month");//�ӥͤ�Ҹզ��Ʀs��EM_tmp
            		String ES_tmp = member_datail.getString("examtimes_semester");//�ӥ;Ǵ��Ҹզ��Ʀs��ES_tmp
            		String LM_tmp = member_datail.getString("login_month");//�ӥͤ�n�J���Ʀs��LM_tmp
            		String LS_tmp = member_datail.getString("login_semester");//�ӥ;Ǵ��n�J���Ʀs��LS_tmp
            		if(PM_tmp == null ||PM_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
            			PM_tmp = "0";
            		}
            		if(PS_tmp == null ||PS_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
            			PS_tmp = "0";
            		}
            		if(EM_tmp == null ||EM_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
            			EM_tmp = "0";
            		}
            		if(ES_tmp == null ||ES_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
            			ES_tmp = "0";
            		}
            		if(LM_tmp == null ||LM_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
            			LM_tmp = "0";
            		}
            		if(LS_tmp == null ||LS_tmp == "" ) {//�p�G�Onull��""���w��0,�קK�U���নint�X��
            			LS_tmp = "0";
            		}
            		classmonth = classmonth + Integer.parseInt(PM_tmp);//�[�`��n��
            		classsemester = classsemester + Integer.parseInt(PS_tmp);//�[�`�Ǵ��n��
            		examtimes_month = examtimes_month + Integer.parseInt(EM_tmp);//�[�`��Ҹզ���
            		examtimes_semester = examtimes_semester + Integer.parseInt(ES_tmp);//�[�`�Ǵ��Ҹզ���
            		login_month = login_month  + Integer.parseInt(LM_tmp);//�[�`�Ǵ��n�J����
            		login_semester = login_semester + Integer.parseInt(LS_tmp);//�[�`�Ǵ��n�J����
            	}
            }
                cls_deatail = new Classmanage();
                cls_deatail.setCD_NAME(classname);
                cls_deatail.setCls_Pointmonth(classmonth);
                cls_deatail.setCls_Pointsemester(classsemester);
                cls_deatail.setCls_examtimesmonth(examtimes_month);
                cls_deatail.setCls_examtimessemester(examtimes_semester);
                cls_deatail.setCls_loginmonth(login_month);
                cls_deatail.setCls_loginsemester(login_semester);
                classlist.add(cls_deatail);
        	
            stmt.close();
            stmt = null;
        }
        catch (SQLException e) {
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
		return classlist;
	}

	
}
