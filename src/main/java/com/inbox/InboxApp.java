package com.inbox;

import java.nio.file.Path;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.inbox.email.Email;
import com.inbox.email.EmailRepository;
import com.inbox.emailslist.EmailsList;
import com.inbox.emailslist.EmailsListPrimaryKey;
import com.inbox.emailslist.EmailsListRepository;
import com.inbox.folders.Folder;
import com.inbox.folders.FolderRepository;
import com.inbox.folders.UnreadEmailStatsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.inbox.email.EmailService;
//import com.inbox.emailslist.EmailsList;
//import com.inbox.emailslist.EmailsListPrimaryKey;
//import com.inbox.emailslist.EmailsListRepository;

@SpringBootApplication
@RestController
public class InboxApp {

	@Autowired
	private FolderRepository folderRepository;
	
	@Autowired
	private EmailsListRepository emailsListRepository;
	
	@Autowired
	private EmailRepository emailRepository;
	
	@Autowired
	private UnreadEmailStatsRepository unreadEmailStatsRepository;

	public static void main(String[] args) {
		
		SpringApplication.run(InboxApp.class, args);
		System.out.println("Running");
	}


	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	@PostConstruct
	public void initializeData() {
//		folderRepository.save(new Folder("VishalMalhotra23","Inbox","blue"));
//		folderRepository.save(new Folder("VishalMalhotra23","Sent","green"));
//		folderRepository.save(new Folder("VishalMalhotra23","hehe","red"));

		
		for(int i=0;i<10;i++) {
			EmailsListPrimaryKey key= new EmailsListPrimaryKey();
			key.setUserId("VishalMalhotra23");
			key.setLabel("Inbox");
			key.setTimeId(Uuids.timeBased());
			
			EmailsList item= new EmailsList();
			item.setId(key);
			item.setTo(Arrays.asList("VishalMalhotra23","Rahul"));
			item.setSubject("Subject "+i);
			item.setRead(false);
			item.setFrom("WickedSanta");
			
			emailsListRepository.save(item);
			
			Email email= new Email();
			email.setId(key.getTimeId());
			email.setTo(item.getTo());
			email.setFrom(item.getFrom());
			email.setSubject(item.getSubject());
			email.setBody("Body "+i);
			
			unreadEmailStatsRepository.incrementUnreadCounter("VishalMalhotra23", "Inbox");
			emailRepository.save(email);
		}
		
		Email email= new Email();
		email.setId(Uuids.timeBased());
		email.setTo(Arrays.asList("abc","def"));
		email.setFrom("abc");
		email.setSubject("yo");
		email.setBody("Body ");

		emailRepository.save(email);

	}

}
