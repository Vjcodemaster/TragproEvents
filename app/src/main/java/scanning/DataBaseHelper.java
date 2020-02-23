package scanning;



public class DataBaseHelper {

    //private variables
    private int _id;
    private String _phone_number;
    private String _name;
    private String _email_id;
    private String _designation;
    private String _time;
    private String _date;
    private String _last_seen_time;
    private String _tag_id;
    private String _major;
    private String _minor;
    private String _uuid;
    private String _rssi;

    // Empty constructor
    public DataBaseHelper(){

    }
    public DataBaseHelper(String _tag_id, String _name, String _designation, String _phone_number, String _email_id, String _date,
                          String _time, String _last_seen_time){
        this._tag_id = _tag_id;
        this._name = _name;
        this._designation = _designation;
        this._phone_number = _phone_number;
        this._email_id = _email_id;
        this._date = _date;
        this._time = _time;
        this._last_seen_time = _last_seen_time;
    }

    // constructor
    public DataBaseHelper(String name, String _phone_number, String _email_id, String _designation){
        this._name = name;
        this._phone_number = _phone_number;
        this._email_id = _email_id;
        this._designation = _designation;
    }

    // constructor
    public DataBaseHelper(String _tag_id, String _major, String _minor, String _uuid, String _rssi, String _time,
                          String _date){
        this._tag_id = _tag_id;
        this._major = _major;
        this._minor = _minor;
        this._uuid = _uuid;
        this._rssi = _rssi;
        this._time = _time;
        this._date = _date;
    }

    // constructor
    public DataBaseHelper(String _last_seen_time, int _id){
        this._last_seen_time = _last_seen_time;
        this._id = _id;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting tagID
    public String getTagID(){
        return this._tag_id;
    }

    // setting tagID
    public void setTagID(String _tag_id){
        this._tag_id = _tag_id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

    // getting phone number
    public String getPhoneNumber(){
        return this._phone_number;
    }

    // setting phone number
    public void setPhoneNumber(String phone_number){
        this._phone_number = phone_number;
    }

    // getting emailID
    public String getEmailID(){
        return this._email_id;
    }

    // setting emailID
    public void setEmailID(String _email_id){
        this._email_id = _email_id;
    }

    // getting Designation
    public String getDesignation(){
        return this._designation;
    }

    // setting Designation
    public void setDesignation(String _designation){
        this._designation = _designation;
    }

    // getting time
    public String getTime(){
        return this._time;
    }

    // setting time
    public void setTime(String _time){
        this._time = _time;
    }

    // getting date
    public String getDate(){
        return this._date;
    }

    // setting date
    public void setDate(String _date){
        this._date = _date;
    }

    // getting lastseen
    public String getLastSeen(){
        return this._last_seen_time;
    }

    // setting lastseen
    public void setLastSeen(String _last_seen_time){
        this._last_seen_time = _last_seen_time;
    }

    // getting major
    public String getMajor(){
        return this._major;
    }

    // setting major
    public void setMajor(String _major){
        this._major = _major;
    }

    // getting minor
    public String getMinor(){
        return this._minor;
    }

    // setting minor
    public void setMinor(String _minor){
        this._minor = _minor;
    }

    // getting uuid
    public String getUUID(){
        return this._uuid;
    }

    // setting uuid
    public void setUUID(String _uuid){
        this._uuid = _uuid;
    }

    // getting rssi
    public String getRssi(){
        return this._rssi;
    }

    // setting rssi
    public void setRssi(String _rssi){
        this._rssi = _rssi;
    }



}
