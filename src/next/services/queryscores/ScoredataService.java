package next.services.queryscores;

import java.util.Collection;
import java.util.List;
import exam.service.Exam;

public interface ScoredataService {
	
	public List<Scoredata> findAll();

	public List<Exam> findRecord(String id);
	
	public List<Scoredata> getpoint(String user_Acc);//�αb���d�ԲӤ���
	
	public List<Scoredata> getdetail(String Memb_Acc);//�ξǥͱb���d�ԲӸ��
	
}
