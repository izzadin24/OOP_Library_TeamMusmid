package com.example.libraryprojectmusmid.service;

import com.example.libraryprojectmusmid.model.user.User;
import com.example.libraryprojectmusmid.model.user.Admin;
import com.example.libraryprojectmusmid.model.user.Member;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserService {

    private static UserService instance;
    private final List<User> userList;
    private final String memberCsvPath = "data/members.csv";

    private UserService() {
        this.userList = new ArrayList<>();
        userList.add(new Admin("111", "111", "S001", "Admin Perpustakaan"));
        loadMembersFromCsv();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private void loadMembersFromCsv() {
        File file = new File(memberCsvPath);
        if (!file.exists()) {
            try {
                Files.createDirectories(Paths.get("data"));
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(memberCsvPath))) {
                    writer.write("memberID,fullName,major,email,username,password\n");
                }
            } catch (IOException e) {
                System.err.println("Gagal membuat file members.csv baru: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(memberCsvPath))) {
            br.readLine(); // Lewati header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(",");
                if (values.length < 6) continue;
                String memberID = values[0].trim();
                String fullName = values[1].trim();
                String major = values[2].trim();
                String email = values[3].trim();
                String username = values[4].trim();
                String password = values[5].trim();
                if (findUserByUsername(username) == null) {
                    userList.add(new Member(username, password, memberID, fullName, major, email));
                }
            }
            System.out.println("Data member berhasil dimuat dari " + memberCsvPath);
        } catch (IOException e) {
            System.err.println("Error memuat member dari CSV: " + e.getMessage());
        }
    }

    // =================================================================
    // METODE BARU UNTUK MENYIMPAN DATA MEMBER
    // =================================================================
    private void saveMembersToCsv() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(memberCsvPath))) {
            bw.write("memberID,fullName,major,email,username,password\n");
            for (User user : userList) {
                if (user instanceof Member) {
                    Member member = (Member) user;
                    String line = String.join(",",
                            member.getMemberID(),
                            member.getFullName(),
                            member.getMajor(),
                            member.getEmail(),
                            member.getUsername(),
                            member.getPassword() //kok error?
                    );
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saat menyimpan member ke CSV: " + e.getMessage());
        }
    }

    /**
     * Registrasi member baru. Termasuk validasi duplikat dan menyimpan ke CSV.
     * @return true jika registrasi berhasil, false jika username atau ID sudah ada.
     */
    public boolean registerMember(String username, String password, String memberID, String fullName, String major, String email) {
        boolean idExists = userList.stream().anyMatch(user -> user instanceof Member && ((Member) user).getMemberID().equals(memberID));
        boolean usernameExists = findUserByUsername(username) != null;

        if (idExists || usernameExists) {
            System.err.println("Registrasi gagal: Member ID atau Username sudah ada.");
            return false;
        }

        Member newMember = new Member(username, password, memberID, fullName, major, email);
        userList.add(newMember);
        saveMembersToCsv(); // Simpan perubahan ke file CSV
        System.out.println("Registrasi member baru berhasil: " + fullName);
        return true;
    }

    // Metode lain tidak perlu diubah
    public User findUserByUsername(String username) {
        return userList.stream().filter(user -> user.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userList);
    }
}