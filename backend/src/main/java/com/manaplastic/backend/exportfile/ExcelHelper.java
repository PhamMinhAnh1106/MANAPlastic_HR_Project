package com.manaplastic.backend.exportfile;

import com.manaplastic.backend.DTO.attendance.AttendanceDTO;
import com.manaplastic.backend.DTO.payroll.ContractDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    //Ateendance =========================================
    static String[] ATTENDANCE_HEADERS = {
            "ID", "USER NAME", "HỌ VÀ TÊN", "PHÒNG BAN", "NGÀY",
            "CHECK IN", "CHECK OUT", "CA LÀM VIỆC", "TRẠNG THÁI"
    };
    static String ATTENDANCE_SHEET = "Báo cáo danh sách chấm công";
    //===================================================

    //Contract ====================================================
    static String[] CONTRACT_HEADERS = {
            "ID", "NHÂN VIÊN", "LOẠI HĐ", "TRẠNG THÁI",
            "NGÀY BẮT ĐẦU", "NGÀY KẾT THÚC", "LƯƠNG CƠ BẢN", "PHỤ CẤP ĐỘC HẠI"
    };
    static String CONTRACT_SHEET = "Báo cáo danh sách hợp đồng";
    //=============================================================

    // Style cho header dùng chung
    public static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);

        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }


    // Xuất file cho các dữ liệu chấm công (DTO là 1 class)
    public static ByteArrayInputStream attendanceToExcel(List<AttendanceDTO> attendances) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(ATTENDANCE_SHEET);
            CellStyle headerStyle = createHeaderStyle(workbook);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < ATTENDANCE_HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(ATTENDANCE_HEADERS[col]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (AttendanceDTO att : attendances) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(att.getAttendanceId());
                row.createCell(1).setCellValue(att.getUserName());
                row.createCell(2).setCellValue(att.getFullNameUser());
                row.createCell(3).setCellValue(att.getDepartmentName());
                row.createCell(4).setCellValue(att.getAttendanceDate());

                if (att.getCheckIn() != null) {
                    row.createCell(5).setCellValue(att.getCheckIn().format(dateTimeFormatter));
                } else {
                    row.createCell(5).setCellValue("Trống");
                }

                if (att.getCheckOut() != null) {
                    row.createCell(6).setCellValue(att.getCheckOut().format(dateTimeFormatter));
                } else {
                    row.createCell(6).setCellValue("Trống");
                }

                row.createCell(7).setCellValue(att.getShiftName());
                row.createCell(8).setCellValue(att.getStatus());
            }

            for (int col = 0; col < ATTENDANCE_HEADERS.length; col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file Excel: " + e.getMessage());
        }
    }

    //Xuất file hdld (DTO là 1 record)
    public static ByteArrayInputStream contractsToExcel(List<ContractDTO> contracts) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(CONTRACT_SHEET);
            CellStyle headerStyle = createHeaderStyle(workbook);

            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("#,##0"));

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < CONTRACT_HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(CONTRACT_HEADERS[col]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (ContractDTO contract : contracts) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(contract.id());
                row.createCell(1).setCellValue(contract.username());
                row.createCell(2).setCellValue(contract.type());
                row.createCell(3).setCellValue(contract.status());

                if (contract.startdate() != null) {
                    row.createCell(4).setCellValue(contract.startdate().toString());
                } else {
                    row.createCell(4).setCellValue("Trống");
                }

                if (contract.enddate() != null) {
                    row.createCell(5).setCellValue(contract.enddate().toString());
                } else {
                    row.createCell(5).setCellValue("Vô thời hạn");
                }

                Cell salaryCell = row.createCell(6);
                if (contract.basesalary() != null) {
                    salaryCell.setCellValue(contract.basesalary().doubleValue());
                    salaryCell.setCellStyle(currencyStyle);
                } else {
                    salaryCell.setCellValue(0);
                }

                row.createCell(7).setCellValue(contract.allowanceToxicType() != null ? contract.allowanceToxicType() : "Trống");
            }

            for (int col = 0; col < CONTRACT_HEADERS.length; col++) {
                sheet.autoSizeColumn(col);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi export Contracts: " + e.getMessage());
        }
    }
}