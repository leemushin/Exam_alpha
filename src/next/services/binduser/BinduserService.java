package next.services.binduser;

import java.util.Date;
import java.util.List;

public interface BinduserService {
	public List<Binduser> searchusr(String account_keyin);

	public List<Binduser> doupdate(String upd_USERNAME,String upd_ACCOUNT,String upd_PASSWORD,String upd_PWD_HINT,String upd_STUDENT,
			String upd_SCHOOL,Date upd_REGISTERED,Date upd_EXPIRYDATE,String upd_TEACHER,String upd_PRINCIPAL,String upd_ADMIN,String remark);
	
	public void deleteusr(String account_keyin);
	
	public void upd_usr_grade(int before_grade,int after_grade);
}
