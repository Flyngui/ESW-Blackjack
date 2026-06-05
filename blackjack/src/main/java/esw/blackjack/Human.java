package esw.blackjack;

public class Human extends Player {
	// 1 - ATRIBUTOS
	protected int currentBet = 0;
	protected int balance = 0;

	// 2 - ACESSORES
	public int getBalance() {
		return balance;
	}

	public int getCurrentBet() {
		return currentBet;
	}

	// 3.1 - CONSTRUTOR DEFAULT
	public Human(String name, int balance) {
		super(name);
		this.balance = balance;
	}

	// 4 - COMPORTAMENTOS
	public boolean placeBet(int amount) {
		if (amount <= 0 || amount > this.balance) {
			return false;
		}
		this.balance -= amount;
		this.currentBet = amount;
		return true;
	}

	public void winBet() {
		this.balance += this.currentBet * 2;
		this.currentBet = 0;
	}

	// blackjack direto ganha 3:2
	public void winBlackJack() {
		this.balance += this.currentBet + (this.currentBet * 3 / 2);
		this.currentBet = 0;
		this.standing = true;
	}

	// emapte
	public void tieBet() {
		this.balance += this.currentBet;
		this.currentBet = 0;
	}

	public void loseBet() {
		this.currentBet = 0;
	}

	// duplica bet mas só pode pedir 1 carta
	public int doubleDown(Card c) {
		if (balance >= currentBet) {
			balance -= currentBet;
			currentBet *= 2;
			int sum = hit(c);
			standing = true;
			return sum;
		}
		return -1;
	}

	// 5 - METODOS COMPLEMENTARES
	@Override
	public void clearHand() {
		super.clearHand();
		this.currentBet = 0;
	}

	@Override
	public String toString() {
		return super.toString() + " [balance=" + balance + ", bet=" + currentBet + "]";
	}
}
