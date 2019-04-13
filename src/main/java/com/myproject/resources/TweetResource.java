package com.myproject.resources;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.myproject.controller.TweetController;
import com.myproject.domain.Tweet;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class TweetResource extends ResourceSupport {

    private String entry;

    private String lastModifiedAt;

    private String hashtag;

    public TweetResource(Tweet tweet) {
        this.entry=tweet.getEntry();
        this.hashtag = tweet.getHashtag();
        this.lastModifiedAt = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S").format(tweet.getLastModifiedAt());
        this.add(linkTo(methodOn(TweetController.class).getCreatedBy(tweet.getId())).withRel("createdBy"));
    }


    public String getEntry() {
        return entry;
    }

    public String getHashtag() {
        return hashtag;
    }

    public String getLastModifiedAt(){
        return lastModifiedAt;

    }
}
