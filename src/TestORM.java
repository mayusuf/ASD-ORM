import entity.User;
import orm.SimpleORM;

public class TestORM {
    public static void main(String[] args) {

        SimpleORM<User> orm = new SimpleORM<>(User.class);

        // Create (Insert)
//        User newUser = new User();
//        newUser.setName("Mahbub Bhai");
//        newUser.setEmail("mahbub@gmail.com");
//        newUser.setAge(45);
//        orm.save(newUser);

        // Read (Select)
        User user = orm.findById(4);
        System.out.println(user);

        // Update
//        if (user != null) {
//            user.setName("Abu Yusuf");
//            orm.update(user);
//        }

        // Delete
        orm.delete(1);
    }
}