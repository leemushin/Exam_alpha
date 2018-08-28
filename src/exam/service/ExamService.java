package exam.service;

import java.util.List;

public interface ExamService {
	public List<Exam> find_chose(String book,String ver,String ch,String se,String lv,String ifteacher,String User_Acc);
	public List<Exam> find_chose_A(String book, String ver, String ch, String lv, String ifteacher,String User_Acc);
}
