package exam.service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;//���ɶ���
import java.util.Date;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.Messagebox;
import java.sql.*;//sql�M��
import javax.sql.DataSource;
import javax.naming.InitialContext;

import exam.service.Exam;
import exam.service.ExamService;
import exam.service.ExamServiceImpl;
import login.services.Login;
import login.services.LoginService;
import login.services.LoginServiceImpl;
//���D�\��D��
public class ExamViewModel {

	private ExamService Service = new ExamServiceImpl();	//Impl�ɮ�	
	private List<Exam> examlist; 							//list��Exam.java�]�w���@��
	private ListModel<Exam> examModel;						//exam.zul�n�Ϊ�ListModel
	private ListModel<Exam> Ary_toexp;						//explain.zul�n�Ϊ�ListModel
	private int countpage;									//�p��ListModel�̭����X�D
    String[] Ary_right;										//zul�ǨӪ��ѵ��}�C
    String[] Ary_choice;									//zul�ǨӪ��ﵪ�}�C
    String[] Ary_explain;									//zul�ǨӪ��ԸѰ}�C
    String[] Ary_quizNo;									//zul�ǨӪ��D���}�C
    String[] Ary_isright;									//�O�_����}�C
    String[] Ary_qcode;										//���D�N�X�}�C
    String qcode;											//���D�N�X�r��(�s�J��Ʈw��)
    //String Ary_quizcode;									//���D�N�X�r��
    int quiz_right = 0;										//�����D��     
    int ActivePage;											//zul�ǨӪ��ϥΤ�����
    String quizname;										//�ը��W��
    //String Correct_rate;									//���T�v
    String point;											//����
    String User_Acc;										//�ϥΪ̱b��
    String exam_start;										//�}�l�Ҹծɶ�
    String exam_exit;										//���}�Ҹծɶ�
    String spendtime;										//��O�ɶ�
    long diff;												//��O�ɶ�(�P�_������)
    String REMARK = "��";									//�O�_�������аO,�w�]����
    String UID;												//�Ըѭ���update��
    String book,ver,ch,se,lv,ifteacher = "false" ;//�o�O�D�طj�M�����ܼ�,�w�]���O�Ѯv
    Session s = Sessions.getCurrent(); 
    
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();//���o�n�J�ɪ���T
	
	public String getquizname() {//�ը��W��
		return quizname;
	}
	public void setquizname(String quizname) {//�ը��W��
		this.quizname = quizname;
	}	
	
	public ExamViewModel() {								//�o���{�Fzul�ɭn�����
			if(s.getAttribute("Ary_from") != null ){		//�p�GAry_from�����(�N��ҧ���) �N�]�wAry_toexp
				Ary_toexp = (ListModel<Exam>) s.getAttribute("Ary_from");
				countpage = Ary_toexp.getSize();
			}
	}	
	
