package com.example.demo;

import com.example.demo.security.MyUserDetails;
import com.example.demo.security.User;
import com.example.demo.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class MyController {

    @Autowired
    UserDetailsServiceImpl userservice;
    @Autowired
    PostService postservice;
    @Autowired
    RelationshipRepository relarepo;

    public Boolean freund(Integer userid, Integer user2) {
        List<Integer> friendsid = new ArrayList<>();
        List<Relationship> friends1 = relarepo.findAllByFriend1id(userid);
        for (Relationship friendship : friends1) {
            friendsid.add(friendship.getFriend2id());
        }
        List<Relationship> friends2 = relarepo.findAllByFriend2id(userid);
        for (Relationship friendship : friends2) {
            friendsid.add(friendship.getFriend1id());
        }
        for (Integer nummer : friendsid) {
            if (nummer == user2) return true;
        }
        return false;
    }
    public List<User> getfriends(MyUserDetails userdetails) {
        Integer userid = userdetails.getId();
        List<Integer> friendsid = new ArrayList<>();
        List<Relationship> friends1 = relarepo.findAllByFriend1id(userid);
        for (Relationship friendship : friends1) {
            friendsid.add(friendship.getFriend2id());
        }
        List<Relationship> friends2 = relarepo.findAllByFriend2id(userid);
        for (Relationship friendship : friends2) {
            friendsid.add(friendship.getFriend1id());
        }

        List<User> friends = new ArrayList<>();
        for (Integer friendid : friendsid) {
            User friend = userservice.getuserbyid(friendid);
            friends.add(friend);
        }

        return friends;
    }

    @GetMapping("/")
    public String homepage(@AuthenticationPrincipal MyUserDetails userdetails, Model model) {
        String name = userdetails.getUsername();
        model.addAttribute("name", name);
        List<User> friends = getfriends(userdetails);

        List<Post> postsfromuser = postservice.loadpostsbyusername(name);
        List<Post> posts = new ArrayList<>();
        posts.addAll(postsfromuser);
        for (User friend : friends) {
            List<Post> postsfromfriends = postservice.loadpostsbyusername(friend.getUsername());
            posts.addAll(postsfromfriends);
        }
        Collections.sort(posts);
        model.addAttribute("posts", posts);
        return "index";
    }
    @PostMapping("/")
    public String hompagereturn(@AuthenticationPrincipal MyUserDetails userdetails, @ModelAttribute("content") String content) {
        Post post = new Post();
        post.setContent(content);
        post.setDatetime(LocalDateTime.now());
        post.setUsername(userdetails.getUsername());
        postservice.save(post);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginsite() {
        return "login";
    }

    @GetMapping("/register")
    public String registersite(Model model) {
        User newuser = new User();
        model.addAttribute("newuser", newuser);
        return "register";
    }
    @PostMapping("/register")
    public String registerreturn(@ModelAttribute("newuser") User newuser) {
        userservice.register(newuser);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String membersite(Model model, @AuthenticationPrincipal MyUserDetails userdetails) {
        List<User> members = userservice.listallusers();
        List<User> found = new ArrayList<>();
        for (User member : members) {
            if (userdetails.getId() == member.getId()) {found.add(member); continue;}
            if (freund(userdetails.getId(), member.getId())) { member.setFreund(true);}
        }
        members.removeAll(found);
        model.addAttribute("members", members);
        return "members";
    }

    @GetMapping("/addfriend/{userid}")
    public String addfriend(@PathVariable(name = "userid") int userid, @AuthenticationPrincipal MyUserDetails userdetails) {
        Integer thisid = userdetails.getId();
        Relationship relationship = new Relationship();
        relationship.setFriend1id(thisid);
        relationship.setFriend2id(userid);
        relarepo.save(relationship);
        return "redirect:/";
    }

    @GetMapping("/friends")
    public String friends(Model model, @AuthenticationPrincipal MyUserDetails userdetails) {
        List<User> friends = getfriends(userdetails);
        model.addAttribute("friends", friends);
        return "friends";
    }

    @GetMapping("/{username}")
    public String profile(@PathVariable(name="username") String username, Model model) {
        MyUserDetails user = (MyUserDetails) userservice.loadUserByUsername(username);
        List<Post> posts = postservice.loadpostsbyusername(username);
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        return "profile";
    }

    @GetMapping("/myprofile")
    public String myprofile(@AuthenticationPrincipal MyUserDetails userdetails, Model model) {
        MyUserDetails user = userdetails;
        List<Post> posts = postservice.loadpostsbyusername(userdetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        return "myprofile";
    }

    @GetMapping("/unfriend/{friendid}")
    public String unfriend(@PathVariable(name="friendid") Integer friendid, @AuthenticationPrincipal MyUserDetails userdetails) {
        relarepo.deleteRelationship(friendid, userdetails.getId());
        return "redirect:/friends";
    }

    @GetMapping("/deletepost/{postid}")
    public String deletepost(@PathVariable(name="postid") Integer postid) {
        postservice.deletepostbyid(postid);
        return "redirect:/myprofile";
    }
}
