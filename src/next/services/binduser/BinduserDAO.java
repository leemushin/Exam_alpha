package next.services.binduser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.sql.*;

public class BinduserDAO {
	
	private final BinduserConnect ds = BinduserConnect.INSTANCE;
	
	public List<Binduser> findAll() {
		List<Binduser> Alluser = new ArrayList<Binduser>();
		try {
			// get connection
		    Statement stmt = ds.getStatement();
			ResultSet rs = stmt.executeQuery("select SYS_USERNAME,SYS_ACCOUNT,PWD_HINT from next_generation.sys_data ORDER BY SYS_ID DESC limit 30");
			// 抓出倒數30筆的使用者資訊
			Binduser usr;
			while (rs.next()) {
				usr = new Binduser();
				usr.setUSERNAME(rs.getString(1));
		        usr.setSYS_ACCOUNT(rs.getString(2));
				usr.setPASSWORD(rs.getString(3));
				Alluser.add(usr);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ds.close();
		}	
		return Alluser;
	}

}
