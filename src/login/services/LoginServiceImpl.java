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
	List<Login> Alluser = LoginDAO.findAll();//���Ҧ��ϥΪ̸��(���ӥi�H��X�즹��)
	
	//�������ϥΪ̱b���̭��O�_���P��J�b���ۦP
	public synchronized Login findUser(String account){
		int s = Alluser.size();//�]s ���輴�X���Ҧ��ϥΪ̪���
		for(int i=0;i<s;i++){
			Login urlist = Alluser.get(i);//urlist = Alluser����i����� 
			if(account.equals(urlist.getaccount())){//�p�G��J���b���Purlist�ۦP
				return Login.clone(urlist);//�^�Ǹӵ���ƪ�clone
			}
		}
		return null;
	}
	@Override
	public boolean User(String account, String password) {
		Login user = findUser(account);//�αb���j�M
		//�p�Guser�Onull(�N��S�b��)�αK�X���۵�
		if(user==null || !user.getpassword().equals(password)){
			return false;
		}
		Session sess = Sessions.getCurrent();
		Login cre = new Login(user.getaccount(),user.getfullName(), user.getpassword(),user.getUR_STUDENT(),user.getUR_TEACHER(),user.getUR_PRINCIPAL(),user.getUR_ADMIN(),user.getEXPIRYDATE(),user.getLAST_LOGIN());
		sess.setAttribute("userCredential",cre);//�Ncre�]���s�@"userCredential"��session
		return true;
	}
	//�n�X�t��,���osession�����b��,��s�n�X�ɶ��P�n�J�X��,����Nsession���e�L��
	@Override
	public void logout() {
		Session sess = Sessions.getCurrent();
		sess.removeAttribute("userCredential");
		sess.invalidate(); //�Nsession �L��
	}
	//���o�ϥΪ̨���
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
	//��s�̫�n�J��H�εn�J����
	@Override
	public void updlastlogin(String account,Date loginmonth) {
		java.text.SimpleDateFormat cuttime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�]�w�榡yyyy-MM-dd HH:mm:ss
		java.text.SimpleDateFormat getyymm = new java.text.SimpleDateFormat("yyyy-MM");//�]�w�榡yyyy-MM
		String loginm = getyymm.format(loginmonth);//�N�n�J�ɶ��নyyyy-MM�A�ѨC��n�J���ƨϥ�
		String logintime = cuttime.format(loginmonth);//�N�n�J�ɶ��নyyyy-MM-dd HH:mm:ss�A�ѳ̫�n�J�ɶ��ϥ�
		int times = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement Logindata = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
            Logindata = conn.prepareStatement("SELECT LD_FREQUENCY FROM next_generation.login_data where LD_ACCOUNT = ? and LD_MONTH like ?");
            Logindata.setNString(1, account);//�Ĥ@��?���b��
            Logindata.setNString(2, loginm+"%");//�ĤG��?���n�J���
            ResultSet ld = Logindata.executeQuery();//����d�ߨñN���G��iresultSet
            while(ld.next()) {
            	times = 1 + ld.getInt("LD_FREQUENCY");//�N�n�J����+1
            }
            Logindata.close();
            Logindata = null;
            if(times == 0) {//�p�G�S����������
            	Logindata = conn.prepareStatement("INSERT INTO next_generation.login_data (LD_ACCOUNT,LD_MONTH,LD_FREQUENCY) values(?,?,?)");
            	//�s�W�@���b������٦��n�J����1�����
            	Logindata.setNString(1, account);//�Ĥ@��?���b��
                Logindata.setNString(2, logintime);//�ĤG��?���n�J���
                Logindata.setInt(3, 1);//�ĤT��?��1
                Logindata.executeUpdate();
                Logindata.close();
            }
            else {//�p�G����������
            	Logindata = conn.prepareStatement("update next_generation.login_data set LD_FREQUENCY =? where LD_ACCOUNT = ? and DATE_FORMAT(LD_MONTH,\"%Y-%m\") like ?");
            	//�N�n�J���Ƨ�אּ+1�᪺�Ʀr
            	Logindata.setInt(1, times);//�Ĥ@��?���n�J����
                Logindata.setNString(2, account);//�ĤG��?���b��
                Logindata.setNString(3, loginm+"%");//�ĤT��?���n�J���
                Logindata.executeUpdate();
                Logindata.close();
            }
            stmt = conn.prepareStatement("update next_generation.sys_data set LAST_LOGIN = ? ,LOGIN_FLAG = ? where SYS_ACCOUNT = ?");
            //��s�̫�n�J���,�H�αN�n�J�X�г]�m��1
            stmt.setNString(1, logintime);//�Ĥ@��?���n�J�ɶ�
            stmt.setNString(2, "1");//�ĤG��?���n�J�X��
            stmt.setNString(3, account);//�ĤT��?���b��
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
	//�ˬd�O�_���n�J�аO
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
        	stmt.setNString(1, account);//�ΨϥΪ̱b����j�M����
        	ResultSet resultSet = stmt.executeQuery();//����d�ߨñN���G��iresultSet
			while (resultSet.next()) {
				loginflag = resultSet.getString("LOGIN_FLAG");//�]�w���n�J���A(0���S���n�J,1���w�g�n�J)
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
	//��s�̫�n�X�ɶ�,�åB�N�n�J�X�Чאּ0(���n�J)
	@Override
	public void updlastlogout(String account) {
		java.text.SimpleDateFormat cuttime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�]�w�榡yyyy-MM-dd HH:mm:ss
		java.util.Date logoutDate = new java.util.Date();//���o���Ѥ��
		String logouttime = cuttime.format(logoutDate);//�N�n�X�ɶ��নyyyy-MM-dd HH:mm:ss�A�ѳ̫�n�X�ɶ��ϥ�
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
            conn = ds.getConnection();
            stmt = conn.prepareStatement("update next_generation.sys_data set LAST_LOGOUT = ? ,LOGIN_FLAG = ? where SYS_ACCOUNT = ?");
            //��s�̫�n�X����H�αN�n�J�X�г]�m��0
            stmt.setNString(1, logouttime);//�Ĥ@��?���n�X�ɶ�
            stmt.setNString(2, "0");//�ĤG��?���n�J�X��
            stmt.setNString(3, account);//�ĤT��?���b��
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
