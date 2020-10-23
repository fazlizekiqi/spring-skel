package com.cepheid.cloud.skel.model.builders;

import com.cepheid.cloud.skel.model.Reader;
import com.cepheid.cloud.skel.model.Review;

public class ReviewBuilder {

    private String mDescription;
    private String mReader;


    public  ReviewBuilder description(String description){
        mDescription=description;
        return this;
    }

    public  ReviewBuilder reviewer(String reader){
        mReader=reader;
        return this;
    }

    public Review build (){
        return new Review(mDescription,mReader);
    }
}
