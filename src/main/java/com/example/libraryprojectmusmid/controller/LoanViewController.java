package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.model.book.Book;
import com.example.libraryprojectmusmid.model.book.BookItem;
import com.example.libraryprojectmusmid.model.book.FictionBook;
import com.example.libraryprojectmusmid.model.user.Member;
import com.example.libraryprojectmusmid.service.AuthenticationService;
import com.example.libraryprojectmusmid.service.BookService;
import com.example.libraryprojectmusmid.service.LoanService;
import com.example.libraryprojectmusmid.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LoanViewController {

    @FXML private Label loanAlertLabel;
    @FXML private ScrollPane recommendationScrollPane;
    @FXML private HBox recommendationsBox;
    @FXML private TextField searchField;
    @FXML private StackPane searchResultContainer;
    @FXML private HBox searchResultPane;
    @FXML private Label notFoundLabel;
    @FXML private ImageView searchCoverImage;
    @FXML private Label judulLabel;
    @FXML private Label pengarangLabel;
    @FXML private Label isbnLabel;
    @FXML private Label genreLabel;
    @FXML private Label stokLabel;
    @FXML private Label tersediaLabel;
    @FXML private Button searchBorrowButton;
    @FXML private Button backButton;

    private final BookService bookService = BookService.getInstance();
    private final LoanService loanService = LoanService.getInstance();
    private Member currentMember;
    private int loansLeft;
    private Book currentlySearchedBook;

    @FXML
    public void initialize() {
        currentMember = (Member) AuthenticationService.getInstance().getCurrentUser();
        if (currentMember == null) return;
        updateLoanQuota();
        populateRecommendations();
        setupRecommendationScroll();
        searchResultPane.setVisible(false);
        notFoundLabel.setVisible(false);
        searchResultContainer.setStyle("-fx-background-color: transparent;");
    }

    private VBox createBookCard(Book book) {
        // Container utama
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 10;");
        card.setPrefWidth(150);
        card.setPrefHeight(220);

        // ImageView untuk Cover (dengan ukuran terkontrol)
        ImageView coverImageView = new ImageView();
        try {
            InputStream imageStream = getClass().getResourceAsStream(book.getCoverImagePath());
            coverImageView.setImage(new Image(imageStream != null ? imageStream :
                    Objects.requireNonNull(getClass().getResourceAsStream("/images/blank-cover.png"))));
        } catch (Exception e) {
            coverImageView.setImage(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/blank-cover.png"))));
        }
        coverImageView.setFitHeight(100); // Lebih kecil dari versi full
        coverImageView.setFitWidth(80);
        coverImageView.setPreserveRatio(true);

        // Label untuk Judul (dengan ellipsis jika terlalu panjang)
        Label titleLabel = new Label(book.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        titleLabel.setMaxWidth(130);
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-text-fill: #333333;");

        // Label Pengarang
        Label authorLabel = new Label(book.getAuthor());
        authorLabel.setFont(Font.font("System", 10));
        authorLabel.setMaxWidth(130);
        authorLabel.setStyle("-fx-text-fill: #555555;");

        // Tombol Pinjam
        Button borrowButton = new Button("Pinjam");
        borrowButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        borrowButton.setOnAction(event -> handleBorrow(book));

        card.getChildren().addAll(coverImageView, titleLabel, authorLabel, borrowButton);
        return card;
    }

    private void setupRecommendationScroll() {
        recommendationScrollPane.setOnScroll(event -> {
            if (event.getDeltaY() != 0 && recommendationsBox.getWidth() > recommendationScrollPane.getWidth()) {
                recommendationScrollPane.setHvalue(recommendationScrollPane.getHvalue() - event.getDeltaY() / recommendationsBox.getWidth());
                event.consume();
            }
        });
    }

    private void updateLoanQuota() {
        int activeLoans = loanService.getActiveLoansForMember(currentMember).size();
        loansLeft = LoanService.MAX_LOANS - activeLoans;
        loanAlertLabel.setText(String.format("Alert: sisa buku yang dapat dipinjam adalah %d dari total %d untuk saat ini",
                loansLeft, LoanService.MAX_LOANS));
        updateAllBorrowButtonsState(loansLeft <= 0);
    }

    private void populateRecommendations() {
        List<Book> allBooks = bookService.getAllBooks();
        Collections.shuffle(allBooks);
        List<Book> recommendations = allBooks.subList(0, Math.min(allBooks.size(), 5)); // Hanya tampilkan 5 rekomendasi
        recommendationsBox.getChildren().clear();
        recommendationsBox.setSpacing(20);
        for (Book book : recommendations) {
            recommendationsBox.getChildren().add(createBookCard(book));
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            searchResultPane.setVisible(false);
            notFoundLabel.setVisible(false);
            searchResultContainer.setStyle("-fx-background-color: transparent;");
            return;
        }

        List<Book> searchResult = bookService.searchBooks(keyword);
        searchResultContainer.setStyle("-fx-background-color: #B9D9EB; -fx-background-radius: 20;");

        if (searchResult.isEmpty()) {
            notFoundLabel.setVisible(true);
            searchResultPane.setVisible(false);
        } else {
            currentlySearchedBook = searchResult.get(0);
            displaySearchResult(currentlySearchedBook);
            notFoundLabel.setVisible(false);
            searchResultPane.setVisible(true);
        }
    }

    private void displaySearchResult(Book book) {
        judulLabel.setText(book.getTitle());
        pengarangLabel.setText(book.getAuthor());
        isbnLabel.setText(book.getISBN());
        genreLabel.setText((book instanceof FictionBook) ? ((FictionBook) book).getGenre() : "Non-Fiksi");
        stokLabel.setText(String.valueOf(bookService.getTotalStockByIsbn(book.getISBN())));
        tersediaLabel.setText(String.valueOf(bookService.getAvailableStockByIsbn(book.getISBN())));

        try (InputStream imageStream = getClass().getResourceAsStream(book.getCoverImagePath())) {
            if (imageStream != null) {
                searchCoverImage.setImage(new Image(imageStream));
            } else {
                searchCoverImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/blank-cover.png"))));
            }
        } catch (Exception e) {
            searchCoverImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/blank-cover.png"))));
        }
    }

    @FXML
    private void handleSearchBorrow() {
        if (currentlySearchedBook != null) {
            handleBorrow(currentlySearchedBook);
        }
    }

    private void handleBorrow(Book book) {
        if (loansLeft <= 0) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Kuota peminjaman Anda sudah habis.");
            return;
        }

        BookItem itemToBorrow = bookService.findAvailableBookItem(book.getISBN());
        if (itemToBorrow != null) {
            boolean success = loanService.borrowBook(itemToBorrow, currentMember);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Buku '" + book.getTitle() + "' berhasil dipinjam.");
                updateLoanQuota();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Peminjaman gagal karena kuota Anda sudah penuh.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Maaf, semua stok untuk buku '" + book.getTitle() + "' sedang dipinjam.");
        }
    }

    @FXML
    private void handleBackButton() {
        NavigationUtil.navigateTo(backButton, "/com/example/libraryprojectmusmid/view/MemberDashboard.fxml");
    }

    private void updateAllBorrowButtonsState(boolean disabled) {
        searchBorrowButton.setDisable(disabled);
        recommendationsBox.getChildren().forEach(node -> {
            if (node instanceof VBox) {
                ((VBox) node).getChildren().forEach(child -> {
                    if (child instanceof Button) {
                        child.setDisable(disabled);
                    }
                });
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}