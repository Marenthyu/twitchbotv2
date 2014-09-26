package package1;

public class mUser {
	String nickname ="";
	boolean op = false;
	
	public mUser(String nick, boolean isOp) {
		nickname = nick;
		op = isOp;
	}
	public mUser(String nick) {
		nickname = nick;
		op = false;
	}
	public void deop() {
		op=false;
	}
	public void setName(String newNick) {
		nickname = newNick;
	}
	public void op() {
		op = true;
	}
	public void setop(boolean opstatus) {
		op=opstatus;
	}
	public boolean isOp() {
		return op;
	}
	public String getNick() {
		return nickname;
	}
}
