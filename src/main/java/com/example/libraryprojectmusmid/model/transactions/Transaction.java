package com.example.libraryprojectmusmid.model.transactions;
import com.example.libraryprojectmusmid.model.book.BookItem;
import com.example.libraryprojectmusmid.model.user.*;

import java.time.LocalDate;

public class Transaction {
    private String transactionID;
    private BookItem bookItem; // Referensi ke objek buku fisik yang dipinjam
    private Member member;     // Referensi ke objek anggota yang meminjam
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate actualReturnDate; // Bisa null jika belum dikembalikan
    private TransactionStatus status;

    /**
     * Konstruktor untuk membuat transaksi baru saat peminjaman terjadi.
     * @param transactionID ID unik untuk transaksi ini.
     * @param bookItem Objek BookItem yang dipinjam.
     * @param member Objek Member yang meminjam.
     */
    public Transaction(String transactionID, BookItem bookItem, Member member) {
        this.transactionID = transactionID;
        this.bookItem = bookItem;
        this.member = member;
        this.borrowDate = LocalDate.now(); // Tanggal peminjaman adalah hari ini
        this.dueDate = this.borrowDate.plusDays(7); // Jatuh tempo 7 hari dari sekarang
        this.status = TransactionStatus.BORROWED; // Status awal adalah dipinjam
        this.actualReturnDate = null; // Belum ada tanggal pengembalian
    }

    // --- Getters ---
    // Tidak semua atribut butuh setter, karena sebagian besar diatur saat pembuatan atau pengembalian.

    public String getTransactionID() {
        return transactionID;
    }

    public BookItem getBookItem() {
        return bookItem;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    // --- Setters untuk proses pengembalian ---

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}

enum TransactionStatus {

    BORROWED,


    RETURNED,


    OVERDUE
}
