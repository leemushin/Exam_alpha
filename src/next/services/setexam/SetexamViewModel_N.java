package next.services.setexam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

import next.services.bindquiz.Bindquiz;
import  next.services.setexam.Setexam;;

public class SetexamViewModel_N {
	
	private SetexamService service = new SetexamServiceImpl();
	private ListModelList<Bindquiz> leftModel;
	private ListModelList<Bindquiz> rightModel;
	private ListModelList<Bindquiz> detail_rightModel;
	private ListModelList<Setexam> EditModel;
    private String QB_VERSION = "ni";//�D�ت���
    private String QB_LEVEL = "e";//�D������
    private String QB_BOOK = "jr1_1";//�D�إU�O
    private int QB_CHAPTER = 1;//�D�س��O
    private int QB_SECTION = 1;//�D�ظ`�O
    private int is_explain = 1;//�O�_���ǥͬݸѵ�
    private int random_num = 1;//�H���D��
	Set<String> selection1;
	List<String> list1;
	Set<String> selection2;
	List<String> list2;
	String examtmp;//�k��w�p�X�D���C��
	Session s = Sessions.getCurrent(); 
	String examcode="";//�Ҩ��N�X
	String exammark="";//�Ҩ��Ƶ�
	int right_lenth;//�X�D���ƶq
	
	public int getright_lenth() {
		return right_lenth;
	}
	public void setright_lenth(int right_lenth) {
		this.right_lenth = right_lenth;
	}	
	public void setexamcode(String examcode) {
		this.examcode = examcode;
	}
	public String getexamcode() {
		return examcode;
	}
	public void setexammark(String exammark) {
		this.exammark = exammark;
	}
	public String getexammark() {
		if(s.getAttribute("custom_EB_NAME") != null ){	
			exammark = (String) s.getAttribute("custom_EB_NAME");//�p�G����,�N��qsetexam_edit�I�ԲӸ��
		}
		return exammark;
	}
	//���J�o��ViewModel���ɭԷ|Ū����
	public SetexamViewModel_N(){
		//leftModel = new ListModelList<Bindquiz>(service.searchquiz(QB_VERSION,QB_LEVEL,QB_BOOK,QB_CHAPTER,QB_SECTION));
		leftModel = new ListModelList<Bindquiz>();
		rightModel = new ListModelList<Bindquiz>();
		selection1 = new HashSet<String>();
		selection2 = new HashSet<String>();
		  ((ListModelList<Bindquiz>)leftModel).setMultiple(true);
	}

	public ListModelList<Bindquiz>  getleftModel() {
		return leftModel;
	}

	public ListModelList<Bindquiz>  getrightModel() {
		return rightModel;
	}
	public ListModelList<Bindquiz>  getdetail_rightModel() {//�s��ը����ɭԥk�䪺�w�]model
		if(s.getAttribute("custom_detail") != null ){		//�p�GCla_detail�����  �N�]�wClassdetailModel
			detail_rightModel = (ListModelList<Bindquiz>) s.getAttribute("custom_detail");
		}
		return detail_rightModel;
	}
	public ListModelList<Setexam>  getEditModel() {//setexam_edit.zul Ū�o��
		EditModel = new ListModelList<Setexam> (service.customlist());//�j�M�����Ҩ�
		return EditModel;
	}
	public Set<String> getSelection1() {
		return selection1;
	}

	public void setSelection1(Set<String> selection1) {
		this.selection1 = selection1;
	}

	public List<String> getList1() {
		return list1;
	}

	public Set<String> getSelection2() {
		return selection2;
	}

	public void setSelection2(Set<String> selection2) {
		this.selection2 = selection2;
	}

