package com.manaplastic.backend.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PdfService {

    public byte[] generatePayslipPdf(Map<String, Object> payslipData) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 30, 30, 30, 30); // Căn lề
            PdfWriter.getInstance(document, out);
            document.open();

            //  CẤU HÌNH FONT TIẾNG VIỆT
            //  file arial.ttf trg src/main/resources/fonts/
            String fontPath = new ClassPathResource("fonts/arial.ttf").getPath();
            BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            Font titleFont = new Font(bf, 16, Font.BOLD);
            Font subTitleFont = new Font(bf, 11, Font.BOLD);
            Font normalFont = new Font(bf, 10, Font.NORMAL);
            Font boldFont = new Font(bf, 10, Font.BOLD);

            // 2. HEADER CÔNG TY & TIÊU ĐỀ
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);

            // Bên trái: Tên công ty
            PdfPCell companyCell = new PdfPCell(new Phrase("MANAPLASTIC COMPANY", subTitleFont));
            companyCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(companyCell);

            // Bên phải: Chữ CONFIDENTIAL
            PdfPCell confCell = new PdfPCell(new Phrase("CONFIDENTIAL / MẬT", new Font(bf, 9, Font.ITALIC)));
            confCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            confCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(confCell);

            document.add(headerTable);

            Paragraph title = new Paragraph("PHIẾU LƯƠNG / PAYSLIP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(10);
            title.setSpacingAfter(20);
            document.add(title);

            @SuppressWarnings("unchecked")
            Map<String, Object> header = (Map<String, Object>) payslipData.get("header");

            if (header != null) {
                PdfPTable infoTable = new PdfPTable(4);
                infoTable.setWidthPercentage(100);
                infoTable.setWidths(new float[]{1.5f, 3.5f, 1.5f, 3.5f});
                infoTable.setSpacingAfter(15);

                addInfoCell(infoTable, "Mã seri / ID:", boldFont);
                addInfoCell(infoTable, getSafeString(header.get("username")), normalFont);

                addInfoCell(infoTable, "Mã nhân viên / username:", boldFont);
                addInfoCell(infoTable, getSafeString(header.get("username")), normalFont);

                addInfoCell(infoTable, "Họ tên / Name:", boldFont);
                addInfoCell(infoTable, getSafeString(header.get("fullname")), normalFont);

                addInfoCell(infoTable, "Bộ phận / Dept:", boldFont);
                addInfoCell(infoTable, getSafeString(header.get("departmentname")), normalFont);

                addInfoCell(infoTable, "Kỳ lương / Period:", boldFont);
                addInfoCell(infoTable, getSafeString(header.get("payperiod")), normalFont);
                // Thêm viền dưới cho block thông tin này
                PdfPCell lineCell = new PdfPCell();
                lineCell.setColspan(4);
                lineCell.setBorder(Rectangle.BOTTOM);
                lineCell.setBorderWidth(1f);
                lineCell.setFixedHeight(5);
                infoTable.addCell(lineCell);

                document.add(infoTable);
            }

            // XỬ LÝ DỮ LIỆU: TÁCH THU NHẬP & KHẤU TRỪ
            List<Map<String, Object>> items = (List<Map<String, Object>>) payslipData.get("items");
            List<Map<String, Object>> incomes = new ArrayList<>();
            List<Map<String, Object>> deductions = new ArrayList<>();
            BigDecimal totalDeduction = BigDecimal.ZERO;

            if (items != null) {
                for (Map<String, Object> item : items) {
                    String name = (String) item.get("item_name");
                    // Logic lọc dữ liệu (Bạn có thể sửa logic này tùy theo tên biến trong DB)
                    // Ví dụ: Nếu tên chứa "Thuế", "Bảo hiểm", "Phạt" -> Khấu trừ
                    if (name != null && (name.contains("Thuế") || name.contains("Bảo hiểm") || name.contains("BH") || name.contains("Phạt") || name.contains("Khấu trừ"))) {
                        deductions.add(item);
                        totalDeduction = totalDeduction.add(getBigDecimal(item.get("item_value")));
                    } else {
                        // Mặc định còn lại là thu nhập
                        incomes.add(item);
                    }
                }
            }

            //  VẼ BẢNG CHI TIẾT (4 Cột: Tên TN | Tiền TN || Tên KT | Tiền KT)
            PdfPTable mainTable = new PdfPTable(4);
            mainTable.setWidthPercentage(100);
            mainTable.setWidths(new float[]{3.5f, 1.5f, 3.5f, 1.5f}); // Căn chỉnh độ rộng

            // Header bảng
            addTableHeader(mainTable, "KHOẢN THU NHẬP / EARNINGS", boldFont, 2);
            addTableHeader(mainTable, "KHOẢN KHẤU TRỪ / DEDUCTIONS", boldFont, 2);

            // Duyệt danh sách (Lấy size lớn nhất để chạy vòng lặp)
            int maxSize = Math.max(incomes.size(), deductions.size());
            NumberFormat vnFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            for (int i = 0; i < maxSize; i++) {
                // --- CỘT TRÁI (THU NHẬP) ---
                if (i < incomes.size()) {
                    addContentRow(mainTable, incomes.get(i), vnFormat, normalFont);
                } else {
                    addEmptyRow(mainTable); // Nếu hết thu nhập thì điền ô trống
                }

                // --- CỘT PHẢI (KHẤU TRỪ) ---
                if (i < deductions.size()) {
                    addContentRow(mainTable, deductions.get(i), vnFormat, normalFont);
                } else {
                    addEmptyRow(mainTable);
                }
            }

            // Row tổng cộng từng bên (Optional)
            addCellTotal(mainTable, "Tổng thu nhập", (BigDecimal) header.get("totalincome"), boldFont, vnFormat);
            addCellTotal(mainTable, "Tổng khấu trừ", totalDeduction, boldFont, vnFormat);

            document.add(mainTable);

            // TỔNG THỰC LĨNH (NET PAY) - To và Rõ ràng
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setSpacingBefore(10);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new float[]{7f, 3f});

            PdfPCell netLabel = new PdfPCell(new Phrase("THỰC LĨNH / NET PAY:", new Font(bf, 12, Font.BOLD)));
            netLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            netLabel.setBorder(Rectangle.NO_BORDER);
            netLabel.setPaddingRight(10);

            BigDecimal netSalary = getBigDecimal(header.get("netsalary"));
            PdfPCell netValue = new PdfPCell(new Phrase(vnFormat.format(netSalary) + " VND", new Font(bf, 13, Font.BOLD)));
            netValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            netValue.setBorder(Rectangle.BOX); // Đóng khung số tiền
            netValue.setBackgroundColor(java.awt.Color.LIGHT_GRAY);

            footerTable.addCell(netLabel);
            footerTable.addCell(netValue);
            document.add(footerTable);

            // Footer
            document.add(new Paragraph(" "));
            Paragraph sign = new Paragraph("Nếu có sai xót vui lòng liên hệ phoòng HR! Xin cảm ơn.", new Font(bf, 10, Font.ITALIC));
            sign.setAlignment(Element.ALIGN_RIGHT);
            sign.setIndentationRight(50);
            document.add(sign);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // hedpler
    private String getSafeString(Object obj) {
        if (obj == null) return "";
        return obj.toString();
    }
    private void addInfoCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(5);
        table.addCell(cell);
    }

    private void addTableHeader(PdfPTable table, String text, Font font, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addContentRow(PdfPTable table, Map<String, Object> item, NumberFormat format, Font font) {
        String name = (String) item.get("item_name");
        BigDecimal value = getBigDecimal(item.get("item_value"));

        // Ô tên khoản
        PdfPCell nameCell = new PdfPCell(new Phrase(name, font));
        nameCell.setBorderWidthLeft(0);
        nameCell.setBorderWidthRight(0);
        nameCell.setBorderWidthTop(0);
        nameCell.setPadding(4);
        table.addCell(nameCell);

        // Ô số tiền
        PdfPCell valueCell = new PdfPCell(new Phrase(format.format(value), font));
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setBorderWidthLeft(0);
        valueCell.setBorderWidthRight(1); // Chỉ kẻ dọc giữa 2 bảng lớn
        valueCell.setBorderWidthTop(0);
        valueCell.setPadding(4);
        table.addCell(valueCell);
    }

    private void addEmptyRow(PdfPTable table) {
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        table.addCell(cell); // Tên rỗng

        PdfPCell cell2 = new PdfPCell(new Phrase(" "));
        cell2.setBorderWidthLeft(0);
        cell2.setBorderWidthRight(1);
        cell2.setBorderWidthTop(0);
        table.addCell(cell2); // Tiền rỗng
    }

    private void addCellTotal(PdfPTable table, String text, BigDecimal value, Font font, NumberFormat format) {
        PdfPCell nameCell = new PdfPCell(new Phrase(text, font));
        nameCell.setPadding(5);
        nameCell.setBorder(Rectangle.TOP);
        table.addCell(nameCell);

        PdfPCell valCell = new PdfPCell(new Phrase(format.format(value), font));
        valCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valCell.setPadding(5);
        valCell.setBorder(Rectangle.TOP);
        table.addCell(valCell);
    }

    private BigDecimal getBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        return new BigDecimal(value.toString());
    }
}