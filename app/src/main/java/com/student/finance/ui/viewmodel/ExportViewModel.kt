package com.student.finance.ui.viewmodel

import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.repository.TransactionRepository
import com.student.finance.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val dataStoreManager: DataStoreManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    fun exportToCsv(
        startDate: Long? = null,
        endDate: Long? = null,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accountId = dataStoreManager.activeAccountId.first()
                val transactions = if (startDate != null && endDate != null) {
                    transactionRepository.getByDateRange(accountId, startDate, endDate).first()
                } else {
                    transactionRepository.getAll(accountId).first()
                }

                if (transactions.isEmpty()) {
                    withContext(Dispatchers.Main) { onError("Tidak ada data untuk diekspor") }
                    return@launch
                }

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "SmartMoneyManage_Export_$timeStamp.csv"
                
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)

                FileWriter(file).use { writer ->
                    writer.append("No,Tanggal,Jenis,Kategori,Jumlah,Catatan\n")
                    
                    transactions.forEachIndexed { index, transaction ->
                        val date = DateUtils.formatDate(transaction.date)
                        val type = if (transaction.type == com.student.finance.data.local.entity.TransactionType.INCOME) "Pemasukan" else "Pengeluaran"
                        val amount = transaction.amount.toString()
                        val desc = transaction.description ?: "-"
                        val category = "-"
                        
                        writer.append("${index + 1},$date,$type,$category,$amount,\"$desc\"\n")
                    }
                }

                withContext(Dispatchers.Main) { onSuccess(file.absolutePath) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError("Gagal mengekspor: ${e.message}") }
            }
        }
    }

    fun exportMonthlyRecap(
        month: Int,
        year: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accountId = dataStoreManager.activeAccountId.first()
                val start = DateUtils.startOfMonth(month, year)
                val end = DateUtils.endOfMonth(month, year)
                val transactions = transactionRepository.getByDateRange(accountId, start, end).first()

                if (transactions.isEmpty()) {
                    withContext(Dispatchers.Main) { onError("Tidak ada data untuk bulan $month/$year") }
                    return@launch
                }

                val income = transactions.filter { it.type == com.student.finance.data.local.entity.TransactionType.INCOME }.sumOf { it.amount }
                val expense = transactions.filter { it.type == com.student.finance.data.local.entity.TransactionType.EXPENSE }.sumOf { it.amount }
                val balance = income - expense

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "Rekap_${month}_${year}_$timeStamp.csv"
                
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)

                FileWriter(file).use { writer ->
                    writer.append("REKAPITULASI KEUANGAN\n")
                    writer.append("Bulan,$month/$year\n")
                    writer.append("Total Pemasukan,$income\n")
                    writer.append("Total Pengeluaran,$expense\n")
                    writer.append("Saldo,$balance\n\n")
                    writer.append("No,Tanggal,Jenis,Jumlah,Catatan\n")
                    
                    transactions.forEachIndexed { index, transaction ->
                        val date = DateUtils.formatDate(transaction.date)
                        val type = if (transaction.type == com.student.finance.data.local.entity.TransactionType.INCOME) "Pemasukan" else "Pengeluaran"
                        writer.append("${index + 1},$date,$type,${transaction.amount},\"${transaction.description ?: "-"}\"\n")
                    }
                }

                withContext(Dispatchers.Main) { onSuccess(file.absolutePath) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError("Gagal mengekspor: ${e.message}") }
            }
        }
    }
}