	public ListModel<Exam> getexamModel_N() {					//exam_n.zul�OŪ�o��
		if(s.getAttribute("search_condition") != null ){
			String alltmp = (String) s.getAttribute("search_condition");//�qexample.zul�ǨӪ�,�pjr1_1,ni,1,1,e,�ƻP�ƽu(��)
			String[] searchtmp = alltmp.split(",");//�j�M�D�ؼȦs�r��
			book = searchtmp[0];//ex:jr1_1
			ver = searchtmp[1];//ex:ni
			ch = searchtmp[2];//ex:1
			se = searchtmp[3];//ex:1
			lv = searchtmp[4];//ex:e
			quizname =  searchtmp[5];//ex:�ƻP�ƽu(��)
		}
		if(se.equals("0")) {//�p�G�`�O�O0(�N���ܥ���) 
			examModel = new ListModelList<Exam>(Service.find_chose_A(book,ver,ch,lv,ifteacher,User_Acc));//�ΤW�z����h�j�M,�S�S�O�]�wifteacher�w�]false
		}
		else {//�̷ӷj�M����h��Ʈw�����
			examModel = new ListModelList<Exam>(Service.find_chose(book,ver,ch,se,lv,ifteacher,User_Acc));//�ΤW�z����h�j�M,�S�S�O�]�wifteacher�w�]false
		}
		countpage = examModel.getSize();
		return examModel;
	}
	public ListModel<Exam> getexamModel_T() {					//exam_t.zul�OŪ�o��(�Ѯv��)		
		if(s.getAttribute("search_condition") != null ){
			String alltmp = (String) s.getAttribute("search_condition");//�qexample.zul�ǨӪ�,�pjr1_1,ni,1,1,e,�ƻP�ƽu(��)
			String[] searchtmp = alltmp.split(",");//�j�M�D�ؼȦs�r��
			book = searchtmp[0];
			ver = searchtmp[1];
			ch = searchtmp[2];
			se = searchtmp[3];
			lv = searchtmp[4];
			quizname =  searchtmp[5];
			ifteacher = "true";
		}
		if(se.equals("0")) {//�p�G�`�O�O0(�N���ܥ���) 
			examModel = new ListModelList<Exam>(Service.find_chose_A(book,ver,ch,lv,ifteacher,User_Acc));//�ΤW�z����h�j�M,ifteacher�Q�]�w��true
		}
		else {//�̷ӷj�M����h��Ʈw�����
			examModel = new ListModelList<Exam>(Service.find_chose(book,ver,ch,se,lv,ifteacher,User_Acc));//�ΤW�z����h�j�M,ifteacher�Q�]�w��true
		}
		countpage = examModel.getSize();
		return examModel;
	}
	public ListModel<Exam> getAry_toexp() {					//explain.zul�OŪ�o��
		return Ary_toexp;
	}
	public List<Exam> getexamlist(){
		return examlist;
	}
	/**public static String accuracy(double num, double total, int scale){	//�p�⥿�T�v---�ȮɨS��
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();	//�i�H�]�m��T�p��
		df.setMaximumFractionDigits(scale);								//�Ҧ� �Ҧp�|�ˤ��J
		df.setRoundingMode(RoundingMode.HALF_UP);
		double accuracy_num = num / total * 100;						//�p�⥿�T�v
		return df.format(accuracy_num);
	}**/
	public static String Calculate(double num, double total, int scale){	//�p�����
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();		//�i�H�]�m��T�p��
		df.setMaximumFractionDigits(scale);									//�Ҧ� �Ҧp�|�ˤ��J
		df.setRoundingMode(RoundingMode.HALF_UP);
		double accuracy_num = num / total * 100;
		double point = Math.round(accuracy_num);
		return df.format(point);
	}
	//�N���Z�s�W���Ʈw--------
    public void InsertScore() 
    { 
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("INSERT INTO next_generation.score_base "
            		+ "(SB_ACCOUNT, SB_POINT, SB_QUIZTOTAL ,SB_QUIZCORRECT,SB_QUIZNAME,SB_QUIZIN,SB_QUIZOUT,SB_ANSWERTIME,SB_REMARK,SB_Aryexplain) "
            		+ "values(?,?,?,?,?,?,?,?,?,?)");
            stmt.setString(1, User_Acc);
            stmt.setString(2, point);
            stmt.setLong(3, countpage);
            stmt.setLong(4, quiz_right);
            stmt.setNString(5, quizname);//����r��Nstring������|�����D
            stmt.setString(6, exam_start);
            stmt.setString(7, exam_exit);   
            stmt.setNString(8, spendtime);//����r��Nstring������|�����D
            stmt.setNString(9, REMARK);//����r��Nstring������|�����D     
            stmt.setString(10, qcode); 
            //stmt.setString(10, Ary_quizcode);   
            stmt.executeUpdate();
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
    }
	//--------------------------------
    
	//�N�ݸѵ��ɶ���s���Ʈw--------
    public void updatetime() 
    { 
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("update next_generation.score_base set SB_QUIZEXPLAIN = ? ,SB_BONUS =? where SB_ID = ?");
            stmt.setNString(1, spendtime);//�ݸѵ��ɶ�
            stmt.setString(2, point);//���y��
            stmt.setNString(3, UID);//�������D�ؽs��
            stmt.executeUpdate();//����sql update�y�k
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
    }
	//--------------------------------
    //���o�̫�@��(�]�N�O��ҧ���)�ҸհO��SB_ID(�D��B�ߤ@)�P����------
    @Command
    public void getUid() 
    { 
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement("SELECT SB_ID,SB_POINT FROM next_generation.score_base where SB_ACCOUNT =? order by SB_ID DESC limit 1;");
            stmt.setNString(1, User_Acc);//����r��Nstring������|�����D
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
            	UID = resultSet.getString("SB_ID");
            	point = resultSet.getString("SB_POINT");
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
    }
    //------
    
