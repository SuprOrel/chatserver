package com.springchat.chat.repository;



import com.springchat.chat.models.User;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

//public class UserRepository {
//    static CrudRepository<User, Integer> users;
//    public static User get(String username) {
//        for(User user : users.findAll()) {
//            if(user.getName().equals(username)) return user;
//        }
//        return null;
//    }
//    public static void add(User value) {
//        users.save(value);
//    }
//    public static void remove(User user) {
//        users.delete(user);
//    }
//    public static void remove(int index) {
//        users.deleteById(index);
//    }
//    public static void remove(String username) {
//        remove(indexOf(username));
//    }
//    public static boolean containsUsername(String username) {
//        return indexOf(username) != -1;
//    }
//    public static int indexOf(String username) {
//        for(User user : users.findAll()) {
//            if(user.getName().equals(username)) return user.getId();
//        }
//        return -1;
//    }
//
//    public static String usernamesToString() {
//        StringBuilder builder = new StringBuilder();
//        for(User user : users.findAll()) {
//            builder.append(user.getName() + ',');
//        }
//        return builder.toString();
//    }
//}


public interface UserRepository extends CrudRepository<User, Integer> {

}
