package exam.service;

public class Exam {
	private String QB_BOOK; 		//題目冊別
	private String QB_VERSION;		//題目版本
	private String QB_CHAPTER;		//題目章別
	private String QB_SECTION;		//題目節別
	private String QB_LEVEL;		//題目級別
	private String QB_NO;			//題目號碼
	private String QB_CODE;			//題目代號
	private String QB_MAIN;			//題目主要內容(改為預備欄位,依情況使用)
	private String QB_PIC;			//題目圖片
	private String QB_ANSWER;		//題目正確解答
	private String QB_EXPLAIN;		//題目詳解
	private String usr_choice;		//題目詳解
	private String is_right;		//答對或錯
	private String is_fill;			//題目是否為填充
	private String is_pick;			//題目是否為選擇
	
	public Exam(){	
	}
	
	public Exam(String QB_BOOK,String QB_VERSION,String QB_CHAPTER,String QB_SECTION,String QB_LEVEL,String QB_NO,String QB_CODE,String QB_MAIN,String QB_PIC,String QB_ANSWER,String QB_EXPLAIN,String is_fill,String is_pick) {
		this.QB_BOOK = QB_BOOK;
		this.QB_VERSION = QB_VERSION;
		this.QB_CHAPTER = QB_CHAPTER;
		this.QB_SECTION = QB_SECTION;
		this.QB_LEVEL = QB_LEVEL;
		this.QB_NO = QB_NO;
		this.QB_CODE = QB_CODE;
		this.QB_MAIN = QB_MAIN;
		this.QB_PIC = QB_PIC;
		this.QB_ANSWER = QB_ANSWER;
		this.QB_EXPLAIN = QB_EXPLAIN;
		this.is_fill = is_fill;
		this.is_pick = is_pick;	
	}
	public Exam(String QB_EXPLAIN,String QB_ANSWER,String usr_choice,String is_right){	
		this.QB_EXPLAIN = QB_EXPLAIN;
		this.QB_ANSWER = QB_ANSWER;
		this.usr_choice = usr_choice;	
		this.is_right = is_right;	
	}
	
	public String getQB_BOOK() {
		return QB_BOOK;
	}
	public void setQB_BOOK(String QB_BOOK) {
		this.QB_BOOK = QB_BOOK;
	}	
	public String getQB_VERSION() {
		return QB_VERSION;
	}
	public void setQB_VERSION(String QB_VERSION) {
		this.QB_VERSION = QB_VERSION;
	}
	public String getQB_CHAPTER() {
		return QB_CHAPTER;
	}
	public void setQB_CHAPTER(String QB_CHAPTER) {
		this.QB_CHAPTER = QB_CHAPTER;
	}
	public String getQB_SECTION() {
		return QB_SECTION;
	}
	public void setQB_SECTION(String QB_SECTION) {
		this.QB_SECTION = QB_SECTION;
	}
	public String getQB_LEVEL() {
		return QB_LEVEL;
	}
	public void setQB_LEVEL(String QB_LEVEL) {
		this.QB_LEVEL = QB_LEVEL;
	}
	public String getQB_NO() {
		return QB_NO;
	}
	public void setQB_NO(String QB_NO) {
		this.QB_NO = QB_NO;
	}	
	public String getQB_MAIN() {
		return QB_MAIN;
	}
	public void setQB_MAIN(String QB_MAIN) {
		this.QB_MAIN = QB_MAIN;
	}	
	public String getQB_CODE() {
		return QB_CODE;
	}
	public void setQB_CODE(String QB_CODE) {
		this.QB_CODE = QB_CODE;
	}		
	public String getQB_PIC() {
		return QB_PIC;
	}
	public void setQB_PIC(String QB_PIC) {
		this.QB_PIC = QB_PIC;
	}	
	public String getQB_ANSWER() {
		return QB_ANSWER;
	}
	public void setQB_ANSWER(String QB_ANSWER) {
		this.QB_ANSWER = QB_ANSWER;
	}	
	public String getusr_choice() {
		return usr_choice;
	}
	public void setusr_choice(String usr_choice) {
		this.usr_choice = usr_choice;
	}	
	public String getQB_EXPLAIN() {
		return QB_EXPLAIN;
	}
	public void setQB_EXPLAIN(String QB_EXPLAIN) {
		this.QB_EXPLAIN = QB_EXPLAIN;
	}	
	public String getis_right() {
		return is_right;
	}
	public void setis_right(String is_right) {
		this.is_right = is_right;
	}	
	public String getis_fill() {
		return is_fill;
	}
	public void setis_fill(String is_fill) {
		this.is_fill = is_fill;
	}	
	public String getis_pick() {
		return is_pick;
	}
	public void setis_pick(String is_pick) {
		this.is_pick = is_pick;
	}

}
