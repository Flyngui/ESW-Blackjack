package esw.blackjack;

public class Dealer extends Player {
	// 1 - ATRIBUTOS
	private boolean hiddenCard = true;
	// STRATEGY: regra de jogo do dealer (por defeito, parar no 17).
	private DealerStrategy strategy;

	// 2 - ACESSORES
	public boolean getHiddenCard() {
		return hiddenCard;
	}

	public void setStrategy(DealerStrategy strategy) {
		this.strategy = strategy;
	}

	// se a carta estiver escondida vai buscar só a segunda
	public Card getUpCard() {
		if (hand.isEmpty()) {
			return null;
		} else {
			return hand.get(1);
		}
	}

	// 3.1 - CONSTRUTOR DEFAULT
	public Dealer() {
		super("Dealer");
		strategy = new Parar17();
	}

	// Permite jogar com regra diferente para o dealer
	public Dealer(DealerStrategy strategy) {
		super("Dealer");
		this.strategy = strategy;
	}

	// 4 - COMPORTAMENTOS
	// caso a carta esteja escondida mostrar só o valor da segunda
	public int getVisibleSum() {
		if (!hiddenCard) {
			return sum();
		}
		return getUpCard().getValue();
	}

	// vira a cata escondida
	public void reveal() {
		hiddenCard = false;
	}

	// delega a decisao de pedir carta na estrategia configurada
	public boolean shouldHit() {
		return strategy.shouldHit(this);
	}

	// 5 - METODOS COMPLEMENTARES
	@Override
	public void clearHand() {
		super.clearHand();
		hiddenCard = true;
	}

	@Override
	public String toString() {
		if (!hiddenCard) {
			return super.toString();
		}
		// mostra so a carta que esta para cima
		return name + ": [##, " + getUpCard() + "] (" + getVisibleSum() + ")";
	}
}
