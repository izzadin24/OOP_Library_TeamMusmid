package com.example.libraryprojectmusmid.controller;

import com.example.libraryprojectmusmid.model.book.Book;
import com.example.libraryprojectmusmid.model.book.FictionBook;
import com.example.libraryprojectmusmid.model.transactions.Transaction;
import com.example.libraryprojectmusmid.service.LoanService;
import com.example.libraryprojectmusmid.util.NavigationUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.List;

public class StatisticViewController {

    // Elemen FXML
    @FXML private Label totalPinjamLabel;
    @FXML private Label totalKembaliLabel;
    @FXML private PieChart categoryChart;
    @FXML private TableView<Transaction> borrowedBooksTable;
    @FXML private TableColumn<Transaction, String> judulColumn;
    @FXML private TableColumn<Transaction, String> peminjamColumn;
    @FXML private TableColumn<Transaction, LocalDate> tglPinjamColumn;
    @FXML private TableColumn<Transaction, LocalDate> tglKembaliColumn;
    @FXML private Button backButton;

    // Service
    private final LoanService loanService = LoanService.getInstance();

    @FXML
    public void initialize() {
        loadSummaryStatistics();
        populateBorrowedBooksTable();
        loadCategoryChartData();
    }

    private void loadSummaryStatistics() {
        totalPinjamLabel.setText(String.valueOf(loanService.getMonthlyBorrowCount()));
        totalKembaliLabel.setText(String.valueOf(loanService.getMonthlyReturnCount()));
    }

    private void populateBorrowedBooksTable() {
        // Tentukan bagaimana setiap kolom mendapatkan datanya dari objek Transaction
        judulColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBookItem().getBook().getTitle()));
        peminjamColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMember().getFullName()));
        tglPinjamColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBorrowDate()));
        tglKembaliColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDueDate()));

        // Ambil semua data pinjaman aktif dan masukkan ke tabel
        borrowedBooksTable.setItems(FXCollections.observableArrayList(loanService.getAllActiveLoans()));
    }

    private void loadCategoryChartData() {
        long fictionCount = loanService.getAllActiveLoans().stream()
                .filter(trx -> trx.getBookItem().getBook() instanceof FictionBook)
                .count();

        long nonFictionCount = loanService.getAllActiveLoans().stream()
                .filter(trx -> !(trx.getBookItem().getBook() instanceof FictionBook))
                .count();

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Fiksi", fictionCount),
                        new PieChart.Data("Non-Fiksi", nonFictionCount));

        categoryChart.setData(pieChartData);
    }

    @FXML
    private void handleBackButton() {
        NavigationUtil.navigateTo(backButton, "/com/example/libraryprojectmusmid/view/AdminDashboard.fxml");
    }
}