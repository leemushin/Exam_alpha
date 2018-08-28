package login.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import java.util.Date;

public class LoginServiceImpl implements LoginService {
	
	private LoginDAO LoginDAO = new LoginDAO();
	List<Login> Alluser = LoginDAO.findAll();//撈所有使用者資料(應該可以整合到此檔)
	
	//比對全部使用者帳號裡面是否有與輸入帳號相同
	public synchronized Login findUser(String account){
		int s = Alluser.size();//設s 為剛撈出的所有使用者長度
		for(int i=0;i<s;i++){
			Login urlist = Alluser.get(i);//urlist = Alluser中第i筆資料 
			if(account.equals(urlist.getaccount())){//如果輸入的帳號與urlist相同
				return Login.clone(urlist);//回傳該筆資料的clone
			}
		}
		return null;
	}
	@Override
	public boolean User(String account, String password) {
		Login user = findUser(account);//用帳號搜尋
		//如果user是null(代表沒帳號)或密碼不相等
		if(user==null || !user.getpassword().equals(password)){
			return false;
		}
		Session sess = Sessions.getCurrent();
		Login cre = new Login(user.getaccount(),user.getfullName(), user.getpassword(),user.getUR_STUDENT(),user.getUR_TEACHER(),user.getUR_PRINCIPAL(),user.getUR_ADMIN(),user.getEXPIRYDATE(),user.getLAST_LOGIN());
		sess.setAttribute("userCredential",cre);//將cre設為叫作"userCredential"的session
		return true;
	}
	//登出系統,取得session中的帳號,更新登出時間與登入旗標,之後將session內容無效
	@Override
	public void logout() {
		Session sess = Sessions.getCurrent();
		sess.removeAttribute("userCredential");
		sess.invalidate(); //將session 無效
	}
	//取得使用者身份
	@Override
	public Login getUserCredential() {
		Session sess = Sessions.getCurrent();
		Login cre = (Login)sess.getAttribute("userCredential");
		if(cre==null){
			cre = new Login();
			sess.setAttribute("userCredential",cre);
		}
		return cre;
	}
	//更新最後登入日以及登入次數
	@Override
	public void updlastlogin(String account,Date loginmonth) {
		java.text.SimpleDateFormat cuttime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//設定格式yyyy-MM-dd HH:mm:ss
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//設定格式yyyy-MM
		String loginm = getyymm.format(loginmonth);//將登入時間轉成yyyy-MM，供每月登入次數使用
		String logintime = cuttime.format(loginmonth);//將登入時間轉成yyyy-MM-dd HH:mm:ss，供最後登入時間使用
		int times = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement Logindata = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            Logindata = conn.prepareStatement("SELECT LD_FREQUENCY FROM next_generation.login_data where LD_ACCOUNT = ? and LD_MONTH like ?");
            Logindata.setNString(1, account);//第一個?為帳號
            Logindata.setNString(2, loginm+"%");//第二個?為登入月份
            ResultSet ld = Logindata.executeQuery();//執行查詢並將結果塞進resultSet
            while(ld.next()) {
            	times = 1 + ld.getInt("LD_FREQUENCY");//將登入次數+1
            }
            Logindata.close();
            Logindata = null;
            if(times == 0) {//如果沒有撈到當月資料
            	Logindata = conn.prepareStatement("INSERT INTO next_generation.login_data (LD_ACCOUNT,LD_MONTH,LD_FREQUENCY) values(?,?,?)");
            	//新增一筆帳號月份還有登入次數1的資料
            	Logindata.setNString(1, account);//第一個?為帳號
                Logindata.setNString(2, logintime);//第二個?為登入月份
                Logindata.setInt(3, 1);//第三個?為1
                Logindata.executeUpdate();
                Logindata.close();
            }
            else {//如果有撈到當月資料
            	Logindata = conn.prepareStatement("update next_generation.login_data set LD_FREQUENCY =? where LD_ACCOUNT = ? and DATE_FORMAT(LD_MONTH,\"%Y-%m\") like ?");
            	//將登入次數更改為+1後的數字
            	Logindata.setInt(1, times);//第一個?為登入次數
                Logindata.setNString(2, account);//第二個?為帳號
                Logindata.setNString(3, loginm+"%");//第三個?為登入月份
                Logindata.executeUpdate();
                Logindata.close();
            }
            stmt = conn.prepareStatement("update next_generation.sys_data set LAST_LOGIN = ? ,LOGIN_FLAG = ? where SYS_ACCOUNT = ?");
            //更新最後登入日期,以及將登入旗標設置為1
            stmt.setNString(1, logintime);//第一個?為登入時間
            stmt.setNString(2, "1");//第二個?為登入旗標
            stmt.setNString(3, account);//第三個?為帳號
            stmt.executeUpdate();
            stmt.close();
            stmt = null;
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
	//檢查是否有登入標記
	@Override
	public String iflogin(String account) {
		String loginflag = "";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
        	conn = ds.getConnection();
        	stmt = conn.prepareStatement("SELECT LOGIN_FLAG FROM next_generation.sys_data"
        			+ " where SYS_ACCOUNT = ? ;");
        	stmt.setNString(1, account);//用使用者帳號當搜尋條件
        	ResultSet resultSet = stmt.executeQuery();//執行查詢並將結果塞進resultSet
			while (resultSet.next()) {
				loginflag = resultSet.getString("LOGIN_FLAG");//設定為登入狀態(0為沒有登入,1為已經登入)
			}
	       	stmt.close();
        	stmt = null;
        }catch (SQLException e) {
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
		return loginflag;
	}
	//更新最後登出時間,並且將登入旗標改為0(未登入)
	@Override
	public void updlastlogout(String account) {
		java.text.SimpleDateFormat cuttime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//設定格式yyyy-MM-dd HH:mm:ss
		java.util.Date logoutDate = new java.util.Date();//取得今天日期
		String logouttime = cuttime.format(logoutDate);//將登出時間轉成yyyy-MM-dd HH:mm:ss，供最後登出時間使用
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            stmt = conn.prepareStatement("update next_generation.sys_data set LAST_LOGOUT = ? ,LOGIN_FLAG = ? where SYS_ACCOUNT = ?");
            //更新最後登出日期以及將登入旗標設置為0
            stmt.setNString(1, logouttime);//第一個?為登出時間
            stmt.setNString(2, "0");//第二個?為登入旗標
            stmt.setNString(3, account);//第三個?為帳號
            stmt.executeUpdate();
	       	stmt.close();
        	stmt = null;
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
