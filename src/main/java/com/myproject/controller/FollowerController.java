package com.myproject.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.myproject.domain.Employee;
import com.myproject.domain.Follower;
import com.myproject.domain.Tweet;
import com.myproject.repository.FollowerRepository;
import com.myproject.resources.EmployeeResource;
import com.myproject.resources.FollowerResource;
import com.myproject.resources.TweetResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/followers")
@Transactional
public class FollowerController {

    @Autowired
    private FollowerRepository followerRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<FollowerResource>> getFollowers(Pageable pageable) {
        Page<Follower> followers = followerRepository.findAll(pageable);
        List<FollowerResource> followerResources = followers.getContent().stream().map(f -> new FollowerResource(f)).collect(Collectors.toList());
        return new ResponseEntity<>(followerResources, HttpStatus.OK);
    }

    @RequestMapping(value = "/{followerId}", method = RequestMethod.GET)
    public ResponseEntity<FollowerResource> getFollower(@PathVariable Long followerId) {
        Preconditions.checkNotNull(followerId,"followerId can not be null");
        Follower follower = followerRepository.findOne(followerId);
        return new ResponseEntity<>(new FollowerResource(follower), HttpStatus.OK);
    }

    @RequestMapping(value = "/{followerId}/following", method = RequestMethod.GET)
    public ResponseEntity<List<EmployeeResource>> getFollowing(@PathVariable Long followerId) {
        Preconditions.checkNotNull(followerId,"followerId can not be null");
        List<Follower> followers = followerRepository.findByFollowerId(followerId);
        List<Employee> employees = followers.stream().map(f -> f.getEmployee()).collect(Collectors.toList());
        List<EmployeeResource> employeeResources = employees.stream().map(e -> new EmployeeResource(e)).collect(Collectors.toList());
        return new ResponseEntity<>(employeeResources, HttpStatus.OK);
    }

    @RequestMapping(value = "/{followerId}/tweets", method = RequestMethod.GET)
    public ResponseEntity<List<TweetResource>> getTweets(@PathVariable Long followerId) {
        Preconditions.checkNotNull(followerId,"followerId can not be null");
        List<Follower> followers  = followerRepository.findByFollowerId(followerId);
        List<Tweet> tweets = followers.stream().map(f -> f.getEmployee().getTweetList()).flatMap(List::stream).collect(Collectors.toList());
        List<TweetResource> tweetResources = tweets.stream().map(t -> new TweetResource(t)).collect(Collectors.toList());
        return new ResponseEntity<>(tweetResources, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{followerId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteFollower(@PathVariable Long followerId) {
        Preconditions.checkNotNull(followerId,"followerId can not be null");
        List<Follower> followers = followerRepository.findByFollowerId(followerId);
        followers.stream().forEach(f -> followerRepository.delete(f));
        return new ResponseEntity(HttpStatus.OK);
    }

}
