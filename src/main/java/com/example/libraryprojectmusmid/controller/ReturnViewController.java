package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.model.book.Book;
import com.example.libraryprojectmusmid.model.transactions.Transaction;
import com.example.libraryprojectmusmid.model.user.Member;
import com.example.libraryprojectmusmid.service.AuthenticationService;
import com.example.libraryprojectmusmid.service.LoanService;
import com.example.libraryprojectmusmid.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ReturnViewController {

    @FXML
    private VBox loanedBooksContainer;
    @FXML
    private Button backButton;

    private final LoanService loanService = LoanService.getInstance();
    private Member currentMember;

    @FXML
    public void initialize() {
        // Dapatkan pengguna yang sedang login dari AuthenticationService
        Object user = AuthenticationService.getInstance().getCurrentUser();
        if (user instanceof Member) {
            this.currentMember = (Member) user;
        }

        if (currentMember == null) {
            // Jika tidak ada user, tampilkan pesan error dan berhenti
            loanedBooksContainer.getChildren().add(new Label("Gagal memuat data pengguna. Silakan login kembali."));
            return;
        }
        // Muat dan tampilkan semua buku yang sedang dipinjam oleh user ini
        loadAndDisplayActiveLoans();
    }

    /**
     * Metode utama untuk mengambil data pinjaman aktif dan menampilkannya ke UI.
     */
    private void loadAndDisplayActiveLoans() {
        // Bersihkan tampilan sebelum memuat data baru untuk proses refresh
        loanedBooksContainer.getChildren().clear();

        List<Transaction> activeLoans = loanService.getActiveLoansForMember(currentMember);

        if (activeLoans.isEmpty()) {
            Label noLoansLabel = new Label("Anda tidak memiliki pinjaman aktif saat ini.");
            noLoansLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
            loanedBooksContainer.getChildren().add(noLoansLabel);
        } else {
            for (Transaction transaction : activeLoans) {
                // Untuk setiap transaksi, buat kartu UI dan tambahkan ke VBox
                loanedBooksContainer.getChildren().add(createLoanCard(transaction));
            }
        }
    }

    /**
     * Membuat satu "kartu" UI untuk setiap item buku yang dipinjam secara dinamis.
     * @param transaction Objek transaksi yang berisi semua info pinjaman.
     * @return Node (dalam kasus ini, HBox) yang merepresentasikan kartu UI.
     */
    private Node createLoanCard(Transaction transaction) {
        Book book = transaction.getBookItem().getBook();

        // 1. Cover Buku
        ImageView cover = new ImageView();
        try (InputStream imageStream = getClass().getResourceAsStream(book.getCoverImagePath())) {
            cover.setImage(new Image(imageStream != null ? imageStream : Objects.requireNonNull(getClass().getResourceAsStream("/images/blank-cover.png"))));
        } catch (Exception e) {
            cover.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/blank-cover.png"))));
        }
        cover.setFitHeight(100);
        cover.setFitWidth(70);

        // 2. Detail Teks (Judul, Pengarang, Sisa Hari)
        Label titleLabel = new Label(book.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label authorLabel = new Label(book.getAuthor());

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), transaction.getDueDate());
        Label dueDateLabel = new Label();
        if (daysLeft >= 0) {
            dueDateLabel.setText("Sisa hari : " + daysLeft + " hingga waktu pengembalian");
            dueDateLabel.setTextFill(Color.BLACK);
        } else {
            dueDateLabel.setText("TERLAMBAT " + Math.abs(daysLeft) + " HARI!");
            dueDateLabel.setTextFill(Color.RED);
            dueDateLabel.setStyle("-fx-font-weight: bold;");
        }

        VBox textDetails = new VBox(5, titleLabel, authorLabel, dueDateLabel);

        // 3. Tombol Kembalikan
        Button returnButton = new Button("Kembalikan");
        returnButton.setStyle("-fx-background-color: #FFD700; -fx-background-radius: 15; -fx-font-weight: bold;");
        // Aksi tombol diatur di sini, meneruskan transaksi yang spesifik
        returnButton.setOnAction(event -> handleReturn(transaction));

        // 4. Gabungkan semua ke dalam HBox
        HBox card = new HBox(20, cover, textDetails, new Region(), returnButton);
        HBox.setHgrow(textDetails, Priority.ALWAYS); // Membuat detail teks mengisi ruang kosong
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #B9D9EB; -fx-background-radius: 20; -fx-padding: 15;");

        return card;
    }

    /**
     * Logika yang dijalankan saat tombol "Kembalikan" di sebuah kartu diklik.
     * @param transaction Transaksi yang spesifik untuk dikembalikan.
     */
    private void handleReturn(Transaction transaction) {
        // Tampilkan dialog konfirmasi sebelum melakukan aksi
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Konfirmasi Pengembalian");
        confirmationAlert.setHeaderText("Anda akan mengembalikan buku: " + transaction.getBookItem().getBook().getTitle());
        confirmationAlert.setContentText("Apakah Anda yakin?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        // Hanya proses jika pengguna menekan tombol OK
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = loanService.processReturn(transaction);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Buku berhasil dikembalikan.");
                // PENTING: Muat ulang daftar buku agar UI ter-update dan buku yang dikembalikan hilang dari daftar
                loadAndDisplayActiveLoans();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Terjadi kesalahan saat proses pengembalian.");
            }
        }
    }

    @FXML
    private void handleBackButton() {
        NavigationUtil.navigateTo(backButton, "/com/example/libraryprojectmusmid/view/MemberDashboard.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}