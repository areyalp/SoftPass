package softpass;
public class COMPort {

	String currentOwner;
	String name;
	int portType;
	public String getCurrentOwner() {
		return currentOwner;
	}
	public void setCurrentOwner(String currentOwner) {
		this.currentOwner = currentOwner;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPortType() {
		return portType;
	}
	public void setPortType(int portType) {
		this.portType = portType;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
