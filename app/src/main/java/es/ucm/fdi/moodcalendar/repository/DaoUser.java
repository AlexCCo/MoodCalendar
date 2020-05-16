package es.ucm.fdi.moodcalendar.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import es.ucm.fdi.moodcalendar.dataModel.entities.User;

@Dao
interface DaoUser {

    @Insert(entity = User.class, onConflict = OnConflictStrategy.ABORT)
    public void insertNewUser(User u);

    @Query("SELECT * from User where username LIKE :username ")
    public LiveData<User> getUserbyId(String username);

    @Update(entity = User.class)
    public void updateUser(User u);

    @Delete(entity = User.class)
    public void deleteUser(User u);


}
