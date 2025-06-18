package com.example.libraryprojectmusmid.service;

import com.example.libraryprojectmusmid.model.book.BookItem;
import com.example.libraryprojectmusmid.model.book.BookStatus; // Asumsi enum ini ada
import com.example.libraryprojectmusmid.model.user.Member;
import com.example.libraryprojectmusmid.model.transactions.Transaction;
import com.example.libraryprojectmusmid.model.transactions.TransactionStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanService {
    private final List<Transaction> transactionList = new ArrayList<>(); // Nanti bisa diganti database

    // INI ADALAH LOGIKA BISNISNYA
    public boolean processNewLoan(BookItem bookItem, Member member) {
        // 1. Validasi
        if (bookItem.getStatus() != BookStatus.AVAILABLE) {
            System.out.println("Gagal: Buku sedang tidak tersedia.");
            return false;
        }

        // 2. Buat ID unik untuk transaksi
        String newTransactionID = "TRX-" + System.currentTimeMillis();

        // 3. Buat objek Model Transaction
        Transaction newLoan = new Transaction(newTransactionID, bookItem, member);

        // 4. Ubah status objek lain yang terlibat
        bookItem.setStatus(BookStatus.BORROWED);

        // 5. Simpan transaksi baru
        transactionList.add(newLoan);
        System.out.println("Sukses: Transaksi peminjaman " + newTransactionID + " berhasil dibuat.");
        return true;
    }

    // INI JUGA LOGIKA BISNIS
    public boolean processReturn(Transaction transaction) {
        if (transaction.getStatus() == TransactionStatus.RETURNED) {
            System.out.println("Gagal: Transaksi ini sudah dikembalikan.");
            return false;
        }

        // Ubah status transaksi
        transaction.setActualReturnDate(LocalDate.now());
        transaction.setStatus(TransactionStatus.RETURNED);

        // Cek keterlambatan (logika tambahan)
        if (transaction.getActualReturnDate().isAfter(transaction.getDueDate())) {
            transaction.setStatus(TransactionStatus.OVERDUE); // Bisa juga tetap RETURNED tapi ada catatan denda
            System.out.println("Peringatan: Pengembalian terlambat!");
        }

        // Ubah status buku menjadi tersedia kembali
        transaction.getBookItem().setStatus(BookStatus.AVAILABLE);
        System.out.println("Sukses: Buku " + transaction.getBookItem().getBook().getTitle() + " telah dikembalikan.");
        return true;
    }

    public List<Transaction> getAllTransactions() {
        return transactionList;
    }
}