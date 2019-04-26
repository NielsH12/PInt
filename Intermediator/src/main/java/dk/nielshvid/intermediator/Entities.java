package dk.nielshvid.intermediator;

import java.time.LocalDate;
public class Entities {
    public static class Person {
        public String firstName;
        public String lastName;
        public String email;
    }
    public static class Sample {
        public LocalDate created;
        public LocalDate accessed;
        public LocalDate expiration;
        public Person owner;
        public int temperature;
    }
    public static class Pizza {
        public LocalDate expirationDate;
        public String ingredients;
        public boolean gluten;
        public int baketime;
    }
}
