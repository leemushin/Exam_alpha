package next.services.focus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class FocusServiceImpl implements FocusService {
	//從資料庫中撈重點資料
	@Override
	public List<Focus> findfocus(String book, String ver, String ch, String se) {
		List<Focus> Focuslist = new ArrayList<Focus>();
        Connection conn = null;
        PreparedStatement stmt = null;
		 try {
	        	DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/MyDB");
	        	conn = ds.getConnection();
	        	stmt = conn.prepareStatement("SELECT FB_MAIN FROM next_generation.focus_base "
	        			+ "where FB_BOOK =? and FB_VERSION = ? and FB_CHAPTER = ? and FB_SECTION = ? ");
	        	stmt.setNString(1, book);//第一個?用冊別當搜尋條件
	        	stmt.setNString(2, ver);//第二個?用版本當搜尋條件
	        	stmt.setNString(3, ch);//第三個?用章別當搜尋條件
	        	stmt.setNString(4, se);//第四個?用節別當搜尋條件
	        	ResultSet rs = stmt.executeQuery();//執行查詢並將結果塞進resultSet
	        	Focus focussourse;
	        	while (rs.next()) {
	        		focussourse = new Focus();
	        		focussourse.setFB_MAIN(rs.getString("FB_MAIN"));
	        		Focuslist.add(focussourse);
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
		return Focuslist;
	}

}
