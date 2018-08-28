package next.services.bindquiz;

public class Bindquiz {
	private int exam_id;
	private String QB_CODE;//題目代碼
	private String QB_VERSION;//題目版本
	private String QB_BOOK;//題目冊別
	private String QB_CHAPTER;//題目章別
	private String QB_SECTION;//題目節別
	private String QB_LEVEL;//題目難度
	private String QB_NO;//題目編號
	private String QB_ANSWER;//題目正解
	private String QB_PIC;//題目圖片位置
	private String QB_EXPLAIN;//題目詳解位置
	private String isfill;//是否為填充
	private String ispick;//是否為選擇
	
	public Bindquiz() {
	}
	public Bindquiz(String QB_CODE,String QB_PIC) {
		this.QB_CODE = QB_CODE;
		this.QB_PIC = QB_PIC;
	}
	public Integer getexam_id() {
		return exam_id;
	}
	public void setexam_id(Integer exam_id) {
		this.exam_id = exam_id;
	}

	public String getQB_CODE() {
		return QB_CODE;
	}
	public void setQB_CODE(String QB_CODE) {
		this.QB_CODE = QB_CODE;
	}
	public String getQB_VERSION() {
		return QB_VERSION;
	}
	public void setQB_VERSION(String QB_VERSION) {
		this.QB_VERSION = QB_VERSION;
	}
	public String getQB_BOOK() {
		return QB_BOOK;
	}
	public void setQB_BOOK(String QB_BOOK) {
		this.QB_BOOK = QB_BOOK;
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
	public String getQB_ANSWER() {
		return QB_ANSWER;
	}
	public void setQB_ANSWER(String QB_ANSWER) {
		this.QB_ANSWER = QB_ANSWER;
	}
	public String getQB_PIC() {
		return QB_PIC;
	}
	public void setQB_PIC(String QB_PIC) {
		this.QB_PIC = QB_PIC;
	}
	public String getQB_EXPLAIN() {
		return QB_EXPLAIN;
	}
	public void setQB_EXPLAIN(String QB_EXPLAIN) {
		this.QB_EXPLAIN = QB_EXPLAIN;
	}
	public String getisfill() {
		return isfill;
	}
	public void setisfill(String isfill) {
		this.isfill = isfill;
	}
	public String getispick() {
		return ispick;
	}
	public void setispick(String ispick) {
		this.ispick = ispick;
	}
}
