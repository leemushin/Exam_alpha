package next.services.setexam;

import java.util.Collection;
import java.util.List;

import next.services.bindquiz.Bindquiz;

public interface SetexamService {
	public List<Bindquiz> searchquiz(String QB_VERSION, String QB_LEVEL, String QB_BOOK, int QB_CHAPTER, int QB_SECTION);
	public List<Bindquiz> searchquiz_random(String QB_VERSION, String QB_LEVEL, String QB_BOOK, int QB_CHAPTER, int QB_SECTION,int random_num);
	public List<Bindquiz> searchbych(String qiz_ch1, String qiz_ch2, String qiz_ch3, int qiz_ch4, String qiz_ch5, String qiz_ch6, String qiz_ch7, int qiz_ch8,String QB_VERSION);
	public List<Bindquiz> searchbyse(String qiz_se1, String qiz_se2, String qiz_se3, String qiz_se4, int qiz_se5, String qiz_se6, String qiz_se7, String qiz_se8, String qiz_se9, int qiz_se10,String QB_VERSION);
	public List<Setexam> customlist();
	public List<Bindquiz> searchdetail(String EB_NO);
}
