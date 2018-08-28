package next.services.queryscores;

import java.sql.Timestamp;

public class Scoredata {
	private String SB_ACCOUNT;//�ӵ��������p�b��
	private String SB_QUIZNAME;//�ը��W��
	private String SB_QUIZTOTAL;//�ը��`�D��
	private String SB_QUIZCORRECT;//�ը����T�D��
	private Timestamp SB_QUIZIN;//�i�J�ը��ɶ�
	private Timestamp SB_QUIZOUT;//���}�ը��ɶ�
	private String SB_QUIZEXPLAIN;//���}�ݸѵ����ɶ�
	private String SB_QUIZTYPE;//�ը�����
	private String SB_POINT;//�Ӧ��������
	private String SB_ANSWERTIME;//�Ӧ�����ɶ�
	private String REMARK;			//�O�_�������O
	private String SB_ID;			//
	private String SB_BONUS;		//���y����
	private String Point_month;		//�C�����
	private String Point_semester;	//�Ǵ�����
	
	
	public Scoredata(){	
	}
	
	public Scoredata(String SB_ACCOUNT,String SB_QUIZNAME,String SB_QUIZTOTAL,String SB_QUIZCORRECT,Timestamp SB_QUIZIN,Timestamp SB_QUIZOUT,String SB_QUIZEXPLAIN,String SB_QUIZTYPE,String SB_POINT,String SB_ANSWERTIME,String REMARK, String SB_ID,String Point_semester){	
		this.SB_ACCOUNT = SB_ACCOUNT;
		this.SB_QUIZNAME = SB_QUIZNAME;
		this.SB_QUIZTOTAL = SB_QUIZTOTAL;
		this.SB_QUIZCORRECT = SB_QUIZCORRECT;
		this.SB_QUIZIN = SB_QUIZIN;
		this.SB_QUIZOUT = SB_QUIZOUT;
		this.SB_QUIZEXPLAIN = SB_QUIZEXPLAIN;
		this.SB_QUIZTYPE = SB_QUIZTYPE;
		this.SB_POINT = SB_POINT;
		this.SB_ANSWERTIME = SB_ANSWERTIME;
		this.REMARK = REMARK;
		this.SB_ID = SB_ID;
		this.Point_semester = Point_semester;
	}

	public String getSB_ACCOUNT() {
		return SB_ACCOUNT;
	}
	public void setSB_ACCOUNT(String SB_ACCOUNT) {
		this.SB_ACCOUNT = SB_ACCOUNT;
	}	
	public String getSB_QUIZNAME() {
		return SB_QUIZNAME;
	}
	public void setSB_QUIZNAME(String SB_QUIZNAME) {
		this.SB_QUIZNAME = SB_QUIZNAME;
	}		
	public String getSB_QUIZTOTAL() {
		return SB_QUIZTOTAL;
	}
	public void setSB_QUIZTOTAL(String SB_QUIZTOTAL) {
		this.SB_QUIZTOTAL = SB_QUIZTOTAL;
	}		
	public String getSB_QUIZCORRECT() {
		return SB_QUIZCORRECT;
	}
	public void setSB_QUIZCORRECT(String SB_QUIZCORRECT) {
		this.SB_QUIZCORRECT = SB_QUIZCORRECT;
	}		
	public Timestamp getSB_QUIZIN() {
		return SB_QUIZIN;
	}
	public void setSB_QUIZIN(Timestamp SB_QUIZIN) {
		this.SB_QUIZIN = SB_QUIZIN;
	}
	public Timestamp getSB_QUIZOUT() {
		return SB_QUIZOUT;
	}
	public void setSB_QUIZOUT(Timestamp SB_QUIZOUT) {
		this.SB_QUIZOUT = SB_QUIZOUT;
	}
	public String getSB_QUIZEXPLAIN() {
		return SB_QUIZEXPLAIN;
	}
	public void setSB_QUIZEXPLAIN(String SB_QUIZEXPLAIN) {
		this.SB_QUIZEXPLAIN = SB_QUIZEXPLAIN;
	}	
	public String getSB_QUIZTYPE() {
		return SB_QUIZTYPE;
	}
	public void setSB_QUIZTYPE(String SB_QUIZTYPE) {
		this.SB_QUIZTYPE = SB_QUIZTYPE;
	}		
	public String getSB_POINT() {
		return SB_POINT;
	}
	public void setSB_POINT(String SB_POINT) {
		this.SB_POINT = SB_POINT;
	}	
	public String getSB_ANSWERTIME() {
		return SB_ANSWERTIME;
	}
	public void setSB_ANSWERTIME(String SB_ANSWERTIME) {
		this.SB_ANSWERTIME = SB_ANSWERTIME;
	}	
	public String getREMARK() {
		return REMARK;
	}
	public void setREMARK(String REMARK) {
		this.REMARK = REMARK;
	}
	public String getSB_ID() {
		return SB_ID;
	}
	public void setSB_ID(String SB_Aryexplain) {
		this.SB_ID = SB_Aryexplain;
	}
	public String getSB_BONUS() {
		return SB_BONUS;
	}
	public void setSB_BONUS(String SB_BONUS) {
		this.SB_BONUS = SB_BONUS;
	}
	public String getPoint_month() {
		return Point_month;
	}
	public void setPoint_month(String Point_month) {
		this.Point_month = Point_month;
	}
	public String getPoint_semester() {
		return Point_semester;
	}
	public void setPoint_semester(String Point_semester) {
		this.Point_semester = Point_semester;
	}
}
