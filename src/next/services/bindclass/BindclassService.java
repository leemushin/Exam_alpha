package next.services.bindclass;

import java.util.List;

public interface BindclassService {
	public List<Bindclass> searchcls(String class_keyin);

	public List<Bindclass> doupdate(String upd_NO, String upd_NAME, String upd_GRADE,
			String upd_TEACHER, String upd_SCHOOL, String upd_MEMBER);
	
	public List<Bindclass> searchlast();
	
	public void deletecls(String class_keyin);
	
	public void upd_cls_grade(int before_grade,int after_grade);
}
