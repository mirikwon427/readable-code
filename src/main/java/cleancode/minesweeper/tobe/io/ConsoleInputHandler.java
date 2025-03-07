package cleancode.minesweeper.tobe.io;

import java.util.Scanner;

public class ConsoleInputHandler {
  private static final Scanner SCANNER = new Scanner(System.in);

  public void printCommentForSelectingCell() {
    System.out.println("선택할 좌표를 입력하세요. (예: a1)");
  }

  public String getUserInput() {
    return SCANNER.nextLine();
  }
}
