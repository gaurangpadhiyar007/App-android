
package com.example.paytrack.pdf

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.paytrack.data.PaymentRecord
import com.example.paytrack.util.formatDate
import java.io.File

object PdfExporter {
    fun export(context: Context, records: List<PaymentRecord>): Uri? {
        if (records.isEmpty()) return null
        val pdf = PdfDocument()
        val pageWidth = 612
        val pageHeight = 792
        val margin = 24
        val paint = Paint().apply { isAntiAlias = true; textSize = 12f }

        var y = margin + 20
        var pageNumber = 1
        var page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas

        fun newPage() {
            pdf.finishPage(page)
            pageNumber++
            page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            canvas = page.canvas
            y = margin + 20
        }

        // Header
        paint.textSize = 18f
        canvas.drawText("PayTrack Report", margin.toFloat(), y.toFloat(), paint)
        paint.textSize = 12f
        y += 20
        canvas.drawText("Total records: ${records.size}", margin.toFloat(), y.toFloat(), paint)
        y += 16

        // Table header
        val headers = listOf("Date", "Vendor", "Type", "Amount", "Txn ID")
        val widths = listOf(100, 180, 60, 80, 140)
        fun drawRow(values: List<String>) {
            var x = margin
            values.forEachIndexed { i, s ->
                val clipped = if (s.length > 22) s.take(22) + "…" else s
                canvas.drawText(clipped, x.toFloat(), y.toFloat(), paint)
                x += widths[i]
            }
            y += 16
        }
        drawRow(headers)
        y += 6

        // Rows
        var debitTotal = 0.0
        var creditTotal = 0.0
        records.forEach { r ->
            if (y > pageHeight - margin) newPage()
            drawRow(
                listOf(
                    formatDate(r.dateEpoch),
                    r.vendorName,
                    r.kind.capitalize(),
                    String.format("%.2f", r.amount),
                    r.transactionId
                )
            )
            if (r.kind.uppercase() == "DEBIT") debitTotal += r.amount else creditTotal += r.amount
        }

        // Totals
        if (y > pageHeight - margin - 40) newPage()
        y += 8
        canvas.drawText("Debit total: ${String.format("%.2f", debitTotal)}", margin.toFloat(), y.toFloat(), paint)
        y += 16
        canvas.drawText("Credit total: ${String.format("%.2f", creditTotal)}", margin.toFloat(), y.toFloat(), paint)

        pdf.finishPage(page)

        val outDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val outFile = File(outDir, "PayTrack_${System.currentTimeMillis()}.pdf")
        outFile.outputStream().use { pdf.writeTo(it) }
        pdf.close()
        return FileProvider.getUriForFile(context, context.packageName + ".provider", outFile)
    }
}
