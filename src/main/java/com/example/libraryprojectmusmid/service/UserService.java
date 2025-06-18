package com.example.libraryprojectmusmid.service;

import com.example.libraryprojectmusmid.model.user.User;
import com.example.libraryprojectmusmid.model.user.Admin;
import com.example.libraryprojectmusmid.model.user.Member;
import com.example.libraryprojectmusmid.model.user.Role; // Pastikan Anda memiliki Enum Role

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service untuk mengelola semua data dan operasi terkait pengguna (Admin dan Member).
 * Mengimplementasikan Singleton Pattern.
 */
public class UserService {

    private static UserService instance;
    private final List<User> userList;

    private UserService() {
        this.userList = new ArrayList<>();
        userList.add(new Admin("admin01", "adminpass", "ADM01", "Anton Wijaya"));
        userList.add(new Member(
                "123",            // username (dianggap sebagai NIM)
                "123",            // password
                "M-TEST001",      // memberID
                "Mahasiswa Tes",  // fullName
                "Teknik Informatika", // major
                "tes@student.umm.ac.id" // email
        ));


        // Muat data Admin secara hardcode (lebih aman)
        userList.add(new Admin("admin", "admin123", "S001", "Admin Perpustakaan"));
        // Muat data Member dari file CSV
        loadMembersFromCsv("data/members.csv");
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Membaca file members.csv dan memuat datanya ke dalam userList.
     * @param filePath Path menuju file members.csv
     */
    private void loadMembersFromCsv(String filePath) {
        // Format CSV: MemberID,FullName,Major,Email,Username,Password
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Melewati baris header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 6) continue;

                String memberID = values[0].trim();
                String fullName = values[1].trim();
                String major = values[2].trim();
                String email = values[3].trim();
                String username = values[4].trim();
                String password = values[5].trim();

                userList.add(new Member(username, password, memberID, fullName, major, email));
            }
        } catch (IOException e) {
            System.err.println("Error loading members from CSV: " + e.getMessage());
        }
    }

    /**
     * Mencari pengguna berdasarkan username. Diperlukan oleh AuthenticationService.
     * @param username Username yang dicari.
     * @return Objek User jika ditemukan, atau null.
     */
    public User findUserByUsername(String username) {
        return userList.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Registrasi member baru. Termasuk validasi duplikat.
     * @return true jika registrasi berhasil, false jika username atau ID sudah ada.
     */
    public boolean registerMember(String username, String password, String memberID, String fullName, String major, String email) {
        // Validasi duplikat ID atau username
        boolean idExists = userList.stream().anyMatch(user -> user instanceof Member && ((Member) user).getMemberID().equals(memberID));
        boolean usernameExists = findUserByUsername(username) != null;

        if (idExists || usernameExists) {
            System.err.println("Registrasi gagal: Member ID atau Username sudah ada.");
            return false;
        }

        Member newMember = new Member(username, password, memberID, fullName, major, email);
        userList.add(newMember);
        // Di sini Anda bisa menambahkan logika untuk menyimpan kembali ke CSV
        System.out.println("Registrasi member baru berhasil: " + fullName);
        return true;
    }

    /**
     * Mengembalikan daftar semua pengguna dengan peran Member.
     * @return List dari objek Member.
     */
    public List<Member> getAllMembers() {
        return userList.stream()
                .filter(user -> user.getRole() == Role.MEMBER)
                .map(user -> (Member) user)
                .collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userList);
    }
}




















/*
package com.example.libraryprojectmusmid.service;

import com.example.libraryprojectmusmid.model.user.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    private static UserService instance;
    private final List<User> userList;


    private UserService() {
        this.userList = new ArrayList<>();
        loadInitialData(); // Memuat data dummy/awal
    }


    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }


    private void loadInitialData() {
        // Menambahkan data Admin dummy
        userList.add(new Admin("admin", "admin123", "S001", "Galih Wicaksono"));

        // Menambahkan data Member dummy
        userList.add(new Member("budi", "budi123", "M001", "Budi Santoso", "Informatics", "budi@umm.ac.id"));
        userList.add(new Member("siti", "siti123", "M002", "Siti Aminah", "Management", "siti@umm.ac.id"));
    }


    public User findUserByUsername(String username) {
        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }


    public List<User> getAllUsers() {
        return userList;
    }


    public List<Member> getAllMembers() {
        return userList.stream()
                .filter(user -> user.getRole() == Role.MEMBER)
                .map(user -> (Member) user)
                .collect(Collectors.toList());
    }


    public void addUser(User user) {
        // Di sini bisa ditambahkan logika untuk mengecek duplikasi username atau ID
        userList.add(user);
        // Nanti bisa ditambahkan logika untuk menyimpan ke file CSV
    }

    // Anda bisa menambahkan metode lain seperti updateUser, deleteUser, dll. di sini
}*/
