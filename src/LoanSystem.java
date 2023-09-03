import java.util.*;

class User {
    String username;

    public User(String username) {
        this.username = username;
    }
}

class Customer extends User {
    List<Loan> loans = new ArrayList<>();

    public Customer(String username) {
        super(username);
    }
}

class Admin extends User {
    public Admin(String username) {
        super(username);
    }

    public void createLoan(Customer customer, double principal, double interestRate, int tenure) {
        Loan loan = new Loan(customer.username, principal, interestRate, tenure);
        customer.loans.add(loan);
    }
}

class Loan {
    String adminUsername;
    String customerUsername;
    double principal;
    double interestRate;
    int tenure;
    List<Double> emiPayments = new ArrayList<>();

    public Loan(String adminUsername, double principal, double interestRate, int tenure) {
        this.adminUsername = adminUsername;
        this.principal = principal;
        this.interestRate = interestRate;
        this.tenure = tenure;
    }

    public void calculateEmis() {
        double interest = principal * tenure * interestRate / 100;
        double totalAmount = principal + interest;
        double emi = totalAmount / (tenure * 12);
        for (int i = 0; i < tenure * 12; i++) {
            emiPayments.add(emi);
        }
    }
}

public class LoanSystem {
    Map<String, User> users = new HashMap<>();

    public void createUser(String username, boolean isAdmin) {
        if (users.containsKey(username)) {
            System.out.println("Username already exists!");
            return;
        }
        User user = isAdmin ? new Admin(username) : new Customer(username);
        users.put(username, user);
    }

    public void makeEmiPayment(String username, int loanIndex, double amount) {
        if (users.containsKey(username) && users.get(username) instanceof Customer) {
            Customer customer = (Customer) users.get(username);
            if (loanIndex >= 0 && loanIndex < customer.loans.size()) {
                Loan loan = customer.loans.get(loanIndex);
                loan.emiPayments.remove(0);
            }
        }
    }

    public Loan fetchLoanInfo(String username, int loanIndex) {
        if (users.containsKey(username) && users.get(username) instanceof Customer) {
            Customer customer = (Customer) users.get(username);
            if (loanIndex >= 0 && loanIndex < customer.loans.size()) {
                return customer.loans.get(loanIndex);
            }
        }
        return null;
    }

    public List<Loan> fetchAllLoansForCustomer(String username) {
        if (users.containsKey(username) && users.get(username) instanceof Admin) {
            Customer customer = (Customer) users.get(username);
            return customer.loans;
        }
        return null;
    }

    public static void main(String[] args) {
        LoanSystem loanSystem = new LoanSystem();

        loanSystem.createUser("admin", true);
        loanSystem.createUser("customer1", false);
        loanSystem.createUser("customer2", false);

        Admin admin = (Admin) loanSystem.users.get("admin");
        Customer customer1 = (Customer) loanSystem.users.get("customer1");

        admin.createLoan(customer1, 10000, 10, 2);
        Loan loan = customer1.loans.get(0);
        loan.calculateEmis();

        loanSystem.makeEmiPayment("customer1", 0, loan.emiPayments.get(0));
        loanSystem.makeEmiPayment("customer1", 0, loan.emiPayments.get(0));

        System.out.println("Loan Info: " + loanSystem.fetchLoanInfo("customer1", 0));

        loanSystem.createUser("customer3", false);
        Customer customer3 = (Customer) loanSystem.users.get("customer3");
        admin.createLoan(customer3, 15000, 12, 3);
        Loan loan3 = customer3.loans.get(0);
        loan3.calculateEmis();

        System.out.println("All Loans for Customer3: " + loanSystem.fetchAllLoansForCustomer("customer3"));
    }
}
