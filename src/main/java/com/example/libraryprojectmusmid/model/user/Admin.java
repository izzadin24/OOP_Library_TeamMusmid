package com.example.libraryprojectmusmid.model.user;
import com.example.libraryprojectmusmid.model.user.*;

public class Admin extends User {
    private String staffID;
    private String fullName;

    public Admin(String username, String password, String staffID, String fullName) {
        super(username, password);
        this.role = Role.ADMIN; // Contoh: Role enum diasumsikan ada
        this.staffID = staffID;
        this.fullName = fullName;
    }

    public String getStaffID() {
        return staffID;
    }

    public String getFullName() {
        return fullName;
    }
}
