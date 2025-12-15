import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors; 

//bus route class
class BusRoute implements Serializable {
    private static final long serialVersionUID = 1L;

    private String routeNumber;
    private String busType;
    private String destination;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    public BusRoute(String routeNumber, String busType, String destination, LocalTime departureTime, LocalTime arrivalTime) {
        this.routeNumber = routeNumber;
        this.busType = busType;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public String getRouteNumber() {
        return routeNumber;
    }
    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getBusType() {
        return busType;
    }
    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }
    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }
    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        return String.format("Route №%s | Type: %s | Destination point: %s | Departure time: %s | Arrival time: %s",
                routeNumber, busType, destination, departureTime.format(fmt), arrivalTime.format(fmt));
    }
}

//account class
class UserAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private boolean isAdmin;

    public UserAccount(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
}

//main class
public class BusStationApp {
    private static final String ROUTES_DATA_FILE = "bus_routes.dat";
    private static final String ACCOUNTS_DATA_FILE = "user_accounts.dat";

    private List<BusRoute> routes = new ArrayList<>();
    private List<UserAccount> users = new ArrayList<>();

    private Scanner scanner = new Scanner(System.in);
    private UserAccount currentUser;

    public static void main(String[] args) {
        BusStationApp app = new BusStationApp();
        app.loadData();
        app.authMenu();
    }
    
