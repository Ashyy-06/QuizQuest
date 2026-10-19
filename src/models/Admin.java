package models;

public class Admin extends User
{
    public Admin(int userId, String username, String password)
    {
        super(userId, username, password);
    }

    @Override
    public String getRole()
    {
        return "Admin";
    }

    public void displayPermissions()
    {
        System.out.println("\nAdmin Permissions");
        System.out.println("1. Add Questions");
        System.out.println("2. Edit Questions");
        System.out.println("3. Delete Questions");
        System.out.println("4. View Quiz Results");
    }
}