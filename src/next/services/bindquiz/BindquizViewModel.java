package next.services.bindquiz;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

import next.services.bindquiz.Bindquiz;
import next.services.bindquiz.BindquizService;
import next.services.bindquiz.BindquizServiceImpl;

public class BindquizViewModel {
	private BindquizService service = new BindquizServiceImpl();
	private ListModel<Bindquiz> BindquizModel;
	private ListModel<Bindquiz> LastquizModel;
	private boolean readonly = true;//�d�߼Ҧ��ϥ�
	private String quiz_keyin;//�j�M����
	
    private String upd_CODE;//�D�إN�X
    private String upd_VERSION;//�D�ت���
    private String upd_BOOK;//�D�إU�O
    private String upd_CHAPTER;//�D�س��O
    private String upd_SECTION;//�D�ظ`�O
    private String upd_LEVEL;//�D������
    private String upd_NO;//�D�ؽs��
    private String upd_ANSWER;//�D�ص���
    private String upd_PIC;//�D�عϤ���m
    private String upd_EXPLAIN;//�D�ظԸѦ�m
    private String isfill;//��R,�w�]��false
    private String ispick;//���,�w�]��true
    
	public void setquiz_keyin(String quiz_keyin) {//��J���D��
		this.quiz_keyin = quiz_keyin;
	}
	public String getquiz_keyin() {//��J���D��
		return quiz_keyin;
	}
	public void setupd_VERSION(String upd_VERSION) {
		this.upd_VERSION = upd_VERSION;
	}
	public String getupd_VERSION() {
		return upd_VERSION;
	}
	public void setupd_BOOK(String upd_BOOK) {
		this.upd_BOOK = upd_BOOK;
	}
	public String getupd_BOOK() {
		return upd_BOOK;
	}
	public void setupd_CHAPTER(String upd_CHAPTER) {
		this.upd_CHAPTER = upd_CHAPTER;
	}
	public String getupd_CHAPTER() {
		return upd_CHAPTER;
	}
	public void setupd_SECTION(String upd_SECTION) {
		this.upd_SECTION = upd_SECTION;
	}
	public String getupd_SECTION() {
		return upd_SECTION;
	}
	public void setupd_LEVEL(String upd_LEVEL) {
		this.upd_LEVEL = upd_LEVEL;
	}
	public String getupd_LEVEL() {
		return upd_LEVEL;
	}
	public void setupd_NO(String upd_NO) {
		this.upd_NO = upd_NO;
	}
	public String getupd_NO() {
		return upd_NO;
	}
	public void setupd_ANSWER(String upd_ANSWER) {
		this.upd_ANSWER = upd_ANSWER;
	}
	public String getupd_ANSWER() {
		return upd_ANSWER;
	}
	public void setupd_PIC(String upd_PIC) {
		this.upd_PIC = upd_PIC;
	}
	public String getupd_PIC() {
		return upd_PIC;
	}
	public void setupd_EXPLAIN(String upd_EXPLAIN) {
		this.upd_EXPLAIN = upd_EXPLAIN;
	}
	public String getupd_EXPLAIN() {
		return upd_EXPLAIN;
	}
	public void setupd_CODE(String upd_CODE) {
		this.upd_CODE = upd_CODE;
	}
	public String getupd_CODE() {
		return upd_CODE;
	}
	public boolean isReadonly() {//�P�_textbox��disabled
		return readonly;
	}
	public void setReadonly(boolean ro) {//�P�_textbox��disabled
		this.readonly=ro;
	}
	public ListModel<Bindquiz> getlastquizModel(){
		LastquizModel = new ListModelList<Bindquiz>(service.searchlastquiz());
		return LastquizModel;
	}
	public ListModel<Bindquiz> getquizModel(){
		return BindquizModel;
	}
	//�j�M�D��
	@Command
	@NotifyChange("*")//�q��binder���s���J
	public void search_quiz(){
		BindquizModel = new ListModelList<Bindquiz>(service.searchquiz(quiz_keyin));
	}
	//����D��
	@Command
	@NotifyChange("*")//�q��binder���s���J
	public void go_update(){
		BindquizModel = new ListModelList<Bindquiz>(service.doupdate(upd_CODE,upd_VERSION,upd_BOOK,
				upd_CHAPTER,upd_SECTION,upd_LEVEL,upd_NO,upd_ANSWER,upd_PIC,upd_EXPLAIN));
		setReadonly(true);
	}
	//�R���D��
	@Command
	public void do_delete(){
		Messagebox.show("�Y�N�R�����D�ظ��,�O�_�T�{����?", "�T�{", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION
				, new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event evt) throws InterruptedException {
		        if (evt.getName().equals("onOK")) {
		        	service.deletequiz(quiz_keyin);}//���UOK�����R���ʧ@
		    }
		});
		
	}
	
	
}
