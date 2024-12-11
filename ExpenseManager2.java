import java.io.*;
import java.util.*;

class ExpenseManager {
    private static final String USER_FILE = "users.txt";
    private static final String EXPENSE_FILE = "expenses.txt";
    private Map<String, String> users = new HashMap<>();
    private Map<String, List<Expense>> userExpenses = new HashMap<>();

    public static void main(String[] args) {
        ExpenseManager manager = new ExpenseManager();
        manager.loadUsers();
        manager.loadExpenses();
        manager.run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Expense Manager!");
        String currentUser = null;

        while (true) {
            if (currentUser == null) {
                System.out.println("1. Register\n2. Login\n3. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        register(scanner);
                        break;
                    case 2:
                        currentUser = login(scanner);
                        break;
                    case 3:
                        saveData();
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } else {
                System.out.println("1. Add Expense\n2. View Expenses\n3. View Category-wise Summation\n4. Logout");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addExpense(scanner, currentUser);
                        break;
                    case 2:
                        viewExpenses(currentUser);
                        break;
                    case 3:
                        viewCategorySummation(currentUser);
                        break;
                    case 4:
                        currentUser = null;
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        }
    }

    private void register(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (users.containsKey(username)) {
            System.out.println("Username already exists.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        users.put(username, password);
        userExpenses.put(username, new ArrayList<>());
        System.out.println("Registration successful.");
    }

    private String login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (users.containsKey(username) && users.get(username).equals(password)) {
            System.out.println("Login successful.");
            return username;
        } else {
            System.out.println("Invalid username or password.");
            return null;
        }
    }

    private void addExpense(Scanner scanner, String username) {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter Entertainment: ");
        String Entertainment= scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        Expense expense = new Expense(date, "Entertainment", amount);
        userExpenses.get(username).add(expense);
        System.out.println("Expense added.");
    }

    private void viewExpenses(String username) {
        List<Expense> expenses = userExpenses.get(username);
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        System.out.println("Date\t\tCategory\t\tAmount");
        for (Expense expense : expenses) {
            System.out.println(expense);
        }
    }

    private void viewCategorySummation(String username) {
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : userExpenses.get(username)) {
            categoryTotals.put(expense.getCategory(),
                    categoryTotals.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
        }

        System.out.println("Category\t\tTotal Amount");
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.println(entry.getKey() + "\t\t" + entry.getValue());
        }
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                users.put(parts[0], parts[1]);
                userExpenses.put(parts[0], new ArrayList<>());
            }
        } catch (IOException e) {
            System.out.println("No user data found.");
        }
    }

    private void loadExpenses() {
        try (BufferedReader reader = new BufferedReader(new FileReader(EXPENSE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String username = parts[0];
                String date = parts[1];
                String category = parts[2];
                double amount = Double.parseDouble(parts[3]);
                userExpenses.get(username).add(new Expense(date, category, amount));
            }
        } catch (IOException e) {
            System.out.println("No expense data found.");
        }
    }

    private void saveData() {
        try (BufferedWriter userWriter = new BufferedWriter(new FileWriter(USER_FILE));
                BufferedWriter expenseWriter = new BufferedWriter(new FileWriter(EXPENSE_FILE))) {

            for (Map.Entry<String, String> entry : users.entrySet()) {
                userWriter.write(entry.getKey() + "," + entry.getValue());
                userWriter.newLine();
            }

            for (Map.Entry<String, List<Expense>> entry : userExpenses.entrySet()) {
                for (Expense expense : entry.getValue()) {
                    expenseWriter.write(entry.getKey() + "," + expense);
                    expenseWriter.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }

    static class Expense {
        private String date;
        private String Entertainment;
        private double amount;

        public Expense(String date, String Entertainment, double amount) {
            this.date = date;
            this.Entertainment = Entertainment;
            this.amount = amount;
        }

        public String getCategory() {
            return Entertainment;
        }

        public double getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return date + "\t" + Entertainment + "\t" + amount;
        }
    }
}
