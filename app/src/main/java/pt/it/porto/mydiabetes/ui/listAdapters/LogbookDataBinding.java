package pt.it.porto.mydiabetes.ui.listAdapters;

public class LogbookDataBinding {

	private CarbsDataBinding ch;
	private InsulinRegDataBinding ins;
	private GlycemiaDataBinding bg;
	
	public CarbsDataBinding get_ch(){
		return ch;
	}
	
	public void set_ch(CarbsDataBinding ch){
		this.ch = ch;
	}
	
	public InsulinRegDataBinding get_ins(){
		return ins;
	}
	
	public void set_ins(InsulinRegDataBinding ins){
		this.ins = ins;
	}
	
	public GlycemiaDataBinding get_bg(){
		return bg;
	}
	
	public void set_bg(GlycemiaDataBinding bg){
		this.bg = bg;
	}
	

}
