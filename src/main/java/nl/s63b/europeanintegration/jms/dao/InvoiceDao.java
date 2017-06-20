package nl.s63b.europeanintegration.jms.dao;

import com.S63B.domain.Entities.Invoice;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Kevin.
 */
public interface InvoiceDao extends CrudRepository<Invoice, Integer> {
}
