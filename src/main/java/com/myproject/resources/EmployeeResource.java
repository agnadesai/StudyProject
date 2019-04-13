package com.myproject.resources;

import java.util.ArrayList;
import java.util.List;

import com.myproject.controller.EmployeeController;
import com.myproject.domain.Employee;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class EmployeeResource extends ResourceSupport {

    public static final String FOLLOW = "follow";

    public static final String TWEET = "tweet";

    public static final String TWEETS = "viewTweets";

    String name;

    List<String> tweets = new ArrayList<>();

    public EmployeeResource(Employee employee) {
        this.name = employee.getFirstName();
        employee.getTweetList().forEach(t-> this.addTweet(t.getEntry()));
        this.add(linkTo(methodOn(EmployeeController.class).getEmployee(employee.getId())).withSelfRel());
        this.add(linkTo(methodOn(EmployeeController.class).addFollowers(employee.getId(),null)).withRel(FOLLOW));
        this.add(linkTo(methodOn(EmployeeController.class).addTweet(employee.getId(),null)).withRel(TWEET));
        this.add(linkTo(methodOn(EmployeeController.class).getTweets(employee.getId())).withRel(TWEETS));
    }

    public String getName() {
        return name;
    }

    public List<String> getTweets() {
        return tweets;
    }

    private void addTweet(String tweet) {
        tweets.add(tweet);
    }
}
