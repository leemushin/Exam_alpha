package next.services.customexam;

public class Customexam {
	private String EB_NO;//考卷代號(table:EXAM_BASE裡)
	private String EB_NAME;//考卷名稱(table:EXAM_BASE裡)
	private String EB_MAIN;//考卷內容(table:EXAM_BASE裡)
	
	public Customexam(){
	}
	
	public String getEB_NO() {
		return EB_NO;
	}
	public void setEB_NO(String EB_NO) {
		this.EB_NO = EB_NO;
	}	
	public String getEB_NAME() {
		return EB_NAME;
	}
	public void setEB_NAME(String EB_NAME) {
		this.EB_NAME = EB_NAME;
	}	
	public String getEB_MAIN() {
		return EB_MAIN;
	}
	public void setEB_MAIN(String EB_MAIN) {
		this.EB_MAIN = EB_MAIN;
	}	

}
