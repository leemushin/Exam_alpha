package next.services.queryscores;

import java.util.Collection;
import java.util.List;
import exam.service.Exam;

public interface ScoredataService {
	
	public List<Scoredata> findAll();

	public List<Exam> findRecord(String id);
	
	public List<Scoredata> getpoint(String user_Acc);//用帳號查詳細分數
	
	public List<Scoredata> getdetail(String Memb_Acc);//用學生帳號查詳細資料
	
}
