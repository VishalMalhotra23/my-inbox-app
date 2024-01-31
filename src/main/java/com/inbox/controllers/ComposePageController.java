package com.inbox.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.inbox.email.Email;
import com.inbox.email.EmailRepository;
import com.inbox.email.EmailService;
import com.inbox.folders.Folder;
import com.inbox.folders.FolderRepository;
import com.inbox.folders.FoldersService;

@Controller
public class ComposePageController {

    @Autowired 
    private FolderRepository folderRepository;
    @Autowired 
    private EmailRepository emailRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private FoldersService foldersService;

    
    @GetMapping(value = "/compose")
    public String getComposePage(@RequestParam(required = false) String to,@RequestParam(required = false) String id ,@RequestParam(required = false) String replayToEmailId, @AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null && principal.getAttribute("login") != null) {
            String loginId = principal.getAttribute("login");
            List<Folder> folders = folderRepository.findAllById(loginId);
            List<Folder> initFolders = foldersService.init(loginId);
                // initFolders.stream().forEach(folderRepository::save);
            model.addAttribute("defaultFolders", initFolders);
            model.addAttribute("userName",principal.getAttribute("login"));
            if (folders.size() > 0) {
                model.addAttribute("userFolders", folders);
            }
            Map<String, Integer> folderToUnreadCounts = foldersService.getUnreadCountsMap(loginId);
            model.addAttribute("folderToUnreadCounts", folderToUnreadCounts);
            
            UUID uuid = UUID.fromString(id);
            Optional<Email> optionalEmail = emailRepository.findById(uuid);
            if (optionalEmail.isPresent()) {
                Email email = optionalEmail.get();
                
                if(emailService.doesHaveAccess(email, loginId)) {
                	model.addAttribute("subject",emailService.getReplySubject(email.getSubject()));
                	model.addAttribute("body",emailService.getReplyBody(email));
                }

            }
        
            return "compose-page";
        }
        return "index";
    }
    
    @GetMapping("/move")
    public String moveEmailToImportant(@RequestParam String id, @AuthenticationPrincipal OAuth2User principal) {
    	if (principal != null && principal.getAttribute("login") != null) {
    		 UUID uuid = UUID.fromString(id);
             String loginId = principal.getAttribute("login");
             emailService.moveEmailToImportant(uuid,loginId);
             return "redirect:/"; 
    	}
        
        return "index";
     }

    @PostMapping(value = "/sendEmail")
    public ModelAndView sendEmail(
        @RequestBody MultiValueMap<String, String> formData, 
        @AuthenticationPrincipal OAuth2User principal
    ) {
        if (principal == null || principal.getAttribute("login") == null) {
            return null;
        }
        String toUserIds = formData.getFirst("toUserIds");
        String subject = formData.getFirst("subject");
        String body = formData.getFirst("body");
        String from = principal.getAttribute("login");

        emailService.sendEmail(from, toUserIds, subject, body);

        return new ModelAndView("redirect:/");
    }

}
