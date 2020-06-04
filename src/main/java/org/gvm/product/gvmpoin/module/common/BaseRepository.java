package org.gvm.product.gvmpoin.module.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

  /**
   * Get Pagination .
   *
   * @return Pagination Model
   */
  default Pagination getPagination(int dataNumber, int totalPages) {
    int current = dataNumber + 1;
    int previous = current > 1 ? current - 1 : current;
    int next = current < totalPages ? current + 1 : current;
    int startLoop = totalPages > 10 ? Math.max(1, current - 5) : 1;
    int lastLoop = totalPages > 10 ? Math.min(startLoop + 9, totalPages) : totalPages;

    Pagination pagination = new Pagination();
    pagination.setCurrent(current);
    pagination.setPrevious(previous);
    pagination.setLast(totalPages);
    pagination.setNext(next);
    pagination.setStartLoop(startLoop);
    pagination.setLastLoop(lastLoop);

    return pagination;
  }
}
