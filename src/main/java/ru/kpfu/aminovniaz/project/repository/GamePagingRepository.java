package ru.kpfu.aminovniaz.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.aminovniaz.project.model.Game;

import java.util.List;

@Repository
public interface GamePagingRepository extends PagingAndSortingRepository<Game, Long> {

}
