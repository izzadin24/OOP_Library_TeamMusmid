package com.example.libraryprojectmusmid.service;

import com.example.libraryprojectmusmid.model.user.*;


public class AuthenticationService {

    private static AuthenticationService instance;
    private final UserService userService; // BERUBAH: Dependensi ke UserService, bukan LibraryManager
    private User currentUser;

    /**
     * Konstruktor dibuat private.
     */
    private AuthenticationService() {
        // Mendapatkan instance dari UserService
        this.userService = UserService.getInstance();
        this.currentUser = null;
    }

    /**
     * Metode static untuk mendapatkan satu-satunya instance dari kelas ini.
     * @return Instance tunggal dari AuthenticationService.
     */
    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Memproses upaya login dari pengguna.
     * @param username Username yang diinput.
     * @param password Password yang diinput.
     * @return true jika login berhasil, false jika gagal.
     */
    public boolean login(String username, String password) {
        // BERUBAH: Mengambil data pengguna dari userService
        User userToAuthenticate = userService.findUserByUsername(username);

        if (userToAuthenticate != null && userToAuthenticate.authenticate(password)) {
            this.currentUser = userToAuthenticate; // Simpan pengguna yang berhasil login
            return true;
        }

        return false; // Gagal jika pengguna tidak ditemukan atau password salah
    }

    /**
     * Memproses logout pengguna.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Mendapatkan objek pengguna yang saat ini sedang login.
     * @return Objek User yang login, atau null jika tidak ada.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Metode bantuan untuk mendapatkan peran dari pengguna yang sedang login.
     * @return Enum Role (ADMIN/MEMBER), atau null jika tidak ada yang login.
     */
    public Role getCurrentUserRole() {
        if (currentUser != null) {
            return currentUser.getRole();
        }
        return null;
    }
}