    //���o�Ը����}�ɶ��B�@�֧�s���Ʈw
    @Command
	public void get_exittime_exp() {
		java.util.Date dt = new java.util.Date();//���o���}�ԸѪ��ɶ�
		java.text.SimpleDateFormat outtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_exit = outtime.format(dt);//���n���}�ɶ�
		Date in = null;
		Date out = null;
		try {
			in = outtime.parse(exam_start);//Init���ɫ���o��
			out = outtime.parse(exam_exit);
		} catch (ParseException e) {
		    e.printStackTrace();
		}   
		diff = out.getTime() - in.getTime();//�N���}�ɶ��P�i�J�ɶ����B��
		long seconds = diff / 1000 % 60;//���o��
		long minutes = TimeUnit.MILLISECONDS.toMinutes(diff); //���o��
		long hours = TimeUnit.MILLISECONDS.toHours(diff);//���o�p��	
		spendtime = hours+"��"+minutes+"��"+seconds+"��";
		Bonus_time();//�I�s���y�ɶ�function
		updatetime();//�N�ɶ���s���Ʈw
		Executions.sendRedirect("../exam_main.zul");
	}
    //�p����y�ɶ�-���P�_�U�O�A�P�_�ɶ��O�_�ŦX
	public void Bonus_time() {
		String alltmp = (String) s.getAttribute("search_condition");
		String[] searchtmp = alltmp.split(",");//�j�M�D�ؼȦs�r��
		book = searchtmp[0];
		ch = searchtmp[2];
		String decide = book+ch;//�U�O+���O
		double bouns = Double.parseDouble(point);
		quiz_right = (int) s.getAttribute("right");//�����D�ƻPright session�����۵�
		/**countpage = (int) s.getAttribute("total");**/
		int Wrong = countpage-quiz_right;//�����D�� - ����� = �����D��
		
		if(decide.equals("jr1_11")) {//�p�G�O�Ĥ@�U�Ĥ@��
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";//���y���Ƭ�30
				}
				else {
					point = "0";//���y���Ƭ�0
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*3.5*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";//���y���Ƭ�20
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*3.5*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�7���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_12")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*8*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*8*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�16���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_13")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*7*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�14���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_21")) {//�p�G�O�ĤG�U�Ĥ@��
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*7.5*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7.5*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�15���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_22")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*5.5*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*5.5*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�11���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_23")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*11*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*11*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�22���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_24")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*5*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*5*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�10���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr1_25")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*6.5*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*6.5*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�13���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_11")) {//�p�G�O�ĤT�U�Ĥ@��
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*7*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�14���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_12")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*6.5*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*6.5*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�13���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_13")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*7.5*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7.5*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�15���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_14")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*6*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*6*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�12���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L��Ʊo������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_21")) {//�p�G�O�ĥ|�U�Ĥ@��
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*7*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�14���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_22")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*7*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*7*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�14���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_23")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*11.5*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*11.5*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�23���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr2_24")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*16*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*16*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�32���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_11")) {//�p�G�O�Ĥ��U�Ĥ@��
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*15*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*15*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�30���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_12")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*12.5*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*12.5*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�25���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_13")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*14*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*14*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�28���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_21")) {//�p�G�O�Ĥ��U�Ĥ@��
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*10.5*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*10.5*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�21���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_22")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*8*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*8*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�16���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
		else if(decide.equals("jr3_23")) {
			if(Wrong==0) {//��������
				if(diff>120000) {//�ݸѵ��W�L2 ����
					point = "30";
				}
				else {
					point = "0";
				}
			}
			else if(bouns == 0) {//�s��
				if(diff>(10*6*1000)) {//�ݸѵ��W�L�ɶ�
					point = "20";
				}
				else {
					point = "0";
				}
				
			}
			else if(diff>=(Wrong*6*1000)) {//�ݸѵ��ɶ�>=�����D�ơ�(�̤�12���2)
				point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
			}
			else {
				point = "0";
			}
		}
	}
    //���o�Ҹ����}�ɶ��åB�@�֭p���F�h�֮ɶ��@����
	public void get_exittime() {
		java.util.Date dt = new java.util.Date();//���o���}��U���ɶ�
		java.text.SimpleDateFormat outtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_exit = outtime.format(dt);//���n���}�ɶ�
		Date in = null;//�i�J�ɶ�
		Date out = null;//���}�ɶ�
		try {
			in = outtime.parse(exam_start);
			out = outtime.parse(exam_exit);
		} catch (ParseException e) {
		    e.printStackTrace();
		}   
		diff = out.getTime() - in.getTime();//�N���}�ɶ��P�i�J�ɶ����B��
		long seconds = diff / 1000 % 60;//���o��
		long minutes = TimeUnit.MILLISECONDS.toMinutes(diff); //���o��
		long hours = TimeUnit.MILLISECONDS.toHours(diff);//���o�p��	
		spendtime = hours+"��"+minutes+"��"+seconds+"��";
	}
	//�̷ӥU�O�P�_�O�_�ŦX�����з�,1000��1��
	public void is_correspond() {
		String decide = book+ch;//�U�O+���O
		if(decide.equals("jr1_11")) {//�p�G�O�Ĥ@�U�Ĥ@��
			if(diff<70000) {//�̷ӭp���W�h �C�D�̤�7�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr1_12")) {
			if(diff<160000) {//�̷ӭp���W�h �C�D�̤�16�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr1_13")) {
			if(diff<140000) {//�̷ӭp���W�h �C�D�̤�14�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr1_21")) {//�p�G�O�ĤG�U�Ĥ@��
			if(diff<150000) {//�̷ӭp���W�h �C�D�̤�15�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr1_22")) {
			if(diff<110000) {//�̷ӭp���W�h �C�D�̤�11�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr1_23")) {
			if(diff<220000) {//�̷ӭp���W�h �C�D�̤�22�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr1_24")) {
			if(diff<100000) {//�̷ӭp���W�h �C�D�̤�10�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr1_25")) {
			if(diff<130000) {//�̷ӭp���W�h �C�D�̤�13�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr2_11")) {//�p�G�O�ĤT�U�Ĥ@��
			if(diff<140000) {//�̷ӭp���W�h �C�D�̤�14�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr2_12")) {
			if(diff<130000) {//�̷ӭp���W�h �C�D�̤�13�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr2_13")) {
			if(diff<150000) {//�̷ӭp���W�h �C�D�̤�15�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr2_14")) {
			if(diff<120000) {//�̷ӭp���W�h �C�D�̤�12�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr2_21")) {//�p�G�O�ĥ|�U�Ĥ@��
			if(diff<140000) {//�̷ӭp���W�h �C�D�̤�14�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr2_22")) {
			if(diff<140000) {//�̷ӭp���W�h �C�D�̤�14�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr2_23")) {
			if(diff<230000) {//�̷ӭp���W�h �C�D�̤�23�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr2_24")) {
			if(diff<320000) {//�̷ӭp���W�h �C�D�̤�32�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr3_11")) {//�p�G�O�Ĥ��U�Ĥ@��
			if(diff<300000) {//�̷ӭp���W�h �C�D�̤�30�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr3_12")) {
			if(diff<250000) {//�̷ӭp���W�h �C�D�̤�25�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr3_13")) {
			if(diff<280000) {//�̷ӭp���W�h �C�D�̤�28�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr3_21")) {//�p�G�O�Ĥ��U�Ĥ@��
			if(diff<210000) {//�̷ӭp���W�h �C�D�̤�21�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr3_22")) {
			if(diff<160000) {//�̷ӭp���W�h �C�D�̤�16�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
		else if(decide.equals("jr3_23")) {
			if(diff<120000) {//�̷ӭp���W�h �C�D�̤�12�� * 10�D
				REMARK = "��";
				point = "0";
			}
		}
	}
	@Init
	//Init=Ū���@��,�b���J�o��ViewModel��,���osession����usr_account�ö�JUser_Acc	,�B���o�i�J�ɶ�exam_start
	public void init(@ContextParam(ContextType.SESSION) Session session){
		User_Acc = usridentity.getaccount();
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat intime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_start = intime.format(dt);
	}
	//---�U���O����zul�ɶǨӰѼƪ�function
	//�}�C����,�N�}�C���׳]���P�`�D�Ƭ۵�
    @Command
    public void lenth(){
    	Ary_choice = new String[countpage];
    	Ary_right = new String[countpage];
    	Ary_explain = new String[countpage];
    	Ary_isright = new String[countpage];
    	Ary_quizNo = new String[countpage];
    	Ary_qcode = new String[countpage];
    }
	/**�H�U���ª��{���X,����zul��Ū����ƫ�^��
	(���bzul�ɷs�W�ݤ�����label
	<!-- <label value="@bind(each.QB_EXPLAIN)" onCreate="@command('set_explain', paramx=self.getValue())" visible="false"/><h:br/>-->	
	<!-- <label value="@bind(each.QB_ANSWER)"  onCreate="@command('set_rightans', paramx=self.getValue())"/><h:br/>	 -->
	<!-- <label value="@bind(each.QB_CODE)"  onCreate="@command('set_quizcode', paramx=self.getValue())" visible="false"/><h:br/>--> 	
	@Command
    //���o���T�ѵ�
    public void set_rightans(@BindingParam("paramx") String right){
		for(int i = 0; i < Ary_right.length; i++) {
			Ary_right[ActivePage] = right;
		    }
	}
	@Command
    //���o�ԸѦ�m
	public void set_explain(@BindingParam("paramx") String exp){
		String exp_buff = exp.replaceFirst("\\[", "").replaceFirst("\\]", "").replaceAll("\\s+", "");
		String[] exp_array = exp_buff.split(",");
		for(int i = 0; i < Ary_explain.length; i++) {
			Ary_explain[i] = exp_array[i];
		    }
	}
	@Command
    //���ozul�ɶǨӪ����D�W��
	public void set_quizcode(@BindingParam("paramx") String code){
		Ary_quizcode = code;
    }
    @Command
    //���ozul�ɶǨӪ��ҸզW��
	public void set_quizname(@BindingParam("paramx") String name){
		quizname = name;
    }
    **/
	//���o�D�ظ��X---2018/05/11�s�W
	public void getQuiz_No() {
		for(int i = 0; i < Ary_quizNo.length; i++) {
			Ary_quizNo[i] =examModel.getElementAt(i).getQB_NO();
		    }
	}
	//���o�ԸѦ�m---2018/05/11��g
	public void getQuiz_explain() {
		for(int i = 0; i < Ary_explain.length; i++) {
			Ary_explain[i] =examModel.getElementAt(i).getQB_EXPLAIN();
		    }
	}
	//���o�D�إN�X---2018/05/11��g
	public void getQuiz_code() {
		for(int i = 0; i < Ary_qcode.length; i++) {
			Ary_qcode[i] =examModel.getElementAt(i).getQB_CODE();
		    }
		qcode = Arrays.toString(Ary_qcode);
	}
	//���o���T����---2018/05/11��g
	public void getrightans() {
		for(int i = 0; i < Ary_right.length; i++) {
			Ary_right[i] =examModel.getElementAt(i).getQB_ANSWER();
		    }
	}
    @Command
    //���o�ϥΪ̿��
	public void set_choiceans(@BindingParam("paramx") String Ans){
		for(int i = 0; i < Ary_choice.length; i++) {
			Ary_choice[ActivePage] = Ans;
		    }
	}
	@Command
    //���ozul�ɶǨӪ��ثe���� 
	public void set_Actpage(@BindingParam("paramx") int page){
    	ActivePage = page;
    }
	//---�W���O����zul�ɶǨӰѼƪ�function
	
	/** 
	@Command
    //���-�ثe�S�ϥ�,�|�ˮ֬O�_�@������,�p�G����n�ϥΰO�o�n��s��P�W��function�ۦP
	public void submit_quiz(){
		 List<Exam> Examlist = new ArrayList<Exam>();
    		//�ˬd�D�ظ̭����S��null
			for(int i=0;i< Ary_choice.length;i++)//i�Y�p���D���`�� i++
			{
				if(Ary_choice[i] == null){ //�p�G���S�@�����N���p��
					Messagebox.show("�ݥ����@�������~�i���!", "����", Messagebox.OK, Messagebox.EXCLAMATION);
					return;//�����ܸ��X���ˬd����
				}
			}
			//�}�l�ˬd����
			for(int i=0;i< Ary_choice.length;i++)//i�Y�p���D���`�� i++
			{
					if(Ary_choice[i].equals(Ary_right[i])){//�r��۵�
						Ary_isright[i] = "���T";
						quiz_right++;//����N���T�D��+1
					}
					else {
						Ary_isright[i] = "���~";
					}
			}	
			//�N�}�C��illist�̭�
			for(int i=0;i< Ary_choice.length;i++)//i�Y�p���D���`�� i++
			{
				Exam examsource = new Exam();
				examsource.setQB_EXPLAIN(Ary_explain[i]);
				examsource.setQB_ANSWER(Ary_right[i]);
				examsource.setusr_choice(Ary_choice[i]);
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}
			Ary_toexp = new ListModelList<Exam>(Examlist);//�N�}�C��imodellist�̭�
			//Correct_rate = accuracy(quiz_right,countpage,1);//�p�⥿�T�v
			point = Calculate(quiz_right,countpage,1);//�o��
			get_exittime();//�I�s���}�ɶ���function
			is_correspond();//�ˬd�O�_�ŦX�����з�
			InsertScore();//�I�s�s�W��ƶi��Ʈw��function
			Session s = Sessions.getCurrent();//�]�wsession
			s.setAttribute("Ary_from", Ary_toexp);//�NAry_toexp�]�w���W��Ary_from��session
			s.setAttribute("point", point);//�Npoint�]�w���W��point��session
			s.setAttribute("total", countpage);//�Ncountpage�]�w���W��total��session
			s.setAttribute("right", quiz_right);//�Nquiz_right�]�w���W��right��session
			Executions.sendRedirect("explain.zul");//�����Ըѭ���
	}**/
	@Command
    //�W��,�򥻤W�����@��,�t�b�|�Nnull�ɦ����@�����~��
	public void timeover(){
		 List<Exam> Examlist = new ArrayList<Exam>();
			getQuiz_No();//���o�D��
			getQuiz_explain();//���o�Ը�
			getQuiz_code();//���o�D�إN�X
			getrightans();//���o���T�ѵ�
    		//�ˬd�D�ظ̭����S��null
			for(int i=0;i< Ary_choice.length;i++)//i�Y�p���D���`�� i++
			{
				if(Ary_choice[i] == null){ //�p�G���S�@�����N���p��
					Ary_choice[i] = "���@��";
				}
			}
			//�}�l�ˬd����
			for(int i=0;i< Ary_choice.length;i++)//i�Y�p���D���`�� i++
			{
					if(Ary_choice[i].equals(Ary_right[i])){//�r��۵�
						Ary_isright[i] = "���T";
						quiz_right++;//����N���T�D��+1
					}
					else {
						Ary_isright[i] = "���~";
					}
			}
			//�N�}�C��illist�̭�
			for(int i=0;i< Ary_choice.length;i++)//i�Y�p���D���`�� i++
			{
				Exam examsource = new Exam();
				examsource.setQB_EXPLAIN(Ary_explain[i]);
				examsource.setQB_ANSWER(Ary_right[i]);
				examsource.setusr_choice(Ary_choice[i]);
				examsource.setQB_NO(Ary_quizNo[i]);
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}
			Ary_toexp = new ListModelList<Exam>(Examlist);//�N�}�C��imodellist�̭�
			//Correct_rate = accuracy(quiz_right,countpage,1);//�p�⥿�T�v
			point = Calculate(quiz_right,countpage,1);//�o��
			get_exittime();//�I�s���}�ɶ���function
			is_correspond();//�ˬd�O�_�ŦX�����з�
			InsertScore();//�I�s�s�W��ƶi��Ʈw��function
			Session s = Sessions.getCurrent();//�]�wsession
			s.setAttribute("Ary_from", Ary_toexp);//�NAry_toexp�]�w���W��Ary_from��session
			s.setAttribute("point", point);//�Npoint�]�w���W��point��session
			s.setAttribute("total", countpage);//�Ncountpage�]�w���W��total��session
			s.setAttribute("right", quiz_right);//�Nquiz_right�]�w���W��right��session
			Executions.sendRedirect("explain.zul");//�����Ըѭ���
	}	
	//------------------------
}
