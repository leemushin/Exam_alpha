package login.services;

import java.util.Date;

public interface LoginService {
	
	public boolean User(String account, String password);
	public Login findUser(String account);
	public void logout();
	public Login getUserCredential();
	public void updlastlogin(String account,Date loginmonth);	
	public String iflogin(String account);
	public void updlastlogout(String account);	
}
