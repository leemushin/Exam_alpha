package next.services.bindquiz;

import java.util.Collection;
import java.util.List;

public interface BindquizService {
	public List<Bindquiz> searchquiz(String quiz_keyin);
	
	public List<Bindquiz>  doupdate(String upd_CODE,String upd_VERSION, String upd_BOOK, String upd_CHAPTER, String upd_SECTION,
			String upd_LEVEL, String upd_NO, String upd_ANSWER, String upd_PIC, String upd_EXPLAIN);
	
	public List<Bindquiz> searchlastquiz();
	
	public void deletequiz(String quiz_keyin);
}
