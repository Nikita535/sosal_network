package sosal_network.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import sosal_network.entity.ChatMessage;
import sosal_network.entity.User;
import sosal_network.repository.BanRepository;
import sosal_network.repository.MessageRepository;
import sosal_network.repository.UserRepository;
import sosal_network.service.ChatMessageService;
import sosal_network.service.FriendService;
import sosal_network.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    BanRepository banRepository;
    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;


    @GetMapping("/messages")
    public String getChat(Model model, @AuthenticationPrincipal User authenticatedUser) {

        if (banRepository.findBanInfoById(authenticatedUser.getBanInfo().getId()).isBanStatus()) {
            return "banError";
        }


        model.addAttribute("user", userService.findUserByUsername(authenticatedUser.getUsername()));
        model.addAttribute("friends", friendService.getAcceptedFriends(authenticatedUser.getUsername()));
        model.addAttribute("userService", userService);
        return "messages";
    }

    @GetMapping("/chat/{username}")
    public String getChat1(Model model, @PathVariable String username, @AuthenticationPrincipal User authenticatedUser) {

        if (banRepository.findBanInfoById(authenticatedUser.getBanInfo().getId()).isBanStatus()) {
            return "banError";
        }

        model.addAttribute("friends", friendService.getAcceptedFriends(authenticatedUser.getUsername()));
        model.addAttribute("userService", userService);

        User friend = userRepository.findByUsername(username);
        model.addAttribute("user", userService.findUserByUsername(authenticatedUser.getUsername()));
        model.addAttribute("userTo", friend);
        model.addAttribute("allMessages", chatMessageService.showAllMessages(authenticatedUser, friend));
        return "messages";
    }


    @MessageMapping("/chat.send/{id1}/{id2}")
    @SendTo("/topic/{id1}/{id2}")
    public ChatMessage sendMessage(@Payload final ChatMessage chatMessage) {
        messageRepository.save(chatMessage);
        return chatMessage;
    }

    @GetMapping("/reloadMessageFriends/{page}")
    @ResponseBody
    public List<Object> showFriendsMessages(@PathVariable int page, @AuthenticationPrincipal User authenticatedUser) {
        List<Object> allInfo = new ArrayList<>();
        List<User> chatFriends = chatMessageService.getChatFriends(authenticatedUser, page);
        List<ChatMessage> lastMessages = chatFriends.stream().map(friend -> chatMessageService.showLastMessage(friend, authenticatedUser)).toList();
        allInfo.add(chatFriends);
        allInfo.add(lastMessages);
        return allInfo;
    }

}
