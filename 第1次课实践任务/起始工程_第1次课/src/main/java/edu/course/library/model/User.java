package edu.course.library.model;

public final class User {
    private final String id;
    private final String name;
    private final String type;
    private final String phone;
    private final String email;

    public User(String id, String name, String type, String phone) {
        this(id, name, type, phone, null);
    }

    public User(String id, String name, String type, String phone, String email) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.phone = phone;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
