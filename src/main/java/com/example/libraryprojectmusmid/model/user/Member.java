package com.example.libraryprojectmusmid.model.user;

import com.example.libraryprojectmusmid.model.transactions.Transaction;

import java.util.List;

public class Member extends User {
    private String memberID;
    private String fullName;
    private String major;
    private String email;
    private List<Transaction> borrowedItems;

    public Member(String username, String password, String memberID, String fullName, String major, String email) {
        super(username, password);
        this.role = Role.MEMBER; // Sesuaikan sesuai enum Role
        this.memberID = memberID;
        this.fullName = fullName;
        this.major = major;
        this.email = email;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMajor() {
        return major;
    }

    public String getEmail() {
        return email;
    }

    public List<Transaction> getBorrowedItems() {
        return borrowedItems;
    }
}
