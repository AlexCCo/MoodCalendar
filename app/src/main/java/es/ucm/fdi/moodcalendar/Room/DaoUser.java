package es.ucm.fdi.moodcalendar.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import es.ucm.fdi.moodcalendar.Room.Entities.User;

@Dao
interface DaoUser {

    @Insert(entity = User.class)
    int insertnewUser(User u);

    @Query("SELECT * from User where username LIKE :username ")
    LiveData<User> getUserbyId(String username);

    @Update(entity = User.class)
    int updateUser(User u);

    @Delete(entity = User.class)
    int deleteUser(int uid);


}
