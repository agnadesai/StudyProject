package com.myproject.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.myproject.domain.Employee;
import com.myproject.domain.Follower;
import com.myproject.domain.Tweet;
import com.myproject.repository.TweetRepository;
import com.myproject.resources.EmployeeResource;
import com.myproject.resources.FollowerResource;
import com.myproject.resources.TweetResource;
import com.myproject.service.EmployeeService;
import com.myproject.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/tweets")
@Transactional
public class TweetController {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping(value = "/mostRecent", method = RequestMethod.GET)
    public ResponseEntity<List<TweetResource>> getAllTweets(Pageable pageable) {
        Page<Tweet> tweets = tweetRepository.findByOrderByLastModifiedAtDesc(pageable);
        List<TweetResource> tweetResourceList = tweets.getContent().stream().map(t -> new TweetResource(t)).collect(Collectors.toList());
        return new ResponseEntity<>(tweetResourceList, HttpStatus.OK);
    }

    @RequestMapping(value = "/findByHashTag/{hashTag}", method = RequestMethod.GET)
    public ResponseEntity<List<TweetResource>> findTweetsByHashTag(@PathVariable String hashTag) {
        List<Tweet> tweets = tweetRepository.findByHashtag(hashTag);
        List<TweetResource> tweetResourceList = tweets.stream().map(t -> new TweetResource(t)).collect(Collectors.toList());
        return new ResponseEntity<>(tweetResourceList, HttpStatus.OK);
    }

    @RequestMapping(value = "/findByEmployee/{employeeId}", method = RequestMethod.GET)
    public ResponseEntity<List<TweetResource>> findTweetsByHashTag(@PathVariable Long employeeId) {
        List<Tweet> tweets = tweetRepository.findByEmployeeId(employeeId);
        List<TweetResource> tweetResourceList = tweets.stream().map(t -> new TweetResource(t)).collect(Collectors.toList());
        return new ResponseEntity<>(tweetResourceList, HttpStatus.OK);
    }

    @RequestMapping(value = "/{tweetId}", method = RequestMethod.GET)
    public ResponseEntity<List<TweetResource>> getTweet(@PathVariable Long tweetId) {
        Preconditions.checkNotNull(tweetId, "Tweet Id can not be null");
        Tweet tweet = tweetRepository.findOne(tweetId);
        return new ResponseEntity(new TweetResource(tweet), HttpStatus.OK);
    }

    @RequestMapping(value = "/{tweetId}", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity<Void> updateTweet(@PathVariable Long tweetId, @RequestBody String modifiedTweet) {
        Preconditions.checkNotNull(tweetId, "Tweet Id can not be null");
        Tweet tweet = tweetRepository.findOne(tweetId);
        tweet.setEntry(modifiedTweet);
        tweet.setLastModifiedAt(Timestamp.from(Instant.now()));
        tweetRepository.save(tweet);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{tweetId}/createdBy", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<EmployeeResource> getCreatedBy(@PathVariable Long tweetId) {
        Preconditions.checkNotNull(tweetId, "Tweet Id can not be null");
        Tweet tweet = tweetRepository.findOne(tweetId);
        Employee employee = tweet.getEmployee();
        EmployeeResource employeeResource = new EmployeeResource(employee);
        return new ResponseEntity<>(employeeResource, HttpStatus.OK);
    }

    @RequestMapping(value = "/{tweetId}/followers", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<FollowerResource>> getFollowers(@PathVariable Long tweetId) {
        Preconditions.checkNotNull(tweetId, "Tweet Id can not be null");
        Tweet tweet = tweetRepository.findOne(tweetId);
        Preconditions.checkNotNull(tweet, "Tweet can not be null");
        Set<Follower> followers = tweet.getEmployee().getFollowers();
        List<FollowerResource> followerResources = followers.stream().map(f -> new FollowerResource(f)).collect(Collectors.toList());
        return new ResponseEntity<>(followerResources, HttpStatus.OK);
    }

    @RequestMapping(value = "/{tweetId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteTweet(@PathVariable Long tweetId) {
        Tweet tweet = tweetRepository.findOne(tweetId);
        if (employeeService.hasAccess(tweet.getEmployee())) {
            tweetRepository.delete(tweet);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
