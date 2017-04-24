package softpass;

public class Transaction {
	private int id;
	private String name;
	private double Amount;
	private double tax;
	
	public Transaction(int id, String name, double Amount, double tax) {
		this.id = id;
		this.name = name;
		this.Amount = Amount;
		this.tax = tax;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public double getAmount() {
		return Amount;
	}

	public double getTax() {
		return tax;
	}

	@Override
	public String toString() {
		return name;
	}

}
