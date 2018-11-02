package jason.app.symphony.order.comp.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jason.app.symphony.order.comp.entity.SalesOrder;

@Repository
public interface SalesOrderRepository extends CrudRepository<SalesOrder, Long> {

	List<SalesOrder> findAllByPartyId(String customerId);
}
