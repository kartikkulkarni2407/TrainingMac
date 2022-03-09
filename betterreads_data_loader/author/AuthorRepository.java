package io.javabrains.betterreads_data_loader.author;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository  extends CassandraRepository<Author, String>{
    
}
