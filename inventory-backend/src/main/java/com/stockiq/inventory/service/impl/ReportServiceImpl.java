package com.stockiq.inventory.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.common.util.SecurityUtils;
import com.stockiq.inventory.dto.ReportDto;
import com.stockiq.inventory.repository.ReportRepository;
import com.stockiq.inventory.service.ReportService;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

  private static final String SERVICE_IMPL = "report_service_impl";
  private static final String INIT_LOG = "init_log";
  private static final String EXCEPTION_LOG = "exception_log";

  private final ReportRepository reportRepository;

  public ReportServiceImpl(ReportRepository reportRepository) {
    this.reportRepository = reportRepository;
  }

  /**
   * Get Inventory Report
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ReportDto.InventoryReportItem>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public List<ReportDto.InventoryReportItem> getInventoryReport(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException {

    try {
      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);
      Long userId = SecurityUtils.getCurrentUserIdAsLong();

      return reportRepository.getInventoryReport(correlationId, logKey, userId);

    } catch (Exception e) {
      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to get inventory report",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Get Low Stock Report
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ReportDto.LowStockReportItem>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public List<ReportDto.LowStockReportItem> getLowStockReport(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException {

    try {
      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);
      Long userId = SecurityUtils.getCurrentUserIdAsLong();

      return reportRepository.getLowStockReport(correlationId, logKey, userId);

    } catch (Exception e) {
      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to get low stock report",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Get Supplier Report
   *
   * @param correlationId String
   * @param logKey String
   * @return List<ReportDto.SupplierPerformanceReportItem>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public List<ReportDto.SupplierPerformanceReportItem> getSupplierReport(
      String correlationId, String logKey) throws GeneralStatusCodeErrorException {

    try {
      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);
      Long userId = SecurityUtils.getCurrentUserIdAsLong();

      return reportRepository.getSupplierReport(correlationId, logKey, userId);

    } catch (Exception e) {
      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to get supplier report",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Export Inventory Report
   *
   * @param correlationId String
   * @param logKey String
   * @param format String
   * @return byte[]
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public byte[] exportInventoryReport(String correlationId, String logKey, String format)
      throws GeneralStatusCodeErrorException {

    try {
      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, "exportInventoryReport - " + format);
      List<ReportDto.InventoryReportItem> items = getInventoryReport(correlationId, logKey);

      if ("pdf".equalsIgnoreCase(format)) {
        return generateInventoryPdf(items);
      } else if ("excel".equalsIgnoreCase(format)) {
        return generateInventoryExcel(items);
      } else {
        throw new IllegalArgumentException("Unsupported report format: " + format);
      }

    } catch (GeneralStatusCodeErrorException e) {
      throw e;
    } catch (Exception e) {
      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to export inventory report",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Export Low Stock Report
   *
   * @param correlationId String
   * @param logKey String
   * @param format String
   * @return byte[]
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public byte[] exportLowStockReport(String correlationId, String logKey, String format)
      throws GeneralStatusCodeErrorException {

    try {
      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, "exportLowStockReport - " + format);
      List<ReportDto.LowStockReportItem> items = getLowStockReport(correlationId, logKey);

      if ("pdf".equalsIgnoreCase(format)) {
        return generateLowStockPdf(items);
      } else if ("excel".equalsIgnoreCase(format)) {
        return generateLowStockExcel(items);
      } else {
        throw new IllegalArgumentException("Unsupported report format: " + format);
      }

    } catch (GeneralStatusCodeErrorException e) {
      throw e;
    } catch (Exception e) {
      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to export low stock report",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Export Supplier Report
   *
   * @param correlationId String
   * @param logKey String
   * @param format String
   * @return byte[]
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public byte[] exportSupplierReport(String correlationId, String logKey, String format)
      throws GeneralStatusCodeErrorException {

    try {
      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, "exportSupplierReport - " + format);
      List<ReportDto.SupplierPerformanceReportItem> items =
          getSupplierReport(correlationId, logKey);

      if ("pdf".equalsIgnoreCase(format)) {
        return generateSupplierPdf(items);
      } else if ("excel".equalsIgnoreCase(format)) {
        return generateSupplierExcel(items);
      } else {
        throw new IllegalArgumentException("Unsupported report format: " + format);
      }

    } catch (GeneralStatusCodeErrorException e) {
      throw e;
    } catch (Exception e) {
      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());
      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to export supplier report",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  // --- PDF GENERATORS ---

  private byte[] generateInventoryPdf(List<ReportDto.InventoryReportItem> items) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4, 36, 36, 54, 36);
    PdfWriter.getInstance(document, baos);
    document.open();

    Paragraph title =
        new Paragraph(
            "StockIQ Smart Inventory Report",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(20, 35, 60)));
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);

    Paragraph meta =
        new Paragraph(
            "Generated on: "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY));
    meta.setAlignment(Element.ALIGN_CENTER);
    meta.setSpacingAfter(20);
    document.add(meta);

    PdfPTable table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setWidths(new float[] {3.5f, 2f, 1.5f, 3f});

    Color headerBg = new Color(20, 35, 60);
    String[] headers = {"Product Name", "Category", "Current Qty", "Suppliers"};
    for (String h : headers) {
      PdfPCell cell =
          new PdfPCell(
              new Paragraph(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
      cell.setBackgroundColor(headerBg);
      cell.setPadding(8);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(cell);
    }

    boolean isAlt = false;
    for (ReportDto.InventoryReportItem item : items) {
      PdfPCell c1 =
          new PdfPCell(
              new Paragraph(item.getProductName(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
      PdfPCell c2 =
          new PdfPCell(
              new Paragraph(
                  item.getCategoryName() != null ? item.getCategoryName() : "Uncategorized",
                  FontFactory.getFont(FontFactory.HELVETICA, 9)));
      PdfPCell c3 =
          new PdfPCell(
              new Paragraph(
                  String.valueOf(item.getCurrentQty()),
                  FontFactory.getFont(FontFactory.HELVETICA, 9)));
      PdfPCell c4 =
          new PdfPCell(
              new Paragraph(
                  item.getSupplierNames() != null ? item.getSupplierNames() : "N/A",
                  FontFactory.getFont(FontFactory.HELVETICA, 9)));

      c3.setHorizontalAlignment(Element.ALIGN_CENTER);

      PdfPCell[] cells = {c1, c2, c3, c4};
      for (PdfPCell c : cells) {
        c.setPadding(6);
        if (isAlt) {
          c.setBackgroundColor(new Color(245, 247, 250));
        }
        table.addCell(c);
      }
      isAlt = !isAlt;
    }

    document.add(table);
    document.close();
    return baos.toByteArray();
  }

  private byte[] generateLowStockPdf(List<ReportDto.LowStockReportItem> items) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4, 36, 36, 54, 36);
    PdfWriter.getInstance(document, baos);
    document.open();

    Paragraph title =
        new Paragraph(
            "StockIQ Low Stock Report",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(153, 27, 27)));
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);

    Paragraph meta =
        new Paragraph(
            "Generated on: "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY));
    meta.setAlignment(Element.ALIGN_CENTER);
    meta.setSpacingAfter(20);
    document.add(meta);

    PdfPTable table = new PdfPTable(5);
    table.setWidthPercentage(100);
    table.setWidths(new float[] {3f, 2f, 1.5f, 1.5f, 2f});

    Color headerBg = new Color(153, 27, 27);
    String[] headers = {"Product Name", "Category", "Current Qty", "Threshold", "Suppliers"};
    for (String h : headers) {
      PdfPCell cell =
          new PdfPCell(
              new Paragraph(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
      cell.setBackgroundColor(headerBg);
      cell.setPadding(8);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(cell);
    }

    boolean isAlt = false;
    for (ReportDto.LowStockReportItem item : items) {
      PdfPCell c1 =
          new PdfPCell(
              new Paragraph(item.getProductName(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
      PdfPCell c2 =
          new PdfPCell(
              new Paragraph(
                  item.getCategoryName() != null ? item.getCategoryName() : "Uncategorized",
                  FontFactory.getFont(FontFactory.HELVETICA, 9)));
      PdfPCell c3 =
          new PdfPCell(
              new Paragraph(
                  String.valueOf(item.getCurrentQty()),
                  FontFactory.getFont(FontFactory.HELVETICA, 9)));
      PdfPCell c4 =
          new PdfPCell(
              new Paragraph(
                  String.valueOf(item.getReorderThreshold()),
                  FontFactory.getFont(FontFactory.HELVETICA, 9)));
      PdfPCell c5 =
          new PdfPCell(
              new Paragraph(
                  item.getSupplierNames() != null ? item.getSupplierNames() : "N/A",
                  FontFactory.getFont(FontFactory.HELVETICA, 9)));

      c3.setHorizontalAlignment(Element.ALIGN_CENTER);
      c4.setHorizontalAlignment(Element.ALIGN_CENTER);

      PdfPCell[] cells = {c1, c2, c3, c4, c5};
      for (PdfPCell c : cells) {
        c.setPadding(6);
        if (isAlt) {
          c.setBackgroundColor(new Color(254, 242, 242));
        }
        table.addCell(c);
      }
      isAlt = !isAlt;
    }

    document.add(table);
    document.close();
    return baos.toByteArray();
  }

  private byte[] generateSupplierPdf(List<ReportDto.SupplierPerformanceReportItem> items)
      throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4, 36, 36, 54, 36);
    PdfWriter.getInstance(document, baos);
    document.open();

    Paragraph title =
        new Paragraph(
            "StockIQ Supplier Performance Report",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(6, 78, 59)));
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);

    Paragraph meta =
        new Paragraph(
            "Generated on: "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY));
    meta.setAlignment(Element.ALIGN_CENTER);
    meta.setSpacingAfter(20);
    document.add(meta);

    PdfPTable table = new PdfPTable(5);
    table.setWidthPercentage(100);
    table.setWidths(new float[] {2.5f, 2.5f, 1.5f, 1.5f, 2f});

    Color headerBg = new Color(6, 78, 59);
    String[] headers = {"Supplier Name", "Category Supplied", "Status", "Products", "Total Stock"};
    for (String h : headers) {
      PdfPCell cell =
          new PdfPCell(
              new Paragraph(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
      cell.setBackgroundColor(headerBg);
      cell.setPadding(8);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(cell);
    }

    boolean isAlt = false;
    for (ReportDto.SupplierPerformanceReportItem item : items) {
      PdfPCell c1 =
          new PdfPCell(
              new Paragraph(item.getSupplierName(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
      PdfPCell c2 =
          new PdfPCell(
              new Paragraph(
                  item.getCategorySupplied(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
      PdfPCell c3 =
          new PdfPCell(
              new Paragraph(item.getStatus(), FontFactory.getFont(FontFactory.HELVETICA, 9)));
      PdfPCell c4 =
          new PdfPCell(
              new Paragraph(
                  String.valueOf(item.getTotalProducts()),
                  FontFactory.getFont(FontFactory.HELVETICA, 9)));
      PdfPCell c5 =
          new PdfPCell(
              new Paragraph(
                  String.valueOf(item.getTotalStockQuantity()),
                  FontFactory.getFont(FontFactory.HELVETICA, 9)));

      c3.setHorizontalAlignment(Element.ALIGN_CENTER);
      c4.setHorizontalAlignment(Element.ALIGN_CENTER);
      c5.setHorizontalAlignment(Element.ALIGN_CENTER);

      PdfPCell[] cells = {c1, c2, c3, c4, c5};
      for (PdfPCell c : cells) {
        c.setPadding(6);
        if (isAlt) {
          c.setBackgroundColor(new Color(240, 253, 250));
        }
        table.addCell(c);
      }
      isAlt = !isAlt;
    }

    document.add(table);
    document.close();
    return baos.toByteArray();
  }

  // --- EXCEL GENERATORS ---

  private byte[] generateInventoryExcel(List<ReportDto.InventoryReportItem> items)
      throws Exception {
    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      XSSFSheet sheet = workbook.createSheet("Inventory Report");
      sheet.setDisplayGridlines(true);

      // Title Row
      XSSFRow titleRow = sheet.createRow(0);
      XSSFCell titleCell = titleRow.createCell(0);
      titleCell.setCellValue("StockIQ Inventory Report");
      XSSFCellStyle titleStyle = workbook.createCellStyle();
      XSSFFont titleFont = workbook.createFont();
      titleFont.setBold(true);
      titleFont.setFontHeightInPoints((short) 16);
      titleStyle.setFont(titleFont);
      titleCell.setCellStyle(titleStyle);

      // Metadata Row
      XSSFRow metaRow = sheet.createRow(1);
      metaRow
          .createCell(0)
          .setCellValue(
              "Generated on: "
                  + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

      // Headers
      XSSFRow headerRow = sheet.createRow(3);
      String[] headers = {"Product Name", "Category", "Current Qty", "Suppliers"};
      XSSFCellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFillForegroundColor(
          new org.apache.poi.xssf.usermodel.XSSFColor(new Color(20, 35, 60), null));
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      headerStyle.setAlignment(HorizontalAlignment.CENTER);

      XSSFFont headerFont = workbook.createFont();
      headerFont.setBold(true);
      headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
      headerStyle.setFont(headerFont);

      for (int i = 0; i < headers.length; i++) {
        XSSFCell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      // Rows
      int rowNum = 4;
      for (ReportDto.InventoryReportItem item : items) {
        XSSFRow row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(item.getProductName());
        row.createCell(1)
            .setCellValue(
                item.getCategoryName() != null ? item.getCategoryName() : "Uncategorized");

        XSSFCell qtyCell = row.createCell(2);
        qtyCell.setCellValue(item.getCurrentQty());

        row.createCell(3)
            .setCellValue(item.getSupplierNames() != null ? item.getSupplierNames() : "N/A");
      }

      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(baos);
      return baos.toByteArray();
    }
  }

  private byte[] generateLowStockExcel(List<ReportDto.LowStockReportItem> items) throws Exception {
    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      XSSFSheet sheet = workbook.createSheet("Low Stock Report");
      sheet.setDisplayGridlines(true);

      XSSFRow titleRow = sheet.createRow(0);
      XSSFCell titleCell = titleRow.createCell(0);
      titleCell.setCellValue("StockIQ Low Stock Report");
      XSSFCellStyle titleStyle = workbook.createCellStyle();
      XSSFFont titleFont = workbook.createFont();
      titleFont.setBold(true);
      titleFont.setFontHeightInPoints((short) 16);
      titleStyle.setFont(titleFont);
      titleCell.setCellStyle(titleStyle);

      XSSFRow metaRow = sheet.createRow(1);
      metaRow
          .createCell(0)
          .setCellValue(
              "Generated on: "
                  + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

      XSSFRow headerRow = sheet.createRow(3);
      String[] headers = {"Product Name", "Category", "Current Qty", "Threshold", "Suppliers"};
      XSSFCellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFillForegroundColor(
          new org.apache.poi.xssf.usermodel.XSSFColor(new Color(153, 27, 27), null));
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      headerStyle.setAlignment(HorizontalAlignment.CENTER);

      XSSFFont headerFont = workbook.createFont();
      headerFont.setBold(true);
      headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
      headerStyle.setFont(headerFont);

      for (int i = 0; i < headers.length; i++) {
        XSSFCell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      int rowNum = 4;
      for (ReportDto.LowStockReportItem item : items) {
        XSSFRow row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(item.getProductName());
        row.createCell(1)
            .setCellValue(
                item.getCategoryName() != null ? item.getCategoryName() : "Uncategorized");
        row.createCell(2).setCellValue(item.getCurrentQty());
        row.createCell(3).setCellValue(item.getReorderThreshold());
        row.createCell(4)
            .setCellValue(item.getSupplierNames() != null ? item.getSupplierNames() : "N/A");
      }

      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(baos);
      return baos.toByteArray();
    }
  }

  private byte[] generateSupplierExcel(List<ReportDto.SupplierPerformanceReportItem> items)
      throws Exception {
    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      XSSFSheet sheet = workbook.createSheet("Supplier Report");
      sheet.setDisplayGridlines(true);

      XSSFRow titleRow = sheet.createRow(0);
      XSSFCell titleCell = titleRow.createCell(0);
      titleCell.setCellValue("StockIQ Supplier Performance Report");
      XSSFCellStyle titleStyle = workbook.createCellStyle();
      XSSFFont titleFont = workbook.createFont();
      titleFont.setBold(true);
      titleFont.setFontHeightInPoints((short) 16);
      titleStyle.setFont(titleFont);
      titleCell.setCellStyle(titleStyle);

      XSSFRow metaRow = sheet.createRow(1);
      metaRow
          .createCell(0)
          .setCellValue(
              "Generated on: "
                  + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

      XSSFRow headerRow = sheet.createRow(3);
      String[] headers = {
        "Supplier Name", "Category Supplied", "Status", "Products", "Total Stock"
      };
      XSSFCellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFillForegroundColor(
          new org.apache.poi.xssf.usermodel.XSSFColor(new Color(6, 78, 59), null));
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      headerStyle.setAlignment(HorizontalAlignment.CENTER);

      XSSFFont headerFont = workbook.createFont();
      headerFont.setBold(true);
      headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
      headerStyle.setFont(headerFont);

      for (int i = 0; i < headers.length; i++) {
        XSSFCell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      int rowNum = 4;
      for (ReportDto.SupplierPerformanceReportItem item : items) {
        XSSFRow row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(item.getSupplierName());
        row.createCell(1).setCellValue(item.getCategorySupplied());
        row.createCell(2).setCellValue(item.getStatus());
        row.createCell(3).setCellValue(item.getTotalProducts());
        row.createCell(4).setCellValue(item.getTotalStockQuantity());
      }

      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(baos);
      return baos.toByteArray();
    }
  }
}
