package next.services.binduser;

import java.sql.*;



public enum BinduserConnect {
	
	INSTANCE;
	//���������}�o���{���̵MŪ�����s����,���ɥi�઺�Τ@��אּŪ��tomcat����context.xml������Ʈw�s��
	private static final String url = "jdbc:mysql://localhost:3305/next_generation?useSSL=false";
	private static final String user = "root";
	private static final String pwd = "kikiwiwi";
	//private static final String url = "jdbc:mysql://localhost:3306/next_generation?useSSL=false";
	//private static final String user = "pipi";
	//private static final String pwd = "6690";
	
	private Connection conn = null;
    PreparedStatement stmt = null;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
        
	public Statement getStatement() throws SQLException {
		Statement stmt = null;
		// get connection
		conn = DriverManager.getConnection(url, user, pwd);
		stmt = conn.createStatement();
		return stmt;
	}
    
	public void close() {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
