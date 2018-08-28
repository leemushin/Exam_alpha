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
    private String QB_VERSION = "ni";//題目版本
    private String QB_LEVEL = "e";//題目難度
    private String QB_BOOK = "jr1_1";//題目冊別
    private int QB_CHAPTER = 1;//題目章別
    private int QB_SECTION = 1;//題目節別
    private int is_explain = 1;//是否給學生看解答
    private int random_num = 1;//隨機題數
	Set<String> selection1;
	List<String> list1;
	Set<String> selection2;
	List<String> list2;
	String examtmp;//右邊預計出題的列表
	Session s = Sessions.getCurrent(); 
	String examcode="";//考卷代碼
	String exammark="";//考卷備註
	int right_lenth;//出題的數量
	
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
			exammark = (String) s.getAttribute("custom_EB_NAME");//如果有值,代表從setexam_edit點詳細資料
		}
		return exammark;
	}
	//載入這個ViewModel的時候會讀取的
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
	public ListModelList<Bindquiz>  getdetail_rightModel() {//編輯試卷的時候右邊的預設model
		if(s.getAttribute("custom_detail") != null ){		//如果Cla_detail有資料  就設定ClassdetailModel
			detail_rightModel = (ListModelList<Bindquiz>) s.getAttribute("custom_detail");
		}
		return detail_rightModel;
	}
	public ListModelList<Setexam>  getEditModel() {//setexam_edit.zul 讀這個
		EditModel = new ListModelList<Setexam> (service.customlist());//搜尋全部考卷
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
	//取得單一考卷詳細內容setexam_edit.zul中 選擇考卷後
	@Command
	public void get_examdetail(@BindingParam("paramx") String EB_NO) {
		detail_rightModel = new ListModelList<Bindquiz>(service.searchdetail(EB_NO));
		s.setAttribute("custom_detail", detail_rightModel);//將detail_rightModel設定成名為custom_detail的session
		Executions.sendRedirect("setexam_detail.zul");//跳轉到可編輯的頁面
	}
    //取得版本
	@Command
	public void get_selectVER(@BindingParam("paramx") String VERSION){
		QB_VERSION = VERSION;
	}
	//取得難度
	@Command
	public void get_selectLEVEL(@BindingParam("paramx") String LEVEL){
		QB_LEVEL = LEVEL;
	}
	//取得冊別
	@Command
	public void get_selectBOOK(@BindingParam("paramx") String BOOK){
		QB_BOOK = BOOK;
	}
	//取得章別
	@Command
	public void get_selectCHAPTER(@BindingParam("paramx") int CHAPTER){
		QB_CHAPTER = CHAPTER;
	}
	//取得節別
	@Command
	public void get_selectSECTION(@BindingParam("paramx") int SECTION){
		QB_SECTION = SECTION;
	}
	//取得隨機題數
	@Command
	public void get_selectrandom_num(@BindingParam("paramx") int number){
		random_num = number;
	}
	@Command
    //取得zul檔傳來的考題名稱
	public void set_EBNO(@BindingParam("paramx") String code){
		examcode = code;
    }
	//取得設定是否能看解答
	@Command
	public void set_type(@BindingParam("paramx") int type) {
		is_explain = type;
	}
	@Command
	@NotifyChange("leftModel")//搜尋時告知左邊的listmodl須變動
	public void search_quiz(){
		leftModel = new ListModelList<Bindquiz>(service.searchquiz(QB_VERSION,QB_LEVEL,QB_BOOK,QB_CHAPTER,QB_SECTION));
	}
	@Command
	@NotifyChange("leftModel")//搜尋時告知左邊的listmodl須變動
	public void search_quiz_random(){
		leftModel = new ListModelList<Bindquiz>(service.searchquiz_random(QB_VERSION,QB_LEVEL,QB_BOOK,QB_CHAPTER,QB_SECTION,random_num));
	}
	//從右邊移到左邊
	@Command
	@NotifyChange({"leftModel","rightModel","selection1","selection2","right_lenth"})//通知要變動的元件
	public void moveToList1(){
		if(selection2!=null && selection2.size()>0){
			Set<Bindquiz> set = rightModel.getSelection();//取得選擇的那列
			leftModel.addAll(set);//左邊的model加此筆資料
			rightModel.removeAll(set);//右邊的model將此筆資料刪除
			right_lenth = rightModel.getSize();
			selection1.clear();//清空左邊選擇列
			selection2.clear();//清空右邊選擇列
		}
	}
	//從左邊移到右邊
	@Command
	@NotifyChange({"leftModel","rightModel","selection1","selection2","right_lenth"})//通知要變動的元件
	public void moveToList2(){
		if(selection1!=null && selection1.size()>0){
			Set<Bindquiz> set = leftModel.getSelection();//取得選擇的那列
			rightModel.addAll(set);//右邊的model加此筆資料
			leftModel.removeAll(set);//左邊的model將此筆資料刪除
			right_lenth = rightModel.getSize();
			selection2.clear();//清空右邊選擇列
			selection1.clear();//清空左邊選擇列
		}
	}
	//右邊-上
	@Command
	@NotifyChange({"rightModel","selection1","selection2"})
	public void right_up(){
		Set<Bindquiz> selection = new LinkedHashSet<Bindquiz>(rightModel.getSelection());
		if (selection.isEmpty())//如果沒選東西就return
			return;
		int index = rightModel.indexOf(selection.iterator().next());
		if (index == 0 || index < 0)
			return;
		rightModel.removeAll(selection);
		rightModel.addAll(--index, selection);
		rightModel.setSelection(selection);
		selection2.clear();//清空右邊選擇列
	}
	//右邊-下
	@Command
	@NotifyChange({"rightModel","selection1","selection2"})
	public void right_down(){
		Set<Bindquiz> selection = new LinkedHashSet<Bindquiz>(rightModel.getSelection());
		if (selection.isEmpty())//如果沒選東西就return
			return;
		int index = rightModel.indexOf(selection.iterator().next());
		if (index == rightModel.size() - selection.size() || index < 0)
			return;
		rightModel.removeAll(selection);
		rightModel.addAll(++index, selection);
		rightModel.setSelection(selection);
		selection2.clear();//清空右邊選擇列
	}
	/**測試抓字串
	@Command
	public void getdata(){
		
	    Messagebox.show("Are you sure to execute Load?", "Execute?", Messagebox.YES | Messagebox.NO, 
		        Messagebox.QUESTION, new EventListener<Event>() {
		            @Override
		            public void onEvent(final Event evt) throws InterruptedException {
		                if (Messagebox.ON_YES.equals(evt.getName())) {
		                	Messagebox.show("按確定!", "提醒", Messagebox.OK, Messagebox.EXCLAMATION);
		            		String[] codelist =  new String[rightModel.size()];
		            		for(int i=0;i< rightModel.size();i++)//i若小於題目總數 i++
		            		{
		            			codelist[i] = rightModel.getElementAt(i).getQB_CODE();
		            		}
		            		examtmp = Arrays.toString(codelist);
		                    // Code if yes clicked
		                } else {
		                	Messagebox.show("按取消!", "提醒", Messagebox.OK, Messagebox.EXCLAMATION);
		                    // Code if no clicked
		                }
		            }
		        }
		    );
		}**/
	
	
	//將選擇的題目列表存到資料庫
	@Command
	public void save_todb() {
		int ifexist = 0;
		String[] codelist =  new String[rightModel.size()];//創造一個跟model長度一樣長的array
		for(int i=0;i< rightModel.size();i++)//i若小於題目總數 i++
		{
			codelist[i] = rightModel.getElementAt(i).getQB_CODE();//將對應的code塞進去array裡面
		}
		examtmp = Arrays.toString(codelist);//將陣列轉成String
		//以上先將選擇的題目轉成examtmp字串
		//以下開始連結資料庫並將東西塞進去
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
        	stmt = conn.prepareStatement("SELECT EB_NO from next_generation.exam_base"
        			+ " where EB_NO = ? ;");
        	stmt.setString(1, examcode);//設定第一個?為題目代碼
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
			while (resultSet.next()) {//如果有撈到東西
				ifexist = 1;//設定為1(有考卷)
			}
        	stmt.close();
        	stmt = null;
    		if(ifexist == 1) {//如果有就中斷
    			Messagebox.show("已有重複名稱,請重新命名!", "提醒", Messagebox.OK, Messagebox.EXCLAMATION);
    			return;
    		}
            stmt = conn.prepareStatement("INSERT INTO next_generation.exam_base (EB_NO,EB_MAIN,EB_NAME,is_explain)"
            		+ " values(?,?,?,?)");
            //將下面設定的值新增到exam_base
            stmt.setNString(1, examcode);//設定第一個?為考卷代碼
            stmt.setNString(2, examtmp);//設定第二個?為題目代碼
            stmt.setNString(3, exammark);//設定第三個?為題目備註
            stmt.setLong(4, is_explain);//設定第四個?為是否能看解答
            stmt.executeUpdate();
            stmt.close();
            stmt = null;
			Messagebox.show("新增完成!", "提醒", Messagebox.OK,Messagebox.INFORMATION
					, new org.zkoss.zk.ui.event.EventListener() {
					    public void onEvent(Event evt) throws InterruptedException {
					        if (evt.getName().equals("onOK")) {
					        	Executions.sendRedirect("setexam_main.zul");}//按下OK後移動到頁面
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
	//從右邊移到左邊
	@Command
	@NotifyChange({"leftModel","detail_rightModel","selection1","selection2","right_lenth"})//通知要變動的元件
	public void moveToList1_d(){
		if(selection2!=null && selection2.size()>0){
			Set<Bindquiz> set = detail_rightModel.getSelection();//取得選擇的那列
			leftModel.addAll(set);//左邊的model加此筆資料
			detail_rightModel.removeAll(set);//右邊的model將此筆資料刪除
			right_lenth = detail_rightModel.getSize();
			selection1.clear();//清空左邊選擇列
			selection2.clear();//清空右邊選擇列
		}
	}
	//從左邊移到右邊
	@Command
	@NotifyChange({"leftModel","detail_rightModel","selection1","selection2","right_lenth"})//通知要變動的元件
	public void moveToList2_d(){
		if(selection1!=null && selection1.size()>0){
			Set<Bindquiz> set = leftModel.getSelection();//取得選擇的那列
			detail_rightModel.addAll(set);//右邊的model加此筆資料
			leftModel.removeAll(set);//左邊的model將此筆資料刪除
			right_lenth = detail_rightModel.getSize();
			selection2.clear();//清空右邊選擇列
			selection1.clear();//清空左邊選擇列
		}
	}
	//右邊-上
	@Command
	@NotifyChange({"detail_rightModel","selection1","selection2"})
	public void right_up_d(){
		Set<Bindquiz> selection = new LinkedHashSet<Bindquiz>(detail_rightModel.getSelection());
		if (selection.isEmpty())//如果沒選東西就return
			return;
		int index = detail_rightModel.indexOf(selection.iterator().next());
		if (index == 0 || index < 0)
			return;
		detail_rightModel.removeAll(selection);
		detail_rightModel.addAll(--index, selection);
		detail_rightModel.setSelection(selection);
		selection2.clear();//清空右邊選擇列
	}
	//右邊-下
	@Command
	@NotifyChange({"detail_rightModel","selection1","selection2"})
	public void right_down_d(){
		Set<Bindquiz> selection = new LinkedHashSet<Bindquiz>(detail_rightModel.getSelection());
		if (selection.isEmpty())//如果沒選東西就return
			return;
		int index = detail_rightModel.indexOf(selection.iterator().next());
		if (index == detail_rightModel.size() - selection.size() || index < 0)
			return;
		detail_rightModel.removeAll(selection);
		detail_rightModel.addAll(++index, selection);
		detail_rightModel.setSelection(selection);
		selection2.clear();//清空右邊選擇列
	}
	
	//更新考卷資料
		@Command
		public void update_todb() {
			String[] codelist =  new String[detail_rightModel.size()];//創造一個跟model長度一樣長的array
			for(int i=0;i< detail_rightModel.size();i++)//i若小於題目總數 i++
			{
				codelist[i] = detail_rightModel.getElementAt(i).getQB_CODE();//將對應的code塞進去array裡面
			}
			examtmp = Arrays.toString(codelist);//將陣列轉成String
			//以上先將選擇的題目轉成examtmp字串
	        Connection conn = null;
	        PreparedStatement stmt = null;
	        try {
	        	  DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
	              conn = ds.getConnection();
	              conn.setAutoCommit(true);
	              stmt = conn.prepareStatement("UPDATE next_generation.exam_base SET EB_NAME = ?, EB_MAIN = ? where EB_NO = ?");
	              //將下面設定的值新增到exam_base
	              stmt.setNString(1, exammark);//設定第一個?為考卷備註
	              stmt.setNString(2, examtmp);//設定第二個?為題目代碼
	              stmt.setNString(3, examcode);
	              stmt.executeUpdate();
	              stmt.close();
	              stmt = null;
	  			Messagebox.show("修改完成!", "提醒", Messagebox.OK,Messagebox.INFORMATION
	  					, new org.zkoss.zk.ui.event.EventListener() {
	  					    public void onEvent(Event evt) throws InterruptedException {
	  					        if (evt.getName().equals("onOK")) {
	  					        	Executions.sendRedirect("setexam_edit.zul");}//按下OK後移動到頁面
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