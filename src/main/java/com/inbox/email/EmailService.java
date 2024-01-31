package com.inbox.email;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.inbox.emailslist.EmailsList;
import com.inbox.emailslist.EmailsListPrimaryKey;
import com.inbox.emailslist.EmailsListRepository;
import com.inbox.folders.UnreadEmailStatsRepository;

@Service
public class EmailService {

    @Autowired
    private EmailsListRepository emailsListRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void sendEmail(String fromUserId, String toUserIds, String subject, String body) {

        UUID timeUuid = Uuids.timeBased();

        List<String> toUserIdList =Arrays.asList(toUserIds.split(","	))
        		.stream()
        		.map(id -> StringUtils.trimWhitespace(id))
        		//.filter(id-> StringUtils.hasText(id))
        		.distinct()
               .collect(Collectors.toList());
        // Add to sent items of sender
        EmailsList sentItemEntry = prepareEmailsListEntry("Sent", fromUserId, fromUserId, toUserIdList, subject,
                timeUuid);
        sentItemEntry.setRead(true);
        emailsListRepository.save(sentItemEntry);
        // Add to inbox of each reciever
        toUserIdList.stream().forEach(toUserId -> {
            EmailsList inboxEntry = prepareEmailsListEntry("Inbox", toUserId, fromUserId, toUserIdList, subject,
                    timeUuid);
            inboxEntry.setRead(false);
            emailsListRepository.save(inboxEntry);
            unreadEmailStatsRepository.incrementUnreadCounter(toUserId, "Inbox");
        });

        // Save email entity
        Email email = new Email();
        email.setId(timeUuid);
        email.setFrom(fromUserId);
        email.setTo(toUserIdList);
        email.setSubject(subject);
        email.setBody(body);
        emailRepository.save(email);

    }

    private EmailsList prepareEmailsListEntry(String folderName, String forUser, String fromUserId,
            List<String> toUserIds, String subject, UUID timeUuid) {

        EmailsListPrimaryKey key = new EmailsListPrimaryKey();
        key.setLabel(folderName);
        key.setUserId(forUser);
        key.setTimeId(timeUuid);
        EmailsList emailsListEntry = new EmailsList();
        emailsListEntry.setId(key);
        emailsListEntry.setFrom(fromUserId);
        emailsListEntry.setTo(toUserIds);
        emailsListEntry.setSubject(subject);
        return emailsListEntry;
    }

    public boolean doesHaveAccess(Email  email,String loginId) {
    	 return (loginId.equals(email.getFrom()) || email.getTo().contains(loginId));
    }
    
    public String getReplySubject(String subject) {
    	return "Re: "+ subject;
    }
    
    public String getReplyBody(Email email) {
    	return "\n\n\n--------------------------------------- \n" +
    	"From: " + email.getFrom()+ "\n"+
    	"To: " + email.getTo() + "\n\n" +
    	email.getBody();
    }

	public void moveEmailToImportant(UUID uuid, String loginId) {
	 
		EmailsListPrimaryKey key = new EmailsListPrimaryKey();
	    key.setLabel("Inbox");
	    key.setUserId(loginId);
	    key.setTimeId(uuid);
		Optional<EmailsList> optionalEmail = emailsListRepository.findById(key);
		if(optionalEmail.isPresent()) {
			EmailsList currentMail=optionalEmail.get();
			currentMail.getId().setLabel("Important");
			emailsListRepository.save(currentMail);
			Optional<EmailsList> optionalEmail2 = emailsListRepository.findById(key);
			emailsListRepository.delete(optionalEmail2.get());
			
		}
	}
}
