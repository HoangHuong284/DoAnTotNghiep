package com.web.repository;

import com.web.entity.Category;
import com.web.entity.Invoice;
import com.web.enums.PayType;
import com.web.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {


    @Query("select i from Invoice i where i.user.id = ?1")
    List<Invoice> findByUser(Long idUser);

    @Query("select i from Invoice i where i.createdDate >= ?1 and i.createdDate <= ?2")
    Page<Invoice> findByDate(Date from, Date to, Pageable pageable);


    @Query("select i from Invoice i where i.createdDate >= ?1 and i.createdDate <= ?2 and i.status = ?3")
    public Page<Invoice> findByDateAndStatus(Date from, Date to, Status status, Pageable pageable);

    @Query("select i from Invoice i where i.createdDate >= ?1 and i.createdDate <= ?2 and i.payType = ?3")
    public Page<Invoice> findByDateAndPaytype(Date from, Date to, PayType payType, Pageable pageable);

    @Query("select i from Invoice i where i.createdDate >= ?1 and i.createdDate <= ?2 and i.payType = ?3 and i.status = ?4")
    public Page<Invoice> findByDateAndPaytypeAndStatus(Date from, Date to, PayType payType,Status statusId, Pageable pageable);


    @Query(value = "select sum(i.total_amount) from invoice i where Month(i.created_date) = ?1 and Year(i.created_date) = ?2 and (i.pay_type = 0 or i.status = 3)", nativeQuery = true)
    public Double calDt(Integer thang, Integer month, Status status, PayType payType);

    @Query(value = "select sum(i.total_amount) from invoice i \n" +
            "WHERE (i.status = 3 or i.pay_type = 0 ) and i.created_date = ?1", nativeQuery = true)
    public Double revenueByDate(Date ngay, Status status, PayType payType);

    @Query(value = "select count(i.id) from invoice i\n" +
            "where i.status = 3 and i.created_date = ?1",nativeQuery = true)
    public Double numInvoiceToDay(Date ngay, Status status);
}
