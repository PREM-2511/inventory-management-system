package com.stockiq.inventory.service.impl;

import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.ApiError;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.dto.DashboardCategoryDto;
import com.stockiq.inventory.dto.DashboardSummaryDto;
import com.stockiq.inventory.dto.ProductDto;
import com.stockiq.inventory.repository.CategoryRepository;
import com.stockiq.inventory.repository.ProductRepository;
import com.stockiq.inventory.repository.StockTransactionRepository;
import com.stockiq.inventory.repository.SupplierRepository;
import com.stockiq.inventory.service.DashboardService;
import com.stockiq.inventory.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

  private static final String SERVICE_IMPL = "dashboard_service_impl";
  private static final String INIT_LOG = "init_log";
  private static final String EXCEPTION_LOG = "exception_log";

  private final ProductService productService;
  private final SupplierRepository supplierRepository;
  private final StockTransactionRepository stockTransactionRepository;

  public DashboardServiceImpl(
      SupplierRepository supplierRepository,
      StockTransactionRepository stockTransactionRepository,
      ProductService productService) {

    this.supplierRepository = supplierRepository;
    this.stockTransactionRepository = stockTransactionRepository;
    this.productService = productService;
  }

  /**
   * Get Dashboard Summary
   *
   * @param correlationId String
   * @param logKey String
   * @return DashboardSummaryDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public DashboardSummaryDto getSummary(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      // Get total number of products
      List<ProductDto> allProducts = productService.getAllProducts(correlationId, logKey);

      long totalProducts = allProducts.size();

      // Get total number of suppliers
      long totalSuppliers = supplierRepository.countAll(correlationId, logKey, "");

      // Get total number of transactions
      long totalTransactions = stockTransactionRepository.countAll(correlationId, logKey);

      // Get low-stock products
      List<ProductDto> lowStockList = productService.getLowStockAlerts(correlationId, logKey);

      long lowStock = lowStockList.size();

      // Build dashboard summary response
      return DashboardSummaryDto.builder()
          .totalProducts(totalProducts)
          .totalSuppliers(totalSuppliers)
          .lowStock(lowStock)
          .totalTransactions(totalTransactions)
          .build();

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(new ErrorMessage(ApiError.INTERNAL_SERVER_ERROR));
    }
  }

  /**
   * Get Category Distribution
   *
   * @param correlationId String
   * @param logKey String
   * @return List<DashboardCategoryDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public List<DashboardCategoryDto> getCategoryDistribution(String correlationId, String logKey)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      // Retrieve all products
      List<ProductDto> allProducts = productService.getAllProducts(correlationId, logKey);

      long total = allProducts.size();

      // Group products by category and count occurrences
      Map<String, Long> grouped =
          allProducts.stream()
              .collect(
                  Collectors.groupingBy(
                      p -> p.getCategory() != null ? p.getCategory() : "Unknown",
                      Collectors.counting()));

      List<DashboardCategoryDto> result = new ArrayList<>();

      // Convert grouped data into response DTO
      for (Map.Entry<String, Long> entry : grouped.entrySet()) {

        int percentage = total > 0 ? (int) Math.round((entry.getValue() * 100.0) / total) : 0;

        result.add(
            DashboardCategoryDto.builder()
                .name(entry.getKey())
                .count(entry.getValue())
                .percentage(percentage)
                .build());
      }

      return result;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(new ErrorMessage(ApiError.INTERNAL_SERVER_ERROR));
    }
  }
}
