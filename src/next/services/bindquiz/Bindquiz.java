package next.services.bindquiz;

public class Bindquiz {
	private int exam_id;
	private String QB_CODE;//�D�إN�X
	private String QB_VERSION;//�D�ت���
	private String QB_BOOK;//�D�إU�O
	private String QB_CHAPTER;//�D�س��O
	private String QB_SECTION;//�D�ظ`�O
	private String QB_LEVEL;//�D������
	private String QB_NO;//�D�ؽs��
	private String QB_ANSWER;//�D�إ���
	private String QB_PIC;//�D�عϤ���m
	private String QB_EXPLAIN;//�D�ظԸѦ�m
	private String isfill;//�O�_����R
	private String ispick;//�O�_�����
	
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