	public List<String> getList2() {
		return list2;
	}
	//���o��@�Ҩ��ԲӤ��esetexam_edit.zul�� ��ܦҨ���
	@Command
	public void get_examdetail(@BindingParam("paramx") String EB_NO) {
		detail_rightModel = new ListModelList<Bindquiz>(service.searchdetail(EB_NO));
		s.setAttribute("custom_detail", detail_rightModel);//�Ndetail_rightModel�]�w���W��custom_detail��session
		Executions.sendRedirect("setexam_detail.zul");//�����i�s�誺����
	}
    //���o����
	@Command
	public void get_selectVER(@BindingParam("paramx") String VERSION){
		QB_VERSION = VERSION;
	}
	//���o����
	@Command
	public void get_selectLEVEL(@BindingParam("paramx") String LEVEL){
		QB_LEVEL = LEVEL;
	}
	//���o�U�O
	@Command
	public void get_selectBOOK(@BindingParam("paramx") String BOOK){
		QB_BOOK = BOOK;
	}
	//���o���O
	@Command
	public void get_selectCHAPTER(@BindingParam("paramx") int CHAPTER){
		QB_CHAPTER = CHAPTER;
	}
	//���o�`�O
	@Command
	public void get_selectSECTION(@BindingParam("paramx") int SECTION){
		QB_SECTION = SECTION;
	}
	//���o�H���D��
	@Command
	public void get_selectrandom_num(@BindingParam("paramx") int number){
		random_num = number;
	}
	@Command
    //���ozul�ɶǨӪ����D�W��
	public void set_EBNO(@BindingParam("paramx") String code){
		examcode = code;
    }
	//���o�]�w�O�_��ݸѵ�
	@Command
	public void set_type(@BindingParam("paramx") int type) {
		is_explain = type;
	}
	@Command
	@NotifyChange("leftModel")//�j�M�ɧi�����䪺listmodl���ܰ�
	public void search_quiz(){
		leftModel = new ListModelList<Bindquiz>(service.searchquiz(QB_VERSION,QB_LEVEL,QB_BOOK,QB_CHAPTER,QB_SECTION));
	}
	@Command
	@NotifyChange("leftModel")//�j�M�ɧi�����䪺listmodl���ܰ�
	public void search_quiz_random(){
		leftModel = new ListModelList<Bindquiz>(service.searchquiz_random(QB_VERSION,QB_LEVEL,QB_BOOK,QB_CHAPTER,QB_SECTION,random_num));
	}
	//�q�k�䲾�쥪��
	@Command
	@NotifyChange({"leftModel","rightModel","selection1","selection2","right_lenth"})//�q���n�ܰʪ�����
	public void moveToList1(){
		if(selection2!=null && selection2.size()>0){
			Set<Bindquiz> set = rightModel.getSelection();//���o��ܪ����C
			leftModel.addAll(set);//���䪺model�[�������
			rightModel.removeAll(set);//�k�䪺model�N������ƧR��
			right_lenth = rightModel.getSize();
			selection1.clear();//�M�ť����ܦC
			selection2.clear();//�M�ťk���ܦC
		}
	}
	//�q���䲾��k��
	@Command
	@NotifyChange({"leftModel","rightModel","selection1","selection2","right_lenth"})//�q���n�ܰʪ�����
	public void moveToList2(){
		if(selection1!=null && selection1.size()>0){
			Set<Bindquiz> set = leftModel.getSelection();//���o��ܪ����C
			rightModel.addAll(set);//�k�䪺model�[�������
			leftModel.removeAll(set);//���䪺model�N������ƧR��
			right_lenth = rightModel.getSize();
			selection2.clear();//�M�ťk���ܦC
			selection1.clear();//�M�ť����ܦC
		}
	}
	//�k��-�W
	@Command
	@NotifyChange({"rightModel","selection1","selection2"})
	public void right_up(){
		Set<Bindquiz> selection = new LinkedHashSet<Bindquiz>(rightModel.getSelection());
		if (selection.isEmpty())//�p�G�S��F��Nreturn
			return;
		int index = rightModel.indexOf(selection.iterator().next());
		if (index == 0 || index < 0)
			return;
		rightModel.removeAll(selection);
		rightModel.addAll(--index, selection);
		rightModel.setSelection(selection);
		selection2.clear();//�M�ťk���ܦC
	}
	//�k��-�U
	@Command
	@NotifyChange({"rightModel","selection1","selection2"})
	public void right_down(){
		Set<Bindquiz> selection = new LinkedHashSet<Bindquiz>(rightModel.getSelection());
		if (selection.isEmpty())//�p�G�S��F��Nreturn
			return;
		int index = rightModel.indexOf(selection.iterator().next());
		if (index == rightModel.size() - selection.size() || index < 0)
			return;
		rightModel.removeAll(selection);
		rightModel.addAll(++index, selection);
		rightModel.setSelection(selection);
		selection2.clear();//�M�ťk���ܦC
	}
	/**���է�r��
	@Command
	public void getdata(){
		
	    Messagebox.show("Are you sure to execute Load?", "Execute?", Messagebox.YES | Messagebox.NO, 
		        Messagebox.QUESTION, new EventListener<Event>() {
		            @Override
		            public void onEvent(final Event evt) throws InterruptedException {
		                if (Messagebox.ON_YES.equals(evt.getName())) {
		                	Messagebox.show("���T�w!", "����", Messagebox.OK, Messagebox.EXCLAMATION);
		            		String[] codelist =  new String[rightModel.size()];
		            		for(int i=0;i< rightModel.size();i++)//i�Y�p���D���`�� i++
		            		{
		            			codelist[i] = rightModel.getElementAt(i).getQB_CODE();
		            		}
		            		examtmp = Arrays.toString(codelist);
		                    // Code if yes clicked
		                } else {
		                	Messagebox.show("������!", "����", Messagebox.OK, Messagebox.EXCLAMATION);
		                    // Code if no clicked
		                }
		            }
		        }
		    );
		}**/
	
	
	//�N��ܪ��D�ئC��s���Ʈw
	@Command
	public void save_todb() {
		int ifexist = 0;
		String[] codelist =  new String[rightModel.size()];//�гy�@�Ӹ�model���פ@�˪���array
		for(int i=0;i< rightModel.size();i++)//i�Y�p���D���`�� i++
		{
			codelist[i] = rightModel.getElementAt(i).getQB_CODE();//�N������code��i�harray�̭�
		}
		examtmp = Arrays.toString(codelist);//�N�}�C�নString
		//�H�W���N��ܪ��D���নexamtmp�r��
		//�H�U�}�l�s����Ʈw�ñN�F���i�h
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
        	stmt = conn.prepareStatement("SELECT EB_NO from next_generation.exam_base"
        			+ " where EB_NO = ? ;");
        	stmt.setString(1, examcode);//�]�w�Ĥ@��?���D�إN�X
        	ResultSet resultSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
			while (resultSet.next()) {//�p�G������F��
				ifexist = 1;//�]�w��1(���Ҩ�)
			}
        	stmt.close();
        	stmt = null;
    		if(ifexist == 1) {//�p�G���N���_
    			Messagebox.show("�w�����ƦW��,�Э��s�R�W!", "����", Messagebox.OK, Messagebox.EXCLAMATION);
    			return;
    		}
            stmt = conn.prepareStatement("INSERT INTO next_generation.exam_base (EB_NO,EB_MAIN,EB_NAME,is_explain)"
            		+ " values(?,?,?,?)");
            //�N�U���]�w���ȷs�W��exam_base
            stmt.setNString(1, examcode);//�]�w�Ĥ@��?���Ҩ��N�X
            stmt.setNString(2, examtmp);//�]�w�ĤG��?���D�إN�X
            stmt.setNString(3, exammark);//�]�w�ĤT��?���D�سƵ�
            stmt.setLong(4, is_explain);//�]�w�ĥ|��?���O�_��ݸѵ�
            stmt.executeUpdate();
            stmt.close();
            stmt = null;
			Messagebox.show("�s�W����!", "����", Messagebox.OK,Messagebox.INFORMATION
					, new org.zkoss.zk.ui.event.EventListener() {
					    public void onEvent(Event evt) throws InterruptedException {
					        if (evt.getName().equals("onOK")) {
					        	Executions.sendRedirect("setexam_main.zul");}//���UOK�Ჾ�ʨ쭶��
					    }
					});
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
//-------------------
	//�q�k�䲾�쥪��
	@Command
	@NotifyChange({"leftModel","detail_rightModel","selection1","selection2","right_lenth"})//�q���n�ܰʪ�����
	public void moveToList1_d(){
		if(selection2!=null && selection2.size()>0){
			Set<Bindquiz> set = detail_rightModel.getSelection();//���o��ܪ����C
			leftModel.addAll(set);//���䪺model�[�������
			detail_rightModel.removeAll(set);//�k�䪺model�N������ƧR��
			right_lenth = detail_rightModel.getSize();
			selection1.clear();//�M�ť����ܦC
			selection2.clear();//�M�ťk���ܦC
		}
	}
	//�q���䲾��k��
	@Command
	@NotifyChange({"leftModel","detail_rightModel","selection1","selection2","right_lenth"})//�q���n�ܰʪ�����
	public void moveToList2_d(){
		if(selection1!=null && selection1.size()>0){
			Set<Bindquiz> set = leftModel.getSelection();//���o��ܪ����C
			detail_rightModel.addAll(set);//�k�䪺model�[�������
			leftModel.removeAll(set);//���䪺model�N������ƧR��
			right_lenth = detail_rightModel.getSize();
			selection2.clear();//�M�ťk���ܦC
			selection1.clear();//�M�ť����ܦC
		}
	}
	//�k��-�W
	@Command
	@NotifyChange({"detail_rightModel","selection1","selection2"})
	public void right_up_d(){
		Set<Bindquiz> selection = new LinkedHashSet<Bindquiz>(detail_rightModel.getSelection());
		if (selection.isEmpty())//�p�G�S��F��Nreturn
			return;
		int index = detail_rightModel.indexOf(selection.iterator().next());
		if (index == 0 || index < 0)
			return;
		detail_rightModel.removeAll(selection);
		detail_rightModel.addAll(--index, selection);
		detail_rightModel.setSelection(selection);
		selection2.clear();//�M�ťk���ܦC
	}
	//�k��-�U
	@Command
	@NotifyChange({"detail_rightModel","selection1","selection2"})
	public void right_down_d(){
		Set<Bindquiz> selection = new LinkedHashSet<Bindquiz>(detail_rightModel.getSelection());
		if (selection.isEmpty())//�p�G�S��F��Nreturn
			return;
		int index = detail_rightModel.indexOf(selection.iterator().next());
		if (index == detail_rightModel.size() - selection.size() || index < 0)
			return;
		detail_rightModel.removeAll(selection);
		detail_rightModel.addAll(++index, selection);
		detail_rightModel.setSelection(selection);
		selection2.clear();//�M�ťk���ܦC
	}
	
	//��s�Ҩ����
		@Command
		public void update_todb() {
			String[] codelist =  new String[detail_rightModel.size()];//�гy�@�Ӹ�model���פ@�˪���array
			for(int i=0;i< detail_rightModel.size();i++)//i�Y�p���D���`�� i++
			{
				codelist[i] = detail_rightModel.getElementAt(i).getQB_CODE();//�N������code��i�harray�̭�
			}
			examtmp = Arrays.toString(codelist);//�N�}�C�নString
			//�H�W���N��ܪ��D���নexamtmp�r��
	        Connection conn = null;
	        PreparedStatement stmt = null;
	        try {
	        	  DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
	              conn = ds.getConnection();
	              conn.setAutoCommit(true);
	              stmt = conn.prepareStatement("UPDATE next_generation.exam_base SET EB_NAME = ?, EB_MAIN = ? where EB_NO = ?");
	              //�N�U���]�w���ȷs�W��exam_base
	              stmt.setNString(1, exammark);//�]�w�Ĥ@��?���Ҩ��Ƶ�
	              stmt.setNString(2, examtmp);//�]�w�ĤG��?���D�إN�X
	              stmt.setNString(3, examcode);
	              stmt.executeUpdate();
	              stmt.close();
	              stmt = null;
	  			Messagebox.show("�ק粒��!", "����", Messagebox.OK,Messagebox.INFORMATION
	  					, new org.zkoss.zk.ui.event.EventListener() {
	  					    public void onEvent(Event evt) throws InterruptedException {
	  					        if (evt.getName().equals("onOK")) {
	  					        	Executions.sendRedirect("setexam_edit.zul");}//���UOK�Ჾ�ʨ쭶��
	  					    }
	  					});
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
		}
			
	
}