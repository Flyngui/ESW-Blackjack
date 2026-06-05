package esw.blackjack;

import java.util.ArrayList;

public class Player {
	// 1 - ATRIBUTOS
	protected String name;
	protected ArrayList<Card> hand;
	protected int sum = 0;
	protected boolean standing = false;

	// 2 - ACESSORES
	public ArrayList<Card> getHand() {
		return hand;
	}

	public int getSum() {
		return sum;
	}

	public boolean getStanding() {
		return standing;
	}

	// 3.1 - CONSTRUTOR DEFAULT
	public Player(String name) {
		this.name = name;
		this.hand = new ArrayList<Card>();
	}

	// 4 - COMPORTAMENTOS
	// pedir carta
	public int hit(Card c) {
		if (standing == false) {
			hand.add(c);
		}
		return sum();
	}

	public void clearHand() {
		hand.clear();
		sum = 0;
		standing = false;
	}

	// soma o valor de todas as cartas na mao do jogador
	public int sum() {
		int total = 0;
		int aces = 0;
		for (Card c : hand) {
			if (c.isAce()) {
				aces++;
			}
			total += c.getValue();
		}
		// ases podem valer 1 ou 11, se houver algum e a mao rebentar muda os ases para
		// 1
		while (total > 21 && aces > 0) {
			total -= 10;
			aces--;
		}
		return sum = total;
	}

	public boolean isBust() {
		return sum() > 21;
	}

	// blackjack de primeira
	public boolean hasBlackJack() {
		return hand.size() == 2 && sum() == 21;
	}

	public int stand() {
		standing = true;
		return sum();
	}

	// 5 - METODOS COMPLEMENTARES
	@Override
	public String toString() {
		return name + ": " + hand + " (" + getSum() + ")";
	}

}
