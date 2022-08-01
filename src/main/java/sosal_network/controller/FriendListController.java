package sosal_network.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sosal_network.entity.ProfileInfo;
import sosal_network.entity.User;
import sosal_network.service.FriendService;
import sosal_network.service.UserService;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class FriendListController {

    @Autowired
    private FriendService friendService;
    @Autowired
    private UserService userService;

    @GetMapping("/{username}/friendList/{page}")
    public String getFriendList(Model model, @PathVariable Optional<String> username,
                                @PathVariable Optional<String> page,
                                @ModelAttribute("searchLine") String searchLine){
        User userFriend = userService.findUserByUsername(username.get());

        int sizeOfPage = 10;


        Object[] informationProfilesOfFriends = friendService.findFriendProfilesByUsername(username.get(),
                searchLine, page.get(), sizeOfPage);
        List<ProfileInfo> profilesOfFriends = (List<ProfileInfo>)informationProfilesOfFriends[0];
        Integer sizeOfFriends = (Integer) informationProfilesOfFriends[1];


        Object[] informationProfilesOfStrangers = friendService.findStrangersProfilesByUsername(username.get(),
                searchLine, page.get(), new LinkedList<>((List<ProfileInfo>)informationProfilesOfFriends[2]), sizeOfPage - profilesOfFriends.size(), sizeOfPage);
        List<ProfileInfo> profilesOfStrangers = (List<ProfileInfo>)informationProfilesOfStrangers[0];
        Integer sizeOfStrangers = (Integer) informationProfilesOfStrangers[1];


        if (profilesOfFriends.size() == 0 && sizeOfFriends == 0 && !Objects.equals(searchLine, ""))
            model.addAttribute("errorNoSuchFriends", true);

        if (profilesOfStrangers.size() == 0 && sizeOfStrangers == 0 && !Objects.equals(searchLine, ""))
            model.addAttribute("errorNoSuchStrangers", true);

        if (profilesOfFriends.size() != 0)
            model.addAttribute("errorNoShowFriends", true);

        if (profilesOfStrangers.size() != 0)
            model.addAttribute("errorNoShowStrangers", true);



        model.addAttribute("isAdminOfTheFriendList", Objects.equals(userService.findUserByUsername(username.get()).getUsername(), userService.getUserAuth().getUsername()));
        model.addAttribute("pages", Math.ceil(((float)sizeOfFriends + (float)sizeOfStrangers) / sizeOfPage));
        model.addAttribute("searchLine", searchLine);
        model.addAttribute("profilesOfStrangers", profilesOfStrangers);
        model.addAttribute("friendProfiles", profilesOfFriends);
        model.addAttribute("friendService", friendService);

        model.addAttribute("user", userService.findUserByUsername(username.get()));
        model.addAttribute("profileInfo", userService.findByUser_Username(username.get()));
        model.addAttribute("friends",friendService.getAcceptedFriends(username.get()));
        model.addAttribute("isFriend",friendService.isFriends(username.get()));
        model.addAttribute("friendAccepted",friendService.checkFriendStatus(username.get()));
        model.addAttribute("isInviteRecieved",friendService.isInviteRecieved(username.get()));
        model.addAttribute("isInviteSend",friendService.isInviteSend(username.get()));
        return "friendList";
    }

    @PostMapping("/{username}/search")
    public String findFriendList(Model model, @PathVariable Optional<String> username,
                                 @RequestParam("searchLine") String searchLine,
                                 RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("searchLine", searchLine);
        return "redirect:/" + username.get() + "/friendList/1";
    }
}
