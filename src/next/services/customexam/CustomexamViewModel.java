package next.services.customexam;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

import exam.service.Exam;
import login.services.Login;
import login.services.LoginService;
import login.services.LoginServiceImpl;
import next.services.customexam.Customexam;
import next.services.customexam.CustomexamService;
import next.services.customexam.CustomexamServiceImpl;
//�������D&�Ȼs�Ҩ�&�����ҥD��
public class CustomexamViewModel {
	private CustomexamService service = new CustomexamServiceImpl();
	private ListModel<Exam> CustomexamModel;
	private ListModel<Exam> SimulationexamModel;
	private ListModel<Exam> PastexamModel;
	private String exam_keyin;//�j�M����
	private int countpage;									//�p��ListModel�̭����X�D
    String[] Ary_right;										//zul�ǨӪ��ѵ��}�C
    String[] Ary_choice;									//zul�ǨӪ��ﵪ�}�C
    String[] Ary_explain;									//zul�ǨӪ��ԸѰ}�C
    String[] Ary_quizNo;									//zul�ǨӪ��D���}�C
    String[] Ary_quizPic;									//�D�عϤ��}�C(�Ȼs�Ҩ������ݸѵ���)
    String[] Ary_isright;									//�O�_����}�C
    String[] Ary_qcode;										//���D�N�X�}�C
    String qcode;											//���D�N�X�r��(�s�J��Ʈw��)
	String quizname;										//���D�W��
    int ActivePage;											//zul�ǨӪ��ϥΤ�����
    String Ary_quizcode;									//���D�N�X�r��
    String Ary_explainsert;
    String is_explain = "1";										//�O�_���ǥͬݸѵ�(�w�]��1,���ǥͬ�)
    int quiz_right = 0;										//����ļ�              
    String Correct_rate;									//���T�v
    String point;											//����
    String User_Acc;										//�ϥΪ̱b��
    String exam_start;										//�}�l�Ҹծɶ�
    String exam_exit;										//���}�Ҹծɶ�
    String spendtime;										//��O�ɶ�
    long diff;												//��O�ɶ�(�P�_������)
    String REMARK = "��";
    String UID;												//�Ըѭ���update��
    String Simulation_book;//�����Ҫ��U�O	
    String Simulation_ver;//�����Ҫ�����
    String sim_ver,sim_book;
	private ListModel<Exam> Ary_toexp;						//explain.zul�n�Ϊ�ListModel
	LoginService LoginService = new LoginServiceImpl();
	Login usridentity= LoginService.getUserCredential();
	Session s = Sessions.getCurrent(); 
	public void setexam_keyin(String exam_keyin) {//��J���Ҩ��W��
		this.exam_keyin = exam_keyin;
	}
	public String getexam_keyin() {//��J���Ҩ��W��
		return exam_keyin;
	}
	public ListModel<Exam>  getCustomexamModel() {
		if(s.getAttribute("custom_data") != null ){
			CustomexamModel = (ListModel<Exam>) s.getAttribute("custom_data");
			countpage = CustomexamModel.getSize();
			quizname = (String) s.getAttribute("custom_quizname");//���o�Ҩ��W��(��Custom_main.zul�ǹL��)
		}
		return CustomexamModel;
	}
	//�|�ҳ�@�~��
	public ListModel<Exam>  getPastexamModel() {
		//�p�G�������Dseeeion���Onull
		if(s.getAttribute("Past_year") != null ){	
			String booktmp = (String) s.getAttribute("Past_year");
			String[] searchtmp = booktmp.split(",");//�������D�~��,�W�ټȦs�r��
			sim_book = searchtmp[0];
			quizname = searchtmp[1];
		}
		PastexamModel = new ListModelList<Exam>(service.pastexam(sim_book));
		countpage = PastexamModel.getSize();
		return PastexamModel;
	}
	
