package com.myproject.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.myproject.domain.Employee;
import com.myproject.domain.Follower;
import com.myproject.domain.Tweet;
import com.myproject.repository.EmployeeRepository;
import com.myproject.repository.FollowerRepository;
import com.myproject.repository.TweetRepository;
import com.myproject.resources.EmployeeResource;
import com.myproject.resources.FollowerResource;
import com.myproject.resources.TweetResource;
import com.myproject.service.EmployeeService;
import com.myproject.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/employees")
@Transactional
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private FollowerRepository followerRepository;

    @Autowired
    private TweetService tweetService;

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<EmployeeResource>> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeResource> employeeResources = employees.stream().map(e -> new EmployeeResource(e)).collect(Collectors.toList());
        return new ResponseEntity<>(employeeResources, HttpStatus.OK);
    }

    @RequestMapping(value = "/{employeeId}", method = RequestMethod.GET)
    public ResponseEntity<EmployeeResource> getEmployee(@PathVariable Long employeeId) {
        Employee employee = employeeRepository.findOne(employeeId);
        return new ResponseEntity<>(new EmployeeResource(employee), HttpStatus.OK);
    }

    @RequestMapping(value = "/{employeeId}/tweets", method = RequestMethod.GET)
    public ResponseEntity<List<TweetResource>> getTweets(@PathVariable Long employeeId) {
        Employee employee = employeeRepository.findOne(employeeId);
        List<Tweet> tweetList = employee.getTweetList();
        List<TweetResource> tweets = tweetList.stream().map(tweet -> new TweetResource(tweet)).collect(Collectors.toList());
        return new ResponseEntity<>(tweets, HttpStatus.OK);
    }

    @RequestMapping(value = "/{employeeId}/followers", method = RequestMethod.GET)
    public ResponseEntity<Set<FollowerResource>> getFollowers(@PathVariable Long employeeId) {
        Employee employee = employeeRepository.findOne(employeeId);
        Set<Follower> followers = employee.getFollowers();
        Set<FollowerResource> followerResourceSet = followers.stream().map(f -> new FollowerResource(f)).collect(Collectors.toSet());
        return new ResponseEntity<>(followerResourceSet, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> createEmployee(@RequestBody Employee newEmployee) {
        Employee employee = new Employee(newEmployee.getFirstName(), newEmployee.getLastName(), newEmployee.getUsername());
        employeeRepository.save(employee);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{employeeId}/tweet", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> addTweet(@PathVariable Long employeeId, @RequestBody Tweet newTweet) {
        Tweet tweet = new Tweet(newTweet.getEntry(), newTweet.getHashtag());
        Employee employee = employeeRepository.findOne(employeeId);
        tweet.setEmployee(employee);
        employee.addTweet(tweet);
        tweet.setLastModifiedAt(Timestamp.from(Instant.now()));

        tweetRepository.save(tweet);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{employeeId}/tweet/{tweetId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteTweet(@PathVariable Long employeeId, @PathVariable Long tweetId) {
        Tweet tweet = tweetRepository.findOne(tweetId);
        Preconditions.checkNotNull(tweet, "The tweet cannot be null");
        if (employeeService.hasAccess(tweet.getEmployee())) {
            tweetRepository.delete(tweet);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value = "/{employeeId}/follow", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity addFollowers(@PathVariable Long employeeId, @RequestBody Long followerId) {
        Optional<Follower> following = followerRepository.findOneByEmployeeIdAndFollowerId(employeeId, followerId);
        if (!following.isPresent()) {
            Employee employee = employeeRepository.findOne(employeeId);
            Employee follower = employeeRepository.findOne(followerId);
            Follower fl = new Follower(employee,follower);
            followerRepository.save(fl);
            return new ResponseEntity(HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "{employeeId}/unfollow", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteFollower(@PathVariable Long employeeId, @RequestBody Long followerId) {
        Employee employee = employeeRepository.findOne(employeeId);
        if (employeeService.hasAccess(employee)) {
            Optional<Follower> follower = followerRepository.findOneByEmployeeIdAndFollowerId(employeeId, followerId);
            if (follower.isPresent()) {
                followerRepository.delete(follower.get());
                return new ResponseEntity(HttpStatus.OK);
            }
            else {
                return new ResponseEntity("Nothing to Delete", HttpStatus.NO_CONTENT);
            }
        }
        else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{employeeId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteEmployee(@PathVariable Long employeeId) {
        Employee employee = employeeRepository.findOne(employeeId);
        List<Follower> followers = followerRepository.findByEmployeeId(employeeId);
        followers.forEach(f -> followerRepository.delete(f));
        employeeRepository.delete(employee);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
