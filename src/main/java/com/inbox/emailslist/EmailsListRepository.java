package com.inbox.emailslist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;

import com.inbox.email.Email;

public interface EmailsListRepository extends CassandraRepository<EmailsList, EmailsListPrimaryKey>  {
    List<EmailsList> findAllById_UserIdAndId_Label(String userId, String label);


}
