package esw.blackjack;

public class Card {

    String value;
    String type; 

    Card(String value, String type) {
        this.value = value;
        this.type = type;
    }

    
    @Override
    public String toString() {
        return "card" + type + value;
    }

    public int getValue() {
        if ("AJQK".contains(value)) { 
            if ("A".equals(value)) {
                return 11;
            }
            return 10;
        }
        return Integer.parseInt(value); // 2-10
    }

    public boolean isAce() {
      
        return "A".equals(value);
    }

    public String getImagePath() {
       
        return "/sprites/PNG/Cards/" + toString() + ".png";
    }
}