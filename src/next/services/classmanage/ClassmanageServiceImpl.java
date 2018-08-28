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

	//取得任教班級
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
            //選取CD_NO,CD_NAME 條件是CD_TEACHER = 使用者帳號
            stmt.setNString(1, "%"+user_Acc+"%");//設定第一個?條件為%user_Acc%
            ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
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

	//取得班級詳細訊息
	@Override
	public List<Classmanage> class_deatail(String NO) {
		//判斷上下學期用---------------------
		String loginmonth;//登入月份
		String StartMonth;//學期開始月份
		String EndMonth;//學期結束月份
		int theMonth;
		int theYear;
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//設定格式yyyy-MM
		java.text.SimpleDateFormat getmonth = new java.text.SimpleDateFormat("MM");//設定格式MM
		java.text.SimpleDateFormat getyear = new java.text.SimpleDateFormat("yyyy");//設定格式yyyy
		java.util.Date dt = new java.util.Date();//取得時間
		loginmonth = getyymm.format(dt);//將時間切成yyyy-MM 
		theMonth = Integer.parseInt(getmonth.format(dt));//取得該月份以用來判斷
		theYear = Integer.parseInt(getyear.format(dt));//取得該年份以用來判斷
		
		if(theMonth>=2 && theMonth<8) {							//如果在2~7月登入
			StartMonth =  (Integer.toString(theYear))+"-02";	//開始月份為20XX-02
			EndMonth = 	 (Integer.toString(theYear))+"-07";		//結束月份為20XX-07
		}
		else if(theMonth>=8 && theMonth<=12) {					//如果在8~12月登入
			StartMonth =  (Integer.toString(theYear))+"-08";	//開始月份為20XX-08
			EndMonth = 	 (Integer.toString(theYear+1))+"-01";	//結束月份為(20XX+1)-01 跨年度
		}
		else {													//如果在1月份登入
			StartMonth =  (Integer.toString(theYear-1))+"-08";	//開始月份為(20XX-1)-08 跨年度
			EndMonth = 	 (Integer.toString(theYear))+"-01";		//結束月份為20XX-01
		}
		//-------------------------------------
		
		List<Classmanage> classlist = new ArrayList<Classmanage>();
        Connection conn = null;
        PreparedStatement stmt = null;
        String membertmp = null;//班級成員暫存字串
        String Sbidtmp =null;//最後一題暫存字串
        int classmonth = 0;//班級月積分
        int classsemester = 0;//班級學期積分
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_MEMBER FROM next_generation.class_data where CD_NO = ?");
            //選取CD_MEMBER 條件是CD_NO = 班級代號
            stmt.setNString(1, NO);//設定第一個?條件為NO
            ResultSet member_rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
            while (member_rs.next()) {
            	membertmp = member_rs.getString("CD_MEMBER");//把CD_MEMBER丟進membertmp
            }
            String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除membertmp裡的[]跟所有空白
            String[] classmember = membertmp_2.split(",");//取得班級成員陣列
            String[] Last_sbid =new String[classmember.length];//最後使用題目陣列,長度跟班級成員一樣
            
            for(int i = 0; i < classmember.length; i++){
            	stmt = conn.prepareStatement("SELECT SB_ID from next_generation.score_base where SB_ACCOUNT = ? ORDER BY SB_ID DESC limit 1 ");
            	stmt.setNString(1, classmember[i]);
            	ResultSet SBid_rs = stmt.executeQuery();//執行查詢並將結果塞進SBid_rs
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
            			+ " where ur.UR_ACCOUNT = ?");//條件的空白不可亂刪,可能導致sql語法出錯
            	stmt.setNString(1, classmember[i]);//設定第一個?條件為classmember[i]
            	stmt.setNString(2, loginmonth+"%");//設定第二個?條件為loginmonth%
            	stmt.setNString(3, StartMonth);//設定第三個?條件為StartMonth
            	stmt.setNString(4, EndMonth);//設定第四個?條件為EndMonth
            	stmt.setNString(5, classmember[i]);//設定第五個?條件為classmember[i]
            	stmt.setNString(6, Last_sbid[i]);//設定第六個?條件為最後一題id
            	stmt.setNString(7, classmember[i]);//設定第七個?條件為classmember[i]
            	ResultSet member_datail = stmt.executeQuery();//執行查詢並將結果塞進member_datail
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
	
	//取得班級積分--大抵跟取得詳細消息相同
	@Override
	public List<Classmanage> class_point(String NO) {
		//判斷上下學期用---------------------
		String loginmonth;//登入月份
		String StartMonth;//學期開始月份
		String EndMonth;//學期結束月份
		int theMonth;
		int theYear;
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//設定格式yyyy-MM
		java.text.SimpleDateFormat getmonth = new java.text.SimpleDateFormat("MM");//設定格式MM
		java.text.SimpleDateFormat getyear = new java.text.SimpleDateFormat("yyyy");//設定格式yyyy
		java.util.Date dt = new java.util.Date();//取得時間
		loginmonth = getyymm.format(dt);//將時間切成yyyy-MM 
		theMonth = Integer.parseInt(getmonth.format(dt));//取得該月份以用來判斷
		theYear = Integer.parseInt(getyear.format(dt));//取得該年份以用來判斷
		
		if(theMonth>=2 && theMonth<8) {							//如果在2~7月登入
			StartMonth =  (Integer.toString(theYear))+"-02";	//開始月份為20XX-02
			EndMonth = 	 (Integer.toString(theYear))+"-07";		//結束月份為20XX-07
		}
		else if(theMonth>=8 && theMonth<=12) {					//如果在8~12月登入
			StartMonth =  (Integer.toString(theYear))+"-08";	//開始月份為20XX-08
			EndMonth = 	 (Integer.toString(theYear+1))+"-01";	//結束月份為(20XX+1)-01 跨年度
		}
		else {													//如果在1月份登入
			StartMonth =  (Integer.toString(theYear-1))+"-08";	//開始月份為(20XX-1)-08 跨年度
			EndMonth = 	 (Integer.toString(theYear))+"-01";		//結束月份為20XX-01
		}
		//-------------------------------------
		
		List<Classmanage> classlist = new ArrayList<Classmanage>();
        Connection conn = null;
        PreparedStatement stmt = null;
        String classname = null;//班級名稱字串
        String membertmp = null;//班級成員暫存字串
        int classmonth = 0;//班級月積分
        int classsemester = 0;//班級學期積分
        int examtimes_month = 0;//班級月考試次數
        int examtimes_semester = 0;//班級學期考試次數
        int login_month = 0;//班級月登入次數
        int login_semester = 0;//班級學期登入次數
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_MEMBER,CD_NAME FROM next_generation.class_data where CD_NO = ?");
            //選取CD_MEMBER 條件是CD_NO = 班級代號
            stmt.setNString(1, NO);//設定第一個?條件為NO
            ResultSet member_rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
            while (member_rs.next()) {
            	membertmp = member_rs.getString("CD_MEMBER");//把CD_MEMBER丟進membertmp
            	classname = member_rs.getString("CD_NAME");//把CD_NAME丟進classname
            }
            String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除membertmp裡的[]跟所有空白
            String[] classmember = membertmp_2.split(",");//取得班級成員陣列        
            Classmanage cls_deatail;
            for(int i = 0; i < classmember.length; i++){
            	stmt = conn.prepareStatement("SELECT "
            			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as Point_month,"
            			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as Point_semester,"
            			+ "(SELECT Monthly_exams from next_generation.login_data where LD_ACCOUNT = ? and LD_MONTH like ?) as examtimes_month,"
            			+ "(SELECT sum(Monthly_exams) from next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") between ? and ? and LD_ACCOUNT =?) as examtimes_semester,"
            			+ "(SELECT LD_FREQUENCY from next_generation.login_data where LD_ACCOUNT = ? and LD_MONTH like ?) as login_month,"
            			+ "(SELECT sum(LD_FREQUENCY) from next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") between ? and ? and LD_ACCOUNT =?) as login_semester"
            			);//條件的空白不可亂刪,可能導致sql語法出錯
            	stmt.setNString(1, classmember[i]);//設定第一個?條件為classmember[i]
            	stmt.setNString(2, loginmonth+"%");//設定第二個?條件為loginmonth%
            	stmt.setNString(3, StartMonth);//設定第三個?條件為StartMonth
            	stmt.setNString(4, EndMonth);//設定第四個?條件為EndMonth
            	stmt.setNString(5, classmember[i]);//設定第五個?條件為classmember[i]
            	stmt.setNString(6, classmember[i]);//設定第六個?條件為classmember[i]
            	stmt.setNString(7, loginmonth+"%");//設定第七個?條件為loginmonth%
            	stmt.setNString(8, StartMonth);//設定第八個?條件為StartMonth
            	stmt.setNString(9, EndMonth);//設定第九個?條件為EndMonth
            	stmt.setNString(10, classmember[i]);//設定第十個?條件為classmember[i]
            	stmt.setNString(11, classmember[i]);//設定第十一個?條件為classmember[i]
            	stmt.setNString(12, loginmonth+"%");//設定第十二個?條件為loginmonth%
            	stmt.setNString(13, StartMonth);//設定第十三個?條件為StartMonth
            	stmt.setNString(14, EndMonth);//設定第十四個?條件為EndMonth
            	stmt.setNString(15, classmember[i]);//設定第十五個?條件為classmember[i]
            	ResultSet member_datail = stmt.executeQuery();//執行查詢並將結果塞進member_datail
            	while (member_datail.next()) {	
            		String PM_tmp = member_datail.getString("Point_month");//該生月積分存到PM_tmp
            		String PS_tmp = member_datail.getString("Point_semester");//該生學期積分存到PS_tmp
            		String EM_tmp = member_datail.getString("examtimes_month");//該生月考試次數存到EM_tmp
            		String ES_tmp = member_datail.getString("examtimes_semester");//該生學期考試次數存到ES_tmp
            		String LM_tmp = member_datail.getString("login_month");//該生月登入次數存到LM_tmp
            		String LS_tmp = member_datail.getString("login_semester");//該生學期登入次數存到LS_tmp
            		if(PM_tmp == null ||PM_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
            			PM_tmp = "0";
            		}
            		if(PS_tmp == null ||PS_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
            			PS_tmp = "0";
            		}
            		if(EM_tmp == null ||EM_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
            			EM_tmp = "0";
            		}
            		if(ES_tmp == null ||ES_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
            			ES_tmp = "0";
            		}
            		if(LM_tmp == null ||LM_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
            			LM_tmp = "0";
            		}
            		if(LS_tmp == null ||LS_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
            			LS_tmp = "0";
            		}
            		classmonth = classmonth + Integer.parseInt(PM_tmp);//加總月積分
            		classsemester = classsemester + Integer.parseInt(PS_tmp);//加總學期積分
            		examtimes_month = examtimes_month + Integer.parseInt(EM_tmp);//加總月考試次數
            		examtimes_semester = examtimes_semester + Integer.parseInt(ES_tmp);//加總學期考試次數
            		login_month = login_month  + Integer.parseInt(LM_tmp);//加總學期登入次數
            		login_semester = login_semester + Integer.parseInt(LS_tmp);//加總學期登入次數
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
