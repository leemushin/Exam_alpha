package login.services;

import java.util.ArrayList;
import java.util.List;
import next.services.binduser.BinduserConnect;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginDAO {

	private final BinduserConnect ds = BinduserConnect.INSTANCE;
	List<Login> usrlist = new ArrayList<Login>();
	//撈出所有使用者資料
	public List<Login> findAll() {
		try {
		    Statement stmt = ds.getStatement();
			ResultSet rs = stmt.executeQuery("SELECT SYS_USERNAME,SYS_ACCOUNT,PASSWORD,UR_ADMIN,UR_PRINCIPAL,UR_STUDENT,UR_TEACHER,EXPIRYDATE,LAST_LOGIN FROM next_generation.sys_data sd "
					+ "left join next_generation.user_role ur ON sd.SYS_ACCOUNT = ur.UR_ACCOUNT");
			Login usrdata;			
			while (rs.next()) {
				usrdata = new Login();
				usrdata.setfullName(rs.getString("SYS_USERNAME"));
				usrdata.setaccount(rs.getString("SYS_ACCOUNT"));
				usrdata.setpassword(rs.getString("PASSWORD"));
				usrdata.setUR_ADMIN(rs.getString("UR_ADMIN"));
				usrdata.setUR_PRINCIPAL(rs.getString("UR_PRINCIPAL"));
				usrdata.setUR_STUDENT(rs.getString("UR_STUDENT"));
				usrdata.setUR_TEACHER(rs.getString("UR_TEACHER"));
				usrdata.setEXPIRYDATE(rs.getDate("EXPIRYDATE"));
				usrdata.setLAST_LOGIN(rs.getTimestamp("LAST_LOGIN"));
				usrlist.add(usrdata);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ds.close();
		}	
		return usrlist;
	}
}
