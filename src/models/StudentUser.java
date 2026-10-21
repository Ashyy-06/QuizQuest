package models;

public class StudentUser extends User{
    public StudentUser(int userId, String username, String password){
        super(userId, username, password);
    }

    @Override
    public String getRole(){
        return "Student";
    }

    public void displayPermissions(){
        System.out.println("\nStudent Permissions");
        System.out.println("1. Choose Subject");
        System.out.println("2. Attend Quiz");
        System.out.println("3. View Score");
        System.out.println("4. View Quiz History");
    }
}