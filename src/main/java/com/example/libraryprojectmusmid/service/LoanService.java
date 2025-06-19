package com.example.libraryprojectmusmid.service;

import com.example.libraryprojectmusmid.model.book.BookItem;
import com.example.libraryprojectmusmid.model.book.BookStatus;
import com.example.libraryprojectmusmid.model.transactions.Transaction;
import com.example.libraryprojectmusmid.model.transactions.TransactionStatus;
import com.example.libraryprojectmusmid.model.user.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Singleton untuk mengelola semua logika bisnis terkait peminjaman dan pengembalian buku.
 */
public class LoanService {

    // =================================================================
    // BAGIAN BARU: IMPLEMENTASI SINGLETON PATTERN
    // =================================================================
    private static LoanService instance;
    private final List<Transaction> allTransactions;
    public static final int MAX_LOANS = 4; // Aturan maksimal peminjaman

    /**
     * Constructor dibuat private untuk mencegah pembuatan objek dari luar.
     */
    private LoanService() {
        this.allTransactions = new ArrayList<>();
        // Di aplikasi nyata, data transaksi akan dimuat dari database di sini.
    }

    /**
     * Metode untuk mendapatkan satu-satunya instance dari LoanService.
     * @return instance tunggal LoanService.
     */
    public static synchronized LoanService getInstance() {
        if (instance == null) {
            instance = new LoanService();
        }
        return instance;
    }


    // =================================================================
    // FUNGSI KUNCI BARU: UNTUK MENGECEK KUOTA PINJAMAN
    // =================================================================
    /**
     * Mendapatkan semua transaksi pinjaman yang masih aktif (status BORROWED)
     * untuk seorang member spesifik.
     * @param member Member yang akan dicek.
     * @return List dari transaksi yang aktif.
     */
    public List<Transaction> getActiveLoansForMember(Member member) {
        if (member == null) {
            return new ArrayList<>(); // Kembalikan list kosong jika member null
        }
        return allTransactions.stream()
                .filter(trx -> trx.getMember().equals(member) && trx.getStatus() == TransactionStatus.BORROWED)
                .collect(Collectors.toList());
    }


    // =================================================================
    // LOGIKA BISNIS YANG DISEMPURNAKAN
    // =================================================================
    /**
     * Memproses peminjaman buku baru.
     * Nama metode diubah menjadi 'borrowBook' agar lebih intuitif.
     * @param bookItem Item buku fisik yang akan dipinjam.
     * @param member Member yang meminjam.
     * @return true jika peminjaman berhasil, false jika gagal.
     */
    public boolean borrowBook(BookItem bookItem, Member member) {
        // VALIDASI TAMBAHAN: Cek kuota peminjaman member terlebih dahulu.
        if (getActiveLoansForMember(member).size() >= MAX_LOANS) {
            System.err.println("Gagal: Kuota peminjaman untuk " + member.getFullName() + " sudah penuh.");
            return false;
        }

        // Validasi ketersediaan buku (dari kode Anda, sudah benar)
        if (bookItem.getStatus() != BookStatus.AVAILABLE) {
            System.err.println("Gagal: Buku '" + bookItem.getBook().getTitle() + "' sedang tidak tersedia.");
            return false;
        }

        // Proses transaksi (dari kode Anda, sudah benar)
        String newTransactionID = "TRX-" + System.currentTimeMillis();
        Transaction newLoan = new Transaction(newTransactionID, bookItem, member);
        allTransactions.add(newLoan);
        bookItem.setStatus(BookStatus.BORROWED);

        System.out.println("Sukses: Transaksi peminjaman " + newTransactionID + " berhasil dibuat.");
        return true;
    }

    public List<Transaction> getAllTransactionsForMember(Member member) {
        if (member == null) {
            return new ArrayList<>();
        }
        return allTransactions.stream()
                .filter(trx -> trx.getMember().equals(member))
                .collect(Collectors.toList());
    }

    /**
     * Memproses pengembalian buku.
     * Logika ini sudah bagus dan tidak perlu diubah.
     * @param transaction Transaksi yang akan diselesaikan.
     * @return true jika pengembalian berhasil, false jika gagal.
     */
    public boolean processReturn(Transaction transaction) {
        if (transaction.getStatus() == TransactionStatus.RETURNED || transaction.getStatus() == TransactionStatus.OVERDUE) {
            System.err.println("Gagal: Transaksi ini sudah diselesaikan.");
            return false;
        }

        transaction.setActualReturnDate(LocalDate.now());

        // Cek keterlambatan
        if (transaction.getActualReturnDate().isAfter(transaction.getDueDate())) {
            transaction.setStatus(TransactionStatus.OVERDUE);
            System.out.println("Peringatan: Pengembalian terlambat!");
        } else {
            transaction.setStatus(TransactionStatus.RETURNED);
        }

        transaction.getBookItem().setStatus(BookStatus.AVAILABLE);
        System.out.println("Sukses: Buku '" + transaction.getBookItem().getBook().getTitle() + "' telah dikembalikan.");
        return true;
    }

    /**
     * Mengembalikan semua transaksi yang pernah tercatat.
     * Berguna untuk laporan atau riwayat admin.
     * @return List dari semua transaksi.
     */
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(allTransactions); // Kembalikan salinan untuk keamanan
    }
}