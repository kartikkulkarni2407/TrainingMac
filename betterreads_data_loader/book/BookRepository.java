package io.javabrains.betterreads_data_loader.book;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository  extends CassandraRepository<Book, String>{
    
}
