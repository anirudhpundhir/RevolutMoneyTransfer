package entity.dto;

/** Request body for creating or updating a user. Null fields are left unchanged on update. */
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;

    public UserDTO() {
    }

    public UserDTO(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
