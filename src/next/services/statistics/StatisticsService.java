package next.services.statistics;

import java.util.Date;
import java.util.List;


public interface StatisticsService {

	
	public List<Statistics> getdetail_1(String selectgrade, Date single_m);
	
	public List<Statistics> getdetail_2(String selectgrade, Date start_m, Date end_m);

	public List<Statistics> getdetail_3(String selectgrade, Date single_m);
	
	public List<Statistics> getdetail_4(String selectgrade, Date start_m, Date end_m);
	
	public List<Statistics> getScoretotal_1();
}
