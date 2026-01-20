
import controller.LibraryController;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LibraryController controller = new LibraryController();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Library Management System ===");
            System.out.println("1. Borrow a book");
            System.out.println("2. Return a book");
            System.out.println("3. View current loans for a member");
            System.out.println("4. List available books");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Book ID: ");
                    int bookId = scanner.nextInt();
                    System.out.print("Enter Member ID: ");
                    int memberId = scanner.nextInt();
                    System.out.print("Enter due date (YYYY-MM-DD): ");
                    String dateStr = scanner.next();
                    LocalDate dueDate = LocalDate.parse(dateStr);

                    System.out.println(controller.borrowBook(bookId, memberId, dueDate));
                    break;

                case 2:
                    System.out.print("Enter Loan ID: ");
                    int loanId = scanner.nextInt();
                    System.out.println(controller.returnBook(loanId));
                    break;

                case 3:
                    System.out.print("Enter Member ID: ");
                    int memId = scanner.nextInt();
                    System.out.println(controller.viewCurrentLoans(memId));
                    break;

                case 4:
                    System.out.println(controller.listAvailableBooks());
                    break;

                case 5:
                    System.out.println("Goodbye!");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}