package next.services.focus;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import next.services.focus.Focus;
import next.services.focus.FocusService;
import next.services.focus.FocusServiceImpl;

public class FocusViewModel {
	private FocusService service = new FocusServiceImpl();
	private ListModel<Focus> focusModel;
	Session s = Sessions.getCurrent();
	String book,ver,ch,se;//�˯�����-�U�O,����,���O,�`�O
	public ListModel<Focus> getfocusModel(){
		if(s.getAttribute("focus_condition") != null ){
			String alltmp = (String) s.getAttribute("focus_condition");
			String[] searchtmp = alltmp.split(",");//�j�M���I�Ȧs�r��
			book = searchtmp[0];
			ver = searchtmp[1];
			ch = searchtmp[2];
			se = searchtmp[3];
		}
		focusModel = new ListModelList<Focus>(service.findfocus(book, ver, ch, se));//�ϥζǨӪ���ƨӷj�M���I
		return focusModel;
	}
}
