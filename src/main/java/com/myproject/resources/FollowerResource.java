package com.myproject.resources;

import com.myproject.controller.FollowerController;
import com.myproject.domain.Employee;
import com.myproject.domain.Follower;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class FollowerResource extends ResourceSupport {

    public static final String TWEETS = "tweets";

    private String firstName;

    private final String lastName;

    private final String  userName;

    private final String following;

    public FollowerResource(Follower follower) {
        Employee followerEmployeeObject = follower.getFollower();
        Employee followingEmployeeObject = follower.getEmployee();
        this.firstName = followerEmployeeObject.getFirstName();
        this.lastName= followerEmployeeObject.getLastName();
        this.userName = followerEmployeeObject.getUsername();
        this.following = followingEmployeeObject.getUsername();
        this.add(linkTo(methodOn(FollowerController.class).getFollower(follower.getId())).withSelfRel());
        this.add(linkTo(methodOn(FollowerController.class).getTweets(follower.getId())).withRel(TWEETS));
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getFollowing() {
        return following;
    }
}
