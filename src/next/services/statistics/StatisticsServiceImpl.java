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
	int isrun = 0;//是否有跑過
	int totalscore_m = 0; //全部班級加總分數(月份)	
	int totalscore_se = 0; //全部班級加總分數(學期)
	int totalmember = 0;	//全部班級人數加總
	int totallogin_m = 0;	//本月登入人數加總
	int totalexam_m = 0;	//本月考試次數加總
	int totalexam_se = 0;	//學期考試次數加總
	
	//如果不是全年級且不是期間(選擇7~9,單一日期)
	@Override
	public List<Statistics> getdetail_1(String selectgrade, Date single_m) {
		List<Statistics> getclass =  new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        String singlemonth;//單月日期
		String StartMonth;//學期開始月份
		String EndMonth;//學期結束月份
		String classNO_tmp = null;//班級代號暫存字串
		String classNO_tmp_2 = "";//班級代號暫存字串
		String membertmp = null;
		int theMonth;
		int theYear;
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//設定格式yyyy-MM
		java.text.SimpleDateFormat getmonth = new java.text.SimpleDateFormat("MM");//設定格式MM
		java.text.SimpleDateFormat getyear = new java.text.SimpleDateFormat("yyyy");//設定格式yyyy
		singlemonth =  getyymm.format(single_m);//將傳來的時間切成yyyy-MM 
		theMonth = Integer.parseInt(getmonth.format(single_m));//取得該月份以用來判斷
		theYear = Integer.parseInt(getyear.format(single_m));//取得該年份以用來判斷
		
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
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_NO FROM next_generation.class_data where CD_GRADE = ?");//取班級代號
            stmt.setNString(1,selectgrade);//設定第一個?條件為selectgrade
            ResultSet classnotSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
            while (classnotSet.next()) {
            	classNO_tmp = classnotSet.getString("CD_NO");
            	classNO_tmp_2 = classNO_tmp_2 + classNO_tmp + ",";
            }
            String classNO_tmp_3 = classNO_tmp_2.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除classNO_tmp_2裡的[]跟所有空白
            String[] classno = classNO_tmp_3.split(",");//取得班級代號陣列
            Statistics cls_Statistics;
            for(int i = 0; i < classno.length; i++) {
            	stmt = conn.prepareStatement("SELECT CD_MEMBER FROM next_generation.class_data where CD_NO = ?");//取班級成員
            	stmt.setNString(1, classno[i]);//設定第一個?條件為classno[i]
            	ResultSet member_rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
                int classmonth = 0;//班級月積分
                int classsemester = 0;//班級學期積分
                int islogin_m =0;//本月使用人數
                int exam_m = 0;//本月考試次數
                int exam_sem =0 ;//學期考試次數
                int marktmp = 0;
                while (member_rs.next()) {
                	membertmp = member_rs.getString("CD_MEMBER");//把CD_MEMBER丟進membertmp
                	
                    String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除membertmp裡的[]跟所有空白
                    String[] classmember = membertmp_2.split(",");//取得班級成員陣列
                    for(int i2 = 0; i2 < classmember.length; i2++) {
                    	stmt = conn.prepareStatement("SELECT "
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as exam_month,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as Point_month,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as Point_semester,"
                    			+ "(SELECT (CASE when LD_FREQUENCY is not null then '1' ELSE '0' end ) as log_m from next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") = ? and LD_ACCOUNT = ?) as login_m,"
                    			//+ "(SELECT (CASE when LAST_LOGIN like ? then '1' ELSE '0' end ) as log_m from next_generation.sys_data where SYS_ACCOUNT = ?) as login_m,"
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? ) as exam_sem");
                    	//第一個撈當月考試次數,第二個撈當月分數,第三個撈學期分數,第四個撈當月是否有使用
                    	stmt.setNString(1, classmember[i2]);//設定第一個?條件為classmember[i]
                    	stmt.setNString(2, singlemonth+"%");//設定第二個?條件為singlemonth%
                    	stmt.setNString(3, classmember[i2]);//設定第三個?條件為classmember[i]
                    	stmt.setNString(4, singlemonth+"%");//設定第四個?條件為singlemonth%
                    	stmt.setNString(5, StartMonth);//設定第五個?條件為StartMonth
                    	stmt.setNString(6, EndMonth);//設定第六個?條件為EndMonth
                    	stmt.setNString(7, classmember[i2]);//設定第七個?條件為classmember[i]
                    	stmt.setNString(8, singlemonth);//設定第八個?條件為singlemonth%
                    	stmt.setNString(9, classmember[i2]);//設定第九個?條件為classmember[i]
                    	stmt.setNString(10, classmember[i2]);//設定第十個?條件為classmember[i]
                    	stmt.setNString(11, StartMonth);//設定第十一個?條件為StartMonth
                    	stmt.setNString(12, EndMonth);//設定第十二個?條件為EndMonth
                    	ResultSet member_datail = stmt.executeQuery();//執行查詢並將結果塞進member_datail
                    	while (member_datail.next()) {
                    		String PM_tmp = member_datail.getString("Point_month");//該生月積分存到PM_tmp
                    		String PS_tmp = member_datail.getString("Point_semester");//該生學期積分存到PS_tmp
                    		String lm_tmp = member_datail.getString("login_m");
                    		String em_tmp = member_datail.getString("exam_month");
                    		String es_tmp = member_datail.getString("exam_sem");
                    		if(PM_tmp == null ||PM_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			PM_tmp = "0";
                    		}
                    		if(PS_tmp == null ||PS_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			PS_tmp = "0";
                    		}
                    		if(lm_tmp == null ||lm_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			lm_tmp = "0";
                    		}
                    		if(em_tmp == null ||em_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			em_tmp = "0";
                    		}
                    		if(es_tmp == null ||es_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			es_tmp = "0";
                    		}
                    		classmonth = classmonth + Integer.parseInt(PM_tmp);//加總月積分
                    		classsemester = classsemester + Integer.parseInt(PS_tmp);//加總學期積分
                    		islogin_m = islogin_m + Integer.parseInt(lm_tmp);//本月是否有登入(使用人數)
                    		exam_m = exam_m + Integer.parseInt(em_tmp);//加總本月考試
                    		exam_sem = exam_sem + Integer.parseInt(es_tmp);//加總學期考試
                    		marktmp = marktmp +i2;//判斷有沒有跑此段的數字

                    	}
                    }
                    if(marktmp > 0) {//代表有跑過上面的	
                    	cls_Statistics = new Statistics();
                    	totalscore_m = totalscore_m + classmonth;//月份分數加總
                		totalscore_se = totalscore_se + classsemester;//學期分數加總
                		totalmember = totalmember + classmember.length;//所有人數加總
                		totallogin_m = totallogin_m + islogin_m;//本月登入人數加總
                		totalexam_m = totalexam_m + exam_m;//本月考試次數加總
                		totalexam_se = totalexam_se + exam_sem;//學期考試次數加總
                		isrun = marktmp;
                    	double clsme = (double)islogin_m/(double)classmember.length * 100;
                    	String Utilization = clsme +"%";//使用率
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
	//如果不是全年級且不是單一日期(選擇7~9,期間)
	@Override
	public List<Statistics> getdetail_2(String selectgrade, Date start_m, Date end_m) {
		List<Statistics> getclass =  new ArrayList<>();
		String StartDate;//學期開始月份
		String EndDate;//學期結束月份
		String classNO_tmp = null;//班級代號暫存字串
		String classNO_tmp_2 = "";//班級代號暫存字串
		String membertmp = null;		
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//設定格式yyyy-MM
		StartDate =  getyymm.format(start_m);//將傳來的時間切成yyyy-MM 
		EndDate =  getyymm.format(end_m);//將傳來的時間切成yyyy-MM
		long daydiff = (end_m.getTime()-start_m.getTime())/((24*60*60*1000));//相差日期(結束-開始)/24小時*60分*60秒*1000(秒)
		long mondiff = daydiff /30;//相差月份 (相差日期)/30天
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_NO FROM next_generation.class_data where CD_GRADE = ?");//取班級代號
            stmt.setNString(1,selectgrade);//設定第一個?條件為selectgrade
            ResultSet classnotSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
            while (classnotSet.next()) {
            	classNO_tmp = classnotSet.getString("CD_NO");
            	classNO_tmp_2 = classNO_tmp_2 + classNO_tmp + ",";
            }
            String classNO_tmp_3 = classNO_tmp_2.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除classNO_tmp_2裡的[]跟所有空白
            String[] classno = classNO_tmp_3.split(",");//取得班級代號陣列
            Statistics cls_Statistics;
            for(int i = 0; i < classno.length; i++) {
            	stmt = conn.prepareStatement("SELECT CD_MEMBER FROM next_generation.class_data where CD_NO = ?");//取班級成員
            	stmt.setNString(1, classno[i]);//設定第一個?條件為classno[i]
            	ResultSet member_rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
                int classsemester = 0;//班級學期積分
                int exam_sem =0 ;//學期考試次數
                int clslogintimes = 0;//班級登入次數
                int marktmp = 0;
                while (member_rs.next()) {
                	membertmp = member_rs.getString("CD_MEMBER");//把CD_MEMBER丟進membertmp
                    String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除membertmp裡的[]跟所有空白
                    String[] classmember = membertmp_2.split(",");//取得班級成員陣列
                    for(int i2 = 0; i2 < classmember.length; i2++) {
                    	stmt = conn.prepareStatement("SELECT "
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? ) as exam_sem,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as Point_semester,"
                    			+ "(SELECT sum(LD_FREQUENCY) FROM next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") between ? and ? and LD_ACCOUNT = ?)as login_times");
                    	stmt.setNString(1, classmember[i2]);//設定第1個?條件為classmember[i]
                    	stmt.setNString(2, StartDate);//設定第2個?條件為StartDate
                    	stmt.setNString(3, EndDate);//設定第3個?條件為EndDate
                    	stmt.setNString(4, StartDate);//設定第4個?條件為StartDate
                    	stmt.setNString(5, EndDate);//設定第5個?條件為EndDate
                    	stmt.setNString(6, classmember[i2]);//設定第6個?條件為classmember[i]
                    	stmt.setNString(7, StartDate);//設定第7個?條件為StartDate
                    	stmt.setNString(8, EndDate);//設定第8個?條件為EndDate
                    	stmt.setNString(9, classmember[i2]);//設定第9個?條件為classmember[i]
                    	
                    	ResultSet member_datail = stmt.executeQuery();//執行查詢並將結果塞進member_datail
                    	while (member_datail.next()) {
                    		String PS_tmp = member_datail.getString("Point_semester");//該生學期積分存到PS_tmp
                    		String es_tmp = member_datail.getString("exam_sem");//期間考試次數
                    		String lt_tmp = member_datail.getString("login_times");//期間登入次數
                    		if(PS_tmp == null ||PS_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			PS_tmp = "0";
                    		}
                    		if(es_tmp == null ||es_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			es_tmp = "0";
                    		}
                    		if(lt_tmp == null ||lt_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			lt_tmp = "0";
                    		}
                    		classsemester = classsemester + Integer.parseInt(PS_tmp);//加總學期積分
                    		exam_sem = exam_sem + Integer.parseInt(es_tmp);//加總學期考試
                    		clslogintimes = clslogintimes +  Integer.parseInt(lt_tmp);//加總登入次數
                    		marktmp = marktmp +i2;//判斷有沒有跑此段的數字
                    	}
                    }
                    if(marktmp > 0) {//代表有跑過上面的	
                    	cls_Statistics = new Statistics();
                    	if(mondiff == 0) {//如果是0,代表同月份,等於一個月
                    		mondiff = 1;
                    	}
                    	long avg_exam = exam_sem/mondiff;//平均考試
                    	long avg_point = classsemester/mondiff;//平均分數
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
	//如果是全年級且不是期間(選擇全年級,單一日期)
	@Override
	public List<Statistics> getdetail_3(String selectgrade, Date single_m) {
		List<Statistics> getclass =  new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        String singlemonth;//單月日期
		String StartMonth;//學期開始月份
		String EndMonth;//學期結束月份
		String classNO_tmp = null;//班級代號暫存字串
		String classNO_tmp_2 = "";//班級代號暫存字串
		String membertmp = null;
		int theMonth;
		int theYear;
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//設定格式yyyy-MM
		java.text.SimpleDateFormat getmonth = new java.text.SimpleDateFormat("MM");//設定格式MM
		java.text.SimpleDateFormat getyear = new java.text.SimpleDateFormat("yyyy");//設定格式yyyy
		singlemonth =  getyymm.format(single_m);//將傳來的時間切成yyyy-MM 
		theMonth = Integer.parseInt(getmonth.format(single_m));//取得該月份以用來判斷
		theYear = Integer.parseInt(getyear.format(single_m));//取得該年份以用來判斷
		
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
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_NO FROM next_generation.class_data");//取班級代號(有多重學校時,加入CD_SCHOOL條件)
            ResultSet classnotSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
            while (classnotSet.next()) {
            	classNO_tmp = classnotSet.getString("CD_NO");
            	classNO_tmp_2 = classNO_tmp_2 + classNO_tmp + ",";
            }
            String classNO_tmp_3 = classNO_tmp_2.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除classNO_tmp_2裡的[]跟所有空白
            String[] classno = classNO_tmp_3.split(",");//取得班級代號陣列
            Statistics cls_Statistics;
            for(int i = 0; i < classno.length; i++) {
            	stmt = conn.prepareStatement("SELECT CD_MEMBER FROM next_generation.class_data where CD_NO = ?");//取班級成員
            	stmt.setNString(1, classno[i]);//設定第一個?條件為classno[i]
            	ResultSet member_rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
                int classmonth = 0;//班級月積分
                int classsemester = 0;//班級學期積分
                int islogin_m =0;//本月使用人數
                int exam_m = 0;//本月考試次數
                int exam_sem =0 ;//學期考試次數
                int marktmp = 0;
                while (member_rs.next()) {
                	membertmp = member_rs.getString("CD_MEMBER");//把CD_MEMBER丟進membertmp
                	
                    String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除membertmp裡的[]跟所有空白
                    String[] classmember = membertmp_2.split(",");//取得班級成員陣列
                    for(int i2 = 0; i2 < classmember.length; i2++) {
                    	stmt = conn.prepareStatement("SELECT "
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as exam_month,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where SB_ACCOUNT = ? and SB_QUIZIN like ?) as Point_month,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as Point_semester,"
                    			+ "(SELECT (CASE when LD_FREQUENCY is not null then '1' ELSE '0' end ) as log_m from next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") = ? and LD_ACCOUNT = ?) as login_m,"
                    			//+ "(SELECT (CASE when LAST_LOGIN like ? then '1' ELSE '0' end ) as log_m from next_generation.sys_data where SYS_ACCOUNT = ?) as login_m,"
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? ) as exam_sem");
                    	//第一個撈當月考試次數,第二個撈當月分數,第三個撈學期分數,第四個撈當月是否有使用,第五個撈學期考試次數
                    	stmt.setNString(1, classmember[i2]);//設定第一個?條件為classmember[i]
                    	stmt.setNString(2, singlemonth+"%");//設定第二個?條件為singlemonth%
                    	stmt.setNString(3, classmember[i2]);//設定第三個?條件為classmember[i]
                    	stmt.setNString(4, singlemonth+"%");//設定第四個?條件為singlemonth%
                    	stmt.setNString(5, StartMonth);//設定第五個?條件為StartMonth
                    	stmt.setNString(6, EndMonth);//設定第六個?條件為EndMonth
                    	stmt.setNString(7, classmember[i2]);//設定第七個?條件為classmember[i]
                    	stmt.setNString(8, singlemonth);//設定第八個?條件為singlemonth%
                    	stmt.setNString(9, classmember[i2]);//設定第九個?條件為classmember[i]
                    	stmt.setNString(10, classmember[i2]);//設定第十個?條件為classmember[i]
                    	stmt.setNString(11, StartMonth);//設定第十一個?條件為StartMonth
                    	stmt.setNString(12, EndMonth);//設定第十二個?條件為EndMonth
                    	ResultSet member_datail = stmt.executeQuery();//執行查詢並將結果塞進member_datail
                    	while (member_datail.next()) {
                    		String PM_tmp = member_datail.getString("Point_month");//該生月積分存到PM_tmp
                    		String PS_tmp = member_datail.getString("Point_semester");//該生學期積分存到PS_tmp
                    		String lm_tmp = member_datail.getString("login_m");
                    		String em_tmp = member_datail.getString("exam_month");
                    		String es_tmp = member_datail.getString("exam_sem");
                    		if(PM_tmp == null ||PM_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			PM_tmp = "0";
                    		}
                    		if(PS_tmp == null ||PS_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			PS_tmp = "0";
                    		}
                    		if(lm_tmp == null ||lm_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			lm_tmp = "0";
                    		}
                    		if(em_tmp == null ||em_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			em_tmp = "0";
                    		}
                    		if(es_tmp == null ||es_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			es_tmp = "0";
                    		}
                    		classmonth = classmonth + Integer.parseInt(PM_tmp);//加總月積分
                    		classsemester = classsemester + Integer.parseInt(PS_tmp);//加總學期積分
                    		islogin_m = islogin_m + Integer.parseInt(lm_tmp);//本月是否有登入(使用人數)
                    		exam_m = exam_m + Integer.parseInt(em_tmp);//加總本月考試
                    		exam_sem = exam_sem + Integer.parseInt(es_tmp);//加總學期考試
                    		marktmp = marktmp +i2;//判斷有沒有跑此段的數字
                    	}
                    }
                    if(marktmp > 0) {//代表有跑過上面的	
                    	cls_Statistics = new Statistics();
                    	totalscore_m = totalscore_m + classmonth;//月份分數加總
                		totalscore_se = totalscore_se + classsemester;//學期分數加總
                		totalmember = totalmember + classmember.length;//所有人數加總
                		totallogin_m = totallogin_m + islogin_m;//本月登入人數加總
                		totalexam_m = totalexam_m + exam_m;//本月考試次數加總
                		totalexam_se = totalexam_se + exam_sem;//學期考試次數加總
                		isrun = marktmp;
                    	double clsme = (double)islogin_m/(double)classmember.length * 100;
                    	String Utilization = clsme +"%";//使用率
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
	//如果是全年級且是期間(選擇全年級,期間)
	@Override
	public List<Statistics> getdetail_4(String selectgrade, Date start_m, Date end_m) {
		List<Statistics> getclass =  new ArrayList<>();
		String StartDate;//學期開始月份
		String EndDate;//學期結束月份
		String classNO_tmp = null;//班級代號暫存字串
		String classNO_tmp_2 = "";//班級代號暫存字串
		String membertmp = null;		
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//設定格式yyyy-MM
		StartDate =  getyymm.format(start_m);//將傳來的時間切成yyyy-MM 
		EndDate =  getyymm.format(end_m);//將傳來的時間切成yyyy-MM
		long daydiff = (end_m.getTime()-start_m.getTime())/((24*60*60*1000));//相差日期(結束-開始)/24小時*60分*60秒*1000(秒)
		long mondiff = daydiff /30;//相差月份 = 相差日期/30天
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT CD_NO FROM next_generation.class_data");//取班級代號(有多重學校時,加入CD_SCHOOL條件)
            ResultSet classnotSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
            while (classnotSet.next()) {
            	classNO_tmp = classnotSet.getString("CD_NO");
            	classNO_tmp_2 = classNO_tmp_2 + classNO_tmp + ",";
            }
            String classNO_tmp_3 = classNO_tmp_2.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除classNO_tmp_2裡的[]跟所有空白
            String[] classno = classNO_tmp_3.split(",");//取得班級代號陣列
            Statistics cls_Statistics;
            for(int i = 0; i < classno.length; i++) {
            	stmt = conn.prepareStatement("SELECT CD_MEMBER FROM next_generation.class_data where CD_NO = ?");//取班級成員
            	stmt.setNString(1, classno[i]);//設定第一個?條件為classno[i]
            	ResultSet member_rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
                int classsemester = 0;//班級學期積分
                int clslogintimes = 0;//班級登入次數
                int exam_sem =0 ;//學期考試次數
                int marktmp = 0;
                while (member_rs.next()) {
                	membertmp = member_rs.getString("CD_MEMBER");//把CD_MEMBER丟進membertmp
                	
                    String membertmp_2 = membertmp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");//去除membertmp裡的[]跟所有空白
                    String[] classmember = membertmp_2.split(",");//取得班級成員陣列
                    for(int i2 = 0; i2 < classmember.length; i2++) {
                    	stmt = conn.prepareStatement("SELECT "
                    			+ "(SELECT count(SB_QUIZOUT) FROM next_generation.score_base where SB_ACCOUNT = ? and DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? ) as exam_sem,"
                    			+ "(SELECT sum(SB_BONUS)+sum(SB_POINT) FROM next_generation.score_base where DATE_FORMAT(SB_QUIZIN,\"%Y-%m\") between ? and ? and SB_ACCOUNT =?) as Point_semester,"
                    			+ "(SELECT sum(LD_FREQUENCY) FROM next_generation.login_data where DATE_FORMAT(LD_MONTH,\"%Y-%m\") between ? and ? and LD_ACCOUNT = ?)as login_times");
                    	stmt.setNString(1, classmember[i2]);//設定第1個?條件為classmember[i]
                    	stmt.setNString(2, StartDate);//設定第2個?條件為StartDate
                    	stmt.setNString(3, EndDate);//設定第3個?條件為EndDate
                    	stmt.setNString(4, StartDate);//設定第4個?條件為StartDate
                    	stmt.setNString(5, EndDate);//設定第5個?條件為EndDate
                    	stmt.setNString(6, classmember[i2]);//設定第6個?條件為classmember[i]
                    	stmt.setNString(7, StartDate);//設定第7個?條件為StartDate
                    	stmt.setNString(8, EndDate);//設定第8個?條件為EndDate
                    	stmt.setNString(9, classmember[i2]);//設定第9個?條件為classmember[i]
                    	ResultSet member_datail = stmt.executeQuery();//執行查詢並將結果塞進member_datail
                    	while (member_datail.next()) {
                    		String PS_tmp = member_datail.getString("Point_semester");//該生學期積分存到PS_tmp
                    		String es_tmp = member_datail.getString("exam_sem");//期間考試次數
                    		String lt_tmp = member_datail.getString("login_times");//期間登入次數
                    		if(PS_tmp == null ||PS_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			PS_tmp = "0";
                    		}
                    		if(es_tmp == null ||es_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			es_tmp = "0";
                    		}
                    		if(lt_tmp == null ||lt_tmp == "" ) {//如果是null或""指定為0,避免下面轉成int出錯
                    			lt_tmp = "0";
                    		}
                    		classsemester = classsemester + Integer.parseInt(PS_tmp);//加總學期積分
                    		exam_sem = exam_sem + Integer.parseInt(es_tmp);//加總學期考試
                    		clslogintimes = clslogintimes +  Integer.parseInt(lt_tmp);//加總登入次數
                    		marktmp = marktmp +i2;//判斷有沒有跑此段的數字
                    	}
                    }
                    if(marktmp > 0) {//代表有跑過上面的	
                    	cls_Statistics = new Statistics();
                    	if(mondiff == 0) {//如果是0,代表同月份,等於一個月
                    		mondiff = 1;
                    	}
                    	long avg_exam = exam_sem/mondiff;//平均考試
                    	long avg_point = classsemester/mondiff;//平均分數
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
	
	//取得單一月份分數
	@Override
	public List<Statistics> getScoretotal_1() {
		List<Statistics> getscore =  new ArrayList<>();
		Statistics scoretotal;
		if (isrun>0) {
			scoretotal = new Statistics();
			double clsme = (double)totallogin_m/(double)totalmember * 100;
			String Utilization = clsme +"%";//使用率
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
