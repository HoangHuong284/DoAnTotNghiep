package com.web.repository;

import com.web.entity.Invoice;
import com.web.entity.InvoiceStatus;
import com.web.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InvoiceStatusRepository extends JpaRepository<InvoiceStatus,Long> {

    @Query("select i from InvoiceStatus i where i.invoice.id = ?2 and i.status = ?1")
    public Optional<InvoiceStatus> findByInvoiceAndStatus(Status status, Long invoiceId);
}
