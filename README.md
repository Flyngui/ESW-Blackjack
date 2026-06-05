# ESW-Blackjack

Jogo de Blackjack feito em Java para a cadeira de Engenharia de Software.

A lógica do jogo está separada da interface: as regras (baralho, mãos, apostas,
turno do dealer) vivem na classe Blackjack e podem ser jogadas de duas formas:

- Interface gráfica em JavaFX, com as cartas e fichas em sprites.
- Versão de terminal (ConsoleGame), que joga o mesmo motor sem GUI.

## Como correr

Versão gráfica:

    cd blackjack
    mvn javafx:run

Versão de terminal:

    cd blackjack
    mvn compile
    java -cp target/classes esw.blackjack.ConsoleGame

Testes:

    cd blackjack
    mvn test

Sprites: https://opengameart.org/content/boardgame-pack
