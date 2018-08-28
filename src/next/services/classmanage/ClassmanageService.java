package next.services.classmanage;

import java.util.List;

public interface ClassmanageService {
	
	public List<Classmanage> class_teach(String user_Acc);
	
	public List<Classmanage> class_deatail(String NO);
	
	public List<Classmanage> class_point(String NO);

}