    //data loader
    private void loadData() {
        //routes loader
        File routeFile = new File(ROUTES_DATA_FILE);
        if (routeFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(routeFile))) {
                routes = (List<BusRoute>) ois.readObject();
            } catch (Exception e) {
                System.out.println("Error with loading routes data: " + e.getMessage());
                routes = new ArrayList<>();
            }
        } else {
            //if there's no files, creating new (example)
            routes.add(new BusRoute("1", "Express", "Minsk", LocalTime.of(9, 0), LocalTime.of(12, 0)));
            saveRoutes();
        }

        //accounts loader
        File accountsFile = new File(ACCOUNTS_DATA_FILE);
        if (accountsFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(accountsFile))) {
                users = (List<UserAccount>) ois.readObject();
            } catch (Exception e) {
                System.out.println("Error with loading accounts data: " + e.getMessage());
                users = new ArrayList<>();
            }
        } else {
            //if there's no accounts - creating new admin and user
            users.add(new UserAccount("admin", "admin123", true));
            users.add(new UserAccount("user", "user123", false));
            saveUsers();
        }
    }

    //routes saver
    private void saveRoutes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ROUTES_DATA_FILE))) {
            oos.writeObject(routes);
        } catch (IOException e) {
            System.out.println("Error with saving rotes data: " + e.getMessage());
        }
    }

    //accounts saver
    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ACCOUNTS_DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.out.println("Error with saving accounts data: " + e.getMessage());
        }
    }

    //authorisation menu
    private void authMenu() {
        System.out.println("=== Welcome to the bus depot database ===");
        while (true) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            Optional<UserAccount> userOpt = users.stream()
                    .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                    .findFirst();

            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                System.out.println("Log in succesful. Hello, " + currentUser.getUsername() + "!");
                if (currentUser.isAdmin()) {
                    adminMenu();
                } else {
                    userMenu();
                }
                break;
            } else {
                System.out.println("Username or password is incorrect. Try again.");
            }
        }
    }

    //user menu
    private void userMenu() {
        while (true) {
            System.out.println("\n=== User menu ===");
            System.out.println("1. Search route");
            System.out.println("2. Sort routes");
            System.out.println("3. Show routes, that arrive less than 12 hours before time");
            System.out.println("0. Log out");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    userSearchMenu();
                    break;
                case "2":
                    userSortMenu();
                    break;
                case "3":
                    showRoutesByArrivalTimeLimit();
                    break;
                case "0":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Incorrect choice. Try again.");
            }
        }
    }

    //admin menu
    private void adminMenu() {
        while (true) {
            System.out.println("\n=== Administrator menu ===");
            System.out.println("1. Search route");
            System.out.println("2. Sort routes");
            System.out.println("3. Show routes, that arrive less than 12 hours before time");
            System.out.println("4. Add route");
            System.out.println("5. Edit route");
            System.out.println("6. Delete rote");
            System.out.println("7. Manage accounts");
            System.out.println("0. Log out");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    userSearchMenu();
                    break;
                case "2":
                    userSortMenu();
                    break;
                case "3":
                    showRoutesByArrivalTimeLimit();
                    break;
                case "4":
                    addRoute();
                    break;
                case "5":
                    editRoute();
                    break;
                case "6":
                    deleteRoute();
                    break;
                case "7":
                    manageAccountsMenu();
                    break;
                case "0":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Incorrect choice. Try again.");
            }
        }
    }

    //search menu
    private void userSearchMenu() {
        System.out.println("\nChoose data to search:");
        System.out.println("1. By route number");
        System.out.println("2. By bus type");
        System.out.println("3. By destination point");
        System.out.println("0. Return");

        String choice = scanner.nextLine();
        List<BusRoute> result = new ArrayList<>();

        switch (choice) {
            case "1":
                System.out.print("Enter route number: ");
                String routeNum = scanner.nextLine().trim();
                result = routes.stream()
                        .filter(r -> r.getRouteNumber().equalsIgnoreCase(routeNum))
                        .collect(Collectors.toList());
                break;
            case "2":
                System.out.print("Enter bus type: ");
                String type = scanner.nextLine().trim();
                result = routes.stream()
                        .filter(r -> r.getBusType().equalsIgnoreCase(type))
                        .collect(Collectors.toList());
                break;
            case "3":
                System.out.print("Enter destination point: ");
                String dest = scanner.nextLine().trim();
                result = routes.stream()
                        .filter(r -> r.getDestination().equalsIgnoreCase(dest))
                        .collect(Collectors.toList());
                break;
            case "0":
                return;
            default:
                System.out.println("Incorrect choice.");
                return;
        }

        if (result.isEmpty()) {
            System.out.println("No results.");
        } else {
            System.out.println("Results:");
            result.forEach(System.out::println);
        }
    }

    //sort menu
    private void userSortMenu() {
        System.out.println("\nChoose data to sort by:");
        System.out.println("1. By route number");
        System.out.println("2. By bus type");
        System.out.println("3. By destination point");
        System.out.println("0. Return");

        String choice = scanner.nextLine();

        Comparator<BusRoute> comparator;

        switch (choice) {
            case "1":
                comparator = Comparator.comparing(BusRoute::getRouteNumber);
                break;
            case "2":
                comparator = Comparator.comparing(BusRoute::getBusType);
                break;
            case "3":
                comparator = Comparator.comparing(BusRoute::getDestination);
                break;
            case "0":
                return;
            default:
                System.out.println("Incorrect choice.");
                return;
        }

        List<BusRoute> sorted = new ArrayList<>(routes);
        sorted.sort(comparator);
        System.out.println("Sorted routes:");
        sorted.forEach(System.out::println);
    }

    //show routes, that arrive less than 12 hours before  user defined time
    private void showRoutesByArrivalTimeLimit() {
        System.out.print("Enter time (HH:mm type): ");
        String input = scanner.nextLine();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime userTime;
        try {
            userTime = LocalTime.parse(input, fmt);
        } catch (Exception e) {
            System.out.println("Invalid time format.");
            return;
        }

        LocalTime limitTime = userTime.minusHours(12);

        List<BusRoute> filtered = routes.stream()
                .filter(r -> !r.getArrivalTime().isAfter(limitTime))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("There is no routes, arriving less than 12 hours before " + userTime.format(fmt));
        } else {
            System.out.println("Results:");
            filtered.forEach(System.out::println);
        }
    }

    //create new route
    private void addRoute() {
        System.out.println("\n=== Create new rote ===");
        System.out.print("Enter route number: ");
        String routeNumber = scanner.nextLine().trim();

        System.out.print("Ente rbus type: ");
        String busType = scanner.nextLine().trim();

        System.out.print("Enter destination point: ");
        String destination = scanner.nextLine().trim();

        LocalTime departureTime = readTime("Enter departure time (HH:mm): ");
        if (departureTime == null) return;

        LocalTime arrivalTime = readTime("Enter arrival time (HH:mm): ");
        if (arrivalTime == null) return;

        routes.add(new BusRoute(routeNumber, busType, destination, departureTime, arrivalTime));
        saveRoutes();
        System.out.println("Route created succesfully.");
    }

    //route editing
    private void editRoute() {
        System.out.println("\n=== Route editing ===");
        System.out.print("Enter route number to edit: ");
        String routeNumber = scanner.nextLine().trim();

        Optional<BusRoute> optRoute = routes.stream()
                .filter(r -> r.getRouteNumber().equals(routeNumber))
                .findFirst();

        if (!optRoute.isPresent()) {
            System.out.println("Route №" + routeNumber + " is not founded.");
            return;
        }

        BusRoute route = optRoute.get();

        System.out.println("Old route data: ");
        System.out.println(route);

        System.out.print("Enter new bus type (leave blank to skip): ");
        String busType = scanner.nextLine().trim();
        if (!busType.isEmpty())
            route.setBusType(busType);

        System.out.print("Enter new destination point (leave blank to skip): ");
        String destination = scanner.nextLine().trim();
        if (!destination.isEmpty())
            route.setDestination(destination);

        LocalTime departureTime = readTimeOrSkip("Enter new departure time (HH:mm) (leave blank to skip): ");
        if (departureTime != null)
            route.setDepartureTime(departureTime);

        LocalTime arrivalTime = readTimeOrSkip("Enter new arrival time (HH:mm) (leave blank to skip): ");
        if (arrivalTime != null)
            route.setArrivalTime(arrivalTime);

        saveRoutes();
        System.out.println("Route updated succesfully.");
    }

    //route deleting
    private void deleteRoute() {
        System.out.println("\n=== Route deleting ===");
        System.out.print("Enter route number to delete: ");
        String routeNumber = scanner.nextLine().trim();

        Iterator<BusRoute> it = routes.iterator();
        boolean found = false;
        while (it.hasNext()) {
            BusRoute r = it.next();
            if (r.getRouteNumber().equals(routeNumber)) {
                it.remove();
                found = true;
                break;
            }
        }

        if (found) {
            saveRoutes();
            System.out.println("Route deleted succesfully.");
        } else {
            System.out.println("Route with this number is not founded.");
        }
    }

    //time reader
    private LocalTime readTime(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        try {
            return LocalTime.parse(input, fmt);
        } catch (Exception e) {
            System.out.println("Invalid time format.");
            return null;
        }
    }

    //time reader
    private LocalTime readTimeOrSkip(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        if (input.trim().isEmpty()) {
            return null;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        try {
            return LocalTime.parse(input, fmt);
        } catch (Exception e) {
            System.out.println("Invalid time format.");
            return null;
        }
    }

    //account manage menu
    private void manageAccountsMenu() {
        while (true) {
            System.out.println("\n=== Account manage menu ===");
            System.out.println("1. Add account");
            System.out.println("2. Edit account");
            System.out.println("3. Delete account");
            System.out.println("4. Show all users");
            System.out.println("0. Return");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addUserAccount();
                    break;
                case "2":
                    editUserAccount();
                    break;
                case "3":
                    deleteUserAccount();
                    break;
                case "4":
                    listUsers();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Incorrect choice.");
            }
        }
    }

    //add user
    private void addUserAccount() {
        System.out.println("\n=== Adding user ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
            System.out.println("User with this username already exists.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Give this user admin rights? (y/n): ");
        String isAdminStr = scanner.nextLine().trim().toLowerCase();
        boolean isAdmin = isAdminStr.equals("y");

        users.add(new UserAccount(username, password, isAdmin));
        saveUsers();
        System.out.println("User added succesfully.");
    }

    //edit user
    private void editUserAccount() {
        System.out.println("\n=== Editing user ===");
        System.out.print("Edit username of user to edit: ");
        String username = scanner.nextLine().trim();

        Optional<UserAccount> optUser = users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();

        if (!optUser.isPresent()) {
            System.out.println("User is not founded.");
            return;
        }

        UserAccount user = optUser.get();

        System.out.print("Enter new password (leave blank to skip): ");
        String password = scanner.nextLine().trim();
        if (!password.isEmpty())
            user = new UserAccount(user.getUsername(), password, user.isAdmin());

        System.out.print("New admin rights (y/n/skip): ");
        String adminStr = scanner.nextLine().trim().toLowerCase();

        if (adminStr.equals("y")) {
            user = new UserAccount(user.getUsername(), user.getPassword(), true);
        } else if (adminStr.equals("n")) {
            user = new UserAccount(user.getUsername(), user.getPassword(), false);
        }

        //deleting old and adding updated user data
        users.removeIf(u -> u.getUsername().equals(username));
        users.add(user);

        saveUsers();
        System.out.println("Account updated succesfully.");
    }

    //delete user
    private void deleteUserAccount() {
        System.out.println("\n=== Deleting user ===");
        System.out.print("Enter username of user to delete: ");
        String username = scanner.nextLine().trim();

        if (username.equals(currentUser.getUsername())) {
            System.out.println("You can not delete yourself.");
            return;
        }

        boolean removed = users.removeIf(u -> u.getUsername().equals(username));

        if (removed) {
            saveUsers();
            System.out.println("User deleted succesfully.");
        } else {
            System.out.println("User is not founded.");
        }
    }

    //show all users
    private void listUsers() {
        System.out.println("\nUser list:");
        for (UserAccount u : users) {
            System.out.printf("%s (Admin: %s)%n", u.getUsername(), u.isAdmin() ? "Yes" : "No");
        }
    }
}