	//�|�Ҽ�����
	public ListModel<Exam>  getPastexam_RModel() {
		//�p�G�������Dseeeion���Onull
		if(s.getAttribute("Past_year") != null){	
			quizname = (String) s.getAttribute("Past_year");
		}
		PastexamModel = new ListModelList<Exam>(service.pastexam_r());
		countpage = PastexamModel.getSize();
		return PastexamModel;
	}
	//��U������
	public ListModel<Exam>  getSimulationexamModel() {
		//�p�G�����Ҫ����P�����ҥU�O��session���Onull
		if(s.getAttribute("Simulation_book") != null && s.getAttribute("Simulation_ver") != null ){	
			String booktmp = (String) s.getAttribute("Simulation_book");
			String[] searchtmp = booktmp.split(",");//�����ҥU�O�Ȧs�r��
			sim_book = searchtmp[0];
			quizname = searchtmp[1];
			sim_ver = (String) s.getAttribute("Simulation_ver");
		}
		SimulationexamModel = new ListModelList<Exam>(service.simulationexam(sim_ver, sim_book));
		countpage = SimulationexamModel.getSize();
		return SimulationexamModel;
	}
	//�h�U������
	public ListModel<Exam>  getSimulationexam_RModel() {
		//�p�G�����Ҫ����P�����ҥU�O��session���Onull
		if(s.getAttribute("Simulation_book") != null && s.getAttribute("Simulation_ver") != null ){	
			String booktmp = (String) s.getAttribute("Simulation_book");
			String[] searchtmp = booktmp.split(",");//�����ҥU�O�Ȧs�r��
			sim_book = searchtmp[0];
			quizname = searchtmp[1];
			sim_ver = (String) s.getAttribute("Simulation_ver");
		}
		SimulationexamModel = new ListModelList<Exam>(service.simulationexam_r(sim_ver, sim_book));
		countpage = SimulationexamModel.getSize();
		return SimulationexamModel;
	}
	//�j�M�ը�,���ŦX���e�N��isession�ø���Ҹխ���
	@Command
	public void search_Customexam(){
		int isexist = service.checkexam(exam_keyin);//���ˬd�O�_���Ӹը�
		if(isexist == 0) {//�p�G�S���N���_
			Messagebox.show("�d�L�Ӹը�!", "����", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		CustomexamModel =  new ListModelList<Exam>(service.searchexam(exam_keyin));//�p�G���N�N�D�ض��CustomexamModel
		s.setAttribute("custom_quizname", exam_keyin);//�Nexam_keyin�]�w���W��custom_quizname��session
		s.setAttribute("custom_data", CustomexamModel);//�NCustomexamModel�]�w���W��custom_data��session
		Executions.sendRedirect("Custom/Custom_exam.zul");//�����Ҹխ���
	}
	//-------------------------�H�U�O�qexamviewmodel��L�Ӫ�-----------------------------------
	public ListModel<Exam> getAry_toexp() {					//custom_explain.zul�OŪ�o��
		
		if(s.getAttribute("Ary_from") != null ){		//�p�GAry_from�����(�N��ҧ���) �N�]�wAry_toexp
			Ary_toexp = (ListModel<Exam>) s.getAttribute("Ary_from");
		}
		return Ary_toexp;
	}
    //���o�Ը����}�ɶ��B�@�֧�s���Ʈw
    @Command
	public void get_exittime_exp() {
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat outtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_exit = outtime.format(dt);//���n���}�ɶ�
		Date in = null;
		Date out = null;
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
		Bonus_time();//�I�s���y�ɶ�function
		updatetime();//�N�ɶ���s���Ʈw
		Executions.sendRedirect("../exam_main.zul");
	}
    //�p����y�ɶ�
	public void Bonus_time() {
		double bouns = Double.parseDouble(point);
		if(diff>20000) {
			point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
		}
		else if(diff>10000) {
			point = String.valueOf(Math.round(bouns*0.1));//�W�L10��o������*0.1�����y
		}
		else {
			point = "0";
		}
	}
    //�p����y�ɶ�---�������D��
	public void Bonus_time_forpast() {
		double bouns = Double.parseDouble(point);
		if(diff>20000) {
			point = String.valueOf(Math.round(bouns *0.3)); //�W�L20��o������*0.3�����y //Math.round�|�ˤ��J
		}
		else if(diff>10000) {
			point = String.valueOf(Math.round(bouns*0.1));//�W�L10��o������*0.1�����y
		}
		else {
			point = "0";
		}
	}
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
	/**public static String accuracy(double num, double total, int scale){	//�p�⥿�T�v
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();	//�i�H�]�m��T�p��
		df.setMaximumFractionDigits(scale);								//�Ҧ� �Ҧp�|�ˤ��J
		df.setRoundingMode(RoundingMode.HALF_UP);
		double accuracy_num = num / total * 100;						//�p�⥿�T�v
		return df.format(accuracy_num);
	}**/
	public static String Calculate(double num, double total, int scale){	//�p�⥿�T�v
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();		//�i�H�]�m��T�p��
		df.setMaximumFractionDigits(scale);									//�Ҧ� �Ҧp�|�ˤ��J
		df.setRoundingMode(RoundingMode.HALF_UP);
		double accuracy_num = num / total * 100;
		double point = Math.round(accuracy_num*1.1);						//�p�����(�̷ӳW�h�����һP�������D����*1.1)
		return df.format(point);
	}
	
	public static String Calculate_forcustom(double num, double total, int scale){	//�p�⥿�T�v
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();		//�i�H�]�m��T�p��
		df.setMaximumFractionDigits(scale);									//�Ҧ� �Ҧp�|�ˤ��J
		df.setRoundingMode(RoundingMode.HALF_UP);
		double accuracy_num = num / total * 100;
		double point = Math.round(accuracy_num);						//�p�����(�ۭq�Ҩ��S�����ƥ[��)
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
            		+ "(SB_ACCOUNT, SB_POINT, SB_QUIZTOTAL ,SB_QUIZCORRECT,SB_QUIZNAME,SB_QUIZIN,SB_QUIZOUT,SB_ANSWERTIME,SB_REMARK,SB_Aryexplain,is_explain)"
            		+ " values(?,?,?,?,?,?,?,?,?,?,?)");
            stmt.setString(1, User_Acc);
            stmt.setString(2, point);
            stmt.setLong(3, countpage);
            stmt.setLong(4, quiz_right);
            stmt.setNString(5, quizname);//����r��Nstring������|�����D
            stmt.setString(6, exam_start);
            stmt.setString(7, exam_exit);   
            stmt.setNString(8, spendtime);//����r��Nstring������|�����D
            stmt.setNString(9, REMARK);//����r��Nstring������|�����D     
            stmt.setString(10, Ary_quizcode);
            stmt.setString(11, is_explain);        
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
	@Init
	//Init=Ū���@��,�b���J�o��ViewModel��,���osession����usr_account�ö�JUser_Acc	,�B���o�i�J�ɶ�exam_start
	public void init(@ContextParam(ContextType.SESSION) Session session){
		User_Acc = usridentity.getaccount();
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat intime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_start = intime.format(dt);
	}
    //���o�Ҹ����}�ɶ��åB�@�֭p���F�h�֮ɶ��@����
	public void get_exittime() {
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat outtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		exam_exit = outtime.format(dt);//���n���}�ɶ�
		Date in = null;
		Date out = null;
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
	//�O�_�ŦX�����з�,1000��1��
	public void is_correspond() {
		String booktmp = (String) s.getAttribute("Simulation_book");
		String[] searchtmp = booktmp.split(",");//�����ҥU�O�Ȧs�r��
		sim_book = searchtmp[0];
		//�p�G�O�@�U�B�G�U�B�T�U�B�@~�G�U�B�@~�T�U�B�@���ɶ��p�󤭤���
		if((sim_book.equals("jr1_1")||sim_book.equals("jr1_2")||sim_book.equals("jr2_1")||sim_book.equals("range_1_2")||sim_book.equals("range_1_3"))&&diff<300000) {
			REMARK = "��";
			point = "0";
		}
		else if(diff<600000) {//�p�G�p��10 ���� ������
			REMARK = "��";
			point = "0";
		}
	}
	//�O�_�ŦX�����з�,1000��1��(�������D��)
	public void is_correspond_forpast() {
		if(diff<600000) {//�p�G�p��10 ���� ������
			REMARK = "��";
			point = "0";
		}
	}
	//�O�_�ŦX�����з�,1000��1��(�Ȼs�Ҩ���)
	public void is_correspond_forcustom() {
		if(diff<45000) {
			REMARK = "��";
			point = "0";
		}
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
    	Ary_quizPic = new String[countpage];;
    	/**Ary_quizcode = new String[countpage];**/
    }	
	@Command
    //���ozul�ɶǨӪ��ҸզW��
	public void set_quizname(@BindingParam("paramx") String name){
		quizname = name;
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
    //���o���T�ѵ�
    public void set_rightans(@BindingParam("paramx") String right){
		for(int i = 0; i < Ary_right.length; i++) {
			Ary_right[ActivePage] = right;
		    }
	}
	@Command
    //���ozul�ɶǨӪ����D�N�X
	public void set_quizcode(@BindingParam("paramx") String code){
		Ary_quizcode = code;
    }
	@Command
    //���ozul�ɶǨӪ��Ϥ��N�X--�Ȼs�Ҩ������ݵ��ת��ԸѴ��N
	public void set_quizPic(@BindingParam("paramx") String pic){
		Ary_quizPic[ActivePage] = pic;
    }
	/**
	//���o�D�ظ��X---2018/05/11�s�W
	public void getQuiz_No() {
		for(int i = 0; i < Ary_quizNo.length; i++) {
			Ary_quizNo[i] =CustomexamModel.getElementAt(i).getQB_NO();
		    }
	}
	//���o�ԸѦ�m---2018/05/11��g
	public void getQuiz_explain() {
		for(int i = 0; i < Ary_explain.length; i++) {
			Ary_explain[i] =CustomexamModel.getElementAt(i).getQB_EXPLAIN();
		    }
	}
	//���o�D�إN�X---2018/05/11��g
	public void getQuiz_code() {
		for(int i = 0; i < Ary_qcode.length; i++) {
			Ary_qcode[i] =CustomexamModel.getElementAt(i).getQB_CODE();
		    }
		qcode = Arrays.toString(Ary_qcode);
	}
	//���o���T����---2018/05/11��g
	public void getrightans() {
		for(int i = 0; i < Ary_right.length; i++) {
			Ary_right[i] =CustomexamModel.getElementAt(i).getQB_ANSWER();
		    }
	}**/
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
	
	@Command
    //���
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
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//�I�s���}�ɶ���function
			is_correspond();//�ˬd�O�_�ŦX�����з�
			InsertScore();//�I�s�s�W��ƶi��Ʈw��function
			s.setAttribute("Ary_from", Ary_toexp);//�NAry_toexp�]�w���W��Ary_from��session
			s.setAttribute("point", point);//�Npoint�]�w���W��point��session
			s.setAttribute("total", countpage);//�Ncountpage�]�w���W��total��session
			s.setAttribute("right", quiz_right);//�Nquiz_right�]�w���W��right��session
			
			Executions.sendRedirect("Custom_explain.zul");//�����Ըѭ���
	}	
	@Command
    //�W��,�򥻤W�����@��,�t�b�|�Nnull�ɦ����@�����~��
	public void timeover(){
		 List<Exam> Examlist = new ArrayList<Exam>();
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
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}
			
			Ary_toexp = new ListModelList<Exam>(Examlist);//�N�}�C��imodellist�̭�
			//Correct_rate = accuracy(quiz_right,countpage,1);//�p�⥿�T�v
			point = Calculate(quiz_right,countpage,1);//�o��
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//�I�s���}�ɶ���function
			is_correspond();//�ˬd�O�_�ŦX�����з�
			InsertScore();//�I�s�s�W��ƶi��Ʈw��function
			s.setAttribute("Ary_from", Ary_toexp);//�NAry_toexp�]�w���W��Ary_from��session
			s.setAttribute("point", point);//�Npoint�]�w���W��point��session
			s.setAttribute("total", countpage);//�Ncountpage�]�w���W��total��session
			s.setAttribute("right", quiz_right);//�Nquiz_right�]�w���W��right��session
			Executions.sendRedirect("Custom_explain.zul");//�����Ըѭ���
	}
	//----------------------�H�U���������D�ϥ�,�I�s��function�P�������Ʒ|�������P
	@Command
    //���
	public void submit_quiz_forpast(){
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
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//�I�s���}�ɶ���function
			is_correspond_forpast();//�ˬd�O�_�ŦX�����з�(�������D�з�)
			InsertScore();//�I�s�s�W��ƶi��Ʈw��function
			s.setAttribute("Ary_from", Ary_toexp);//�NAry_toexp�]�w���W��Ary_from��session
			s.setAttribute("point", point);//�Npoint�]�w���W��point��session
			s.setAttribute("total", countpage);//�Ncountpage�]�w���W��total��session
			s.setAttribute("right", quiz_right);//�Nquiz_right�]�w���W��right��session
			
			Executions.sendRedirect("Custom_explain.zul");//�����Ըѭ���
	}	
	@Command
    //�W��,�򥻤W�����@��,�t�b�|�Nnull�ɦ����@�����~��
	public void timeover_forpast(){
		 List<Exam> Examlist = new ArrayList<Exam>();
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
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}
			
			Ary_toexp = new ListModelList<Exam>(Examlist);//�N�}�C��imodellist�̭�
			//Correct_rate = accuracy(quiz_right,countpage,1);//�p�⥿�T�v
			point = Calculate(quiz_right,countpage,1);//�o��
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//�I�s���}�ɶ���function
			is_correspond_forpast();//�ˬd�O�_�ŦX�����з�(�������D�з�)
			InsertScore();//�I�s�s�W��ƶi��Ʈw��function
			s.setAttribute("Ary_from", Ary_toexp);//�NAry_toexp�]�w���W��Ary_from��session
			s.setAttribute("point", point);//�Npoint�]�w���W��point��session
			s.setAttribute("total", countpage);//�Ncountpage�]�w���W��total��session
			s.setAttribute("right", quiz_right);//�Nquiz_right�]�w���W��right��session
			Executions.sendRedirect("Custom_explain.zul");//�����Ըѭ���
	}
	//----------------------�H�U���Ȼs�Ҹըϥ�,�I�s��function�P�������Ʒ|�������P
	@Command
    //���
	public void submit_quiz_forcustom(){
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
			point = Calculate_forcustom(quiz_right,countpage,1);//�o��
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//�I�s���}�ɶ���function
			is_correspond_forcustom();//�ˬd�O�_�ŦX�����з�(�Ȼs�Ҩ��з�)
			InsertScore();//�I�s�s�W��ƶi��Ʈw��function
			s.setAttribute("Ary_from", Ary_toexp);//�NAry_toexp�]�w���W��Ary_from��session
			s.setAttribute("point", point);//�Npoint�]�w���W��point��session
			s.setAttribute("total", countpage);//�Ncountpage�]�w���W��total��session
			s.setAttribute("right", quiz_right);//�Nquiz_right�]�w���W��right��session
			
			Executions.sendRedirect("Custom_explain.zul");//�����Ըѭ���
	}	
	@Command
    //�W��,�򥻤W�����@��,�t�b�|�Nnull�ɦ����@�����~��
	public void timeover_forcustom(){
		 List<Exam> Examlist = new ArrayList<Exam>();
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
				examsource.setQB_PIC(Ary_quizPic[i]);
				examsource.setQB_EXPLAIN(Ary_explain[i]);
				examsource.setQB_ANSWER(Ary_right[i]);
				examsource.setusr_choice(Ary_choice[i]);
				examsource.setis_right(Ary_isright[i]);
				Examlist.add(i, examsource);
			}
			is_explain = CustomexamModel.getElementAt(0).getQB_MAIN();//���i�Ҩ��O�_�൹�ǥͬݸԸ�,�P�Ѯv�]�w�ۦP
			Ary_toexp = new ListModelList<Exam>(Examlist);//�N�}�C��imodellist�̭�
			//Correct_rate = accuracy(quiz_right,countpage,1);//�p�⥿�T�v
			point = Calculate_forcustom(quiz_right,countpage,1);//�o��
			/**Ary_explainsert = Arrays.toString(Ary_quizcode);**/
			get_exittime();//�I�s���}�ɶ���function
			is_correspond_forcustom();//�ˬd�O�_�ŦX�����з�(�Ȼs�Ҩ��з�)
			InsertScore();//�I�s�s�W��ƶi��Ʈw��function
			s.setAttribute("Ary_from", Ary_toexp);//�NAry_toexp�]�w���W��Ary_from��session
			s.setAttribute("point", point);//�Npoint�]�w���W��point��session
			s.setAttribute("total", countpage);//�Ncountpage�]�w���W��total��session
			s.setAttribute("right", quiz_right);//�Nquiz_right�]�w���W��right��session
			if(CustomexamModel.getElementAt(0).getQB_MAIN().equals("1")) {//�p�G�o�i�Ҩ��൹�ǥͬݸԸ�
				Executions.sendRedirect("Custom_explain.zul");//�����Ըѭ���
			}
			else {
				Executions.sendRedirect("Custom_explain_none.zul");//����줣���ݸԸѪ�����
			}
			
	}
	
}
