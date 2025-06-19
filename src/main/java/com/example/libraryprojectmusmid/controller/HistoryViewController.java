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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HistoryViewController {

    @FXML private VBox historyContainer;
    @FXML private Button backButton;

    private final LoanService loanService = LoanService.getInstance();
    private Member currentMember;

    @FXML
    public void initialize() {
        Object user = AuthenticationService.getInstance().getCurrentUser();
        if (user instanceof Member) {
            this.currentMember = (Member) user;
        }

        if (currentMember == null) {
            historyContainer.getChildren().add(new Label("Gagal memuat data pengguna."));
            return;
        }
        loadAndDisplayHistory();
    }

    private void loadAndDisplayHistory() {
        historyContainer.getChildren().clear();

        // 1. Ambil SEMUA transaksi untuk member ini
        List<Transaction> allUserTransactions = loanService.getAllTransactionsForMember(currentMember);

        if (allUserTransactions.isEmpty()) {
            Label noHistoryLabel = new Label("Anda belum memiliki riwayat peminjaman.");
            noHistoryLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
            historyContainer.getChildren().add(noHistoryLabel);
            return;
        }

        // 2. LOGIKA INTI: Kelompokkan transaksi berdasarkan ISBN buku
        Map<String, List<Transaction>> groupedByIsbn = allUserTransactions.stream()
                .collect(Collectors.groupingBy(trx -> trx.getBookItem().getBook().getISBN()));

        // 3. Tampilkan setiap buku yang unik beserta jumlah peminjamannya
        for (Map.Entry<String, List<Transaction>> entry : groupedByIsbn.entrySet()) {
            List<Transaction> transactionsForBook = entry.getValue();
            Book book = transactionsForBook.get(0).getBookItem().getBook(); // Ambil detail buku dari transaksi pertama
            int borrowCount = transactionsForBook.size(); // Jumlah peminjaman adalah ukuran list

            historyContainer.getChildren().add(createHistoryCard(book, borrowCount));
        }
    }

    /**
     * Membuat satu "kartu" UI untuk setiap buku dalam riwayat.
     * @param book Objek buku yang unik.
     * @param count Berapa kali buku ini dipinjam.
     * @return Node (HBox) yang merepresentasikan kartu UI.
     */
    private Node createHistoryCard(Book book, int count) {
        // Cover Buku
        ImageView cover = new ImageView();
        try (InputStream imageStream = getClass().getResourceAsStream(book.getCoverImagePath())) {
            cover.setImage(new Image(imageStream != null ? imageStream : Objects.requireNonNull(getClass().getResourceAsStream("/images/blank-cover.png"))));
        } catch (Exception e) {
            cover.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/blank-cover.png"))));
        }
        cover.setFitHeight(80);
        cover.setFitWidth(55);

        // Detail Teks
        Label titleLabel = new Label(book.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label authorLabel = new Label(book.getAuthor());
        Label countLabel = new Label("Anda meminjam sebanyak " + count + " kali");
        countLabel.setStyle("-fx-font-style: italic;");

        VBox textDetails = new VBox(5, titleLabel, authorLabel, countLabel);

        // Gabungkan semua ke dalam HBox (tanpa tombol)
        HBox card = new HBox(20, cover, textDetails);
        HBox.setHgrow(textDetails, Priority.ALWAYS);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #B9D9EB; -fx-background-radius: 20; -fx-padding: 15;");

        return card;
    }

    @FXML
    private void handleBackButton() {
        NavigationUtil.navigateTo(backButton, "/com/example/libraryprojectmusmid/view/MemberDashboard.fxml");
    }
}