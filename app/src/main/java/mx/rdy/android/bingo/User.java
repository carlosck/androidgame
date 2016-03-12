package mx.rdy.android.bingo;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Seca on 3/11/16.
 */
public class User implements Serializable{
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private int credit;


    public User(JSONObject jsobject)

    {
        try
        {
            JSONObject user= jsobject.getJSONObject("user");
            this.id = user.getInt("id");
            this.firstName = user.getString("first_name");
            this.lastName = user.getString("last_name");
            this.email = user.getString("email");
            this.username = user.getString("username");
            this.credit = jsobject.getInt("credit");
        }
        catch(Exception e)
        {

        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}
