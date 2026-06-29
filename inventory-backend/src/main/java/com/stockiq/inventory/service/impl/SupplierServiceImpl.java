package com.stockiq.inventory.service.impl;

import com.stockiq.inventory.common.enums.ErrorMessageCodeEnum;
import com.stockiq.inventory.common.enums.HttpStatusCodeEnum;
import com.stockiq.inventory.common.enums.supplier.SupplierStatus;
import com.stockiq.inventory.common.exception.GeneralStatusCodeErrorException;
import com.stockiq.inventory.common.response.supplier.SupplierDetails;
import com.stockiq.inventory.common.util.ActivityLog;
import com.stockiq.inventory.common.util.ErrorMessage;
import com.stockiq.inventory.common.util.PageResponse;
import com.stockiq.inventory.dto.SupplierDto;
import com.stockiq.inventory.entity.Supplier;
import com.stockiq.inventory.repository.SupplierRepository;
import com.stockiq.inventory.service.SupplierService;
import com.stockiq.inventory.service.AuditTrailService;
import com.stockiq.inventory.common.enums.audit.AuditAction;
import com.stockiq.inventory.common.enums.audit.AuditEntityType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

  private static final String SERVICE_IMPL = "supplier_service_impl";
  private static final String INIT_LOG = "init_log";
  private static final String EXCEPTION_LOG = "exception_log";
  private static final String DUPLICATE_EXCEPTION_LOG = "duplicate_exception_log";

  private final SupplierRepository supplierRepository;
  private final AuditTrailService auditTrailService;

  public SupplierServiceImpl(
      SupplierRepository supplierRepository, AuditTrailService auditTrailService) {
    this.supplierRepository = supplierRepository;
    this.auditTrailService = auditTrailService;
  }

  /**
   * Save Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param supplierDto SupplierDto
   * @return SupplierDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public SupplierDto save(String correlationId, String logKey, SupplierDto supplierDto)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      Supplier supplier = toEntity(supplierDto);
      supplier.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

      long id = supplierRepository.save(correlationId, logKey, supplier);

      supplierDto.setId(id);
      supplierDto.setStatus(SupplierStatus.fromString(supplier.getStatus()));
      supplierDto.setCreatedAt(supplier.getCreatedAt().toString());

      auditTrailService.record(
          correlationId,
          logKey,
          AuditAction.CREATE,
          AuditEntityType.SUPPLIER,
          id,
          supplierDto.getName(),
          null,
          "Category="
              + supplierDto.getCategory()
              + ", Status="
              + supplierDto.getStatus().name()
              + ", Email="
              + supplierDto.getEmail()
              + ", Phone="
              + supplierDto.getPhone());

      return supplierDto;

    } catch (DuplicateKeyException e) {

      ActivityLog.logError(
          correlationId, logKey, SERVICE_IMPL, DUPLICATE_EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Supplier name already exists",
              "Supplier Create Failed",
              HttpStatus.BAD_REQUEST,
              HttpStatusCodeEnum.BAD_REQUEST.getValue(),
              ErrorMessageCodeEnum.SUPPLIER_CREATE_FAILED.getCode(),
              new Date().toString(),
              null));

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to create supplier",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Update Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param supplierDto SupplierDto
   * @param id long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public void update(String correlationId, String logKey, SupplierDto supplierDto, long id)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      SupplierDetails existingSupplier = findById(correlationId, logKey, id);

      Supplier supplier = toEntity(supplierDto);
      supplier.setId(id);
      supplier.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

      int updated = supplierRepository.update(correlationId, logKey, supplier);

      if (updated == 0) {

        throw new GeneralStatusCodeErrorException(
            new ErrorMessage(
                "Supplier not found",
                "Supplier Update Failed",
                HttpStatus.NOT_FOUND,
                HttpStatusCodeEnum.NOT_FOUND.getValue(),
                ErrorMessageCodeEnum.SUPPLIER_NOT_FOUND.getCode(),
                new Date().toString(),
                null));
      }

      StringBuilder oldVal = new StringBuilder();
      StringBuilder newVal = new StringBuilder();

      if (existingSupplier.getName() != null
          && !existingSupplier.getName().equals(supplierDto.getName())) {
        oldVal.append("Name=").append(existingSupplier.getName()).append("; ");
        newVal.append("Name=").append(supplierDto.getName()).append("; ");
      }
      if (existingSupplier.getCategory() != null
          && !existingSupplier.getCategory().equals(supplierDto.getCategory())) {
        oldVal.append("Category=").append(existingSupplier.getCategory()).append("; ");
        newVal.append("Category=").append(supplierDto.getCategory()).append("; ");
      }
      SupplierStatus newStatus =
          supplierDto.getStatus() != null ? supplierDto.getStatus() : existingSupplier.getStatus();
      if (existingSupplier.getStatus() != newStatus) {
        oldVal.append("Status=").append(existingSupplier.getStatus().name()).append("; ");
        newVal.append("Status=").append(newStatus.name()).append("; ");
      }
      if (existingSupplier.getEmail() != null
          && !existingSupplier.getEmail().equals(supplierDto.getEmail())) {
        oldVal.append("Email=").append(existingSupplier.getEmail()).append("; ");
        newVal.append("Email=").append(supplierDto.getEmail()).append("; ");
      }
      if (existingSupplier.getPhone() != null
          && !existingSupplier.getPhone().equals(supplierDto.getPhone())) {
        oldVal.append("Phone=").append(existingSupplier.getPhone()).append("; ");
        newVal.append("Phone=").append(supplierDto.getPhone()).append("; ");
      }

      String oldString = oldVal.toString().trim();
      String newString = newVal.toString().trim();
      if (oldString.endsWith(";")) {
        oldString = oldString.substring(0, oldString.length() - 1).trim();
      }
      if (newString.endsWith(";")) {
        newString = newString.substring(0, newString.length() - 1).trim();
      }

      if (!oldString.isEmpty() || !newString.isEmpty()) {
        auditTrailService.record(
            correlationId,
            logKey,
            AuditAction.UPDATE,
            AuditEntityType.SUPPLIER,
            id,
            supplierDto.getName(),
            oldString,
            newString);
      }

    } catch (DuplicateKeyException e) {

      ActivityLog.logError(
          correlationId, logKey, SERVICE_IMPL, DUPLICATE_EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Supplier name already exists",
              "Supplier Update Failed",
              HttpStatus.BAD_REQUEST,
              HttpStatusCodeEnum.BAD_REQUEST.getValue(),
              ErrorMessageCodeEnum.SUPPLIER_UPDATE_FAILED.getCode(),
              new Date().toString(),
              null));

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to update supplier",
              "Supplier Update Failed",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.SUPPLIER_UPDATE_FAILED.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Delete Supplier.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public void delete(String correlationId, String logKey, long id)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      SupplierDetails existingSupplier = findById(correlationId, logKey, id);

      int deleted = supplierRepository.delete(correlationId, logKey, id);

      if (deleted == 0) {

        throw new GeneralStatusCodeErrorException(
            new ErrorMessage(
                "Supplier not found",
                "Supplier Delete Failed",
                HttpStatus.NOT_FOUND,
                HttpStatusCodeEnum.NOT_FOUND.getValue(),
                ErrorMessageCodeEnum.SUPPLIER_NOT_FOUND.getCode(),
                new Date().toString(),
                null));
      }

      auditTrailService.record(
          correlationId,
          logKey,
          AuditAction.DELETE,
          AuditEntityType.SUPPLIER,
          id,
          existingSupplier.getName(),
          "Category="
              + existingSupplier.getCategory()
              + ", Status="
              + existingSupplier.getStatus().name()
              + ", Email="
              + existingSupplier.getEmail()
              + ", Phone="
              + existingSupplier.getPhone(),
          null);

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to delete supplier",
              "Supplier Delete Failed",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.SUPPLIER_DELETE_FAILED.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Find Supplier By Id.
   *
   * @param correlationId String
   * @param logKey String
   * @param id long
   * @return SupplierDto
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public SupplierDetails findById(String correlationId, String logKey, long id)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      Supplier supplier =
          supplierRepository
              .findById(correlationId, logKey, id)
              .orElseThrow(
                  () ->
                      new GeneralStatusCodeErrorException(
                          new ErrorMessage(
                              "Supplier not found",
                              "Supplier Not Found",
                              HttpStatus.NOT_FOUND,
                              HttpStatusCodeEnum.NOT_FOUND.getValue(),
                              ErrorMessageCodeEnum.SUPPLIER_NOT_FOUND.getCode(),
                              new Date().toString(),
                              null)));

      return toDto(supplier);

    } catch (GeneralStatusCodeErrorException e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw e;

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to find supplier",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  /**
   * Find All Suppliers With Pagination.
   *
   * @param correlationId String
   * @param logKey String
   * @param page int
   * @param size int
   * @param search String
   * @return PageResponse<SupplierDto>
   * @throws GeneralStatusCodeErrorException if an error occurs during the operation
   */
  @Override
  public PageResponse<SupplierDetails> findAll(
      String correlationId, String logKey, int page, int size, String search)
      throws GeneralStatusCodeErrorException {

    try {

      ActivityLog.logInfo(correlationId, logKey, SERVICE_IMPL, INIT_LOG);

      List<SupplierDetails> suppliers =
          supplierRepository.findAll(correlationId, logKey, page, size, search).stream()
              .map(this::toDto)
              .toList();

      long total = supplierRepository.countAll(correlationId, logKey, search);

      return new PageResponse<>(suppliers, page, size, total);

    } catch (Exception e) {

      ActivityLog.logError(correlationId, logKey, SERVICE_IMPL, EXCEPTION_LOG, e.getMessage());

      throw new GeneralStatusCodeErrorException(
          new ErrorMessage(
              "Failed to find suppliers",
              "Internal Server Error",
              HttpStatus.INTERNAL_SERVER_ERROR,
              HttpStatusCodeEnum.INTERNAL_SERVER_ERROR.getValue(),
              ErrorMessageCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
              new Date().toString(),
              null));
    }
  }

  private Supplier toEntity(SupplierDto dto) {
    return Supplier.builder()
        .id(dto.getId() != null ? dto.getId() : 0L)
        .name(dto.getName())
        .category(dto.getCategory())
        .status(
            dto.getStatus() != null ? dto.getStatus().toValue() : SupplierStatus.ACTIVE.toValue())
        .email(dto.getEmail())
        .phone(dto.getPhone())
        .createdBy(com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserId())
        .updatedBy(com.stockiq.inventory.common.util.SecurityUtils.getCurrentUserId())
        .build();
  }

  private SupplierDetails toDto(Supplier entity) {
    return SupplierDetails.builder()
        .id(entity.getId())
        .name(entity.getName())
        .category(entity.getCategory())
        .status(SupplierStatus.fromString(entity.getStatus()))
        .email(entity.getEmail())
        .phone(entity.getPhone())
        .build();
  }
}
