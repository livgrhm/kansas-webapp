/*
 * The MIT License
 *
 * Copyright 2016 oliviagraham.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.livgrhm.kansas.db;

import com.livgrhm.kansas.core.User;
import com.livgrhm.kansas.core.UserMapper;
import java.util.Date;
import java.util.List;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface UserDAO {

    /**
     * Set User Authentication Hash (user is logged in)
     * @param userId    user that is logged in
     * @param authHash  authentication hash
     * @param now       authentication timestamp, will last 24hrs
     * @param lastIP    IP address of login
     */
    @SqlUpdate("update user set userAuthHash=:authHash, userAuthTimestamp=:now, "
            + "userLastIP=:lastIP where userId=:userId")
    void setUserAuthHash(@Bind("userId") int userId, @Bind("authHash") String authHash, 
            @Bind("now") Date now, @Bind("lastIP") String lastIP);
    
    /**
     * Create a User (registration)
     * @param firstName         user first name
     * @param lastName          user last name
     * @param email             user email address
     * @param userStatus        user status (N == password to be reset, A == active)
     * @param userPasswordHash  user password, hashed
     * @param userAuthHash      user authorisation hash
     * @param userAuthTimestamp user authorisation timestamp
     * @return                  integer userId of new user
     */
    @SqlUpdate("insert into user (firstName, lastName, email, userStatus, userPasswordHash, "
            + "userAuthHash, userAuthTimestamp, userFailedLogons, userLastIP) "
            + "values (:firstName, :lastName, :email, :userStatus, :userPasswordHash, "
            + ":userAuthHash, :userAuthTimestamp, 0, null)")
    @GetGeneratedKeys
    int createUser(@Bind("firstName") String firstName, @Bind("lastName") String lastName,
            @Bind("email") String email, @Bind("userStatus") String userStatus, 
            @Bind("userPasswordHash") String userPasswordHash, @Bind("userAuthHash") String userAuthHash,
            @Bind("userAuthTimestamp") Date userAuthTimestamp);
   
    /**
     * Update a User
     * @param userId        ID of user to update
     * @param firstName     user first name
     * @param lastName      user last name
     * @param email         user email
     */
    @SqlUpdate("update user set firstName=:firstName, lastName=:lastName, "
            + "email=:email, datetimeUpdated=now() where userId=:userId")
    void updateUser(@Bind("userId") int userId, @Bind("firstName") String firstName, 
            @Bind("lastName") String lastName, @Bind("email") String email);
    
    /**
     * Delete a User
     * @param userId    ID of user to delete
     */
    @SqlUpdate("update user set isActive=0, isDeleted=1, datetimeUpdated=now() "
            + "where userId=:userId")
    void deleteUser(@Bind("userId") int userId);
    
    /**
     * Update failed logon count for user
     * @param userId    ID of user to update logon count for
     */
    @SqlUpdate("update user set userFailedLogons=userFailedLogons+1 where userId=:userId")
    void updateFailedLogonCount(@Bind("userId") int userId);
    
    /**
     * Lock user account
     * @param userId ID of user
     */
    @SqlUpdate("update user set userStatus='L' where userId=:userId")
    void updateUserLockAccount(@Bind("userId") int userId);
    
    /**
     * Get User by Current Hash
     * @param hash  Password hash to search for
     * @return      User if found
     */
    @SqlQuery("select userId from user where userAuthHash=:hash and isActive=1 and isDeleted=0")
    User getUserByCurrentHash(@Bind("hash") String hash);
    
    /**
     * Get User by ID
     * @param userId    ID of user to get
     * @return          User object
     */
    @SqlQuery("select * from user where userId=:userId and isActive=1 and isDeleted=0")
    @Mapper(UserMapper.class)
    User getUserById(@Bind("userId") int userId);
    
    /**
     * Get User by Email Address
     * @param email Email address of user to get
     * @return      User object
     */
    @SqlQuery("select * from user where email=:email and isActive=1 and isDeleted=0")
    @Mapper(UserMapper.class)
    User getUserByEmail(@Bind("email") String email);
    
    /**
     * Get List of All Users
     * @return  List of User objects
     */
    @SqlQuery("select * from user where isActive=1 and isDeleted=0")
    @Mapper(UserMapper.class)
    List<User> getUsers();
}