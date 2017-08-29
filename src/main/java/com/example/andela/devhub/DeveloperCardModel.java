package com.example.andela.devhub;

/**
 * Created by Ismail on 7/16/2017.
 */

public class DeveloperCardModel {

    private String avaterResourceId; //developer profile pic resource id //changed int

    private String username; //developer github username
    private String profileLink; //developer github profile link



    /**
     * @param avaterResourceId represents developer github profile pic.
     *
     * @param username represents developer's github username
     */
    public DeveloperCardModel(String avaterResourceId, String username)
    {
        this.avaterResourceId = avaterResourceId;
        this.username = username;
    }

    /**
     * @param avaterResourceId represents developer github profile pic.
     *
     * @param username represents developer's github username
     *
     * @param profileLink represents developer's github profile page link
     */
    public DeveloperCardModel(String avaterResourceId, String username, String profileLink)
    {
        this(avaterResourceId, username);
        this.profileLink = profileLink;
    }


    /**
     * provides developer's github profile pic resource id
     */
    public String getAvaterResourceId() {
        return avaterResourceId;
    }

    /**
     * provides developer's github username
     */
    public String getUsername() {
        return username;
    }

    /**
     * provides developer's github profile page link
     */
    public String getProfileLink() {
        return profileLink;
    }


}
