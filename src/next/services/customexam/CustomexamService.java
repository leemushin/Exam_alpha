package next.services.customexam;

import java.util.List;
import exam.service.Exam;
public interface CustomexamService {
	public List<Exam> searchexam(String exam_keyin);
	public int checkexam(String exam_keyin);
	public List<Exam> simulationexam(String sim_ver,String sim_book);
	public List<Exam> simulationexam_r(String sim_ver, String sim_book);
	public List<Exam> pastexam(String past_year);
	public List<Exam> pastexam_r();
}
